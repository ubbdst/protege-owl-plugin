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

package edu.stanford.smi.protegex.owl.ui.components.datarangefield;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DataRangeFieldComponent extends AbstractPropertyValuesComponent {

    private JComboBox comboBox;

    private Action deleteAction = new AbstractAction("Delete value", OWLIcons.getDeleteIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleDeleteAction();
        }
    };

    private ActionListener comboBoxListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            assignComboBoxValue();
        }
    };

    public DataRangeFieldComponent(RDFProperty predicate) {
    	this(predicate, null);
    }

    public DataRangeFieldComponent(RDFProperty predicate, String label) {
        this(predicate, label, false);
    }

    public DataRangeFieldComponent(RDFProperty predicate, String label, boolean isReadOnly) {
        super(predicate, label, isReadOnly);

        comboBox = new JComboBox();
        OWLLabeledComponent lc = new OWLLabeledComponent((label == null ? getLabel():label), comboBox);
        lc.addHeaderButton(deleteAction);

        add(BorderLayout.CENTER, lc);
        enabledCompListeners();
    }


    private void assignComboBoxValue() {
        Object value = comboBox.getSelectedItem();
        getSubject().setPropertyValue(getPredicate(), value);
    }


    private OWLDataRange getDataRange() {
        RDFProperty predicate = getPredicate();
        OWLDataRange dataRange = null;
        for (Iterator it = getSubject().getRDFTypes().iterator(); it.hasNext();) {
            RDFSNamedClass type = (RDFSNamedClass) it.next();
            if (type instanceof OWLNamedClass) {
                final OWLNamedClass namedClass = (OWLNamedClass) type;
                RDFResource allValuesFrom = namedClass.getAllValuesFrom(predicate);
                if (allValuesFrom instanceof OWLDataRange) {
                    dataRange = (OWLDataRange) allValuesFrom;
                    break;
                }
                RDFResource someValuesFrom = namedClass.getSomeValuesFrom(predicate);
                if (someValuesFrom instanceof OWLDataRange) {
                    dataRange = (OWLDataRange) someValuesFrom;
                    break;
                }
            }
        }
        if (dataRange == null) {
            RDFResource range = predicate.getRange(true);
            if (range instanceof OWLDataRange) {
                dataRange = (OWLDataRange) range;
            }
        }
        return dataRange;
    }


    private Collection getDataRangeValues() {
        if (getSubject() != null) {
            OWLDataRange dataRange = getDataRange();
            if (dataRange != null) {
                return dataRange.getOneOfValueLiterals();
            }
        }
        return Collections.EMPTY_LIST;
    }


    private void handleDeleteAction() {
        getSubject().setPropertyValue(getPredicate(), null);
    }


    @Override
	public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        disableCompListeners();
        updateActionState();
        Collection values = getDataRangeValues();
        Object[] items = values.toArray();
        comboBox.setModel(new DefaultComboBoxModel(items));
        updateComboBoxState();
        enabledCompListeners();
    }


    private void updateActionState() {
        deleteAction.setEnabled(getSubject() != null &&
                getSubject().getPropertyValue(getPredicate()) != null &&
                hasOnlyEditableValues());
    }


    private void updateComboBoxState() {
        comboBox.setEnabled(getSubject() != null && hasOnlyEditableValues());
    }


    public void valuesChanged() {
    	disableCompListeners();
        Object value = getObject();
        if (value != null &&
                !(value instanceof RDFSLiteral) &&
                !(value instanceof RDFResource)) {
            value = getOWLModel().createRDFSLiteral(value);
        }
        comboBox.setSelectedItem(value);
        updateActionState();
        updateComboBoxState();
        enabledCompListeners();
    }

    protected void enabledCompListeners() {
    	comboBox.addActionListener(comboBoxListener);
    }

    protected void disableCompListeners() {
    	comboBox.removeActionListener(comboBoxListener);
    }
}
