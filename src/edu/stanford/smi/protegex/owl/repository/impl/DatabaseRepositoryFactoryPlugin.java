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

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.storage.database.DatabaseProperty;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactoryPlugin;

public class DatabaseRepositoryFactoryPlugin implements RepositoryFactoryPlugin {
    private final static transient Logger log = Log.getLogger(DatabaseRepositoryFactoryPlugin.class);

    public Repository createRepository(OWLModel model,
                                       String repositoryDescriptor) {
        try {
            return new DatabaseRepository(repositoryDescriptor);
        }
        catch (ClassNotFoundException e) {
            log.warning("Database repository driver class not found = " + e);
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "Exception caught initializing the database repository", e);
            }
            return null;
        }
        catch (SQLException e) {
            log.warning("SQL error caught initializing the database repository" + e);
            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "SQL error caught initializing the database repository", e);
            }
            return null;
        }
    }

    public boolean isSuitable(OWLModel model, String repositoryDescriptor) {
        if (repositoryDescriptor.startsWith(DatabaseRepository.REPOSITORY_DESCRIPTOR_PREFIX)) {
            try {
                List<String> fields = DatabaseRepository.parse(repositoryDescriptor);
                Class.forName(fields.get(DatabaseRepository.getDBPropertyIndex(DatabaseProperty.DRIVER_PROPERTY)));
                return  fields.size() > DatabaseRepository.DATABASE_FIELDS.length;
            }
            catch (Throwable t) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Repository descriptor = " + repositoryDescriptor);
                    log.log(Level.FINE, "Exception caught figuring out if database repository was applicable", t);
                }
                return false;
            }
        }
        return false;
    }

}
