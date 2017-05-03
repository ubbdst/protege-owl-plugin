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

package edu.stanford.smi.protegex.owl.ui.conditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public class OWLClassUtil {
	
	/**
	 * Returns a string with the user-friendly representation of the rdf:list given as argument 
	 * @param rdfList - the rdf:list to be printed
	 * @return a String with the user-friendly representation of the rdf:list given as argument
	 */
	public static String printRDFList(RDFList rdfList) {
		String rdfString = new String("(");
		RDFList list = rdfList;
		
		while (list != null && list.getFirst() != null) {			
			rdfString = rdfString +  list.getFirst() + " ,";				
			list = list.getRest();
		}
		rdfString = rdfString + ")";
		
		return rdfString;
	}
	
	 /**
	  * This method replaces an operand in an intersection class (without deleting the old operand)
	  * It searches for the place in the opernad list where the old operand was, and removes it 
	  * (but does not delete it) and inserts in place the new operand    
	 * @param definitionCls - the intersection class
	 * @param oldClass - old operand class
	 * @param newClass - new operand class
	 * @return true - if the replace was done, fals - otherwise  
	 */
	public static boolean replaceOperand(OWLIntersectionClass definitionCls, RDFSClass oldClass, RDFSClass newClass) {		   
		   boolean replaced = false;
		   RDFList rdfList = (RDFList) definitionCls.getPropertyValue(definitionCls.getOperandsProperty());
			
			if (rdfList == null)
				return false;
						
			RDFList list = rdfList;
						
			while (!replaced && list != null && list.getFirst() != null) {
				if (list.getFirst().equals(oldClass)) {					
					list.setFirst(newClass);					
					replaced = true;
					break;
				}
				list = list.getRest();
			}
			
	        return replaced;
		}
	   
	   
	   /**
	    * Remove a superclass from a class, without deleting the class
	    * This method was necessary because OWLFrameStore deletes all anonymous 
	    * classes automatically when removing a superclass and this is not always desirable.
	    * This method should be removed after we have implemented a better way of dealing 
	    * with anonymous classes. (Tania, 06/12/06)
	 * @param cls
	 * @param superclass
	 */
	public static void removeDirectSuperClass(Cls cls, Cls superclass) {
		   KnowledgeBase kb = cls.getKnowledgeBase();
		   Slot dirSuperclsesSlot = kb.getSlot(Model.Slot.DIRECT_SUPERCLASSES);
		   kb.removeOwnSlotValue(cls, dirSuperclsesSlot, superclass );
		   /*
		   ArrayList<Cls> directOwnSlotValues = new ArrayList(cls.getDirectOwnSlotValues(dirSuperclsesSlot));		
		   directOwnSlotValues.remove(superclass);
		   
		   cls.setDirectOwnSlotValues(dirSuperclsesSlot, directOwnSlotValues);
		   */
	}

	
	public static void removeSuperClass(Cls cls, Cls superclass) {
		   KnowledgeBase kb = cls.getKnowledgeBase();		   
		   Slot dirSuperclsesSlot = kb.getSlot(Model.Slot.DIRECT_SUPERCLASSES);
		   
		   kb.removeOwnSlotValue(cls, dirSuperclsesSlot, superclass );
		   /*
		   ArrayList<Cls> ownSlotValues = new ArrayList(cls.getOwnSlotValues(dirSuperclsesSlot));		
		   ownSlotValues.remove(superclass);
		   
		   cls.setDirectOwnSlotValues(dirSuperclsesSlot, ownSlotValues);
		   */
	}

	
	
	public static void removeEquivalentClass(RDFSClass cls, RDFSClass equivClass) {
		removeDirectSuperClass(cls, equivClass);
		if (equivClass instanceof OWLNamedClass) {
			removeSuperClass(equivClass, cls);
		}
	}
}
