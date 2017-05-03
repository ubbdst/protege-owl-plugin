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

/**
 * 
 */
package edu.stanford.smi.protegex.owl.ui.repository.wizard.impl;

import java.awt.GridLayout;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.stanford.smi.protege.storage.database.AbstractDatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseProperty;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.DatabaseRepository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryCreatorWizardPanel;

public class DatabaseWizardPanel extends RepositoryCreatorWizardPanel {
    private static final long serialVersionUID = 8313995336416582467L;
    
    private static Logger log = Log.getLogger(DatabaseWizardPanel.class);
    
    private EnumMap<DatabaseProperty, JTextField> textMap 
            = new EnumMap<DatabaseProperty, JTextField>(DatabaseProperty.class);
    private JComboBox tablesBox;
    public String ANY_TABLE_ENTRY = "--All Tables--";
    private Map<String, String> descriptionToTableMap = new HashMap<String, String>();

    private WizardPage wizardPage;
    
    public DatabaseWizardPanel(WizardPage wizardPage,
                       OWLModel owlModel) {
        TextDocumentListener listener = new TextDocumentListener();
        this.wizardPage = wizardPage;    
        setLayout(new GridLayout(DatabaseRepository.DATABASE_FIELDS.length + 1, 1));
        for (DatabaseProperty field : DatabaseRepository.DATABASE_FIELDS) {
            JTextField text;
            if (field == DatabaseProperty.PASSWORD_PROPERTY) {
                text = ComponentFactory.createPasswordField();
            }
            else {
                text = ComponentFactory.createTextField();
            }
            text.setText(DatabaseProperty.getProperty(field));
            textMap.put(field, text);
            LabeledComponent component = new LabeledComponent(field.getTitle(), text);
            text.getDocument().addDocumentListener(listener);
            add(component);
        }
        tablesBox = new JComboBox();
        LabeledComponent tcLb = new LabeledComponent("Table Name", tablesBox);        
        add(tcLb);
        wizardPage.setPageComplete(validateFields());
        tablesBox.setSelectedIndex(0);
    }

    @Override
    public Repository createRepository() {
        Object selectedTable = tablesBox.getSelectedItem();
        if (selectedTable == null || selectedTable.equals(ANY_TABLE_ENTRY)) {
            return createAllTablesRepository();
        }
        String table = descriptionToTableMap.get(selectedTable);
        if (table == null) {
            return createAllTablesRepository();
        }
        else {
            return createSingleTableRepository(table);
        }
        
    }
    
    private DatabaseRepository createSingleTableRepository(String table) {
        Level loggingLevel = AbstractDatabaseFrameDb.log.getLevel();
        AbstractDatabaseFrameDb.log.setLevel(Level.WARNING);
        try {
            return new DatabaseRepository(getText(DatabaseProperty.DRIVER_PROPERTY),
                                          getText(DatabaseProperty.URL_PROPERTY),
                                          getText(DatabaseProperty.USERNAME_PROPERTY),
                                          getText(DatabaseProperty.PASSWORD_PROPERTY),
                                          table);
        }
        catch (Throwable t) {
            if (log.isLoggable(Level.FINE))  {
                log.fine("driver = "  + getText(DatabaseProperty.DRIVER_PROPERTY));
                log.fine("url = " + getText(DatabaseProperty.URL_PROPERTY));
                log.fine("username = " + getText(DatabaseProperty.USERNAME_PROPERTY));
                log.fine("password = " + getText(DatabaseProperty.PASSWORD_PROPERTY));
                log.log(Level.FINE, "Create Repository failed", t);
            }
            return null; 
        }
        finally {
            AbstractDatabaseFrameDb.log.setLevel(loggingLevel);
        }
    }
    
    private DatabaseRepository createAllTablesRepository() {
        Level loggingLevel = AbstractDatabaseFrameDb.log.getLevel();
        AbstractDatabaseFrameDb.log.setLevel(Level.WARNING);
        try {
            return new DatabaseRepository(getText(DatabaseProperty.DRIVER_PROPERTY),
                                          getText(DatabaseProperty.URL_PROPERTY),
                                          getText(DatabaseProperty.USERNAME_PROPERTY),
                                          getText(DatabaseProperty.PASSWORD_PROPERTY));
        }
        catch (Throwable t) {
            if (log.isLoggable(Level.FINE))  {
                log.fine("driver = "  + getText(DatabaseProperty.DRIVER_PROPERTY));
                log.fine("url = " + getText(DatabaseProperty.URL_PROPERTY));
                log.fine("username = " + getText(DatabaseProperty.USERNAME_PROPERTY));
                log.fine("password = " + getText(DatabaseProperty.PASSWORD_PROPERTY));
                log.log(Level.FINE, "Create Repository failed", t);
            }
            return null; 
        }
        finally {
            AbstractDatabaseFrameDb.log.setLevel(loggingLevel);
        }
    }
    
    protected String getText(DatabaseProperty property) {
        JTextField text = textMap.get(property);
        return text.getText();
    }
    
    private boolean validateFields() {
    	Repository rep =  createRepository();
    	descriptionToTableMap.clear();
    	tablesBox.removeAllItems();
    	tablesBox.addItem(ANY_TABLE_ENTRY);
    	List<String> tableDescriptions = new ArrayList<String>(); 
    	if (rep != null) {
    		Map<String, URI> tableToOntologyMap = getTableToOntologyMap(rep);
    		for (String table : tableToOntologyMap.keySet()) {
    			URI ontology = tableToOntologyMap.get(table);
    			String description = new StringBuffer(table)
    			.append(" (")
    					.append(ontology.toString())
    					.append(")")
    					.toString();
    			descriptionToTableMap.put(description, table);
    			tableDescriptions.add(description);
    		}        
    		Collections.sort(tableDescriptions);
    		for (String description : tableDescriptions) {
    			tablesBox.addItem(description);
    		}
    	}
    	return rep != null;
    }
    
    /*
     * TODO - this is hacky - see Clark Parsia's CPDatabaseRepository TODO item.
     */
    protected Map<String, URI> getTableToOntologyMap(Repository rep) {
    	return ((DatabaseRepository) rep).getTableToOntologyMap();
    }
    
    private class TextDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            wizardPage.setPageComplete(validateFields());
        }


        public void removeUpdate(DocumentEvent e) {
            wizardPage.setPageComplete(validateFields());
        }


        public void changedUpdate(DocumentEvent e) {
            wizardPage.setPageComplete(validateFields());
        }
    }

}