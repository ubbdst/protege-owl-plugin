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

package edu.stanford.smi.protegex.owl.swrl.model.factory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class SWRLJavaFactory extends OWLJavaFactory {

    private OWLModel owlModel;

    /**
     * A Hashtable from Protege classes to Java classes from this package
     */
    private HashMap<String, Class<? extends SimpleInstance>> classMap = new HashMap<String, Class<? extends SimpleInstance>>();

    public SWRLJavaFactory(OWLModel owlModel) {
        super((AbstractOWLModel) owlModel);
        this.owlModel = owlModel;

        for (SWRLFrameTypeId swrlType : SWRLFrameTypeId.values()) {
        	classMap.put(swrlType.getProtegeName(), swrlType.getJavaClass());
        }
    } // SWRLJavaFactory


    @Override
    public SimpleInstance createSimpleInstance(FrameID id, Collection directTypes) {
        Constructor<? extends SimpleInstance> constructor;
        Object[] args = {owlModel, id};


        if (directTypes.size() == 1) {
            Cls typeCls = (Cls) directTypes.iterator().next();
            Class<? extends SimpleInstance> c = getJavaTypeFromSWRLType(typeCls);
            if (c != null) {
                try {
                    constructor = c.getConstructor(new Class[]{
                            KnowledgeBase.class,
                            FrameID.class
                    });
                    return constructor.newInstance(args);
                }
                catch (Exception e) {
                    System.err.println("Fatal Error: Could not create SimpleInstance from OWL metaclass " + c.getName());
                    Log.getLogger().log(Level.SEVERE, "Exception caught", e);
                    return new DefaultSimpleInstance(owlModel, id);
                } // try

            } // if
        } // if

        return super.createSimpleInstance(id, directTypes);

    } // createSimpleInstance


    private Class<? extends SimpleInstance> getJavaTypeFromSWRLType(Cls type) {
        String typeClsName = type.getName();
        Class<? extends SimpleInstance> c = classMap.get(typeClsName);
        if (c == null) {
            Iterator<?> supers = type.getSuperclasses().iterator();
            while (c == null && supers.hasNext()) {
                Frame frame = (Frame) supers.next();
                c = classMap.get(frame.getName());
            }
        }
        return c;
    }


    @Override
    public boolean isCorrectJavaImplementationClass(FrameID id, Collection types, Class clas) {
        for (Object o : types) {
            if (o instanceof Cls) {
                if (getJavaTypeFromSWRLType((Cls) o) != null) {
                    return false;
                }
            }
        }
        return super.isCorrectJavaImplementationClass(id, types, clas);
    }

    @Override
    public Frame createFrameFromClassId(int javaClassId, FrameID id) {

        if (javaClassId >= SWRLFrameTypeId.SWRL_FRAME_TYPE_ID_BASE &&
                javaClassId <= SWRLFrameTypeId.SWRL_FRAME_TYPE_ID_END) {
            int index = javaClassId - SWRLFrameTypeId.SWRL_FRAME_TYPE_ID_BASE;
            SWRLFrameTypeId typeId = SWRLFrameTypeId.values()[index];
            Frame frame = createInstance(id, typeId.getJavaClass());
            if (frame != null) {
                return frame;
            }
        }
        return super.createFrameFromClassId(javaClassId, id);
    }

    @Override
    public int getJavaClassId(Frame frame) {
        for (SWRLFrameTypeId frameTypeId : SWRLFrameTypeId.values()) {
            try {
                Class<? extends SimpleInstance> javaType = frameTypeId.getJavaClass();
                if (javaType.isInstance(frame)) {
                    return frameTypeId.getFrameTypeId();
                }
            } catch (Exception e) {
                // this should never happen!
                Log.getLogger().log(Level.WARNING, "Error at getting the Java class Id for: " + frame , e);
            }
        }

        return super.getJavaClassId(frame);

    }

} // SWRLJavaFactory
