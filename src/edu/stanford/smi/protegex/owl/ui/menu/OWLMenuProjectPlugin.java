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

package edu.stanford.smi.protegex.owl.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import edu.stanford.smi.protege.action.ArchiveProject;
import edu.stanford.smi.protege.action.Copy;
import edu.stanford.smi.protege.action.CreateProject;
import edu.stanford.smi.protege.action.Cut;
import edu.stanford.smi.protege.action.DisplayHtml;
import edu.stanford.smi.protege.action.OpenProject;
import edu.stanford.smi.protege.action.Paste;
import edu.stanford.smi.protege.action.RedoAction;
import edu.stanford.smi.protege.action.RevertProject;
import edu.stanford.smi.protege.action.SaveProject;
import edu.stanford.smi.protege.action.ShowAboutPluginsBox;
import edu.stanford.smi.protege.action.UndoAction;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.ProjectMenuBar;
import edu.stanford.smi.protege.ui.ProjectToolBar;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.StandardAction;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.FormWidget;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerPluginMenuManager;
import edu.stanford.smi.protegex.owl.javacode.JavaCodeGeneratorResourceAction;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.model.util.XSDVisibility;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionManager;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.actions.ShowAboutProtegeOWLAction;
import edu.stanford.smi.protegex.owl.ui.forms.AbsoluteFormsGenerator;
import edu.stanford.smi.protegex.owl.ui.forms.AbsoluteFormsLoader;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.navigation.NavigationHistoryManager;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.subsumption.ChangedClassesPanel;
import edu.stanford.smi.protegex.owl.ui.tooltips.ClassDescriptionToolTipGenerator;
import edu.stanford.smi.protegex.owl.ui.tooltips.HomeOntologyToolTipGenerator;
import edu.stanford.smi.protegex.owl.ui.widget.OWLToolTipGenerator;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.OWLWidgetMapper;

/**
 * A ProjectPlugin that makes a couple of initializing adjustments to
 * the main menu, tool bar etc, after a project has been loaded.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLMenuProjectPlugin extends ProjectPluginAdapter {
    private final static String CHANGED_WIDGETS = "ChangedWidgets";
    private final static String HELP_URL_GETTING_STARTED = "http://protege.stanford.edu/doc/owl/getting-started.html";
    private final static String HELP_URL_FAQ = "http://protege.stanford.edu/doc/owl-faq.html";
    private final static String HELP_URL_OWL_TUTORIAL = "http://www.co-ode.org/resources/tutorials/protege-owl-tutorial.php";
    public static final String PROSE_PROPERTY = "OWL-Prose";

    public static final String MENU_NAME = AbstractOWLModelAction.OWL_MENU;

    private SyntaxHelpAction syntaxHelpAction = new SyntaxHelpAction();
    private JCheckBoxMenuItem proseBox;
    private OWLModelAction recentAction = null;


    private void addToolBarButton(JToolBar toolBar, Action action) {
        ComponentFactory.addToolBarButton(toolBar, action);
    }

    private void addToolBarButton(JToolBar toolBar, Action action, Icon icon) {
        action.putValue(Action.SMALL_ICON, icon);
        action.putValue(StandardAction.DISABLED_ICON, null);
        addToolBarButton(toolBar, action);
    }

    private void adjustMenuAndToolBar(final OWLModel owlModel,
    								  final ProjectMenuBar menuBar,
    								  final ProjectToolBar toolBar) {

        JMenu owlMenu = new JMenu(AbstractOWLModelAction.OWL_MENU);
        owlMenu.setMnemonic(KeyEvent.VK_O);

        JMenu reasoningMenu = new JMenu(AbstractOWLModelAction.REASONING_MENU);
        reasoningMenu.setMnemonic(KeyEvent.VK_R);

        JMenu codeMenu = ComponentUtilities.getMenu(menuBar, AbstractOWLModelAction.CODE_MENU, true, menuBar.getComponentCount() - 2);
        codeMenu.setMnemonic(KeyEvent.VK_C);

        JMenu toolsMenu = new JMenu(AbstractOWLModelAction.TOOLS_MENU);
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        // Added - JLV.  Decided to completely regenerate the Help menu.
        // It is too error prone to guess what Core Protege added and then
        // figure out where OWL specific menu items should be inserted.
        JMenu helpMenu = ComponentUtilities.getMenu(menuBar, LocalizedText.getText(ResourceKey.MENUBAR_HELP));
        if (helpMenu != null) {
            regenerateHelpMenu(helpMenu, owlModel);
        } else {
            Log.getLogger().warning("Protege-OWL could not adapt the Help menu.");
        }

        // add OWLMenu to mainMenuBar
        menuBar.add(owlMenu, 3);
        menuBar.add(reasoningMenu, 4);
        menuBar.add(codeMenu, 5);
        menuBar.add(toolsMenu, 6);

        ReasonerPluginMenuManager.fillReasoningMenu(owlModel, reasoningMenu);

        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_MANAGE_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_CHANGE_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_MERGE_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_SHOW_INCLUDED);
        disableProjectMenuItem(menuBar, ResourceKey.PROJECT_INCLUDE);

        disableMenuItem(menuBar, ResourceKey.MENUBAR_PROJECT, ResourceKey.PROJECT_METRICS, false);

        adjustToolBar(toolBar);

        recentAction = null;
        OWLModelActionManager.addOWLModelActionsToMenubar(owlModel, new OWLModelActionManager.Adder() {
            public void addOWLModelAction(OWLModelAction action) {
                addOWLModelActionToMenuBar(action, owlModel, menuBar);
            }
        });

        recentAction = null;
        OWLModelActionManager.addOWLModelActionsToToolbar(owlModel, new OWLModelActionManager.Adder() {
            public void addOWLModelAction(OWLModelAction action) {
                addOWLModelActionToToolBar(action, owlModel, toolBar);
            }
        });

        toolBar.addSeparator();

        NavigationHistoryManager manager = ProtegeUI.getNavigationHistoryManager(owlModel);
        manager.add(owlModel.getOWLThingClass());
        toolBar.add(manager.getBackAction());
        toolBar.add(manager.getForwardAction());

        // Remove separators as first entries
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getItemCount() > 0) {
                JMenuItem item = menu.getItem(0);
                if (item == null) {
                    menu.remove(0);
                }
            }
        }
    }

    private void addOWLModelActionToMenuBar(OWLModelAction action, OWLModel owlModel, ProjectMenuBar menuBar) {
        StringTokenizer tokenizer = new StringTokenizer(action.getMenubarPath(), "/", false);
        if (tokenizer.hasMoreElements()) {
            Action a = createAction(action, owlModel);
            if (a != null) {
                String menuName = tokenizer.nextToken();
                JMenu menu = null;
                for (int i = 0; i < menuBar.getMenuCount(); i++) {
                    if (menuName.equals(menuBar.getMenu(i).getText())) {
                        menu = menuBar.getMenu(i);
                    }
                }
                if (menu == null) {
                    menu = new JMenu(menuName);
                    menuBar.add(menu, menuBar.getMenuCount() - 1);
                }
                else if (recentAction != null) {
                    if (!recentAction.getMenubarPath().equals(action.getMenubarPath())) {
                        menu.addSeparator();
                    }
                }
                menu.add(a);
            }
        }
        recentAction = action;
    }

    private void addOWLModelActionToToolBar(OWLModelAction action, OWLModel owlModel, ProjectToolBar toolBar) {
        Action a = createAction(action, owlModel);
        if (a != null) {
            if (recentAction != null) {
                if (!recentAction.getToolbarPath().equals(action.getToolbarPath())) {
                    toolBar.addSeparator();
                }
            }
            addToolBarButton(toolBar, a);
        }
        recentAction = action;
    }

    private void adjustToolBar(JToolBar toolBar) {
        toolBar.removeAll();

        addToolBarButton(toolBar, new CreateProject(true), OWLIcons.getImageIcon("File"));
        addToolBarButton(toolBar, new OpenProject(true), OWLIcons.getOpenProjectIcon());
        addToolBarButton(toolBar, new SaveProject(true), OWLIcons.getSaveProjectIcon());
        toolBar.addSeparator();

        addToolBarButton(toolBar, new Cut(true), OWLIcons.getCutIcon());
        addToolBarButton(toolBar, new Copy(true), OWLIcons.getCopyIcon());
        addToolBarButton(toolBar, new Paste(true), OWLIcons.getPasteIcon());
        toolBar.addSeparator();

        addToolBarButton(toolBar, new ArchiveProject(true), OWLIcons.getArchiveProjectIcon());
        addToolBarButton(toolBar, new RevertProject(true), OWLIcons.getRevertProjectIcon());
        toolBar.addSeparator();

        addToolBarButton(toolBar, new UndoAction(true), OWLIcons.getUndoIcon());
        addToolBarButton(toolBar, new RedoAction(true), OWLIcons.getRedoIcon());
        toolBar.addSeparator();
    }

    @Override
	public void afterCreate(Project p) {
        final KnowledgeBase kb = p.getKnowledgeBase();
        if (kb instanceof OWLModel) {

            //added TT:
            OWLUI.fixBrowserSlotPatterns(p);

            //p.setWidgetMapper(new OWLWidgetMapper((OWLModel) p.getKnowledgeBase()));
            OWLModel owlModel = (OWLModel) kb;
            if (owlModel instanceof OWLDatabaseModel) {
                ((OWLDatabaseModel) p.getKnowledgeBase()).initDefaultNamespaces();
            }
        }
    }

    @Override
	public void afterLoad(Project project) {
        KnowledgeBase kb = project.getKnowledgeBase();
        if (!(kb instanceof OWLModel)) {
            return;
        }

        OWLModel owlModel = (OWLModel) kb;
        //make sure owl:Thing is visible
        owlModel.getOWLThingClass().setVisible(true);
        makeHiddenClsesWithSubclassesVisible(owlModel);
        project.setWidgetMapper(new OWLWidgetMapper(owlModel));

        // added TT:
        OWLUI.fixBrowserSlotPatterns(project);

        fix(owlModel);

        if (project.getSources().getString(AbsoluteFormsGenerator.SAVE_FORMS_KEY) != null) {
            try {
                AbsoluteFormsLoader absoluteFormsLoader = new AbsoluteFormsLoader(owlModel);
                absoluteFormsLoader.loadAll();
            }
            catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught at loading absolute forms", ex);
            }
        }
    }

    @Override
	public void afterSave(Project p) {
        if (p.getKnowledgeBase() instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) p.getKnowledgeBase();
            restoreWidgetsAfterSave(owlModel);
        }
    }

    @Override
	public void afterShow(ProjectView view, ProjectToolBar toolBar, ProjectMenuBar menuBar) {
        KnowledgeBase kb = view.getProject().getKnowledgeBase();
        if (kb instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) kb;

            ProtegeUI.register(view);

            if (owlModel instanceof JenaOWLModel) {
                JenaOWLModel.inUI = true;
            }

            owlModel.getProject().setInstanceDisplayClass(ResourceDisplay.class);
            adjustMenuAndToolBar(owlModel, menuBar, toolBar);
            adjustOWLAnnotationPropertyForm(view.getProject());

            ResourceActionManager.addResourceActionClass(JavaCodeGeneratorResourceAction.class);
        }
    }

    // Make sure that the annotation property class form does not contain
    // rdfs:domain and rdfs:range widgets
    private void adjustOWLAnnotationPropertyForm(Project project) {
    	OWLModel owlModel = (OWLModel) project.getKnowledgeBase();

    	try {
        	ClsWidget clsWidget = project.getDesignTimeClsWidget(owlModel.getOWLAnnotationPropertyClass());

    		Slot s1 = owlModel.getRDFSDomainProperty();
    		clsWidget.replaceWidget(s1, null);

    		Slot s2 = owlModel.getRDFSRangeProperty();
    		clsWidget.replaceWidget(s2, null);

    		Slot s3 = owlModel.getSystemFrames().getDirectDomainSlot();
    		clsWidget.replaceWidget(s3, null);

    		((FormWidget)clsWidget).setVerticalStretcher(FormWidget.STRETCH_NONE);
    		((FormWidget)clsWidget).setHorizontalStretcher(FormWidget.STRETCH_ALL);

		} catch (Exception e) {
			Log.getLogger().warning("Problems at adjusting the class form of owl:AnnotationProperty.");
		}

	}


	@Override
	public void beforeHide(ProjectView view, ProjectToolBar toolBar,
			ProjectMenuBar menuBar) {
		KnowledgeBase kb = view.getProject().getKnowledgeBase();
		if (kb instanceof OWLModel) {
			ProtegeUI.unregister(view);
			ChangedClassesPanel.dispose((OWLModel) kb);
			OWLUI.setOWLToolTipGenerator(null);
			proseBox = null;
		}
	}

    @Override
	public void beforeSave(Project p) {
        if (p.getKnowledgeBase() instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) p.getKnowledgeBase();
            String value = owlModel.getOWLProject().getSettingsMap().getString(AbsoluteFormsGenerator.SAVE_FORMS_KEY);
            if (value != null) {
                AbsoluteFormsGenerator generator = new AbsoluteFormsGenerator(owlModel);
                try {
                    generator.generateFiles(value);
                }
                catch (Exception ex) {
                    Log.getLogger().warning("Could not save .forms files");
                    Log.getLogger().log(Level.WARNING, "Exception caught", ex);
                }

                if (AbsoluteFormsLoader.useNewFormMechanism_DontUseThisMethod()) {
                    prepareWidgetsForSave(owlModel);
                }
            }
        }
    }

    private Action createAction(final OWLModelAction owlModelAction, final OWLModel owlModel) {
        return new OWLModelActionAction(owlModelAction, owlModel);
    }

    private void disableProjectMenuItem(ProjectMenuBar menuBar, ResourceKey resourceKey) {
        disableMenuItem(menuBar, ResourceKey.MENUBAR_PROJECT, resourceKey, true);
    }

    private void disableMenuItem(ProjectMenuBar menuBar, ResourceKey menuKey,
                                 ResourceKey resourceKey, boolean removeTrailingSeparator) {
        String menuName = LocalizedText.getText(menuKey);
        String itemName = LocalizedText.getText(resourceKey);
        JMenu projectMenu = menuBar.getMenu(0);
        for (int i = 1; !projectMenu.getText().equals(menuName); i++) {
            projectMenu = menuBar.getMenu(i);
        }
        for (int i = 0; i < projectMenu.getItemCount(); i++) {
            JMenuItem item = projectMenu.getItem(i);
            if (item != null && itemName.equals(item.getText())) {
                projectMenu.remove(item);
                if (removeTrailingSeparator && projectMenu.getItem(i) == null) {
                    projectMenu.remove(i);
                }
            }
        }
    }

    private void fix(OWLModel owlModel) {
        if (!owlModel.getProject().isMultiUserClient()) {
        	//TODO: TT - this must not be called
            //OWLBackwardsCompatibilityProjectFixups.fix(owlModel);
            XSDVisibility.updateVisibility(owlModel);
        }
    }


    public boolean isProseActivated() {
        return proseBox != null && proseBox.isSelected();
    }

    /**
     * Checks whether a given class is used somewhere as a range of a property.
     * This method is not executed for database knowledgebase, because this would
     * be too slot.
     *
     * @param cls the Cls to test
     * @return true if cls is used at some range (VALUE_TYPE slot)
     */
    private static boolean isUsedInRange(Cls cls, Set systemFrames) {
        OWLModel owlModel = (OWLModel) cls.getKnowledgeBase();
        if (owlModel instanceof JenaOWLModel) {
            Collection refs = ((KnowledgeBase) owlModel).getReferences(cls, 1000);
            Slot rangeSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.VALUE_TYPE);
            for (Iterator it = refs.iterator(); it.hasNext();) {
                Reference reference = (Reference) it.next();
                Frame frame = reference.getFrame();
                if (reference.getSlot().equals(rangeSlot) &&
                    !systemFrames.contains(frame) &&
                    !frame.getName().startsWith(ProtegeNames.PROTEGE_OWL_NAMESPACE)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
	public static void makeHiddenClsesWithSubclassesVisible(OWLModel owlModel) {
        if (owlModel.getOWLNamedClassClass().getSubclassCount() > 0 ||
            owlModel.getRDFSNamedClassClass().getSubclassCount() > 3) { //3: owlClass, owl:DeprecatedClass, protege:ExternalClass
            owlModel.getRDFSNamedClassClass().setVisible(true);
            owlModel.getOWLNamedClassClass().setVisible(true);
        }
        Set systemFrames = new HashSet(owlModel.getOWLSystemResources());
        if (owlModel.getOWLObjectPropertyClass().getSubclassCount() > 3 || //better test needed
            owlModel.getOWLDatatypePropertyClass().getSubclassCount() > 0 ||
            isUsedInRange(owlModel.getOWLObjectPropertyClass(), systemFrames) ||
            isUsedInRange(owlModel.getOWLDatatypePropertyClass(), systemFrames) ||
            isUsedInRange(owlModel.getRDFPropertyClass(), systemFrames)) {
            owlModel.getRDFPropertyClass().setVisible(true);
            owlModel.getOWLObjectPropertyClass().setVisible(true);
            owlModel.getOWLDatatypePropertyClass().setVisible(true);
        }
        makeVisibleIfSubclassesExist(owlModel.getRDFListClass(), systemFrames);
        makeVisibleIfSubclassesExist(owlModel.getOWLDataRangeClass(), systemFrames);
        makeVisibleIfSubclassesExist(owlModel.getOWLNothing(), systemFrames);
        makeVisibleIfSubclassesExist(owlModel.getRDFSNamedClass(RDFNames.Cls.STATEMENT), systemFrames);

        //make visibile the external resource if instances exist
        if (owlModel.getDirectInstanceCount(owlModel.getRDFUntypedResourcesClass()) > 0) {
        	owlModel.getRDFUntypedResourcesClass().setVisible(true);
        }
    }

    private static void makeVisibleIfSubclassesExist(Cls cls, Set systemFrames) {
        if (cls.getVisibleDirectSubclassCount() > 0 || isUsedInRange(cls, systemFrames)) {
            cls.setVisible(true);
        }
    }

    /**
     * Should be called prior to saving a Project in order to remove widgets
     * from the file.
     * This call must be followed by <CODE>restoreWidgetsAfterSave()</CODE>
     * after save completed.
     *
     * @param owlModel the OWLModel to adjust
     */
    public static void prepareWidgetsForSave(OWLModel owlModel) {
        Project p = owlModel.getProject();
        Collection widgets = new ArrayList();
        Iterator i = p.getClsesWithCustomizedForms().iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            if (!cls.isSystem()) {
                WidgetDescriptor d = p.getDesignTimeClsWidget(cls).getDescriptor();
                if (!d.isIncluded()) {
                    widgets.add(d);
                    d.setIncluded(true);
                }
            }
        }
        owlModel.getOWLProject().setSessionObject(CHANGED_WIDGETS, widgets);
    }

    public static void restoreWidgetsAfterSave(OWLModel owlModel) {
        OWLProject owlProject = owlModel.getOWLProject();
        Object widgets = owlProject.getSessionObject(CHANGED_WIDGETS);
        if (widgets instanceof Collection) {
            Iterator wit = ((Collection) widgets).iterator();
            while (wit.hasNext()) {
                WidgetDescriptor d = (WidgetDescriptor) wit.next();
                d.setIncluded(false);
            }
            owlProject.setSessionObject(CHANGED_WIDGETS, null);
        }
    }

    private void regenerateHelpMenu(JMenu helpMenu, final OWLModel owlModel) {
    	// Remove everything that was added by Core Protege and
    	// build menu again.  This seems safer than removing select items
    	// and then inserting items in particular places.
    	helpMenu.removeAll();

    	ComponentFactory.addMenuItemNoIcon(helpMenu,
    		new DisplayHtml(ResourceKey.HELP_MENU_GETTING_STARTED, HELP_URL_GETTING_STARTED));

    	ComponentFactory.addMenuItemNoIcon(helpMenu,
    		new DisplayHtml(ResourceKey.HELP_MENU_FAQ, HELP_URL_FAQ));

    	ComponentFactory.addMenuItemNoIcon(helpMenu,
    	new AbstractAction("Prot\u00E9g\u00E9-OWL Tutorial...") {
        	public void actionPerformed(ActionEvent e) {
        		SystemUtilities.showHTML(HELP_URL_OWL_TUTORIAL);
        	}
        });

        helpMenu.addSeparator();

        ComponentFactory.addMenuItemNoIcon(helpMenu, syntaxHelpAction);

	    // prose tooltips selector
	    proseBox = new JCheckBoxMenuItem("Display prose as tool tip of OWL expressions", true);
	    helpMenu.add(proseBox);
	    proseBox.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            owlModel.getOWLProject().getSettingsMap().setBoolean(PROSE_PROPERTY, proseBox.isSelected());
	            if (proseBox.isSelected()) {
	                OWLUI.setOWLToolTipGenerator(new ClassDescriptionToolTipGenerator());
	            }
	            else {
	                OWLUI.setOWLToolTipGenerator(new HomeOntologyToolTipGenerator());
	            }
	        }
	    });
	    proseBox.setSelected(Boolean.TRUE.equals(owlModel.getOWLProject().getSettingsMap().getBoolean(PROSE_PROPERTY)));

	    OWLToolTipGenerator toolTipGenerator = OWLUI.getOWLToolTipGenerator();
	    //TT: Temporary solution. This is kind of hacky, but needed for the case in which a tab widget sets its own tooltip generator
	    if (toolTipGenerator == null || toolTipGenerator instanceof ClassDescriptionToolTipGenerator || toolTipGenerator instanceof HomeOntologyToolTipGenerator) {
	        OWLUI.setOWLToolTipGenerator(proseBox.isSelected() ? new ClassDescriptionToolTipGenerator() : new HomeOntologyToolTipGenerator());
	    }

        helpMenu.addSeparator();
	    ComponentFactory.addMenuItemNoIcon(helpMenu, new ShowAboutProtegeOWLAction());
	    ComponentFactory.addMenuItemNoIcon(helpMenu, new ShowAboutPluginsBox());
	    ComponentFactory.addMenuItemNoIcon(helpMenu, new DisplayHtml(ResourceKey.HELP_MENU_CITE_PROTEGE, 
	    		ApplicationProperties.getHowToCiteProtegeURLString()));
    }
}
