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

package edu.stanford.smi.protegex.owl.javacode;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class ProtegeJavaMapping {
    
    private static Map<String, Entry> protegeMap  = new HashMap<String, Entry>();
    private static Map<Class, Entry> interfaceMap = new HashMap<Class, Entry>();
    private static Map<Class, Entry> implementationMap = new HashMap<Class, Entry>();

    public static void add(String protegeClassName, 
                           Class<? extends RDFResource> javaInterface, 
                           Class<? extends RDFResource> javaImplementation) {
        Entry entry = new Entry(protegeClassName, javaInterface, javaImplementation);
        protegeMap.put(protegeClassName, entry);
        interfaceMap.put(javaInterface, entry);
        implementationMap.put(javaImplementation, entry);
    }
    
    @SuppressWarnings("unchecked")
    public static <X> X create(OWLModel owlModel, Class<? extends X> javaInterface, String name) {
        Entry entry = interfaceMap.get(javaInterface);
        if (entry == null) {
            return null;
        }
        if (name == null) {
            name = owlModel.getNextAnonymousResourceName();
        }
        RDFSNamedClass cls = owlModel.getRDFSNamedClass(entry.getProtegeClass());
        if (cls == null) {
            return null;
        }
        cls.createInstance(name);
        return constructImplementation(owlModel, (Class<? extends X>) entry.getJavaImplementation(), new FrameID(name));
    }
    
    public static boolean canAs(RDFResource resource, Class<? extends RDFResource> javaInterface) {
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return true;
        }
        return getJavaImplementation(resource, javaInterface) != null;
    }
    
    public static <X extends RDFResource> X as(RDFResource resource, Class<? extends X> javaInterface) {
        OWLModel owlModel = resource.getOWLModel();
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return javaInterface.cast(resource);
        }
        Class<? extends X> type = getJavaImplementation(resource, javaInterface);
        return constructImplementation(owlModel, type, resource.getFrameID());
    }
    
    private static <X> X constructImplementation(OWLModel owlModel, Class<? extends X> implType, FrameID id) {
        try {
            Constructor<? extends X> con = implType.getConstructor(new Class[] { OWLModel.class, FrameID.class});
            return con.newInstance(new Object[] { owlModel, id });
        }
        catch (Throwable t) {
            ClassCastException classcast = new ClassCastException("Resource " + id.getName() + " could not be cast to type " + implType);
            classcast.initCause(t);
            throw classcast;
        }
    }
    
    private static <X> Class<? extends X> getJavaImplementation(RDFResource resource, Class<? extends X> javaInterface) {
    	Collection protegeTypes = resource.getProtegeTypes();
    	Collection allTypes = new ArrayList();
    	allTypes.addAll(protegeTypes);
    	for (Object o  : protegeTypes) {
            if  (!(o instanceof RDFSNamedClass)) {
                continue;
            }
            RDFSNamedClass type = (RDFSNamedClass) o;
    		Collection superclasses = type.getSuperclasses(true);
    		for (Object sup : superclasses) {
				if ( ! allTypes.contains(sup) ) {
					allTypes.add(sup);
				}
			}
    	}
        //for (Object o  : resource.getProtegeTypes()) {
    	for (Object o  : allTypes) {
            if  (!(o instanceof Cls)) {
                continue;
            }
            Cls type = (Cls) o;
            Entry entry = protegeMap.get(type.getName());
            if (entry == null) {
                continue;
            }
            Class<? extends RDFResource> javaImplementationClass = entry.getJavaImplementation();
            if (javaInterface.isAssignableFrom(javaImplementationClass)) {
                return javaImplementationClass.asSubclass(javaInterface);
            }
        }
        return null;
    }
    
    private static class Entry {
        private String protegeClass;
        private Class<? extends RDFResource> javaInterface;
        private Class<? extends RDFResource> javaImplementation;
        
        public Entry(String protegeClass,
                     Class<? extends RDFResource> javaInterface,
                     Class<? extends RDFResource> javaImplementation) {
            this.protegeClass = protegeClass;
            this.javaInterface = javaInterface;
            this.javaImplementation = javaImplementation;
        }

        public String getProtegeClass() {
            return protegeClass;
        }

        public Class<? extends RDFResource> getJavaInterface() {
            return javaInterface;
        }

        public Class<? extends RDFResource> getJavaImplementation() {
            return javaImplementation;
        }        
    }
    
}
