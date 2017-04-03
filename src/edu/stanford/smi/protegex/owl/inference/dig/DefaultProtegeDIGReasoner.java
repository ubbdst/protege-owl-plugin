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

package edu.stanford.smi.protegex.owl.inference.dig;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGError;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasoner;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DefaultDIGReasoner;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.logger.DIGLogger;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.logger.DIGLoggerListener;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ErrorMessageLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogger;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskAdapter;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskEvent;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.ClassifyTaxonomyTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetAncestorConceptsTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetConceptIntersectionSuperclassesTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetConceptSatisfiableTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetDescendantConceptsTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetEquivalentConceptsTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetIndividualInferredTypesTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetIndividualsBelongingToConceptTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetSubConceptsTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetSubsumptionRelationshipTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.GetSuperConceptsTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.IsConceptIntersectionSatisfiableTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.IsDisjointToTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.IsSubsumedByTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.SynchronizeReasonerTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.UpdateEquivalentClassesTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.UpdateInconsistentClassesTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.UpdateInferredHierarchyTask;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.digreasoner.UpdateInferredTypesTask;
import edu.stanford.smi.protegex.owl.inference.reasoner.AbstractProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerPreferences;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 14, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultProtegeDIGReasoner extends AbstractProtegeReasoner implements ProtegeOWLReasoner, ProtegeReasoner {
    private static transient Logger log = Log.getLogger(DefaultProtegeDIGReasoner.class);

    private DIGReasoner reasoner;

    private String kbURI;
    
    private DIGLoggerListener digLoggerListener = new DIGLoggerListener() {
        public void errorLogged(DIGError error) {
            RDFResource cause = owlModel.getOWLNamedClass(error.getID());
            ReasonerLogger.getInstance().postLogRecord(new ErrorMessageLogRecord(cause, error.getMessage(), null));
        }
    };
  

    public DefaultProtegeDIGReasoner() {
    	super();
        this.reasoner = new DefaultDIGReasoner();
        DIGLogger.getInstance(this.reasoner).addListener(digLoggerListener);
        setURL(ReasonerPreferences.getInstance().getReasonerURL());     
    }


    /**
     * Gets the DIGReasoner that this reasoner uses
     * to talk to the external DIG Reasoner Process
     */
    public DIGReasoner getDIGReasoner() {
        return reasoner;
    }

    public void setURL(String url) {
        try {
            if (reasoner.getReasonerURL().equals(url) == false) {
                if (kbURI != null) {
                    reasoner.releaseKnowledgeBase(kbURI);
                    kbURI = null;
                    forceReasonerReSynchronization();
                }
                reasoner.setReasonerURL(url);
            }
        }
        catch (DIGReasonerException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public String getURL() {
        return reasoner.getReasonerURL();
    }


    public boolean isConnected() {
        try {
            reasoner.getIdentity();
            return true;
        }

        catch (DIGReasonerException ex) {
            return false;
        }
    }

    
    public void reset() {
    	 try {
			getDIGReasoner().clearKnowledgeBase(getReasonerKnowledgeBaseURI());
		} catch (DIGReasonerException e) {
			Log.getLogger().log(Level.WARNING, "Error at resetting model", e);
		}
    }
    
    
    public String getReasonerKnowledgeBaseURI() {
        return kbURI;
    }

    protected void synchronizeReasoner(ReasonerTaskListener taskListener) throws DIGReasonerException {
        // Render the ontology into DIG and send it to the external
        // dig reasoner
        if (synchronizeReasoner == true || kbURI == null) {
            SynchronizeReasonerTask synchronizeReasonerTask = new SynchronizeReasonerTask(this);
            if (taskListener != null) {
                synchronizeReasonerTask.addTaskListener(taskListener);
            }

            if (kbURI == null) {
                kbURI = reasoner.createKnowledgeBase();
            }

            synchronizeReasonerTask.run();

            if (taskListener != null) {
                synchronizeReasonerTask.removeTaskListener(taskListener);
            }

            synchronizeReasoner = false;
        }
    }

    public DIGReasonerIdentity getIdentity() {
        DIGReasonerIdentity id = null;

        try {
            id = reasoner.getIdentity();
        }
        catch (DIGReasonerException e) {
          Log.getLogger().severe(e.getMessage());
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Exception caught", e);
          }
        }

        return id;
    }

	public static String getReasonerName() {
		return "DIG Reasoner";
	}
    

    public OWLModel getKnowledgeBase() {
        return getOWLModel();
    }


    public void classifyTaxonomy(ReasonerTaskListener taskListener) throws DIGReasonerException {
        ClassifyTaxonomyTask task = new ClassifyTaxonomyTask(this);    
      	performTask(task, taskListener);		        
    }
    
    @Override
    public void classifyTaxonomy() throws ProtegeReasonerException {
        ClassifyTaxonomyTask task = new ClassifyTaxonomyTask(this);    
      	performTask(task);
    }

    public void computeInferredHierarchy(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateInferredHierarchyTask task = new UpdateInferredHierarchyTask(this);
       	performTask(task, taskListener);	
    }

    @Override
    public void computeInferredHierarchy() throws ProtegeReasonerException {
        UpdateInferredHierarchyTask task = new UpdateInferredHierarchyTask(this);
       	performTask(task);	
    }

    public void computeEquivalentConcepts(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateEquivalentClassesTask task = new UpdateEquivalentClassesTask(this);
       	performTask(task, taskListener);	
    }
    
    @Override
    public void computeEquivalentConcepts() throws ProtegeReasonerException {
        UpdateEquivalentClassesTask task = new UpdateEquivalentClassesTask(this);
       	performTask(task);	
    }


    public void computeInconsistentConcepts(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateInconsistentClassesTask task = new UpdateInconsistentClassesTask(this);
       	performTask(task, taskListener);
    }
    
    @Override
    public void computeInconsistentConcepts() throws ProtegeReasonerException {
        UpdateInconsistentClassesTask task = new UpdateInconsistentClassesTask(this);
       	performTask(task);    
    }


    public void computeInferredIndividualTypes(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateInferredTypesTask task = new UpdateInferredTypesTask(this);
        performTask(task, taskListener);
    }

    @Override
    public void computeInferredIndividualTypes() throws ProtegeReasonerException {
        UpdateInferredTypesTask task = new UpdateInferredTypesTask(this);
        performTask(task);
    }
    

    public boolean isSatisfiable(OWLClass aClass,
                                 ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetConceptSatisfiableTask task = new GetConceptSatisfiableTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }
    
    @Override
    public boolean isSatisfiable(OWLClass class1) throws ProtegeReasonerException {
        GetConceptSatisfiableTask task = new GetConceptSatisfiableTask(class1, this);
        performTask(task);
        return task.getResult();
    }


    public boolean isIntersectionSatisfiable(OWLClass[] clses,
                                             ReasonerTaskListener taskListener) throws DIGReasonerException {
        IsConceptIntersectionSatisfiableTask task = new IsConceptIntersectionSatisfiableTask(clses, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public boolean isIntersectionSatisfiable(OWLClass[] clses) throws ProtegeReasonerException {
        IsConceptIntersectionSatisfiableTask task = new IsConceptIntersectionSatisfiableTask(clses, this);
        performTask(task);
        return task.getResult();
    }
    

    public boolean isSubsumedBy(OWLClass cls1,
                                OWLClass cls2,
                                ReasonerTaskListener taskListener) throws DIGReasonerException {
        IsSubsumedByTask task = new IsSubsumedByTask(this, cls1, cls2);
        performTask(task, taskListener);
        return task.getResult();
    }
    
    @Override
    public boolean isSubsumedBy(OWLClass cls1, OWLClass cls2) throws ProtegeReasonerException {
        IsSubsumedByTask task = new IsSubsumedByTask(this, cls1, cls2);
        performTask(task);
        return task.getResult();
    }


    public boolean isDisjointTo(OWLClass cls1,
                                OWLClass cls2,
                                ReasonerTaskListener taskListener) throws DIGReasonerException {
        IsDisjointToTask task = new IsDisjointToTask(this, cls1, cls2);
        performTask(task, taskListener);
        return task.getResult();
    }
    
    @Override
    public boolean isDisjointTo(OWLClass cls1, OWLClass cls2) throws ProtegeReasonerException {
        IsDisjointToTask task = new IsDisjointToTask(this, cls1, cls2);
        performTask(task);
        return task.getResult();
    }


    public int getSubsumptionRelationship(OWLClass cls1,
                                          OWLClass cls2,
                                          ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetSubsumptionRelationshipTask task = new GetSubsumptionRelationshipTask(this, cls1, cls2);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public int getSubsumptionRelationship(OWLClass cls1, OWLClass cls2)	throws ProtegeReasonerException {
        GetSubsumptionRelationshipTask task = new GetSubsumptionRelationshipTask(this, cls1, cls2);
        performTask(task);
        return task.getResult();    
    }

    public Collection getSuperclasses(OWLClass aClass,
                                      ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetSuperConceptsTask task = new GetSuperConceptsTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public Collection<OWLClass> getSuperclasses(OWLClass class1) throws ProtegeReasonerException {
        GetSuperConceptsTask task = new GetSuperConceptsTask(class1, this);
        performTask(task);
        return task.getResult();    
    }
    

    public Collection getSuperclassesOfIntersection(OWLClass[] clses,
                                                    ReasonerTaskListener taskListener)
            throws DIGReasonerException {
        GetConceptIntersectionSuperclassesTask task = new GetConceptIntersectionSuperclassesTask(clses, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public Collection<OWLClass> getSuperclassesOfIntersection(OWLClass[] clses)
    		throws ProtegeReasonerException {
        GetConceptIntersectionSuperclassesTask task = new GetConceptIntersectionSuperclassesTask(clses, this);
        performTask(task);
        return task.getResult();
    }

    public Collection getSubclasses(OWLClass aClass,
                                    ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetSubConceptsTask task = new GetSubConceptsTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public Collection<OWLClass> getSubclasses(OWLClass class1) throws ProtegeReasonerException {
        GetSubConceptsTask task = new GetSubConceptsTask(class1, this);
        performTask(task);
        return task.getResult();
    }
    

    public Collection getAncestorClasses(OWLClass aClass,
                                         ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetAncestorConceptsTask task = new GetAncestorConceptsTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public Collection<OWLClass> getAncestorClasses(OWLClass class1) throws ProtegeReasonerException {
        GetAncestorConceptsTask task = new GetAncestorConceptsTask(class1, this);
        performTask(task);
        return task.getResult();
    }

    public Collection getDescendantClasses(OWLClass aClass,
                                           ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetDescendantConceptsTask task = new GetDescendantConceptsTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public Collection<OWLClass> getDescendantClasses(OWLClass class1)
    		throws ProtegeReasonerException {
        GetDescendantConceptsTask task = new GetDescendantConceptsTask(class1, this);
        performTask(task);
        return task.getResult();
    }

    public Collection getEquivalentClasses(OWLClass aClass,
                                           ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetEquivalentConceptsTask task = new GetEquivalentConceptsTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }
    
    @Override
    public Collection<OWLClass> getEquivalentClasses(OWLClass class1)
    		throws ProtegeReasonerException {
        GetEquivalentConceptsTask task = new GetEquivalentConceptsTask(class1, this);
        performTask(task);
        return task.getResult();
    }


    public Collection getIndividualsBelongingToClass(OWLClass aClass,
                                                     ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetIndividualsBelongingToConceptTask task = new GetIndividualsBelongingToConceptTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    @Override
    public Collection<OWLIndividual> getIndividualsBelongingToClass(OWLClass class1)
    		throws ProtegeReasonerException {
        GetIndividualsBelongingToConceptTask task = new GetIndividualsBelongingToConceptTask(class1, this);
        performTask(task);
        return task.getResult();
    }

    public Collection getIndividualTypes(OWLIndividual individual,
                                         ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetIndividualInferredTypesTask task = new GetIndividualInferredTypesTask(individual, this);
        performTask(task, taskListener);
        return task.getResult();
    }

    
    @Override
    public Collection<OWLClass> getIndividualTypes(OWLIndividual individual)
    		throws ProtegeReasonerException {
        GetIndividualInferredTypesTask task = new GetIndividualInferredTypesTask(individual, this);
        performTask(task);
        return task.getResult();
    }
    
    
    /************************** MANAGEMENT STUFF ************************************/
    
    
    @Override
    protected ProjectListener getProjectListener() {
    	projectListener = new ProjectAdapter() {
	        public void projectClosed(ProjectEvent event) {
	            // If the reasoner still contains a model then release it
	            if (kbURI != null) {
	                try {
	                    reasoner.releaseKnowledgeBase(kbURI);
	                    kbURI = null;
	                    // Remove listeners from model
	                    removeListeners();
	                    //owlModel.getProject().removeProjectListener(projectListener);
	                    owlModel = null;
	                }
	                catch (DIGReasonerException e) {
	                  Log.getLogger().log(Level.SEVERE, "Exception caught", e);
	                }
	            }
	        }
	    };
	    
	    return projectListener;
    }

    /**
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
                            ReasonerTaskListener taskListener) throws DIGReasonerException {
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
        catch (DIGReasonerException e) {
            String oldKbURI = kbURI;

            // Flag that the knowledgebase need recreating
            kbURI = null;

            // Attempt to release the old model.  This
            // may result in exceptions, but we have
            // already flagged that the knowledgebase
            // needs recreating
            reasoner.releaseKnowledgeBase(oldKbURI);

            synchronizeReasoner = true;

            throw e;
        } catch (ProtegeReasonerException e) {
            String oldKbURI = kbURI;

            // Flag that the knowledgebase need recreating
            kbURI = null;

            // Attempt to release the old model.  This
            // may result in exceptions, but we have
            // already flagged that the knowledgebase
            // needs recreating
            reasoner.releaseKnowledgeBase(oldKbURI);

            synchronizeReasoner = true;

            throw new DIGReasonerException(e.getMessage(), e);
        }
        finally {
            if (taskListener != null) {
                task.removeTaskListener(taskListener);
            }

        }

        td.markEnd();

        ReasonerLogger.getInstance().postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Total time: " + td, null));
    }


    public void dispose() {
    	try {    		
    		if (kbURI != null) {
    			reasoner.releaseKnowledgeBase(kbURI);
    			kbURI = null;
    		}
    		reasonerTaskListener = null;
    		DIGLogger.getInstance(this.reasoner).removeListener(digLoggerListener);
    		super.dispose();    		
    	} catch (Exception e) {
    		Log.getLogger().log(Level.WARNING, "Errors at disposing DIG reasoner", e);
    	}
    }

}

