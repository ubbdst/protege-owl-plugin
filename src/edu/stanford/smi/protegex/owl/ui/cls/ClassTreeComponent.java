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

import edu.stanford.smi.protege.ui.ParentChildRoot;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * An panel showing a JTree of all classes in an OWLModel.
 * This can be configured in various ways, e.g. you can get the OWLLabeledComponent
 * to add buttons to the header, or you can listen to selection changes in the tree.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassTreeComponent extends JPanel {

    private OWLLabeledComponent lc;

    private SelectableTree tree;


    public ClassTreeComponent(String title, OWLModel owlModel) {
        this(title, owlModel.getOWLThingClass(),
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        // Do nothing
                    }
                });
    }


    public ClassTreeComponent(String title, RDFSNamedClass rootClass, Action doubleClickAction) {
        tree = new SelectableTree(doubleClickAction, new ParentChildRoot(rootClass));
        tree.setExpandsSelectedPaths(true);
        tree.setSelectionRow(0);
        tree.setCellRenderer(new ResourceRenderer());
        setLayout(new BorderLayout());
        lc = new OWLLabeledComponent(title, new JScrollPane(tree));
        add(BorderLayout.CENTER, lc);
    }


    public OWLLabeledComponent getLabeledComponent() {
        return lc;
    }


    public SelectableTree getTree() {
        return tree;
    }
}
