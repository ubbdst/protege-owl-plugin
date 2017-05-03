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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class ImportUtil {
	   
	/**
	 * This is a utility method that calculates the top level ontology from an owlModel.
	 * This method is aware of the cycles in the import hierarchy and tries to come up with the best answer.
	 * @param owlModel - the OWL model on which the top level ontology will be computed
	 * @return - the top level ontology - if no cycles are detected; 
	 * - null - if there are more than one top level ontologies
	 * - if a top level cycle is found, it returns one of the ontologies in the top level cycle
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static OWLOntology calculateTopLevelOntology(OWLModel owlModel) {
		RDFProperty owlImportProp = owlModel.getRDFProperty(OWLNames.Slot.IMPORTS);

		HashMap<OWLOntology, Collection<OWLOntology>> ontology2ImportedOntologiesMap = new HashMap<OWLOntology, Collection<OWLOntology>>();

		Collection<OWLOntology> ontologies = owlModel.getOWLOntologyClass().getInstances();

		if (ontologies == null || ontologies.size() == 0) {
			//Log.getLogger().warning("No ontology instance found.");
			return null;
		}

		Set<OWLOntology> importedOntologies = new HashSet<OWLOntology>();

		for (OWLOntology ontology : ontologies) {
			Collection<OWLOntology> impOntos = new HashSet<OWLOntology>(ontology.getPropertyValues(owlImportProp));

			importedOntologies.addAll(impOntos);
			ontology2ImportedOntologiesMap.put(ontology, impOntos);
		}

		ontologies.removeAll(importedOntologies);

		//the good case, no cycles, one top level ontology
		if (ontologies.size() == 1) {
			return ontologies.iterator().next();
		}

		//bad case: more top level ontologies
		if (ontologies.size() > 1) {
			//Log.getLogger().warning("More than one top level ontology: " + ontologies);
			return null;
		}

		//case with cycles (ontologies.size == 0)

		ontologies = new HashSet<OWLOntology>(ontology2ImportedOntologiesMap.keySet());

		boolean ontologyRemoved = true;

		while (ontologyRemoved) {
			ontologyRemoved = false;

			for (Iterator<OWLOntology> iterator = ontologies.iterator(); iterator.hasNext();) {
				OWLOntology ontology = (OWLOntology) iterator.next();

				Collection<OWLOntology> imports = ontology2ImportedOntologiesMap.get(ontology);
				if (imports == null || imports.size() == 0) {
					ontology2ImportedOntologiesMap.remove(ontology);
					iterator.remove();
					ontologyRemoved = true;

					for (OWLOntology ontologyKey : ontology2ImportedOntologiesMap.keySet()) {
						Collection<OWLOntology> keyImports = ontology2ImportedOntologiesMap.get(ontologyKey);
						keyImports.remove(ontology);
					}
				}
			}
		}

		OWLOntology topOntology = null;

		for (OWLOntology ontology : ontologies) {
			Set<OWLOntology> directOwnSlotValuesClosure = owlModel.getDirectOwnSlotValuesClosure(ontology, owlImportProp);

			if (directOwnSlotValuesClosure.size() - 1 == ontologies.size()) {
				topOntology = ontology;
				break;
			}
		}

		if (topOntology != null) {
			return topOntology;
		}

		//make a guess, but it is better than nothing - anyway, this should not be the case
		topOntology = ontologies.iterator().next();
		Log.getLogger().warning(
				"Failed to detect top level ontology. Making a guess and use as top ontology: " + topOntology);

		return topOntology;

	}	
	
	   
}
