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

package edu.stanford.smi.protegex.owl.model.project;

/**
 * A generic interface to store project-related settings and configurations.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLProject {

    /**
     * Gets a session object for the associated OWLModel.
     * Session objects can be associated with an OWLModel, but they are not stored
     * when the project is closed.
     * @param key  the key of the object
     * @return the object or null
     */
    Object getSessionObject(String key);


    /**
     * Gets the projects' SettingsMap.
     * @return the SettingsMap
     */
    SettingsMap getSettingsMap();


    /**
     * Sets a session object.
     * @param key  the key of the object
     * @param object  the new value or null to delete the entry
     */
    void setSessionObject(String key, Object object);
}
