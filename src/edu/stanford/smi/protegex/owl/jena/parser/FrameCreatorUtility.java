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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDataRange;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLVariable;

public class FrameCreatorUtility {
	private static transient Logger log = Log.getLogger(FrameCreatorUtility.class);

    public static Frame createFrameWithType(OWLModel owlModel, FrameID id, Cls type, TripleStore ts) {
        Frame frame = ((KnowledgeBase) owlModel).getFrame(id);

        if (frame != null) {
			return frame;
		}

        String typeUri = type.getName();

        //maybe remove the anon condition
        if (typeUri.equals(OWL.Ontology.getURI())) {
            frame = new DefaultOWLOntology(owlModel, id );
        }
        else if (typeUri.equals(OWL.Class.getURI())) {
            frame = new DefaultOWLNamedClass(owlModel, id );
        } else if (typeUri.equals(OWL.DeprecatedClass.getURI())) {
        	frame = new DefaultOWLNamedClass(owlModel, id );
        }
        else if (typeUri.equals(OWL.DatatypeProperty.getURI())) {
            frame = new DefaultOWLDatatypeProperty(owlModel, id);
        }
        else if (typeUri.equals(OWL.ObjectProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);
        }
        else if (typeUri.equals(OWL.TransitiveProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);
        }
        else if (typeUri.equals(OWL.SymmetricProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);
        }
        else if (typeUri.equals(OWL.AnnotationProperty.getURI())) {
            frame = new DefaultRDFProperty(owlModel, id);  //should this be abstract owl prop?
        }
        else if (typeUri.equals(OWL.InverseFunctionalProperty.getURI())) {
            frame = new DefaultOWLObjectProperty(owlModel, id);
        }
        else if (typeUri.equals(OWL.FunctionalProperty.getURI())) {
            frame = new DefaultRDFProperty(owlModel, id);
            ((DefaultRDFProperty)frame).setFunctional(true);
        }
        else if (typeUri.equals(OWL.AllDifferent.getURI())) {
            frame = new DefaultOWLAllDifferent(owlModel, id);
        }
        else if (typeUri.equals(OWL.DataRange.getURI())) {
            frame = new DefaultOWLDataRange(owlModel, id);
        }
        else if (typeUri.equals(RDF.Property.getURI())) {
            frame = new DefaultRDFProperty(owlModel, id);
        }  else if (typeUri.equals(OWL.DeprecatedProperty.getURI())) {
        	//is this correct? Hopefully it will be swizzled later, if needed
        	frame = new DefaultRDFProperty(owlModel, id);
        }
        else if (typeUri.equals(RDF.List.getURI())) {
            frame = new DefaultRDFList(owlModel, id);
        }
        else if (typeUri.equals(RDFS.Class.getURI())) {
            frame = new DefaultRDFSNamedClass(owlModel, id);
        }
        else if (typeUri.equals(RDFS.Datatype.getURI()))  {
            frame = new DefaultRDFSDatatype(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.ATOM_LIST)) {
            frame = new DefaultSWRLAtomList(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.BUILTIN_ATOM)) {
            frame = new DefaultSWRLBuiltinAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.BUILTIN)) {
            frame = new DefaultSWRLBuiltin(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.CLASS_ATOM)) {
            frame = new DefaultSWRLClassAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.DATA_RANGE_ATOM)) {
            frame = new DefaultSWRLDataRangeAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.DATAVALUED_PROPERTY_ATOM)) {
            frame = new DefaultSWRLDatavaluedPropertyAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.DIFFERENT_INDIVIDUALS_ATOM)) {
            frame = new DefaultSWRLDifferentIndividualsAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.IMP)) {
            frame = new DefaultSWRLImp(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.INDIVIDUAL_PROPERTY_ATOM)) {
            frame = new DefaultSWRLIndividualPropertyAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.SAME_INDIVIDUAL_ATOM)) {
            frame = new DefaultSWRLSameIndividualAtom(owlModel, id);
        }
        else if (typeUri.equals(SWRLNames.Cls.VARIABLE)) {
            frame = new DefaultSWRLVariable(owlModel, id);
        }

        else {
            frame = createDefaultEntity(owlModel, id, type, ts);
        }

        assertFrameName(ts, frame);

        if (log.getLevel() == Level.FINE) {
    		log.fine("Created frame: " + frame);
    	}

        if (!hasDirectType((Instance)frame, type)) {
        	if (log.getLevel() == Level.FINE) {
        		log.fine("Adding direct type to " + frame + " type: " + type);
        	}
        	addInstanceType((Instance)frame, type, ts);
        	//--the rdf:type will be added in addTriple in the TripleProcessorForResources
        	//addOwnSlotValue(frame, systemFrames.getRdfTypeProperty(), type, ts);
        }

        return frame;

    }

    private static Frame createDefaultEntity(OWLModel owlModel, FrameID id, Cls type, TripleStore ts) {
    	//This is actually the job of the Java factory, but we don't want to invoke it because it is too expensive
    	//We try to make some guess about the type of the entity, so that we minimize postprocessing
    	if (type.hasSuperclass(owlModel.getRDFSNamedClassClass())) { //type is a metaclass, so this should be a named owl (or rdfs) class
    		return new DefaultOWLNamedClass(owlModel, id);
    	} else if (type.hasSuperclass(owlModel.getRDFPropertyClass())) { //type is metaproperty
    		return new DefaultRDFProperty(owlModel, id);
    	}

    	return new DefaultOWLIndividual(owlModel, id);
    }


    public static void assertFrameName(TripleStore ts, Frame frame) {
    	NarrowFrameStore nfs = ts.getNarrowFrameStore();
    	nfs.addValues(frame, frame.getKnowledgeBase().getSystemFrames().getNameSlot(), null, false,
    			CollectionUtilities.createCollection(frame.getName()));
	}


	public static boolean addInstanceType(Instance inst, Cls type, TripleStore ts) {
        if (inst == null || type == null) {
            return false;
        }

        Slot typeSlot = inst.getKnowledgeBase().getSystemFrames().getDirectTypesSlot();
        addOwnSlotValue(inst, typeSlot , type, ts);
        addOwnSlotValue(type, inst.getKnowledgeBase().getSystemFrames().getDirectInstancesSlot(), inst, ts);

        return true;
    }
	
	
	public static boolean setInstanceType(Instance inst, Cls type, TripleStore ts) {
        if (inst == null || type == null) {
            return false;
        }

        Slot typeSlot = inst.getKnowledgeBase().getSystemFrames().getDirectTypesSlot();
        setOwnSlotValue(inst, typeSlot , type, ts);
        setOwnSlotValue(type, inst.getKnowledgeBase().getSystemFrames().getDirectInstancesSlot(), inst, ts);

        return true;
    }

    public static boolean setOwnSlotValue(Frame frame, Slot slot, Frame value, TripleStore ts) {
    	 if (frame == null || slot == null) {
             return false;
         }

         NarrowFrameStore nfs = ts.getNarrowFrameStore();
         //what should happen if value is a collection?
         nfs.setValues(frame, slot, null, false, CollectionUtilities.createCollection(value));

         return true;		
	}

	public static boolean removeInstanceType(Instance inst, Cls type) {
        Slot typeSlot = inst.getKnowledgeBase().getSystemFrames().getDirectTypesSlot();
        ParserUtil.getSimpleFrameStore(inst).removeDirectOwnSlotValue(inst, typeSlot , type);
        ParserUtil.getSimpleFrameStore(type).removeDirectOwnSlotValue(type, inst.getKnowledgeBase().getSystemFrames().getDirectInstancesSlot(), inst);
        return true;
    }


    public static boolean hasDirectType(Instance inst, Cls type) {
    	return ParserUtil.getSimpleFrameStore(inst).getDirectTypes(inst).contains(type);
    }

    public static boolean hasDirectSuperclass(Cls cls, Cls supercls) {
    	return ParserUtil.getSimpleFrameStore(cls).getDirectSuperclasses(cls).contains(supercls);
    }

    public static boolean createSubclassOf(Cls cls, Cls superCls, TripleStore ts) {
        if (cls == null || superCls == null) {
            return false;
        }

        NarrowFrameStore nfs = ts.getNarrowFrameStore();

    	nfs.addValues(cls, cls.getKnowledgeBase().getSystemFrames().getDirectSuperclassesSlot(), null, false,
    			CollectionUtilities.createCollection(superCls));
    	nfs.addValues(superCls, cls.getKnowledgeBase().getSystemFrames().getDirectSubclassesSlot(), null, false,
    			CollectionUtilities.createCollection(cls));

        return true;
    }


    public static boolean createSubpropertyOf(Slot slot, Slot superSlot, TripleStore ts) {
        if (slot == null || superSlot == null) {
            return false;
        }

        NarrowFrameStore nfs = ts.getNarrowFrameStore();

    	nfs.addValues(slot, slot.getKnowledgeBase().getSystemFrames().getDirectSuperslotsSlot(), null, false,
    			CollectionUtilities.createCollection(superSlot));
    	nfs.addValues(superSlot, slot.getKnowledgeBase().getSystemFrames().getDirectSubclassesSlot(), null, false,
    			CollectionUtilities.createCollection(slot));

        return true;
    }

    public static boolean addOwnSlotValue(Frame frame, Slot slot, Object value, TripleStore ts) {
        if (frame == null || slot == null) {
            return false;
        }

        NarrowFrameStore nfs = ts.getNarrowFrameStore();
        //what should happen if value is a collection?
        nfs.addValues(frame, slot, null, false, CollectionUtilities.createCollection(value));

        return true;
    }

    public static Collection<Cls> getDirectTypes(Instance instance) {
        return ParserUtil.getSimpleFrameStore(instance).getDirectTypes(instance);
    }

    public static boolean hasOwnSlotValue(Frame frame, Slot slot, Object value) {
    	Collection values = ParserUtil.getSimpleFrameStore(frame).getDirectOwnSlotValues(frame, slot);
    	return values.contains(value);
    }

    public static boolean hasRDFType(Frame frame, Slot rdfTypeSlot, Cls type, TripleStore ts) {
    	NarrowFrameStore nfs = ts.getNarrowFrameStore();
    	return nfs.getValues(frame, rdfTypeSlot, null, false).contains(type);
    }

    public static OWLEnumeratedClass createOWLEnumeratedCls(OWLModel owlModel, String enumClsName, TripleStore ts) {
    	OWLEnumeratedClass cls = new DefaultOWLEnumeratedClass(owlModel, new FrameID(enumClsName));
		FrameCreatorUtility.assertFrameName(ts, cls);
		RDFSNamedClass owlEnumeratedClassClass = owlModel.getSystemFrames().getOwlEnumeratedClassClass();
		FrameCreatorUtility.addInstanceType(cls, owlEnumeratedClassClass, ts);
		FrameCreatorUtility.addOwnSlotValue(cls, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass(), ts);
		return cls;
    }

}
