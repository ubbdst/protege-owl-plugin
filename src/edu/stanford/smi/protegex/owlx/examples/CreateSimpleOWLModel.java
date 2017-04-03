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
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;

/**
 * An example program that creates an empty OWLModel, fills it
 * with some example classes, properties and individuals, and
 * finally prints it in RDF syntax to the screen.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateSimpleOWLModel {

    public static void main(String[] args) throws OntologyLoadException {

        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();

        OWLNamedClass personClass = owlModel.createOWLNamedClass("Person");

        OWLDatatypeProperty ageProperty = owlModel.createOWLDatatypeProperty("age");
        ageProperty.setRange(owlModel.getXSDint());
        ageProperty.setDomain(personClass);

        OWLObjectProperty childrenProperty = owlModel.createOWLObjectProperty("children");
        childrenProperty.setRange(personClass);
        childrenProperty.setDomain(personClass);

        RDFIndividual darwin = personClass.createRDFIndividual("Darwin");
        darwin.setPropertyValue(ageProperty, new Integer(0));

        RDFIndividual holgi = personClass.createRDFIndividual("Holger");
        holgi.setPropertyValue(childrenProperty, darwin);
        holgi.setPropertyValue(ageProperty, new Integer(33));

        Jena.dumpRDF(owlModel.getOntModel());
    }
}
