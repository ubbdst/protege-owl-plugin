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


package edu.stanford.smi.protegex.owl.swrl.ui.tab;

import java.awt.BorderLayout;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.ui.SWRLProjectPlugin;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.swrl.ui.table.SWRLTablePanel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

/**
 * A Protege-OWL tab widget that contains the SWRLTab GUI. This tab serves as the entry point to all of the GUI-based software components that work with SWRL in
 * Protege-OWL.
 * <p>
 * Documentation is available <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTab">here</a>.
 */
public class SWRLTab extends AbstractTabWidget
{
	private SWRLTablePanel panel = null;

	public void initialize()
	{
		setLabel("SWRL Rules");
		setIcon(SWRLIcons.getImpsIcon());

		activateSWRL();
		this.panel = new SWRLTablePanel((OWLModel)getKnowledgeBase(), null, this);

		add(this.panel);
	}

	private void activateSWRL()
	{
		OWLModel owlModel = (OWLModel)getKnowledgeBase();

		try {
			if (owlModel.getProject().isMultiUserClient())
				return;

			owlModel.getNamespaceManager().setPrefix(new URI(SWRLNames.SWRLA_NAMESPACE), SWRLNames.SWRLA_PREFIX);
			owlModel.getNamespaceManager().setPrefix(new URI(SWRLNames.SQWRL_NAMESPACE), SWRLNames.SQWRL_PREFIX);

			ImportHelper importHelper = new ImportHelper(owlModel);
			boolean importsAdded = false;

			if (!ApplicationProperties.getBooleanProperty(SWRLNames.EXCLUDE_STANDARD_IMPORTS, false)) {
				importsAdded |= addImport(owlModel, SWRLNames.SWRLA_IMPORT, importHelper);
				importsAdded |= addImport(owlModel, SWRLNames.SQWRL_IMPORT, importHelper);

				importHelper.importOntologies(false);
			}

			// Make ":TO" and ":FROM" visible for dynamic expansion.
			owlModel.getSystemFrames().getToSlot().setVisible(true);
			owlModel.getSystemFrames().getFromSlot().setVisible(true);
			SWRLProjectPlugin.setSWRLClassesAndPropertiesVisible(getProject(), false);
			SWRLProjectPlugin.adjustWidgets(getProject());

			if (importsAdded) {
				ProjectView prjView = ProjectManager.getProjectManager().getCurrentProjectView();
				if (prjView != null)
					prjView.reloadAllTabsExcept(this);
			}

		} catch (Exception ex) {
			ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
					"Could not activate SWRLTab: " + ex + "\n. Your project might be in an inconsistent state now.");
			Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
		}
	}

	private boolean addImport(OWLModel owlModel, String importUri, ImportHelper importHelper) throws URISyntaxException
	{
		if (owlModel.getTripleStoreModel().getTripleStore(importUri) == null) {
			importHelper.addImport(new URI(importUri));
			return true;
		}
		return false;
	}

	public void reconfigure()
	{
		if (this.panel != null) {
			remove(this.panel);
			setLayout(new BorderLayout());
			add(this.panel);
		}
	}

	public static boolean isSuitable(Project p, Collection errors)
	{
		if (p.getKnowledgeBase() instanceof OWLModel) {
			return true;
		} else {
			errors.add("This tab can only be used with OWL projects.");
			return false;
		}
	}
}
