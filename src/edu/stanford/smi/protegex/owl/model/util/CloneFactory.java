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
import java.util.Iterator;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * A utility class to clone a named class
 * TODO: add methods for copying Properties and Individuals
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         11-Jan-2006
 */
public class CloneFactory {

    public static OWLNamedClass cloneOWLNamedClass(OWLNamedClass source) {
    	return cloneOWLNamedClass(source, getNextAvailableCloneName(source));
    }
    
    public static OWLNamedClass cloneOWLNamedClass(OWLNamedClass source, String cloneName) {
    	OWLModel owlModel = source.getOWLModel();    	
    	if (cloneName != null && owlModel.getRDFResource(cloneName) != null) {
    		Log.getLogger().warning("RDFResource with name " + cloneName + " already exists.");
    		return null;
    	}
    	
        OWLNamedClass clone = owlModel.createOWLNamedClass(cloneName);
        
        ResourceCopier scopier = new ResourceCopier();
	    scopier.copyMultipleSlotValues(source, clone);

        Collection sourceSuperclasses = source.getSuperclasses(false);
        for (Iterator it = sourceSuperclasses.iterator(); it.hasNext();) {
            RDFSClass sourceSuperclass = (RDFSClass) it.next();
            sourceSuperclass.accept(scopier);
            RDFSClass clonesSuperclass = (RDFSClass) scopier.getCopy();
            clone.addSuperclass(clonesSuperclass);
            // add equivalent classes
            if (sourceSuperclass.getSuperclasses(false).contains(source)) {
                clonesSuperclass.addSuperclass(clone);
            }
        }

        // remove owl:Thing parent unless specified in original
        if (!sourceSuperclasses.contains(owlModel.getOWLThingClass())) {
            clone.removeSuperclass(owlModel.getOWLThingClass());
        }
        return clone;
    }
    
    public static String getNextAvailableCloneName(OWLNamedClass source) {
    	OWLModel owlModel = source.getOWLModel();    	
    	 String newName = null;    	 
         int i = 2;
         do {
             newName = source.getName() + "_" + i;
             i++;
         } while (owlModel.getRDFResource(newName) != null);         
         return newName;
    }
    
}
