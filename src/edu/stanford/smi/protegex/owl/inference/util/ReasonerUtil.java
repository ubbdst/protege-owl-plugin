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

package edu.stanford.smi.protegex.owl.inference.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitorAdapter;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 20, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerUtil implements Disposable{

    private static ReasonerUtil instance;

    private Map<OWLModel, Collection<RDFSClass>> namedClsesMap;

    private Map<OWLModel, Collection<OWLDatatypeProperty>> datatypePropertiesMap;

    private Map<OWLModel, Collection<OWLObjectProperty>> objectPropertiesMap;
    
    private Map<OWLModel, Collection<RDFIndividual>> individualsMap;
    
    private Set<OWLModel> owlModelsWithListener; 

    private ProjectListener projectListener = new ProjectAdapter() {
        public void projectClosed(ProjectEvent event) {
            Project project = (Project) event.getSource();
            dispose((OWLModel) project.getKnowledgeBase());            
        }
    };

    private ModelListener modelListener = new ModelAdapter() {
        public void classCreated(RDFSClass cls) {
            invalidateNamedClsCache(cls.getOWLModel());
        }


        public void classDeleted(RDFSClass cls) {
            invalidateNamedClsCache(cls.getOWLModel());
        }


        public void propertyCreated(RDFProperty property) {
            invalidatePropertiesCache(property.getOWLModel());
        }


        public void propertyDeleted(RDFProperty property) {
            invalidatePropertiesCache(property.getOWLModel());
        }


        public void individualCreated(RDFResource resource) {
            invalidateIndividualsCache(resource.getOWLModel());
        }


        public void individualDeleted(RDFResource resource) {
            invalidateIndividualsCache(resource.getOWLModel());
        }
    };

    private long lastClassificationTime;


    protected ReasonerUtil() {
        initHashMaps();
        lastClassificationTime = 0;
    }


    public static synchronized ReasonerUtil getInstance() {
        if (instance == null) {
            instance = new ReasonerUtil();
        }

        return instance;
    }


    private void initHashMaps() {
        namedClsesMap = new HashMap<OWLModel, Collection<RDFSClass>>();
        datatypePropertiesMap = new HashMap<OWLModel, Collection<OWLDatatypeProperty>>();
        objectPropertiesMap = new HashMap<OWLModel, Collection<OWLObjectProperty>>();
        individualsMap = new HashMap<OWLModel, Collection<RDFIndividual>>();
        owlModelsWithListener = new HashSet<OWLModel>();
    }


    /**
     * Returns a collection of named classes.  This collection
     * is cached.  If named classes are added or deleted, the
     * cache is emptied an rebuilt.
     *
     * @param kb The knowledge base that contains the named classes
     */
    public Collection getNamedClses(OWLModel kb) {
        // If we haven't got a cache for the knowledge base
        // then create one.
        if (namedClsesMap.containsKey(kb) == false) {
            namedClsesMap.put(kb, getFilteredNamedClasses(kb));

            addListeners(kb);
        }


        Collection namedClses = (Collection) namedClsesMap.get(kb);

        if (namedClses == null) {
            namedClses = getFilteredNamedClasses(kb);

            namedClsesMap.put(kb, namedClses);
        }

        return namedClses;
    }

    public Collection<SWRLImp> getSWRLImps(OWLModel owlModel)
    {
    	SWRLFactory swrlFactory = new SWRLFactory(owlModel);
    	
    	return swrlFactory.getImps();
    }
        
    private Collection getFilteredNamedClasses(OWLModel owlModel) {
    	Collection allNamedClasses = owlModel.getUserDefinedOWLNamedClasses();
    	
    	try {
        	//filter out SWRL classes, if present
        	if  (owlModel.getOWLJavaFactory() instanceof SWRLJavaFactory) {
        		SWRLFactory swrlFactory = new SWRLFactory(owlModel);
        		Collection swrlClasses = swrlFactory.getSWRLClasses();
        		allNamedClasses.removeAll(swrlClasses);
        	}			
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at filtering out the SWRL classes from the classes sent to the reasoner", e);
		}
    	
    	return allNamedClasses;
    }

    
    public Collection<RDFProperty> getProperties(OWLModel kb) {
    	//should we care about rdf:Properties?
    	ArrayList<RDFProperty> allProps = new ArrayList<RDFProperty>();
    	allProps.addAll(getDataTypeProperties(kb));
    	allProps.addAll(getObjectProperties(kb));
    	
    	return allProps;
    }
    
    
    public Collection<OWLDatatypeProperty> getDataTypeProperties(OWLModel kb) {
        // If we haven't got a cache for the knowledge base
        // then create one.
        if (datatypePropertiesMap.containsKey(kb) == false) {
            datatypePropertiesMap.put(kb, getFilteredProperties(kb, kb.getUserDefinedOWLDatatypeProperties()));

            addListeners(kb);
        }


        Collection<OWLDatatypeProperty> properties = (Collection<OWLDatatypeProperty>) datatypePropertiesMap.get(kb);

        if (properties == null) {
            properties = getFilteredProperties(kb, kb.getUserDefinedOWLDatatypeProperties());

            datatypePropertiesMap.put(kb, properties);
        }

        return properties;
    }

    public Collection<OWLObjectProperty> getObjectProperties(OWLModel kb) {
        // If we haven't got a cache for the knowledge base
        // then create one.
        if (objectPropertiesMap.containsKey(kb) == false) {
            objectPropertiesMap.put(kb, getFilteredProperties(kb, kb.getUserDefinedOWLObjectProperties()));

            addListeners(kb);
        }


        Collection<OWLObjectProperty> properties = (Collection<OWLObjectProperty>) objectPropertiesMap.get(kb);

        if (properties == null) {
            properties = getFilteredProperties(kb, kb.getUserDefinedOWLObjectProperties());

            objectPropertiesMap.put(kb, properties);
        }

        return properties;
    }
    
    
    private <T> Collection<T> getFilteredProperties(OWLModel owlModel, Collection<T> properties) {
    	ArrayList<T> allProps = new ArrayList<T>(properties);
    	try {
        	//filter out SWRL properties, if present
        	if  (owlModel.getOWLJavaFactory() instanceof SWRLJavaFactory) {
        		SWRLFactory swrlFactory = new SWRLFactory(owlModel);
        		Collection swrlProperties = swrlFactory.getSWRLProperties();
        		Collection swrlbProperties = swrlFactory.getSWRLBProperties();
        		allProps.removeAll(swrlProperties);
        		allProps.removeAll(swrlbProperties);
        	}			
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at filtering out the SWRL properties from the properties sent to the reasoner", e);
		}
    	
    	return allProps;
    }
    

    public Collection getIndividuals(OWLModel kb) {
        if (individualsMap.containsKey(kb) == false) {
            individualsMap.put(kb, getOWLIndividuals(kb));
            addListeners(kb);
        }

        Collection individuals = (Collection) individualsMap.get(kb);

        if (individuals == null) {
            individuals = getOWLIndividuals(kb);
            individualsMap.put(kb, individuals);
        }

        return individuals;
    }


    private Collection getOWLIndividuals(OWLModel model) {
        IndividualsFilter filter = new IndividualsFilter(model);
        return filter.getOWLIndividuals();
    }


    private class IndividualsFilter extends OWLModelVisitorAdapter {

        private Collection individuals;

        private OWLModel model;

        public IndividualsFilter(OWLModel model) {
            this.model = model;            
        }


        public Collection getOWLIndividuals() {
            individuals = new ArrayList();
            for (Iterator it = model.getOWLIndividuals().iterator(); it.hasNext();) {
                RDFResource curRes = (RDFResource) it.next();
                curRes.accept(this);
            }
            return individuals;
        }


        public void visitOWLIndividual(OWLIndividual owlIndividual) {
        	individuals.add(owlIndividual);
        }
        
        @Override
        public void visitSWRLIndividual(SWRLIndividual swrlIndividual) {
        	//ignore SWRL individuals
        }
        
        @Override
        public void visitSWRLAtomListIndividual(SWRLAtomList swrlAtomList) {
        	//ignore SWRL atom list
        }   
        
    }


    public void invalidateNamedClsCache(OWLModel kb) {
        namedClsesMap.put(kb, null);
    }


    public void invalidatePropertiesCache(OWLModel kb) {
        objectPropertiesMap.put(kb, null);
        datatypePropertiesMap.put(kb, null);
    }


    public void invalidateIndividualsCache(OWLModel kb) {
        individualsMap.put(kb, null);
    }


    public long getLastClassificationTime() {
        return lastClassificationTime;
    }


    public void setLastClassificationTime() {
        lastClassificationTime = System.currentTimeMillis();
    }


    public OWLNamedClass getNamedReferent(OWLAnonymousClass cls) {
        OWLAnonymousClass expRoot, newExpRoot;

        expRoot = cls.getExpressionRoot();

        while (true) {
            newExpRoot = expRoot.getExpressionRoot();

            if (newExpRoot == expRoot) {
                break;
            }

            expRoot = newExpRoot;
        }

        // Check to see if the class has any named sub classes
        OWLNamedClass namedCls = null;

        Iterator subclsIt = cls.getNamedSubclasses().iterator();

        if (subclsIt.hasNext()) {
            namedCls = (OWLNamedClass) subclsIt.next();
        }

        return namedCls;
    }
    
    private void addListeners(OWLModel owlModel) {
    	if (!owlModelsWithListener.contains(owlModel)) {
    		owlModel.addModelListener(modelListener);
    		owlModel.getProject().addProjectListener(projectListener);
    		owlModelsWithListener.add(owlModel);
    	}
    }
    
    
    private void removeListeners(OWLModel owlModel) {
    	if (owlModelsWithListener.contains(owlModel)) {
    		owlModel.removeModelListener(modelListener);
    		owlModel.getProject().removeProjectListener(projectListener);
    		owlModelsWithListener.remove(owlModel);
    	}
    }
    
    private void dispose(OWLModel owlModel) {
    	namedClsesMap.remove(owlModel);
    	datatypePropertiesMap.remove(owlModel);
    	objectPropertiesMap.remove(owlModel);
    	individualsMap.remove(owlModel);
    	removeListeners(owlModel);    	
    }
    
    public void dispose() {
    	for (OWLModel owlModel : owlModelsWithListener) {
			dispose(owlModel);
		}
    	
    	namedClsesMap.clear();
    	datatypePropertiesMap.clear();
    	objectPropertiesMap.clear();
    	individualsMap.clear();
    	owlModelsWithListener.clear();
    }
    
    public static void handleOutOfMemory() {
    	ReasonerManager.getInstance().dispose();
		
		System.gc();
		System.runFinalization();
		System.gc();
    }
    
}

