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

package edu.stanford.smi.protegex.owlx.examples;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SwingListExample {

    public static void main(String[] args) throws Exception {

        String uri = "http://www.owl-ontologies.com/travel.owl";
        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
        OWLNamedClass destinationClass = owlModel.getOWLNamedClass("Destination");

        ListPanel listPanel = new ListPanel(destinationClass);
        JFrame frame = new JFrame("Simple List Example");
        Container cont = frame.getContentPane();
        cont.setLayout(new BorderLayout());
        cont.add(BorderLayout.CENTER, listPanel);

        frame.setBounds(100, 100, 300, 300);
        frame.setVisible(true);
    }


    private static class ListPanel extends JPanel implements Disposable {

        private static final long serialVersionUID = -3004789529589202212L;

        private OWLNamedClass destinationClass;

        private JList list;

        private DefaultListModel listModel;

        private ModelListener modelListener = new ModelAdapter() {
            public void individualCreated(RDFResource resource) {
                if (resource.hasProtegeType(destinationClass, true)) {
                    handleDestinationAdded(resource);
                }
            }
        };

        private OWLModel owlModel;


        ListPanel(OWLNamedClass activityClass) {

            this.destinationClass = activityClass;
            this.owlModel = activityClass.getOWLModel();

            owlModel.addModelListener(modelListener);

            listModel = new DefaultListModel();
            for (Iterator it = activityClass.getInstances(true).iterator(); it.hasNext();) {
                OWLIndividual individual = (OWLIndividual) it.next();
                listModel.addElement(individual);
            }
            list = new JList(listModel);

            // Make sure list entries show up nicely with icons
            list.setCellRenderer(new ResourceRenderer());

            // Wrap the list together with a button bar
            OWLLabeledComponent lc = new OWLLabeledComponent("Destinations", new JScrollPane(list));
            lc.addHeaderButton(new AbstractAction("Add Destination...", OWLIcons.getAddIcon(OWLIcons.RDF_INDIVIDUAL)) {
                private static final long serialVersionUID = 5718620565773628276L;

                public void actionPerformed(ActionEvent e) {
                    addDestination();
                }
            });

            // Add everything into the JPanel
            setLayout(new BorderLayout());
            add(BorderLayout.CENTER, lc);
        }


        private void addDestination() {
            RDFSNamedClass newType = ProtegeUI.getSelectionDialogFactory().selectClass(this, owlModel,
                    Collections.singleton(destinationClass), "Select type of new Destination");
            if (newType != null) {
                String name = JOptionPane.showInputDialog("Enter name of new " + newType.getBrowserText());
                if (name != null) {
                    newType.createRDFIndividual(name);
                }
            }
        }


        public void dispose() {
            owlModel.removeModelListener(modelListener);
        }


        private void handleDestinationAdded(RDFResource destination) {
            listModel.addElement(destination);
            list.setSelectedValue(destination, true);
        }
    }
}
