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
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLComplementClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public class LogicalClassCreatorUtility {


	private final static HashMap<String, String> logicalClassURI2MetaclassName = new HashMap<String, String>();

	//make the hashmap with objects
	static {
		logicalClassURI2MetaclassName.put(OWL.complementOf.getURI(), OWLNames.Cls.COMPLEMENT_CLASS);
		logicalClassURI2MetaclassName.put(OWL.intersectionOf.getURI(), OWLNames.Cls.INTERSECTION_CLASS);
		logicalClassURI2MetaclassName.put(OWL.unionOf.getURI(), OWLNames.Cls.UNION_CLASS);
		//OneOf - handled specially
		//logicalClassURI2MetaclassName.put(OWL.oneOf.getURI(), OWLNames.Cls.ENUMERATED_CLASS);
	}

	private final static HashMap<String, String> filler2SlotName = new HashMap<String, String>();

	//make the hashmap with objects
	static {
		filler2SlotName.put(OWL.complementOf.getURI(), OWLNames.Slot.COMPLEMENT_OF);
		filler2SlotName.put(OWL.intersectionOf.getURI(), OWLNames.Slot.INTERSECTION_OF);
		filler2SlotName.put(OWL.unionOf.getURI(), OWLNames.Slot.UNION_OF);
		//filler2SlotName.put(OWL.oneOf.getURI(), OWLNames.Slot.ONE_OF);
	}


	public static Frame createLogicalClass(OWLModel owlModel, FrameID id, String predUri, TripleStore ts) {
		Frame inst = ((KnowledgeBase) owlModel).getFrame(id);
		OWLSystemFrames systemFrames = owlModel.getSystemFrames();

		if (inst != null) {
			return inst;
		}

		if (predUri.equals(OWL.complementOf.getURI())) {
			inst = new DefaultOWLComplementClass(owlModel, id);
		} else if (predUri.equals(OWL.intersectionOf.getURI())) {
			inst = new DefaultOWLIntersectionClass(owlModel, id);
		} else 	if (predUri.equals(OWL.unionOf.getURI())) {
			inst = new DefaultOWLUnionClass(owlModel, id);
		}/* else if (predUri.equals(OWL.oneOf.getURI())) { //potential ambiguous case
			inst = new DefaultOWLEnumeratedClass(owlModel, id);
		}
		*/
		inst.assertFrameName();

		//if (!(inst instanceof DefaultOWLEnumeratedClass)) {
			FrameCreatorUtility.addOwnSlotValue(inst, systemFrames.getRdfTypeProperty(), systemFrames.getOwlNamedClassClass(), ts);
			// 	((RDFResource) inst).setPropertyValue(systemFrames.getRdfTypeProperty(), systemFrames.getOwlNamedClassClass());
			Cls metaCls = owlModel.getCls(logicalClassURI2MetaclassName.get(predUri));
			FrameCreatorUtility.addInstanceType((Instance)inst, metaCls, ts);
		//}

		return inst;
	}


	public static boolean addLogicalFiller(OWLModel owlModel, Frame logicalClass, Frame filler, String predUri, TripleStore ts) {
		if (logicalClass == null || filler == null) {
			return false;
		}

		Slot fillerSlot = owlModel.getSlot(filler2SlotName.get(predUri));

		if (fillerSlot == null) {
			return false;
		}

		FrameCreatorUtility.addOwnSlotValue(logicalClass, fillerSlot, filler, ts);

		return true;
	}

}
