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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

class TripleProcessorForResourceObjects extends AbstractStatefulTripleProcessor {
	private static final transient Logger log = Log.getLogger(TripleProcessorForResourceObjects.class);

	//optimization
	private Collection<RDFSNamedClass> untypedMetaClasses = new ArrayList<RDFSNamedClass>();
	
	public enum TripleStatus {
		TRIPLE_PROCESSING_SHOULD_CONTINUE,
		TRIPLE_PROCESSING_COMPLETE,
		TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
	};


	public TripleProcessorForResourceObjects(TripleProcessor processor) {
		super(processor);
		initUntypedMetaClasses();
	}
	
	protected void initUntypedMetaClasses() {
		untypedMetaClasses.add(((AbstractOWLModel)owlModel).getRDFExternalClassClass());
		untypedMetaClasses.add(((AbstractOWLModel)owlModel).getRDFExternalPropertyClass());
		untypedMetaClasses.add(((AbstractOWLModel)owlModel).getRDFExternalResourceClass());	
	}

	public boolean processTriple(AResource subj, AResource pred, AResource obj, TripleStore ts, boolean alreadyInUndef) {
		return new InternalTripleProcessorForResourceObjects(subj, pred, obj, ts, alreadyInUndef).processTriple();
	}


	private class InternalTripleProcessorForResourceObjects {

		private AResource subj;
		private String subjName;
		private Frame subjFrame;

		private AResource pred;
		private String predName;
		private Slot predSlot;

		private AResource obj;
		private String objName;
		private Frame objFrame;

		private TripleStore tripleStore;

		private boolean alreadyInUndef;

		public InternalTripleProcessorForResourceObjects(AResource subj, AResource pred, AResource obj,
				TripleStore ts, boolean alreadyInUndef) {
			if (log.isLoggable(Level.FINER)) {
				log.finer("Process Triple " + subj + " " + pred + " " + obj);
			}

			this.subj = subj;
			this.pred = pred;
			this.obj = obj;
			this.alreadyInUndef = alreadyInUndef;
			this.tripleStore = ts;

			predName = ParserUtil.getResourceName(pred);
			predSlot = getSlot(predName);

			// do some checks if it already exists and is twice defined?
			subjName = ParserUtil.getResourceName(subj);
			subjFrame = getFrame(subjName);

			objName = ParserUtil.getResourceName(obj);
			objFrame = getFrame(objName);
		}

		public boolean processTriple() {
			TripleStatus status;

			//System.out.println(subjName + " " + predName + " " + objName);

			status = handlePredUndefs();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}

			status = handleSetTypeAndCreation();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}

			status = handleSubjObjUndefs();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}


			/*
			 * TODO: Add a method to get the subjFrame,
			 * objFrame, predSlot as the expected type
			 * e.g. rdfsSubclassOf -> (cls, cls)
			 */
			adjustJavaTypes();

			status = handleEquivalentClassesOrProperties();
			switch (status) {
			case TRIPLE_PROCESSING_COMPLETE:
				return true;
			case TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS:
				return false;
			}

			handleGeneralizedConceptInclusions();

			addTriple();

			return true;
		}



		private void adjustJavaTypes() {
			//we need a case for each RDF(S)/OWL predicate
			if (predName.equals(RDFS.subClassOf.getURI())) {
				subjFrame = getCls(subjFrame);
				objFrame = getCls (objFrame);
				return;
			}

			if (predName.equals(RDFS.subPropertyOf.getURI())) {
				subjFrame = getSlot(subjFrame);
				objFrame = getSlot(objFrame);
				return;
			}

			if (predName.equals(OWL.disjointWith.getURI())) {
				subjFrame = getCls(subjFrame);
				objFrame = getCls (objFrame);
				return;
			}

		}


		private TripleStatus handleSetTypeAndCreation() {
			TripleStatus status = TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;

			if (predName.equals(OWL.imports.getURI())) {
				OWLImportsCache.addOWLImport(subjName, objName);
			} else if (predName.equals(RDF.type.getURI())) { // creation
				status = handleSetType();
			}
			else if (predName.equals(RDF.first.getURI()) || predName.equals(RDF.rest.getURI())) {
				if (log.isLoggable(Level.FINE)) {
					log.fine("Triple <" + subjName + ", " + predName + ", " + objName + "> signals RDFList creation");
				}
				createRDFList();
			} else if (OWLFramesMapping.getLogicalPredicatesNames().contains(predName)) {
				status = handleCreateLogicalClass();
			} else if (OWLFramesMapping.getRestrictionPredicatesNames().contains(predName)) {
				subjFrame = createRestriction(subjName, predName, tripleStore);
			} else if (predName.equals(OWL.oneOf.getURI())) {
				//TODO: should be refactored in its own method
				subjFrame = getFrame(subjName);
				objFrame = getFrame(objName);

				if (subjFrame != null && objFrame != null && subjFrame instanceof RDFSNamedClass) {
					OWLEnumeratedClass enCls = FrameCreatorUtility.createOWLEnumeratedCls(owlModel, owlModel.getNextAnonymousResourceName(), tripleStore);
					FrameCreatorUtility.addOwnSlotValue(enCls, owlModel.getSystemFrames().getOwlOneOfProperty(), objFrame, tripleStore);
					FrameCreatorUtility.createSubclassOf((Cls)subjFrame, enCls, tripleStore);
					FrameCreatorUtility.createSubclassOf(enCls,(Cls) subjFrame, tripleStore);
					FrameCreatorUtility.addOwnSlotValue(subjFrame, owlModel.getSystemFrames().getOwlEquivalentClassProperty(), enCls, tripleStore);
					status = TripleStatus.TRIPLE_PROCESSING_COMPLETE;
				}
				//globalParserCache.getOneOfTriples().add(new UndefTriple(subj, pred, obj, null, tripleStore));
			}

			subjFrame = getFrame(subjName);
			objFrame = getFrame(objName);

			return status;
		}

		/*
		 * The simple story is that this routine would create the subject frame
		 * by just calling createLogicalClass. But life isn't that easy. It is
		 * possible that the the logical class being created is a named class.
		 * This is awkward in Protege3. So we avoid representing this logical
		 * class as a named class. We create a new unnamed class to hold the
		 * logical expression and state that the named class is equivalent.
		 *
		 * But the pain isn't over. The triple may be processed more than once.
		 * So we need to keep track of whether it has been seen before. This is
		 * awkward because we need to track the whole triple. It is possible
		 * that a named class is a logical class in more than one different way.
		 * But I think that I can track duplicates by using the object of the
		 * triple.
		 *
		 * Had enough? Here is another jolt! I worried about the possibility
		 * that owlModel.getNextAnonymousResourceName() would generate an
		 * anonymous name that would later appear in the parsed ontology
		 * (generated by ParserUtility.getResourceName). So I looked in the two
		 * routines and hacked the latter to always provide a different name
		 * than the former.
		 *
		 * This is a nasty little routine and maybe one day I will be punished
		 * for it.
		 */
		private TripleStatus handleCreateLogicalClass() {
			Frame oldSubjFrame = subjFrame;
			boolean logicalClassIsNamed = false;
			if (!subj.isAnonymous()) {
				if (subjFrame == null) {
					addUndefTriple(subjName);
					return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
				}

				if (objFrame == null) {
					addUndefTriple(objName);
					return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
				}

				subjFrame = globalParserCache.getObjectToNamedLogicalClassSurrogate().get(objName);
				if (subjFrame != null) {
					subjName = subjFrame.getName();
					return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
				}

				// can this cause conflicts??
				subjName = owlModel.getNextAnonymousResourceName();
				logicalClassIsNamed = true;
			}

			subjFrame = createLogicalClass();

			if (logicalClassIsNamed) {
				subjFrame = getCls(subjFrame);
				globalParserCache.getObjectToNamedLogicalClassSurrogate().put(objName, (Cls) subjFrame);
				FrameCreatorUtility.addOwnSlotValue(oldSubjFrame, owlModel.getOWLEquivalentClassProperty(), subjFrame, tripleStore);
				FrameCreatorUtility.createSubclassOf((Cls) subjFrame, getCls(oldSubjFrame), tripleStore);
				FrameCreatorUtility.createSubclassOf(getCls(oldSubjFrame), (Cls) subjFrame, tripleStore);
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}


		private TripleStatus handleSetType() {			
			if (objName.equals(OWL.Restriction.getURI())) {
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}
			
			if (subj.isAnonymous() && objName.equals(OWL.Class.getURI())) {
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}
			
			if (objName.equals(OWL.Ontology.getURI())) {
				handleOntologyDeclaration();
			}
			

			if (objFrame == null) {
				addUndefTriple(objName);
				return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
			}

			// find a better way to handle this...
			boolean subjAlreadyHasType = subjFrame != null && !getTypes(subjFrame).isEmpty();

			objFrame = getCls(objFrame);
			subjFrame = createFrameWithType(subjName, (Cls) objFrame);

			//add the rdf:type triple if not already there
			if (!FrameCreatorUtility.hasRDFType(subjFrame, predSlot, (Cls)objFrame, tripleStore)) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame, tripleStore);
			}

			if (subjAlreadyHasType && objFrame instanceof Cls) {
				if (log.isLoggable(Level.FINE)) {
					log.fine("found an alternative type for " + subjFrame + " = " + objFrame);
				}
				globalParserCache.getMultipleTypesInstanceCache().addType((Instance) subjFrame, (Cls)objFrame);
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		private TripleStatus handlePredUndefs() {
			if (predSlot == null) {
				predSlot = new DefaultRDFProperty(owlModel, new FrameID(predName));
				if (isImporting(tripleStore)) {
					predSlot.setIncluded(true);
				}
				globalParserCache.getFramesWithWrongJavaType().add(predName);
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		private TripleStatus handleSubjObjUndefs() {
			if (subjFrame == null) {
				if (log.isLoggable(Level.FINE)) {
					log.fine("\tDeferring triple because subject is not yet defined");
				}
				addUndefTriple(subjName);
				return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
			}

			if (objFrame == null) {
				addUndefTriple(objName);
				if (log.isLoggable(Level.FINE)) {
					log.fine("+++ Add undef triple: " + subj + " " + pred + " " + obj + " undef:" + objName);
				}
				return TripleStatus.TRIPLE_HAS_UNDEF_NEEDS_POST_PROCESS;
			}
			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		private void handleOntologyDeclaration() {
			// guessing that the ontology for the parsed file is the first
			// ontology found
			if (tripleStore.getName() == null) {
				tripleStore.setName(subjName);
				tripleStore.addIOAddress(subjName);
				//For the top level ontology, we don't know the name
				//and we only discover it here..
				if (!isImporting(tripleStore)) {
					ProtegeOWLParser.setTopOntologyName(subjName);
				}
			}
		}


		private TripleStatus handleEquivalentClassesOrProperties() {
			// special treatment of equivalent classes
			if (predName.equals(OWL.equivalentClass.getURI())) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame, tripleStore);
				//test for duplicates
				if(!FrameCreatorUtility.hasOwnSlotValue(objFrame, predSlot, subjFrame)) {
					subjFrame=getCls(subjFrame);
					objFrame = getCls(objFrame);
					FrameCreatorUtility.createSubclassOf((Cls) subjFrame,(Cls) objFrame, tripleStore);
					if (!subjFrame.equals(objFrame)) {
						FrameCreatorUtility.createSubclassOf((Cls)objFrame, (Cls)subjFrame, tripleStore);
					}
				}
				return TripleStatus.TRIPLE_PROCESSING_COMPLETE;
			}

			return TripleStatus.TRIPLE_PROCESSING_SHOULD_CONTINUE;
		}

		/*
		 * This is not a good method but it would take too long to change it.
		 * The protege 4 method is cleaner but makes it a little hard to find
		 * the axioms until somebody tells you how.
		 */
		private void handleGeneralizedConceptInclusions() {
			if (subjFrame instanceof RDFSClass && ((RDFSClass) subjFrame).isAnonymous()
					&& globalParserCache.getPossibleGCIPredicates().contains(predSlot)) {
				OWLNamedClass axiom = owlModel.createOWLNamedClass(null);
				subjFrame = getCls(subjFrame);
				FrameCreatorUtility.addOwnSlotValue(axiom, owlModel.getOWLEquivalentClassProperty(), subjFrame, tripleStore);
				FrameCreatorUtility.createSubclassOf((Cls)subjFrame, axiom, tripleStore);
				FrameCreatorUtility.createSubclassOf(axiom, (Cls)subjFrame, tripleStore);
				subjFrame = axiom;
				globalParserCache.addGciAxiom(axiom); // need to name these later
			}
		}

		private void addTriple() {
			if (predSlot.equals(owlModel.getRDFTypeProperty())) {
				return;
			}

			// add what it is really in the triple
			FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame, tripleStore);
			// add frame correspondent
			String frameMapSlot = OWLFramesMapping.getFramesSlotMapName(predName);
			if (frameMapSlot != null) {
				FrameCreatorUtility.addOwnSlotValue(subjFrame, getSlot(frameMapSlot), objFrame, tripleStore);
			}
			// add frame pair (inverse) correspondent
			String frameMapInvSlot = OWLFramesMapping.getFramesInvSlotMapName(predName);
			if (frameMapInvSlot != null) {
				FrameCreatorUtility.addOwnSlotValue(objFrame, getSlot(frameMapInvSlot), subjFrame, tripleStore);
			}
		}

		private void addUndefTriple(String undef) {
			processor.addUndefTriple(subj, pred, obj, undef, alreadyInUndef, tripleStore);
		}

		// special treatment of RDFList
		private void createRDFList() {
			Frame subjList = getFrame(subjName);

			// applies both for rdf:fist and rdf:rest
			if (subjList == null) {
				// move this to a RDFListCreator
				FrameID id = new FrameID(subjName);

				Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, owlModel.getSystemFrames().getRdfListClass(), tripleStore);
				if (isImporting(tripleStore)) {
					listFrame.setIncluded(true); // ineffective in client-server or db mode
				}

				if (listFrame != null) {
					checkUndefinedResources(subjName);
				}
			}

			if (!predName.equals(RDF.rest.getURI())) {
				return;
			}

			Frame objList = getFrame(objName);
			if (objList == null) {
				FrameID id = new FrameID(objName);

				Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, owlModel.getSystemFrames().getRdfListClass(), tripleStore);

				if (listFrame != null) {
					checkUndefinedResources(objName);
				}
			}
		}

		private Frame createFrameWithType(String frameUri, Cls type) {
			Frame frame = getFrame(frameUri);
			if (frame != null) {
				return addTypeToInstance(frameUri, (Instance)frame, type);				
			}

			FrameID id = new FrameID(frameUri);
			frame = FrameCreatorUtility.createFrameWithType(owlModel, id, type, tripleStore);
			if (isImporting(tripleStore)) {
				frame.setIncluded(true); 
			}
			if (frame != null) {
				checkUndefinedResources(frameUri);
			}
			return frame;
		}
		
		private Frame addTypeToInstance(String instanceURI, Instance instance, Cls type) {
			if (!FrameCreatorUtility.hasOwnSlotValue(instance, owlModel.getSystemFrames().getNameSlot(), instanceURI)) {
				FrameCreatorUtility.addOwnSlotValue(instance, owlModel.getSystemFrames().getNameSlot(), instanceURI, tripleStore);
			}
			
			Collection<Cls> types = FrameCreatorUtility.getDirectTypes(instance);
		
			if (types!= null && types.contains(type)) { //duplicate types in imports are lost
				return instance;
			}
			
			if (alreadyInUndef) {//frame is undefined
				if (types != null) {
					types = new ArrayList<Cls>(types);
					types.removeAll(untypedMetaClasses);
				}
			}
			
			if (types == null || types.isEmpty()) {//frame was an external resource/class/property 		
				FrameCreatorUtility.addInstanceType(instance, type, tripleStore);				
				globalParserCache.getFramesWithWrongJavaType().add(instanceURI);
				return getFrame(instanceURI);
			}
			
			if (alreadyInUndef) { //optimization
				FrameCreatorUtility.addInstanceType(instance, type, tripleStore);
				simpleFrameStore.swizzleInstance(instance);
			} else {
				globalParserCache.getMultipleTypesInstanceCache().addType(instance, type);
			}

			instance = (Instance) getFrame(instanceURI);
			
			//might be needed because we create the properties on the fly
			if (instance instanceof RDFProperty) {
				instance.setIncluded(isImporting(tripleStore));
			}
			
			return instance;
		}
		

		private Frame createLogicalClass() {
			Frame logClass = getFrame(subjName);

			if (logClass != null) {
				return logClass;
			}

			FrameID id = new FrameID(subjName);
			logClass = LogicalClassCreatorUtility.createLogicalClass(owlModel, id, predName, tripleStore);

			if (logClass != null) {
				checkUndefinedResources(subjName);
			}
			if (isImporting(tripleStore)) {
				logClass.setIncluded(true);
			}

			return logClass;
		}

	} // end InternalProcessorForResourceObjects class

}
