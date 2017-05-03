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

package edu.stanford.smi.protegex.owlx.examples;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ListenerExample {

    public static void main(String[] args) throws OntologyLoadException  {

        OWLModel owlModel = ProtegeOWL.createJenaOWLModel();

        OWLNamedClass cls = owlModel.createOWLNamedClass("Class");
        cls.addClassListener(new ClassAdapter() {
            public void instanceAdded(RDFSClass cls, RDFResource instance) {
                System.out.println("Instance was added to class: " + instance.getName());
            }
        });

        for (int i = 0; i < 5; i++) {
            String newName = "Individual" + (int) (Math.random() * 10000);
            cls.createOWLIndividual(newName);
        }


        owlModel.addModelListener(new ModelAdapter() {
            public void propertyCreated(RDFProperty property) {
                System.out.println("Property created: " + property.getName());
            }
        });

        owlModel.createRDFProperty("RDF-Property");
        owlModel.createOWLObjectProperty("Object-Property");
        owlModel.createOWLDatatypeProperty("Datatype-Property");
    }
}
