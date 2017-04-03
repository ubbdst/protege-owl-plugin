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

package edu.stanford.smi.protegex.owl.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.BrowserTextListFinder;
import edu.stanford.smi.protege.ui.Finder;
import edu.stanford.smi.protege.ui.FrameWithBrowserTextRenderer;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.FrameWithBrowserTextComparator;
import edu.stanford.smi.protege.util.GetInstancesAndBrowserTextJob;
import edu.stanford.smi.protege.util.StringUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class SelectResourcesWithBrowserTextPanel extends SelectResourcesPanel {

	private static final long serialVersionUID = -844734066823481870L;
	
	public SelectResourcesWithBrowserTextPanel(OWLModel owlModel,
			Collection classes, boolean allowsMultipleSelection) {
		super(owlModel, classes, allowsMultipleSelection);	
	}

	@Override
	protected JComboBox createDirectAllInstanceComboBox() {	
		return super.createDirectAllInstanceComboBox();
	}
	
	@Override
	protected JComponent createInstanceList() {	
		JComponent instList = super.createInstanceList();
		((JList)instList).setCellRenderer(new FrameWithBrowserTextRenderer() {
			@Override
			public void setMainText(String text) {			
				super.setMainText(StringUtilities.unquote(text));
			}
		});
		return instList;
	}
	
	@Override
	protected Finder createListFinder() {	
		return new BrowserTextListFinder(_instanceList, ResourceKey.INSTANCE_SEARCH_FOR);
	}
	
	@Override
	protected Collection getInstances(Cls cls, boolean direct) {
		GetInstancesAndBrowserTextJob job = new GetInstancesAndBrowserTextJob(cls.getKnowledgeBase(),
				CollectionUtilities.createCollection(cls), direct);
		return job.execute();
	}
	
	
	@Override
	protected Comparator getInstancesComparator() {
		return new FrameWithBrowserTextComparator();
	}

	@Override
	protected Collection getInstanceSelection() {
		List<Frame> selectedFrames = new ArrayList<Frame>();
		Collection sel = super.getInstanceSelection();
		for (Iterator iterator = sel.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			if (object instanceof FrameWithBrowserText) {
				selectedFrames.add(((FrameWithBrowserText)object).getFrame());
			}
		}
		return selectedFrames;
	}
	
}
