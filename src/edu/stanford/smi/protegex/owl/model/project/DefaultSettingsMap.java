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

import edu.stanford.smi.protege.util.PropertyList;

import java.util.Iterator;

/**
 * An OWLProject wrapping a traditional Protege Project.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultSettingsMap extends AbstractSettingsMap {

    private PropertyList propertyList;


    public DefaultSettingsMap(PropertyList propertyList) {
        this.propertyList = propertyList;
    }


    public Boolean getBoolean(String key) {
        return propertyList.getBoolean(key);
    }


    public Integer getInteger(String key) {
        return propertyList.getInteger(key);
    }


    public SettingsMap getSettingsMap(String key) {
        PropertyList list = propertyList.getPropertyList(key);
        return new DefaultSettingsMap(list);
    }


    public String getString(String key) {
        return propertyList.getString(key);
    }


    public Iterator listKeys() {
        return propertyList.getNames().iterator();
    }


    public void remove(String key) {
        propertyList.remove(key);
    }


    public void setBoolean(String key, Boolean value) {
        if (value == null) {
            propertyList.remove(key);
        }
        else {
            propertyList.setBoolean(key, value);
        }
    }


    public void setInteger(String key, Integer value) {
        if (value == null) {
            propertyList.remove(key);
        }
        else {
            propertyList.setInteger(key, value);
        }
    }


    public void setString(String key, String value) {
        if (value == null) {
            propertyList.remove(key);
        }
        else {
            propertyList.setString(key, value);
        }
    }
}
