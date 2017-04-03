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

package edu.stanford.smi.protegex.owl.ui.importstree;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImportsTreeNode extends LazyTreeNode {

	private FrameListener _frameListener;

	@SuppressWarnings("deprecation")
	public ImportsTreeNode(LazyTreeNode parent, RDFResource resource) {
		super(parent, resource);	
		resource.addFrameListener(getFrameListener());
	}


	protected LazyTreeNode createNode(Object o) {
		return new ImportsTreeNode(this, (RDFResource) o);
	}


	protected int getChildObjectCount() {
		RDFResource resource = getResource();
		if (resource instanceof OWLOntology) {
			OWLOntology ontology = (OWLOntology) resource;
			return ontology.getImports().size();
		}
		else {
			return 0;
		}
	}


	protected Collection getChildObjects() {
		RDFResource resource = getResource();
		if (resource instanceof OWLOntology) {
			OWLOntology ontology = (OWLOntology) resource;
			return ontology.getImportResources();
		}
		else {
			return Collections.EMPTY_LIST;
		}
	}


	protected Comparator getComparator() {
		return null;
	}


	private RDFResource getResource() {
		return (RDFResource) getUserObject();
	}


	protected FrameListener getFrameListener() {
		if (_frameListener == null) {
			_frameListener = new FrameAdapter() {
			
			    // ToDo This code doesn't belong here.
				@Override
				public void frameReplaced(FrameEvent event) {
					Frame oldFrame = event.getFrame();
					Frame newFrame = event.getNewFrame();
					RDFResource resource = getResource();    	    		
					if (resource != null && resource.equals(oldFrame)) {	
					    
						OWLModel model = resource.getOWLModel();
                        OWLUtil.synchronizeTripleStoreAfterOntologyRename(model, oldFrame.getName(), (OWLOntology) newFrame);
						model.getTripleStoreModel().getTopTripleStore().setOriginalXMLBase(newFrame.getName());
						reload(newFrame);
					}
				}
			};
		}

		return _frameListener;
	}


	@SuppressWarnings("deprecation")
	protected void dispose() {
		super.dispose();
		if (getResource() != null) {
			getResource().removeFrameListener(_frameListener);
		}
	}

}
