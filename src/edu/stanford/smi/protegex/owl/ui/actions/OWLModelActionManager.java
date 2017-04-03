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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * A singleton managing the available OWLModelActions.
 * This can be used to populate menubars and toolbars for various platforms.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLModelActionManager {


    public static void addOWLModelActionsToMenubar(OWLModel owlModel, Adder adder) {
        List actions = getOWLModelActions();
        List menubarActions = new ArrayList();
        for (Iterator it = actions.iterator(); it.hasNext();) {
            OWLModelAction action = (OWLModelAction) it.next();
            String path = action.getMenubarPath();
            if (path != null) { // && action.isSuitable(owlModel)) {
                menubarActions.add(action);
            }
        }
        Collections.sort(menubarActions, new Comparator() {
            public int compare(Object o, Object o1) {
                OWLModelAction actionA = (OWLModelAction) o;
                OWLModelAction actionB = (OWLModelAction) o1;
                String a = actionA.getMenubarPath() + actionA.getName();
                String b = actionB.getMenubarPath() + actionB.getName();
                return a.compareTo(b);
            }
        });
        for (Iterator it = menubarActions.iterator(); it.hasNext();) {
            OWLModelAction action = (OWLModelAction) it.next();
            adder.addOWLModelAction(action);
        }
    }


    public static void addOWLModelActionsToToolbar(OWLModel owlModel, Adder adder) {
        List actions = getOWLModelActions();
        List toolbarActions = new ArrayList();
        for (Iterator it = actions.iterator(); it.hasNext();) {
            OWLModelAction action = (OWLModelAction) it.next();
            String path = action.getToolbarPath();
            if (path != null) { // && action.isSuitable(owlModel)) {
                toolbarActions.add(action);
            }
        }
        Collections.sort(toolbarActions, new Comparator() {
            public int compare(Object o, Object o1) {
                OWLModelAction actionA = (OWLModelAction) o;
                OWLModelAction actionB = (OWLModelAction) o1;
                String a = actionA.getToolbarPath() + actionA.getName();
                String b = actionB.getToolbarPath() + actionB.getName();
                return a.compareTo(b);
            }
        });
        for (Iterator it = toolbarActions.iterator(); it.hasNext();) {
            OWLModelAction action = (OWLModelAction) it.next();
            adder.addOWLModelAction(action);
        }
    }


    private static List getOWLModelActions() {
        List actions = new ArrayList();
        Class[] classes = getOWLModelActionClasses();
        for (int i = 0; i < classes.length; i++) {
            Class aClass = classes[i];
            OWLModelAction action = getOWLModelAction(aClass);
            actions.add(action);
        }
        return actions;
    }


    public static OWLModelAction getOWLModelAction(Class clazz) {
        try {
            return (OWLModelAction) clazz.newInstance();
        }
        catch (Exception ex) {
            System.err.println("[OWLModelActionManager] Fatal Error: Could not create OWLModelAction for " + clazz);
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
            return null;
        }
    }


    public static Class[] getOWLModelActionClasses() {
        Collection clses = new ArrayList(PluginUtilities.getClassesWithAttribute("OWLModelAction", "True"));
        return (Class[]) clses.toArray(new Class[0]);
    }


    public static interface Adder {

        void addOWLModelAction(OWLModelAction action);
    }
}
