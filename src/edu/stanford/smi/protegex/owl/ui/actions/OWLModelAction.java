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

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.beans.PropertyChangeListener;

/**
 * An object representing a global "action" which can be used in tool bars and
 * menu bars.  This interface is independent from the Swing Action class, because
 * it should also serve as a container for action items in Eclipse or similar
 * platforms.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLModelAction extends Disposable, IconOwner {

    final static String PATH_SEPARATOR = "/";

    final static String NAME = "OWLModelAction.Name";

    final static String ICON = "OWLModelAction.Icon";

    final static String SUITABILITY = "OWLModelAction.Suitable";


    /**
     * Adds a PropertyChangeListener to receive events when one of the characteristics
     * of this (name, icon, suitability) have changed.
     * @param listener  the listener to add
     */
    void addPropertyChangeListener(PropertyChangeListener listener);


    /**
     * Gets a path to the menu bar location of this action.
     * This method determines where the menu item will show up, if at all.
     * The path consists of a menu name with an optional group name.
     * There must be a '/' character (<CODE>PATH_SEPARATOR</CODE>) between
     * the menu and group name.
     *
     * @return the path for the menu or null if this shall not appear in the menu
     */
    String getMenubarPath();


    /**
     * Gets the human readable name of this action, to be used as menu item text and
     * tool bar button tool tip text.
     *
     * @return the name (must not be null)
     */
    String getName();


    /**
     * Gets the path to the tool bar location of this action.
     * This is either null (to not show up in the tool bar), or a group name.
     * Actions that shall be grouped together should get the same path.
     *
     * @return the path or null
     */
    String getToolbarPath();


    /**
     * Determines whether this action shall be used for the given OWLModel.
     * This allows programmers to veto the use of their action in OWLDatabaseModels
     * or projects that don't import SWRL etc.
     *
     * @param owlModel the OWLModel to check against
     * @return true  if this is suitable for owlModel
     */
    boolean isSuitable(OWLModel owlModel);


    void notifyPropertyChangeListeners(String propertyName, Object oldValue, Object newValue);


    void removePropertyChangeListener(PropertyChangeListener listener);


    /**
     * Called when the action is executed by the user.
     *
     * @param owlModel the OWLModel to operate on
     */
    void run(OWLModel owlModel);
}