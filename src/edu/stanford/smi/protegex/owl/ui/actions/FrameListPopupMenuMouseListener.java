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

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.PopupMenuMouseListener;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FrameListPopupMenuMouseListener extends PopupMenuMouseListener {

    private JList list;


    public FrameListPopupMenuMouseListener(JList list) {
        super(list);
        this.list = list;
    }


    protected JPopupMenu getPopupMenu() {
        Object[] selection = list.getSelectedValues();
        if (selection.length == 1) {
            Instance instance = (Instance) selection[0];
            if (instance instanceof RDFResource) {
                JPopupMenu menu = new JPopupMenu();
                ResourceActionManager.addResourceActions(menu, list, (RDFResource) instance);
                if (menu.getSubElements().length > 0) {
                    return menu;
                }
            }
        }
        return null;
    }


    protected void setSelection(JComponent c, int x, int y) {
        final int listSize = list.getModel().getSize();
        Rectangle r = list.getCellBounds(0, listSize - 1);
        if (r.contains(x, y)) {
            int row = y / (r.height / listSize);
            if (row < listSize) {
                list.setSelectedIndex(row);
            }
        }
    }
}
