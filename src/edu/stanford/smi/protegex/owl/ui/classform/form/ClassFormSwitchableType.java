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

package edu.stanford.smi.protegex.owl.ui.classform.form;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.util.ClosureAxiomFactory;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchableType;
import edu.stanford.smi.protegex.owl.ui.widget.ClassFormWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassFormSwitchableType implements SwitchableType {

    public String getButtonText() {
        return "Form View";
    }


    /**
     * Gets the list of all classes that need to checked for the sufficiently
     * expressive test.  This includes all superclasses and operands of
     * equivalent intersections.
     *
     * @param namedClass the named class
     * @return the "parent" classes
     */
    private List getParentClasses(RDFSNamedClass namedClass) {
        List classes = new ArrayList();
        Iterator it = namedClass.getSuperclasses(true).iterator();
        while (it.hasNext()) {
            RDFSClass rdfsClass = (RDFSClass) it.next();
            if (rdfsClass instanceof OWLIntersectionClass) {
                classes.addAll(((OWLIntersectionClass) rdfsClass).getOperands());
            }
            else {
                classes.add(rdfsClass);
            }
        }
        return classes;
    }


    public Class getWidgetClassType() {
        return ClassFormWidget.class;
    }


    /**
     * Checks if a given OWLAllValuesFrom restriction is a closure axiom for
     * any existential restriction from a given list.
     *
     * @param namedClass    the named class hosting the axiom/classes
     * @param classes       the parent classes of namedClass
     * @param allValuesFrom the potential closure axiom
     * @return true if allValuesFrom is a closure axiom
     */
    private boolean isClosureAxiom(OWLNamedClass namedClass, List classes, OWLAllValuesFrom allValuesFrom) {
        boolean existentialExists = false;
        boolean closed = false;
        Iterator es = classes.iterator();
        while (es.hasNext()) {
            RDFSClass e = (RDFSClass) es.next();
            if (e instanceof OWLExistentialRestriction) {
                existentialExists = true;
                OWLExistentialRestriction ex = (OWLExistentialRestriction) e;
                OWLAllValuesFrom closure = ClosureAxiomFactory.getClosureAxiom(namedClass, ex);
                if (allValuesFrom.equals(closure)) {
                    closed = true;
                    break;
                }
            }
        }
        return existentialExists && closed;
    }


    public boolean isSufficientlyExpressive(RDFSNamedClass namedClass) {
        if (namedClass instanceof OWLNamedClass) {
            OWLNamedClass oc = (OWLNamedClass) namedClass;
            if (oc.getEquivalentClasses().size() > 1) {
                return false;
            }
            List classes = getParentClasses(namedClass);
            Iterator cs = classes.iterator();
            while (cs.hasNext()) {
                RDFSClass cls = (RDFSClass) cs.next();
                if (cls instanceof OWLLogicalClass || cls instanceof OWLCardinalityBase) {
                    return false;
                }
                if (cls instanceof OWLAllValuesFrom) {
                    if (!isClosureAxiom(oc, classes, (OWLAllValuesFrom) cls)) {
                        return false;
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }


    public boolean isSuitable(OWLModel owlModel) {
        return true;
    }
}
