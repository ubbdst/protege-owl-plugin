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

import junit.framework.Assert;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

/**
 * Illustrates the use of XML Schema datatypes, values and RDFS Literals.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFSDatatypeExamples {

    public static void main(String[] args) throws OntologyLoadException {

        OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
        OWLNamedClass cls = owlModel.createOWLNamedClass("Class");
        OWLIndividual individual = cls.createOWLIndividual("Individual");

        // The four default datatypes are rendered into primitive Java types
        OWLDatatypeProperty stringProperty = owlModel.createOWLDatatypeProperty("stringProperty", owlModel.getXSDstring());
        individual.setPropertyValue(stringProperty, "MyString");
        String stringValue = (String) individual.getPropertyValue(stringProperty);

        OWLDatatypeProperty booleanProperty = owlModel.createOWLDatatypeProperty("booleanProperty", owlModel.getXSDboolean());
        individual.setPropertyValue(booleanProperty, Boolean.TRUE);
        Boolean booleanValue = (Boolean) individual.getPropertyValue(booleanProperty);

        OWLDatatypeProperty floatProperty = owlModel.createOWLDatatypeProperty("floatProperty", owlModel.getXSDfloat());
        individual.setPropertyValue(floatProperty, new Float(4.2));
        Float floatValue = (Float) individual.getPropertyValue(floatProperty);

        OWLDatatypeProperty intProperty = owlModel.createOWLDatatypeProperty("intProperty", owlModel.getXSDint());
        individual.setPropertyValue(floatProperty, new Integer(42));
        Integer intValue = (Integer) individual.getPropertyValue(intProperty);

        // If you prefer to get the value as RDFSLiteral instead of primitive objects
        RDFSLiteral intLiteral = individual.getPropertyValueLiteral(intProperty);
        Assert.assertEquals(intLiteral.getInt(), intValue.intValue());
        Assert.assertTrue(intLiteral.getDatatype().equals(owlModel.getXSDint()));

        // Values of non-default datatypes must be wrapped into RDFSLiterals
        RDFSDatatype xsdDate = owlModel.getRDFSDatatypeByName("xsd:date");
        OWLDatatypeProperty dateProperty = owlModel.createOWLDatatypeProperty("dateProperty", xsdDate);
        RDFSLiteral dateLiteral = owlModel.createRDFSLiteral("1971-07-06", xsdDate);
        individual.setPropertyValue(dateProperty, dateLiteral);
        RDFSLiteral myDate = (RDFSLiteral) individual.getPropertyValue(dateProperty);
        System.out.println("Date: " + myDate);

        // Strings with language tags must be wrapped into RDFSLiterals
        RDFSLiteral langLiteral = owlModel.createRDFSLiteral("Wert", "de");
        individual.setPropertyValue(stringProperty, langLiteral);
        RDFSLiteral result = (RDFSLiteral) individual.getPropertyValue(stringProperty);
        Assert.assertTrue(result.getLanguage().equals("de"));
        Assert.assertTrue(result.getString().equals("Wert"));
    }
}
