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

package edu.stanford.smi.protegex.owl.ui.triplestore;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;

import javax.swing.table.AbstractTableModel;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class TripleStoreTableModel extends AbstractTableModel {

    public final static int COL_EDITABLE = 0;

    public final static int COL_ACTIVE = 1;

    public final static int COL_URI = 2;

    public final static int COL_COUNT = 3;

    private OWLModel owlModel;

    public static final String MAIN_FILE_NAME = "<Main File>";


    TripleStoreTableModel(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public Class getColumnClass(int columnIndex) {
        if (columnIndex == COL_ACTIVE || columnIndex == COL_EDITABLE) {
            return Boolean.class;
        }
        else {
            return String.class;
        }
    }


    public String getColumnName(int column) {
        if (column == COL_ACTIVE) {
            return "Active";
        }
        else if (column == COL_URI) {
            return "URI";
        }
        else if (column == COL_EDITABLE) {
            return "Editable";
        }
        else {
            return null;
        }
    }


    public int getRowCount() {
        return owlModel.getTripleStoreModel().getTripleStores().size() - 1;
    }


    public int getSelectedTripleStoreRow() {
        for (int i = 0; i < getRowCount(); i++) {
            if (Boolean.TRUE.equals(getValueAt(i, COL_ACTIVE))) {
                return i;
            }
        }
        return -1;
    }


    TripleStore getTripleStore(int row) {
        return (TripleStore) owlModel.getTripleStoreModel().getTripleStores().get(row + 1);
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        TripleStore tripleStore = getTripleStore(rowIndex);
        String uri = tripleStore.getName();
        if (columnIndex == COL_ACTIVE) {
            return Boolean.valueOf(tripleStore == owlModel.getTripleStoreModel().getActiveTripleStore());
        }
        else if (columnIndex == COL_URI) {
            if (rowIndex == 0) {
                return "<Main ontology>";
            }
            else {
                return uri;
            }
        }
        else if (columnIndex == COL_EDITABLE) {
            if (rowIndex == 0) {
                return Boolean.TRUE;
            }
            else {
                try {
                    RepositoryManager man = owlModel.getRepositoryManager();
                    URI ontURI = new URI(uri);
                    Repository rep = man.getRepository(ontURI);
                    if (rep != null) {
                        return Boolean.valueOf(rep.isWritable(ontURI));
                    }
                    else {
                        return Boolean.FALSE;
                    }
                }
                catch (URISyntaxException e) {
                    return Boolean.FALSE;
                }
            }
        }
        else {
            return null;
        }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == COL_ACTIVE) {
            return Boolean.TRUE.equals(getValueAt(rowIndex, COL_EDITABLE));
        }
        else if (columnIndex == COL_EDITABLE) {
            return false;
        }
        else {
            return false;
        }
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == COL_ACTIVE) {
            if (Boolean.TRUE.equals(aValue)) {
                TripleStore tripleStore = getTripleStore(rowIndex);
                TripleStoreUtil.switchTripleStore(owlModel, tripleStore);
                for (int row = 0; row < getRowCount(); row++) {
                    fireTableCellUpdated(row, columnIndex);
                }
            }
        }
    }
}
