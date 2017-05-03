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
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LogicalClassDefinitions {

    public static void main(String[] args) throws OntologyLoadException {

        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();

        OWLNamedClass personClass = owlModel.createOWLNamedClass("Person");
        OWLNamedClass manClass = owlModel.createOWLNamedSubclass("Man", personClass);
        OWLNamedClass womanClass = owlModel.createOWLNamedSubclass("Woman", personClass);

        // Create expression  (PersonClass & !(Man | Woman))
        OWLUnionClass unionClass = owlModel.createOWLUnionClass();
        unionClass.addOperand(manClass);
        unionClass.addOperand(womanClass);
        OWLComplementClass complementClass = owlModel.createOWLComplementClass(unionClass);
        OWLIntersectionClass intersectionClass = owlModel.createOWLIntersectionClass();
        intersectionClass.addOperand(personClass);
        intersectionClass.addOperand(complementClass);

        OWLNamedClass kidClass = owlModel.createOWLNamedClass("Kid");
        kidClass.addSuperclass(intersectionClass);

       // String expression = "Person & !(Man | Woman)";
        String expression = "Person and not (Man or Woman)";
        OWLIntersectionClass ic = (OWLIntersectionClass) owlModel.createRDFSClassFromExpression(expression);
        System.out.println("Browser text: " + ic.getBrowserText());

        String parsable = intersectionClass.getParsableExpression();
        System.out.println("Expression: " + parsable);

        //RDFSClass c = owlModel.createRDFSClassFromExpression("!(" + parsable + ")");
        RDFSClass c = owlModel.createRDFSClassFromExpression("not(" + parsable + ")");
        System.out.println("New expression: " + c.getParsableExpression());
    }
}
