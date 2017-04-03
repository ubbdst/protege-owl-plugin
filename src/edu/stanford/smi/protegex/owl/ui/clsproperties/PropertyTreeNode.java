/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2007.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.owl.ui.clsproperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLCardinalityBase;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.util.ExpressionInfo;

public class PropertyTreeNode extends DefaultMutableTreeNode
        implements Comparable, Disposable {

    private OWLNamedClass cls;

    private boolean inherited;

    private PropertyRestrictionsTree tree;

    private PropertyValueListener valueListener = new PropertyValueAdapter() {
        public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues) {
            tree.refillLater();
        }
    };


    public PropertyTreeNode(PropertyRestrictionsTree tree, 
                            OWLNamedClass cls, 
                            RDFProperty property, 
                            boolean inherited) {
        setUserObject(property);
        this.inherited = inherited;
        this.cls = cls;
        this.tree = tree;
        property.addPropertyValueListener(valueListener);
    }

    public PropertyTreeNode(PropertyRestrictionsTree tree, 
                            OWLNamedClass cls, 
                            RDFProperty property, 
                            boolean inherited,
                            List<ExpressionInfo<OWLRestriction>> restrictions) {
      setUserObject(property);
      this.inherited = inherited;
      this.cls = cls;
      this.tree = tree;
      addChildNodes(restrictions);
      property.addPropertyValueListener(valueListener);
    }
    
    private void addChildNodes() {
        List<ExpressionInfo<OWLRestriction>> restrictions = getRestrictions();
        addChildNodes(restrictions);
    }
    
    private void addChildNodes(List<ExpressionInfo<OWLRestriction>> restrictions) {
        for (ExpressionInfo<OWLRestriction> ri : restrictions) {
            OWLRestriction restriction = ri.getExpression();
            RDFSClass inheritedFromClass = ri.getDirectNamedClass();
            if (cls.equals(inheritedFromClass)) {
                inheritedFromClass = null;
            }
            RestrictionTreeNode node = new OldRestrictionTreeNode(tree, restriction, inheritedFromClass);
            add(node);
        }
    }


    public int compareTo(Object o) {
        PropertyTreeNode other = (PropertyTreeNode) o;
        if (!isInherited()) {
            if (other.isInherited()) {
                return -1;
            }
        }
        else {
            if (!other.isInherited()) {
                return 1;
            }
            else {
                if (getChildCount() > 0) {
                    if (other.getChildCount() == 0) {
                        return -1;
                    }
                }
                else {
                    if (other.getChildCount() > 0) {
                        return 1;
                    }
                }
            }
        }
        return getRDFProperty().compareTo(other.getRDFProperty());
    }


    public void dispose() {
        for (int i = 0; i < getChildCount(); i++) {
            getRestrictionTreeNode(i).dispose();
        }
        getRDFProperty().removePropertyValueListener(valueListener);
    }


    /**
     * A helper method to refill the child nodes.  This returns a List with relevant OWLRestrictions
     * that are defined in the class or its superclasses, including operands from
     * OWLIntersectionClasses.  The List is ordered by their distance from the top class, using
     * a breadth-first algorithm, so that the closest restriction appears at 0.
     *
     * @return a List of OWLRestriction instances
     */
    private List<ExpressionInfo<OWLRestriction>> getRestrictions() {
        List<ExpressionInfo<OWLRestriction>> rs = new ArrayList<ExpressionInfo<OWLRestriction>>();
        List<OWLNamedClass> queue = new ArrayList<OWLNamedClass>();
        queue.add(cls);
        Set<OWLNamedClass> reachedClses = new HashSet<OWLNamedClass>();
        Set<RDFSClass> overloadedClses = new HashSet<RDFSClass>();
        boolean first = true;
        while (!queue.isEmpty()) {

            OWLNamedClass c = (OWLNamedClass) queue.get(0);
            reachedClses.add(c);
            queue.remove(0);

            for (Iterator it = c.getSuperclasses(false).iterator(); it.hasNext();) {
                RDFSClass superClass = (RDFSClass) it.next();
                if (superClass instanceof OWLRestriction) {
                    ExpressionInfo<OWLRestriction> ri = new ExpressionInfo<OWLRestriction>((OWLRestriction) superClass, 
                                                             (OWLAnonymousClass) superClass, c);
                    perhapsAddRestriction(ri, rs, overloadedClses);
                }
                else if (superClass instanceof OWLIntersectionClass) {
                    OWLIntersectionClass intersectionClass = (OWLIntersectionClass) superClass;
                    for (Iterator ot = intersectionClass.getOperands().iterator(); ot.hasNext();) {
                        RDFSClass operand = (RDFSClass) ot.next();
                        if (operand instanceof OWLRestriction) {
                            ExpressionInfo<OWLRestriction> ri = new ExpressionInfo<OWLRestriction>((OWLRestriction) operand, intersectionClass, c);
                            perhapsAddRestriction(ri, rs, overloadedClses);
                        }
                    }
                }
                else if (superClass instanceof OWLNamedClass && !reachedClses.contains(superClass)) {
                    queue.add((OWLNamedClass) superClass);
                }
            }
            if (first) {
                Collections.sort(rs, new Comparator<ExpressionInfo<OWLRestriction>>() {
                    public int compare(ExpressionInfo<OWLRestriction> o1, ExpressionInfo<OWLRestriction> o2) {
                        RDFSClass directType1 = o1.getExpression().getProtegeType();
                        RDFSClass directType2 = o2.getExpression().getProtegeType();
                        return directType1.getName().compareTo(directType2.getName());
                    }
                });
                first = false;
            }
        }
        removeOverloadedAllRestrictions(rs);
        return rs;
    }


    public Icon getIcon() {
        RDFProperty property = getRDFProperty();
        if (isInherited()) {
            return property.getInheritedIcon();
        }
        else {
            return ProtegeUI.getIcon(property);
        }
    }


    public RDFProperty getRDFProperty() {
        return (RDFProperty) getUserObject();
    }


    public OWLNamedClass getRestrictedClass() {
        return cls;
    }


    public RestrictionTreeNode getRestrictionTreeNode(int index) {
        return (RestrictionTreeNode) getChildAt(index);
    }


    public OldRestrictionTreeNode getRestrictionTreeNode(OWLRestriction restriction) {
        for (int i = 0; i < getChildCount(); i++) {
            OldRestrictionTreeNode node = (OldRestrictionTreeNode) getChildAt(i);
            if (node.getRestriction().equals(restriction)) {
                return node;
            }
        }
        return null;
    }


    public OldRestrictionTreeNode getRestrictionTreeNode(String restrictionText) {
        for (int i = 0; i < getChildCount(); i++) {
            Object child = getChildAt(i);
            if (child instanceof OldRestrictionTreeNode) {
                OldRestrictionTreeNode node = (OldRestrictionTreeNode) child;
                if (node.getRestriction().getBrowserText().equals(restrictionText)) {
                    return node;
                }
            }
        }
        return null;
    }


    public boolean isInherited() {
        return inherited;
    }


    public boolean hasRestrictionOfType(Cls metaCls) {
        for (int i = 0; i < getChildCount(); i++) {
            if (metaCls.equals(getRestrictionTreeNode(i).getRestrictionMetaCls())) {
                return true;
            }
        }
        return false;
    }


    private void perhapsAddRestriction(ExpressionInfo<OWLRestriction> ri, List<ExpressionInfo<OWLRestriction>> rs, Set overloadedClses) {
        OWLRestriction restriction = ri.getExpression();
        // Avoid duplicates
        String browserText = ri.getExpression().getBrowserText();
        for (ExpressionInfo<OWLRestriction> alreadyIncludedRi : rs) {
            OWLRestriction alreadyIncludedRestriction = alreadyIncludedRi.getExpression();
            if (browserText.equals(alreadyIncludedRestriction.getBrowserText())) {
                return;
            }
        }

        // Avoid generalizing OWLSomeValuesFrom restrictions
        if (restriction instanceof OWLSomeValuesFrom) {
            OWLSomeValuesFrom someValuesFrom = (OWLSomeValuesFrom) restriction;
            if (someValuesFrom.getSomeValuesFrom() instanceof RDFSNamedClass) {
                RDFSNamedClass namedClass = (RDFSNamedClass) someValuesFrom.getSomeValuesFrom();
                for (ExpressionInfo<OWLRestriction> alreadyIncludedRi : rs) {
                    OWLRestriction alreadyIncludedRestriction = alreadyIncludedRi.getExpression();
                    if (alreadyIncludedRestriction instanceof OWLSomeValuesFrom) {
                        OWLSomeValuesFrom other = (OWLSomeValuesFrom) alreadyIncludedRestriction;
                        if (other.getSomeValuesFrom() instanceof RDFSNamedClass) {
                            RDFSNamedClass subclass = (RDFSNamedClass) other.getSomeValuesFrom();
                            if (subclass.getSuperclasses(true).contains(namedClass)) {
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (getRDFProperty().equals(restriction.getOnProperty())) {
            boolean isQCR = false;
            if (restriction instanceof OWLCardinalityBase) {
                OWLCardinalityBase base = (OWLCardinalityBase) restriction;
                isQCR = base.isQualified();
            }
            if (isQCR) {
                rs.add(ri);
            }
            else if (!overloadedClses.contains(restriction.getProtegeType())) {
                if (restriction instanceof OWLCardinalityBase) {
                    overloadedClses.add(restriction.getProtegeType());
                }
                rs.add(ri);
            }
        }
    }


    private void removeOverloadedAllRestrictions(List<ExpressionInfo<OWLRestriction>> rs) {
        boolean changed = false;
        do {
            for (int i = 0; i < rs.size(); i++) {
                ExpressionInfo<OWLRestriction> ri = rs.get(i);
                OWLRestriction restriction = ri.getExpression();
                if (restriction instanceof OWLAllValuesFrom &&
                        ((OWLAllValuesFrom) restriction).getFiller() instanceof RDFSClass) {
                    removeOverloadedAllRestrictions(rs, i);
                }
            }
        }
        while (changed);
    }


    private boolean removeOverloadedAllRestrictions(List<ExpressionInfo<OWLRestriction>> rs, int index) {
        boolean changed = false;
        OWLAllValuesFrom allRestriction = (OWLAllValuesFrom) rs.get(index).getExpression();
        RDFSClass superFiller = (RDFSClass) allRestriction.getFiller();
        RDFSClass superCls = allRestriction.getOwner();
        for (int i = index + 1; i < rs.size(); i++) {
            ExpressionInfo<OWLRestriction> ri = rs.get(i);
            final OWLRestriction restriction = ri.getExpression();
            if (restriction instanceof OWLAllValuesFrom &&
                    restriction.getOnProperty().equals(allRestriction.getOnProperty())) {
                OWLAllValuesFrom a = (OWLAllValuesFrom) restriction;
                RDFSClass subFiller = (RDFSClass) a.getFiller();
                RDFSClass andir = a.getOwner();
                if (a.getFiller() instanceof RDFSClass &&
                        superFiller.isSubclassOf(subFiller) &&
                        !andir.equals(superCls)) {
                    rs.remove(i);
                    changed = true;
                }
            }
        }
        return changed;
    }
    
}
