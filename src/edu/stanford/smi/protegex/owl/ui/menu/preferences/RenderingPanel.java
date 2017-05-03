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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

public class RenderingPanel extends JPanel {
    private static final long serialVersionUID = -2021694698732430578L;
    public static String RENDERING_PANEL_TITLE = "Rendering";
    public static String DEFAULT_BROWSER_SLOT_PROP = "owl.default.browser.slot";
    public static String DEFAULT_LANGUATE_PROPERTY = "owl.default.language.slot";
    public static String[] META_SLOT_NAMES = {
        OWLNames.Cls.NAMED_CLASS, RDFSNames.Cls.NAMED_CLASS, RDFNames.Cls.PROPERTY, OWLNames.Cls.THING
    };
    private OWLModel owlModel;
    private JComboBox renderingPropertyBox;
    private JComboBox defaultRenderingPropertyBox;
    private JTextField defaultLanguageField;
    private boolean requiresReloadUI = false;
    
    public RenderingPanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    	setAlignmentX(Component.LEFT_ALIGNMENT);
    	setBorder(BorderFactory.createTitledBorder(RENDERING_PANEL_TITLE));
    	
        add(getLocalSettingsPanel());
        add(Box.createVerticalStrut(10));        
    	add(getGlobalSettingsPanel());
    }
    
    public boolean getRequiresReloadUI() {
        return requiresReloadUI;
    }
    
   
    private JComponent getLocalSettingsPanel() {
    	JPanel localSettingsPanel = new JPanel();
    	localSettingsPanel.setLayout(new BoxLayout(localSettingsPanel, BoxLayout.PAGE_AXIS));
    	localSettingsPanel.setBorder(BorderFactory.createTitledBorder("Local Settings"));
    	localSettingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    	localSettingsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
       	
    	renderingPropertyBox = makePropertyComboBox(OWLUI.getCommonBrowserSlot(owlModel));
    	renderingPropertyBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        renderingPropertyBox.addActionListener(new SetRendererActionListener());      

        localSettingsPanel.add(new LabeledComponent("Render entities in this ontology (" + getOntologyName() + ") using property: ", renderingPropertyBox));
        localSettingsPanel.setToolTipText("The local setting will apply only to the current ontology and it will be saved in the pprj file.");
        
    	return localSettingsPanel;
    }
    
    private JComponent getGlobalSettingsPanel() {
    	JPanel globalSettingsPanel = new JPanel();
    	globalSettingsPanel.setLayout(new BoxLayout(globalSettingsPanel, BoxLayout.PAGE_AXIS));
    	globalSettingsPanel.setBorder(BorderFactory.createTitledBorder("Global Settings"));
    	globalSettingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    	globalSettingsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 210));
    	
    	defaultRenderingPropertyBox = makePropertyComboBox(OWLUI.getDefaultBrowserSlot(owlModel));
    	defaultRenderingPropertyBox.setAlignmentX(Component.LEFT_ALIGNMENT);    	
        defaultRenderingPropertyBox.addActionListener(new SetDefaultRendererActionListener());     
        globalSettingsPanel.add(new LabeledComponent("Render entities in all OWL files using property:", defaultRenderingPropertyBox));        
      
        defaultLanguageField = makeDefaultLanguageField();
        defaultLanguageField.setAlignmentX(Component.LEFT_ALIGNMENT);
        defaultLanguageField.getDocument().addDocumentListener(new  SetLanguageDocumentListener());    	
        globalSettingsPanel.add(new LabeledComponent("Global default language for all OWL files (en, pt, de, ...):", defaultLanguageField));
        
        globalSettingsPanel.setToolTipText("<html>The global settings will apply to all ontologies open by an OWL file (and not to the ones open by a pprj file).<br>" +
        		"The global settings will be saved in the protege.properties file.<html>");
        
        globalSettingsPanel.add(Box.createVerticalStrut(10));
        globalSettingsPanel.add(getExplanationTextArea());       
        
    	return globalSettingsPanel;
    }
    
    private JComponent getExplanationTextArea() {
    	JEditorPane pane = new JEditorPane();
    	pane.setBorder(BorderFactory.createTitledBorder("Explanation"));
    	pane.setEditable(false);
    	pane.setOpaque(false);
		pane.setEditorKit(new HTMLEditorKit());		
		// add a CSS rule to force body tags to use the default label font
		// instead of the value in javax.swing.text.html.default.csss
		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument)pane.getDocument()).getStyleSheet().addRule(bodyRule);	

    	String defaultLangInFile = getDefaultLanguageSetInFile();    	
    	String text = "<html>" +
    			"The global settings will apply to all ontologies open by an OWL file (and not to the ones open by a pprj file).<br>" +
    			"The global language setting will overide the default language set in the ontology file.<br><br>" +
    			"The current language set in the ontology file is:<b>" + (defaultLangInFile == null ? "(none)" : defaultLangInFile) + "</b>.</html>";
    	
    	pane.setText(text);
    	
    	return new JScrollPane(pane);
    }
    
    
    @SuppressWarnings("deprecation")
    private JComboBox makePropertyComboBox(Slot defaultSlot) {
        TreeSet<RDFProperty> properties = new TreeSet(owlModel.getOWLAnnotationProperties());
        properties.remove(owlModel.getRDFSLabelProperty());
        Slot[] propertyArray = new Slot[properties.size() + 2];
        int counter = 0;
        propertyArray[counter++] = owlModel.getNameSlot();
        propertyArray[counter++] = owlModel.getRDFSLabelProperty();
        for (Slot p : properties)  {
            propertyArray[counter++] = p;
        }

        JComboBox combo = new JComboBox(propertyArray);
        combo.setRenderer(new ComboBoxRenderer());
        if (defaultSlot != null && 
                (properties.contains(defaultSlot) ||
                        defaultSlot.equals(owlModel.getNameSlot()) ||
                        defaultSlot.equals(owlModel.getRDFSLabelProperty()))) {
            combo.setSelectedItem(defaultSlot);
        }
        else {
            combo.setSelectedIndex(-1);
        }
        return combo;
    }
    
    private JTextField makeDefaultLanguageField() {
        JTextField langField = new JTextField();
        String lang = ApplicationProperties.getString(DEFAULT_LANGUATE_PROPERTY);
        if (lang != null) {
            langField.setText(lang);
        }
        return langField;
    }
    
    private String getOntologyName() {
    	OWLOntology displayedOntology =  OWLUtil.getActiveOntology(owlModel);
        return NamespaceUtil.getLocalName(displayedOntology.getName());
    }
    
    private String getDefaultLanguageSetInFile() {
    	 String defaultLanguage = null;
         RDFProperty defaultLangProp = owlModel.getRDFProperty(ProtegeNames.getDefaultLanguageSlotName());
         if (defaultLangProp != null) {
             OWLOntology oi = owlModel.getDefaultOWLOntology();
             if (oi != null) {
                 Object value = oi.getPropertyValue(defaultLangProp);
                 if (value instanceof String) {
                 	String stringValue = (String) value;
                 	if (stringValue != null && stringValue.length() > 0) {
                 		defaultLanguage = stringValue;
                 	}
                 }
             }
         }
         return defaultLanguage;
    }
    
    /* ****************************************************************
     * Inner Classes
     */
    private class ComboBoxRenderer extends ResourceRenderer {
        private static final long serialVersionUID = -964343376627678218L;

        @Override
        protected void loadSlot(Slot slot) {
            if (slot.equals(owlModel.getSystemFrames().getNameSlot())) {
                addText("rdf:id");
            }
            else {
                super.loadSlot(slot);
            }
        }  
    }
    
    private class SetRendererActionListener implements ActionListener {
        @SuppressWarnings("deprecation")
        public void actionPerformed(ActionEvent e) {
            Slot slot = (Slot) renderingPropertyBox.getSelectedItem();
            OWLUI.setCommonBrowserSlot(owlModel, slot);
            requiresReloadUI = true;
        }
    }
    
    private class SetDefaultRendererActionListener implements ActionListener  {
        public void actionPerformed(ActionEvent e) {
            Slot slot = (Slot) defaultRenderingPropertyBox.getSelectedItem();
            OWLUI.setDefaultBrowserSlot(owlModel, slot);
        }
    }

    private class SetLanguageDocumentListener implements DocumentListener {

        public void changedUpdate(DocumentEvent e) {
            saveContents();
        }

        public void insertUpdate(DocumentEvent e) {
            saveContents();
        }

        public void removeUpdate(DocumentEvent e) {
            saveContents();
        }
        
        private void saveContents() {
            String lang = defaultLanguageField.getText();
            if (lang != null) {lang = lang.trim();}
            ApplicationProperties.setString(DEFAULT_LANGUATE_PROPERTY, lang == null || lang.equals("") ? null : lang);
        }
    }
}
