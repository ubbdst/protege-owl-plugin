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
import edu.stanford.smi.protegex.owl.ui.components.ComponentUtil;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 14, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AnnotationsLangEditor extends AbstractCellEditor implements TableCellEditor {

    private AnnotationsTableCellHolder langHolder;

    private JComboBox comboBox;


    public AnnotationsLangEditor(OWLModel model, JTable table) {
        comboBox = ComponentUtil.createLangCellEditor(model, table);
        JPanel holderPanel = new JPanel(new BorderLayout());
        holderPanel.add(comboBox, BorderLayout.NORTH);
        holderPanel.setOpaque(false);
        holderPanel.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        langHolder = new AnnotationsTableCellHolder(holderPanel, BorderLayout.CENTER);
        langHolder.setOpaque(false);
	    comboBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			    stopCellEditing();
		    }
	    });
    }


    public Object getCellEditorValue() {
        if (comboBox.getSelectedItem() != null) {
            return comboBox.getSelectedItem().toString();
        }
        else {
            return null;
        }
    }


    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        comboBox.setSelectedItem(value);
        return langHolder;
    }
}

