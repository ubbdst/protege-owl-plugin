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

package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SecondaryHierarchyPanel extends HierarchyPanel {

    private Action closeAction = new AbstractAction("Close hierarchy",
            OWLIcons.getImageIcon("CloseHierarchy")) {
        public void actionPerformed(ActionEvent e) {
            close();
        }
    };

    private Action startSynchronizeAction = new AbstractAction("Synchronize this hierarchy",
            OWLIcons.getImageIcon("StartSynchronizeHierarchy")) {
        public void actionPerformed(ActionEvent e) {
            startSynchronize();
        }
    };

    private Action stopSynchronizeAction = new AbstractAction("Stop synchronizing this hierarchy",
            OWLIcons.getImageIcon("StopSynchronizeHierarchy")) {
        public void actionPerformed(ActionEvent e) {
            stopSynchronize();
        }
    };


    public SecondaryHierarchyPanel(Hierarchy hierarchy, HierarchyManager hierarchyManager) {
        super(hierarchy, hierarchyManager, false, null);
        stopSynchronizeAction.setEnabled(false);
        ComponentFactory.addToolBarButton(toolBar, startSynchronizeAction, ResourceDisplay.SMALL_BUTTON_WIDTH);
        ComponentFactory.addToolBarButton(toolBar, stopSynchronizeAction, ResourceDisplay.SMALL_BUTTON_WIDTH);
        ComponentFactory.addToolBarButton(toolBar, closeAction, ResourceDisplay.SMALL_BUTTON_WIDTH);
    }


    private void close() {
        hierarchyManager.close(hierarchy);
    }


    public boolean isSynchronized() {
        return stopSynchronizeAction.isEnabled();
    }


    public void startSynchronize() {
        startSynchronizeAction.setEnabled(false);
        stopSynchronizeAction.setEnabled(true);
    }


    public void stopSynchronize() {
        startSynchronizeAction.setEnabled(true);
        stopSynchronizeAction.setEnabled(false);
    }
}
