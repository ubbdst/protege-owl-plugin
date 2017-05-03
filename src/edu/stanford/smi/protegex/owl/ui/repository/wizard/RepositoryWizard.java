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

package edu.stanford.smi.protegex.owl.ui.repository.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.JComponent;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.DatabaseRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.HTTPRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.LocalFileRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.LocalFolderRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.RelativeFileRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.impl.RelativeFolderRepositoryCreatorWizardPlugin;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizard;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryWizard extends OWLWizard {

    private ArrayList<RepositoryCreatorWizardPlugin> plugins;

    private RepositoryCreatorWizardPlugin selectedPlugin;

	private PluginPanelHolder pluginPanelHolder;

    public RepositoryWizard(JComponent component, OWLModel owlModel) {
        super(component, "Create Ontology Repository");
        plugins = new ArrayList<RepositoryCreatorWizardPlugin>();
        plugins.add(new LocalFolderRepositoryCreatorWizardPlugin());        
        plugins.add(new RelativeFolderRepositoryCreatorWizardPlugin());
        plugins.add(new LocalFileRepositoryCreatorWizardPlugin());
        plugins.add(new RelativeFileRepositoryCreatorWizardPlugin());
        plugins.add(new HTTPRepositoryCreatorWizardPlugin());   
        plugins.add(new DatabaseRepositoryCreatorWizardPlugin());
        Collection<Class> pluginClses = PluginUtilities.getClassesWithAttribute(RepositoryCreatorWizardPlugin.PLUGIN_TYPE,
                "True");
        for (Iterator<Class> it = pluginClses.iterator(); it.hasNext();) {
            Class cls = it.next();
            try {
                plugins.add((RepositoryCreatorWizardPlugin) cls.newInstance());
            }
            catch (InstantiationException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
            catch (IllegalAccessException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
        }
        addPage(new SelectRepositoryTypeWizardPage(this, owlModel));
	    addPage(pluginPanelHolder = new PluginPanelHolder(this, owlModel));
    }

    public Repository getRepository() {
        return pluginPanelHolder.getRepository();
    }

    public ArrayList<RepositoryCreatorWizardPlugin> getPlugins() {
        return plugins;
    }

    public void setSelectedPlugin(RepositoryCreatorWizardPlugin selectedPlugin) {
        this.selectedPlugin = selectedPlugin;
    }

	public RepositoryCreatorWizardPlugin getSelectedPlugin() {
		return selectedPlugin;
	}

	public static void main(String [] args) throws OntologyLoadException {
		OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
		RepositoryWizard wiz = new RepositoryWizard(null, owlModel);
		if(wiz.execute() == Wizard.RESULT_FINISH) {
			Repository rep = wiz.getRepository();
		}
	}
}

