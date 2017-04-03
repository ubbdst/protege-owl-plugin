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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.classform.form.ClassForm;

import java.awt.*;

/**
 * A SlotWidget wrapping a ClassForm so that it can be used inside of a
 * traditional Protege form.
 *
 * @author Matthew Horridge  <matthew.horridge@cs.man.ac.uk>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassFormWidget extends AbstractPropertyWidget {

    private ClassForm classForm;


    public void initialize() {
        classForm = new ClassForm();
        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, classForm);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if (cls.getKnowledgeBase() instanceof OWLModel && cls instanceof RDFSNamedClass) {
            OWLModel owlModel = (OWLModel) cls.getKnowledgeBase();
            RDFSNamedClass namedClass = (RDFSNamedClass) cls;
            return owlModel.getOWLNamedClassClass().equals(cls) ||
                    namedClass.getSuperclasses(true).contains(owlModel.getOWLNamedClassClass());
        }
        return false;
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance instanceof OWLNamedClass) {
            classForm.setNamedClass((OWLNamedClass) newInstance);
        }
        else {
            classForm.setNamedClass(null);
        }
    }
}

