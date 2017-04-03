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

package edu.stanford.smi.protegex.owl.ui.properties.types;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

/**
 * A SlotWidget to edit additional RDF types of an RDFProperty
 * (e.g., FunctionalProperty, TransitiveProperty).
 * The widget basically consists of a stack of checkboxes where
 * the single types can be switched on or off.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractPropertyTypesWidget extends AbstractPropertyWidget {

    private TypeCheckBox[] checkBoxes;

    private class TypeCheckBox extends JCheckBox {

        private RDFSNamedClass type;


        TypeCheckBox(RDFSNamedClass type) {
            String name = type.getLocalName();
            setText(name.substring(0, name.length() - "Property".length()));
            this.type = type;
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addOrRemoveType();
                }
            });
        }


        private void addOrRemoveType() {
            RDFProperty property = getEditedProperty();
            if (isSelected() && !property.hasProtegeType(type)) {
                if (isValidChange(type, true)) {
                    property.addProtegeType(type);
                    postProcessChange(type);
                }
                else {
                    setSelected(false);
                }
            }
            else if (!isSelected() && property.hasProtegeType(type)) {
                if (isValidChange(type, false)) {
                    property.removeProtegeType(type);
                    postProcessChange(type);
                }
                else {
                    setSelected(true);
                }
            }
        }


        void updateSelection() {
            boolean selected = getEditedProperty().hasProtegeType(type);
            if (!selected && isPropagatedType(type)) {
                selected = getSuperpropertyWithType(type) != null;
            }
            setSelected(selected);
        }
    }


    private RDFProperty getEditedProperty() {
        return (RDFProperty) getEditedResource();
    }


    private RDFProperty getSuperpropertyWithType(RDFSNamedClass type) {
        for (Iterator it = getEditedProperty().getSuperproperties(true).iterator(); it.hasNext();) {
            RDFProperty superproperty = (RDFProperty) it.next();
            if (superproperty.hasProtegeType(type)) {
                return superproperty;
            }
        }
        return null;
    }


    private String getTypeLabel(RDFSNamedClass type) {
        return type.getName();
    }


    protected void initialize(RDFSNamedClass[] types) {
        checkBoxes = new TypeCheckBox[types.length];
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (int i = 0; i < types.length; i++) {
            RDFSNamedClass type = types[i];
            checkBoxes[i] = new TypeCheckBox(type);
            add(checkBoxes[i]);
            add(Box.createVerticalStrut(4));
        }
    }


    private boolean isPropagatedType(RDFSClass type) {
        return type.equals(type.getOWLModel().getOWLFunctionalPropertyClass()) ||
                type.equals(type.getOWLModel().getOWLInverseFunctionalPropertyClass());
    }


    protected boolean isValidChange(RDFSNamedClass type, boolean value) {
        if (!value && isPropagatedType(type)) {
            RDFProperty superproperty = getSuperpropertyWithType(type);
            if (superproperty != null) {
                ProtegeUI.getModalDialogFactory().showMessageDialog(
                        type.getOWLModel(),
                        "This property already has the " + getTypeLabel(type) + "\n" +
                                "super-property " + superproperty.getBrowserText() + " and therefore\n" +
                                "must also remain " + getTypeLabel(type) + ".", "Warning");
                return false;
            }
        }
        return true;
    }


    /**
     * Can be overloaded to perform operations after the type has changed.
     *
     * @param type the type that was added/removed
     */
    protected void postProcessChange(RDFSNamedClass type) {

        RDFProperty property = getEditedProperty();
        RDFProperty inverseProperty = property.getInverseProperty();
        if (inverseProperty != null) {
            RDFSNamedClass functional = type.getOWLModel().getOWLFunctionalPropertyClass();
            RDFSNamedClass inverseFunctional = type.getOWLModel().getOWLInverseFunctionalPropertyClass();
            if (type.equals(functional)) {
                if (property.hasRDFType(functional)) {
                    if (!inverseProperty.hasRDFType(inverseFunctional)) {
                        inverseProperty.addRDFType(inverseFunctional);
                    }
                }
                else {  // !functional
                    if (inverseProperty.hasRDFType(inverseFunctional)) {
                        inverseProperty.removeRDFType(inverseFunctional);
                    }
                }
            }
            else {
                if (property.hasRDFType(inverseFunctional)) {
                    if (!inverseProperty.hasRDFType(functional)) {
                        inverseProperty.addRDFType(functional);
                    }
                }
                else {  // !functional
                    if (inverseProperty.hasRDFType(functional)) {
                        inverseProperty.removeRDFType(functional);
                    }
                }
            }
        }

        //Collection subproperties = getEditedProperty().getSubproperties(true);
        //if (isPropagatedType(type) && getEditedProperty().hasRDFType(type)) {
        //    for (Iterator it = subproperties.iterator(); it.hasNext();) {
        //        RDFProperty subproperty = (RDFProperty) it.next();
        //        if (!subproperty.hasRDFType(type)) {
        //            subproperty.addRDFType(type);
        //        }
        //    }
        //}
    }


    public void setEditable(boolean b) {
        for (int i = 0; i < checkBoxes.length; i++) {
            boolean enabled = b;
            TypeCheckBox checkBox = checkBoxes[i];
            if (isPropagatedType(checkBox.type)) {
                Collection superproperties = getEditedProperty().getSuperproperties(true);
                for (Iterator it = superproperties.iterator(); it.hasNext();) {
                    RDFProperty superproperty = (RDFProperty) it.next();
                    if (superproperty.hasRDFType(checkBox.type)) {
                        enabled = false;
                        break;
                    }
                }
            }
            checkBox.setEnabled(enabled);
        }
    }


    public void setValues(Collection values) {
        updateCheckBoxes();
    }


    private void updateCheckBoxes() {
        for (int i = 0; i < checkBoxes.length; i++) {
            TypeCheckBox checkBox = checkBoxes[i];
            checkBox.updateSelection();
        }
    }
    
    public void setEnabled(boolean enabled) {
    	setEditable(enabled);
    	super.setEnabled(enabled);
    };
}
