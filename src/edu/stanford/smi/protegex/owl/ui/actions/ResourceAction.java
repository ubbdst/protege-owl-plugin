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

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class ResourceAction extends AbstractAction implements Comparable {

    private Component component;

    private String group;

    private boolean inToolBar;

    private RDFResource resource;


    public ResourceAction(String name, Icon icon) {
        this(name, icon, null);
    }


    public ResourceAction(String name, Icon icon, String group) {
        this(name, icon, group, false);
    }


    /**
     * Constructs a new ResourceAction.
     *
     * @param name      the display name of the Action
     * @param icon      the Icon
     * @param group     the (optional) group this should belong to
     * @param inToolBar true to put this into the tool bar at the bottom of forms
     *                  (this is only possible if there is an icon as well)
     */
    public ResourceAction(String name, Icon icon, String group, boolean inToolBar) {
        super(name, icon);
        this.group = group;
        this.inToolBar = inToolBar;
    }


    public int compareTo(Object o) {
        if (o instanceof ResourceAction) {
            ResourceAction other = (ResourceAction) o;
            String thisGroup = getGroup();
            if (thisGroup == null) {
                thisGroup = "";
            }
            String otherGroup = other.getGroup();
            if (otherGroup == null) {
                otherGroup = "";
            }
            String thisName = (String) getValue(Action.NAME);
            String otherName = (String) other.getValue(Action.NAME);
            int groupCompare = thisGroup.compareTo(otherGroup);
            if (groupCompare == 0) {
                int result = new Integer(other.getPriority()).compareTo(new Integer(getPriority()));
                if (result != 0) {
                    return result;
                }
                return thisName.compareTo(otherName);
            }
            else {
                return groupCompare;
            }
        }
        return 0;
    }


    protected Component getComponent() {
        return component;
    }


    public String getGroup() {
        return group;
    }


    /**
     * Gets an (optional) integer that can be used to control the order of actions
     * in a menu.  The higher the number, the further up the item will appear.
     *
     * @return the priority (0 is default)
     */
    public int getPriority() {
        return 0;
    }


    public OWLModel getOWLModel() {
        if (resource != null) {
            return resource.getOWLModel();
        }
        else {
            return null;
        }
    }


    protected RDFResource getResource() {
        return resource;
    }


    public void initialize(Component component, RDFResource resource) {
        this.component = component;
        this.resource = resource;
    }


    public boolean isInToolBar() {
        return inToolBar;
    }


    public abstract boolean isSuitable(Component component, RDFResource resource);
}
