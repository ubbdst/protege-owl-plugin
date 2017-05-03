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

package edu.stanford.smi.protegex.owl.ui.menu.preferences;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStore;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SearchSettingsPanel extends JPanel {

    private JCheckBox ignorePrefixesCheckBox = new JCheckBox("Ignore prefixes for searching");

    private SelectableList list;

    private OWLModel owlModel;


    public SearchSettingsPanel(final OWLModel owlModel) {
        this.owlModel = owlModel;
        list = new SelectableList();
        list.setListData(owlModel.getSearchSynonymProperties().toArray());
        list.setCellRenderer(new ResourceRenderer());
        LabeledComponent lc = new OWLLabeledComponent("Use search synonyms", new JScrollPane(list));
        lc.addHeaderButton(new AbstractAction("Add property...",
                OWLIcons.getAddIcon(OWLIcons.RDF_PROPERTY)) {
            public void actionPerformed(ActionEvent e) {
                addProperty();
            }
        });
        lc.addHeaderButton(new AllowableAction("Remove selected property",
                OWLIcons.getRemoveIcon(OWLIcons.RDF_PROPERTY), list) {
            public void actionPerformed(ActionEvent e) {
                removeSelectedProperty();
            }
        });
        setLayout(new BorderLayout());

        Boolean b = owlModel.getOWLProject().getSettingsMap().getBoolean(OWLFrameStore.IGNORE_PREFIXES_IN_SEARCH);
        ignorePrefixesCheckBox.setSelected(Boolean.TRUE.equals(b));
        ignorePrefixesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                owlModel.getOWLProject().getSettingsMap().setBoolean(OWLFrameStore.IGNORE_PREFIXES_IN_SEARCH,
                        ignorePrefixesCheckBox.isSelected());
            }
        });
        add(BorderLayout.NORTH, ignorePrefixesCheckBox);
        add(BorderLayout.CENTER, lc);
    }


    private void addProperty() {
        Collection oldValues = owlModel.getSearchSynonymProperties();
        Collection properties = owlModel.getRDFProperties();
        for (Iterator it = properties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (oldValues.contains(property) ||
                    ( ((Slot) property).getValueType() != ValueType.STRING && !property.isAnnotationProperty())   ){
                it.remove();
            }
        }
        Collection neo = new HashSet(oldValues);
        neo.addAll(ProtegeUI.getSelectionDialogFactory().selectResourcesFromCollection(this, owlModel,
                properties, "Add search properties..."));
        owlModel.setSearchSynonymProperties(neo);
        list.setListData(owlModel.getSearchSynonymProperties().toArray());
    }


    private void removeSelectedProperty() {
        Collection oldValues = owlModel.getSearchSynonymProperties();
        oldValues.removeAll(list.getSelection());
        owlModel.setSearchSynonymProperties(oldValues);
        list.setListData(owlModel.getSearchSynonymProperties().toArray());
    }
}
