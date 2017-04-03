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

package edu.stanford.smi.protegex.owl.ui.components.singleliteral;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.components.ComponentUtil;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractSingleLiteralComponent extends AbstractPropertyValuesComponent {

	private final static String UNDEFINED = "undefined";

	private JComboBox booleanComboBox;
	private JComboBox datatypeComboBox;
	private JTextComponent textComponent;
	private Component textComponentHolder;
	private JPanel mainPanel;

	private Action deleteAction = new AbstractAction("Delete value", OWLIcons.getDeleteIcon()) {
		public void actionPerformed(ActionEvent e) {
			handleDeleteAction();
		}
	};

	protected ActionListener booleanComboListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			assignBooleanComboBoxValue();
		}
	};

	protected ActionListener datatypeComboListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			assignTextFieldValue();
			updateTextFieldAlignment((RDFSDatatype) datatypeComboBox.getSelectedItem());
		}
	};

	protected FocusListener textCompFocusListener = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			assignTextFieldValue();
		}
	};

	protected KeyListener textCompKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				assignTextFieldValue();
			}
		}
	};


	protected Action viewAction = new AbstractAction("View/edit value...", OWLIcons.getViewIcon()) {
		public void actionPerformed(ActionEvent e) {
			handleViewAction();
		}
	};


	public AbstractSingleLiteralComponent(RDFProperty predicate) {
		this(predicate, null);
	}

	public AbstractSingleLiteralComponent(RDFProperty predicate, String label) {
		this(predicate, label, false);
	}

	public AbstractSingleLiteralComponent(RDFProperty predicate, String label, boolean isReadOnly) {
		super(predicate, label, isReadOnly);

		this.datatypeComboBox = ComponentUtil.createDatatypeComboBox(getOWLModel());
		int height = datatypeComboBox.getPreferredSize().height;
		datatypeComboBox.setPreferredSize(new Dimension(80, height));
		booleanComboBox = new JComboBox(new Object[]{UNDEFINED, Boolean.FALSE, Boolean.TRUE});
		textComponent = createTextComponent();

		OWLUI.addCopyPastePopup(textComponent);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(BorderLayout.EAST, datatypeComboBox);
		textComponentHolder = createTextComponentHolder(textComponent);
		mainPanel.add(BorderLayout.CENTER, textComponentHolder);

		OWLLabeledComponent lc = new OWLLabeledComponent((label == null ? getLabel():label), mainPanel);
		lc.addHeaderButton(viewAction);
		lc.addHeaderButton(deleteAction);
		if (textComponentHolder instanceof JScrollPane) {
			lc.setVerticallyStretchable(true);
		}
		add(lc);
		enableComponentListeners();
	}


	protected abstract JTextComponent createTextComponent();


	protected abstract Component createTextComponentHolder(JTextComponent textComponent);


	private void assignBooleanComboBoxValue() {
		Object sel = booleanComboBox.getSelectedItem();
		if (UNDEFINED.equals(sel)) {
			getSubject().setPropertyValue(getPredicate(), null);
		}
		else {
			getSubject().setPropertyValue(getPredicate(), sel);
		}
	}


	private void assignTextFieldValue() {
		Object oldValue = getSubject().getPropertyValue(getPredicate());
		String text = textComponent.getText().trim();
		Object newValue = null;
		if (text.length() > 0) {
			RDFSDatatype datatype = getDatatype();
			if (datatype == null) {
				datatype = getOWLModel().getXSDstring();
			}
			if (getOWLModel().getXSDstring().equals(datatype)) {
				String language = null;
				if (oldValue instanceof RDFSLiteral) {
					RDFSLiteral oldLiteral = (RDFSLiteral) oldValue;
					if (oldLiteral.getLanguage() != null) {
						language = oldLiteral.getLanguage();
						newValue = getOWLModel().createRDFSLiteral(text, language);
					}
					else {
						newValue = text;
					}
				}
				else {
					newValue = text;
				}
			}
			else {
				newValue = getOWLModel().createRDFSLiteral(text, datatype);
			}
		}
		if (newValue == null) {
			getSubject().setPropertyValue(getPredicate(), null);
		}
		else {
			newValue = DefaultRDFSLiteral.getPlainValueIfPossible(newValue);
			Collection oldValues = getSubject().getPropertyValues(getPredicate(), true);
			if (!oldValues.contains(newValue)) {
				getSubject().setPropertyValue(getPredicate(), newValue);
			}
		}
	}


	private RDFSDatatype getDatatype() {
		return (RDFSDatatype) datatypeComboBox.getSelectedItem();
	}


	protected JTextComponent getTextComponent() {
		return textComponent;
	}


	private void handleDeleteAction() {
		textComponent.setText("");
		assignTextFieldValue();
		resetDatatypeComboBox();
	}


	private void handleViewAction() {
		Object object = getObject();
		PropertyValueEditor editor = getEditor(object);
		if (editor != null) {
			if (getObject() == null) {
				object = editor.createDefaultValue(getSubject(), getPredicate());
			}
			Object newValue = editor.editValue(null, getSubject(), getPredicate(), object);
			if (newValue != null) {
				getSubject().setPropertyValue(getPredicate(), newValue);
			}
		}
	}


	private boolean hasOnlyActiveValues() {
		if (getSubject() != null) {
			Collection values = getSubject().getPropertyValues(getPredicate());
			TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
			for (Iterator it = values.iterator(); it.hasNext();) {
				Object value = it.next();
				if (!tsm.isActiveTriple(getSubject(), getPredicate(), value)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}


	private boolean isRangeDefined() {
		final RDFResource resource = getSubject();
		final RDFProperty property = getPredicate();
		return ComponentUtil.isRangeDefined(resource, property);
	}


	private void resetDatatypeComboBox() {
		RDFResource range = getPredicate().getRange();
		if (range instanceof RDFSDatatype) {
			setDatatypeComboBoxItem(range);
			updateTextFieldAlignment((RDFSDatatype) range);
		}
		else {
			Collection types = getSubject().getRDFTypes();
			for (Iterator it = types.iterator(); it.hasNext();) {
				RDFSClass type = (RDFSClass) it.next();
				if (type instanceof OWLNamedClass) {
					OWLNamedClass namedClass = (OWLNamedClass) type;
					RDFResource allValuesFrom = namedClass.getAllValuesFrom(getPredicate());
					if (allValuesFrom instanceof RDFSDatatype) {
						setDatatypeComboBoxItem(allValuesFrom);
						updateTextFieldAlignment((RDFSDatatype) allValuesFrom);
						return;
					}
					RDFResource someValuesFrom = namedClass.getSomeValuesFrom(getPredicate());
					if (someValuesFrom instanceof RDFSDatatype) {
						setDatatypeComboBoxItem(someValuesFrom);
						updateTextFieldAlignment((RDFSDatatype) someValuesFrom);
						return;
					}
				}
			}
		}
	}


	private void setDatatypeComboBoxItem(RDFResource range) {
		datatypeComboBox.setSelectedItem(range);
	}


	@Override
	public void setSubject(RDFResource subject) {
		super.setSubject(subject);
		updateComboBoxVisibility();
		boolean editable = !isReadOnly() && subject.getHasValuesOnTypes(getPredicate()).isEmpty();
		if (!getOWLModel().getProject().isMultiUserClient()) {
			editable = editable && hasOnlyEditableValues();
			editable = editable && hasOnlyActiveValues();
		}
		textComponent.setEditable(editable);
		booleanComboBox.setEnabled(editable);
		datatypeComboBox.setEnabled(editable && !isRangeDefined());
	}


	private void updateActionStatus() {
		boolean editable = !isReadOnly(); //&& hasOnlyEditableValues();
		deleteAction.setEnabled(getSubject() != null &&
				getSubject().getPropertyValue(getPredicate()) != null &&
				editable);
		final Object object = getObject();		
		viewAction.setEnabled(true);
		
		if (datatypeComboBox != null) {
			datatypeComboBox.setEnabled(!isReadOnly());
		}

		if (booleanComboBox != null) {
			booleanComboBox.setEnabled(!isReadOnly());
		}

		if (textComponent != null) {
			textComponent.setEditable(!isReadOnly());
		}

	}


	private void updateComboBoxVisibility() {
		final RDFProperty property = getPredicate();
		RDFSDatatype datatype = null;
		Collection types = getSubject().getRDFTypes();
		for (Iterator it = types.iterator(); it.hasNext();) {
			RDFSClass type = (RDFSClass) it.next();
			if (type instanceof OWLNamedClass) {
				OWLNamedClass namedClass = (OWLNamedClass) type;
				RDFResource allValuesFrom = namedClass.getAllValuesFrom(property);
				if (allValuesFrom instanceof RDFSDatatype) {
					datatype = (RDFSDatatype) allValuesFrom;
				}
			}
		}
		if (datatype == null) {
			RDFResource range = property.getRange();
			if (range instanceof RDFSDatatype) {
				datatype = (RDFSDatatype) range;
			}
		}
		else {
			if (datatype.getBaseDatatype() != null) {
				datatype = datatype.getBaseDatatype();
			}
		}
		OWLModel owlModel = getOWLModel();
		boolean defaultDatatype =
			owlModel.getXSDboolean().equals(datatype) ||
			owlModel.getXSDstring().equals(datatype) ||
			owlModel.getXSDint().equals(datatype) ||
			owlModel.getXSDfloat().equals(datatype);
		if (defaultDatatype) {
			mainPanel.remove(datatypeComboBox);
		}
		else {
			mainPanel.add(BorderLayout.EAST, datatypeComboBox);
		}
	}


	private void updateDatatypeComboBox(Object value) {
		RDFSDatatype type = getOWLModel().getRDFSDatatypeOfValue(value);
		if (type != null) {
			setDatatypeComboBoxItem(type);
			updateTextFieldAlignment(type);
		}
		else {
			updateDatatypeComboBox(getOWLModel().getXSDstring());
		}
	}


	private void updateTextFieldAlignment(RDFSDatatype type) {
		if (getOWLModel().getXSDboolean().equals(type)) {
			if (booleanComboBox.getParent() != mainPanel) {
				mainPanel.remove(textComponentHolder);
				mainPanel.add(BorderLayout.CENTER, booleanComboBox);
			}
		}
		else {
			final boolean numericDatatype = type.isNumericDatatype();
			updateTextFieldAlignment(numericDatatype);
			if (textComponentHolder.getParent() != mainPanel) {
				mainPanel.remove(booleanComboBox);
				mainPanel.add(BorderLayout.CENTER, textComponentHolder);
			}
		}
	}


	protected void updateTextFieldAlignment(final boolean numericDatatype) { }


	public void valuesChanged() {
		disableComponentListeners();
		Collection values = new ArrayList(getObjects(true));
		Collection hasValues = getSubject().getHasValuesOnTypes(getPredicate());
		for (Iterator it = hasValues.iterator(); it.hasNext();) {
			Object hasValue = it.next();
			if (!values.contains(hasValue)) {
				values.add(hasValue);
			}
		}
		Iterator it = values.iterator();
		if (it.hasNext()) {
			Object value = it.next();
			textComponent.setText("" + value);
			if (value instanceof Boolean) {
				booleanComboBox.setSelectedItem(value);
			}
			updateDatatypeComboBox(value);
		}
		else {
			textComponent.setText("");
			booleanComboBox.setSelectedItem(UNDEFINED);
			resetDatatypeComboBox();
		}
		updateActionStatus();
		enableComponentListeners();
	}

	protected void enableComponentListeners() {
		booleanComboBox.addActionListener(booleanComboListener);
		datatypeComboBox.addActionListener(datatypeComboListener);
		textComponent.addFocusListener(textCompFocusListener);
		textComponent.addKeyListener(textCompKeyListener);
	}

	protected void disableComponentListeners() {
		booleanComboBox.removeActionListener(booleanComboListener);
		datatypeComboBox.removeActionListener(datatypeComboListener);
		textComponent.removeFocusListener(textCompFocusListener);
		textComponent.removeKeyListener(textCompKeyListener);
	}
}
