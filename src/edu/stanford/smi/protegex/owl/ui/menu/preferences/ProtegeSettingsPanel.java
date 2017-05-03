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

package edu.stanford.smi.protegex.owl.ui.menu.preferences;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

/**
 * A JComponent that allows to specify Protege specific features in an OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeSettingsPanel extends JComponent {

    private OWLModel owlModel;

    private JCheckBox importMetaCBox;

    private JCheckBox userDatatypesCBox;

    public final static String USER_DEFINED_DATATYPES = "edu.stanford.smi.protegex.owl.userDefinedDatatypes";


    ProtegeSettingsPanel(final JenaOWLModel owlModel) {
        this.owlModel = owlModel;

        importMetaCBox = new JCheckBox("Import Protege metadata ontology");
        importMetaCBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (importMetaCBox.isSelected()) {
                    enableProtegeOntology();
                }
                else {
                    disableProtegeOntology();
                }
            }
        });
        if (owlModel.isProtegeMetaOntologyImported()) {
            importMetaCBox.setSelected(true);
            if (!isMetadataOntologyImportedDirectly(OWLUtil.getActiveOntology(owlModel))) {
                importMetaCBox.setEnabled(false);
            }
        }

        userDatatypesCBox = new JCheckBox("Support user-defined XML Schema datatypes (numeric ranges)");
        userDatatypesCBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUserDefinedDatatypesSupported(owlModel, userDatatypesCBox.isSelected());
                ProtegeUI.reloadUI(owlModel);
            }
        });
        userDatatypesCBox.setEnabled(importMetaCBox.isSelected());
        userDatatypesCBox.setSelected(ProtegeSettingsPanel.isUserDefinedDatatypesSupported(owlModel));

        setBorder(BorderFactory.createTitledBorder("Protege Features"));
        setLayout(new GridLayout(2, 1));
        add(importMetaCBox);
        add(userDatatypesCBox);
    }


    private void disableProtegeOntology() {
        if (OWLUtil.confirmSaveAndReload(owlModel.getProject())) {
            owlModel.getDefaultOWLOntology().removeImports(ProtegeNames.PROTEGE_OWL_ONTOLOGY);
            OWLUtil.saveAndReloadProject();
        }

        userDatatypesCBox.setSelected(false);
        userDatatypesCBox.setEnabled(false);
    }


    private void enableProtegeOntology() {
        if (!owlModel.isProtegeMetaOntologyImported()) {
            ImportHelper importHelper = new ImportHelper((JenaOWLModel) owlModel);
            try {
                URI uri = new URI(ProtegeNames.PROTEGE_OWL_ONTOLOGY);
                importHelper.addImport(uri);
                importHelper.importOntologies();
                owlModel.getNamespaceManager().setPrefix(ProtegeNames.PROTEGE_OWL_NAMESPACE, ProtegeNames.PROTEGE_PREFIX);
                userDatatypesCBox.setEnabled(true);
            }
            catch (Exception e) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel, e.getMessage());
            }
        }
    }


    public static boolean isUserDefinedDatatypesSupported(OWLModel owlModel) {
        return Boolean.TRUE.equals(owlModel.getOWLProject().getSettingsMap().getBoolean(USER_DEFINED_DATATYPES));
    }


    public static void setUserDefinedDatatypesSupported(OWLModel owlModel, boolean value) {
        owlModel.getOWLProject().getSettingsMap().setBoolean(USER_DEFINED_DATATYPES, Boolean.valueOf(value));
    }


    private boolean isMetadataOntologyImportedDirectly(OWLOntology ont) {
        return ont.getImportResources().contains(getMetadataOnt());
    }


    private RDFResource getMetadataOnt() {
        RDFResource result = null;
        try {
            URI metaURI = new URI(ProtegeNames.PROTEGE_OWL_ONTOLOGY);
            result = owlModel.getOWLOntologyByURI(metaURI);
        }
        catch (URISyntaxException e) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        return result;
    }
}
