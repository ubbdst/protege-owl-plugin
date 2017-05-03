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

package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;

import java.util.*;
import java.util.logging.Level;

/**
 * A utility for creating and detecting closure axioms.
 * <p/>
 * Closure axioms can be applied to existential restrictions that are direct
 * pure superclass or part of an equivalent intersection of an OWLNamedClass.
 * <p/>
 * For example:
 * hasParent some Mother
 * hasParent some Father
 * Can be closed by adding:
 * hasParent all (Mother or Father)
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClosureAxiomFactory {

    public static OWLAllValuesFrom addClosureAxiom(OWLNamedClass namedClass, OWLExistentialRestriction restriction) {

        RDFProperty property = (OWLObjectProperty) restriction.getOnProperty();
        OWLModel owlModel = restriction.getOWLModel();
        OWLAnonymousClass root = restriction.getExpressionRoot();
        OWLNamedClass owner = (OWLNamedClass) root.getOwner();
        List existentials = getFillerStrings(owner, property);
        RDFSClass filler = getFiller(existentials, owlModel);
        OWLAllValuesFrom allValuesFrom = owlModel.createOWLAllValuesFrom(property, filler);

        if (root instanceof OWLIntersectionClass) {
            if (owner.equals(namedClass)) {
                OWLIntersectionClass cl = (OWLIntersectionClass) root.createClone();
                cl.addOperand(allValuesFrom);
                root.delete();
                namedClass.addEquivalentClass(cl);
            } else {
                namedClass.addSuperclass(allValuesFrom);
            }
            return allValuesFrom;
        } else {
            if (owner.hasEquivalentClass(restriction) && owner.equals(namedClass)) {
                OWLIntersectionClass intersectionCls = owlModel.createOWLIntersectionClass();
                intersectionCls.addOperand(restriction.createClone());
                intersectionCls.addOperand(allValuesFrom);
                restriction.delete();
                namedClass.addEquivalentClass(intersectionCls);
            } else {
                namedClass.addSuperclass(allValuesFrom);
            }
            return allValuesFrom;
        }
    }


    /**
     * Checks whether a closure axiom exists for a given class/property pair.
     * This is always true after <CODE>addClosureAxiom()</CODE> has been called.
     *
     * @param namedClass  the named class
     * @param restriction the restriction being (possibly) closed
     * @return an existing OWLAllValuesFrom or null if no matching restriction exists
     */
    public static OWLAllValuesFrom getClosureAxiom(OWLNamedClass namedClass, OWLExistentialRestriction restriction) {
        Iterator candidates = getClosureAxiomCandidates(namedClass, restriction);
        if (candidates.hasNext()) {
            return (OWLAllValuesFrom) candidates.next();  // TODO: Wrong!
        }
        return null;
    }

    /**
     * Use to find a potential restriction as a base for adding closure on a particular property
     * Will only return universals that have a named class or
     * union of named classes as filler
     *
     * @param namedClass the base class to be searched
     * @param prop       will also search for universals on the superproperties of that given
     * @return the first suitable restriction for the given property or null
     */
    public static OWLAllValuesFrom getClosureAxiom(OWLNamedClass namedClass,
                                                   RDFProperty prop) {
        Iterator<OWLAllValuesFrom> candidates = getUniversals(namedClass);
        while (candidates.hasNext()) {
            OWLAllValuesFrom current = candidates.next();
            if (current.getOnProperty().equals(prop) ||
                    prop.isSubpropertyOf(current.getOnProperty(), true)) {
                RDFResource filler = current.getFiller();
                if (filler instanceof RDFSNamedClass) {
                    return current;
                } else if (filler instanceof OWLUnionClass) {
                    boolean allNamed = true;
                    Iterator ops = ((OWLUnionClass) filler).getOperands().iterator();
                    while (ops.hasNext() && allNamed) {
                        if (!(ops.next() instanceof RDFSNamedClass)) {
                            allNamed = false;
                        }
                    }
                    if (allNamed) {
                        return current;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Use to find a closure axiom for a given property (that already contains the
     * given filler)
     *
     * @param namedClass the base class to be searched
     * @param prop       will also search for universals on the superproperties of that given
     * @param filler     will search the fillers to find one that contains this resource
     * @return the first universal restriction that matches both property
     */
    public static OWLAllValuesFrom getClosureAxiom(OWLNamedClass namedClass,
                                                   RDFProperty prop,
                                                   RDFResource filler) {
        Iterator<OWLAllValuesFrom> candidates = getUniversals(namedClass);
        while (candidates.hasNext()) {
            OWLAllValuesFrom current = candidates.next();
            RDFProperty onProp = current.getOnProperty();
            if (onProp.equals(prop) || prop.isSubpropertyOf(onProp, true)) {
                RDFResource currentfiller = current.getFiller();
                if (currentfiller instanceof OWLUnionClass &&
                        ((OWLUnionClass) currentfiller).getOperands().contains(filler)) {
                    return current;
                } else if (currentfiller.equals(filler)) {
                    return current;
                }
            }
        }
        return null;
    }

    private static Iterator<OWLAllValuesFrom> getUniversals(OWLNamedClass namedClass) {
        List candidates = new ArrayList();
        Iterator it = namedClass.getSuperclasses(true).iterator();
        while (it.hasNext()) {
            RDFSClass superclass = (RDFSClass) it.next();
            if (superclass instanceof OWLAllValuesFrom) {
                OWLAllValuesFrom allValuesFrom = (OWLAllValuesFrom) superclass;
                if (!allValuesFrom.getEquivalentClasses().contains(namedClass)) {
                    candidates.add(allValuesFrom);
                }
            }
        }
        return candidates.iterator();
    }


    private static Iterator getClosureAxiomCandidates(OWLNamedClass namedClass, OWLExistentialRestriction restriction) {
        List candidates = new ArrayList();
        OWLAnonymousClass root = restriction.getExpressionRoot();
        if (root instanceof OWLIntersectionClass) {
            Iterator it = ((OWLIntersectionClass) root).listOperands();
            while (it.hasNext()) {
                RDFSClass operand = (RDFSClass) it.next();
                if (operand instanceof OWLAllValuesFrom) {
                    candidates.add(operand);
                }
            }
        } else {
            Iterator it = namedClass.getSuperclasses(true).iterator();
            while (it.hasNext()) {
                RDFSClass superclass = (RDFSClass) it.next();
                if (superclass instanceof OWLAllValuesFrom) {
                    OWLAllValuesFrom allValuesFrom = (OWLAllValuesFrom) superclass;
                    if (!allValuesFrom.getEquivalentClasses().contains(namedClass)) {
                        candidates.add(allValuesFrom);
                    }
                }
            }
        }
        return candidates.iterator();
    }


    private static RDFSClass getFiller(List existentials, OWLModel owlModel) {
        OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
        RDFSClass filler = null;
        if (existentials.size() == 1) {
            try {
                String expression = (String) existentials.iterator().next();
                filler = parser.parseClass(owlModel, expression);
            }
            catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
        } else {
            OWLUnionClass unionCls = owlModel.createOWLUnionClass();
            for (Iterator it = existentials.iterator(); it.hasNext();) {
                String expression = (String) it.next();
                try {
                    RDFSClass fillerCls = parser.parseClass(owlModel, expression);
                    unionCls.addOperand(fillerCls);
                }
                catch (Exception ex) {
                    Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                }
            }
            filler = unionCls;
        }
        return filler;
    }


    private static List getFillerStrings(OWLNamedClass cls, RDFProperty property) {
        Set set = new HashSet();
        for (Iterator it = cls.getRestrictions(false).iterator(); it.hasNext();) {
            OWLRestriction restriction = (OWLRestriction) it.next();
            if (restriction instanceof OWLExistentialRestriction) {
                if (property.equals(restriction.getOnProperty())) {
                    String fillerText = restriction.getFillerText();
                    if (restriction instanceof OWLSomeValuesFrom) {
                        set.add(fillerText);
                    } else if (restriction instanceof OWLHasValue) {
                        set.add("{" + fillerText + "}");
                    }
                }
            }
        }
        java.util.List results = new ArrayList(set);
        Collections.sort(results);
        return results;
    }
}
