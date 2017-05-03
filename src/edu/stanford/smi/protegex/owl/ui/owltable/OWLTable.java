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

package edu.stanford.smi.protegex.owl.ui.owltable;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.code.OWLSymbolPanel;
import edu.stanford.smi.protegex.owl.ui.code.OWLTextAreaPanel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * A JTable in which one column displays an OWL expression in the syntax defined by
 * the OWLModel's OWLClassDisplay.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class OWLTable extends SymbolTable {

    private OWLTableAction copyAction = new AbstractOWLTableAction("Copy", OWLIcons.getCopyIcon()) {
        public void actionPerformed(ActionEvent e) {
            transferHandler.exportToClipboard(OWLTable.this, getClipboard(), TransferHandler.COPY);
        }


        public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
            return getOWLTableModel().getClass(rowIndex) != null;
        }
    };

    private OWLTableAction cutAction = new AbstractOWLTableAction("Cut", OWLIcons.getCutIcon()) {
        public void actionPerformed(ActionEvent e) {
            getTransferHandler().exportToClipboard(OWLTable.this, getClipboard(), TransferHandler.MOVE);
        }


        public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
            OWLTableModel tableModel = getOWLTableModel();
            return tableModel.isEditable() &&
                   tableModel.getClass(rowIndex) != null &&
                   tableModel.isDeleteEnabledFor(tableModel.getClass(rowIndex));
        }
    };

    private OWLTableAction pasteAction = new AbstractOWLTableAction("Paste", OWLIcons.getPasteIcon()) {
        public void actionPerformed(ActionEvent e) {
            Transferable trans = getClipboard().getContents(null);
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                transferHandler.importData(OWLTable.this, trans);
            }
        }


        public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
            OWLTableModel tableModel = getOWLTableModel();
            if (tableModel.isEditable()) {
                try {
                    Transferable trans = getClipboard().getContents(null);
                    if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        return tableModel.isAddEnabledAt(rowIndex);
                    }
                }
                catch (HeadlessException he) {
                    return false;
                }
            }
            return false;
        }
    };


    private OWLTableAction viewClsAction =
            new AbstractOWLTableAction("Edit/View named class...",
                                       OWLIcons.getViewIcon()) {
                public void actionPerformed(ActionEvent e) {
                    viewSelectedCls();
                }


                public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
                    return cls instanceof RDFSNamedClass;
                }
            };

    private OWLTableTransferHandler transferHandler;


    public OWLTable(OWLTableModel tableModel, OWLModel owlModel, boolean withIcon) {
        super(tableModel, owlModel, withIcon, new OWLSymbolPanel(owlModel, true, true));

        registerAction(editMultiLineAction);
        registerAction(viewClsAction);
        registerAction(editAnnotationsAction);
        registerActionSeparator();
        registerAction(copyAction);
        registerAction(cutAction);
        registerAction(pasteAction);
        registerActionSeparator();

        try {
            // Enable drag and drop, copy and paste
            setDragEnabled(OWLUI.isDragAndDropSupported(owlModel));
        }
        catch (HeadlessException he) {
            // in headless, we won't be dragging things around.
        }
        transferHandler = new DefaultOWLTableTransferHandler(owlModel);
        setTransferHandler(transferHandler);
    }


    protected ResourceRenderer createOWLFrameRenderer() {
        return new ResourceRenderer();
    }


    protected String editMultiLine(RDFResource input) {
        return OWLTextAreaPanel.showEditDialog(this, getOWLModel(), (RDFSClass) input);
    }


    public void enableActions(Collection actions) {
        OWLTableModel tableModel = getOWLTableModel();
        for (Iterator it = actions.iterator(); it.hasNext();) {
            OWLTableAction action = (OWLTableAction) it.next();
            if (action != null) {
                if (isEnabled()) {
                    if (getSelectionModel().getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
                        int sel = getSelectedRow();
                        if (sel >= 0 && sel < tableModel.getRowCount()) {
                            RDFSClass superclass = tableModel.getClass(sel);
                            boolean enabled = action.isEnabledFor(superclass, sel);
                            action.setEnabled(enabled);
                        }
                        else {
                            action.setEnabled(false);
                        }
                    }
                    else {  // Multiple selection allowed: Action must be enabled for all
                        int[] sels = getSelectedRows();
                        boolean en = sels.length > 0;
                        boolean one = false;
                        for (int i = 0; i < sels.length; i++) {
                            int sel = sels[i];
                            if (sel < tableModel.getRowCount()) {
                                one = true;
                                RDFSClass superclass = tableModel.getClass(sel);
                                if (!action.isEnabledFor(superclass, sel)) {
                                    en = false;
                                    break;
                                }
                            }
                        }
                        action.setEnabled(en && one);
                    }
                }
                else {
                    action.setEnabled(false);
                }
            }
        }
    }


    public OWLNamedClass getEditedCls() {
        return getOWLTableModel().getEditedCls();
    }


    protected Collection getNavigationMenuItems(RDFResource resource) {
        if (OWLClassesTab.getOWLClassesTab(this) != null) {
            Set set = new HashSet();
            RDFSClass rdfsClass = (RDFSClass) resource;
            rdfsClass.getNestedNamedClasses(set);
            return set;
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    public OWLTableModel getOWLTableModel() {
        return (OWLTableModel) getSymbolTableModel();
    }


    public Cls getSelectedCls() {
        OWLTableModel tableModel = getOWLTableModel();
        int index = getSelectedRow();
        if (index >= 0 && index < tableModel.getRowCount()) {
            return tableModel.getClass(index);
        }
        else {
            return null;
        }
    }


    protected String getToolTipText(RDFResource resource) {
        String str = OWLUI.getOWLToolTipText(resource);
        if (str != null && str.length() > 0) {
            return str;
        }
        return null;
    }


    protected void navigateTo(final RDFResource instance) {
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(this);
        if (tab != null && instance instanceof RDFSNamedClass) {
            tab.setSelectedCls((RDFSNamedClass) instance);
        }
    }


    public void setCls(OWLNamedClass cls) {
        if (getSymbolCellEditor() != null) {
            getSymbolCellEditor().cancelCellEditing();
        }
        hideSymbolPanel();
        getOWLTableModel().setCls(cls);
    }


    public void setValueAt(Object aValue, int row, int column) {
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(this);
        super.setValueAt(aValue, row, column);
        if (tab != null) {
            tab.ensureClassSelected(getEditedCls(), -1);
        }
    }


    private void viewSelectedCls() {
        editAnnotations();
    }
}
