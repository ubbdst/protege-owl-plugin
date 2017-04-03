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

import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Selectable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.explorer.ExplorerFilter;
import edu.stanford.smi.protegex.owl.ui.explorer.ExplorerTreePanel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ToggleSuperclassExplorerAction extends AbstractAction {

    private Component component;

    private ExplorerTreePanel explorerTreePanel;

    private Selectable selectable;

    private boolean useInferredSuperclasses;


    public ToggleSuperclassExplorerAction(Selectable selectable, boolean useInferredSuperclasses) {
        super("Show/Hide superclass explorer", OWLIcons.getImageIcon(OWLIcons.SUPERCLASS_EXPLORER));
        this.component = (Component) selectable;
        this.selectable = selectable;
        this.useInferredSuperclasses = useInferredSuperclasses;
        selectable.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                if (explorerTreePanel != null) {
                    RDFSClass root = getSelectedClass();
                    explorerTreePanel.setRoot(root);
                    explorerTreePanel.expandToFillSpace();
                }
            }
        });
    }


    public void actionPerformed(ActionEvent e) {
        Component c = component;
        while (c != null && !(c instanceof HierarchyPanel)) {
            c = c.getParent();
        }
        if (c != null) {
            HierarchyPanel panel = (HierarchyPanel) c;
            if (panel.getNestedHierarchy() != null) {
                panel.setNestedHierarchy(null);
                explorerTreePanel = null;
            }
            else {
                RDFSClass root = getSelectedClass();
                String prefix = useInferredSuperclasses ? "Inferred" : "Asserted";
                explorerTreePanel = new ExplorerTreePanel(root, new ExplorerFilter() {
                    public boolean getUseInferredSuperclasses() {
                        return useInferredSuperclasses;
                    }


                    public boolean isValidChild(RDFSClass parentClass, RDFSClass childClass) {
                        return childClass instanceof RDFSNamedClass;
                    }
                }, prefix + " Superclasses", false);
                panel.setNestedHierarchy(explorerTreePanel);
                explorerTreePanel.expandToFillSpace();
            }
        }
    }


    private RDFSClass getSelectedClass() {
        RDFSClass selectedClass = null;
        Collection sel = selectable.getSelection();
        if (sel != null && sel.size()>0){
            selectedClass = (RDFSClass) CollectionUtilities.getFirstItem(sel);
        }
        return selectedClass;
    }
}
