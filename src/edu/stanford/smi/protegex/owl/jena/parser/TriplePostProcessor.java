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

package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protege.editor.owl.model.hierarchy.roots.Relation;
import org.protege.editor.owl.model.hierarchy.roots.TerminalElementFinder;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.ConsoleFormatter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLSystemFrames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

class TriplePostProcessor extends AbstractStatefulTripleProcessor {
    public static final transient Logger log = Log.getLogger(TriplePostProcessor.class);
    static {
        try {
            initLogger();
        } catch (Throwable t) {
            System.err.println("Could not initialize logger for TriplePostProcessor");
        }
    }

    static void initLogger() {
        Log.makeInheritedLoggersLocal(log);

        Handler[] handlers = log.getHandlers();

        for (Handler handler : handlers) {
            if (handler.getFormatter() instanceof ConsoleFormatter) {
                //replace the existing console handler
                log.removeHandler(handler);

                ConsoleHandler consoleHandler = new ConsoleHandler();
                Formatter consoleFormatter = new TriplePostProcessorLogFormatter();
                consoleHandler.setFormatter(consoleFormatter);
                log.addHandler(consoleHandler);
            }
        }
    }

    //should rather be a list? Is the order of ts important?
    Collection<TripleStore> parsedTripleStores = new HashSet<TripleStore>();

    public TriplePostProcessor(TripleProcessor processor) {
        super(processor);
    }

    @Override
    public void doPostProcessing() {
        parsedTripleStores = globalParserCache.getParsedTripleStores();
        if (parsedTripleStores.size() == 0) {
            /*
             * There are no new parsed triple store to postprocess.
             * Probably all triplestores are database.
             */
            return;
        }

        //undef triples handling
        processor.processUndefTriples();

        /*
         * The reinitCaches() between the post processing calls
         * is necessary for the update of the caches, because
         * we mix high-level and low-level calls.
         */

        //swizzling
        reinitCaches();
        processFramesWithWrongJavaType();
        reinitCaches();
        processMetaclasses();
        reinitCaches();
        processSubclassesOfRdfList();
        reinitCaches();
        processInstancesWithMultipleTypes();
        reinitCaches();

        //create untyped resources if needed
        if (isCreateUntypedResourcesEnabled()) {
            processor.createUntypedResources();
            reinitCaches();
        }

        //classes
        processInferredSuperclasses(); //this should be done after create untyped resources
        reinitCaches();
        processOrphanClses(); //this should be done after create untyped resources
        reinitCaches();
        processGeneralizedConceptInclusions();
        reinitCaches();
        processAbstractClasses();
        reinitCaches();

        //properties
        processDomainAndRange();
        reinitCaches();

        processPossiblyTypedResources();
        reinitCaches();
        processProtegeOWLImport();
        reinitCaches();
    }

    private void processMetaclasses() {
        int userMetaClassesCount = owlModel.getSystemFrames().getRdfsNamedClassClass().getSubclassCount(); // - 36; // 36 comes from experience..

        log.info("Postprocess: Process metaclasses (" + userMetaClassesCount + " metaclasses) ... ");
        long time0 = System.currentTimeMillis();

        for (TripleStore ts : parsedTripleStores) {
            processMetaclasses(ts);
        }

        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void processSubclassesOfRdfList() {
        // there will always be 1 subclass - swrl:atomList
        log.info("Postprocess: Process subclasses of rdf:List (" + owlModel.getRDFListClass().getSubclassCount()
                + " classes) ... ");
        long time0 = System.currentTimeMillis();

        for (TripleStore ts : parsedTripleStores) {
            processSubclassesOfRdfList(ts);
        }
        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void processInferredSuperclasses() {
        log.info("Postprocess: Add inferred superclasses ... ");
        long time0 = System.currentTimeMillis();

        for (TripleStore ts : parsedTripleStores) {
            processInferredSuperclasses(ts);
        }

        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void processAbstractClasses() {
        log.info("Postprocess: Abstract classes... ");
        long time0 = System.currentTimeMillis();

        for (TripleStore ts : parsedTripleStores) {
            processAbstractClasses(ts);
        }
        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void processDomainAndRange() {
        log.info("Postprocess: Domain and range of properties... ");
        long time0 = System.currentTimeMillis();

        for (TripleStore ts : parsedTripleStores) {
            processDomainAndRange(ts);
        }

        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void reinitCaches() {
        owlModel.getFrameStoreManager().reinitialize();
    }

    private boolean isCreateUntypedResourcesEnabled() {
        return ApplicationProperties.getBooleanProperty(ProtegeOWLParser.CREATE_UNTYPED_RESOURCES, true);
    }

    private void processFramesWithWrongJavaType() {
        Set<String> framesWithWrongJavaType = globalParserCache.getFramesWithWrongJavaType();

        log.info("Postprocess: Process entities with incorrect Java type (" + framesWithWrongJavaType.size()
                + " entities) ... ");
        long time0 = System.currentTimeMillis();

        for (Iterator iterator = framesWithWrongJavaType.iterator(); iterator.hasNext();) {
            String frameName = (String) iterator.next();
            try {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Fixing wrong Java type of: " + frameName);
                }
                Instance instance = (Instance) simpleFrameStore.getFrame(frameName);
                if (instance != null) {
                    simpleFrameStore.swizzleInstance(instance);
                }
                iterator.remove();
            } catch (Exception e) {
                log.log(Level.WARNING, "\nError at processing entity with incorrect Java type: " + frameName, e);
            }
        }

        processWrongOneOfTypes();

        if (framesWithWrongJavaType.size() > 0) {
            log.warning("\n    Frames with wrong Java type: " + globalParserCache.getFramesWithWrongJavaType() + "\n");
        }

        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void processWrongOneOfTypes() {
        for (TripleStore ts : parsedTripleStores) {
            NarrowFrameStore nfs = ts.getNarrowFrameStore();
            Collection<Frame> frames = nfs.getFrames(owlModel.getSystemFrames().getRdfTypeProperty(), null, false,
                    owlModel.getSystemFrames().getOwlDataRangeClass());
            for (Frame frame : frames) {
                if (frame instanceof OWLEnumeratedClass) {
                    simpleFrameStore.swizzleInstance((Instance) frame);
                }
            }
        }

    }

    private void processMetaclasses(TripleStore ts) {
        processMetaclasses(owlModel.getRDFSNamedClassClass(), ts);
        processMetaclasses(owlModel.getRDFPropertyClass(), ts);
    }

    private void processMetaclasses(Cls superMetaclass, TripleStore ts) {
        for (Iterator iterator = superMetaclass.getSubclasses().iterator(); iterator.hasNext();) {
            RDFSNamedClass metaclass = (RDFSNamedClass) iterator.next();

            if (!metaclass.isSystem()) {
                for (Object element : ts.getUserDefinedInstancesOf(metaclass, RDFResource.class)) {
                    Instance inst = (Instance) element;
                    //this should be fine, because swizzling does not change anything in the NFS
                    ParserUtil.getSimpleFrameStore(owlModel).swizzleInstance(inst);
                }
            }
        }
    }

    private void processSubclassesOfRdfList(TripleStore ts) {
        processMetaclasses(owlModel.getRDFListClass(), ts);
    }

    @SuppressWarnings( { "deprecation" })
    private void processOrphanClses() {
        Set<RDFSNamedClass> classes = new HashSet<RDFSNamedClass>();
        for (TripleStore ts : parsedTripleStores) {
            classes.addAll(ts.getUserDefinedClasses());
        }
        ;

        TerminalElementFinder<RDFSNamedClass> rootFinder = new TerminalElementFinder<RDFSNamedClass>(
                new Relation<RDFSNamedClass>() {

                    public Collection<RDFSNamedClass> getR(RDFSNamedClass x) {
                        HashSet<RDFSNamedClass> parents = new HashSet<RDFSNamedClass>();

                        for (Object element : x.getDirectSuperclasses()) {
                            if (element instanceof RDFSNamedClass) {
                                parents.add((RDFSNamedClass) element);
                            }
                        }

                        return parents;
                    }
                });

        classes.addAll(owlModel.getSystemFrames().getRdfExternalClassClass().getInstances());
        classes.remove(owlModel.getOWLThingClass());

        log.info("Postprocess: Process orphan classes (" + classes.size() + " classes) ... ");
        long time0 = System.currentTimeMillis();

        reinitCaches();
        Set<RDFSNamedClass> orphanClasses = new HashSet<RDFSNamedClass>();
        try {
            rootFinder.appendTerminalElements(classes);
            orphanClasses.addAll(rootFinder.getTerminalElements());
        } catch (Exception e) {
            log.log(Level.WARNING, "Error at computing orphan classes. Error message: " + e.getMessage(), e);
        } finally {
            rootFinder.clear();
        }

        orphanClasses.remove(owlModel.getOWLThingClass());
        reinitCaches();

        for (RDFSNamedClass cls : orphanClasses) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("processClsesWithoutSupercls: No declared supercls: " + cls + "\n");
            }
            try {
                TripleStore homeTs = owlModel.getTripleStoreModel().getHomeTripleStore(cls);
                FrameCreatorUtility.createSubclassOf(cls, owlModel.getOWLThingClass(), homeTs);
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Error at adding owl:Thing as a superclass of " + cls, e);
            }
        }

        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    @SuppressWarnings( { "unchecked", "deprecation" })
    private void processInferredSuperclasses(TripleStore ts) {
        Collection<RDFSNamedClass> namedClasses = getNamedClassesWithEquivalentClasses(ts);

        for (Object obj : namedClasses) {
            try {
                RDFSNamedClass namedClass = (RDFSNamedClass) obj;

                if (!namedClass.isSystem()) {
                    Collection<Cls> inferredSuperclasses = getInferredSuperClasses(namedClass);

                    for (Cls inferredSupercls : inferredSuperclasses) {
                        if (!FrameCreatorUtility.hasDirectSuperclass(namedClass, inferredSupercls)) {
                            //create the inferred superclass in the same TS and NFS as the class
                            TripleStore homeTs = owlModel.getTripleStoreModel().getHomeTripleStore(namedClass);
                            FrameCreatorUtility.createSubclassOf(namedClass, inferredSupercls, homeTs);
                        }
                    }
                }

            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, " Error at post processing " + obj + "\n", e);
            }
        }
    }

    private Collection<RDFSNamedClass> getNamedClassesWithEquivalentClasses(TripleStore ts) {
        Collection<RDFSNamedClass> classes = new HashSet<RDFSNamedClass>();
        NarrowFrameStore nfs = ts.getNarrowFrameStore();

        Set<Frame> frames = nfs.getFramesWithAnyValue(owlModel.getOWLEquivalentClassProperty(), null, false);
        for (Frame frame : frames) {
            if (frame instanceof RDFSNamedClass) {
                classes.add((RDFSNamedClass) frame);
            }
        }
        return classes;
    }

    private Collection<Cls> getInferredSuperClasses(RDFSNamedClass namedClass) {
        Collection<Cls> inferredSuperClasses = new ArrayList<Cls>();

        //make this into a recursive function
        Collection<RDFSClass> equivClasses = namedClass.getEquivalentClasses(); //can we use here nfs call?
        for (RDFSClass equivClass : equivClasses) {
            try {
                if (equivClass instanceof RDFSNamedClass) {
                    inferredSuperClasses.add(equivClass);
                } else if (equivClass instanceof OWLIntersectionClass) {
                    //add operands if defined
                    Collection<RDFSClass> operands = ((OWLIntersectionClass) equivClass).getOperands();

                    for (RDFSClass operand : operands) {
                        if (operand instanceof RDFSNamedClass) {
                            inferredSuperClasses.add(operand);
                        }
                    }
                }
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Errors at adding inferred superclasses to: " + equivClass, e);
            }
        }

        return inferredSuperClasses;
    }

    private void processInstancesWithMultipleTypes() {
        MultipleTypesInstanceCache multipleTypesInstanceCache = globalParserCache.getMultipleTypesInstanceCache();

        Set<Instance> instancesWithMultipleTypes = multipleTypesInstanceCache.getInstancesWithMultipleTypes();

        log.info("Postprocess: Instances with multiple types (" + instancesWithMultipleTypes.size()
                + " instances) ... ");
        long time0 = System.currentTimeMillis();

        for (Instance instance : instancesWithMultipleTypes) {
            Set<Cls> typesSet = multipleTypesInstanceCache.getTypesForInstanceAsSet(instance);
            adjustTypesOfInstance(instance, typesSet);
            if (log.isLoggable(Level.FINE)) {
                log.fine("process instance with multiple types" + instance + ": " + typesSet + "\n");
            }
        }
        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void adjustTypesOfInstance(Instance instance, Set<Cls> typesSet) {
        Collection<Cls> existingTypes = FrameCreatorUtility.getDirectTypes(instance);
        typesSet.removeAll(existingTypes); // types to add

        String instName = instance.getName();

        for (Cls type : typesSet) {
            instance = (Instance) simpleFrameStore.getFrame(instName);
            /*
             * This is kind of painful. We have to find out where the type
             * triple came from and add the type in the same TS.
             * (What should happen if the same type comes from different TS-es?
             * Which is very likely...)
             */

            TripleStoreModel tsm = owlModel.getTripleStoreModel();
            TripleStore initialActiveTs = tsm.getActiveTripleStore();

            try {
                TripleStore homeTs = tsm.getHomeTripleStore(instance, owlModel.getRDFTypeProperty(), type);
                if (homeTs != null) {
                    tsm.setActiveTripleStore(homeTs);
                    FrameCreatorUtility.addInstanceType(instance, type, homeTs);
                    simpleFrameStore.swizzleInstance(instance);
                } else {
                    log.warning("Could not find home triple store of type triple for " + instance + "\n");
                }
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Error at adjusting types of: " + instance, e);
            } finally {
                tsm.setActiveTripleStore(initialActiveTs);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void processDomainAndRange(TripleStore ts) {
        for (Object element : ts.getUserDefinedProperties()) {
            RDFProperty property = (RDFProperty) element;

            // Do this postprocessing in the TS of the property
            TripleStoreModel tsm = owlModel.getTripleStoreModel();

            TripleStore initialActiveTs = tsm.getActiveTripleStore();

            try {
                TripleStore homeTs = tsm.getHomeTripleStore(property);
                tsm.setActiveTripleStore(homeTs);
                owlModel.getFrameStoreManager().getDomainUpdateFrameStore().synchronizeRDFSDomainWithProtegeDomain(
                        property);

                owlModel.getFrameStoreManager().getRangeUpdateFrameStore()
                        .synchronizeRDFSRangeWithProtegeAllowedValues(property);
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Errors at post-processing domain and range of: " + property, e);
            } finally {
                tsm.setActiveTripleStore(initialActiveTs);
            }
        }
    }

    //this method will be refactored
    private void processGeneralizedConceptInclusions() {
        Collection<RDFSClass> gciAxioms = new ArrayList<RDFSClass>(globalParserCache.getGciAxioms());

        log.info("Postprocess: Generalized Concept Inclusion (" + gciAxioms.size() + " axioms) ... ");
        long time0 = System.currentTimeMillis();

        // now try to give them a good name
        NamespaceManager names = owlModel.getNamespaceManager();
        String namespace = names.getDefaultNamespace();
        if (namespace == null && owlModel.getDefaultOWLOntology() != null) {
            namespace = owlModel.getDefaultOWLOntology().getName();
            if (!namespace.endsWith("#")) {
                namespace = namespace + "#";
            }
        }
        String axiomPrefix = namespace + "Axiom";
        int counter = 0;

        if (namespace != null) {
            for (RDFSClass gci : gciAxioms) {
                try {
                    globalParserCache.removeGciAxiom(gci);
                    while (getFrame(axiomPrefix + counter) != null) {
                        counter++;
                    }
                    gci.rename(axiomPrefix + counter);
                } catch (Exception e) {
                    Log.getLogger().log(Level.WARNING, "Error at post-processing GCI: " + gci, e);
                }
            }
        }

        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    @SuppressWarnings("deprecation")
    private void processAbstractClasses(TripleStore ts) {
        RDFProperty abstractProp = owlModel.getRDFProperty(ProtegeNames.Slot.ABSTRACT);

        if (abstractProp == null) {
            return;
        }

        Collection abstractClses = ts.getNarrowFrameStore().getFrames(abstractProp, null, false, Boolean.TRUE);

        for (Iterator iterator = abstractClses.iterator(); iterator.hasNext();) {
            Object object = iterator.next();

            if (object instanceof RDFSClass) {
                // Do this postprocessing in the TS of the property
                TripleStoreModel tsm = owlModel.getTripleStoreModel();

                TripleStore initialActiveTs = tsm.getActiveTripleStore();

                try {
                    TripleStore homeTs = tsm.getHomeTripleStore((RDFSClass) object);
                    tsm.setActiveTripleStore(homeTs);
                    ((Cls) object).setAbstract(true);
                } catch (Exception e) {
                    Log.getLogger().log(Level.WARNING, "Error at post-processing abstract class: " + object, e);
                } finally {
                    tsm.setActiveTripleStore(initialActiveTs);
                }
            }
        }

    }

    @SuppressWarnings("deprecation")
    private void processPossiblyTypedResources() {
        RDFSClass untypedClass = ((AbstractOWLModel) owlModel).getRDFExternalClassClass();
        RDFSClass untypedProp = ((AbstractOWLModel) owlModel).getRDFExternalPropertyClass();
        RDFSClass untypedRes = ((AbstractOWLModel) owlModel).getRDFExternalResourceClass();

        int count = untypedClass.getDirectInstanceCount() + untypedProp.getDirectInstanceCount()
                + untypedRes.getDirectInstanceCount();

        log.info("Postprocess: Possibly typed entities (" + count + " resources) ... ");
        long time0 = System.currentTimeMillis();

        processPossiblyTypedResources(untypedClass);
        processPossiblyTypedResources(untypedProp);
        processPossiblyTypedResources(untypedRes);

        log.info(System.currentTimeMillis() - time0 + " ms\n");
    }

    private void processPossiblyTypedResources(Cls untypedCls) {
        for (Object element : untypedCls.getDirectInstances()) {
            Instance untypedEntity = (Instance) element;

            if (untypedEntity.getDirectTypes().size() > 1) {
                untypedEntity.removeDirectType(untypedCls); //it will also swizzle
            }
        }
    }

    private Map<RDFResource, RDFSNamedClass> protegeSystemTypeMap = new HashMap<RDFResource, RDFSNamedClass>();
    {
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        protegeSystemTypeMap.put(systemFrames.getDirectedBinaryRelationCls(), systemFrames.getOwlNamedClassClass());
        protegeSystemTypeMap.put(systemFrames.getPalConstraintCls(), systemFrames.getOwlNamedClassClass());
        protegeSystemTypeMap.put(systemFrames.getFromSlot(), systemFrames.getOwlObjectPropertyClass());
        protegeSystemTypeMap.put(systemFrames.getToSlot(), systemFrames.getOwlObjectPropertyClass());
        protegeSystemTypeMap.put(systemFrames.getSlotConstraintsSlot(), systemFrames.getOwlObjectPropertyClass());
        protegeSystemTypeMap.put(systemFrames.getPalStatementSlot(), systemFrames.getOwlDatatypePropertyClass());
        protegeSystemTypeMap.put(systemFrames.getPalDescriptionSlot(), systemFrames.getOwlDatatypePropertyClass());
        protegeSystemTypeMap.put(systemFrames.getPalNameSlot(), systemFrames.getOwlDatatypePropertyClass());
        protegeSystemTypeMap.put(systemFrames.getPalRangeSlot(), systemFrames.getOwlDatatypePropertyClass());
    }

    private void processProtegeOWLImport() {
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore protegeOwlTripleStore = tripleStoreModel.getTripleStore(ProtegeNames.PROTEGE_OWL_ONTOLOGY);
        if (protegeOwlTripleStore == null) {
            return;
        }
        OWLSystemFrames systemFrames = owlModel.getSystemFrames();
        for (Entry<RDFResource, RDFSNamedClass> entry : protegeSystemTypeMap.entrySet()) {
            try {
                RDFResource protegeSysFrame = entry.getKey();
                RDFSNamedClass type = entry.getValue();
                // these assertions are lost from the protege owl triple store because we avoid adding duplicate types.
                FrameCreatorUtility.addInstanceType(protegeSysFrame, type, protegeOwlTripleStore);
                FrameCreatorUtility.addOwnSlotValue(protegeSysFrame, systemFrames.getRdfTypeProperty(), type,
                        protegeOwlTripleStore);
                FrameCreatorUtility.addOwnSlotValue(protegeSysFrame, systemFrames.getNameSlot(), protegeSysFrame
                        .getName(), protegeOwlTripleStore);
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Error at post processing: " + entry.getKey(), e);
            }
        }
        // now we have duplicate information (type, domain, range) contained in both the system frames
        // and the protege owl triple store but maybe nobody will notice.
    }

}
