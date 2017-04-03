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

package edu.stanford.smi.protegex.owl.inference.protegeowl.log;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;

public class OWLPropertyLogRecord extends ReasonerLogRecord {

	private RDFProperty property;
	private JLabel label;

	public OWLPropertyLogRecord(RDFProperty prop, ReasonerLogRecord parent) {
		super(parent);
		this.property = prop;

		label = new JLabel(prop.getBrowserText());
		label.setIcon(ProtegeUI.getIcon(prop));
	}

	public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3,
			boolean arg4) {

		return label;
	}

	public Component getTreeCellRendererComponent(JTree arg0, Object arg1, boolean arg2,
			boolean arg3, boolean arg4, int arg5, boolean arg6) {
		return label;
	}

}
