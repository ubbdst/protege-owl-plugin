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

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.FileField;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaFilePanel extends JPanel {

    private static final long serialVersionUID = -3806090237244175900L;
    private FileField owlFileField;
    private JCheckBox useNativeWriterChechBox;


    public JenaFilePanel() {
        owlFileField = new FileField("OWL file name",
                                     null, JenaKnowledgeBaseSourcesEditor.EXTENSION,
                                     "Web Ontology Language (OWL) files");
        
        useNativeWriterChechBox = ComponentFactory.createCheckBox("Use Protege native writer");
        
        setLayout(new BorderLayout(8, 8));
        add(BorderLayout.NORTH, owlFileField);
        add(BorderLayout.SOUTH, useNativeWriterChechBox);
    }


    public String getOWLFilePath() {
        String path = owlFileField.getPath();

        // make sure the filename has an extension
        File file = new File(path);
        String filename = file.getName();
        int extIndex = filename.indexOf('.');
        if (extIndex < 0){
            path = owlFileField.getPath() + "." + JenaKnowledgeBaseSourcesEditor.EXTENSION;
        }
        return path;
    }
    
    public boolean getUseNativeWriter() {
    	return useNativeWriterChechBox.isSelected();
    }
    
    public JCheckBox getUseNativeWriterCheckBox() {
    	return useNativeWriterChechBox;
    }
    
}
