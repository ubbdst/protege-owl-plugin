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

package edu.stanford.smi.protegex.owl.inference.reasoner;

import java.util.Collection;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogger;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskAdapter;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskEvent;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner.ClassifyTaxonomyTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner.SynchronizeReasonerTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner.UpdateInconsistentClassesTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner.UpdateInferredHierarchyTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner.UpdateInferredTypesTask;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.event.ResourceAdapter;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;


public abstract class AbstractProtegeReasoner implements ProtegeReasoner {
	
	protected OWLModel owlModel;
	
	protected ReasonerTaskListener reasonerTaskListener;

	protected boolean synchronizeReasoner = true;
    protected boolean autoSynchReasoner = true;

    /* A ModelListener so we can listen to model
	events and determine whether or not we need to sync the
	reasoner. */
    protected ModelListener modelListener;

    protected ResourceListener resourceAdapter;

    protected PropertyListener propertyListener;

    protected ClassListener classListener;

    protected PropertyValueListener propertyValueListener;

    protected ProjectListener projectListener;

    
	protected AbstractProtegeReasoner() {			
		projectListener = getProjectListener();
		resourceAdapter = getResourceListener();
		propertyListener = getPropertyListener();
		classListener = getClassListener();
		propertyValueListener = getPropertyValueListener();
		modelListener = getModelListener();
	}

	
	public void setReasonerTaskListener(ReasonerTaskListener reasonerTaskListener) {
		this.reasonerTaskListener = reasonerTaskListener;		
	}
	
	public ReasonerTaskListener getReasonerTaskListener() {	
		return reasonerTaskListener;
	}
	

	public void setOWLModel(OWLModel owlModel) {	    
		if (this.owlModel != null) {
			removeListeners();
			try {
				reset();
			} catch (Throwable e) {
				Log.getLogger().warning("Error at releasing old knowledgebase " + owlModel + " from reasoner. " +
						"Error message: " + e.getMessage());
			}
		}
		this.owlModel = owlModel;
		forceReasonerReSynchronization();
		addListeners();
	}

	public void dispose() {
		removeListeners();
	}

	public void initialize() {
		
	}
	
	public void rebind() {
		reset();
		setOWLModel(owlModel);
	}
	
	public void classifyTaxonomy() throws ProtegeReasonerException {
		ClassifyTaxonomyTask task = new ClassifyTaxonomyTask(this);
		performTask(task);		
	}
	

	public void computeInferredHierarchy() throws ProtegeReasonerException {
		 UpdateInferredHierarchyTask task = new UpdateInferredHierarchyTask(this);								
		 performTask(task);
		}
	

	public void computeInconsistentConcepts() throws ProtegeReasonerException {
		UpdateInconsistentClassesTask task = new UpdateInconsistentClassesTask(this);					
		performTask(task);	
	}
	

	public void computeInferredIndividualTypes() throws ProtegeReasonerException {
		UpdateInferredTypesTask task = new UpdateInferredTypesTask(this);							
		performTask(task);				
	}
	
	public void computeEquivalentConcepts() throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}

	public Collection<OWLClass> getEquivalentClasses(OWLClass class1) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}
	
	public Collection<OWLClass> getAncestorClasses(OWLClass class1) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public Collection<OWLClass> getDescendantClasses(OWLClass class1)
			throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public Collection<OWLClass> getIndividualTypes(OWLIndividual individual)
			throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}

		
	public Collection<OWLClass> getIndividualDirectTypes(OWLIndividual owlIndividual)	throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}
	
	public Collection<OWLIndividual> getIndividualsBelongingToClass(OWLClass class1)
			throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public OWLModel getOWLModel() {		
		return owlModel;
	}


	public Collection<OWLIndividual> getRelatedIndividuals(OWLIndividual subject,
			OWLObjectProperty objectProperty) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public Collection getRelatedValues(OWLIndividual subject, OWLDatatypeProperty datatypeProperty)
			throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public Collection<OWLClass> getSubclasses(OWLClass class1) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public int getSubsumptionRelationship(OWLClass cls1, OWLClass cls2)
			throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public Collection<OWLClass> getSuperclasses(OWLClass class1) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public Collection<OWLClass> getSuperclassesOfIntersection(OWLClass[] clses)	throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}

	
	public Collection<OWLProperty> getSubProperties(OWLProperty property) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}

	
	public Collection<OWLProperty> getSuperProperties(OWLProperty property) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public Collection<OWLProperty> getAncestorProperties(OWLProperty property) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}

	
	public Collection<OWLProperty> getDescendantProperties(OWLProperty property) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}

	
	
	public boolean isDisjointTo(OWLClass cls1, OWLClass cls2) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public boolean isIntersectionSatisfiable(OWLClass[] clses) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public boolean isSatisfiable(OWLClass class1) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}


	public boolean isSubsumedBy(OWLClass cls1, OWLClass cls2) throws ProtegeReasonerException {
		throw new ProtegeReasonerException("Operation not supported by this reasoner");
	}
	
	
	
	/*
     ****************** Task ***************************
     */

	
	public void performTask(ReasonerTask task) throws ProtegeReasonerException {
		performTask(task, reasonerTaskListener);
	}
	
	/*
     * Executes the specified task after synchronizing the reasoner if necessary.
     * The task listener (if not <code>null</code>)  is automatically registered
     * with the task before task execution, and the unregistered after task
     * execution.
     *
     * @param task         The task to be executed.
     * @param taskListener The listener to be registered with the task.  May be
     *                     <code>null</code> if no listener should be registered.
     */
    public void performTask(ReasonerTask task,
                            ReasonerTaskListener taskListener) throws ProtegeReasonerException {
        TimeDifference td = new TimeDifference();

        td.markStart();

        final ReasonerTaskListener tskLsnr = taskListener;

        ReasonerTaskAdapter taskAdapter;

        if (taskListener != null) {
            taskAdapter = new ReasonerTaskAdapter() {
                // Don't override task completed


                public void addedToTask(ReasonerTaskEvent event) {
                    tskLsnr.addedToTask(event);
                }


                public void progressChanged(ReasonerTaskEvent event) {
                    tskLsnr.progressChanged(event);
                }


                public void progressIndeterminateChanged(ReasonerTaskEvent event) {
                    tskLsnr.progressIndeterminateChanged(event);
                }


                public void descriptionChanged(ReasonerTaskEvent event) {
                    tskLsnr.descriptionChanged(event);
                }


                public void messageChanged(ReasonerTaskEvent event) {
                    tskLsnr.messageChanged(event);
                }


                public void taskFailed(ReasonerTaskEvent event) {
                    tskLsnr.taskFailed(event);
                }
            };
        }
        else {
            taskAdapter = new ReasonerTaskAdapter();
        }

        synchronizeReasoner(taskAdapter);

        if (taskListener != null) {
            task.addTaskListener(taskListener);
        }

        try {
            task.run();
        }
        catch (ProtegeReasonerException e) {
            // Attempt to release the old model.  This
            // may result in exceptions, but we have
            // already flagged that the knowledgebase           
        	reset();
            synchronizeReasoner = true;

            ReasonerLogger.getInstance().postLogRecord(ReasonerLogRecordFactory.getInstance().createErrorMessageLogRecord(e.getMessage(), null));
            task.setRequestAbort();
            
            throw e;
        }
        finally {
            if (taskListener != null) {
                task.removeTaskListener(taskListener);
            }

        }

        td.markEnd();

        ReasonerLogger.getInstance().postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Total time: " + td, null));
    }
	

    /*
     * ***************** Listeners ***************************
     */
    
    protected void removeListeners() {
    	if (owlModel == null) {
    		return;
    	}
    	
        owlModel.removeModelListener(modelListener);
        owlModel.removeResourceListener(resourceAdapter);
        owlModel.removePropertyListener(propertyListener);
        owlModel.removeClassListener(classListener);
        owlModel.removePropertyValueListener(propertyValueListener);
        owlModel.getProject().removeProjectListener(projectListener);
    }


    protected void addListeners() {
    	if (owlModel == null) {
    		return;
    	}
    	
    	owlModel.getProject().addProjectListener(projectListener);
        owlModel.addModelListener(modelListener);
        owlModel.addResourceListener(resourceAdapter);
        owlModel.addPropertyListener(propertyListener);
        owlModel.addClassListener(classListener);
        owlModel.addPropertyValueListener(propertyValueListener);
    }
    
    
    protected ClassListener getClassListener() {
    	classListener = new ClassAdapter() {
            public void addedToUnionDomainOf(RDFSClass cls,
                                             RDFProperty property) {
                reactToKnowledgeBaseChange();
            }


            public void instanceAdded(RDFSClass cls,
                                      RDFResource instance) {
                reactToKnowledgeBaseChange();
            }


            public void instanceRemoved(RDFSClass cls,
                                        RDFResource instance) {
                reactToKnowledgeBaseChange();
            }


            public void removedFromUnionDomainOf(RDFSClass cls,
                                                 RDFProperty property) {
                reactToKnowledgeBaseChange();
            }


            public void subclassAdded(RDFSClass cls,
                                      RDFSClass subclass) {
                reactToKnowledgeBaseChange();
            }


            public void subclassRemoved(RDFSClass cls,
                                        RDFSClass subclass) {
                reactToKnowledgeBaseChange();
            }


            public void superclassAdded(RDFSClass cls,
                                        RDFSClass superclass) {
                reactToKnowledgeBaseChange();
            }


            public void superclassRemoved(RDFSClass cls,
                                          RDFSClass superclass) {
                reactToKnowledgeBaseChange();
            }
    	};
    	
    	return classListener;
    }
    
    
    protected PropertyListener getPropertyListener() {
    	propertyListener = new PropertyAdapter() {
            public void subpropertyAdded(RDFProperty property,
                                         RDFProperty subproperty) {
                reactToKnowledgeBaseChange();
            }


            public void subpropertyRemoved(RDFProperty property,
                                           RDFProperty subproperty) {
                reactToKnowledgeBaseChange();
            }


            public void superpropertyAdded(RDFProperty property,
                                           RDFProperty superproperty) {
                reactToKnowledgeBaseChange();
            }


            public void superpropertyRemoved(RDFProperty property,
                                             RDFProperty superproperty) {
                reactToKnowledgeBaseChange();
            }


            public void unionDomainClassAdded(RDFProperty property,
                                              RDFSClass rdfsClass) {
                reactToKnowledgeBaseChange();
            }


            public void unionDomainClassRemoved(RDFProperty property,
                                                RDFSClass rdfsClass) {
                reactToKnowledgeBaseChange();
            }
        };
        
        return propertyListener;
    }
    
    protected PropertyValueListener getPropertyValueListener() {
    	propertyValueListener = new PropertyValueAdapter() {
            public void nameChanged(RDFResource resource,
                                    String oldName) {
                reactToKnowledgeBaseChange();
            }


            public void propertyValueChanged(RDFResource resource,
                                             RDFProperty property,
                                             Collection oldValues) {
                reactToKnowledgeBaseChange();
            }
        };
        
        return propertyValueListener;
    }
    
    protected ResourceListener getResourceListener() {
    	resourceAdapter = new ResourceAdapter() {
            public void typeAdded(RDFResource resource,
                                  RDFSClass type) {
                reactToKnowledgeBaseChange();
            }


            public void typeRemoved(RDFResource resource,
                                    RDFSClass type) {
                reactToKnowledgeBaseChange();
            }
        };
        
        return resourceAdapter;
    }
    
    protected ModelListener getModelListener() {
    	 modelListener = new ModelAdapter() {
    	        public void classCreated(RDFSClass cls) {
    	            reactToKnowledgeBaseChange();
    	        }


    	        public void classDeleted(RDFSClass cls) {
    	            reactToKnowledgeBaseChange();
    	        }


    	        public void propertyCreated(RDFProperty property) {
    	            reactToKnowledgeBaseChange();
    	        }


    	        public void propertyDeleted(RDFProperty property) {
    	            reactToKnowledgeBaseChange();
    	        }


    	        public void individualCreated(RDFResource resource) {
    	            reactToKnowledgeBaseChange();
    	        }


    	        public void individualDeleted(RDFResource resource) {
    	            reactToKnowledgeBaseChange();
    	        }
    	        
    	        @Override
    	        public void resourceReplaced(RDFResource oldResource, RDFResource newResource, String oldName) {
    	        	reactToKnowledgeBaseChange();
    	        }
    	    };
    	 return modelListener;
    }
    
    
    protected ProjectListener getProjectListener() {
    	projectListener = new ProjectAdapter() {
    		@Override
    		public void projectClosed(ProjectEvent event) {
    			if (owlModel != null) {
    				removeListeners();
    				owlModel = null;
    			}
    		}
    	};
    	
    	return projectListener;
    }
    
    /*
     * ***************** Synchronization ***************************
     */
    		
    
    protected void synchronizeReasoner(ReasonerTaskListener taskListener) throws ProtegeReasonerException {
        // Render the ontology into DIG and send it to the external
        // dig reasoner
        if (synchronizeReasoner == true) {
            SynchronizeReasonerTask synchronizeReasonerTask = new SynchronizeReasonerTask(this);
            if (taskListener != null) {
                synchronizeReasonerTask.addTaskListener(taskListener);
            }

            synchronizeReasonerTask.run();

            if (taskListener != null) {
                synchronizeReasonerTask.removeTaskListener(taskListener);
            }

            synchronizeReasoner = false;
        }
    }
    
    
    protected void reactToKnowledgeBaseChange() {
        if (autoSynchReasoner) {
            forceReasonerReSynchronization();
        }
    }


    public void forceReasonerReSynchronization() {
        synchronizeReasoner = true;
    }


    public void setAutoSynchronizationEnabled(boolean b) {
        autoSynchReasoner = b;
    }


    public boolean isAutoSynchronizationEnabled() {
        return autoSynchReasoner;
    }

    
	
	
}
