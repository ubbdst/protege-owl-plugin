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

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLExpressivityTableModel extends AbstractTableModel {

	private List features;

	public static final int SYMBOL_COLUMN = 0;

	public static final int EXPLANATION_COLUMN = 1;

	public static final String [] COLUMNS = {"Symbol", "Explanation"};

	public DLExpressivityTableModel(List features) {
		this.features = features;
	}


	public int getRowCount() {
		return features.size();
	}


	public int getColumnCount() {
		return COLUMNS.length;
	}


	public Object getValueAt(int rowIndex,
	                         int columnIndex) {

		String s = (String) features.get(rowIndex);
		if(columnIndex == SYMBOL_COLUMN) {
			return ExpressivityIcons.getIcon(s);
		}
		else {
			return DLExpressivityExplanation.getExplanation(s);
		}
	}


	public String getColumnName(int column) {
		return COLUMNS[column];
	}


	public Class getColumnClass(int columnIndex) {
		if(columnIndex == SYMBOL_COLUMN) {
			return ImageIcon.class;
		}
		else {
			return String.class;
		}
	}
}

