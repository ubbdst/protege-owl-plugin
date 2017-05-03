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

package edu.stanford.smi.protegex.owl.ui.components.annotations;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.View;
import java.awt.*;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AnnotationsValueRenderer implements TableCellRenderer {

    private JTextArea textArea;

    private JLabel label;

    private JLabel langLabel;

    public static final int EXTRA_SPACING = 4;

    private AnnotationsTableCellHolder resourceHolder;

    private AnnotationsTableCellHolder plainTextPropertyValHolder;

    private AnnotationsTableCellHolder langHolder;

    public AnnotationsValueRenderer() {
        super();
        textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setFocusable(true);
        textArea.setOpaque(false);
        plainTextPropertyValHolder = new AnnotationsTableCellHolder(textArea, BorderLayout.CENTER);
        label = new JLabel();
        label.setOpaque(false);
	    if(System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
		    // Adjust border on label due to some silly windows 'feature'
		    label.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
	    }
	    resourceHolder = new AnnotationsTableCellHolder(label, BorderLayout.NORTH);
        langLabel = new JLabel();
        langHolder = new AnnotationsTableCellHolder(langLabel, BorderLayout.NORTH);
    }


    public Component getTableCellRendererComponent(JTable table,
                                                   Object o,
                                                   boolean selected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int col) {
        // Ensure that the row is the correct height.  We want to adjust the
	    // row height if it hasn't been adjusted by the cell editor, so check
	    // to ensure that the value of the property isn't being edited.
        if ((table.getEditingRow() == row && table.getEditingColumn() == AnnotationsTableModel.COL_VALUE) == false) {
            int rowHeight = getRowHeight(table, row);
            if (table.getRowHeight(row) != rowHeight) {
                table.setRowHeight(row, rowHeight);
            }
        }
        if (col == AnnotationsTableModel.COL_PROPERTY) {
            return getResourceComponent((RDFResource) o, selected, hasFocus);
        }
        else if (col == AnnotationsTableModel.COL_VALUE) {
            if (o instanceof RDFResource) {
                return getResourceComponent((RDFResource) o, selected, hasFocus);
            }
            else {
                textArea.setText(o != null ? o.toString() : "");
                plainTextPropertyValHolder.setColors(selected, hasFocus);
                return plainTextPropertyValHolder;
            }
        }
        else {
            langLabel.setText(o != null ? o.toString() : "");
            langHolder.setColors(selected, hasFocus);
            return langHolder;
        }
    }


    private JComponent getResourceComponent(RDFResource resource, boolean selected, boolean focused) {
        label.setText(resource.getBrowserText());
        label.setIcon(ProtegeUI.getIcon(resource));
        resourceHolder.setColors(selected, focused);
        return resourceHolder;
    }


    private int getRowHeight(JTable table,
                             int row) {
        Object val = table.getValueAt(row, AnnotationsTableModel.COL_VALUE);
        if (val instanceof String) {
            String text = val.toString();
            textArea.setText(text);
            View v = textArea.getUI().getRootView(textArea);
            v.setSize(table.getColumnModel().getColumn(AnnotationsTableModel.COL_VALUE).getWidth(), Integer.MAX_VALUE);
            int height = (int) v.getPreferredSpan(View.Y_AXIS) + 4;
            if (height < table.getRowHeight()) {
                height = table.getRowHeight();
            }
            return height;
        }
        return table.getRowHeight();
    }


}

