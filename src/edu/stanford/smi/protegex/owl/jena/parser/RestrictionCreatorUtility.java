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

import com.hp.hpl.jena.vocabulary.OWL;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLHasValue;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public class RestrictionCreatorUtility {	
	
	private final static HashMap<String, String> restrictionURI2MetaclassName = new HashMap<String, String>();
	
	//make the hashmap with objects
	static {
		restrictionURI2MetaclassName.put(OWL.someValuesFrom.getURI(), OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
		restrictionURI2MetaclassName.put(OWL.allValuesFrom.getURI(), OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION);
		restrictionURI2MetaclassName.put(OWL.hasValue.getURI(), OWLNames.Cls.HAS_VALUE_RESTRICTION);
		restrictionURI2MetaclassName.put(OWL.maxCardinality.getURI(), OWLNames.Cls.MAX_CARDINALITY_RESTRICTION);
		restrictionURI2MetaclassName.put(OWL.minCardinality.getURI(), OWLNames.Cls.MIN_CARDINALITY_RESTRICTION);
		restrictionURI2MetaclassName.put(OWL.cardinality.getURI(), OWLNames.Cls.CARDINALITY_RESTRICTION);
	}
	
	private final static HashMap<String, String> filler2SlotName = new HashMap<String, String>();
	
	//make the hashmap with objects
	static {
		filler2SlotName.put(OWL.someValuesFrom.getURI(), OWLNames.Slot.SOME_VALUES_FROM);
		filler2SlotName.put(OWL.allValuesFrom.getURI(), OWLNames.Slot.ALL_VALUES_FROM);
		filler2SlotName.put(OWL.hasValue.getURI(), OWLNames.Slot.HAS_VALUE);
		filler2SlotName.put(OWL.maxCardinality.getURI(), OWLNames.Slot.MAX_CARDINALITY);
		filler2SlotName.put(OWL.minCardinality.getURI(), OWLNames.Slot.MIN_CARDINALITY);
		filler2SlotName.put(OWL.cardinality.getURI(), OWLNames.Slot.CARDINALITY);
	}
	
	
	
	public static Frame createRestriction(OWLModel owlModel, FrameID id, String predUri, TripleStore ts) {
		Frame inst = ((KnowledgeBase) owlModel).getFrame(id);
		OWLSystemFrames systemFrames = owlModel.getSystemFrames();
		
		if (inst != null)
			return inst;
		
		if (predUri.equals(OWL.someValuesFrom.getURI())) {
			inst = new DefaultOWLSomeValuesFrom(owlModel, id);
		} else if (predUri.equals(OWL.allValuesFrom.getURI())) {
			inst = new DefaultOWLAllValuesFrom(owlModel, id);
		} else 	if (predUri.equals(OWL.hasValue.getURI())) {
			inst = new DefaultOWLHasValue(owlModel, id);
		} else if (predUri.equals(OWL.maxCardinality.getURI())) {
			inst = new DefaultOWLMaxCardinality(owlModel, id);
		} else if (predUri.equals(OWL.minCardinality.getURI())) {
			inst = new DefaultOWLMinCardinality(owlModel, id);
		} else if (predUri.equals(OWL.cardinality.getURI())) {
			inst = new DefaultOWLCardinality(owlModel, id);
		}
		inst.assertFrameName();
		FrameCreatorUtility.addOwnSlotValue(inst, systemFrames.getRdfTypeProperty(), systemFrames.getOwlRestrictionClass(), ts);
//        ((RDFResource) inst).setPropertyValue(systemFrames.getRdfTypeProperty(), systemFrames.getOwlNamedClassClass());
		
		// should be safe
		Cls metaCls = ((KnowledgeBase) owlModel).getCls(restrictionURI2MetaclassName.get(predUri));
				
		FrameCreatorUtility.addInstanceType((Instance)inst, metaCls, ts);
		
		return inst;
	}


	//remove the pred argument
	public static boolean addRestrictionFiller(OWLModel owlModel, Frame restriction, Frame filler, String predUri, TripleStore ts) {
		if (restriction == null || filler == null)
			return false;
		
		Slot fillerSlot = owlModel.getSlot(filler2SlotName.get(predUri));
		
		if (fillerSlot == null)
			return false;
		
		FrameCreatorUtility.addOwnSlotValue(restriction, fillerSlot, filler, ts);
		
		return true;
	}
	
	

}
