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

package edu.stanford.smi.protegex.owl.inference.protegeowl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class ReasonerPluginManager {

	private final static String REASONER_PLUGIN = "ReasonerPlugin";

	private static final String IS_SUITABLE_METHOD_NAME = "isSuitable";
	private static final Class<?>[] IS_SUITABLE_METHOD_ARGS = new Class[] {Project.class, Collection.class}; 

	private final static String METHOD_NAME = "getReasonerName";
	private final static Class<?>[] METHOD_ARG_CLASSES = new Class<?>[] {};


	public static Collection<Class> getAvailableReasonerPluginClasses() {
		return PluginUtilities.getClassesWithAttribute(REASONER_PLUGIN, "True");
	}

	public static Collection<Class<? extends ProtegeReasoner>> getSuitableReasonerPluginClasses(Project project) {
		Collection<Class> availablePlugins = getAvailableReasonerPluginClasses();

		Collection<Class<? extends ProtegeReasoner>> suitablePlugins = new ArrayList<Class<? extends ProtegeReasoner>>();

		for (Class<? extends ProtegeReasoner> plugin : availablePlugins) {
			if (isSuitable(project, plugin)) {
				suitablePlugins.add(plugin);
			}
		}

		return suitablePlugins;		
	}



	private static boolean isSuitable(Project project, Class<? extends ProtegeReasoner> reasonerPluginClass) {
		boolean isSuitable;

		try {
			Collection errors = new ArrayList();
			Method method = reasonerPluginClass.getMethod(IS_SUITABLE_METHOD_NAME, IS_SUITABLE_METHOD_ARGS);
			Boolean returnValue = (Boolean) method.invoke(reasonerPluginClass, new Object[] { project, errors });
			isSuitable = returnValue.booleanValue();
		} catch (NoSuchMethodException e) {
			isSuitable = true;
		} catch (Exception e) {
			isSuitable = false;
			Log.getLogger().warning(e.getMessage());
		}

		return isSuitable;
	}



	public static ProtegeReasoner getReasonerPluginInstance(OWLModel owlModel, Class<? extends ProtegeReasoner> reasonerJavaClass) {		
		ProtegeReasoner reasoner = getReasonerPluginInstance(reasonerJavaClass);

		if (reasoner != null) {
			reasoner.setOWLModel(owlModel);
		}

		return reasoner;
	}

	public static ProtegeReasoner getReasonerPluginInstance(Class<? extends ProtegeReasoner> reasonerJavaClass) {		
		ProtegeReasoner reasoner = null;
		try {			
			reasoner = (ProtegeReasoner) reasonerJavaClass.newInstance();			
		}
		catch (Exception ex) {
			Log.getLogger().log(Level.WARNING, "Could not create plugin for " + reasonerJavaClass, ex);
		}

		return reasoner;
	}

	public static ProtegeReasoner getReasonerPluginInstance(String reasonerJavaClassName) {
		Class<? extends ProtegeReasoner> reasonerJavaClass = getReasonerJavaClass(reasonerJavaClassName);

		if (reasonerJavaClass == null) {
			Log.getLogger().warning("Cannot find reasoner java class " + reasonerJavaClassName);
			return null;
		}

		return getReasonerPluginInstance(reasonerJavaClass);		
	}

	public static ProtegeReasoner getReasonerPluginInstance(OWLModel owlModel, String reasonerJavaClassName) {
		Class<? extends ProtegeReasoner> reasonerJavaClass = getReasonerJavaClass(reasonerJavaClassName);

		if (reasonerJavaClass == null) {
			Log.getLogger().warning("Cannot find reasoner java class " + reasonerJavaClassName);
			return null;
		}

		return getReasonerPluginInstance(owlModel, reasonerJavaClass);
	}


	@SuppressWarnings("unchecked")
	public static Class<? extends ProtegeReasoner> getReasonerJavaClass(String reasonerJavaClassName) {
		return PluginUtilities.forName(reasonerJavaClassName, true);
	}

	public static Collection<String> getSuitableReasonerNames(OWLModel owlModel) {
		ArrayList<String> names = new ArrayList<String>(getSuitableReasonerMap(owlModel).keySet());

		Collections.sort(names);		
		return names;
	}

	public static Map<String,String> getSuitableReasonerMap(OWLModel owlModel) {
		Map<String, String> name2JavaClassName = new HashMap<String, String>();

		for (Class<? extends ProtegeReasoner> reasonerClass : getSuitableReasonerPluginClasses(owlModel.getProject())) {		
			name2JavaClassName.put(getReasonerName(reasonerClass), reasonerClass.getName());						
		}	

		return name2JavaClassName;
	}


	public static String getReasonerName(Class<? extends ProtegeReasoner> reasonerClass) {
		String name = null;
		try {
			Method method = reasonerClass.getMethod(METHOD_NAME, METHOD_ARG_CLASSES);
			name = (String) method.invoke(null, new Object[] {});
		} catch (Exception e) {
			Log.getLogger().warning("Could not find reasoner name for: " + reasonerClass.getName());
			name = reasonerClass.getSimpleName();
		}
		return name;
	}


}
