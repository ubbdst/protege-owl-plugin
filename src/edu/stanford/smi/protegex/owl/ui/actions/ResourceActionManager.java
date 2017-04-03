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

package edu.stanford.smi.protegex.owl.ui.actions;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceActionManager {

    private static Map map = new HashMap();

    private static Set userDefinedClasses = new HashSet();


    /**
     * A work-around method for ResourceActions from outside the OWL Plugin.
     * Since due to a bug in the Java virtual machine these ResourceActions
     * can not be found through their manifest, they need to be added
     * manually, preferably through a ProjectPlugin.
     *
     * @param clazz the Class of a ResourceAction to add
     */
    public static void addResourceActionClass(Class clazz) {
        userDefinedClasses.add(clazz);
    }


    public static void addResourceActions(JPopupMenu menu, Component parent, RDFResource resource) {
        List actions = getResourceActions(parent, resource);
        if (actions.size() > 0) {
            if (menu.getSubElements().length > 0) {
                menu.addSeparator();
            }
            Collections.sort(actions);
            String previousGroup = null;
            JMenu subMenu = null;
            for (Iterator it = actions.iterator(); it.hasNext();) {
                ResourceAction resourceAction = (ResourceAction) it.next();
                String group = resourceAction.getGroup();
                if (group != null && !group.equals(previousGroup)) {
                    boolean isSubMenu = group.endsWith("/");
                    if ((previousGroup != null && !previousGroup.endsWith("/")) || !isSubMenu) {
                        menu.addSeparator();
                    }
                    if (isSubMenu) {
                        subMenu = new JMenu(group.substring(0, group.length() - 1));
                        menu.add(subMenu);
                    }
                    else {
                        subMenu = null;
                    }
                }
                if (subMenu == null) {
                    menu.add(resourceAction);
                }
                else {
                    subMenu.add(resourceAction);
                }
                previousGroup = group;
            }
        }
    }


    public static void addResourceActions(JToolBar toolBar, Component parent, RDFResource resource) {
        List actions = getResourceActions(parent, resource);
        if (actions.size() > 0) {
            toolBar.addSeparator();
            Collections.sort(actions);
            String previousGroup = null;
            for (Iterator it = actions.iterator(); it.hasNext();) {
                ResourceAction resourceAction = (ResourceAction) it.next();
                if (resourceAction.isInToolBar()) {
                    String group = resourceAction.getGroup();
                    if (group != null && !group.equals(previousGroup)) {
                        toolBar.addSeparator();
                    }
                    ResourceAction c = ResourceActionManager.createClone(resourceAction);
                    ComponentFactory.addToolBarButton(toolBar, c);
                    previousGroup = group;
                }
            }
        }
    }


    private static List getResourceActions(Component parent, RDFResource resource) {
        List actions = new ArrayList();
        if (resource != null) {
            Class[] classes = getResourceActionClasses();
            for (int i = 0; i < classes.length; i++) {
                Class aClass = classes[i];
                ResourceAction action = getResourceAction(aClass);
                if (action.isSuitable(parent, resource)) {
                    action.initialize(parent, resource);
                    actions.add(action);
                }
            }
        }
        return actions;
    }


    public static ResourceAction createClone(ResourceAction resourceAction) {
        Class clazz = resourceAction.getClass();
        try {
            ResourceAction action = (ResourceAction) clazz.newInstance();
            action.initialize(resourceAction.getComponent(), resourceAction.getResource());
            return action;
        }
        catch (Exception ex) {
            System.err.println("[ResourceActionManager] Fatal Error: Could not create ResourceAction for " + clazz);
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            return null;
        }
    }


    public static ResourceAction getResourceAction(Class clazz) {
        ResourceAction action = (ResourceAction) map.get(clazz);
        if (action == null) {
            try {
                action = (ResourceAction) clazz.newInstance();
                map.put(clazz, action);
            }
            catch (Exception ex) {
              Log.getLogger().log(Level.SEVERE, "[ResourceActionManager] Fatal Error: Could not create ResourceAction for " + clazz, ex);
            }
        }
        return action;
    }


    public static Class[] getResourceActionClasses() {
        Collection clses = new ArrayList(PluginUtilities.getClassesWithAttribute("ResourceAction", "True"));
        clses.addAll(userDefinedClasses);
        return (Class[]) clses.toArray(new Class[0]);
    }
    
    
    public static void setResourceActionsEnabled(JPopupMenu menu, boolean enabled) {            	
    	for (int i=0; i < menu.getComponentCount();i++) {
			Object item = menu.getComponent(i);			
			if (item instanceof Component) {
				((Component)item).setEnabled(enabled);
			}
		}  
    }
}
