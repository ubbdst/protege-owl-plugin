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

package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.HashMap;
import java.util.HashSet;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Model;

public class OWLFramesMapping {
	private static HashMap<String, String> owlProp2FramesSlotMap = new HashMap<String, String>();


	static {
		owlProp2FramesSlotMap.put(RDFS.subClassOf.getURI(), Model.Slot.DIRECT_SUPERCLASSES);
		owlProp2FramesSlotMap.put(RDFS.subPropertyOf.getURI(), Model.Slot.DIRECT_SUPERSLOTS);
		owlProp2FramesSlotMap.put(RDFS.domain.getURI(), Model.Slot.DIRECT_DOMAIN);
		//what to do with range?

		owlProp2FramesSlotMap.put(OWL.maxCardinality.getURI(), Model.Slot.MAXIMUM_CARDINALITY);
		owlProp2FramesSlotMap.put(OWL.minCardinality.getURI(), Model.Slot.MINIMUM_CARDINALITY);
		//what to do with owl:Cardinality?
	}


	private static HashMap<String, String> owlProp2FramesInvSlotMap = new HashMap<String, String>();

	static {
		owlProp2FramesInvSlotMap.put(RDFS.subClassOf.getURI(), Model.Slot.DIRECT_SUBCLASSES);
		owlProp2FramesInvSlotMap.put(RDFS.subPropertyOf.getURI(), Model.Slot.DIRECT_SUBSLOTS);
		owlProp2FramesInvSlotMap.put(RDFS.domain.getURI(), Model.Slot.DIRECT_TEMPLATE_SLOTS);
	}

	private final static HashSet<String> restrictionPredicates = new HashSet<String>();

	static {
		restrictionPredicates.add(OWL.someValuesFrom.getURI());
		restrictionPredicates.add(OWL.allValuesFrom.getURI());
		restrictionPredicates.add(OWL.hasValue.getURI());
		restrictionPredicates.add(OWL.maxCardinality.getURI());
		restrictionPredicates.add(OWL.minCardinality.getURI());
		restrictionPredicates.add(OWL.cardinality.getURI());
	}

	private final static HashSet<String> logicalPredicates = new HashSet<String>();

	static {
		logicalPredicates.add(OWL.intersectionOf.getURI());
		logicalPredicates.add(OWL.unionOf.getURI());
		logicalPredicates.add(OWL.complementOf.getURI());
		//logicalPredicates.add(OWL.oneOf.getURI());
	}

	private final static HashSet<String> equivalentPropSlots = new HashSet<String>();

	static {
		equivalentPropSlots.add(Model.Slot.DIRECT_SUBCLASSES);
		equivalentPropSlots.add(Model.Slot.DIRECT_SUPERCLASSES);
	}


	public static String getFramesSlotMapName(String propertyName) {
		return owlProp2FramesSlotMap.get(propertyName);
	}


	public static String getFramesInvSlotMapName(String propertyName) {
		return owlProp2FramesInvSlotMap.get(propertyName);
	}

	public static HashSet<String> getLogicalPredicatesNames() {
		return logicalPredicates;
	}

	public static HashSet<String> getRestrictionPredicatesNames() {
		return restrictionPredicates;
	}

	public static HashSet<String> getEquivalentPropertySlots() {
		return equivalentPropSlots;
	}

}
