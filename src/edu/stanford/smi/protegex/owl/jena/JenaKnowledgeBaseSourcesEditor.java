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

package edu.stanford.smi.protegex.owl.jena;

import edu.stanford.smi.protege.model.KnowledgeBaseSourcesEditor;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

/**
 * The KnowledgeBaseSourcesEditor for the JenaKnowledgeBaseFactory.
 * This simply provides an additional text field in which the name of the OWL file can be edited.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaKnowledgeBaseSourcesEditor extends KnowledgeBaseSourcesEditor {

    private static final long serialVersionUID = 3974294657555722203L;

    public static final String EXTENSION = "owl";

    private URIField owlFileURIField;

    private JComboBox languageComboBox;

    private boolean callback = false; // Avoids infite recursion exception


    public JenaKnowledgeBaseSourcesEditor(String projectURIString, final PropertyList sources) {

        super(projectURIString, sources);

        Object[] langs = new String[]{
                JenaKnowledgeBaseFactory.fileLanguages[0],
                JenaKnowledgeBaseFactory.fileLanguages[1]
        };
        languageComboBox = new JComboBox(langs);
        languageComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = (String) languageComboBox.getSelectedItem();
                JenaKnowledgeBaseFactory.setOWLFileLanguage(sources, value);
                String path = getProjectPath();
                updatePath(path);
            }
        });
        LabeledComponent languagePanel = new LabeledComponent("Language", languageComboBox);

        String name = JenaKnowledgeBaseFactory.getOWLFilePath(getSources());
        if (name == null) {
            String language = JenaKnowledgeBaseFactory.getOWLFileLanguage(sources);
            name = constructName(getProjectPath(), JenaKnowledgeBaseFactory.getExtension(language));
        }
        owlFileURIField = new URIField("OWL file name or URL",
                getURI(name), EXTENSION,
                "Web Ontology Language (OWL) files");
        owlFileURIField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // updateComboBox();
            }
        });
        updateComboBox();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(8, 8));
        panel.add(BorderLayout.NORTH, owlFileURIField);
        panel.add(BorderLayout.CENTER, languagePanel);
        add(panel);
    }


    private String constructName(String projectPath, String extension) {
        return FileUtilities.replaceExtension(projectPath, extension);
    }


    public JComponent createIncludedProjectsList() {
        return null;
    }


    public String getProjectPath() {
        String path = super.getProjectPath();

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


    protected void onProjectPathChange(String oldPath, String newPath) {
        if (newPath != null) {
            updatePath(newPath);
        }
    }


    public void saveContents() {
        final URI relativeURI = owlFileURIField.getRelativeURI();
        String owlFileURI = relativeURI != null ? relativeURI.toString() : "";
        String language = (String) languageComboBox.getSelectedItem();
        JenaKnowledgeBaseFactory.setOWLFileLanguage(getSources(), language);
        JenaKnowledgeBaseFactory.setOWLFileName(getSources(), owlFileURI);
    }


    private void updateComboBox() {
        URI fileURI = owlFileURIField.getAbsoluteURI();
        if (fileURI != null) {
            for (int i = 0; i < JenaKnowledgeBaseFactory.extensions.length; i++) {
                String extension = JenaKnowledgeBaseFactory.extensions[i];
                if (fileURI.toString().endsWith("." + extension)) {
                    callback = true;
                    languageComboBox.setSelectedIndex(i);
                    callback = false;
                }
            }
        }
    }


    private void updatePath(String newPath) {
        if (getProjectPath() != null && !callback) {
            String language = (String) languageComboBox.getSelectedItem();
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
            OWLUI.showErrorMessageDialog("You need to enter a valid URI for an OWL/RDF file.\n" +
                    "Currently, these URIs should not contain spaces.", "Invalid URI");
            return false;
        }
        else {
            return true;
        }
    }
}
