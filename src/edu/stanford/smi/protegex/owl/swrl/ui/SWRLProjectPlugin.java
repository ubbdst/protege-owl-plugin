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


package edu.stanford.smi.protegex.owl.swrl.ui;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.FormWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.swrl.ui.widget.SWRLRuleSlotWidget;
import edu.stanford.smi.protegex.owl.ui.metadata.NameDocumentationWidget;

/**
 * A Project Plugin that does some initialization after a SWRL project has been loaded. The idea is that SWRL support is activated iff the ontology imports the
 * SWRL namespace. This triggers the installation of a specific FrameFactory so that SWRL-specific API classes are used automatically.
 * 
 * @author Martin O'Connor <moconnor@smi.stanford.edu>
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class SWRLProjectPlugin extends ProjectPluginAdapter
{

	@Override
	public void afterLoad(Project p)
	{
		adjustGUI(p);
	}

	public static void adjustGUI(Project p)
	{
		if (!isSWRLPresent(p)) {
			return;
		}

		setSWRLClassesAndPropertiesVisible(p, false);
		adjustWidgets(p);
		addSWRLTab(p);
	}

	public static void setSWRLClassesAndPropertiesVisible(Project p, boolean visible)
	{
		OWLModel owlModel = (OWLModel)p.getKnowledgeBase();

		SWRLFactory swrlFactory = new SWRLFactory(owlModel);

		for (RDFSNamedClass swrlClass : swrlFactory.getSWRLClasses()) {
			swrlClass.setVisible(visible);
		}

		for (RDFProperty swrlProperty : swrlFactory.getSWRLProperties()) {
			swrlProperty.setVisible(visible);
		}

		for (RDFProperty swrlbProperty : swrlFactory.getSWRLBProperties()) {
			swrlbProperty.setVisible(visible);
		}

		owlModel.getRDFListClass().setVisible(visible);
	}

	@SuppressWarnings("deprecation")
	public static void adjustWidgets(Project project)
	{
		KnowledgeBase kb = project.getKnowledgeBase();

		Cls impCls = kb.getCls(SWRLNames.Cls.IMP);

		if (impCls == null)
			return;

		try {
			ClsWidget clsWidget = project.getDesignTimeClsWidget(impCls);

			Slot nameSlot = kb.getSlot(Model.Slot.NAME);
			// clsWidget.replaceWidget(nameSlot, RDFSNamedClassMetadataWidget.class.getName());
			clsWidget.replaceWidget(nameSlot, null);

			Slot inferredTypeSlot = kb.getSlot(ProtegeNames.Slot.INFERRED_TYPE);
			clsWidget.replaceWidget(inferredTypeSlot, null);

			Slot headSlot = kb.getSlot(SWRLNames.Slot.HEAD);
			clsWidget.replaceWidget(headSlot, null);

			Slot bodySlot = kb.getSlot(SWRLNames.Slot.BODY);
			clsWidget.replaceWidget(bodySlot, null);

			clsWidget.replaceWidget(nameSlot, NameDocumentationWidget.class.getName());
			clsWidget.replaceWidget(bodySlot, SWRLRuleSlotWidget.class.getName());

			((FormWidget)clsWidget).setVerticalStretcher(SWRLNames.Slot.BODY);
			((FormWidget)clsWidget).setHorizontalStretcher(FormWidget.STRETCH_ALL);
			((FormWidget)clsWidget).setModified(true);

			clsWidget.relayout();

		} catch (Exception e) {
			Log.getLogger().warning("Error at configuring SWRL forms: " + e.getMessage());
		}
	}

	private static void addSWRLTab(Project p)
	{
		WidgetDescriptor swrlTabDescriptor = p.getTabWidgetDescriptor(SWRLTab.class.getName());
		swrlTabDescriptor.setVisible(true);
	}

	public static boolean isSWRLPresent(Project project)
	{
		KnowledgeBase kb = project.getKnowledgeBase();

		if (!(kb instanceof OWLModel))
			return false;
		return isSWRLPresent((OWLModel)kb);
	}

	public static boolean isSWRLPresent(OWLModel owlModel)
	{
		Cls impCls = owlModel.getSystemFrames().getImpCls();
		return impCls.getInstanceCount() != 0;
	}

	/**
	 * @deprecated always returns true - swrl is included in the system frames.
	 * @param project
	 * @return true
	 */
	@Deprecated
	public static boolean isSWRLImported(Project project)
	{
		return true;
	}
}
