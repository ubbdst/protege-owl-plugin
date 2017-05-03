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

package edu.stanford.smi.protegex.owl.jena;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * An interface that can be implemented by all OWLModel implementations
 * which provide a Jena OntModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OntModelProvider {

    final static int OWL_LITE = 0;

    final static int OWL_DL = 1;

    final static int OWL_FULL = 2;


    /**
     * Gets an OntModel which represents the current state.
     *
     * @return an OntModel
     */
    OntModel getOntModel();


    /**
     * Gets or prepares an OntModel which is guaranteed to be in OWL DL, for
     * classification and other reasoning tasks.
     *
     * @return null  if the OntModel cannot be reduced to OWL DL
     */
    OntModel getOWLDLOntModel();


    /**
     * Gets the OWL Species of the current main OntModel.
     *
     * @return one of the three OWL_xxx constants
     */
    int getOWLSpecies();


    /**
     * Gets an OntModel that is connected to a (DIG) reasoner.
     *
     * @param classifierURL the URL of the classifier (usually defined in the preferences)
     * @return An OntModel that contains the classification result
     */
    OntModel getReasonerOntModel(String classifierURL);
}
