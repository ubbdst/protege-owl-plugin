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

package edu.stanford.smi.protegex.owl.ui.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Selectable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.navigation.NavigationHistoryTabWidget;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourcePanel;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractTabWidget;

/**
 * The tab to edit RDFProperties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLPropertiesTab extends AbstractTabWidget implements NavigationHistoryTabWidget {

    private ResourcePanel resourcePanel;

    //private OWLSubpropertyPane subpropertyPane;

    //private OWLSuperpropertiesPanel superpropertiesPanel;

	private OWLPropertyHierarchiesPanel owlHierarchiesPanel;


    private JComponent createMainSplitter() {
        JSplitPane mainSplitter = createLeftRightSplitPane("SlotsTab.left_right", 250);
        mainSplitter.setLeftComponent(owlHierarchiesPanel = new OWLPropertyHierarchiesPanel(getOWLModel()));
        owlHierarchiesPanel.addSelectionListener(new SelectionListener() {
	        public void selectionChanged(SelectionEvent event) {
		        transmitSelection();
	        }
        });
	    resourcePanel = createResourcePanel();
        JPanel resourceDisplayHolder = new JPanel(new BorderLayout());
        resourceDisplayHolder.add(BorderLayout.CENTER, (Component) resourcePanel);
        mainSplitter.setRightComponent(resourceDisplayHolder);

        return mainSplitter;
    }


    protected ResourcePanel createResourcePanel() {
        return ProtegeUI.getResourcePanelFactory().createResourcePanel(getOWLModel(), ResourcePanel.DEFAULT_TYPE_PROPERTY);
    }

    public boolean displayHostResource(RDFResource resource) {
        return owlHierarchiesPanel.displayHostResource(resource);
    }


    public Selectable getNestedSelectable() {
        return owlHierarchiesPanel;
    }


    public void initialize() {
        setIcon(OWLIcons.getPropertiesIcon());
        setLabel("Properties");
        add(createMainSplitter());
        if (!owlHierarchiesPanel.getSelection().isEmpty()) {
            transmitSelection();
        }
        setEnabled(true);
    }


    @SuppressWarnings("unchecked")
    public static boolean isSuitable(Project p, Collection errors) {
        if (!(p.getKnowledgeBase() instanceof OWLModel)) {
            errors.add("This tab can only be used with OWL projects.");
            return false;
        }
        else if (p.isMultiUserClient() &&
                !RemoteClientFrameStore.isOperationAllowed(p.getKnowledgeBase(), 
                                                           OwlMetaProjectConstants.USE_PROPERTY_TAB)) {
            errors.add("Don't have permission to access the owl properties tab");
            return false;
        }
        else {
            return true;
        }
    }


    public void setSelectedProperty(RDFProperty property) {
        //OWLHierarchiesPanel.setSelectedProperty(property);
    }


    /**
     * @see #setSelectedProperty
     * @deprecated
     */
    public void setSelectedSlot(OWLProperty property) {
        setSelectedProperty(property);
    }


    private void transmitSelection() {
        RDFProperty selection = (RDFProperty) CollectionUtilities.getFirstItem(owlHierarchiesPanel.getSelection());
        resourcePanel.setResource(selection);
        //tt: maybe delete this
        ((ResourceDisplay)resourcePanel).setEnabled(isEnabled());        
    }
    
   
    @Override
    public void setEnabled(boolean enabled) { 
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(getOWLModel(), OwlMetaProjectConstants.OPERATION_PROPERTY_TAB_WRITE);
    	owlHierarchiesPanel.setEnabled(enabled);
    	((ResourceDisplay)resourcePanel).setEnabled(enabled);
    	super.setEnabled(enabled);
    }
}
