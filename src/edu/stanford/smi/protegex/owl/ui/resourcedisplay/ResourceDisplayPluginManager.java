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

package edu.stanford.smi.protegex.owl.ui.resourcedisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * The manager of all InstanceDisplayPlugins.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceDisplayPluginManager {
    private static transient final Logger log = Log.getLogger(ResourceDisplayPluginManager.class);

    private static Map map = new HashMap();

    private static Set userDefinedClasses = new HashSet();


    /**
     * A work-around method for InstanceDisplayPlugins from outside the OWL Plugin.
     * Since due to a bug in the Java virtual machine these Classes
     * can not be found through their manifest, they need to be added
     * manually, preferably through a ProjectPlugin.
     *
     * @param clazz the Class of a ResourceDisplayPlugin to add
     */
    public static void addInstanceDisplayPluginClass(Class clazz) {
        userDefinedClasses.add(clazz);
    }


    public static ResourceDisplayPlugin getInstanceDisplayPlugin(Class clazz) {
        ResourceDisplayPlugin plugin = (ResourceDisplayPlugin) map.get(clazz);
        if (plugin == null) {
            try {
                plugin = (ResourceDisplayPlugin) clazz.newInstance();
                map.put(clazz, plugin);
            }
            catch (Exception ex) {
                System.err.println("[ResourceDisplayPluginManager] Fatal Error: Could not create Plugin for " + clazz);
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            }
        }
        return plugin;
    }


    public static Class[] getInstanceDisplayPluginClasses() {
        Collection clses = new ArrayList(PluginUtilities.getClassesWithAttribute("ResourceDisplayPlugin", "True"));
        clses.addAll(userDefinedClasses);
        Class[] classes = (Class[]) clses.toArray(new Class[0]);
        Arrays.sort(classes, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 instanceof Class && o2 instanceof Class) {
                    return ((Class) o1).getName().compareTo(((Class) o2).getName());
                }
                return 0;
            }
        });
        return classes;
    }


    /**
     * Calls the corresponding method in all currently installed InstanceDisplayPlugins.
     *
     * @param frame     the currently selected Frame
     * @param hostPanel the JPanel to add stuff into (if requested by the plugins)
     */
    public static void initInstanceDisplay(RDFResource frame, JPanel hostPanel) {
        Class[] classes = getInstanceDisplayPluginClasses();
        if (log.isLoggable(Level.FINE)) {
            log.fine("Installed InstanceDisplayPlugins:");
        }
        for (int i = 0; i < classes.length; i++) {
            Class c = classes[i];
            ResourceDisplayPlugin plugin = getInstanceDisplayPlugin(c);
            if (log.isLoggable(Level.FINE)) {
                log.fine("- " + c + ": " + plugin);
            }
            if (plugin != null) {
                plugin.initResourceDisplay(frame, hostPanel);
            }
        }
    }
}
