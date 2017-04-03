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

package edu.stanford.smi.protegex.owl.ui.metadata;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.components.annotations.AnnotationsTableModel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.HTMLEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A property widget that can be used to edit a value of a (multi-valued) annotation
 * property.  This is usually used in conjunction with the AnnotationsWidget:
 * The currently selected Annotation property is activated here, so that the
 * user can edit and view the value in a multi-line editor.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AnnotationTextAreaWidget extends AbstractPropertyWidget {

    /**
     * The currently edited annotation property (default: rdfs:comment)
     */
    private RDFProperty annotationProperty;

    private Action editHTMLAction = new AbstractAction("Edit as HTML...", OWLIcons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            editAsHTML();
        }
    };


    private PropertyValueListener valueListener = new PropertyValueAdapter() {
        public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues) {
            if (property.equals(annotationProperty)) {
                resetEditedValue();
            }
        }
    };

    private OWLLabeledComponent labeledComponent;

    private Object oldValue;

    private JTextArea textArea;


    private void assignCurrentValue() {
        final RDFResource resource = getEditedResource();
        if (resource != null && annotationProperty != null) {
            String text = textArea.getText();
            if (!text.equals(oldValue)) {
                assignValue(text, resource);
            }
        }
        else {
            oldValue = null;
        }
    }


    private void assignValue(String text, final RDFResource instance) {
        Object newValue = null;
        if (oldValue instanceof RDFSLiteral) {
            newValue = getOWLModel().createRDFSLiteralOrString(text, ((RDFSLiteral) oldValue).getLanguage());
        }
        else {
            newValue = text;
        }
        if (!getEditedResource().getPropertyValues(annotationProperty).contains(newValue)) {
            if (!AnnotationsTableModel.isInvalidXMLLiteral(getRDFProperty(), newValue)) {
                Collection oldValues = instance.getPropertyValues(annotationProperty);
                List newValues = new ArrayList(oldValues);
                int index = newValues.indexOf(oldValue);
                if (text.length() > 0) {
                    if (index >= 0) {
                        newValues.set(index, newValue);
                    }
                    else {
                        newValues.add(newValue);
                    }
                    instance.setPropertyValues(annotationProperty, newValues);
                    oldValue = newValue;
                }
            }
        }
    }


    public void dispose() {
        super.dispose();
        unregisterFrameListener();
    }


    private void editAsHTML() {
        if (XMLSchemaDatatypes.isXMLLiteralSlot(getRDFProperty())) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(getOWLModel(),
                    "This HTML editor does not support XMLLiterals yet.");
        }
        else {
            String oldText = textArea.getText();
            String newText = HTMLEditorPanel.show(this, oldText,
                    "Edit " + getRDFProperty().getBrowserText() + " at " + getEditedResource().getBrowserText());
            if (newText != null && !oldText.equals(newText)) {
                assignValue(newText, getEditedResource());
            }
        }
    }


    public void initialize() {
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                assignCurrentValue();
            }
        });
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    getParent().requestFocus();
                }
            }
        });
        labeledComponent = new OWLLabeledComponent(RDFSNames.Slot.COMMENT, new JScrollPane(textArea));
        labeledComponent.addHeaderButton(editHTMLAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, labeledComponent);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return slot.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(RDFSNames.Slot.COMMENT);
    }


    public void resetEditedValue() {
        OWLDatatypeProperty commentProperty = getOWLModel().getRDFSCommentProperty();
        String value = null;
        String defaultLanguage = getOWLModel().getDefaultLanguage();
        String languageStr = "" + defaultLanguage;
        Object v = null;
        for (Iterator it = getEditedResource().getPropertyValues(commentProperty).iterator(); it.hasNext();) {
            Object comment = (Object) it.next();
            String lang = comment instanceof RDFSLiteral ?
                    ((RDFSLiteral) comment).getLanguage() : null;
            if (languageStr.equals("" + lang)) {
                value = comment instanceof RDFSLiteral ?
                        ((RDFSLiteral) comment).getString() : comment.toString();
                v = comment;
                break;
            }
        }
        setEditedValueInternal(commentProperty, value, defaultLanguage, v);
    }


    public void setEditedValue(OWLDatatypeProperty property, String value, String language, Object v) {
        assignCurrentValue();
        setEditedValueInternal(property, value, language, v);
    }

    /* 
     * WARNING!
     *    See OWLModel.getProtegeReadOnlyProperty javadoc for explanation of protege:readOnly property.
     */
    private void setEditedValueInternal(OWLDatatypeProperty property,
                                        String value, String language, Object v) {
        if (value == null) {
            value = "";
        }

        annotationProperty = property;
        String label = property.getBrowserText();
        if (language != null && language.length() > 0) {
            label += " (" + language + ")";
        }
        labeledComponent.setHeaderLabel(label);
        oldValue = property.getOWLModel().createRDFSLiteralOrString(value, language);
        textArea.setText(value);

        boolean b = false;
        if (!property.isReadOnly()) {
            if (v == null) {
                b = true;
            }
            else {
                TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
                b = tsm.isActiveTriple(getEditedResource(), property, v);
            }
        }
        textArea.setEditable(b);
        editHTMLAction.setEnabled(b);
    }


    public void setInstance(Instance newInstance) {
        unregisterFrameListener();
        assignCurrentValue();
        super.setInstance(newInstance);
        if (newInstance instanceof RDFResource) {
            ((RDFResource) newInstance).addPropertyValueListener(valueListener);
        }
        resetEditedValue();
    }


    private void unregisterFrameListener() {
        if (getEditedResource() != null) {
            getEditedResource().removePropertyValueListener(valueListener);
        }
    }
}
