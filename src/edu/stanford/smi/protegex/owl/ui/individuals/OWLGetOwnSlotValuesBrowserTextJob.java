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
import edu.stanford.smi.protege.util.GetOwnSlotValuesBrowserTextJob;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Modified by Hemed Al Ruwehy.
 *
 * 21-11-2017
 */
public class OWLGetOwnSlotValuesBrowserTextJob extends GetOwnSlotValuesBrowserTextJob {

	private static final long serialVersionUID = 5135524417428952393L;
	private Frame frame;
	private Slot slot;
	private boolean directValues = false;
	private int limitCall = 0;

	public OWLGetOwnSlotValuesBrowserTextJob(KnowledgeBase kb, Frame frame, Slot slot, boolean directValues) {
		super(kb, frame, slot, directValues);
		this.frame = frame;
		this.slot = slot;
		this.directValues = directValues;
	}

	@Override
	protected Collection getValues() {
		if (frame instanceof RDFResource && slot instanceof RDFProperty) {

			System.out.println("Get values is called");
			//int totalVal = ((RDFResource)frame).getPropertyValueCount((RDFProperty)slot);

			/*if(totalVal > 1000) {
				return Collections.emptyList();
			}*/

			//Collection values = ((RDFResource)frame).getHasValuesOnTypes((RDFProperty)slot);
			//Collection properties =  ((RDFResource)frame).getPropertyValues((RDFProperty)slot);
			Collection properties = InstanceUtil.getPropertyValues((RDFResource)frame, (RDFProperty)slot, false);
			//values.addAll(properties);
			//return values;
			return properties;
		} else {
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
	public Collection<FrameWithBrowserText> run() throws ProtegeException {
		int count = 0;
		int limit = 1000;
		ArrayList var1 = new ArrayList();
		this.addRequestsToFrameCalculator(this.frame);
		Collection var2 = this.getValues();

	   for (Object var4 : var2) {
		   if (var4 instanceof Frame) {
			   Frame var5 = (Frame) var4;
			   var1.add(new FrameWithBrowserText(var5, var5.getBrowserText(), ((Instance) var5).getDirectTypes()));
			   this.addRequestsToFrameCalculator(var5);
		   } else {
			   var1.add(new FrameWithBrowserText(null, var4.toString(), null));
		   }
		   count++;

		   /*if (count == limit) {
		   	Collections.sort(var1, new FrameWithBrowserTextComparator());
		   	return var1;
		   }*/
	   }
		//Collections.sort(var1, new FrameWithBrowserTextComparator());
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
}