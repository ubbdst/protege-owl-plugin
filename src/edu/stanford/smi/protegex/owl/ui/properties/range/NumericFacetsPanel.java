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

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSDatatype;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.AssigningTextField;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.Map;

/**
 * A JPanel to edit the XML Schema datatype facets for a numeric type.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class NumericFacetsPanel extends FacetsPanel implements ExInclusiveToggleAction.Callback {

    private ExInclusiveToggleAction maxAction;

    private JTextField maxTextField;

    private ExInclusiveToggleAction minAction;

    private JTextField minTextField;


    public NumericFacetsPanel(OWLRangeWidget rangeWidget) {
        super(rangeWidget);
        maxTextField = new AssigningTextField(new AssigningTextField.Assign() {
            public void assign(String value) {
                assignInterval(false);
            }
        });
        minTextField = new AssigningTextField(new AssigningTextField.Assign() {
            public void assign(String value) {
                assignInterval(false);
            }
        });
        maxTextField.setHorizontalAlignment(JTextField.RIGHT);
        minTextField.setHorizontalAlignment(JTextField.RIGHT);
        JToolBar mainPanel = ComponentFactory.createToolBar();
        maxAction = new ExInclusiveToggleAction("Toggle between ] and ), which mean <= or <",
                OWLIcons.XSD_MAX_EXCLUSIVE, OWLIcons.XSD_MAX_INCLUSIVE, this);
        minAction = new ExInclusiveToggleAction("Toggle between [ and (, which mean >= or >",
                OWLIcons.XSD_MIN_EXCLUSIVE, OWLIcons.XSD_MIN_INCLUSIVE, this);
        ComponentFactory.addToolBarButton(mainPanel, minAction);
        mainPanel.add(minTextField);
        mainPanel.add(new JLabel("  "));
        mainPanel.add(maxTextField);
        ComponentFactory.addToolBarButton(mainPanel, maxAction);
        OWLLabeledComponent lc = new OWLLabeledComponent("Range Interval (Min/Max)", mainPanel);
        add(BorderLayout.CENTER, lc);
    }


    public void assignInterval() {
        assignInterval(true);
    }


    public void assignInterval(boolean force) {
        RDFResource range = rangeWidget.getEditedProperty().getRange();
        if (range instanceof RDFSDatatype) {
            RDFSDatatype datatype = (RDFSDatatype) range;
            String newMinText = minTextField.getText();
            String newMaxText = maxTextField.getText();
            if (force || !newMaxText.equals(getMaxText(datatype)) || !newMinText.equals(getMinText(datatype))) {
                String expression = (minAction.isExclusive() ? "(" : "[") +
                        newMinText + "," + newMaxText +
                        (maxAction.isExclusive() ? ")" : "]");
                if (datatype.isAnonymous()) {
                    datatype = datatype.getBaseDatatype();
                }
                expression = datatype.getName() + expression;
                OWLModel owlModel = rangeWidget.getOWLModel();
                Map map = DefaultRDFSDatatype.parse(owlModel, expression);
                if (map.size() > 0) {
                    String name = owlModel.getNextAnonymousResourceName();
                    RDFSDatatype newDatatype = owlModel.createRDFSDatatype(name);
                    for (Iterator it = map.keySet().iterator(); it.hasNext();) {
                        RDFProperty property = (RDFProperty) it.next();
                        Object value = map.get(property);
                        newDatatype.setPropertyValue(property, value);
                    }
                    rangeWidget.setRange(newDatatype);
                }
                else {
                    rangeWidget.setRange(datatype);
                }
            }
        }
    }


    private String getMinText(RDFSDatatype datatype) {
        RDFSLiteral min = datatype.getMinInclusive();
        if (min == null) {
            min = datatype.getMinExclusive();
        }
        return min == null ? "" : min.toString();
    }


    private String getMaxText(RDFSDatatype datatype) {
        RDFSLiteral max = datatype.getMaxInclusive();
        if (max == null) {
            max = datatype.getMaxExclusive();
        }
        return max == null ? "" : max.toString();
    }


    public void setEditable(boolean value) {
        minAction.setEnabled(value);
        maxAction.setEnabled(value);
        minTextField.setEditable(value);
        maxTextField.setEditable(value);
    }


    public void update(RDFSDatatype datatype) {
        if (datatype.isAnonymous()) {
            String maxText = getMaxText(datatype);
            maxTextField.setText(maxText);
            String minText = getMinText(datatype);
            minTextField.setText(minText);
            minAction.setExclusive(datatype.getMinExclusive() != null);
            maxAction.setExclusive(datatype.getMaxExclusive() != null);
        }
        else {
            minTextField.setText("");
            maxTextField.setText("");
        }
    }
}
