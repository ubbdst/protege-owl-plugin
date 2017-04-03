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
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.ui.widget.MultiResourceWidget;
import edu.stanford.smi.protegex.owl.ui.widget.MultiWidgetPropertyWidget;

/**
 * An AbstractSlotWidget that allows to toggle between a RestrictionsWidget and a
 * TemplateSlotsWidget using a JTabbedPane.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @deprecated 
 */
public class OWLMetadataWidget extends MultiWidgetPropertyWidget {

    private NameDocumentationWidget nameWidget;


    protected void createDifferentFromWidget() {
        MultiResourceWidget differentFromWidget = new MultiResourceWidget();
        addNestedWidget(differentFromWidget, OWLNames.Slot.DIFFERENT_FROM, "DifferentFrom", "owl:differentFrom");
    }


    /**
     * Can be overloaded by subclasses to add extra widgets after the labels,
     * or to suppress sameAs and differentFrom.
     */
    protected void createExtraWidgets() {
        createSameAsWidget();
        createDifferentFromWidget();
    }


    protected void createSameAsWidget() {
        MultiResourceWidget sameAsWidget = new MultiResourceWidget();
        addNestedWidget(sameAsWidget, OWLNames.Slot.SAME_AS, "SameAs", "owl:sameAs");
    }


    protected void createNestedWidgets() {

        nameWidget = new NameDocumentationWidget();
        addNestedWidget(nameWidget, Model.Slot.NAME, "Name", "Name");

        createExtraWidgets();
    }


    public NameDocumentationWidget getNameDocumentationWidget() {
        return nameWidget;
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return slot.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(Model.Slot.NAME);
    }
}
