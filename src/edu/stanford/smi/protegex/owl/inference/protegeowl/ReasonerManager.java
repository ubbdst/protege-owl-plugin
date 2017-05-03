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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.DefaultProtegeDIGReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.util.DefaultUncaughtExceptionHandler;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 22, 2004<br><br>
 *
 * @author Matthew Horridge <matthew.horridge@cs.man.ac.uk>
 * @author Tania Tudorache <tudorache@stanford.edu>
 */
public class ReasonerManager implements Disposable {

	public static final String CURRENT_REASONER_PROPERTY = "current.reasoner.class.name";

    private Map<OWLModel, ProtegeReasoner> owlModel2reasonerMap;

    private static ReasonerManager instance;

    static {
    	//move this to a more central place?
    	initializeUncaughtExceptionHandler();
    }


    private ProjectListener projectListener = new ProjectAdapter() {
        @Override
		public void projectClosed(ProjectEvent event) {
            Project project = (Project) event.getSource();
            disposeReasoner((OWLModel) project.getKnowledgeBase());
        }
    };


    private ReasonerManager() {
        owlModel2reasonerMap = new HashMap<OWLModel, ProtegeReasoner>();
    }


    private static void initializeUncaughtExceptionHandler() {
    	try {
    		Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Could not initialize default uncaught exception handler", e);
		}

	}


	public synchronized static ReasonerManager getInstance() {
        if (instance == null) {
            instance = new ReasonerManager();
        }

        return instance;
    }


    /**
     * Gets the DIG reasoner for the specified knowledge base
     *
     * @param owlModel The {@link OWLModel}
     * @return A reasoner to be used for reasoning over
     *         the specified knowledge base.
     * @deprecated
     */
    @Deprecated
	public ProtegeOWLReasoner getReasoner(OWLModel owlModel) {
    	ProtegeReasoner reasoner = owlModel2reasonerMap.get(owlModel);

    	if (reasoner == null) {
    		return (ProtegeOWLReasoner) getProtegeReasoner(owlModel, getDefaultDIGReasonerClass(), false);
    	}

    	if (reasoner instanceof ProtegeOWLReasoner) {
    		return (ProtegeOWLReasoner) reasoner;
    	}

    	return (ProtegeOWLReasoner) replaceReasoner(owlModel, getDefaultDIGReasonerClass());
    }


    public ProtegeReasoner getProtegeReasoner(OWLModel owlModel) {
    	return getProtegeReasoner(owlModel, getDefaultReasonerClass(), false);
    }


    private ProtegeReasoner getProtegeReasoner(OWLModel owlModel, Class reasonerJavaClass, boolean replace ) {

    	if (replace == true) {
    		disposeReasoner(owlModel);
    	}

    	if (reasonerJavaClass == null) {
    		return null;
    	}

    	ProtegeReasoner reasoner = owlModel2reasonerMap.get(owlModel);

    	if (reasoner == null) {
    		reasoner = internalCreateReasoner(owlModel, reasonerJavaClass);

    		owlModel2reasonerMap.put(owlModel, reasoner);
    		owlModel.getProject().addProjectListener(projectListener);
    	}

    	return reasoner;
    }


    private ProtegeReasoner internalCreateReasoner(OWLModel owlModel, Class reasonerJavaClass) {
    	if (reasonerJavaClass == null) {
    		return null;
    	}

    	return ReasonerPluginManager.getReasonerPluginInstance(owlModel, reasonerJavaClass);
    }


    /**
     * @deprecated Use <code>createProtegeReasoner(owlModel, DefaultProtegeOWLReasoner.class)</code>
     */
    @Deprecated
	public ProtegeOWLReasoner getReasoner(OWLModel owlModel, boolean createNew) {
        return createReasoner(owlModel);
    }


    /**
     * @deprecated Use <code>createProtegeReasoner(owlModel, DefaultProtegeOWLReasoner.class)</code>
     */
    @Deprecated
	public ProtegeOWLReasoner createReasoner(OWLModel owlModel) {
        return (ProtegeOWLReasoner) createProtegeReasoner(owlModel, getDefaultDIGReasonerClass());
    }


    public ProtegeReasoner createProtegeReasoner(OWLModel owlModel) {
		return createProtegeReasoner(owlModel, getDefaultReasonerClass());
    }

    public ProtegeReasoner createProtegeReasoner(OWLModel owlModel, Class reasonerJavaClass) {
    	return getProtegeReasoner(owlModel, reasonerJavaClass, true);
    }


	public String getDefaultReasonerClassName() {
		String className = ApplicationProperties.getString(CURRENT_REASONER_PROPERTY, getDefaultDIGReasonerClass().getName());

		if (className.equals(ReasonerPluginMenuManager.NONE_REASONER)) {
			return ReasonerPluginMenuManager.NONE_REASONER;
		}

		//make sure that this class exists, otherwise use the DIG resoner as the default;
		Class reasonerClass = ReasonerPluginManager.getReasonerJavaClass(className);
		if (reasonerClass == null) {
			className = getDefaultDIGReasonerClass().getName();
		}

		return className;
	}

	public Class getDefaultReasonerClass() {
		return ReasonerPluginManager.getReasonerJavaClass(getDefaultReasonerClassName());
	}

	public void setDefaultReasonerClass(String javaClassName) {
		if (javaClassName == null) {
			javaClassName = ReasonerPluginMenuManager.NONE_REASONER;
		}
		ApplicationProperties.setString(ReasonerManager.CURRENT_REASONER_PROPERTY, javaClassName);
	}



	private ProtegeReasoner replaceReasoner(OWLModel owlModel, Class reasonerJavaClass) {
		disposeReasoner(owlModel);

		return createProtegeReasoner(owlModel, reasonerJavaClass);
	}


	public void disposeReasoner(OWLModel owlModel) {
		ProtegeReasoner existingReasoner = owlModel2reasonerMap.get(owlModel);

		try {
			if (existingReasoner != null) {
				existingReasoner.dispose();

				owlModel.getProject().removeProjectListener(projectListener);
				owlModel2reasonerMap.remove(owlModel);
			}
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at disposing reasoner " + existingReasoner, e);
		}
	}

	public Class getDefaultDIGReasonerClass() {
		return DefaultProtegeDIGReasoner.class;
	}


	public ProtegeReasoner setProtegeReasonerClass(OWLModel owlModel, Class reasonerJavaClass) {
		ProtegeReasoner reasoner = replaceReasoner(owlModel, reasonerJavaClass);

		return reasoner;
	}


	public ProtegeReasoner setProtegeReasonerClass(OWLModel owlModel, String reasonerJavaClassName) {
		Class reasonerJavaClass = null;

		if (reasonerJavaClassName != null && !reasonerJavaClassName.equals(ReasonerPluginMenuManager.NONE_REASONER)) {
			reasonerJavaClass = ReasonerPluginManager.getReasonerJavaClass(reasonerJavaClassName);
		}

		return setProtegeReasonerClass(owlModel, reasonerJavaClass);
	}


	public boolean hasAttachedReasoner(OWLModel owlModel) {
		return owlModel2reasonerMap.get(owlModel) != null;
	}


	/* Releases all the owl model <-> reasonser connections and
	 * disposes all reasoners.
	 *
	 * @see edu.stanford.smi.protege.util.Disposable#dispose()
	 */
	public void dispose() {
		for (OWLModel owlModel : owlModel2reasonerMap.keySet()) {
			disposeReasoner(owlModel);
		}
		owlModel2reasonerMap.clear();

		ReasonerUtil.getInstance().dispose();
	}

}


