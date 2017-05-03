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

package edu.stanford.smi.protegex.owl.ui.testing;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.testing.OWLTestResult;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A TableModel displaying the results of an OWLTest run.
 * This has two columns - one for the anonymous class, and one for the named host class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTestResultsTableModel extends AbstractTableModel
        implements Disposable, OWLTestResultsTableModelColumns {

    private List items;

    private OWLModel owlModel;

    private ModelListener listener = new ModelAdapter() {
        public void classDeleted(RDFSClass cls) {
            deleteItemsWith(cls);
        }


        public void propertyDeleted(RDFProperty property) {
            deleteItemsWith(property);
        }


        public void individualDeleted(RDFResource resource) {
            deleteItemsWith(resource);
        }
    };


    public OWLTestResultsTableModel(OWLModel owlModel, Collection items) {
        this.items = new ArrayList(items);
        this.owlModel = owlModel;
        owlModel.addModelListener(listener);
    }


    private void deleteItemsWith(Frame frame) {
        for (int i = items.size() - 1; i >= 0; i--) {
            OWLTestResult item = getOWLTestResult(i);
            if (frame.equals(item.getHost())) {
                items.remove(i);
                fireTableRowsDeleted(i, i);
            }
        }
    }


    public void dispose() {
        owlModel.removeModelListener(listener);
    }


    public Class getColumnClass(int column) {
        if (column == COL_SOURCE) {
            return RDFResource.class;
        }
        else if (column == COL_MESSAGE) {
            return String.class;
        }
        else if (column == COL_TYPE) {
            return Icon.class;
        }
        return null;
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public String getColumnName(int column) {
        if (column == COL_SOURCE) {
            return "Source";
        }
        else if (column == COL_MESSAGE) {
            return "Test Result";
        }
        else if (column == COL_TYPE) {
            return "Type";
        }
        return null;
    }


    RDFResource getSource(int row) {
        return getOWLTestResult(row).getHost();
    }


    public OWLTestResult getOWLTestResult(int row) {
        return (OWLTestResult) items.get(row);
    }


    public int getRowCount() {
        return items.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == COL_MESSAGE) {
            return getOWLTestResult(rowIndex).getMessage();
        }
        else if (columnIndex == COL_SOURCE) {
            return getSource(rowIndex);
        }
        else if (columnIndex == COL_TYPE) {
            return getOWLTestResult(rowIndex).getIcon();
        }
        return null;
    }


    public void removeRow(int row) {
        items.remove(row);
        fireTableRowsDeleted(row, row);
    }


    /**
     * Saves the entries of this TableModel to a text file.
     *
     * @param file the target File
     * @return an error message or null if everything was fine
     */
    public String saveToFile(File file) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            for (Iterator it = items.iterator(); it.hasNext();) {
                OWLTestResult testResult = (OWLTestResult) it.next();
                pw.println(testResult.toString());
            }
            pw.close();
            return null;
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }


    public void setItems(Collection items) {
        this.items = new ArrayList(items);
        fireTableDataChanged();
    }
}
