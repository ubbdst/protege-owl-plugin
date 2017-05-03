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

package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.ui.widget.HeaderWidget;
import edu.stanford.smi.protegex.owl.ui.widget.MultiWidgetPropertyWidget;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 20, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClassAnnotationsAndDescriptionWidget extends MultiWidgetPropertyWidget {

    private SwitchableClassDefinitionWidget switchableClassDefinitionWidget;

    private HeaderWidget headerWidget;

    protected void createNestedWidgets() {

        headerWidget = new HeaderWidget();
        addNestedWidget(headerWidget, OWLNames.Slot.EQUIVALENT_CLASS, "Class Annotations",
                "Class Annotations");

        switchableClassDefinitionWidget = new SwitchableClassDefinitionWidget();
         addNestedWidget(switchableClassDefinitionWidget, OWLNames.Slot.EQUIVALENT_CLASS, "Class Description",
                "Class Description");

    }


    public void initialize() {
        super.initialize();
        setAllMode(true);
    }

    protected void initAllPanel(JPanel allPanel, java.util.List widgets) {


        JSplitPane mainPanel = new MySplitPane(JSplitPane.VERTICAL_SPLIT,
                headerWidget, switchableClassDefinitionWidget, 0.3);

        allPanel.setLayout(new BorderLayout());
        allPanel.add(mainPanel);

    }

    protected class MySplitPane extends JSplitPane {

        MySplitPane(int orientation, Component leftComponent, Component rightComponent, double resizeWeight) {
            super(orientation, leftComponent, rightComponent);
            setDividerSize(2);
            setBorder(null);
            setResizeWeight(resizeWeight);
        }
    }

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if (cls.getKnowledgeBase() instanceof OWLModel  &&
            cls instanceof OWLNamedClass) {
            return true;
        }
        return false;
    }
}
