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

package edu.stanford.smi.protegex.owl.ui;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.dialogs.DefaultModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.dialogs.DefaultSelectionDialogFactory;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.dialogs.SelectionDialogFactory;
import edu.stanford.smi.protegex.owl.ui.icons.DefaultIconFactory;
import edu.stanford.smi.protegex.owl.ui.icons.IconFactory;
import edu.stanford.smi.protegex.owl.ui.navigation.NavigationHistoryManager;
import edu.stanford.smi.protegex.owl.ui.navigation.TabNavigationHistorySelectable;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.DefaultResourcePanelFactory;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourcePanelFactory;

/**
 * A utility class that can be used to access and manipulate the high-level Swing
 * containers of Protege for a given Project.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeUI {

    private static IconFactory iconFactory = new DefaultIconFactory();

    private static Map<Project, ProjectView> viewMap = new HashMap<Project, ProjectView>();
    private static Map<OWLModel, NavigationHistoryManager> navMap = new HashMap<OWLModel, NavigationHistoryManager>();

    private static ModalDialogFactory mdf = new DefaultModalDialogFactory();
    private static SelectionDialogFactory sdf = new DefaultSelectionDialogFactory();
    private static ResourcePanelFactory resourcePanelFactory = new DefaultResourcePanelFactory();


    /**
     * Gets an Icon representing a given resource, using the current <CODE>IconFactory</CODE>.
     *
     * @param resource the resource to get an Icon for
     * @return an Icon
     * @see #getIconFactory
     */
    public static Icon getIcon(RDFResource resource) {
        return iconFactory.getIcon(resource);
    }


    public static IconFactory getIconFactory() {
        return iconFactory;
    }


    public static ModalDialogFactory getModalDialogFactory() {
        return mdf;
    }


    public static ProjectView getProjectView(Component component) {
        while (!(component instanceof ProjectView) && component != null) {
            component = component.getParent();
        }
        return (ProjectView) component;
    }


    public static ProjectView getProjectView(OWLModel owlModel) {
        return getProjectView(owlModel.getProject());
    }


    public static ProjectView getProjectView(Project project) {
        if (project != null) {
            ProjectView view = viewMap.get(project);
            if (view != null) {
                return view;
            }
            else {
                Log.getLogger().warning("No ProjectView registered for project " + project.getName());
                return ProjectManager.getProjectManager().getCurrentProjectView();
            }
        }
        else {
        	Log.getLogger().warning("Unsafe access to ProjectView using null Project");
            return ProjectManager.getProjectManager().getCurrentProjectView();
        }
    }

    public static NavigationHistoryManager getNavigationHistoryManager(OWLModel owlModel) {
    	NavigationHistoryManager nhm = navMap.get(owlModel);
    	if (nhm == null) {
    		TabNavigationHistorySelectable selectable = new TabNavigationHistorySelectable(owlModel);
    		nhm = new NavigationHistoryManager(selectable, owlModel);
    		navMap.put(owlModel, nhm);
    	}
    	return nhm;
    }


    public static ResourcePanelFactory getResourcePanelFactory() {
        return resourcePanelFactory;
    }


    public static SelectionDialogFactory getSelectionDialogFactory() {
        return sdf;
    }


    public static Component getTopLevelContainer(OWLModel owlModel) {
        return getTopLevelContainer(owlModel.getProject());
    }


    public static Component getTopLevelContainer(Project project) {
        Component view = getProjectView(project);
        while (view != null && view.getParent() != null) {
            view = view.getParent();
        }
        return view;
    }


    public static void register(ProjectView projectView) {
        viewMap.put(projectView.getProject(), projectView);
    }


    public static void reloadUI(Project project) {
        ProjectView projectView = getProjectView(project);
        reloadUI(projectView);
    }


    public static void reloadUI(ProjectView projectView) {
    	if (projectView == null) {
    		return;
    	}
        int oldTabMode = projectView.getTabbedPane().getTabPlacement();
        projectView.reload(true);
        projectView.getTabbedPane().setTabPlacement(oldTabMode);
        Component parent = projectView.getTopLevelAncestor();
        parent.invalidate();
        parent.validate();
    }


    public static void reloadUI(OWLModel owlModel) {
        reloadUI(owlModel.getProject());
    }


    public static void setIconFactory(IconFactory iconFactory) {
        ProtegeUI.iconFactory = iconFactory;
    }


    public static void setModalDialogFactory(ModalDialogFactory mdf) {
        ProtegeUI.mdf = mdf;
    }


    public static void setResourcePanelFactory(ResourcePanelFactory factory) {
        ProtegeUI.resourcePanelFactory = factory;
    }


    public static void setSelectionDialogFactory(SelectionDialogFactory sdf) {
        ProtegeUI.sdf = sdf;
    }


    /**
     * Shows a given RDFResource in an external window.
     * This method should be used instead of core Protege's Project.show() method.
     *
     * @param resource the resource to show
     */
    public static void show(RDFResource resource) {
        if (resource != null) {
            resource.getProject().show(resource);
        }
    }


    public static void unregister(ProjectView projectView) {
        Project project = projectView.getProject();
        NavigationHistoryManager nhm = navMap.get(project.getKnowledgeBase());
        if (nhm != null) {
        	nhm.dispose();
        	navMap.remove(project.getKnowledgeBase());
        }
        viewMap.remove(project);
    }
    
	public static String getPotentialIconName(Frame frame) {
		return (frame instanceof RDFResource) ? ((RDFResource)frame).getIconName() : null;
	}	
}