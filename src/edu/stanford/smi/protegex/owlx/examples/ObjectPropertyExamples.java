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

import java.util.Collection;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ObjectPropertyExamples {

    public static void main(String[] args) throws OntologyLoadException {

        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();

        OWLNamedClass personClass = owlModel.createOWLNamedClass("Person");
        OWLNamedClass animalClass = owlModel.createOWLNamedClass("Animal");
        OWLObjectProperty childrenProperty = owlModel.createOWLObjectProperty("children");
        childrenProperty.addUnionRangeClass(personClass);
        childrenProperty.addUnionRangeClass(animalClass);

        childrenProperty.setDomain(personClass);
        childrenProperty.addUnionDomainClass(animalClass);
        // Now the domain of the property is Person or Animal

        // A subproperty inherits the domain of its superproperty
        OWLObjectProperty sonsProperty = owlModel.createOWLObjectProperty("sons");
        sonsProperty.addSuperproperty(childrenProperty);
        assert (sonsProperty.getDomain(false) == null);
        assert (sonsProperty.getDomain(true) instanceof OWLUnionClass);

        // Union domains can be resolved using getUnionDomain
        Collection unionDomain = sonsProperty.getUnionDomain(true);
        assert (unionDomain.contains(personClass));
        assert (unionDomain.contains(animalClass));

        OWLObjectProperty ancestorProperty = owlModel.createOWLObjectProperty("ancestor");
        ancestorProperty.setRange(personClass);
        ancestorProperty.setTransitive(true);

        Jena.dumpRDF(owlModel.getOntModel());
    }
}
