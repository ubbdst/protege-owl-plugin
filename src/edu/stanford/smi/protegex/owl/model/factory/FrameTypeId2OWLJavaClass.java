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

package edu.stanford.smi.protegex.owl.model.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLComplementClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDataRange;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLHasValue;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFExternalResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFUntypedResource;

/**
 * Class for storing the mapping between the frame type ids and the corresponding Java classes.
 * 
 * @author Tania Tudorache <tudorache@stanford.edu>
 *
 */
public class FrameTypeId2OWLJavaClass {

    private static HashMap<Integer, Class<? extends Instance>> frameTypeId2JavaClass 
                        = new HashMap<Integer, Class<? extends Instance>>();
    private static HashMap<Class<? extends Instance>, Integer> javaClass2frameTypeId 
                        = new HashMap<Class<? extends Instance>, Integer>();
    
    // Used for finding the most specialized Java class of a frame 
    private static Collection<Class<? extends Instance>> orderedJavaClasses 
                        = new ArrayList<Class<? extends Instance>>();
    
    // The basic Protege frames types have ids between 5 -8. Don't override them!
    private static final int OWL_CLASS_FRAME_TYPE_ID_BASE = 9; 
    private static final int OWL_PROPERTY_FRAME_TYPE_ID_BASE = OWL_CLASS_FRAME_TYPE_ID_BASE + 3;
    private static final int OWL_RESTRICTION_FRAME_TYPE_ID_BASE = OWL_PROPERTY_FRAME_TYPE_ID_BASE + 3;
    private static final int OWL_LOGIC_CLASS_FRAME_TYPE_ID_BASE = OWL_RESTRICTION_FRAME_TYPE_ID_BASE + 6;    
    private static final int OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE = OWL_LOGIC_CLASS_FRAME_TYPE_ID_BASE + 3;
    private static final int OWL_OTHER_FRAME_TYPE_ID_BASE = OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 8;
     
    
    //TODO: The order is very important! Don't change it. It is from specific -> general Java types
    static {
    	
    	// frame type ids for OWL/RDFS classes
    	addMappedValues(OWL_CLASS_FRAME_TYPE_ID_BASE, DefaultOWLNamedClass.class);					// 9
    	addMappedValues(OWL_CLASS_FRAME_TYPE_ID_BASE + 1, DefaultOWLEnumeratedClass.class);			// 10
    	addMappedValues(OWL_CLASS_FRAME_TYPE_ID_BASE + 2, DefaultRDFSNamedClass.class);				// 11

        //frame type ids for OWL/RDFS properties    	
    	addMappedValues(OWL_PROPERTY_FRAME_TYPE_ID_BASE, DefaultOWLDatatypeProperty.class);			// 12
    	addMappedValues(OWL_PROPERTY_FRAME_TYPE_ID_BASE + 1, DefaultOWLObjectProperty.class);		// 13
    	addMappedValues(OWL_PROPERTY_FRAME_TYPE_ID_BASE + 2, DefaultRDFProperty.class);				// 14
    	
    	//frame type ids for restrictions    	
    	addMappedValues(OWL_RESTRICTION_FRAME_TYPE_ID_BASE, DefaultOWLSomeValuesFrom.class);		// 15
    	addMappedValues(OWL_RESTRICTION_FRAME_TYPE_ID_BASE + 1, DefaultOWLAllValuesFrom.class);		// 16
    	addMappedValues(OWL_RESTRICTION_FRAME_TYPE_ID_BASE + 2, DefaultOWLHasValue.class);			// 17
    	addMappedValues(OWL_RESTRICTION_FRAME_TYPE_ID_BASE + 3, DefaultOWLMaxCardinality.class);	// 18
    	addMappedValues(OWL_RESTRICTION_FRAME_TYPE_ID_BASE + 4, DefaultOWLMinCardinality.class);	// 19
    	addMappedValues(OWL_RESTRICTION_FRAME_TYPE_ID_BASE + 5, DefaultOWLCardinality.class);		// 20
    	
    	//frame type ids for logical classes    	
    	addMappedValues(OWL_LOGIC_CLASS_FRAME_TYPE_ID_BASE, DefaultOWLIntersectionClass.class);		// 21
    	addMappedValues(OWL_LOGIC_CLASS_FRAME_TYPE_ID_BASE + 1, DefaultOWLUnionClass.class);		// 22
    	addMappedValues(OWL_LOGIC_CLASS_FRAME_TYPE_ID_BASE + 2, DefaultOWLComplementClass.class);	// 23
    	
    	//frame type for individuals
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE, DefaultOWLAllDifferent.class);			// 24
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 1, DefaultOWLIndividual.class);			// 25
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 2, DefaultRDFList.class);				// 26
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 3, DefaultRDFSDatatype.class);			// 27
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 4, DefaultRDFUntypedResource.class);	// 28
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 5, DefaultOWLOntology.class);			// 29
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 6, DefaultOWLDataRange.class);			// 30
    	addMappedValues(OWL_INDIVIDUAL_FRAME_TYPE_ID_BASE + 7, DefaultRDFIndividual.class);			// 31
    	
    	//frame type ids for other stuff
    	addMappedValues(OWL_OTHER_FRAME_TYPE_ID_BASE, DefaultRDFExternalResource.class);			// 32
    	
    	
    	for (int i = 0; i < frameTypeId2JavaClass.keySet().size(); i++) {
			orderedJavaClasses.add(frameTypeId2JavaClass.get(new Integer(OWL_CLASS_FRAME_TYPE_ID_BASE + i)));
		}
    	        
    }
	
    protected static void addMappedValues(int frameTypeId, Class<? extends Instance> javaClass) {
    	frameTypeId2JavaClass.put(new Integer(frameTypeId), javaClass);
    	javaClass2frameTypeId.put(javaClass, new Integer(frameTypeId));
    }
    
    public static int getFrameTypeId(Class<? extends Instance> javaClass) {
    	Integer integer = javaClass2frameTypeId.get(javaClass); 
    	
    	return (integer == null ? 0 : integer.intValue());
    }
       
    
    public static Class<? extends Instance> getJavaClass(int frameTypeId) {
    	return frameTypeId2JavaClass.get(new Integer(frameTypeId));
    }

	public static Collection<Class<? extends Instance>> getOrderedJavaClasses() {
		return orderedJavaClasses;
	}

	
}
