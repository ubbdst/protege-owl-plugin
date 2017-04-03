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

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AnnotationsValueEditor extends AbstractCellEditor implements TableCellEditor {

    private JTextArea textArea;

    private AnnotationsTableCellHolder multiLineHolder;

    private JTextField textField;

    private AnnotationsTableCellHolder singleLineHolder;

    private JTextComponent textComponent;

    private Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");

    public static final int EDITING_MARGIN = 30;


    public AnnotationsValueEditor(OWLModel owlModel, JTable t) {

        textArea = new JTextArea();
        OWLUI.addCopyPastePopup(textArea);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_ENTER:
                        if (e.isControlDown()) {
                            stopCellEditing();
                            e.consume();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        cancelCellEditing();
                        break;
                }
            }
        });
        textArea.setFocusable(true);
        JScrollPane sp = new JScrollPane(textArea);
        sp.setBorder(focusBorder);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        multiLineHolder = new AnnotationsTableCellHolder(sp, BorderLayout.CENTER);
        textField = new JTextField();
        OWLUI.addCopyPastePopup(textField);
        singleLineHolder = new AnnotationsTableCellHolder(textField, BorderLayout.CENTER);
    }


    public Object getCellEditorValue() {
        return textComponent.getText().trim();
    }


    public Component getTableCellEditorComponent(final JTable table,
                                                 final Object value,
                                                 final boolean isSelected,
                                                 final int row,
                                                 final int column) {
        RDFProperty property = (RDFProperty) table.getValueAt(row, AnnotationsTableModel.COL_PROPERTY);
        if (AnnotationsTable.isMultiLineProperty(property)) {
            textArea.setText(value != null ? value.toString() : "");
            int rowHeight = getRowHeight(table, row);
            if (table.getRowHeight(row) != rowHeight) {
                table.setRowHeight(row, rowHeight);
            }
            textComponent = textArea;
            focusTextField();
            return multiLineHolder;
        }
        else {
            textField.setText(value != null ? value.toString() : "");
            textComponent = textField;
            focusTextField();
            return singleLineHolder;
        }
    }


    private void focusTextField() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textComponent.requestFocus();
            }
        });
    }


	public boolean isCellEditable(EventObject e) {
		// Only edit cell if the user has double
		// clicked it.
		if(e instanceof MouseEvent) {
			return ((MouseEvent) e).getClickCount() == 2;
		}
		else {
			return super.isCellEditable(e);
		}
	}


    private int getRowHeight(JTable table,
                             int row) {
        Object val = table.getValueAt(row, AnnotationsTableModel.COL_VALUE);
	    if(val == null) {
		    val = "";
	    }
        int preferredHeight = 0;
        View v = textArea.getUI().getRootView(textArea);
        v.setSize(table.getColumnModel().getColumn(AnnotationsTableModel.COL_VALUE).getWidth(), Integer.MAX_VALUE);
        preferredHeight = (int) v.getPreferredSpan(View.Y_AXIS) + EDITING_MARGIN;
        JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
        if (sp != null) {
            int maxHeight = sp.getViewport().getViewRect().height;
            if (preferredHeight > maxHeight) {
                preferredHeight = maxHeight;
            }
        }
        return preferredHeight;
    }


}

