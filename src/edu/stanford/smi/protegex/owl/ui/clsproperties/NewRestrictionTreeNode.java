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

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.*;
import edu.stanford.smi.protegex.owl.ui.restrictions.RestrictionKindRenderer;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class NewRestrictionTreeNode extends RestrictionTreeNode {

    private Cls metaCls;

    private PropertyRestrictionsTree tree;


    public NewRestrictionTreeNode(Cls metaCls, PropertyRestrictionsTree tree) {
        this.metaCls = metaCls;
        this.tree = tree;
    }


    public void checkExpression(String text) throws Throwable {
        RDFProperty property = getParentNode().getRDFProperty();
        String metaClsName = metaCls.getName();
        if (metaClsName.equals(OWLNames.Cls.HAS_VALUE_RESTRICTION)) {
            DefaultOWLHasValue.checkFillerText(text, property);
        }
        else if (metaClsName.equals(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION) ||
                metaClsName.equals(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION)) {
            AbstractOWLQuantifierRestriction.checkFillerText(text, property);
        }
        else {
            AbstractOWLCardinalityBase.checkFillerText(text, property);
        }
    }


    public String getFillerText() {
        //String str = "                                 ";
        //return str + str + str + str + str;
        return "";
    }


    public Icon getIcon() {
        return RestrictionKindRenderer.getClsIcon(metaCls);
    }


    public char getOperator() {
        String name = metaCls.getName();
        if (OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION.equals(name)) {
            return DefaultOWLAllValuesFrom.OPERATOR;
        }
        else if (OWLNames.Cls.HAS_VALUE_RESTRICTION.equals(name)) {
            return DefaultOWLHasValue.OPERATOR;
        }
        else if (OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION.equals(name)) {
            return DefaultOWLSomeValuesFrom.OPERATOR;
        }
        else if (OWLNames.Cls.CARDINALITY_RESTRICTION.equals(name)) {
            return DefaultOWLCardinality.OPERATOR;
        }
        else if (OWLNames.Cls.MAX_CARDINALITY_RESTRICTION.equals(name)) {
            return DefaultOWLMaxCardinality.OPERATOR;
        }
        else if (OWLNames.Cls.MIN_CARDINALITY_RESTRICTION.equals(name)) {
            return DefaultOWLMinCardinality.OPERATOR;
        }
        else {
            return '?';
        }
    }


    public Cls getRestrictionMetaCls() {
        return metaCls;
    }


    public RDFSClass getInheritedFromClass() {
        return null;
    }


    public boolean isInherited() {
        return false;
    }


    public void setUserObject(Object userObject) {
        KnowledgeBase kb = metaCls.getKnowledgeBase();
        OWLModel owlModel = (OWLModel) kb;
        try {
            OWLNamedClass cls = getParentNode().getRestrictedClass();
            owlModel.beginTransaction("Add restriction on property " +
                    getParentNode().getRDFProperty().getBrowserText() +
                    " at class " + cls.getBrowserText(), cls.getName());
            Cls anonRootCls = ((KnowledgeBase) owlModel).getCls(OWLNames.Cls.ANONYMOUS_ROOT);
            OWLRestriction restriction = (OWLRestriction) kb.createCls(null,
                    Collections.singleton(anonRootCls), metaCls);
            restriction.setOnProperty(getParentNode().getRDFProperty());
            String text = (String) userObject;
            restriction.setFillerText(text);
            RDFSClass definition = cls.getDefinition();
            if (definition != null) {
                if (definition instanceof OWLIntersectionClass) {
                    ((OWLIntersectionClass) definition).addOperand(restriction);
                    owlModel.commitTransaction();
                    tree.refill(); // Needed because no event is issued
                }
                else {
                    OWLIntersectionClass intersectionCls = owlModel.createOWLIntersectionClass();
                    intersectionCls.addOperand(definition.createClone());
                    intersectionCls.addOperand(restriction);
                    cls.setDefinition(intersectionCls);
                    owlModel.commitTransaction();
                }
            }
            else {
                cls.addSuperclass(restriction);
                owlModel.commitTransaction();
            }
            tree.setSelectedRestriction(restriction);
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);            
        }
    }
}
