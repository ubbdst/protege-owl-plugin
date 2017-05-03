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

package edu.stanford.smi.protegex.owl.model.classdisplay;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.classdisplay.manchester.ManchesterOWLClassDisplay;

import java.util.logging.Level;

/**
 * A Singleton object that manages the available OWLClassDisplays.
 * New displays can be registered by plugins by means of a manifest entry
 * tagged with "OWLClassRenderer=true".
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLClassDisplayFactory {

    public final static String APPLICATION_PROPERTY = "OWLClassDisplayFactory" + ".default";

    private static OWLClassDisplay defaultDisplay;

    public final static String KEY = "OWLClassDisplay";


    public static Class[] getAvailableDisplayClasses() {
        return (Class[]) PluginUtilities.getClassesWithAttribute(KEY, "true").toArray(new Class[0]);
    }


    public static OWLClassDisplay getDefaultDisplay() {
        if (defaultDisplay == null) {
            String className = ApplicationProperties.getString(APPLICATION_PROPERTY);
            if (className != null) {
                try {
                    Class c = Class.forName(className);// PluginUtilities.forName(className);
                    defaultDisplay = getDisplay(c);
                }
                catch (Exception ex) {
                    Log.getLogger().log(Level.WARNING, "Could not create OWLClassDisplay of type " + className);
                    defaultDisplay = new ManchesterOWLClassDisplay();
                }
            }
            else {
                defaultDisplay = new ManchesterOWLClassDisplay();
            }
        }
        return defaultDisplay;
    }


    public static OWLClassDisplay getDisplay(Class type) {
        try {
            return (OWLClassDisplay) type.newInstance();
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }


    public static void setDefaultDisplay(OWLClassDisplay renderer) {
        defaultDisplay = renderer;
        ApplicationProperties.setString(APPLICATION_PROPERTY, renderer.getClass().getName());
    }
}
