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

package edu.stanford.smi.protegex.owl.ui.cls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JFileChooser;

import com.hp.hpl.jena.ontology.OntModel;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaDLConverter;
import edu.stanford.smi.protegex.owl.jena.creator.JenaCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;

/**
 * Currently only working in database mode!
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExtractOntologyAction extends ResourceAction {
    private static transient final Logger log = Log.getLogger(ExtractOntologyAction.class);
    private JFileChooser fileChooser;


    public ExtractOntologyAction() {
        super("Extract (sub) ontology to OWL file...", Icons.getBlankIcon(),
                ExtractTaxonomyAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {

        int result = ProtegeUI.getModalDialogFactory().showConfirmCancelDialog(getResource().getOWLModel(),
                "Shall the OWL File be converted to OWL DL if needed?",
                getValue(Action.NAME).toString());

        if (result == ModalDialogFactory.OPTION_CANCEL) {
            return;
        }

        Collection clses = null;
        RDFSNamedClass aClass = (RDFSNamedClass) getResource();
        if (!getResource().equals(getResource().getOWLModel().getOWLThingClass())) {
            clses = new HashSet();
            for (Iterator it = aClass.getSubclasses(true).iterator(); it.hasNext();) {
                Object c = it.next();
                if (c instanceof OWLNamedClass && ((OWLNamedClass) c).isEditable()) {
                    clses.add(c);
                }
            }
            clses.add(aClass);
        }

        final OWLModel owlModel = aClass.getOWLModel();
        JenaCreator creator = new JenaCreator(owlModel, false, clses,
                new ModalProgressBarManager("Preparing Ontology"));
        OntModel ontModel = creator.createOntModel();
        if (result == ModalDialogFactory.OPTION_OK) {
            log.info("Running JenaDLConverter...");
            JenaDLConverter c = new JenaDLConverter(ontModel, owlModel.getNamespaceManager());
            ontModel = c.convertOntModel();
        }

        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
        }
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            Jena.saveOntModel(owlModel, file, ontModel, "The (sub) ontology has been extracted to\n" + file);
        }
    }


    @Override
    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof OWLSubclassPane &&
                resource instanceof RDFSNamedClass &&
                resource.getOWLModel() instanceof OWLDatabaseModel;
    }
}
