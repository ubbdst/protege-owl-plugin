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
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public abstract class AbstractStatefulTripleProcessor {
	private static final transient Logger log = Log.getLogger(AbstractStatefulTripleProcessor.class);

	protected TripleProcessor processor;

	protected OWLModel owlModel;
	protected GlobalParserCache globalParserCache;
	protected SimpleFrameStore simpleFrameStore;
	protected MergingNarrowFrameStore mnfs;


	public AbstractStatefulTripleProcessor(TripleProcessor processor) {
		this.processor = processor;
		this.owlModel = processor.getOWLModel();
		this.globalParserCache = processor.getGlobalParserCache();
		this.simpleFrameStore = ParserUtil.getSimpleFrameStore(owlModel);
		this.mnfs = MergingNarrowFrameStore.get(owlModel);
	}


	protected Frame createRestriction(String restrName, String predName, TripleStore ts) {
		Frame restriction = getFrame(restrName);

		if (restriction != null) {
			return restriction;
		}

		FrameID id = new FrameID(restrName);
		restriction = RestrictionCreatorUtility.createRestriction(owlModel, id, predName, ts);
		if (restriction != null) {
			checkUndefinedResources(restrName);
		}

		if (isImporting(ts)) {
			restriction.setIncluded(true);
		}
		return restriction;
	}


	protected void checkUndefinedResources(String uri) {
		processor.checkUndefinedResources(uri);
	}

	public void doPostProcessing(){
		// do nothing by default
	}

	public boolean isImporting(TripleStore ts) {
		return !ts.equals(owlModel.getTripleStoreModel().getTopTripleStore());
	}

	protected Frame getFrame(String name) {
		return simpleFrameStore.getFrame(name);
	}

	protected Cls getCls(String name) {
		return getCls(getFrame(name));
	}

	protected Cls getCls(Frame frame) {
		if (frame == null) {
			return (Cls) frame;
		}
		if (frame instanceof Cls) {
			return (Cls) frame;
		}

		//frame is not a class
		if (log.isLoggable(Level.FINE)) {
			log.warning("    Frame with wrong Java type: " + frame.getName() +
					". Expected: RDFSClass (or subclass), got: " + frame.getClass().getName());
		}

		try {
			//should not necessarily be a owl named class - it will be swizzled later anyway
			Frame newFrame = new DefaultOWLNamedClass(frame.getKnowledgeBase(), new FrameID(frame.getName()));
			swizzleFrame(frame, newFrame);
			frame = newFrame;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error at changing Java type of: " + frame + " to DefaultOWLNamedClass", e);
		}

		globalParserCache.getFramesWithWrongJavaType().add(frame.getName());

		//TODO: should we replace it?
		return (Cls) frame;
	}


	protected Slot getSlot(String name) {
		return getSlot(getFrame(name));
	}

	protected Slot getSlot(Frame frame) {
		if (frame == null) {
			return (Slot) frame;
		}
		if (frame instanceof Slot) {
			return (Slot) frame;
		}

		//frame is not a slot
		if (log.isLoggable(Level.FINE)) {
			log.fine("    Frame with wrong Java type: " + frame.getName() +
					". Expected: RDFProperty (or subclass), got: " + frame.getClass().getName());
		}

		try {
			Frame newFrame = new DefaultRDFProperty(frame.getKnowledgeBase(), new FrameID(frame.getName()));
			swizzleFrame(frame, newFrame);
			frame = newFrame;
		} catch (Exception e) {
			log.log(Level.WARNING, "Error at changing Java type of: " + frame + " to DefaultRDFProperty", e);
		}

		globalParserCache.getFramesWithWrongJavaType().add(frame.getName());
		return (Slot) frame;
	}
	
	protected Collection getTypes(Frame frame) {
		Collection types = simpleFrameStore.getDirectOwnSlotValues(frame, 
																	owlModel.getSystemFrames().getDirectTypesSlot());
		if (types == null) {
			return Collections.emptyList();
		}
		return types;
	}


	private void swizzleFrame(Frame oldFrame, Frame newFrame) {
		newFrame.setIncluded(oldFrame.isIncluded());
		newFrame.setEditable(oldFrame.isEditable());
		mnfs.replaceFrame(newFrame);  // replace the frames in all NFS-s
		simpleFrameStore.reinitialize(); // flush all caches
	}


}
