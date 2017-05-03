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

package edu.stanford.smi.protegex.owl.ui.widget;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;
import java.util.LinkedList;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FormsPanel;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protege.widget.FormsTab;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.search.finder.DefaultClassFind;
import edu.stanford.smi.protegex.owl.ui.search.finder.Find;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindAction;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindInDialogAction;
import edu.stanford.smi.protegex.owl.ui.search.finder.ResourceFinder;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         25-Oct-2005
 */
public class OWLFormsTab extends FormsTab implements HostResourceDisplay {

    private SelectableTree theTree;

    @Override
	public void initialize() {
        super.initialize();
        FormsPanel panel = (FormsPanel) getSubComponent(this, FormsPanel.class);
        LabeledComponent lc = (LabeledComponent) getSubComponent(panel, LabeledComponent.class);
        theTree = (SelectableTree) panel.getSelectable();

        FindAction fAction = new FindInDialogAction(new DefaultClassFind((OWLModel) getKnowledgeBase(),
                                                                         Find.CONTAINS),
                                                                         Icons.getFindFormIcon(),
                                                                         this, true);

        ResourceFinder finder = new ResourceFinder(fAction);

        lc.setFooterComponent(finder);
    }

    private Object getSubComponent(Component start, Class searchClass) {
        Object component = null;
        java.util.List components = new LinkedList();
        components.add(start);

        while (component == null && components.size() > 0) {
            Component current = (Component) components.remove(0);
            if (searchClass.isAssignableFrom(current.getClass())) {
                component = current;
            }
            else if (current instanceof Container) {
                components.addAll(Arrays.asList(((Container) current).getComponents()));
            }
        }
        return component;
    }

    public boolean displayHostResource(RDFResource resource) {
       return OWLUI.setSelectedNodeInTree(theTree, resource);
    }

}
