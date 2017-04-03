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


package edu.stanford.smi.protegex.owl.swrl.ui.table;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanel;

import javax.swing.*;
import java.awt.*;

public class SWRLResultsPanel extends ResultsPanel
{
	private RDFResource instance;
	private SWRLTablePanel tablePanel;

	private ModelListener listener = new ModelAdapter() {
		public void classDeleted(RDFSClass cls)
		{
			if (instance.equals(cls)) {
				closeSoon();
			}
		}

		public void individualDeleted(RDFResource resource)
		{
			if (instance.equals(resource)) {
				closeSoon();
			}
		}

		public void propertyDeleted(RDFProperty property)
		{
			if (instance.equals(property)) {
				closeSoon();
			}
		}
	};

	public SWRLResultsPanel(RDFResource resource)
	{
		super(resource.getOWLModel());
		this.instance = resource;
		OWLModel owlModel = resource.getOWLModel();
		owlModel.addModelListener(listener);
		tablePanel = new SWRLTablePanel(owlModel, resource);
		add(BorderLayout.CENTER, tablePanel);
	}

	private void closeSoon()
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				close();
			}
		});
	}

	public void dispose()
	{
		tablePanel.dispose();
		OWLModel owlModel = instance.getOWLModel();
		owlModel.removeModelListener(listener);
	}

	public String getTabName()
	{
		return "SWRL Rules about " + instance.getBrowserText();
	}
}
