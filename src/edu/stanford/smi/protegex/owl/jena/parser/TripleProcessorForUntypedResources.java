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


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.vocabulary.OWL;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

class TripleProcessorForUntypedResources extends AbstractStatefulTripleProcessor {

    public TripleProcessorForUntypedResources(TripleProcessor processor) {
        super(processor);
    }

    public void processUndefTriples() {
        processRemainingUndefinedTriples();
    }

    protected void processRemainingUndefinedTriples() {
        Collection<String> emtpyUndefsToRemove = new HashSet<String>();
        Set<String> undefTriplesKeys = new HashSet(processor.getGlobalParserCache().getUndefTriplesKeys());
        for (String undef : undefTriplesKeys) {
            Collection<UndefTriple> undefTriples = processor.getGlobalParserCache().getUndefTriples(undef);

            for (Iterator<UndefTriple> iterator2 = undefTriples.iterator(); iterator2.hasNext();) {
                UndefTriple undefTriple = iterator2.next();

                Object obj = undefTriple.getTripleObj();
                TripleStore undefTripleStore = undefTriple.getTripleStore();

                //special handling of owl:oneOf
                if (ParserUtil.getResourceName(undefTriple.getTriplePred()).equals(OWL.oneOf.getURI())) {
                    handleCreationOfOneOf(undefTriple, undef);
                }

                boolean success = false;

                if (obj instanceof AResource) {
                    success = processor.processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), undefTripleStore, true);
                } else if (obj instanceof ALiteral) {
                    success = processor.processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), undefTripleStore, true);
                }

                if (success) {
                    iterator2.remove();
                }
            }

            //clean-up, if no more undef triples for this undef - moved to outside of for because of concurrent modification
            Collection<UndefTriple> remainingUndefs = processor.getGlobalParserCache().getUndefTriples(undef);
            if (remainingUndefs.isEmpty()) {
                emtpyUndefsToRemove.add(undef);
            }
        }

        for (String emptyUndefKey : emtpyUndefsToRemove) {
            processor.getGlobalParserCache().removeUndefTripleKey(emptyUndefKey);
        }

    }


    private void handleCreationOfOneOf(UndefTriple undefTriple, String undef) {
        AResource tripleSubj = undefTriple.getTripleSubj();
        TripleStore ts = undefTriple.getTripleStore();
        if (ParserUtil.getResourceName(tripleSubj).equals(undef)) {
            try {
                FrameCreatorUtility.createOWLEnumeratedCls(owlModel, ParserUtil.getResourceName(tripleSubj), ts);
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Error at creating enumeration class", e);
            }
        }
    }


    /**
     * This method will create untyped resources for all undefined triples.
     */
    public void createUntypedResources() {
        Collection<String> createdObjs = new HashSet<String>();
        for (String undef : processor.getGlobalParserCache().getUndefTriplesKeys()) {
            Collection<UndefTriple> undefTriples = processor.getGlobalParserCache().getUndefTriples(undef);
            for (UndefTriple undefTriple : undefTriples) {
                try {
                    //check for undefined predicate
                    String pred = ParserUtil.getResourceName(undefTriple.getTriplePred());
                    if (resolvUndefinedTriple(undefTriple, pred)) { //undef predicate was created
                        createdObjs.add(pred);
                    }

                    //check for undefined subjects
                    String subj = ParserUtil.getResourceName(undefTriple.getTripleSubj());
                    if (resolvUndefinedTriple(undefTriple, subj)) { //undef subject was created
                        createdObjs.add(subj);
                    }

                    //check for undefined objects
                    Object objObj = undefTriple.getTripleObj();
                    if (objObj instanceof AResource) {
                        String obj = ParserUtil.getResourceName((AResource)undefTriple.getTripleObj());
                        if (resolvUndefinedTriple(undefTriple, obj)) { //undef object was created
                            createdObjs.add(obj);
                        }
                    }
                } catch (Throwable t) {
                    TriplePostProcessor.log.log(Level.WARNING, "Could not process untyped resource from statement: "
                            + "(" + undefTriple.getTripleSubj() + ", " + undefTriple.getTriplePred() + ", " + undefTriple.getTripleObj() + ")", t);
                }
            }
        }

        //had to do it this way, because of concurrent modif exception
        for (String undef : createdObjs) {
            checkUndefinedResources(undef);
        }

        processUndefTriples();
    }

    /**
     * returns true - if it created new object
     */
    protected boolean resolvUndefinedTriple(UndefTriple undefTriple, String undef) {
        if (owlModel.getRDFResource(undef) == null) {
            RDFResource resource = createUntypedObject(undefTriple, undef);
            return resource != null;
        }
        return false;
    }


    //TODO: create in the right TS!!
    //if property system -> create class, if not -> create instance
    /**
     * Creates either a untyped class, property or resource depending on the triple.
     * @param undefTriple - the undef triple
     */
    protected RDFResource createUntypedObject(UndefTriple undefTriple, String undef) {
        AResource subject = undefTriple.getTripleSubj();
        AResource predicate = undefTriple.getTriplePred();
        Object object = undefTriple.getTripleObj();

        //what to do with resources that already exist?
        RDFResource undefResource = owlModel.getRDFResource(undef);
        if (undefResource != null) {
            return undefResource;
        }

        //The predicate is undefined
        if (undef.equals(ParserUtil.getResourceName(predicate))) {
            return handleUndefinedPredicate(undefTriple);
        }

        //The subject is undefined
        if (undef.equals(ParserUtil.getResourceName(subject))) {
            return handleUndefinedSubject(undefTriple);
        }

        //The object is undefined
        if (undef.equals(object.toString())) {
            return handleUndefinedObject(undefTriple);
        }

        return null;
    }


    protected RDFProperty handleUndefinedPredicate(UndefTriple undefTriple) {
        String predicateName = ParserUtil.getResourceName(undefTriple.getTriplePred());
        Cls extraType = null;
        Object obj = undefTriple.getTripleObj();
        //try to get the most specific type of property
        /*//TODO: probably we need a more complicated algorithm here
		if (obj instanceof AResource) {
			extraType = owlModel.getSystemFrames().getOwlObjectPropertyClass();
		} else if (obj instanceof ALiteral) {
			extraType = owlModel.getSystemFrames().getOwlDatatypePropertyClass();
		}
         */
        return createUntypedPredicate(owlModel, predicateName, extraType);
    }

    @SuppressWarnings("deprecation")
    protected RDFResource handleUndefinedSubject(UndefTriple undefTriple) {
        RDFSClass untypedClassClass = ((AbstractOWLModel)owlModel).getRDFExternalClassClass();

        /*
         * Hopefully the predicate is defined. If not, create an untyped predicate (it will be an RDFProperty).
         * If the predicate will become defined in future, then a new type will be added to it and it will be swizzled.
         */
        RDFProperty property = owlModel.getRDFProperty(ParserUtil.getResourceName(undefTriple.getTriplePred()));

        if (property == null) {
            property = handleUndefinedPredicate(undefTriple);
        }

        String subjectName = ParserUtil.getResourceName(undefTriple.getTripleSubj());

        //custom case for rdf:type
        if (property.equals(owlModel.getSystemFrames().getRdfTypeProperty())) {
            Object tripleObj = undefTriple.getTripleObj();
            if (tripleObj instanceof AResource) {
                return createInstanceWithType(subjectName, ParserUtil.getResourceName((AResource)tripleObj));
            } else {
                Log.getLogger().warning("Invalid triple: " + undefTriple);
                return createUntypedResource(owlModel, subjectName);
            }
        }

        /*
         * Try to create the new resource using the domain of the property
         */
        RDFSClass domain = (RDFSClass) property.getPropertyValue(owlModel.getRDFSDomainProperty());

        if (domain == null) {
            //return owlModel.createRDFUntypedResource(subjectName);
            //it's not clear what the default should be..
            return createUntypedClassOrResource(property, subjectName);
        }

        //domain is not null
        if (domain.equals(owlModel.getOWLThingClass())) {
            //return owlModel.createRDFUntypedResource(subjectName);
            //it's not clear what the default should be..
            return createUntypedClassOrResource(property, subjectName);
        }

        if (domain.equals(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
            return untypedClassClass.createInstance(subjectName);
        }

        if (domain.isMetaclass()) {
            RDFResource untypedClass = domain.createInstance(subjectName);
            //this is fishy -
            if (domain.hasSuperclass(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
                untypedClass.addDirectType(untypedClassClass);
            } else if (domain.hasSuperclass(owlModel.getRDFPropertyClass()) ) {
                untypedClass.addDirectType(((AbstractOWLModel)owlModel).getRDFExternalPropertyClass());
            }
            return untypedClass;
        } else {
            //domain is not metaclass
            return owlModel.createRDFUntypedResource(subjectName);
        }
    }

    @SuppressWarnings("deprecation")
    protected RDFResource handleUndefinedObject(UndefTriple undefTriple) {
        RDFSClass untypedClassClass = ((AbstractOWLModel)owlModel).getRDFExternalClassClass();

        /*
         * Hopefully the predicate is defined. If not, create an untyped predicate (it will be an RDFProperty).
         * If the predicate will become defined in future, then a new type will be added to it and it will be swizzled.
         */
        RDFProperty property = owlModel.getRDFProperty(ParserUtil.getResourceName(undefTriple.getTriplePred()));

        if (property == null) {
            property = handleUndefinedPredicate(undefTriple);
        }

        String objectName = undefTriple.getTripleObj().toString();

        if (property.equals(owlModel.getRDFTypeProperty())) {
            // it's a class
            return untypedClassClass.createInstance(objectName);
        }

        //rdfs:seeAlso is probably just a string or untyped resource
        if (property.equals(owlModel.getSystemFrames().getRdfsSeeAlsoProperty())) {
            return owlModel.createRDFUntypedResource(objectName);
        }

        //owl:versionInfo should be a literal.. create it as an untyped resource for now
        if (property.equals(owlModel.getSystemFrames().getOwlVersionInfoProperty())) {
            return owlModel.createRDFUntypedResource(objectName);
        }

        /*
         * Try to create the new resource using the range of the property
         */
        RDFSClass range = (RDFSClass) property.getPropertyValue(owlModel.getRDFSRangeProperty());

        if (range == null) {
            //return owlModel.createRDFUntypedResource(objectName);
            //it's not clear what the default should be..
            return createUntypedClassOrResource(property, objectName);
        }

        //range is not null
        if (range.equals(owlModel.getOWLThingClass())) {
            //return owlModel.createRDFUntypedResource(objectName);
            //it's not clear what the default should be..
            return createUntypedClassOrResource(property, objectName);
        }

        if (range.equals(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
            return untypedClassClass.createInstance(objectName);
        }

        if (range.isMetaclass()) {
            RDFResource untypedClass = range.createInstance(objectName);
            //this is fishy -
            if (range.hasSuperclass(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
                untypedClass.addDirectType(untypedClassClass);
            } else if ( range.hasSuperclass(owlModel.getRDFPropertyClass()) ) {
                untypedClass.addDirectType(((AbstractOWLModel)owlModel).getRDFExternalPropertyClass());
            }
            return untypedClass;
        } else {
            //domain is not metaclass
            return owlModel.createRDFUntypedResource(objectName);
        }

        //TODO: how to handle dataranges?

    }


    protected RDFResource createUntypedClassOrResource(RDFProperty prop, String name) {
        /*
         * Make a guess: If the property is user defined it is likely that the entity is
         * an individual. Otherwise, we will guess that it is a class (less class cast exceptions)
         */

        if (prop.isSystem()) {
            //special case for rdf:value - it will accomodate SKOS..
            if (prop.equals(owlModel.getSystemFrames().getRdfValueProperty())) {
                return owlModel.createRDFUntypedResource(name);
            }
            return createUntypedClass(owlModel, name);
        } else {
            return owlModel.createRDFUntypedResource(name);
        }

        /*
         * More complicated algorithms can be added here.
         * I hope that this will work for most cases.
         */
    }

    //should be created in the right ts
    @SuppressWarnings("deprecation")
    public static RDFProperty createUntypedPredicate(OWLModel owlModel, String predicateName, Cls extraType) {
        RDFProperty prop = (RDFProperty) ((AbstractOWLModel)owlModel).getRDFExternalPropertyClass().createInstance(predicateName);
        if (extraType != null) {
            prop.addDirectType(extraType);
        }
        return  prop;
    }

    public static RDFResource createUntypedClass(OWLModel owlModel, String className) {
        RDFSClass untypedClassClass = ((AbstractOWLModel)owlModel).getRDFExternalClassClass();
        return untypedClassClass.createInstance(className);
    }

    public static RDFResource createUntypedResource(OWLModel owlModel, String name) {
        return owlModel.createRDFUntypedResource(name);
    }

    //TODO: class cast territory
    private RDFResource createInstanceWithType(String subjectName, String typeName) {
        Cls type = getCls(typeName);
        if (type == null) { //create the type
            type = (Cls) createUntypedClass(owlModel, typeName);
        }
        Instance inst = (Instance)getFrame(subjectName);
        if (inst != null) {
            return (RDFResource) inst; //TODO: should we add the type?
        }
        inst = type.createDirectInstance(subjectName); //rdf:type triple will be added by processTriple
        return (RDFResource)inst;
    }


}
