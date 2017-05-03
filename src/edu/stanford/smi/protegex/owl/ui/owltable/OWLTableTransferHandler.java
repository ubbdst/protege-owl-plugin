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
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A TransferHandler for transferring parsable OWL expressions with an OWLTable.
 * Based on source code from Java Tutorial
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/example-1dot4/index.html#ExtendedDnDDemo
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class OWLTableTransferHandler extends TransferHandler {

    private boolean inTransaction = false;

    private OWLModel owlModel;

    protected int[] rows = null;


    public OWLTableTransferHandler(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    protected boolean addRow(OWLTableModel tableModel, RDFSClass clone, int index) {
        return tableModel.addRow(clone, index);
    }


    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        OWLTable table = (OWLTable) c;
        if (table.getOWLTableModel().isEditable()) {
            for (int i = 0; i < flavors.length; i++) {
                DataFlavor dataFlavor = flavors[i];
                if (DataFlavor.stringFlavor.equals(dataFlavor)) {
                    return true;
                }
            }
        }
        return false;
    }


    protected void cleanup(JComponent c, boolean remove) {
        rows = null;
        if (inTransaction) {
            inTransaction = false;
            owlModel.endTransaction();
        }
    }


    protected Transferable createTransferable(JComponent c) {
        JTable table = (JTable) c;
        int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
            return exportOWLClses(table);
        }
        return null;
    }


    protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == MOVE);
    }


    protected Transferable exportOWLClses(JComponent c) {
        JTable table = (JTable) c;
        OWLTableModel model = (OWLTableModel) table.getModel();
        rows = table.getSelectedRows();

        String str = "";
        for (int i = 0; i < rows.length; i++) {
            Cls cls = model.getClass(rows[i]);
            if (cls != null) {
                str += cls.getBrowserText();
                if (i < rows.length - 1) {
                    str += "\n";
                }
            }
            else {
                rows = null;
                return null;
            }
        }

        return new StringSelection(str);
    }


    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }


    protected OWLNamedClass getRootCls() {
        return owlModel.getOWLThingClass();
    }


    protected int importOWLClses(JComponent c, String clsesText) {

        OWLTable target = (OWLTable) c;
        OWLTableModel model = (OWLTableModel) target.getModel();
        int index = target.getSelectedRow();

        // if (rows != null && index >= rows[0] - 1 && index <= rows[rows.length - 1]) {
        if (rows != null && index >= rows[0] && index <= rows[rows.length - 1]) {
            rows = null;
            return -1;
        }

        if (index < 0) {
            index = model.getRowCount();
        }
        int result = index;
        try {
        	//TODO: Urgent! Check if transactions always get closed!!
            owlModel.beginTransaction("Drag and drop in classes/conditions");
            String[] values = clsesText.split("\n");
            for (int i = 0; i < values.length; i++) {
                String text = values[i];
                RDFSClass clone = owlModel.getOWLClassDisplay().getParser().parseClass(owlModel, text);
                boolean added = addRow(model, clone, index);
                if (added) {
                    target.setSelectedRow(clone);
                }
                else {
                    rows = null;
                    result = -1;
                }
                index++;
                if (!added && clone instanceof OWLAnonymousClass) {
                    clone.delete();  // Delete clone if add has failed
                }
            }
        }
        catch (Exception ex) {
            // OWLUI.handleError(ex);
        }
        finally {
            // owlModel.endTransaction(true);
            inTransaction = true;
        }
        return result;
    }


    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                String str = (String) t.getTransferData(DataFlavor.stringFlavor);
                return importOWLClses(c, str) >= 0;
            }
            catch (UnsupportedFlavorException ufe) {
            }
            catch (IOException ioe) {
            }
        }
        return false;
    }
}
