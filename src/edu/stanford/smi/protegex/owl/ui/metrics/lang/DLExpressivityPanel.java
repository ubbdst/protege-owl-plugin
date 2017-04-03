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

package edu.stanford.smi.protegex.owl.ui.metrics.lang;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.util.DLExpressivityChecker;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLExpressivityPanel extends JPanel {

	private OWLModel owlModel;

	public DLExpressivityPanel(OWLModel owlModel) {
		this.owlModel = owlModel;
		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout(12, 12));
		DLExpressivityChecker checker = new DLExpressivityChecker(owlModel);
		checker.check();
		JPanel holder = new JPanel(new BorderLayout(7, 7));
		holder.add(new JLabel("The DL expressivity of this ontology is:"), BorderLayout.NORTH);
		ArrayList list = new ArrayList(checker.getDL());
		DLNamePanel DLNamePanel = new DLNamePanel(list);
		DLNamePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		holder.add(DLNamePanel, BorderLayout.SOUTH);
		add(holder, BorderLayout.NORTH);
		JTable table = new JTable(new DLExpressivityTableModel(list));
		table.setRowHeight(55);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getColumnModel().getColumn(DLExpressivityTableModel.SYMBOL_COLUMN).setPreferredWidth(60);
		table.getColumnModel().getColumn(DLExpressivityTableModel.EXPLANATION_COLUMN).setPreferredWidth(600);
		table.getColumnModel().getColumn(DLExpressivityTableModel.EXPLANATION_COLUMN).setCellRenderer(new ExplanationRenderer());
		table.setShowGrid(true);
		table.setGridColor(Color.LIGHT_GRAY);
		add(new JScrollPane(table));
	}

	private class ExplanationRenderer implements TableCellRenderer {

		private JTextArea textArea;

		public ExplanationRenderer() {
			textArea = new JTextArea();
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);
		}

		public Component getTableCellRendererComponent(JTable table,
		                                               Object value,
		                                               boolean isSelected,
		                                               boolean hasFocus,
		                                               int row,
		                                               int column) {
			textArea.setText(value != null ? value.toString() : "");
			return textArea;
		}
	}
}

