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

package edu.stanford.smi.protegex.owl.ui.properties.range;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.metaproject.MetaProjectConstants;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDataRangeComponent extends JComponent {

	private Action createAction = new AbstractAction("Create value...", OWLIcons.getAddIcon()) {
		public void actionPerformed(ActionEvent e) {        	
			createValue();
		}
		/*@Override
		public boolean isEnabled() {        	
			return datatype != null;
		}
		*/
	};

	private RDFSDatatype datatype;

	private Action deleteAction = new AbstractAction("Delete selected value...", OWLIcons.getDeleteIcon()) {
		public void actionPerformed(ActionEvent e) {
			deleteSelectedValue();
		}
	};

	private JList list;

	private DefaultListModel listModel;

	private OWLRangeWidget rangeWidget;


	public OWLDataRangeComponent(OWLRangeWidget rangeWidget) {
		this.rangeWidget = rangeWidget;
		listModel = new DefaultListModel();
		list = new JList(listModel);
		setLayout(new BorderLayout());
		LabeledComponent lc = new LabeledComponent("Allowed values", new JScrollPane(list));
		lc.addHeaderButton(createAction);
		lc.addHeaderButton(deleteAction);
		add(BorderLayout.CENTER, lc);
		deleteAction.setEnabled(false);
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateActions(true);
			}
		});
	}


	//FIXME: Not initialized correctly when browsing a different datatype. If "Any", you should not 
	//be able to add a value
	private void createValue() {
		if (datatype == null) {
			return;
		}    	

		String newValue = ProtegeUI.getModalDialogFactory().showInputDialog(this, "Enter a new " + 
				datatype.getBrowserText() + " literal", null);
		if (newValue != null) {
			RDFProperty property = rangeWidget.getEditedProperty();
			newValue = newValue.trim();
			if (newValue.length() > 0) {
				OWLModel owlModel = property.getOWLModel();
				try {
					owlModel.beginTransaction("Add to property " + property.getBrowserText() + " data range " + newValue, property.getName());
					OWLDataRange newDataRange = null;
					RDFSLiteral newLiteral = owlModel.createRDFSLiteral(newValue, datatype);
					if (property.getRange() instanceof OWLDataRange) {
						OWLDataRange dataRange = (OWLDataRange) property.getRange();
						java.util.List values = dataRange.getOneOfValueLiterals();
						RDFSLiteral[] newLiterals = new RDFSLiteral[values.size() + 1];
						Iterator it = values.iterator();
						for (int i = 0; it.hasNext(); i++) {
							RDFSLiteral oldValue = (RDFSLiteral) it.next();
							if (newLiteral.equals(oldValue)) {
								ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
										"The value " + newValue + " is already among the values.");
								return;
							}
							newLiterals[i] = oldValue;
						}
						newLiterals[newLiterals.length - 1] = newLiteral;
						newDataRange = owlModel.createOWLDataRange(newLiterals);
					}
					else {
						newDataRange = owlModel.createOWLDataRange(new RDFSLiteral[]{newLiteral});
					}
					property.setRange(newDataRange);
					owlModel.commitTransaction();
				} catch (Exception e) {
					owlModel.rollbackTransaction();
					OWLUI.handleError(owlModel, e);
				}
			}
		}
	}


	private void deleteSelectedValue() {
		int index = list.getSelectedIndex();
		if (index < 0) { return; }
		RDFProperty property = rangeWidget.getEditedProperty();
		OWLModel owlModel = property.getOWLModel();
		OWLDataRange oldDataRange = (OWLDataRange) property.getRange();
		java.util.List oldLiterals = oldDataRange.getOneOfValueLiterals();
		try {
			owlModel.beginTransaction("Remove from property " + property.getBrowserText() +
					" data range " + listModel.get(index), property.getName());
			if (oldLiterals.size() == 1) {
				property.setRange(null);
			}			
			else {
				oldLiterals.remove(index);
				RDFSLiteral[] newLiterals = (RDFSLiteral[]) oldLiterals.toArray(new RDFSLiteral[0]);
				property.setRange(owlModel.createOWLDataRange(newLiterals));
			}
			owlModel.commitTransaction();
		} catch (Exception e) {
			owlModel.rollbackTransaction();
			OWLUI.handleError(owlModel, e);
		}
	}


	public void refill() {
		listModel.clear();
		RDFProperty property = rangeWidget.getEditedProperty();
		if (property != null) {
			RDFResource range = property.getRange();
			if (range instanceof OWLDataRange) {
				OWLDataRange dataRange = (OWLDataRange) range;
				Collection values = dataRange.getOneOfValueLiterals();
				for (Iterator it = values.iterator(); it.hasNext();) {
					RDFSLiteral literal = (RDFSLiteral) it.next();
					listModel.addElement(literal);
				}
			}
		}
	}


	public void setDatatype(RDFSDatatype datatype) {
		this.datatype = datatype;
		listModel.clear();
	}


	public void setEditable(boolean editable) {
		list.setEnabled(editable);
		updateActions(editable);
	}


	private void updateActions(boolean editable) {
		editable = editable && RemoteClientFrameStore.isOperationAllowed(rangeWidget.getOWLModel(), 
		                                                                 OwlMetaProjectConstants.OPERATION_ONTOLOGY_TAB_WRITE);	
		createAction.setEnabled(editable);
		deleteAction.setEnabled(editable && list.getSelectedValue() != null);
	}

	@Override
	public void setEnabled(boolean enabled) {
		enabled = enabled && RemoteClientFrameStore.isOperationAllowed(rangeWidget.getOWLModel(),
		                                                               OwlMetaProjectConstants.OPERATION_ONTOLOGY_TAB_WRITE);
		createAction.setEnabled(enabled);
		deleteAction.setEnabled(enabled);
		setEditable(enabled);
		super.setEnabled(enabled);
	};
}
