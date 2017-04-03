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

package edu.stanford.smi.protegex.owl.ui.search;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A JPanel for modal dialogs that can be used to String-matching search for a certain
 * value of a selected Slot.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SearchNamedClassPanel extends JPanel {

    private OWLModel owlModel;

    private RDFSNamedClass rootClass;

    private JList propertyList;

    private JTextField textField;


    public SearchNamedClassPanel(RDFSNamedClass rootClass) {

        owlModel = rootClass.getOWLModel();
        this.rootClass = rootClass;
        List properties = new ArrayList();
        for (Iterator it = owlModel.getRDFProperties().iterator(); it.hasNext();) {
            RDFProperty slot = (RDFProperty) it.next();
            if (slot instanceof OWLDatatypeProperty && ((Slot) slot).getValueType() == ValueType.STRING) {
                properties.add(slot);
            }
        }
        Frame[] frames = (Frame[]) properties.toArray(new Frame[0]);
        Arrays.sort(frames, new FrameComparator());
        propertyList = new JList(frames);
        propertyList.setCellRenderer(new ResourceRenderer());
        propertyList.setSelectedIndex(0);

        textField = new JTextField();

        setLayout(new BorderLayout(10, 10));
        add(BorderLayout.CENTER,
                new LabeledComponent("Available Properties", new JScrollPane(propertyList)));
        add(BorderLayout.SOUTH,
                new LabeledComponent("Search Pattern", textField));
    }


    public List getResult() {
        RDFProperty property = (RDFProperty) propertyList.getSelectedValue();
        String matchString = textField.getText();
        Collection matches = owlModel.getMatchingResources(property, matchString, -1);
        List result = new ArrayList();
        for (Iterator it = matches.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (frame instanceof RDFSNamedClass && ((RDFSNamedClass) frame).isSubclassOf(rootClass)) {
                result.add(frame);
            }
        }
        return result;
    }


    public static List showDialog(Component parent, RDFSNamedClass rootClass) {
        SearchNamedClassPanel panel = new SearchNamedClassPanel(rootClass);
        int result = ProtegeUI.getModalDialogFactory().showDialog(parent, panel,
                "Search subclasses of " + rootClass.getBrowserText(),
                ModalDialogFactory.MODE_OK_CANCEL, null, true);
        if (result == ModalDialogFactory.OPTION_OK) {
            return panel.getResult();
        }
        else {
            return null;
        }
    }
}
