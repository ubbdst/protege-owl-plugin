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

package edu.stanford.smi.protegex.owl.ui.menu.code;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelOrderedWriter;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriterPreferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * A JDialog displaying the active triplestore RDF/XML rendering
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SourceCodePanel extends JPanel {

    private JTextArea textArea;

    private JCheckBox useXMLEntitiesBox;

    private JComboBox tripleStoreCombo;

    private OWLModel owlModel;


    public SourceCodePanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        setLayout(new BorderLayout());
        useXMLEntitiesBox = new JCheckBox("Use XML Entities", XMLWriterPreferences.getInstance().isUseNamespaceEntities());
        useXMLEntitiesBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeSelectedTripleStore();
            }
        });
        ArrayList displayedTripleStores = new ArrayList(this.owlModel.getTripleStoreModel().getTripleStores());
        displayedTripleStores.remove(0);
        tripleStoreCombo = new JComboBox(displayedTripleStores.toArray());
        tripleStoreCombo.setSelectedItem(this.owlModel.getTripleStoreModel().getActiveTripleStore());
        tripleStoreCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeSelectedTripleStore();
            }
        });
        tripleStoreCombo.setRenderer(new TripleStoreListCellRenderer());
        JPanel panel = new JPanel(new BorderLayout(7, 7));
        panel.add(tripleStoreCombo, BorderLayout.NORTH);
        panel.add(useXMLEntitiesBox, BorderLayout.SOUTH);
        add(panel, BorderLayout.NORTH);
        // Create JTextArea
        textArea = new JTextArea();
        textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane rdfScrollPane = new JScrollPane(textArea);

        // Throw scrollPane into a labeledComponent and add buttons to it
        LabeledComponent lc = new LabeledComponent("Source Code", rdfScrollPane);
        add(BorderLayout.CENTER, lc);
        setPreferredSize(new Dimension(760, 600));
        writeSelectedTripleStore();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
            }
        });
    }


    private void writeSelectedTripleStore() {
        StringWriter stringWriter = new StringWriter();
        XMLWriterPreferences.getInstance().setUseNamespaceEntities(useXMLEntitiesBox.isSelected());
        TripleStore ts = (TripleStore) tripleStoreCombo.getSelectedItem();
        OWLModelOrderedWriter writer = new OWLModelOrderedWriter(owlModel, ts,
                stringWriter);
        try {
            writer.write();
        }
        catch (IOException e) {
            ProtegeUI.getModalDialogFactory().showThrowable(owlModel, e);
        }
        String buffer = stringWriter.toString();
        textArea.setText(buffer);
    }


    private class TripleStoreListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            TripleStore ts = (TripleStore) value;
            String name = ts.getName();
            if (name == null) {
                name = "<Main File>";
            }
            label.setText(name);
            return label;
        }
    }
}
