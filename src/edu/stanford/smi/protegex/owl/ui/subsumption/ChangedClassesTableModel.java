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

package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * A TableModel containing all classes that are either inconsistent or
 * where the asserted subclasses differ from the computed subclasses.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ChangedClassesTableModel extends AbstractTableModel implements Disposable {
    private static final long serialVersionUID = 3094682908756274652L;

    public final static int COL_CLS = 0;

    public final static int COL_TEXT = 1;

    public final static int COL_COUNT = 2;

    private List<ChangedClassItem> items = new ArrayList<ChangedClassItem>();

    private Hashtable<OWLNamedClass, ChangedClassItem> itemsByCls 
            = new Hashtable<OWLNamedClass, ChangedClassItem>();

    private ModelListener modelListener = new ModelAdapter() {
        public void classDeleted(RDFSClass cls) {
            refill();
        }
    };

    private OWLModel owlModel;


    public ChangedClassesTableModel(OWLModel owlModel) {
        this.owlModel = owlModel;
        refill();
        owlModel.addModelListener(modelListener);
    }


    void assertChange(OWLNamedClass cls) {
        ChangedClassItem changedClassItem = getOrCreateItem(cls);
        assertChange(changedClassItem);
    }


    private void assertChange(ChangedClassItem item) {
        item.assertChange();
        int index = this.items.indexOf(item);
        this.items.remove(index);
        itemsByCls.remove(item.getCls());
        fireTableRowsDeleted(index, index);
    }


    void assertChanges(int[] rows) {
        Collection is = new ArrayList();
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            ChangedClassItem item = items.get(row);
            OWLNamedClass cls = item.getCls();
            if (!OWLUtil.isInconsistent(cls) && cls.isEditable()) {
                is.add(item);
            }
        }
        assertChanges(is);
    }


    private void assertChanges(Collection<ChangedClassItem> items) {
    	try {
            owlModel.beginTransaction("Assert classification changes");
            for (Iterator<ChangedClassItem> it = items.iterator(); it.hasNext();) {
                ChangedClassItem item = it.next();
                assertChange(item);
            }
            owlModel.commitTransaction();			
		} catch (Exception e) {
			owlModel.rollbackTransaction();
			// TODO: check whether you need to rethrow exception
			RuntimeException re = new RuntimeException(e.getMessage());
			re.initCause(e);
			throw(re);
		}
    }


    public boolean contains(Cls cls) {
        return itemsByCls.containsKey(cls);
    }


    public void dispose() {
        owlModel.removeModelListener(modelListener);
    }


    private void fillItems(Collection assertedSubClses, Collection computedSubClses, Cls cls) {
        removeDuplicates(assertedSubClses, computedSubClses);
        for (Iterator ait = assertedSubClses.iterator(); ait.hasNext();) {
            OWLNamedClass namedCls = (OWLNamedClass) ait.next();
            if (!namedCls.isMetaclass()) {
                ChangedClassItem item = getOrCreateItem(namedCls);
                item.addRemovedSuperCls(cls);
            }
        }
        for (Iterator ait = computedSubClses.iterator(); ait.hasNext();) {
            OWLNamedClass namedCls = (OWLNamedClass) ait.next();
            if (!namedCls.isMetaclass()) {
                ChangedClassItem item = getOrCreateItem(namedCls);
                item.addAddedSuperCls(cls);
            }
        }
    }


    public String getChangeText(Cls cls) {
        ChangedClassItem item = (ChangedClassItem) itemsByCls.get(cls);
        if (item != null) {
            return item.toString();
        }
        else {
            return null;
        }
    }


    public Cls getCls(int row) {
        return (Cls) getValueAt(row, COL_CLS);
    }


    public Class getColumnClass(int columnIndex) {
        if (columnIndex == COL_CLS) {
            return Frame.class;
        }
        else {
            return String.class;
        }
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public String getColumnName(int column) {
        if (column == COL_CLS) {
            return "Class";
        }
        else {
            return "Changed direct superclasses";
        }
    }


    private ChangedClassItem getOrCreateItem(OWLNamedClass cls) {
        ChangedClassItem item = (ChangedClassItem) itemsByCls.get(cls);
        if (item == null) {
            item = new ChangedClassItem(cls);
            items.add(item);
            itemsByCls.put(cls, item);
        }
        return item;
    }


    public int getRowCount() {
        return items.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        ChangedClassItem item = items.get(rowIndex);
        if (columnIndex == COL_CLS) {
            return item.getCls();
        }
        else {
            return item.toString();
        }
    }

    public void refill() {
        refill(true);
    }

    @SuppressWarnings("unchecked")
	public void refill(boolean refillByCls) {

        items = new ArrayList<ChangedClassItem>();
        itemsByCls = new Hashtable<OWLNamedClass, ChangedClassItem>();

        for (Iterator it = owlModel.getChangedInferredClasses().iterator(); it.hasNext();) {
            OWLNamedClass cls = (OWLNamedClass) it.next();
            ChangedClassItem item = getOrCreateItem(cls);
            Collection asserted = new ArrayList(cls.getNamedSuperclasses());
            Collection inferred = new ArrayList(cls.getInferredSuperclasses());
            removeDuplicates(asserted, inferred);
            for (Iterator ait = asserted.iterator(); ait.hasNext();) {
                RDFSNamedClass superCls = (RDFSNamedClass) ait.next();
                if (!superCls.isMetaclass()) {
                    item.addRemovedSuperCls(superCls);
                }
            }
            for (Iterator ait = inferred.iterator(); ait.hasNext();) {
                RDFSNamedClass superCls = (RDFSNamedClass) ait.next();
                if (!superCls.isMetaclass()) {
                    item.addAddedSuperCls(superCls);
                }
            }
        }

        for (Iterator it = owlModel.getInconsistentClasses().iterator(); it.hasNext();) {
            OWLNamedClass cls = (OWLNamedClass) it.next();
            getOrCreateItem(cls);
        }
        if (refillByCls) {
            Collections.sort(items);
        }
        else {
            Collections.sort(items, new ChangedClassesChangeComporator());
        }
        fireTableDataChanged();
    }


    private void removeDuplicates(Collection a, Collection b) {
        Collection copyA = new ArrayList(a);
        a.removeAll(b);
        b.removeAll(copyA);
    }
}
