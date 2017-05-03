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

package edu.stanford.smi.protegex.owl.ui.conditions;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.InferredModeWidget;

import java.awt.*;
import java.util.Collection;

/**
 * A MultiWidgetPropertyWidget showing asserted and inferred conditions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ConditionsWidget extends AbstractPropertyWidget implements InferredModeWidget, PropertyConditionsDisplay {

    private AbstractConditionsWidget currentWidget;


    public ConditionsWidget() {
        currentWidget = new AssertedConditionsWidget();
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, currentWidget);
    }


    public void displayRowsWithProperty(OWLProperty property) {
        currentWidget.displayRowsWithProperty(property);
    }


    /**
     * @deprecated
     */
    public AssertedConditionsWidget getAssertedConditionsWidget() {
        if (currentWidget instanceof AssertedConditionsWidget) {
            return (AssertedConditionsWidget) currentWidget;
        }
        else {
            return new AssertedConditionsWidget();
        }
    }


    public AbstractConditionsWidget getCurrentConditionsWidget() {
        return currentWidget;
    }


    /**
     * @deprecated
     */
    public InferredConditionsWidget getInferredConditionsWidget() {
        if (currentWidget instanceof InferredConditionsWidget) {
            return (InferredConditionsWidget) currentWidget;
        }
        else {
            return new InferredConditionsWidget();
        }
    }


    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }


    /**
     * Gets the selected row in the asserted conditions widget.
     *
     * @return the index of the selected row
     */
    public int getSelectedRow() {
        return currentWidget.getTable().getSelectedRow();
    }


    public void initialize() {
        currentWidget.initialize();
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(Model.Slot.DIRECT_SUPERCLASSES);
    }


    public static void selectProperty(OWLModel owlModel, Container p, RDFProperty selectedProperty) {
        while (p != null) {
            if (p instanceof ClsWidget) {
                ClsWidget clsWidget = (ClsWidget) p;
                SlotWidget widget = clsWidget.getSlotWidget(((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES));
                if (widget instanceof PropertyConditionsDisplay) {
                    ((PropertyConditionsDisplay) widget).displayRowsWithProperty((OWLProperty) selectedProperty);
                }
            }
            p = p.getParent();
        }
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        currentWidget.setInstance(newInstance);
    }


    public void setInferredMode(boolean value) {
        if (value && currentWidget instanceof InferredConditionsWidget) {
            return;
        }
        if (!value && currentWidget instanceof AssertedConditionsWidget) {
            return;
        }
        ComponentUtilities.dispose(currentWidget);
        remove(currentWidget);
        if (value) {
            currentWidget = new InferredConditionsWidget();
        }
        else {
            currentWidget = new AssertedConditionsWidget();
        }
        currentWidget.setup(getDescriptor(), isDesignTime(), getProject(), getCls(), ((SlotWidget) this).getSlot());
        currentWidget.initialize();
        currentWidget.setInstance(getEditedResource());
        add(BorderLayout.CENTER, currentWidget);
        revalidate();
    }


    public void setSelectedRow(int row) {
        currentWidget.getTable().setSelectedRow(row);
    }


    public void setSlot(Slot slot) {
        super.setSlot(slot);
        currentWidget.setSlot(slot);
    }


    public void setup(final WidgetDescriptor descriptor, boolean isDesignTime, Project project, Cls cls, Slot slot) {
        super.setup(descriptor, isDesignTime, project, cls, slot);
        currentWidget.setup(descriptor, isDesignTime, project, cls, slot);
    }


    public void showInferredConditions() {
        setInferredMode(true);
    }


    protected void updateBorder(Collection values) {
        setBorder(null);
    }
}
