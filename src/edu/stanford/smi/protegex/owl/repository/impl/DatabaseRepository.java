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

package edu.stanford.smi.protegex.owl.repository.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.AmalgamatedLoadException;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseFrameDbFactory;
import edu.stanford.smi.protege.storage.database.DatabaseProperty;
import edu.stanford.smi.protege.storage.database.DefaultDatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.IdleConnectionNarrowFrameStore;
import edu.stanford.smi.protege.storage.database.ValueCachingNarrowFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.database.DatabaseFactoryUtils;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.repository.Repository;

public class DatabaseRepository implements Repository {
    private final static Logger log = Log.getLogger(DatabaseRepository.class);
    
	private Connection connection;
	private Map<DatabaseProperty, String> fieldMap = new EnumMap<DatabaseProperty, String>(DatabaseProperty.class);
	
	//FIXME: This map has a problem if the DB contains tables with the same ontology name
	private Map<URI, String> ontologyToTable = new HashMap<URI, String>();
	//This map contains all tables (also the ones with the same ontology name)
	private Map<String, URI> tableToOntology = new HashMap<String, URI>();
	
	public final static String REPOSITORY_DESCRIPTOR_PREFIX = "database:";
	public final static char SEPARATOR_CHAR = ',';
	
    public static final String SQL_TABLE_TYPE = "TABLE";
    public static final String SQL_VIEW_TYPE  = "VIEW";
    public int SQL_GET_TABLE_TYPES_TABLE_TYPE_COL=1;
    public int SQL_GET_TABLES_TABLENAME_COL=3;
    
    public final static DatabaseProperty[] DATABASE_FIELDS = { 
        DatabaseProperty.DRIVER_PROPERTY,
        DatabaseProperty.URL_PROPERTY,
        DatabaseProperty.USERNAME_PROPERTY,
        DatabaseProperty.PASSWORD_PROPERTY
    };
    public static int getDBPropertyIndex(DatabaseProperty property) {
        int i = 0;
        for (DatabaseProperty other : DATABASE_FIELDS) {
            if (property == other) {
                return i;
            }
            i++;
        }
        throw  new IllegalArgumentException("Invalid property");
    }
	
	static public List<String> parse(String repositoryDescriptor) {
		List<String> fields = new ArrayList<String>();
		int start = REPOSITORY_DESCRIPTOR_PREFIX.length();
		while (true) {
			int end = repositoryDescriptor.indexOf(SEPARATOR_CHAR, start);
			if (end < 0) {
				fields.add(repositoryDescriptor.substring(start));
				return fields;
			}
			fields.add(repositoryDescriptor.substring(start, end));
			start = end + 1;
		}
	}
	
	public DatabaseRepository(String driver,
	                          String url,
	                          String user,
	                          String password) throws SQLException, ClassNotFoundException {
	    fieldMap.put(DatabaseProperty.DRIVER_PROPERTY, driver);
	    fieldMap.put(DatabaseProperty.URL_PROPERTY, url);
	    fieldMap.put(DatabaseProperty.USERNAME_PROPERTY, user);
	    fieldMap.put(DatabaseProperty.PASSWORD_PROPERTY, password);
	    Class.forName(getDriver());
	    connect();
	    try {
	        findAllTables();
	    }
	    finally {
	        disconnect();
	    }
	}
	
	   public DatabaseRepository(String driver,
	                              String url,
	                              String user,
	                              String password,
	                              String table) throws SQLException, ClassNotFoundException {
	        fieldMap.put(DatabaseProperty.DRIVER_PROPERTY, driver);
	        fieldMap.put(DatabaseProperty.URL_PROPERTY, url);
	        fieldMap.put(DatabaseProperty.USERNAME_PROPERTY, user);
	        fieldMap.put(DatabaseProperty.PASSWORD_PROPERTY, password);
	        Class.forName(getDriver());
	        addTable(table);      
	    }
	
	public DatabaseRepository(String repositoryDescriptor) throws ClassNotFoundException, SQLException {
		List<String> fields = parse(repositoryDescriptor);
		for (DatabaseProperty field : DATABASE_FIELDS) {
			fieldMap.put(field, fields.get(getDBPropertyIndex(field)));
		}
		Class.forName(getDriver());
		connect();
		try {
		    for (int index = DATABASE_FIELDS.length; index < fields.size(); index++) {
		        String table = fields.get(index);
		        if (table == null || table.length() == 0) { //backwards compatibility fix for 3.4
		        	log.warning("Invalid table description in database repository. Table name is empty." +
		        			" Repository Description: " + repositoryDescriptor);
		        } else {
		        	addTable(table);
		        }
		    }
		} finally {
		    disconnect();
		}
	}


	
	private void findAllTables() throws SQLException {
	    DatabaseMetaData metaData = connection.getMetaData();
	    ResultSet tableTypesSet = metaData.getTableTypes();
	    List<String> tableTypes = new ArrayList<String>();
	    while (tableTypesSet.next()) {
	        String tableType = tableTypesSet.getString(SQL_GET_TABLE_TYPES_TABLE_TYPE_COL);
	        if (tableType.equals(SQL_TABLE_TYPE) || tableType.equals(SQL_VIEW_TYPE)) {
	            tableTypes.add(tableType);
	        }
	    }
	    ResultSet tableSet = metaData.getTables(null, null, null, tableTypes.toArray(new String[1]));
	    while (tableSet.next()) {
	        String table = tableSet.getString(SQL_GET_TABLES_TABLENAME_COL);
	        addTable(table);
	    }
	    if (ontologyToTable.isEmpty()) {
	        throw new SQLException("No tables containing ontologies found");
	    }
	}
	
	public void connect() throws SQLException {
		connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
	}
	
	public void disconnect() throws SQLException {
		connection.close();
		connection = null;
	}
	
	public boolean addTable(String table) {
        String ontology = null;
	    try {
	        ontology = DatabaseFactoryUtils.getOntologyFromTable(
	                DefaultDatabaseFrameDb.class,
					getDriver(), getUrl(), getUser(), getPassword(), table );
	        if (ontology != null) {
	            ontologyToTable.put(new URI(ontology), table);
	            tableToOntology.put(table, new URI(ontology));
	            return true;
	        }
	    }
	    catch (SQLException e) {
	        if (log.isLoggable(Level.FINE)) {
	            log.log(Level.FINE, "Exception caught looking for ontology in db table " + table, e);
	        }
	    }
	    catch (URISyntaxException e) {
	        if (log.isLoggable(Level.FINE)) {
	            log.log(Level.FINE, "Ontology " + ontology + " found in " + table + " not in uri format.", e);
	        } 
	    }
        return false;
	}

	@SuppressWarnings("unchecked")
    public TripleStore loadImportedAssertions(OWLModel owlModel, URI ontologyName)
			throws OntologyLoadException {
	    String table = ontologyToTable.get(ontologyName);
	    DatabaseFrameDb dbFrameStore = DatabaseFrameDbFactory.createDatabaseFrameDb(DefaultDatabaseFrameDb.class);
	    dbFrameStore.initialize(owlModel.getOWLJavaFactory(), getDriver(), getUrl(), getUser(), getPassword(), table, true);
	    ValueCachingNarrowFrameStore valueCache = new ValueCachingNarrowFrameStore(dbFrameStore);
	    IdleConnectionNarrowFrameStore nfs = new IdleConnectionNarrowFrameStore(valueCache);
	    nfs.setName(ontologyName.toString());
	    TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
	    TripleStore importedTripleStore = null;
	    TripleStore importingTripleStore = tripleStoreModel.getActiveTripleStore();
	    try {
	        importedTripleStore = tripleStoreModel.createActiveImportedTripleStore(nfs);
	        Collection errors = new ArrayList();
	        DatabaseFactoryUtils.readOWLOntologyFromDatabase(owlModel, importedTripleStore);
	        FactoryUtils.loadEncodedNamespaceFromModel(owlModel, importedTripleStore, errors);
	        FactoryUtils.addPrefixesToModelListener(owlModel, importedTripleStore);
	        DatabaseFactoryUtils.loadImports(owlModel, errors);
	        if (!errors.isEmpty()) {
	            throw new AmalgamatedLoadException(errors);
	        }
	    }
	    finally {
	        tripleStoreModel.setActiveTripleStore(importingTripleStore);
	    }
	    return importedTripleStore;
	}

	public boolean contains(URI ontologyName) {
	    return ontologyToTable.keySet().contains(ontologyName);
	}

	public Collection<URI> getOntologies() {
	    return Collections.unmodifiableCollection(ontologyToTable.keySet());
	}
	
	public String getDBTable(URI ontologyName) {
	    return ontologyToTable.get(ontologyName);
	}

	public String getOntologyLocationDescription(URI ontologyName) {
		return "Table " + ontologyToTable.get(ontologyName) + " of the database " + getUrl();
	}

	public OutputStream getOutputStream(URI ontologyName) throws IOException {
		return null;
	}

	public String getRepositoryDescription() {
		return "Repository for the database " + getUrl();
	}

	public String getRepositoryDescriptor() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(REPOSITORY_DESCRIPTOR_PREFIX);
	    for (DatabaseProperty field : DATABASE_FIELDS) {
	        sb.append(fieldMap.get(field));
	        sb.append(SEPARATOR_CHAR);
	    }
	    for (String table : ontologyToTable.values()) {
	        sb.append(table);
	        sb.append(SEPARATOR_CHAR);
	    }
	    return sb.substring(0, sb.length() - 1);
	}

	public boolean isSystem() {
		return false;
	}

	public boolean isWritable(URI ontologyName) {
		return true;
	}
	
	public boolean hasOutputStream(URI ontologyName) {
	    return false;
	}

	public void refresh() {
	    // not sure if this should look at all the tables in the database or just 
	    // request the ones selected...  When am i called?
	}

	public String getDriver() {
		return fieldMap.get(DatabaseProperty.DRIVER_PROPERTY);
	}
	
	public String getUrl() {
		return fieldMap.get(DatabaseProperty.URL_PROPERTY);
	}
	
	public String getUser() {
		return fieldMap.get(DatabaseProperty.USERNAME_PROPERTY);
	}

	public String getPassword() {
		return fieldMap.get(DatabaseProperty.PASSWORD_PROPERTY);
	}
	
	public Map<String, URI> getTableToOntologyMap() { 
		return new HashMap<String, URI>(tableToOntology);
	}
}
