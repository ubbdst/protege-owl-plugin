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

package edu.stanford.smi.protegex.owl.ui.individuals;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.ServerProject;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.server.framestore.background.CacheRequestReason;
import edu.stanford.smi.protege.server.framestore.background.FrameCalculator;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.FrameWithBrowserTextComparator;
import edu.stanford.smi.protege.util.GetOwnSlotValuesBrowserTextJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;
import edu.stanford.smi.protegex.owl.util.SubjectPredicateStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 * Modified by Hemed Al Ruwehy.
 *
 * 21-11-2017
 */
public class OWLGetOwnSlotValuesBrowserTextJob extends GetOwnSlotValuesBrowserTextJob {

	private static final long serialVersionUID = 5135524417428952393L;
	private Frame frame;
	private Slot slot;
	private static long DEFAULT_LIMIT = 2;

	public OWLGetOwnSlotValuesBrowserTextJob(KnowledgeBase kb, Frame frame, Slot slot, boolean directValues) {
		super(kb, frame, slot, directValues);
		this.frame = frame;
		this.slot = slot;
	}

	@Override
	protected Collection getValues() {
		if (frame instanceof RDFResource && slot instanceof RDFProperty) {
                Collection values = ((RDFResource) frame).getHasValuesOnTypes((RDFProperty) slot);
                //Collection properties =  ((RDFResource)frame).getPropertyValues((RDFProperty)slot);
                Collection properties = InstanceUtil.getPropertyValues((RDFResource)frame, (RDFProperty)slot, false);
                //System.out.println("Get values is called for " + frame.getName() + " on " + slot.getName() + " with size " + properties.size());
                int listSize = properties.size();

                //If maximum limit is exceeded
                if(listSize > DEFAULT_LIMIT) {
					int count = 0;

					if(!SubjectPredicateStore.exists((RDFResource)frame, (RDFProperty)slot)) {
                        showMessageDialog("Too many objects to show in the slot \"" + slot.getBrowserText() + "\"" +
                                " of instance \"" + frame.getBrowserText() + "\". " + "Currently showing only " + DEFAULT_LIMIT + " out of " + listSize);
                    }
                    //Add to the store
					SubjectPredicateStore.add((RDFResource)frame, (RDFProperty)slot);

					for (Object o : properties) {
						values.add(o);
					    count ++;
					    if(count == DEFAULT_LIMIT) {
					    	return values;
						}

					}
				}
                values.addAll(properties);
                return values;
            }
		else {
			return super.getValues();
		}
	}


	//Original
	/*@Override
	protected Collection getValues() {
	    if (frame instanceof RDFResource && slot instanceof RDFProperty) {
			Collection values = ((RDFResource)frame).getHasValuesOnTypes((RDFProperty)slot);
			values.addAll(super.getValues());
			return values;
		} else {
			return super.getValues();
		}
	}*/


	@Override
	public Collection<FrameWithBrowserText> execute() throws ProtegeException {
		return super.execute();
	}


   @Override
   @SuppressWarnings("unchecked")
	public Collection<FrameWithBrowserText> run() throws ProtegeException {
		ArrayList var1 = new ArrayList();
		this.addRequestsToFrameCalculator(this.frame);
		Collection values = this.getValues();

	   for (Object var4 : values) {
		   if (var4 instanceof Frame) {
			   Frame var5 = (Frame) var4;
			   var1.add(new FrameWithBrowserText(var5, var5.getBrowserText(), ((Instance) var5).getDirectTypes()));
			   this.addRequestsToFrameCalculator(var5);
		   } else {
			   var1.add(new FrameWithBrowserText(null, var4.toString(), null));
		   }
	   }
		Collections.sort(var1, new FrameWithBrowserTextComparator());
		return var1;
	}


	private void addRequestsToFrameCalculator(Frame var1) {
		if (this.getKnowledgeBase().getProject().isMultiUserServer()) {
			Server var2 = Server.getInstance();
			RemoteSession var3 = ServerFrameStore.getCurrentSession();
			ServerProject var4 = var2.getServerProject(this.getKnowledgeBase().getProject());
			ServerFrameStore var5 = (ServerFrameStore)var4.getDomainKbFrameStore(var3);
			FrameCalculator var6 = var5.getFrameCalculator();
			var6.addRequest(var1, var3, CacheRequestReason.USER_REQUESTED_FRAME_VALUES);
		}
	}

	/**
	 * Display popup error window with a given message
	 */
	private void showMessageDialog(String msg) {
		//Display error message in a popup window
		ProtegeUI.getModalDialogFactory().showMessageDialog((OWLModel) getKnowledgeBase(), msg);
	}
}