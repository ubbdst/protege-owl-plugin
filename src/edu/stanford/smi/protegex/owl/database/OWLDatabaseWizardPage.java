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

package edu.stanford.smi.protegex.owl.database;

import static edu.stanford.smi.protege.storage.database.DatabaseProperty.DRIVER_PROPERTY;
import static edu.stanford.smi.protege.storage.database.DatabaseProperty.URL_PROPERTY;
import static edu.stanford.smi.protege.storage.database.DatabaseProperty.PASSWORD_PROPERTY;
import static edu.stanford.smi.protege.storage.database.DatabaseProperty.TABLENAME_PROPERTY;
import static edu.stanford.smi.protege.storage.database.DatabaseProperty.USERNAME_PROPERTY;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.stanford.smi.protege.exception.ProtegeIOException;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseWizardPage;
import edu.stanford.smi.protege.storage.database.DefaultDatabaseFrameDb;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseWizardPage extends DatabaseWizardPage {
    private static final long serialVersionUID = 251501005887139194L;
    private static transient Logger log = Log.getLogger(OWLDatabaseWizardPage.class);

    private OWLDatabasePlugin plugin;
    
    private JTextField ontologyName;


    public OWLDatabaseWizardPage(Wizard wizard, OWLDatabasePlugin plugin) {
        super(wizard, plugin);
        this.plugin = plugin;
    }
    
    protected Class<? extends DatabaseFrameDb> getDatabaseFrameDbClass() {
        return DefaultDatabaseFrameDb.class;
    }
    
    @Override
    protected void layoutComponents(Box panel) {
        if (!getFromExistingSources()) {
            ontologyName = ComponentFactory.createTextField(FactoryUtils.generateOntologyURIBase());
            ontologyName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent event) {
                    updateSetPageComplete();
                }
            });
            panel.add(new LabeledComponent("Ontology Name", ontologyName));
        }
        super.layoutComponents(panel);
    }
    
    @Override
    protected boolean isComplete() {
        if (getFromExistingSources()) {
            return super.isComplete();  
        }
        return super.isComplete() && isValidURL();
    }
    
    private boolean isValidURL() {
        try {
            new URL(ontologyName.getText());
            return true;
        }
        catch (MalformedURLException e) {
            setErrorText("Need valid URL for ontology name: " + e);
            return false;
        }
    }
    
    @Override
    public void onFinish() {
        if (!getFromExistingSources()) {
            plugin.setOntologyName(ontologyName.getText());
        }
        if (!okToCreateDatabase()) {  // ToDo Jennifer or Tania will know the better way...
            throw new ProtegeIOException("Database already exists! and the user denied overwrite");
        }
        super.onFinish();
    }
    
    protected boolean okToCreateDatabase() {
        Connection connection = null;
        try {
            try {
                Class.forName(getFieldText(DRIVER_PROPERTY));
                connection = DriverManager.getConnection(getFieldText(URL_PROPERTY), getFieldText(USERNAME_PROPERTY), getFieldText(PASSWORD_PROPERTY));
            }
            catch (ClassNotFoundException cnfe) {
                throw new ProtegeIOException(cnfe);
            }
            catch (SQLException sqle) {
                throw new ProtegeIOException(sqle);
            }
            return okToCreateDatabase(connection);
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException sqle) {
                log.log(Level.WARNING, "Exception caught trying to close connections during database existence check.", sqle);
            }
        }
    }
    
    private boolean okToCreateDatabase(Connection connection) {
        if (!databaseExists(connection)) {
            return true;
        }
        String existingOntologyName;
        try {
            existingOntologyName = DatabaseFactoryUtils.getOntologyFromTable(getDatabaseFrameDbClass(),
                                                                             getFieldText(DRIVER_PROPERTY), getFieldText(URL_PROPERTY), 
                                                                             getFieldText(USERNAME_PROPERTY), getFieldText(PASSWORD_PROPERTY), 
                                                                             getFieldText(TABLENAME_PROPERTY));
        }
        catch (SQLException sqle) {
            existingOntologyName = null;
        }
        String message;
        if (getFromExistingSources() && !isFileToDatabase() && existingOntologyName != null) {
            return true;
        }
        else if (getFromExistingSources() && !isFileToDatabase()) {
            message = "Database already exists but is in the wrong format for an OWL Database Project.\nOverwrite with blank owl project?";
        }
        else if (existingOntologyName != null ) {
           message =  "Database table already exists and holds an ontology named\n" + existingOntologyName + ".\nOverwrite?";
        }
        else {
            message = "Database table already exists.  Overwrite?";
        }
        int ok = ModalDialog.showMessageDialog(this, message, "Overwrite Table?", ModalDialog.MODE_OK_CANCEL);
        return ok == ModalDialog.OPTION_OK;
    }
    
    protected boolean databaseExists(Connection connection)  {
        Statement statement;
        try {
            statement = connection.createStatement();
        }
        catch (SQLException  sqle) {
            throw new ProtegeIOException(sqle);
        }
        try {
            statement.execute("select count(*) from " + getFieldText(TABLENAME_PROPERTY));
            return true;
        }
        catch (SQLException  sqle) {
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "This exception should indicate that the table doesn't exist", sqle);
            }
            return false;
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (SQLException sqle) {
                log.log(Level.WARNING, "Unexpected exception caught trying to check the  existence of a database table", sqle);
            }
        }

    }
    
    public boolean getFromExistingSources() {
        return false;
    }
    
    public boolean isFileToDatabase() {
        return false;
    }
}
