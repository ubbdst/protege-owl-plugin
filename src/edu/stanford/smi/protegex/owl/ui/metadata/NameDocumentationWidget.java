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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.ui.widget.MultiWidgetPropertyWidget;

/**
 * A combined widget with name and documentation side by side.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @deprecated no longer encouraged
 */
public class NameDocumentationWidget extends MultiWidgetPropertyWidget {

    private AnnotationTextAreaWidget textAreaWidget;

    private RDFResourceNameWidget nameWidget;


    protected void createNestedWidgets() {

        nameWidget = new RDFResourceNameWidget();
        addNestedWidget(nameWidget, Model.Slot.NAME, "Name", "Name");

        textAreaWidget = new AnnotationTextAreaWidget(); //new OWLCommentsWidget();   
        
        addNestedWidget(textAreaWidget, RDFSNames.Slot.COMMENT, "Comment", "Comment");
    }


    public AnnotationTextAreaWidget getAnnotationTextAreaWidget() {
        return textAreaWidget;
    }


    protected void initAllPanel(JPanel allPanel, List widgets) {
        allPanel.setLayout(new BorderLayout());
        allPanel.add(BorderLayout.NORTH, nameWidget);
        allPanel.add(BorderLayout.CENTER, (Component) textAreaWidget);
    }


    public void initialize() {
        super.initialize();
        setPreferredColumns(2);
        setPreferredRows(2);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return slot.getKnowledgeBase() instanceof OWLModel &&
                (slot.getName().equals(Model.Slot.NAME) ||
                        slot.getName().equals(RDFSNames.Slot.COMMENT));
    }
}
