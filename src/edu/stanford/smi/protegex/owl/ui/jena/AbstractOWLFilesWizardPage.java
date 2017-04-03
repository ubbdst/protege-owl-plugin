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

package edu.stanford.smi.protegex.owl.ui.jena;

import com.hp.hpl.jena.util.FileUtils;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.OWLFilesPlugin;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.net.URI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLFilesWizardPage extends WizardPage {

    private boolean callback = false; // Avoids infite recursion exception

    public static final String EXTENSION = "owl";

    private URIField owlFileURIField;

    private OWLFilesPlugin plugin;

    private static String HELP_TEXT = "<P>Please specify either a local file or the URL of an online " +
            "ontology which you want to load.  You can search for a <B>file</B> using the Browse button in the upper right " +
            "corner.  For <B>online ontologies</B>, you could copy and paste the URL from an internet browser.</P>";


    public AbstractOWLFilesWizardPage(Wizard wizard, OWLFilesPlugin aPlugin, String name) {
        super(name, wizard);

        this.plugin = aPlugin;

        owlFileURIField = new URIField("OWL file name or URL",
                null, EXTENSION,
                "Web Ontology Language (OWL) files");
        owlFileURIField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                updateSetPageComplete();
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(8, 8));
        panel.add(BorderLayout.NORTH, owlFileURIField);
        panel.add(BorderLayout.SOUTH, OWLUI.createHelpPanel(HELP_TEXT,
                "How to load an OWL Ontology?", OWLUI.WIZARD_HELP_HEIGHT));

        add(panel);

        updateSetPageComplete();
    }


    protected OWLFilesPlugin getPlugin() {
        return plugin;
    }


    public String getProjectPath() {
        String path = ""; // super.getProjectPath();

        if (path == null) {
            if (owlFileURIField != null) {
                URI absoluteURI = owlFileURIField.getAbsoluteURI();
                if (absoluteURI != null) {
                    path = absoluteURI.toString();
                    path = FileUtilities.replaceExtension(path, ".pprj");
                }
                else {
                    path = "";
                }
            }
            else {
                path = "";
            }
        }
        return path;
    }


    private URI getURI(String str) {
        try {
            return new URI(str);
        }
        catch (Exception ex) {
            return URIUtilities.createURI(str);
        }
    }


    public void onFinish() {
        plugin.setLanguage(FileUtils.langXMLAbbrev);

        final URI uri = owlFileURIField.getAbsoluteURI();
        String owlFileURI = uri != null ? uri.toString() : "";
        plugin.setFile(owlFileURI);
    }


    protected void onProjectPathChange(String oldPath, String newPath) {
        if (newPath != null) {
            updatePath(newPath);
        }
    }


    private void updatePath(String newPath) {
        if (getProjectPath() != null && !callback) {
            String language = FileUtils.langXMLAbbrev;
            String ext = "." + JenaKnowledgeBaseFactory.getExtension(language);
            int index = newPath.lastIndexOf('/');
            if (index >= 0) {
                newPath = newPath.substring(index + 1);
            }
            String name = new File(newPath).getName();
            String fieldText = FileUtilities.replaceExtension(name, ext);
            owlFileURIField.setURI(getURI(fieldText));
        }
    }


    public boolean validateContents() {
        if (owlFileURIField.getRelativeURI() == null) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(this,
                    "You need to enter a valid URI for an OWL/RDF file.\n" +
                    "Currently, these URIs should not contain spaces.");
            return false;
        }
        else {
            return true;
        }
    }


    private void updateSetPageComplete() {
        setPageComplete(owlFileURIField.getRelativeURI() != null);
    }
}
