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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         19-Oct-2005
 */
public class FindResultsTableView extends AbstractFindResultsView {

    private JTable table;

    private ResultsViewModelFind find;

    protected FindResultsTableView(ResultsViewModelFind find, HostResourceDisplay hrd) {
        super(hrd);

        setLayout(new BorderLayout(6, 6));

        this.find = find;

        table = new JTable(find);

        TableColumn tc;
        TableCellRenderer ren = ResourceRenderer.createInstance();
        tc = table.getColumnModel().getColumn(FindResult.RESOURCE_NAME);
        tc.setCellRenderer(ren);
        tc = table.getColumnModel().getColumn(FindResult.PROPERTY_NAME);
        tc.setCellRenderer(ren);
        tc = table.getColumnModel().getColumn(FindResult.LANG);
        tc.setMinWidth(40);
        tc.setMaxWidth(40);

        JScrollPane scroller = new JScrollPane(table);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getViewport().setBackground(table.getBackground());

        add(scroller, BorderLayout.CENTER);
    }

    public RDFResource getSelectedResource() {
        int row = table.getSelectedRow();
        return (RDFResource) find.getElementAt(row);
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        if (d.height < 400) {
            d.height = 400;
        }
        return d;
    }

    public void addMouseListener(MouseListener l) {
        table.addMouseListener(l);
    }

    public void addKeyListener(KeyListener l) {
        table.addKeyListener(l);
    }

    public void requestFocus() {
        table.requestFocus();
    }
}
