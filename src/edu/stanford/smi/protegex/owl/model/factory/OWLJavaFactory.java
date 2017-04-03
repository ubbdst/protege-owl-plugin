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

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultCls;
import edu.stanford.smi.protege.model.DefaultFacet;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.DefaultSlot;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.DefaultFrameFactory;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDataRange;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFUntypedResource;

/**
 * A DefaultFrameFactory that creates the proper Java objects for Protege frames.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLJavaFactory extends DefaultFrameFactory {

    /**
     * A Hashtable from Protege metaclass names to Java class names from this package
     */
    private static Hashtable<String, Class<? extends RDFResource>> clsNames = new Hashtable<String, Class<? extends RDFResource>>();


    static {
        for (OWLFactoryClassType type : OWLFactoryClassType.values()) {
            clsNames.put(type.getTypeName(), type.getImplementingClass());
        }
    }

    private static final Class<?>[] CONSTRUCTOR_PARAMETERS = { KnowledgeBase.class, FrameID.class };

    private AbstractOWLModel owlModel;

    public OWLJavaFactory(AbstractOWLModel owlModel) {
        super(owlModel);
        this.owlModel = owlModel;
    }


    @Override
    public Frame createFrameFromClassId(int javaClassId, FrameID id) {
        Frame frame = null;

        //copied from DefaultFramefactory
        switch (javaClassId) {
            case DEFAULT_CLS_JAVA_CLASS_ID:
                frame = createCls(id, DefaultCls.class);
                break;
            case DEFAULT_SLOT_JAVA_CLASS_ID:
                frame = createSlot(id, DefaultSlot.class);
                break;
            case DEFAULT_FACET_JAVA_CLASS_ID:
                frame = createFacet(id, DefaultFacet.class);
                break;
            case DEFAULT_SIMPLE_INSTANCE_JAVA_CLASS_ID:
                frame = createSimpleInstance(id, DefaultSimpleInstance.class);
                break;
        }

        //TT: should this be rewritten?
        if (frame != null) {
        	return frame;
        }

        Class<? extends Instance> javaType = null;

        try {
			javaType = FrameTypeId2OWLJavaClass.getJavaClass(javaClassId);
		} catch (Exception e) {
			//this should never happen!
			Log.getLogger().log(Level.SEVERE, "Error at creating Java class with Java Frame type id: " + javaClassId , e);
		}

		if (javaType == null) {
			throw new RuntimeException("Invalid java class id: " + javaClassId);
		}

        return createInstance(id, javaType);
    }


    @Override
    public int getJavaClassId(Frame frame) {
    	for (Iterator<Class<? extends Instance>> iter = FrameTypeId2OWLJavaClass.getOrderedJavaClasses().iterator(); iter.hasNext();) {
    		try {
    			Class<? extends Instance> javaType = iter.next();
    			if (javaType.isInstance(frame)) {
    				return FrameTypeId2OWLJavaClass.getFrameTypeId(javaType);
    			}
			} catch (Exception e) {
				// this should never happen!
				Log.getLogger().log(Level.SEVERE, "Error at getting the Java class Id for: " + frame , e);
			}
		}

    	return super.getJavaClassId(frame);

    }


    protected Instance createInstance(FrameID id, Class<? extends Instance> type) {
        Instance instance = null;
        try {
            Constructor<? extends Instance> constructor = type.getConstructor(CONSTRUCTOR_PARAMETERS);
            instance = constructor.newInstance(new Object[] { owlModel, id });
        } catch (Exception e) {
            Log.getLogger().severe(Log.toString(e));
        }
        return instance;
    }



    public RDFResource as(RDFResource resource, Class javaInterface) {
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return resource;
        }
        else {
            Constructor con = getImplementationConstructor(javaInterface);
            try {
                return (RDFResource) con.newInstance(new Object[]{
                        resource.getOWLModel(),
                        ((Frame) resource).getFrameID()
                });
            }
            catch (Exception ex) {
                System.err.println("[OWLJavaFactory] Fatal Error: Could not create Java object for " + javaInterface);
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                return resource;
            }
        }
    }


    public boolean canAs(RDFResource resource, Class javaInterface) {
        if (javaInterface.isAssignableFrom(resource.getClass())) {
            return true;
        }
        else {
            Set clses = new HashSet();
            for (Iterator it = resource.getProtegeTypes().iterator(); it.hasNext();) {
                RDFSClass type = (RDFSClass) it.next();
                clses.add(type);
                clses.addAll(type.getSuperclasses(true));
            }
            String matchName = javaInterface.getName();
            matchName = matchName.substring(matchName.lastIndexOf('.') + 1);
            for (Iterator it = clses.iterator(); it.hasNext();) {
                RDFSClass type = (RDFSClass) it.next();
                if (type instanceof RDFSNamedClass) {
                    String name = getJavaInterfaceName(type);
                    if (name.equals(matchName)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }


    public String getImplementationClassName(Class javaInterface) {
        String name = javaInterface.getName();
        int lastDot = name.lastIndexOf(".");
        String partialName = name.substring(lastDot + 1);
        return name.substring(0, lastDot + 1) + "impl.Default" + partialName;
    }


    public Class getImplementationClass(Class javaInterface) throws ClassNotFoundException {
        return Class.forName(getImplementationClassName(javaInterface));
    }


    public Constructor getImplementationConstructor(Class javaInterface) {
        try {
            Class c = getImplementationClass(javaInterface);
            try {
                return c.getConstructor(new Class[]{
                        OWLModel.class,
                        FrameID.class
                });
            }
            catch (Exception ex) {
                // Ignore first attempt
            }
            return c.getConstructor(new Class[]{
                    KnowledgeBase.class,
                    FrameID.class
            });
        }
        catch (Exception ex) {
          Log.getLogger().severe("[OWLJavaFactory] Fatal Error: Could not find constructor for " + javaInterface);
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
          return null;
        }
    }


    public String getJavaInterfaceName(RDFSClass cls) {
        String typeName = cls.getLocalName();
        StringBuffer className = new StringBuffer();
        for (int i = 0; i < typeName.length(); ++i) {
            char c = typeName.charAt(i);
            if (isValidCharacter(c, className.length())) {
                className.append(c);
            }
        }
        return className.toString();
    }


    /**
     * Creates instances of the corresponding subclass of DefaultCls if one of the metaclasses of
     * the directTypes list is an OWL metaclass.
     *
     * @param id
     * @param directTypes
     * @return the new Cls object
     */
    @Override
	public Cls createCls(FrameID id, Collection directTypes) {

        if (id.equals(Model.ClsID.THING)) {
            return new DefaultOWLNamedClass(owlModel, id);
        }
        final Cls rdfsClsMetaCls = owlModel.getRDFSClassMetaClassCls();
        final Cls namedClsMetaCls = owlModel.getOWLNamedClassMetaClassCls();
        boolean isRDFSNamedClass = false;
        for (Iterator it = directTypes.iterator(); it.hasNext();) {
            final Instance metaCls = (Instance) it.next();
            final String metaClsName = metaCls.getName();
            final Class<? extends RDFResource> javaClass = clsNames.get(metaClsName);
            if (javaClass != null) {
                return createCls(javaClass, id);
            }
            if (!metaCls.isSystem()) {
                if (metaCls instanceof Cls) {
                    if (((Cls) metaCls).hasSuperclass(namedClsMetaCls)) {
                        return new DefaultOWLNamedClass(owlModel, id); // handle subclasses of OWLNamedClass
                    }
                    if (((Cls) metaCls).hasSuperclass(rdfsClsMetaCls)) {
                      isRDFSNamedClass = true;   // handle subclasses of RDFSNamedClass
                    }
                }
                else { // when do I get here exactly?
                  return new DefaultOWLNamedClass(owlModel, id); // Robust but ugly guess
                }
            }
            else if (metaCls.equals(owlModel.getOWLDeprecatedClassClass())) {
                isRDFSNamedClass = true;
            } else if (metaCls.equals(owlModel.getRDFExternalClassClass())) {
            	isRDFSNamedClass = true;
            }
        }
        if (isRDFSNamedClass) {
          return new DefaultRDFSNamedClass(owlModel, id);
        }
        return super.createCls(id, directTypes);
    }


    private Cls createCls(Class<? extends RDFResource> clazz, FrameID id) {
        try {
            Class<?>[] parameterTypes = {KnowledgeBase.class, FrameID.class};
            Constructor<? extends RDFResource> constructor = clazz.getConstructor(parameterTypes);
            Object[] args = {owlModel, id};
            return (Cls) constructor.newInstance(args);
        }
        catch (Exception ex) {
            Log.getLogger().log(Level.SEVERE, "Could not create class: " + id + " of Java type: " + clazz.getSimpleName(), ex);
            return new DefaultOWLNamedClass(owlModel, id);
        }
    }


    @Override
	public SimpleInstance createSimpleInstance(FrameID id, Collection directTypes) {
        if (directTypes.contains(owlModel.getOWLAllDifferentClassCls())) {
            return new DefaultOWLAllDifferent(owlModel, id);
        }
        else if (directTypes.contains(owlModel.getOWLDataRangeClass())) {
            return new DefaultOWLDataRange(owlModel, id);
        }
        else if (directTypes.contains(owlModel.getRDFSDatatypeClass())) {
            return new DefaultRDFSDatatype(owlModel, id);
        }
        else if (directTypes.contains(owlModel.getRDFUntypedResourcesClass())) {
            return new DefaultRDFUntypedResource(owlModel, id);
        }
        else if (directTypes.contains(owlModel.getRDFListCls()) ||
                (directTypes.size() > 0 && ((Cls) directTypes.iterator().next()).hasSuperclass(owlModel.getRDFListClass()))) {
            return new DefaultRDFList(owlModel, id);
        }
        else if (directTypes.contains(owlModel.getOWLOntologyCls())) {
            return new DefaultOWLOntology(owlModel, id);
        }
        else if (directTypes.size() == 0 || directTypes.toArray()[0] instanceof OWLClass) {  // was: OWLNamedClass
            return new DefaultOWLIndividual(owlModel, id);
        }
        else {
            return new DefaultRDFIndividual(owlModel, id);
        }
    }


    @Override
	public Slot createSlot(FrameID id, Collection directTypes) {
        final Cls datatypeSlotMetaCls = owlModel.getOWLDatatypePropertyClass();
        final Cls objectSlotMetaCls = owlModel.getOWLObjectPropertyClass();
        final Cls rdfSlotMetaCls = owlModel.getRDFPropertyClass();
        boolean isRDFProperty = false;
        if (directTypes.isEmpty()) {
            MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(owlModel);
            Slot slot = (Slot) mnfs.getFrame(id);
            return slot == null ? super.createSlot(id, directTypes) : slot;
        }
        for (Iterator it = directTypes.iterator(); it.hasNext();) {
            Cls metaCls = (Cls) it.next();
            if (metaCls.equals(datatypeSlotMetaCls) || metaCls.hasSuperclass(datatypeSlotMetaCls)) {
                return new DefaultOWLDatatypeProperty(owlModel, id);
            }
            else if (metaCls.equals(objectSlotMetaCls) || metaCls.hasSuperclass(objectSlotMetaCls)) {
                return new DefaultOWLObjectProperty(owlModel, id);
            }
            else if (!isRDFProperty && (metaCls.equals(rdfSlotMetaCls) || metaCls.hasSuperclass(rdfSlotMetaCls))) {
                isRDFProperty = true;
            }
            else if (metaCls.equals(owlModel.getRDFExternalPropertyClass())) {
            	isRDFProperty = true;
            }
        }
        if (isRDFProperty) {
          return new DefaultRDFProperty(owlModel, id);
        }
        return super.createSlot(id, directTypes);
    }


    @Override
	public boolean isCorrectJavaImplementationClass(FrameID id, Collection types, Class clas) {
        Cls namedClsMetaCls = owlModel.getOWLNamedClassMetaClassCls();
        Cls datatypeSlotMetaCls = owlModel.getOWLDatatypePropertyMetaClassCls();
        Cls objectSlotMetaCls = owlModel.getOWLObjectPropertyMetaClassCls();
        for (Iterator it = types.iterator(); it.hasNext();) {
            Cls metaCls = (Cls) it.next();
            Class<? extends RDFResource> javaClassName = clsNames.get(metaCls.getName());
            if (javaClassName != null) {
                return clas.equals(javaClassName);
            }
            if (metaCls.hasSuperclass(namedClsMetaCls)) {
                return clas.equals(DefaultOWLNamedClass.class);
            }
            if (metaCls.hasSuperclass(datatypeSlotMetaCls)) {
                return clas.equals(DefaultOWLDatatypeProperty.class);
            }
            if (metaCls.hasSuperclass(objectSlotMetaCls)) {
                return clas.equals(DefaultOWLObjectProperty.class);
            }
        }
        return clas.equals(DefaultRDFIndividual.class);
    }
}
