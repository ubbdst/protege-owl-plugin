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

package edu.stanford.smi.protegex.owl.ui.explorer.filter;

import edu.stanford.smi.protege.util.DefaultRenderer;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Iterator;

/**
 * A JPanel that can be used to select the types of classes that shall be filtered
 * by a DefaultExplorerFilter.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ValidClassesPanel extends JPanel {

    private DefaultExplorerFilter filter;

    private JList list;

    public static final int PREFERRED_WIDTH = 260;


    public ValidClassesPanel(DefaultExplorerFilter filter) {
        this.filter = filter;

        list = new JList(new ListItem[]{
                new ListItem(RDFSNamedClass.class, "Named classes", OWLIcons.PRIMITIVE_OWL_CLASS),
                new ListItem(OWLAllValuesFrom.class, "AllValuesFrom restrictions", OWLIcons.OWL_ALL_VALUES_FROM),
                new ListItem(OWLSomeValuesFrom.class, "SomeValuesFrom restrictions", OWLIcons.OWL_SOME_VALUES_FROM),
                new ListItem(OWLHasValue.class, "HasValue restrictions", OWLIcons.OWL_HAS_VALUE),
                new ListItem(OWLCardinality.class, "Cardinality restrictions", OWLIcons.OWL_CARDINALITY),
                new ListItem(OWLMinCardinality.class, "MinCardinality restrictions", OWLIcons.OWL_MIN_CARDINALITY),
                new ListItem(OWLMaxCardinality.class, "MaxCardinality restrictions", OWLIcons.OWL_MAX_CARDINALITY),
                new ListItem(OWLIntersectionClass.class, "Intersection classes", OWLIcons.OWL_INTERSECTION_CLASS),
                new ListItem(OWLUnionClass.class, "Union classes", OWLIcons.OWL_UNION_CLASS),
                new ListItem(OWLComplementClass.class, "Complement classes", OWLIcons.OWL_COMPLEMENT_CLASS),
                new ListItem(OWLEnumeratedClass.class, "Enumerated classes", OWLIcons.OWL_ENUMERATED_CLASS)
        });
        list.setCellRenderer(new DefaultRenderer() {
            public void load(Object o) {
                ListItem item = (ListItem) o;
                setMainIcon(item.icon);
                setMainText(item.name);
            }
        });
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateFilter();
            }
        });
        updateListSelection();
        OWLLabeledComponent lc = new OWLLabeledComponent("Show only Classes of Types", new JScrollPane(list));
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);

        setPreferredSize(new Dimension(PREFERRED_WIDTH, 260));
    }


    private int getListIndex(Class c) {
        for (int i = 0; i < list.getModel().getSize(); i++) {
            ListItem listItem = ((ListItem) list.getModel().getElementAt(i));
            if (listItem.type == c) {
                return i;
            }
        }
        return -1;
    }


    private void updateFilter() {
        filter.removeAllValidClasses();
        int[] sels = list.getSelectedIndices();
        for (int i = 0; i < sels.length; i++) {
            ListItem listItem = ((ListItem) list.getModel().getElementAt(sels[i]));
            filter.addValidClass(listItem.type);
        }
    }


    private void updateListSelection() {
        ListSelectionModel selectionModel = list.getSelectionModel();
        selectionModel.clearSelection();
        Iterator it = filter.getValidClasses().iterator();
        while (it.hasNext()) {
            Class c = (Class) it.next();
            int index = getListIndex(c);
            selectionModel.addSelectionInterval(index, index);
        }
    }


    private class ListItem {

        Class type;

        Icon icon;

        String name;


        ListItem(Class type, String name, String iconName) {
            this.type = type;
            this.icon = OWLIcons.getImageIcon(iconName);
            this.name = name;
        }
    }
}
