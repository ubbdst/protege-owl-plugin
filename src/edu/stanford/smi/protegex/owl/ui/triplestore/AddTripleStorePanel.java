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

package edu.stanford.smi.protegex.owl.ui.triplestore;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URI;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIField;
import edu.stanford.smi.protege.util.Validatable;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AddTripleStorePanel extends JPanel implements Validatable {

    private OWLModel owlModel;

    private JTextField prefixField;

    protected URIField uriField;


    protected AddTripleStorePanel(OWLModel owlModel) {
        this(owlModel, "Prefix");
    }


    protected AddTripleStorePanel(OWLModel owlModel, String prefixTitle) {
        this.owlModel = owlModel;
        try {
            URI uri = new URI("http://www.owl-ontologies.com/submodel.owl");
            uriField = new URIField("Ontology name (URI)", uri, ".owl", "");
            prefixField = new JTextField("submodel");
            JPanel mainPanel = new JPanel(new GridLayout(3, 1));
            mainPanel.add(uriField);
            mainPanel.add(new LabeledComponent(prefixTitle, prefixField));
            setLayout(new BorderLayout(0, 16));
            add(BorderLayout.CENTER, mainPanel);
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
    }


    protected String getNamespaceFromURI(URI uri) {
        String namespace = uri.toString();
        return Jena.getNamespaceFromURI(namespace);
    }


    protected OWLModel getOWLModel() {
        return owlModel;
    }


    protected String getPrefix() {
        return prefixField.getText();
    }


    protected URI getURI() {
        return uriField.getAbsoluteURI();
    }


    public void saveContents() {
    }


    protected void setPrefix(String prefix) {
        prefixField.setText(prefix);
    }


    public boolean validateContents() {
        URI uri = getURI();
        if (uri != null) {
            String uriString = uri.toString();
            TripleStoreModel tsm = owlModel.getTripleStoreModel();
            if (tsm.getTripleStore(uriString) == null) {
                String prefix = getPrefix();
                if (prefix != null) {
                    final String namespace = owlModel.getNamespaceManager().getNamespaceForPrefix(prefix);
                    final String nuri = getNamespaceFromURI(uri);
                    if (namespace == null || namespace.equals(nuri)) {
                        return true;
                    }
                    else {
                        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                                "Prefix \"" + prefix + "\" is already used.");
                    }
                }
                else {
                    ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                            "Invalid Prefix.");
                }
            }
            else {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                        "Model with this URI already exists:\n" + uriString);
            }
        }
        else {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "Invalid URI.");
        }
        return false;
    }
}
