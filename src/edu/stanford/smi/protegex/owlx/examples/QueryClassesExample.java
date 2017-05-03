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

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class QueryClassesExample {

    public static void main(String[] args) throws Exception {

        String uri = "http://www.owl-ontologies.com/travel.owl";
        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);

        // Print all classes and their instances
        Collection classes = owlModel.getUserDefinedOWLNamedClasses();
        for (Iterator it = classes.iterator(); it.hasNext();) {
            OWLNamedClass cls = (OWLNamedClass) it.next();
            Collection instances = cls.getInstances(false);
            System.out.println("Class " + cls.getBrowserText() + " (" + instances.size() + ")");
            for (Iterator jt = instances.iterator(); jt.hasNext();) {
                OWLIndividual individual = (OWLIndividual) jt.next();
                System.out.println(" - " + individual.getBrowserText());
            }
        }

        // Print all resources that have owl:Thing as their rdfs:subClassOf value
        RDFProperty subClassOfProperty = owlModel.getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
        OWLNamedClass owlThingClass = owlModel.getOWLThingClass();
        Collection results = owlModel.getRDFResourcesWithPropertyValue(subClassOfProperty, owlThingClass);
        System.out.println("Subclasses of owl:Thing:");
        for (Iterator it = results.iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            System.out.println(" - " + resource.getBrowserText());
        }
    }
}
