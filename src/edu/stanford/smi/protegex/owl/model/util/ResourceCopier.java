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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;

/**
 * A utility visitor class to copy resources by their slot values.
 * This class is used to generate replicas of a class description
 * and will only copy anonymous classes, leaving references to named
 * classes, properties and individuals. Slots that are NOT copied include
 * all sub/superclass slots and several others including :NAME
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         10-Jan-2006
 */
public class ResourceCopier extends OWLModelVisitorAdapter {

    private RDFObject copy;

    protected static String[] doNotCopySlots = {
            Model.Slot.DIRECT_SUBCLASSES,
            Model.Slot.DIRECT_SUPERCLASSES,
            RDFSNames.Slot.SUB_CLASS_OF,
            OWLNames.Slot.EQUIVALENT_CLASS,
            Model.Slot.NAME
    };

    private Set<Slot> doNotCopySlotsList = new HashSet<Slot>();

    private OWLModel owlModel;


    /**
     * Returns the last copy of the last visited resource.  Note
     * that repeatedly calling this method does not create
     * multiple copies.
     */
    public RDFObject getCopy() {
        return copy;
    }

    //////////////////////////////////// DO NOT copy the below - give a reference

    @Override
    public void visitOWLNamedClass(OWLNamedClass source) {
        visitResourceToBeReferenced(source);
    }

    @Override
    public void visitOWLIndividual(OWLIndividual source) {
        visitResourceToBeReferenced(source);
    }

    @Override
    public void visitOWLObjectProperty(OWLObjectProperty source) {
        visitResourceToBeReferenced(source);
    }

    @Override
    public void visitOWLDatatypeProperty(OWLDatatypeProperty source) {
        visitResourceToBeReferenced(source);
    }

    @Override
    public void visitRDFSNamedClass(RDFSNamedClass source) {
        visitResourceToBeReferenced(source);
    }

    @Override
    public void visitRDFIndividual(RDFIndividual source) {
        visitResourceToBeReferenced(source);
    }

    @Override
    public void visitRDFProperty(RDFProperty source) {
        visitResourceToBeReferenced(source);
    }

    @Override
    public void visitRDFDatatype(RDFSDatatype source) {
        visitResourceToBeReferenced(source);
    }

    protected void visitResourceToBeReferenced(RDFResource source) {
        if (!source.isSystem()) {

            owlModel = source.getOWLModel();

            // if needed copy the declaration into the active ontology.
            if (owlModel.getDefaultOWLOntology().getImports().size() > 0) {

                TripleStoreModel tsm = owlModel.getTripleStoreModel();
                TripleStore topTs = tsm.getTopTripleStore();
                TripleStore activeTs = tsm.getActiveTripleStore();

                if (!activeTs.equals(topTs)) {
                    Collection types = source.getRDFTypes();
                    try {
                        tsm.setViewActiveOnly(true);
                        Collection typesSeenByActive = source.getRDFTypes();
                        if (typesSeenByActive == null || typesSeenByActive.isEmpty()) {
                            source.setRDFTypes(types);
                        }
                    }
                    finally {
                        tsm.setViewActiveOnly(false);
                    }
                }
            }
        }

        copy = source;
    }

    @Override
    public void visitRDFSLiteral(RDFSLiteral source) {
        copy = source;
    }

    @Override
    public void visitRDFUntypedResource(RDFUntypedResource source) {
        copy = source;
    }

    //////////////////////////////////// DO copy below

    /////////////////////////////////// anonymous classes

    @Override
    public void visitOWLUnionClass(OWLUnionClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLUnionClass());
    }

    @Override
    public void visitOWLIntersectionClass(OWLIntersectionClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLIntersectionClass());
    }

    @Override
    public void visitOWLEnumeratedClass(OWLEnumeratedClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLEnumeratedClass());
    }

    @Override
    public void visitOWLComplementClass(OWLComplementClass source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLComplementClass());
    }

    /////////////////////////////////// restrictions

    @Override
    public void visitOWLSomeValuesFrom(OWLSomeValuesFrom source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLSomeValuesFrom());
    }

    @Override
    public void visitOWLAllValuesFrom(OWLAllValuesFrom source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLAllValuesFrom());
    }

    @Override
    public void visitOWLCardinality(OWLCardinality source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLCardinality());
    }

    @Override
    public void visitOWLMaxCardinality(OWLMaxCardinality source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLMaxCardinality());
    }

    @Override
    public void visitOWLMinCardinality(OWLMinCardinality source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLMinCardinality());
    }

    @Override
    public void visitOWLHasValue(OWLHasValue source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLHasValue());
    }

    /////////////////////////////////// rdf components

    @Override
    public void visitRDFList(RDFList source) {
        if (source.equals(source.getOWLModel().getRDFNil())) {
            copy = source;
        }
        else {
            copyMultipleSlotValues(source, source.getOWLModel().createRDFList());
        }
    }

    /////////////////////////////////// other

    @Override
    public void visitOWLAllDifferent(OWLAllDifferent source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLAllDifferent());
    }

    @Override
    public void visitOWLDataRange(OWLDataRange source) {
        copyMultipleSlotValues(source, source.getOWLModel().createOWLDataRange());
    }

    @Override
    public void visitOWLOntology(OWLOntology source) {
        String newName = null;
        int i = 2;
        do {
            newName = source.getName() + i;
            i++;
        }
        while (source.getOWLModel().getRDFResource(newName) != null);
        try {
            copyMultipleSlotValues(source, source.getOWLModel().createOWLOntology(newName));
        }
        catch (AlreadyImportedException e) {
            new RuntimeException("This shouldn't happen", e);
        }
    }

    /////////////////////////////////// Utility methods


    public void copyMultipleSlotValues(RDFResource source, RDFResource target) {

        if (source.getOWLModel().equals(owlModel) == false) {
            owlModel = source.getOWLModel();
         
            for (int i = 0; i < doNotCopySlots.length; i++) {
                doNotCopySlotsList.add(owlModel.getSlot(doNotCopySlots[i]));
            }
        }

        // TODO: I think this could be replaced with kb.getOwnSlots(source).iterator() to
        // get rid of the deprecation warning.
        Iterator slots = source.getOwnSlots().iterator();
        while (slots.hasNext()) {
            Slot slot = (Slot) slots.next();
            if (!doNotCopySlotsList.contains(slot)) {
                copySlotValues(source, target, slot);
            }
        }

        copy = target;
    }

    @SuppressWarnings("deprecation")
	public void copySlotValues(RDFResource source, RDFResource target, Slot slot) {
    	
    	OWLModel sourceKb = source.getOWLModel();
    	OWLModel targetKb = target.getOWLModel();

    	Collection values = source.getDirectOwnSlotValues(slot);
    	
    	//TT - The slot should be the target slot.
    	//TT - This method does not treat the case that the target slot is not present
    	if (sourceKb.equals(targetKb) == false) {
    		//this does not treat the case that slot has a different name in the target kb
    		slot = targetKb.getSlot(slot.getName());
    		
    		if (slot == null) {
    			//Log.getLogger().warning("Slot not found. Did not copy " + target + " " + slot);
    			return;
    		}    		
    	}
    	        

        if ((values != null) && (values.size() > 0)) {

            // check if values themselves need to be cloned
            Collection newvalues = new ArrayList(values.size());

            for (Iterator i = values.iterator(); i.hasNext();) {

                Object value = i.next();
                if (value instanceof RDFObject) {
                    ((RDFObject) value).accept(this);
                    value = copy;
                }
                if (value != null) {
                    newvalues.add(value);
                }
            }

            if (newvalues.size() > 0) {
                target.setDirectOwnSlotValues(slot, newvalues);
            }
        }
    }
}


