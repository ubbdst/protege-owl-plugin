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

import edu.stanford.smi.protege.ui.ParentChildNode;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.InstanceNameEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A tree cell editor that allows the name of a class to be edited
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Jan-2006
 */
public class ClassNameTreeCellEditor extends DefaultCellEditor{

    RDFSClass cls;

    InstanceNameEditor component;

    JTree tree;

    JPanel panel;
    JLabel iconLabel;

    public ClassNameTreeCellEditor() {
        super(new InstanceNameEditor());
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

        this.tree = tree;
        this.cls = (RDFSClass)((ParentChildNode)value).getUserObject();
        this.component = (InstanceNameEditor)super.getTreeCellEditorComponent(tree, cls.getName(), isSelected, expanded, leaf, row);
        this.component.setInstance(cls);
        this.component.addFocusListener(new FocusAdapter(){
            public void focusLost(FocusEvent e) {
                fireEditingCanceled();
            }
        });

        panel = new JPanel(new BorderLayout(1, 1));
        panel.setBackground(tree.getBackground());

        iconLabel = new JLabel();
        iconLabel.setBackground(panel.getBackground());
        iconLabel.setIcon(cls.getImageIcon());

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(component, BorderLayout.EAST);

        return panel;
    }

    public Object getCellEditorValue() {
        return cls.getName();
    }

    protected void fireEditingStopped() {
        if (component.isValidName(component.getText())){
            // pretend cancelled to stop the tree crashing
            // as the default expects MutableTreeNodes
            super.fireEditingCanceled();
        }
    }

    public void cancelCellEditing() {
        super.cancelCellEditing();
        tree.setEditable(false);
    }
}

