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

package edu.stanford.smi.protegex.owl.ui.explorer;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.cls.HierarchyManager;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.explorer.filter.DefaultExplorerFilter;
import edu.stanford.smi.protegex.owl.ui.explorer.filter.ExplorerFilterPanel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.search.SearchNamedClassAction;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExplorerAction extends ResourceAction {


    public ExplorerAction() {
        super("Explore superclass relationships...",
                OWLIcons.getImageIcon(OWLIcons.SHOW_EXPLORER),
                SearchNamedClassAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        RDFSNamedClass cls = (RDFSNamedClass) getResource();
        DefaultExplorerFilter filter = new DefaultExplorerFilter();
        if (ExplorerFilterPanel.show(getResource().getOWLModel(), filter)) {
            ExplorerTreePanel tp = new ExplorerTreePanel(cls, filter, "Asserted Recursive Hierachy", true);
            OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(getComponent());
            HierarchyManager hierarchyManager = tab.getHierarchyManager();
            hierarchyManager.addHierarchy(tp);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return resource instanceof RDFSClass &&
                OWLClassesTab.getOWLClassesTab(component) != null;
    }
}
