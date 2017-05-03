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

package edu.stanford.smi.protegex.owl.ui;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OverlayIcon;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLLabeledComponent extends LabeledComponent {


    private static final long serialVersionUID = -7058443751321097659L;


    public OWLLabeledComponent(String label, Component c) {
        super(label, c);
    }


    public OWLLabeledComponent(String label, Component c, boolean verticallyStretchable, boolean swappedHeader) {
        super(label, c, verticallyStretchable, swappedHeader);
    }


    public OWLLabeledComponent(String label, JScrollPane c) {
        super(label, c);
    }


    public JButton addHeaderButton(Action action) {
        JButton button = super.addHeaderButton(action);
        Icon icon = (Icon) action.getValue(Action.SMALL_ICON);
        if (icon instanceof OverlayIcon) {
            button.setDisabledIcon(((OverlayIcon) icon).getGrayedIcon());
        }
        if (action instanceof ResourceSelectionAction) {
            ((ResourceSelectionAction) action).activateComboBox(button);
        }
        return button;
    }


    public void insertHeaderComponent(Component component, int index) {
        JToolBar toolBar = WidgetUtilities.getJToolBar(this);
        toolBar.add(component, index);
    }
}
