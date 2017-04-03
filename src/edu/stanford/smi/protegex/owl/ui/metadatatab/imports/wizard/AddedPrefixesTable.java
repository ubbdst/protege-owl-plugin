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

package edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 30, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AddedPrefixesTable extends JTable {

    public AddedPrefixesTable(NamespaceManager nsm, Collection addedPrefixes) {
        super(new AddedPrefixesTableModel(nsm, addedPrefixes));
        setPreferredScrollableViewportSize(new Dimension(600, 200));
        getColumnModel().getColumn(AddedPrefixesTableModel.PREFIX_COLUMN).setWidth(30);
    }


    public static void showDialog(Component parent, OWLModel owlModel, Collection addedPrefixes) {
        JPanel holder = new JPanel(new BorderLayout(12, 12));
        AddedPrefixesTable table = new AddedPrefixesTable(owlModel.getNamespaceManager(), addedPrefixes);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(Color.WHITE);
        holder.add(new LabeledComponent("Added namespace prefixes", sp));
        ProtegeUI.getModalDialogFactory().showDialog(parent, holder, "Added Prefixes", ModalDialogFactory.MODE_CLOSE);
    }


}

