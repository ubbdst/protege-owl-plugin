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

package edu.stanford.smi.protegex.owl.ui.resourcedisplay;

import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TriplesViewResourceDisplayPlugin implements ResourceDisplayPlugin {


    public void initResourceDisplay(RDFResource resource, JPanel hostPanel) {
        if (resource instanceof RDFIndividual) {
            Component c = hostPanel.getParent();
            while (!(c instanceof ResourceDisplay)) {
                c = c.getParent();
            }
            final ResourceDisplay resourceDisplay = (ResourceDisplay) c;
            if (resourceDisplay != null) {
                final JRadioButton formViewBox = new JRadioButton("Form View");
                final JRadioButton triplesViewBox = new JRadioButton("Triples View");
                formViewBox.setSelected(!resourceDisplay.isTriplesDisplayed());
                formViewBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        updateResourceDisplay(resourceDisplay, formViewBox, triplesViewBox);
                    }
                });
                triplesViewBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        updateResourceDisplay(resourceDisplay, formViewBox, triplesViewBox);
                    }
                });
                triplesViewBox.setSelected(resourceDisplay.isTriplesDisplayed());
                ButtonGroup group = new ButtonGroup();
                group.add(formViewBox);
                group.add(triplesViewBox);
                Box box = Box.createHorizontalBox();
                box.add(formViewBox);
                box.add(Box.createHorizontalStrut(8));
                box.add(triplesViewBox);
                hostPanel.add(box);
            }
        }
    }


    private void updateResourceDisplay(ResourceDisplay resourceDisplay, JRadioButton formViewBox, JRadioButton triplesViewBox) {
        boolean formView = formViewBox.isSelected();
        boolean triplesView = triplesViewBox.isSelected();
        resourceDisplay.setMode(formView, triplesView);
    }
}
