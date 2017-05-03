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

package edu.stanford.smi.protegex.owl.ui.conditions;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableTransferHandler;

import javax.swing.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;

/**
 * An OWLTableTransferHandler that does not assume that the class maintain their
 * rows after changing something.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ConditionsTableTransferHandler extends OWLTableTransferHandler
        implements ConditionsTableConstants {

    private String movedClsText;

    private String movedClsBlock;

    private boolean movedClsWasDefinition;

    private OWLNamedClass movedNamedCls;

    private boolean movedNamedClsHadThingAsSuperClass;

    private int recentTargetRow;

    private int recentTargetType;

    /**
     * Debugging only
     */
    public Transferable recentTransferable;


    public ConditionsTableTransferHandler(OWLModel owlModel) {
        super(owlModel);
    }


    protected boolean addRow(OWLTableModel tableModel, RDFSClass clone, int index) {
        recentTargetType = ((ConditionsTableModel) tableModel).getType(index);
        return ((ConditionsTableModel) tableModel).addRowAllowMove(clone, index);
    }


    public void cleanup(JComponent c, boolean remove) {   // public for testing only
        OWLTable source = (OWLTable) c;
        OWLClassesTab tab = ((ConditionsTable) source).getOWLClsesTab();
        if (remove && rows != null) {
            cleanup(source);
            movedClsBlock = null;
            movedClsText = null;
            recentTargetType = -10;
        }
        ((ConditionsTable) source).ensureEditedClassSelectedInExplorer(tab);
        super.cleanup(c, remove);
        movedNamedCls = null;
    }


    private void cleanup(OWLTable source) {
        ConditionsTableModel tableModel = (ConditionsTableModel) source.getModel();
        String oldBrowserText = null;
        int oldSelection = source.getSelectedRow();
        if (oldSelection >= 0 && tableModel.getClass(oldSelection) != null) {
            oldBrowserText = tableModel.getClass(oldSelection).getBrowserText();
        }
        int index = getDeleteRowIndex(tableModel);
        if (index >= 0) {
            if (tableModel.isDefinition(index) &&
                    tableModel.getClass(index) instanceof OWLNamedClass &&
                    recentTargetType == TYPE_SUPERCLASS &&
                    tableModel.getDefinition(index) == null) {
                // Special case: Single named equivalent class moved to superclasses
                final Cls cls = tableModel.getClass(index);
                cls.removeDirectSuperclass(tableModel.getEditedCls());
            }
            else {
                tableModel.deleteRow(index, true);
            }
            if (movedNamedCls != null && recentTargetType == TYPE_SUPERCLASS) {
                tableModel.getEditedCls().addSuperclass(movedNamedCls);
                if (!movedNamedClsHadThingAsSuperClass) {
                    tableModel.getEditedCls().removeSuperclass(getRootCls());
                }
            }
            if (movedNamedCls != null && recentTargetRow == 0) {
                tableModel.getEditedCls().addEquivalentClass(movedNamedCls);
                if (!movedNamedClsHadThingAsSuperClass) {
                    tableModel.getEditedCls().removeSuperclass(getRootCls());
                }
            }
        }
        if (oldBrowserText != null) {
            source.setSelectedRow(oldBrowserText);
        }
    }


    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        ConditionsTable table = (ConditionsTable) comp;
        ConditionsTableModel tableModel = (ConditionsTableModel) table.getModel();
        int sel = table.getSelectedRow();
        if (sel >= 0 && tableModel.getClass(sel) instanceof OWLNamedClass && tableModel.isDefinition(sel)) {
            movedNamedCls = (OWLNamedClass) tableModel.getClass(sel);
            movedNamedClsHadThingAsSuperClass = tableModel.getEditedCls().getSuperclasses(false).contains(getRootCls());
        }
        super.exportAsDrag(comp, e, action);
    }


    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        movedNamedCls = null;
        super.exportToClipboard(comp, clip, action);
    }


    protected Transferable exportOWLClses(JComponent c) {
        JTable table = (JTable) c;
        ConditionsTableModel tableModel = (ConditionsTableModel) table.getModel();
        int row = table.getSelectedRow();
        if (row >= 0 &&
                tableModel.getClass(row) != null &&
                tableModel.getType(row) != TYPE_INHERITED) {
            movedClsText = tableModel.getClass(row).getBrowserText();
            movedClsBlock = tableModel.getBlockText(tableModel.getType(row));
            movedClsWasDefinition = tableModel.isDefinition(row);
        }
        recentTransferable = super.exportOWLClses(c);
        return recentTransferable;
    }


    private int getDeleteRowIndex(ConditionsTableModel tableModel) {
        int expectedType = TYPE_SUPERCLASS;
        if (movedClsWasDefinition) {
            if (movedClsBlock == null) {
                return -1;  // Moved inherited to somewhere
            }
            // Find a block that matches the old block (syntactically)
            expectedType = TYPE_DEFINITION_BASE;
            while (!movedClsBlock.equals(tableModel.getBlockText(expectedType))) {
                expectedType++;
            }
        }
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Cls cls = tableModel.getClass(i);
            if (cls != null &&
                    tableModel.getType(i) == expectedType &&
                    cls.getBrowserText().equals(movedClsText)) {
                return i;
            }
        }
        return -1;
    }


    protected int importOWLClses(JComponent c, String clsesText) {
        ConditionsTable table = (ConditionsTable) c;
        recentTargetRow = table.getSelectedRow();
        OWLClassesTab tab = ((ConditionsTable) c).getOWLClsesTab();
        int result = super.importOWLClses(c, clsesText);
        ((ConditionsTable) c).ensureEditedClassSelectedInExplorer(tab);
        return result;
    }
}
