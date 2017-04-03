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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.plugin.ProjectFixupPlugin;
import edu.stanford.smi.protege.resource.Files;
import edu.stanford.smi.protege.storage.clips.ClipsKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.SystemUtilities;
import edu.stanford.smi.protege.widget.CheckBoxWidget;
import edu.stanford.smi.protege.widget.ComboBoxWidget;
import edu.stanford.smi.protege.widget.FloatFieldWidget;
import edu.stanford.smi.protege.widget.FloatListWidget;
import edu.stanford.smi.protege.widget.IntegerFieldWidget;
import edu.stanford.smi.protege.widget.IntegerListWidget;
import edu.stanford.smi.protege.widget.StringListWidget;
import edu.stanford.smi.protege.widget.SymbolListWidget;
import edu.stanford.smi.protege.widget.TextAreaWidget;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.swrl.SWRLSystemFrames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.individuals.OWLIndividualsTab;
import edu.stanford.smi.protegex.owl.ui.widget.MultiLiteralWidget;
import edu.stanford.smi.protegex.owl.ui.widget.MultiResourceWidget;
import edu.stanford.smi.protegex.owl.ui.widget.RDFListWidget;
import edu.stanford.smi.protegex.owl.ui.widget.SingleLiteralAreaWidget;
import edu.stanford.smi.protegex.owl.ui.widget.SingleLiteralWidget;
import edu.stanford.smi.protegex.owl.ui.widget.SingleResourceWidget;

/**
 * Repairs invalid forms from old pprj files.
 *
 * @author Ray Fergerson  <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */

public class OWLBackwardsCompatibilityProjectFixups implements ProjectFixupPlugin {


    public static final String OWL_BUILD_PROPERTY = "owl_build";  // this implementation was muffed and I am not going to fix it.
    public static final String OWL_MAJOR_BUILD_PROPERTY = "owl_major_build";
    public static final String OWL_DATABASE_INCLUSION = "parser_namespace_database_inclusion_version";

    public static void insertVersionData(PropertyList sources) {
        sources.setString(OWL_BUILD_PROPERTY, OWLText.getBuildNumber());
        sources.setString(OWL_MAJOR_BUILD_PROPERTY, OWLText.getVersion());
        sources.setBoolean(OWL_DATABASE_INCLUSION, true);
    }

    public static void fix(OWLModel owlModel) {
        Project project = owlModel.getProject();
        KnowledgeBase internalKb = project.getInternalProjectKnowledgeBase();

        fixInternalProject(internalKb);
    }


    private static void fixInternalProject(KnowledgeBase internalKb) {
        PropertyList sources = getProjectSources(internalKb);

        if (isReallyAncientVersion(sources)) {
        	Log.getLogger().info("Backwards compatibility fixup for OWL project file (really ancient one)");
            fixReallyAncient(internalKb);
        }
        if (isBeforeDatabaseInclusion(sources)) {
        	Log.getLogger().info("Backwards compatibility fixup for OWL project file (before DB inclusion)");
            fixForDatabaseInclusion(internalKb);
        }
    }


    /*
     * =================================================================================================
     *    Database inclusion fixups...
     */
    public static boolean isBeforeDatabaseInclusion(PropertyList sources) {
        return sources.getBoolean(OWL_DATABASE_INCLUSION) == null;
    }

    @SuppressWarnings("unchecked")
    private static void fixForDatabaseInclusion(KnowledgeBase internalKb) {
        adjustNamespacePrefix(internalKb);
        fixFramesVisibility(internalKb);
    }

    private static void fixFramesVisibility(KnowledgeBase internalKb) {
    	//fix owl:Thing to be visible
    	Instance projectInstance = getProjectInstance(internalKb);
    	fixSWRLVisibility(internalKb);
        ModelUtilities.removeOwnSlotValue(projectInstance, SLOT_HIDDEN_FRAMES, OWLNames.Cls.THING);
    }

    private static void fixSWRLVisibility(KnowledgeBase internalKb) {
    	SWRLSystemFrames swrlSystemFrames = new SWRLSystemFrames(null);

    	Collection<Frame> invisibles = swrlSystemFrames.getFrames();

        Instance projectInstance = getProjectInstance(internalKb);
        
        for (Frame frame : invisibles) {
			ModelUtilities.addOwnSlotValue(projectInstance, SLOT_HIDDEN_FRAMES, frame.getName());
		}
    }

    @SuppressWarnings("unchecked")
    private static void adjustNamespacePrefix(KnowledgeBase internalKb) {
        for (Instance instance : internalKb.getInstances()) {
            for (Slot slot : instance.getOwnSlots()) {
                Collection values = instance.getDirectOwnSlotValues(slot);
                adjustNamespacePrefix(instance, slot,values);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void adjustNamespacePrefix(Instance instance, Slot slot, Collection values) {
        Collection<Object> fixedValues = new ArrayList<Object>();
        boolean needsFixing = false;
        for (Object value : values) {
            if (value instanceof String) {
                fixedValues.add(adjustNamespacePrefix((String) value));
                needsFixing = true;
            }
            else {
                fixedValues.add(value);
            }
        }
        if (needsFixing) {
            instance.setDirectOwnSlotValues(slot, fixedValues);
        }
    }

    private static String adjustNamespacePrefix(String value) {
        String prefix;
        prefix = RDFNames.RDF_PREFIX + ":";
        if  (value.startsWith(prefix)) {
            return replacePrefix(value, prefix, RDFNames.RDF_NAMESPACE);
        }
        prefix = RDFSNames.RDFS_PREFIX + ":";
        if (value.startsWith(prefix)) {
            return replacePrefix(value, prefix,  RDFSNames.RDFS_NAMESPACE);
        }
        prefix = OWLNames.OWL_PREFIX + ":";
        if (value.startsWith(prefix)) {
            return replacePrefix(value, prefix, OWLNames.OWL_NAMESPACE);
        }
        prefix = SWRLNames.SWRL_PREFIX + ":";
        if (value.startsWith(prefix)) {
            return replacePrefix(value, prefix, SWRLNames.SWRL_NAMESPACE);
        }
        prefix = ProtegeNames.PROTEGE_PREFIX + ":";
        if (value.startsWith(prefix)) {
            return replacePrefix(value, prefix, ProtegeNames.PROTEGE_OWL_NAMESPACE);
        }
        return value;
    }

    private static String replacePrefix(String value, String prefix, String namespace) {
        return namespace + value.substring(prefix.length());
    }

    /*
     * =================================================================================================
     *    Really ancient fixups
     */

    public static boolean isReallyAncientVersion(PropertyList sources) {
        return sources.getString(OWLBackwardsCompatibilityProjectFixups.OWL_BUILD_PROPERTY) == null;
    }


    private static void fixReallyAncient(KnowledgeBase internalKb) {
        updateStandardForms(internalKb);
        renameWidgets(internalKb);
        fixFramesVisibility(internalKb);
    }


    private static void renameWidgets(KnowledgeBase internalKb) {
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.cls.OWLClsesTab", OWLClassesTab.class.getName());
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.OWLIndividualsTab", OWLIndividualsTab.class.getName());
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.widget.OWLInstanceListWidget", MultiResourceWidget.class.getName());
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.widget.OWLInstanceFieldWidget", SingleResourceWidget.class.getName());
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.widget.ListInstanceWidget", RDFListWidget.class.getName());
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.widget.LiteralFieldWidget", SingleLiteralWidget.class.getName());
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.widget.LiteralListWidget", MultiLiteralWidget.class.getName());
        renameWidget(internalKb, "edu.stanford.smi.protegex.owl.ui.widget.MultiLanguageStringValueWidget", MultiLiteralWidget.class.getName());
        renameWidget(internalKb, TextFieldWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(internalKb, TextAreaWidget.class.getName(), SingleLiteralAreaWidget.class.getName());
        renameWidget(internalKb, CheckBoxWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(internalKb, ComboBoxWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(internalKb, FloatFieldWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(internalKb, FloatListWidget.class.getName(), MultiLiteralWidget.class.getName());
        renameWidget(internalKb, IntegerFieldWidget.class.getName(), SingleLiteralWidget.class.getName());
        renameWidget(internalKb, IntegerListWidget.class.getName(), MultiLiteralWidget.class.getName());
        renameWidget(internalKb, StringListWidget.class.getName(), MultiLiteralWidget.class.getName());
        renameWidget(internalKb, SymbolListWidget.class.getName(), MultiLiteralWidget.class.getName());
    }

    private static final String SLOT_HIDDEN_FRAMES = "hidden_classes";


    private static void changeInstanceValue(KnowledgeBase kb,
                                            String className,
                                            String slotNameToCheck,
                                            String slotValue,
                                            String slotNameToChange,
                                            Object oldValue,
                                            Object newValue) {
        Cls cls = kb.getCls(className);
        Slot slotToCheck = kb.getSlot(slotNameToCheck);
        Slot slotToChange = kb.getSlot(slotNameToChange);
        Iterator i = cls.getInstances().iterator();
        while (i.hasNext()) {
            Instance instance = (Instance) i.next();
            if (slotValue.equals(instance.getOwnSlotValue(slotToCheck))
                    && oldValue.equals(instance.getOwnSlotValue(slotToChange))) {
                instance.setOwnSlotValue(slotToChange, newValue);
                // Log.trace("set " + slotNameToChange + " to " + newValue, Project.class, "changeInstanceValue");
            }
        }
    }


    private static Instance getClsWidgetInstance(String name, KnowledgeBase kb) {
        Instance result = null;
        Collection values = ModelUtilities
                .getDirectOwnSlotValues(getProjectInstance(kb), "customized_instance_widgets");
        Iterator i = values.iterator();
        while (i.hasNext()) {
            Instance widgetInstance = (Instance) i.next();
            String widgetSlotName = (String) ModelUtilities.getDirectOwnSlotValue(widgetInstance, "name");
            if (name.equals(widgetSlotName)) {
                result = widgetInstance;
                break;
            }
        }
        return result;
    }


    private static KnowledgeBaseFactory getFactory(KnowledgeBase kb) {
        KnowledgeBaseFactory result = null;
        Instance projectInstance = getProjectInstance(kb);
        Instance sources = (Instance) ModelUtilities.getDirectOwnSlotValue(projectInstance, "sources");
        Iterator i = ModelUtilities.getDirectOwnSlotValues(sources, "properties").iterator();
        while (i.hasNext()) {
            Instance property = (Instance) i.next();
            String name = (String) ModelUtilities.getDirectOwnSlotValue(property, "name");
            if (name.equals("factory_class_name")) {
                String factoryName = (String) ModelUtilities.getDirectOwnSlotValue(property, "string_value");
                result = (KnowledgeBaseFactory) SystemUtilities.newInstance(factoryName);
                break;
            }
        }
        return result;
    }


    private static KnowledgeBase getTemplateKnowledgeBase(KnowledgeBase projectKB) {
        Collection errors = new ArrayList();
        Reader clsesReader = Files.getSystemClsesReader();
        KnowledgeBaseFactory factory = getFactory(projectKB);
        String path = (factory == null) ? (String) null : factory.getProjectFilePath();
        Reader instancesReader;
        if (path == null) {
            instancesReader = Files.getSystemInstancesReader();
        }
        else {
            instancesReader = FileUtilities.getResourceReader(factory.getClass(), path);
        }
        return new ClipsKnowledgeBaseFactory().loadKnowledgeBase(clsesReader, instancesReader, errors);
    }


    private static void renameWidget(KnowledgeBase kb, String oldWidgetName, String newWidgetName) {
        changeInstanceValue(kb,
                "Widget",
                "widget_class_name",
                oldWidgetName,
                "widget_class_name",
                oldWidgetName,
                newWidgetName);
    }


    private static boolean replaceFormWidget(String name, KnowledgeBase projectKB, KnowledgeBase templateProjectKB) {
        Instance templateClsWidget = getClsWidgetInstance(name, templateProjectKB);
        Instance projectClsWidget = getClsWidgetInstance(name, projectKB);
        if (projectClsWidget == null) {
            Cls widgetCls = projectKB.getCls("Widget");
            projectClsWidget = projectKB.createInstance(null, widgetCls);
            ModelUtilities.setOwnSlotValue(projectClsWidget, "name", name);
            Instance projectInstance = getProjectInstance(projectKB);
            ModelUtilities.addOwnSlotValue(projectInstance, "customized_instance_widgets", projectClsWidget);
            // Log.trace("added customization for new class " + name, BackwardsCompatibilityProjectFixups.class, "replaceFormWidget");
        }
        Instance templatePropertyList = (Instance) ModelUtilities.getDirectOwnSlotValue(templateClsWidget,
                "property_list");
        Instance newPropertyList = (Instance) templatePropertyList.deepCopy(projectKB, null);
        ModelUtilities.setOwnSlotValue(projectClsWidget, "property_list", newPropertyList);
        return templateClsWidget != null;
    }


    private static void updateStandardForms(KnowledgeBase projectKB) {
        KnowledgeBase templateProjectKB = getTemplateKnowledgeBase(projectKB);
        Instance templateProjectInstance = getProjectInstance(templateProjectKB);
        // Instance projectInstance = getProjectInstance(projectKB);
        Iterator i = ModelUtilities.getDirectOwnSlotValues(templateProjectInstance, "customized_instance_widgets")
                .iterator();
        while (i.hasNext()) {
            Instance widgetInstance = (Instance) i.next();
            String widgetClsName = (String) ModelUtilities.getDirectOwnSlotValue(widgetInstance, "name");
            boolean changed = replaceFormWidget(widgetClsName, projectKB, templateProjectKB);
            // Log.trace(widgetClsName + " form changed= " + changed, BackwardsCompatibilityProjectFixups.class, "updateStandardForms");
        }
    }


    // ******************* interface methods ******************* //

	public void fixProject(KnowledgeBase internalKb) {
		fixInternalProject(internalKb);

	}

	public String getName() {
		return "OWL Backward Project Compatibility Fixups";
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public static boolean isSuitable(KnowledgeBase internalKb, Collection errors) {
		String factory = getProjectSources(internalKb).getString(KnowledgeBaseFactory.FACTORY_CLASS_NAME);

		return factory != null && factory.contains(".owl.");
	}

	 private static final String CLASS_PROJECT = "Project";

	// ********** utility methods *************

    protected static Instance getProjectInstance(KnowledgeBase kb) {
        Instance result = null;
        Cls cls = kb.getCls(CLASS_PROJECT);
        if (cls == null) {
            Log.getLogger().severe("no project class");
        } else {
            Collection<Instance> instances = cls.getDirectInstances();
            // Assert.areEqual(instances.size(), 1);
            result = CollectionUtilities.getFirstItem(instances);
        }
        if (result == null) {
            Log.getLogger().severe("no project instance");
        }
        return result;
    }

    private static final String SLOT_SOURCES = "sources";
    private static PropertyList getProjectSources(KnowledgeBase internalKb) {
    	Instance projectInstance = getProjectInstance(internalKb);
        return new PropertyList((Instance) ModelUtilities.getDirectOwnSlotValue(projectInstance, SLOT_SOURCES));
    }

}
