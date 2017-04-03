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

package edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryWizard;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TreeSet;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 1, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryImportPage extends AbstractImportStartWizardPage {

    private JList list;

    private Action addRepositoryAction;


    public RepositoryImportPage(ImportWizard wizard) {
        super("Repository Import Page", wizard);
        createUI();
    }


    private void createUI() {
	    setHelpText("Importing an ontology from a repository", HELP_TEXT);
        list = new JList();
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                setPageComplete(validateData());
                updateNextPage();
            }
        });
        refreshList();
        LabeledComponent lc = new LabeledComponent("Select an ontology to import", new JScrollPane(list));
        JPanel holder = new JPanel(new BorderLayout(3, 3));
        holder.add(lc, BorderLayout.NORTH);
        addRepositoryAction = new AbstractAction("Add repository...") {
            public void actionPerformed(ActionEvent e) {
                addRepository();
            }
        };
        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonHolder.add(new JButton(addRepositoryAction), BorderLayout.SOUTH);
        holder.add(buttonHolder, BorderLayout.SOUTH);
        getContentComponent().add(holder, BorderLayout.NORTH);
        setPageComplete(validateData());
    }


	public void nextPressed() {
		Object [] selValues = list.getSelectedValues();
		for(int i = 0; i < selValues.length; i++) {
			URI curURI = (URI) selValues[i];
			Repository rep = getImportWizard().getOWLModel().getRepositoryManager().getRepository(curURI);
			getImportWizard().getImportData().addImportEntry(new RepositoryImportEntry(curURI, rep));
		}
	}


    private boolean validateData() {
        Object selVal = list.getSelectedValue();
        if (selVal != null) {
            try {
                URI uri = new URI(selVal.toString());
                //((ImportWizard) getWizard()).setOntologyURI(uri);
                return true;
            }
            catch (URISyntaxException e) {
                return false;
            }
        }
        else {
            return false;
        }
    }


    private void addRepository() {
        JComponent c = null;
        if (SwingUtilities.getRoot(this) instanceof JFrame) {
            c = this;
        }
        OWLModel owlModel = ((ImportWizard) getWizard()).getOWLModel();
        RepositoryWizard wizard = new RepositoryWizard(c, owlModel);
        if (wizard.execute() == Wizard.RESULT_FINISH) {
            Repository rep = wizard.getRepository();
            if (rep != null) {
                owlModel.getRepositoryManager().addProjectRepository(0, rep);
                refreshList();
            }
        }
    }


    public WizardPage getNextPage() {
        return new ImportVerificationPage(getImportWizard());
    }


    private void refreshList() {
        OWLModel owlModel = ((ImportWizard) getWizard()).getOWLModel();
        Collection availOntologies = new LinkedHashSet();
        for (Iterator it = owlModel.getRepositoryManager().getProjectRepositories().iterator(); it.hasNext();) {
            Repository rep = (Repository) it.next();
            availOntologies.addAll(new TreeSet(rep.getOntologies()));
        }
        for (Iterator it = owlModel.getRepositoryManager().getGlobalRepositories().iterator(); it.hasNext();) {
            Repository rep = (Repository) it.next();
            availOntologies.addAll(new TreeSet(rep.getOntologies()));
        }
        Collection importedOntologies = owlModel.getAllImports();
        for (Iterator it = availOntologies.iterator(); it.hasNext();) {
            URI uri = (URI) it.next();
            if (importedOntologies.contains(uri.toString())) {
                it.remove();
            }
        }
	    try {
		    availOntologies.remove(new URI(owlModel.getDefaultOWLOntology().getURI()));
	    }
	    catch(URISyntaxException e) {
		    System.err.print(e.getMessage());
	    }
	    list.setListData(availOntologies.toArray());
    }


    private static final String HELP_TEXT = "<p>Please select the ontology to be imported.  If " +
            "the list above does not contain the ontology, press " +
            "the 'Add repository...' button to specify a repository " +
            "that contains the ontology.</p>";

}

