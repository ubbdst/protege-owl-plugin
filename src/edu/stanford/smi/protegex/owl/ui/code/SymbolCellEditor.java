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

package edu.stanford.smi.protegex.owl.ui.code;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 6, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SymbolCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor, SymbolEditorHandler {

    private SymbolEditorComponent editorComponent;


    public SymbolCellEditor(SymbolEditorComponent editorComponent) {
        this.editorComponent = editorComponent;
    }


    public Object getCellEditorValue() {
        return editorComponent.getTextComponent().getText();
    }


    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return ((MouseEvent) e).getClickCount() >= 2 &&
                    SwingUtilities.isRightMouseButton((MouseEvent) e) == false;
        }
        return true;
    }


    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        editorComponent.getTextComponent().setText(value != null ? value.toString() : "");
        editorComponent.getTextComponent().requestFocus();
        return editorComponent;
    }


    public Component getTreeCellEditorComponent(JTree tree,
                                                Object value,
                                                boolean isSelected,
                                                boolean expanded,
                                                boolean leaf,
                                                int row) {
        editorComponent.getTextComponent().setText(value != null ? value.toString() : "");
        editorComponent.requestFocus();
        return editorComponent;
    }


    public void stopEditing() {
        stopCellEditing();
    }


    public void cancelEditing() {
        cancelCellEditing();
    }
}

