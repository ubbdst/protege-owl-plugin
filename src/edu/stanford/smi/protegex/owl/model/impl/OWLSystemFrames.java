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

package edu.stanford.smi.protegex.owl.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameFactory;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.MaximumCardinalityConstraint;
import edu.stanford.smi.protege.model.RoleConstraint;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.ValueTypeConstraint;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.XSDNames;

/*
 * This class is very repetitious - it essentially represents the owl metadata
 * ontology written in Java.  For this rea  son I have declared and
 * defined and asserted facts about classes and slots in the order
 * that they appear in the (non-OWL) Protege classes and slots tab.
 */

/*
 * There are some translations that you need to make to map this ontology to the rdf.rdf, rdfs.rdf, owl.rdf ontologies.
 * 
 * First there is a little weirdness about owl:Class.  For some reason, the protege system ontology views owl:Class as 
 * the class of owl named classes.  Broken!  Fixing this would be good but it seems pretty awkward at this point.  If you
 * want to talk about the class of all owl classes then use the owlClassMetaCls (which shouldn't really be in this ontology
 * except for this weird owl:Class interpretation).
 * 
 * I don't think that the above weirdness applies as much to rdfs:Class even though it is named with the variable  and methods
 * rdfsNamedClassClass.
 * 
 * Second, we don't have an rdf:Resource and we use owl:Thing for that purpose.  Very owl full-ish even for owl dl ontologies.
 * This comes up in the setting of domains because every property should have a domain.  When it is everything we use owl:Thing.
 * 
 * There is probably lots of other  weirdness but these are the ones that jump  out at me now.
 * 
 * It would be really cool if this were done right...
 */


public abstract class OWLSystemFrames extends SystemFrames {
    private static final transient Logger log = Log.getLogger(OWLSystemFrames.class);
    protected OWLModel owlModel;

    /*
     * Declarations of meta class entities.
     * 
     *  Cls declarations in the order that they appear in the Clses tab.
     */
    private OWLNamedClass owlThingClass;
    private RDFSNamedClass owlClassMetaCls;
    private OWLNamedClass rdfsNamedClassClass;
    private OWLNamedClass owlNamedClassClass;
    private RDFSNamedClass owlDeprecatedClassClass;
    private OWLNamedClass anonymousClassMetaCls;
    private RDFSNamedClass owlEnumeratedClassClass;
    private RDFSNamedClass owlRestrictionClass;
    private RDFSNamedClass owlAllValuesFromClass;
    private RDFSNamedClass owlHasValueClass;
    private RDFSNamedClass owlMaxCardinalityClass;
    private RDFSNamedClass owlMinCardinalityClass;
    private RDFSNamedClass owlCardinalityClass;
    private RDFSNamedClass owlSomeValuesFromClass;
    private RDFSNamedClass owlLogicalClassClass;
    private RDFSNamedClass owlComplementClassClass;
    private RDFSNamedClass owlIntersectionClassClass;
    private RDFSNamedClass owlUnionClassClass;
    private OWLNamedClass rdfPropertyClass;
    private OWLNamedClass  owlDatatypePropertyClass;
    private OWLNamedClass  owlObjectPropertyClass;
    private OWLNamedClass owlInverseFunctionalPropertyClass;
    private OWLNamedClass owlSymmetricPropertyClass;
    private OWLNamedClass owlTransitivePropertyClass;
    private OWLNamedClass owlAnnotationPropertyClass;
    private OWLNamedClass owlFunctionalPropertyClass;
    private RDFSNamedClass owlDeprecatedPropertyClass;
    private RDFSNamedClass rdfsDatatypeClass;
    private OWLNamedClass owlOntologyClass;
    private OWLNamedClass owlNothingClass;
    private RDFSNamedClass rdfListClass;
    private RDFSNamedClass owlAllDifferentClass;
    private RDFSNamedClass rdfsContainerClass;
    private RDFSNamedClass rdfAltClass;
    private RDFSNamedClass rdfBagClass;
    private RDFSNamedClass rdfSeqClass;
    private RDFSNamedClass rdfStatementClass;
    private RDFSNamedClass owlDataRangeClass;
    private RDFSNamedClass anonymousRootCls;
    private RDFSNamedClass rdfExternalResourceClass;
    private RDFSNamedClass rdfExternalClassClass;
    private RDFSNamedClass rdfExternalPropertyClass;
    private RDFSNamedClass owlOntologyPointerClass;
    private OWLNamedClass directedBinaryRelation;
    private OWLNamedClass palConstraintCls;
    

    /*
     * Slot declarations in the order that they appear in the Slot tab.
     */
    private RDFProperty owlAllValuesFromProperty;
    private RDFProperty owlBackwardCompatibleWithProperty;
    private RDFProperty owlCardinalityProperty;
    private RDFProperty owlComplementOfProperty;
    private RDFProperty owlDifferentFromProperty;
    private RDFProperty owlDisjointWithProperty;
    private RDFProperty owlDistinctMembersProperty;
    private RDFProperty owlEquivalentClassProperty;
    private RDFProperty owlEquivalentPropertyProperty;
    private RDFProperty owlHasValueProperty;
    private RDFProperty owlImportsProperty;
    private RDFProperty owlIncompatibleWithProperty;
    private RDFProperty owlIntersectionOfProperty;
    private RDFProperty owlInverseOfProperty;
    private RDFProperty owlMaxCardinalityProperty;
    private RDFProperty owlMinCardinalityProperty;
    private RDFProperty owlOneOfProperty;
    private RDFProperty owlOnPropertyProperty;
    private RDFProperty owlPriorVersionProperty;
    private RDFProperty owlSameAsProperty;
    private RDFProperty owlSomeValuesFromProperty;
    private RDFProperty owlUnionOfProperty;
    private RDFProperty owlValuesFromProperty;
    private OWLDatatypeProperty owlVersionInfoProperty;
    private RDFProperty protegeClassificationStatusProperty;
    private RDFProperty protegeInferredSubclassesProperty;
    private RDFProperty protegeInferredSuperclassesProperty;
    private RDFProperty protegeInferredTypeProperty;
    private RDFProperty rdfFirstProperty;
    private RDFProperty rdfObjectProperty;
    private RDFProperty rdfPredicateProperty;
    private RDFProperty rdfRestProperty;
    private RDFProperty rdfSubjectProperty;
    private RDFProperty rdfTypeProperty;
    private RDFProperty rdfValueProperty;
    private OWLDatatypeProperty rdfsCommentProperty;
    private RDFProperty rdfsDomainProperty;
    private RDFProperty rdfsIsDefinedByProperty;
    private RDFProperty rdfsLabelProperty;
    private RDFProperty rdfsMemberProperty;
    private RDFProperty rdfsRangeProperty;
    private RDFProperty rdfsSeeAlsoProperty;
    private RDFProperty rdfsSubClassOfProperty;
    private RDFProperty rdfsSubPropertyOf;
    private RDFProperty owlOntologyPrefixesProperty;
    private RDFProperty owlResourceURIProperty;
    private RDFProperty owlOntologyPointerProperty;
    private OWLObjectProperty toSlot;
    private OWLObjectProperty fromSlot;
    private OWLObjectProperty slotConstraints;
    private OWLDatatypeProperty palStatementSlot;
    private OWLDatatypeProperty palDescriptionSlot;
    private OWLDatatypeProperty palNameSlot;
    private OWLDatatypeProperty palRangeSlot;
    
    /* 
     * Instance Declarations
     */
    private RDFList rdfNil;
    private Set<RDFSDatatype> rdfDatatypes = new HashSet<RDFSDatatype>();   
    private Set<RDFSDatatype> floatDatatypes = new HashSet<RDFSDatatype>();
    private Set<RDFSDatatype> integerDatatypes = new HashSet<RDFSDatatype>();
	
    public OWLSystemFrames(OWLModel owlModel) {
        super(owlModel);
        this.owlModel = owlModel;
        createOWLMetaModel();
    }
	
    /* ******************************************************************************
     * Creating the model elements - nothing is asserted about these elements
     *                               until they are added to the frame store
     */
	
    /*
     * first some utilities for the creation.
     */
	
    protected OWLNamedClass createOWLNamedClass(String name) {
        FrameID id = new FrameID(name);
        OWLNamedClass cls = new DefaultOWLNamedClass(owlModel, id);
        addFrame(id, cls);
        return cls;
    }
    
    protected RDFSNamedClass createRDFSNamedClass(String name) {
        FrameID id = new FrameID(name);
        RDFSNamedClass cls = new DefaultRDFSNamedClass(owlModel, id);
        addFrame(id, cls);
        return cls;
    }

    protected RDFProperty createRDFProperty(String name) {
        FrameID id = new FrameID(name);
        RDFProperty property = new DefaultRDFProperty(owlModel, id);
        addFrame(id, property);
        return property;
    }
    
    protected OWLDatatypeProperty createOWLDatatypeProperty(String name) {
    	FrameID id = new FrameID(name);
    	OWLDatatypeProperty property = new DefaultOWLDatatypeProperty(owlModel, id);
    	addFrame(id, property);
    	return property;
    }
    
    protected OWLObjectProperty createOWLObjectProperty(String name) {
    	FrameID id = new FrameID(name);
    	OWLObjectProperty property = new DefaultOWLObjectProperty(owlModel, id);
    	addFrame(id, property);
    	return property;
    }
    
    protected RDFSDatatype createRDFSDatatype(String name) {
        FrameID id = new FrameID(name);
        RDFSDatatype frame = new DefaultRDFSDatatype(owlModel, id);
        addFrame(id, frame);
        rdfDatatypes.add(frame);
        return frame;
    }
    
    /*
     * Now the work of actually creating the model.
     */
	
    private void createOWLMetaModel() {
        createOWLClses();
        createOWLSlots();
        createOWLInstances();
        removeUnusedProtegeFrames();
    }
	
    private void createOWLClses() {
        owlThingClass = createOWLNamedClass(OWLNames.Cls.THING);
        owlClassMetaCls = createRDFSNamedClass(OWLNames.Cls.OWL_CLASS);
        rdfsNamedClassClass = createOWLNamedClass(RDFSNames.Cls.NAMED_CLASS);
        owlNamedClassClass = createOWLNamedClass(OWLNames.Cls.NAMED_CLASS);
        owlDeprecatedClassClass = createRDFSNamedClass(OWLNames.Cls.DEPRECATED_CLASS);
        anonymousClassMetaCls = createOWLNamedClass(OWLNames.Cls.ANONYMOUS_CLASS);
        owlEnumeratedClassClass = createRDFSNamedClass(OWLNames.Cls.ENUMERATED_CLASS);
        owlRestrictionClass = createRDFSNamedClass(OWLNames.Cls.RESTRICTION);
        owlAllValuesFromClass = createRDFSNamedClass(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION);
        owlHasValueClass = createRDFSNamedClass(OWLNames.Cls.HAS_VALUE_RESTRICTION);
        owlMaxCardinalityClass = createRDFSNamedClass(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION);
        owlMinCardinalityClass = createRDFSNamedClass(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION);
        owlCardinalityClass = createRDFSNamedClass(OWLNames.Cls.CARDINALITY_RESTRICTION);
        owlSomeValuesFromClass = createRDFSNamedClass(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
        owlLogicalClassClass = createRDFSNamedClass(OWLNames.Cls.LOGICAL_CLASS);
        owlComplementClassClass = createRDFSNamedClass(OWLNames.Cls.COMPLEMENT_CLASS);
        owlIntersectionClassClass = createRDFSNamedClass(OWLNames.Cls.INTERSECTION_CLASS);
        owlUnionClassClass = createRDFSNamedClass(OWLNames.Cls.UNION_CLASS);
        rdfPropertyClass = createOWLNamedClass(RDFNames.Cls.PROPERTY);
        owlDatatypePropertyClass = createOWLNamedClass(OWLNames.Cls.DATATYPE_PROPERTY);
        owlObjectPropertyClass = createOWLNamedClass(OWLNames.Cls.OBJECT_PROPERTY);
        owlInverseFunctionalPropertyClass = createOWLNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY);
        owlSymmetricPropertyClass = createOWLNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY);
        owlTransitivePropertyClass = createOWLNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY);
        owlAnnotationPropertyClass = createOWLNamedClass(OWLNames.Cls.ANNOTATION_PROPERTY);
        owlFunctionalPropertyClass = createOWLNamedClass(OWLNames.Cls.FUNCTIONAL_PROPERTY);
        owlDeprecatedPropertyClass = createRDFSNamedClass(OWLNames.Cls.DEPRECATED_PROPERTY);
        rdfsDatatypeClass = createRDFSNamedClass(RDFSNames.Cls.DATATYPE);
        owlOntologyClass = createOWLNamedClass(OWLNames.Cls.ONTOLOGY);
        owlNothingClass = createOWLNamedClass(OWLNames.Cls.NOTHING);
        rdfListClass = createRDFSNamedClass(RDFNames.Cls.LIST);
        owlAllDifferentClass = createRDFSNamedClass(OWLNames.Cls.ALL_DIFFERENT);
        rdfsContainerClass = createRDFSNamedClass(RDFSNames.Cls.CONTAINER);
        rdfAltClass = createRDFSNamedClass(RDFNames.Cls.ALT);
        rdfBagClass = createRDFSNamedClass(RDFNames.Cls.BAG);
        rdfSeqClass = createRDFSNamedClass(RDFNames.Cls.SEQ);
        rdfStatementClass = createRDFSNamedClass(RDFNames.Cls.STATEMENT);
        owlDataRangeClass = createRDFSNamedClass(OWLNames.Cls.DATA_RANGE);
        anonymousRootCls= createRDFSNamedClass(OWLNames.Cls.ANONYMOUS_ROOT);
        rdfExternalResourceClass= createRDFSNamedClass(RDFNames.Cls.EXTERNAL_RESOURCE);
        rdfExternalClassClass= createRDFSNamedClass(RDFNames.Cls.EXTERNAL_CLASS);
        rdfExternalPropertyClass= createRDFSNamedClass(RDFNames.Cls.EXTERNAL_PROPERTY);
        owlOntologyPointerClass = createRDFSNamedClass(OWLNames.Cls.OWL_ONTOLOGY_POINTER_CLASS);
        
        directedBinaryRelation = createOWLNamedClass(ProtegeNames.Cls.DIRECTED_BINARY_RELATION);
        palConstraintCls = createOWLNamedClass(ProtegeNames.Cls.PAL_CONSTRAINT);
    }
	
    private void createOWLSlots() {
        owlAllValuesFromProperty = createRDFProperty(OWLNames.Slot.ALL_VALUES_FROM);
        owlBackwardCompatibleWithProperty = createRDFProperty(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        owlCardinalityProperty = createRDFProperty(OWLNames.Slot.CARDINALITY);
        owlComplementOfProperty = createRDFProperty(OWLNames.Slot.COMPLEMENT_OF);
        owlDifferentFromProperty = createRDFProperty(OWLNames.Slot.DIFFERENT_FROM);
        owlDisjointWithProperty = createRDFProperty(OWLNames.Slot.DISJOINT_WITH);
        owlDistinctMembersProperty = createRDFProperty(OWLNames.Slot.DISTINCT_MEMBERS);
        owlEquivalentClassProperty = createRDFProperty(OWLNames.Slot.EQUIVALENT_CLASS);
        owlEquivalentPropertyProperty = createRDFProperty(OWLNames.Slot.EQUIVALENT_PROPERTY);
        owlHasValueProperty = createRDFProperty(OWLNames.Slot.HAS_VALUE);
        owlImportsProperty = createRDFProperty(OWLNames.Slot.IMPORTS);
        owlIncompatibleWithProperty = createRDFProperty(OWLNames.Slot.INCOMPATIBLE_WITH);
        owlIntersectionOfProperty = createRDFProperty(OWLNames.Slot.INTERSECTION_OF);
        owlInverseOfProperty = createRDFProperty(OWLNames.Slot.INVERSE_OF);
        owlMaxCardinalityProperty = createRDFProperty(OWLNames.Slot.MAX_CARDINALITY);
        owlMinCardinalityProperty = createRDFProperty(OWLNames.Slot.MIN_CARDINALITY);
        owlOneOfProperty = createRDFProperty(OWLNames.Slot.ONE_OF);
        owlOnPropertyProperty = createRDFProperty(OWLNames.Slot.ON_PROPERTY);
        owlPriorVersionProperty = createRDFProperty(OWLNames.Slot.PRIOR_VERSION);
        owlSameAsProperty = createRDFProperty(OWLNames.Slot.SAME_AS);
        owlSomeValuesFromProperty = createRDFProperty(OWLNames.Slot.SOME_VALUES_FROM);
        owlUnionOfProperty = createRDFProperty(OWLNames.Slot.UNION_OF);
        owlValuesFromProperty = createRDFProperty(OWLNames.Slot.VALUES_FROM);
        owlVersionInfoProperty = createOWLDatatypeProperty(OWLNames.Slot.VERSION_INFO);
        protegeClassificationStatusProperty = createRDFProperty(ProtegeNames.Slot.CLASSIFICATION_STATUS);
        protegeInferredSubclassesProperty = createRDFProperty(ProtegeNames.Slot.INFERRED_SUBCLASSES);
        protegeInferredSuperclassesProperty = createRDFProperty(ProtegeNames.Slot.INFERRED_SUPERCLASSES);
        protegeInferredTypeProperty = createRDFProperty(ProtegeNames.Slot.INFERRED_TYPE);
        rdfFirstProperty = createRDFProperty(RDFNames.Slot.FIRST);
        rdfObjectProperty = createRDFProperty(RDFNames.Slot.OBJECT);
        rdfPredicateProperty = createRDFProperty(RDFNames.Slot.PREDICATE);
        rdfRestProperty = createRDFProperty(RDFNames.Slot.REST);
        rdfSubjectProperty = createRDFProperty(RDFNames.Slot.SUBJECT);
        rdfTypeProperty = createRDFProperty(RDFNames.Slot.TYPE);
        rdfValueProperty = createRDFProperty(RDFNames.Slot.VALUE);
        rdfsCommentProperty = createOWLDatatypeProperty(RDFSNames.Slot.COMMENT);
        rdfsDomainProperty = createRDFProperty(RDFSNames.Slot.DOMAIN);
        rdfsIsDefinedByProperty = createRDFProperty(RDFSNames.Slot.IS_DEFINED_BY);
        rdfsLabelProperty = createRDFProperty(RDFSNames.Slot.LABEL);
        rdfsMemberProperty = createRDFProperty(RDFSNames.Slot.MEMBER);
        rdfsRangeProperty = createRDFProperty(RDFSNames.Slot.RANGE);
        rdfsSeeAlsoProperty = createRDFProperty(RDFSNames.Slot.SEE_ALSO);
        rdfsSubClassOfProperty = createRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
        rdfsSubPropertyOf  = createRDFProperty(RDFSNames.Slot.SUB_PROPERTY_OF);    
        owlOntologyPrefixesProperty = createRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
        owlResourceURIProperty= createRDFProperty(OWLNames.Slot.RESOURCE_URI);
        owlOntologyPointerProperty = createRDFProperty(OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY);
        
        fromSlot = createOWLObjectProperty(ProtegeNames.Slot.FROM);
        toSlot = createOWLObjectProperty(ProtegeNames.Slot.TO); 
        slotConstraints = createOWLObjectProperty(ProtegeNames.Slot.CONSTRAINTS);
        palStatementSlot = createOWLDatatypeProperty(ProtegeNames.Slot.PAL_STATEMENT);
        palDescriptionSlot = createOWLDatatypeProperty(ProtegeNames.Slot.PAL_DESCRIPTION);
        palNameSlot = createOWLDatatypeProperty(ProtegeNames.Slot.PAL_NAME);
        palRangeSlot = createOWLDatatypeProperty(ProtegeNames.Slot.PAL_RANGE);
    }
    
    private void createOWLInstances() {
        FrameID nilFrameId = new FrameID(RDFNames.Instance.NIL);
        rdfNil = new DefaultRDFList(owlModel, nilFrameId);
        addFrame(nilFrameId, rdfNil);
        createRDFSDatatypes();
    }
    
    
    private void createRDFSDatatypes() {
        createRDFSDatatype(RDFSNames.LITERAL);

        createRDFSDatatype(RDFNames.XML_LITERAL);
        TypeMapper typeMapper = TypeMapper.getInstance();
        Iterator<RDFDatatype> it = typeMapper.listTypes();
        while (it.hasNext()) {
            com.hp.hpl.jena.datatypes.RDFDatatype type = (com.hp.hpl.jena.datatypes.RDFDatatype) it.next();
            String uri = type.getURI();
            if (uri.startsWith(XSDDatatype.XSD)) {
               String name = uri.toString();
               createRDFSDatatype(name);
            }
        }
        fillDatatypeSet(XMLSchemaDatatypes.floatTypes, floatDatatypes);
        fillDatatypeSet(XMLSchemaDatatypes.integerTypes, integerDatatypes);
    }


    private void fillDatatypeSet(XSDDatatype[] types, Set<RDFSDatatype> set) {
        for (int i = 0; i < types.length; i++) {
            XSDDatatype datatype = types[i];
            String name = datatype.getURI();
            set.add((RDFSDatatype) getFrame(new FrameID(name)));
        }
    }
    
    /*
     * replaceProtegeFrames and removeUnusedProtegeFrames are slightly
     * different.  The first is used in the case that the protege core
     * name is being kept but the java type for that object has
     * changed.  The second one is used in conjunction with the
     * modified Systems calls section just below it in the case that
     * the protege core frame is being replaced with a frame with a
     * different name and a different java type.
     */
  
    
    
	
    /*
     * Each call in here corresponds to a changed modified System getter in the section
     * just below.
     */
	private void removeUnusedProtegeFrames() {
	    removeFrame(super.getRootCls().getFrameID());
	    removeFrame(super.getDirectSuperslotsSlot().getFrameID());
	    removeFrame(super.getInverseSlotSlot().getFrameID());
	}

	
	
    /* **********************************************************************
     * SystemFrames calls that have been change to return a new frame with a different name.
     */
    @Override
	public OWLNamedClass getRootCls() {
        return owlThingClass;
    }

    @Override
	public RDFProperty getDirectSuperslotsSlot() {
        return rdfsSubPropertyOf;
    }
	
    @Override
	public RDFProperty getInverseSlotSlot() {
        return owlInverseOfProperty;
    }
    
    @Override
    public OWLNamedClass getDirectedBinaryRelationCls() {
        return directedBinaryRelation;
    }
    
    @Override
    public OWLNamedClass getPalConstraintCls() {
        return palConstraintCls;
    }
    
    @Override
    public OWLObjectProperty getFromSlot() {
        return fromSlot;
    }

    @Override
    public OWLObjectProperty getToSlot() {
        return toSlot;
    }
    
    @Override
    public OWLObjectProperty getConstraintsSlot() {
        return slotConstraints;
    }
    
    @Override
    public OWLObjectProperty getSlotConstraintsSlot() {
        return slotConstraints;
    }
    
    @Override
    public OWLDatatypeProperty getPalStatementSlot() {
        return palStatementSlot;
    }
    
    @Override
    public OWLDatatypeProperty getPalDescriptionSlot() {
        return palDescriptionSlot;
    }
    
    @Override
    public OWLDatatypeProperty getPalNameSlot() {
        return palNameSlot;
    }
    
    @Override
    public OWLDatatypeProperty getPalRangeSlot() {
        return palRangeSlot;
    }
    
    /* **************************************************************************
     * Tell the Frame Store
     */
    
    @Override
    public void addSystemFrames(FrameStore fs) {
        long start = System.currentTimeMillis();
        FrameFactory oldFrameFactory = ((KnowledgeBase) owlModel).getFrameFactory();
        try {
            ((KnowledgeBase) owlModel).setFrameFactory(new NonSwizzlingFactory());
            super.addSystemFrames(fs);
            OWLSystemFramesAssertions asserter = new OWLSystemFramesAssertions(fs);
            asserter.addClassHierarchy();
            asserter.addInstances();
            asserter.addClassAssertions();
            asserter.addSlotAssertions();
        }
        finally {
            ((KnowledgeBase) owlModel).setFrameFactory(oldFrameFactory);
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("Loading OWL System Frames took " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    
    protected class OWLSystemFramesAssertions extends SystemFramesAsserter {
    	private RDFSDatatype xsdInt;
    	private RDFSDatatype xsdNonNegativeInteger;
    	private RDFSDatatype xsdString;
    	
    	
    	public OWLSystemFramesAssertions(FrameStore fs) {
    	    super(fs);
            xsdInt = (RDFSDatatype) getFrame(new FrameID(XSDNames.INT));
            xsdNonNegativeInteger = (RDFSDatatype) getFrame(new FrameID(XSDNames.NON_NEGATIVE_INTEGER));
            xsdString = (RDFSDatatype) getFrame(new FrameID(XSDNames.STRING));
    	}
    	

        
        /* **************************************************
         * OWL Assertions
         */

        /*
         * Proper indentation is important in this function (and the indents must reflect the code).
         * Try to avoid using tabs so it looks the same for everybody.  Hopefully compilers don't have
         * trouble with very long statements.
         */
        public void addClassHierarchy() {
            fs.addDirectSuperclass(owlClassMetaCls, getStandardClsMetaCls());
            assertTypeAndSubclasses(owlClassMetaCls, rdfsNamedClassClass, new Cls[] {
                    assertTypeAndSubclasses(rdfsNamedClassClass, owlNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(owlNamedClassClass,      owlNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(owlDeprecatedClassClass, rdfsNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(rdfExternalClassClass, rdfsNamedClassClass, new Cls[] { })
                        }),
                    assertTypeAndSubclasses(anonymousClassMetaCls, rdfsNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(owlEnumeratedClassClass, rdfsNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(owlRestrictionClass,     rdfsNamedClassClass, new Cls[] {
                                    assertTypeAndSubclasses(owlAllValuesFromClass,  rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlHasValueClass,       rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlMaxCardinalityClass, rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlMinCardinalityClass, rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlCardinalityClass,    rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlSomeValuesFromClass, rdfsNamedClassClass, new Cls[] {})
                                }),
                            assertTypeAndSubclasses(owlLogicalClassClass,  rdfsNamedClassClass, new Cls[] { 
                                    assertTypeAndSubclasses(owlComplementClassClass,   rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlIntersectionClassClass, rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlUnionClassClass,        rdfsNamedClassClass, new Cls[] {})
                                })
                        })
                });
            fs.addDirectSuperclass(rdfPropertyClass, getStandardSlotMetaCls());
            assertTypeAndSubclasses(rdfPropertyClass, owlNamedClassClass, new Cls[] {
                    assertTypeAndSubclasses(owlDatatypePropertyClass,   owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlObjectPropertyClass,     owlNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(owlInverseFunctionalPropertyClass, owlNamedClassClass, new Cls[] {}),
                            assertTypeAndSubclasses(owlSymmetricPropertyClass,         owlNamedClassClass, new Cls[] {}),
                            assertTypeAndSubclasses(owlTransitivePropertyClass,        owlNamedClassClass, new Cls[] {})
                        }),
                    assertTypeAndSubclasses(owlAnnotationPropertyClass, owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlFunctionalPropertyClass, owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlDeprecatedPropertyClass, rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfExternalPropertyClass, rdfsNamedClassClass, new Cls[] {})
                });
            assertTypeAndSubclasses(owlThingClass, owlNamedClassClass, new Cls[] {
                    rdfsNamedClassClass,
                    rdfPropertyClass,
                    directedBinaryRelation,
                    palConstraintCls,
                    assertTypeAndSubclasses(rdfsDatatypeClass,    rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlOntologyClass,     owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlNothingClass,      owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfListClass,         rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlAllDifferentClass, rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfsContainerClass,   rdfsNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(rdfAltClass, rdfsNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(rdfBagClass, rdfsNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(rdfSeqClass, rdfsNamedClassClass, new Cls[] { })
                        }),
                    assertTypeAndSubclasses(rdfStatementClass,        rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlDataRangeClass,        rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(anonymousRootCls,         rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfExternalResourceClass, rdfsNamedClassClass, new Cls[] {}),                     
                    assertTypeAndSubclasses(owlOntologyPointerClass,  rdfsNamedClassClass, new Cls[] {}),
                    getDirectedBinaryRelationCls()
                });
        }
     
        private void addClassAssertions() {
            fs.setDirectOwnSlotValues(owlClassMetaCls, getRoleSlot(), 
                                      Collections.singleton(RoleConstraint.CONCRETE));
                   
            fs.setDirectOwnSlotValues(owlClassMetaCls, getRoleSlot(), 
                                      Collections.singleton(RoleConstraint.ABSTRACT));
            
            fs.setDirectTemplateFacetValues(owlDatatypePropertyClass, owlEquivalentPropertyProperty, 
                                            getValueTypeFacet(), Collections.singleton(owlDatatypePropertyClass));
            
            fs.setDirectTemplateFacetValues(owlObjectPropertyClass, owlEquivalentPropertyProperty, getValueTypeFacet(),
                                            ValueTypeConstraint.getValues(ValueType.INSTANCE, Collections.singleton(owlObjectPropertyClass)));
            

            assertTypeAndName(getDirectedBinaryRelationCls(), owlNamedClassClass);
            
            assertTypeAndName(getPalConstraintCls(), owlNamedClassClass);
        }

        private void addSlotAssertions() {
            assertTypeAndName(owlAllValuesFromProperty, rdfPropertyClass);
            assertDomain(owlAllValuesFromProperty, owlAllValuesFromClass);
            assertRange(owlAllValuesFromProperty, rdfsNamedClassClass);
            
            assertTypeAndName(owlBackwardCompatibleWithProperty, annotationObjectPropertyTypes);
            assertDomain(owlBackwardCompatibleWithProperty, owlOntologyClass);
            assertRange(owlBackwardCompatibleWithProperty, owlOntologyClass);
            assertValueType(owlBackwardCompatibleWithProperty, ValueType.INSTANCE);
            
            assertTypeAndName(owlCardinalityProperty, rdfPropertyClass);
            assertFunctional(owlCardinalityProperty);
            assertDomain(owlCardinalityProperty, owlCardinalityClass);
            assertValueType(owlCardinalityProperty, ValueType.INTEGER);
            fs.setDirectOwnSlotValues(owlCardinalityProperty, rdfsRangeProperty, Collections.singleton(xsdNonNegativeInteger));
            
            assertTypeAndName(owlComplementOfProperty, rdfPropertyClass);
            assertDomain(owlComplementOfProperty, owlClassMetaCls);
            assertRange(owlComplementOfProperty, owlClassMetaCls);
            
            assertTypeAndName(owlDifferentFromProperty, rdfPropertyClass);
            assertDomain(owlDifferentFromProperty);
            assertRange(owlDifferentFromProperty, owlThingClass);

            assertTypeAndName(owlDisjointWithProperty, rdfPropertyClass);
            assertDomain(owlDisjointWithProperty, owlClassMetaCls);
            assertRange(owlDisjointWithProperty, owlClassMetaCls);
            
            assertTypeAndName(owlDistinctMembersProperty, rdfPropertyClass);
            assertDomain(owlDistinctMembersProperty, owlAllDifferentClass);
            assertRange(owlDistinctMembersProperty, rdfListClass);
            
            assertTypeAndName(owlEquivalentClassProperty, rdfPropertyClass);
            assertDomain(owlEquivalentClassProperty, owlClassMetaCls);
            assertRange(owlDistinctMembersProperty, owlClassMetaCls);
            
            //missing owl:EquivalentProperty subproperty of rdfs:subPropertyOf
            assertTypeAndName(owlEquivalentPropertyProperty, rdfPropertyClass);
            assertDomain(owlEquivalentPropertyProperty, rdfPropertyClass);
            assertRange(owlEquivalentPropertyProperty, rdfPropertyClass);
            
            assertTypeAndName(owlHasValueProperty, rdfPropertyClass);
            assertDomain(owlHasValueProperty, owlHasValueClass);
            assertFunctional(owlHasValueProperty);
            
            //Check if the range specification causes problems.
            //Range assertion is according to the OWL spec. 
            assertTypeAndName(owlImportsProperty, rdfPropertyClass);
            assertDomain(owlImportsProperty, owlOntologyClass);
            assertRange(owlImportsProperty, owlOntologyClass);
            
            assertTypeAndName(owlIncompatibleWithProperty, annotationObjectPropertyTypes);
            assertDomain(owlIncompatibleWithProperty, owlOntologyClass);
            assertRange(owlIncompatibleWithProperty, owlOntologyClass);
            
            assertTypeAndName(owlIntersectionOfProperty, rdfPropertyClass);
            assertDomain(owlIntersectionOfProperty, owlClassMetaCls);
            assertRange(owlIntersectionOfProperty, rdfListClass);
            
            assertTypeAndName(owlInverseOfProperty, rdfPropertyClass);
            assertDomain(owlInverseOfProperty, owlObjectPropertyClass);
            assertRange(owlInverseOfProperty, owlObjectPropertyClass);
            
            assertTypeAndName(owlMaxCardinalityProperty, rdfPropertyClass);
            assertDomain(owlMaxCardinalityProperty, owlMaxCardinalityClass);
            assertValueType(owlMaxCardinalityProperty, ValueType.INTEGER);
            assertFunctional(owlMaxCardinalityProperty);
            fs.setDirectOwnSlotValues(owlMaxCardinalityProperty, rdfsRangeProperty, Collections.singleton(xsdNonNegativeInteger));
            
            assertTypeAndName(owlMinCardinalityProperty, rdfPropertyClass);
            assertDomain(owlMinCardinalityProperty, owlMinCardinalityClass);
            assertValueType(owlMinCardinalityProperty, ValueType.INTEGER);
            assertFunctional(owlMinCardinalityProperty);
            fs.setDirectOwnSlotValues(owlMinCardinalityProperty, rdfsRangeProperty, Collections.singleton(xsdNonNegativeInteger));

            //domain not according to OWL spec: Should be rdfs:Class
            assertTypeAndName(owlOneOfProperty, rdfPropertyClass);
            assertDomains(owlOneOfProperty, new Cls[] { owlEnumeratedClassClass, owlDataRangeClass });
            assertRange(owlOneOfProperty, rdfListClass);
            
            assertTypeAndName(owlOnPropertyProperty, rdfPropertyClass);
            assertDomain(owlOnPropertyProperty, owlRestrictionClass);
            assertRange(owlOnPropertyProperty, rdfPropertyClass);
            
            assertTypeAndName(owlPriorVersionProperty, annotationObjectPropertyTypes);
            assertDomain(owlPriorVersionProperty, owlOntologyClass);
            assertRange(owlPriorVersionProperty, owlOntologyClass);
            
            assertTypeAndName(owlSameAsProperty, rdfPropertyClass);
            assertDomain(owlSameAsProperty);
            assertRange(owlSameAsProperty, owlThingClass);
            
            //range not according to OWL spec: Should be rdfs:Class
            assertTypeAndName(owlSomeValuesFromProperty, rdfPropertyClass);  
            assertDomain(owlSomeValuesFromProperty, owlSomeValuesFromClass);
            assertRange(owlSomeValuesFromProperty, owlClassMetaCls);
            
            assertTypeAndName(owlUnionOfProperty, rdfPropertyClass);
            assertDomain(owlUnionOfProperty, owlClassMetaCls);
            assertRange(owlUnionOfProperty, rdfListClass);
            
            //not in OWL 1.0, but in OWL 1.1 to support qualified cardinality restrictions
            assertTypeAndName(owlValuesFromProperty, rdfPropertyClass);
            assertDomains(owlValuesFromProperty, new Cls[] {owlMaxCardinalityClass, owlMinCardinalityClass, owlCardinalityClass });
            assertRange(owlValuesFromProperty, owlClassMetaCls);
            assertFunctional(owlValuesFromProperty);
            
            //no String type assertion in OWL spec
            //this is the correct one
            //assertTypeAndName(owlVersionInfoProperty, annotationObjectPropertyTypes);
            /* 
             * Although it should be just an annotation property,
             * we make it an annotation datatype property, so that 
             * we don't change the OWLModel interface..
             */ 
            assertTypeAndName(owlVersionInfoProperty, annotationDatatypePropertyTypes);            
            assertDomain(owlVersionInfoProperty);
            assertValueType(owlVersionInfoProperty, ValueType.STRING);
            
            assertTypeAndName(protegeClassificationStatusProperty, rdfPropertyClass);
            assertDomain(protegeClassificationStatusProperty,rdfsNamedClassClass);            
            assertFunctional(protegeClassificationStatusProperty);
            assertValueType(owlVersionInfoProperty, ValueType.BOOLEAN);
            
            assertTypeAndName(protegeInferredSubclassesProperty, rdfPropertyClass);      
            assertDomain(protegeInferredSubclassesProperty, rdfsNamedClassClass);
            assertRange(protegeInferredSubclassesProperty, getStandardClsMetaCls());
            
            assertTypeAndName(protegeInferredSuperclassesProperty, rdfPropertyClass);      
            assertDomain(protegeInferredSuperclassesProperty, rdfsNamedClassClass);
            assertRange(protegeInferredSuperclassesProperty, getStandardClsMetaCls());
            
            assertTypeAndName(protegeInferredTypeProperty, rdfPropertyClass); 
            assertDomain(protegeInferredTypeProperty, rdfsNamedClassClass);
            assertRange(protegeInferredTypeProperty, getStandardClsMetaCls());
            
            //range should be rdf:Resource
            assertTypeAndName(rdfFirstProperty, rdfPropertyClass); 
            assertDomain(rdfFirstProperty, rdfListClass);
            assertFunctional(rdfFirstProperty);
            
            //range should be rdf:Resource
            assertTypeAndName(rdfObjectProperty, rdfPropertyClass);
            assertDomain(rdfObjectProperty, rdfStatementClass);
            assertValueType(rdfObjectProperty, ValueType.INSTANCE);
  
            //range should be rdf:Resource
            assertTypeAndName(rdfPredicateProperty, rdfPropertyClass);    
            assertDomain(rdfPredicateProperty, rdfStatementClass);
            assertValueType(rdfPredicateProperty, ValueType.INSTANCE);
                        
            assertTypeAndName(rdfRestProperty, rdfPropertyClass);  
            assertFunctional(rdfRestProperty);
            assertDomain(rdfRestProperty, rdfListClass);
            assertRange(rdfRestProperty, rdfListClass);
            
            //range should be rdf:Resource
            assertTypeAndName(rdfSubjectProperty, rdfPropertyClass);  
            assertDomain(rdfSubjectProperty, rdfStatementClass);
            assertValueType(rdfSubjectProperty, ValueType.INSTANCE);
            
            //range should be rdf:Resource
            assertTypeAndName(rdfTypeProperty, rdfPropertyClass);
            assertDomain(rdfTypeProperty);
            assertValueType(rdfTypeProperty, ValueType.CLS);
            
            //range should be rdf:Resource
            assertTypeAndName(rdfValueProperty, rdfPropertyClass);
            assertDomain(rdfValueProperty);
            
            //range should be rdf:Resource
            assertTypeAndName(rdfsCommentProperty, annotationDatatypePropertyTypes);
            assertDomain(rdfsCommentProperty);
            assertValueType(rdfsCommentProperty, ValueType.STRING);
            fs.setDirectOwnSlotValues(rdfsCommentProperty, rdfsRangeProperty, Collections.singleton(xsdString));
            
            //domain should be rdfs:Class
            assertTypeAndName(rdfsDomainProperty, rdfPropertyClass);
            assertDomain(rdfsDomainProperty, rdfPropertyClass);
            assertRange(rdfsDomainProperty, owlClassMetaCls);
            assertValueType(rdfsDomainProperty, ValueType.INSTANCE);
           
            //range should be rdf:Resource
            assertTypeAndName(rdfsIsDefinedByProperty, annotationObjectPropertyTypes);
            assertDomain(rdfsIsDefinedByProperty);
            
            //domain should be rdf:Resource, range should be: rdf:Literal
            assertTypeAndName(rdfsLabelProperty, annotationObjectPropertyTypes);
            assertDomain(rdfsLabelProperty);
            assertValueType(rdfsLabelProperty, ValueType.STRING);
            fs.setDirectOwnSlotValues(rdfsLabelProperty, rdfsRangeProperty, Collections.singleton(xsdString));
            
            //range should be rdf:Resource
            assertTypeAndName(rdfsMemberProperty, rdfPropertyClass);
            assertDomain(rdfsMemberProperty);
            assertValueType(rdfsMemberProperty, ValueType.INSTANCE);

            assertTypeAndName(rdfsRangeProperty, rdfPropertyClass);  
            assertDomain(rdfsRangeProperty, rdfPropertyClass);
            assertRange(rdfsRangeProperty, rdfsNamedClassClass);

            //range should be rdf:Resource
            assertTypeAndName(rdfsSeeAlsoProperty, annotationObjectPropertyTypes);
            assertDomain(rdfsSeeAlsoProperty);

            assertTypeAndName(rdfsSubClassOfProperty, rdfPropertyClass); 
            assertDomain(rdfsSubClassOfProperty, rdfsNamedClassClass);
            assertRange(rdfsSubClassOfProperty, rdfsNamedClassClass);
            
            assertTypeAndName(rdfsSubPropertyOf, rdfPropertyClass);
            assertDomain(rdfsSubPropertyOf, rdfPropertyClass);
            assertRange(rdfsSubPropertyOf, rdfPropertyClass);
            
            assertTypeAndName(owlOntologyPrefixesProperty, rdfPropertyClass);
            assertDomain(owlOntologyPrefixesProperty, owlOntologyClass);
            assertValueType(owlOntologyPrefixesProperty, ValueType.STRING);
            assertFunctional(owlOntologyPrefixesProperty);

            assertTypeAndName(owlOntologyPointerProperty, rdfPropertyClass);
            assertDomain(owlOntologyPointerProperty, owlOntologyPointerClass);
            assertFunctional(owlOntologyPointerProperty);
            assertRange(owlOntologyPointerProperty, owlOntologyClass);

            assertTypeAndName(owlResourceURIProperty, rdfPropertyClass);
            assertDomain(owlResourceURIProperty, rdfExternalResourceClass);
            
            assertTypeAndName(getFromSlot(), owlObjectPropertyClass);
            fs.setDirectOwnSlotValues(getFromSlot(), getRdfsDomainProperty(), Collections.singleton(getDirectedBinaryRelationCls()));
            
            assertTypeAndName(getToSlot(), owlObjectPropertyClass);
            fs.setDirectOwnSlotValues(getToSlot(), getRdfsDomainProperty(), Collections.singleton(getDirectedBinaryRelationCls()));
            
            assertTypeAndName(getSlotConstraintsSlot(), owlObjectPropertyClass);
            assertDomain(getSlotConstraintsSlot(), owlThingClass);
            
            assertTypeAndName(getPalStatementSlot(), owlDatatypePropertyClass);
            fs.setDirectOwnSlotValues(getPalStatementSlot(), getRdfsDomainProperty(), Collections.singleton(getPalConstraintCls()));
            assertValueType(getPalStatementSlot(), ValueType.STRING);
            
            assertTypeAndName(getPalDescriptionSlot(), owlDatatypePropertyClass);
            fs.setDirectOwnSlotValues(getPalDescriptionSlot(), getRdfsDomainProperty(), Collections.singleton(getPalConstraintCls()));
            assertValueType(getPalDescriptionSlot(), ValueType.STRING);
            
            assertTypeAndName(getPalNameSlot(), owlDatatypePropertyClass);
            fs.setDirectOwnSlotValues(getPalNameSlot(), getRdfsDomainProperty(), Collections.singleton(getPalConstraintCls()));
            assertValueType(getPalNameSlot(), ValueType.STRING);
            
            assertTypeAndName(getPalRangeSlot(), owlObjectPropertyClass);
            fs.setDirectOwnSlotValues(getPalRangeSlot(), getRdfsDomainProperty(), Collections.singleton(getPalConstraintCls()));
            assertValueType(getPalRangeSlot(), ValueType.STRING);
        }
        

        
        private void addInstances() {
            assertTypeAndName(rdfNil, rdfListClass);
            for (Instance datatype : rdfDatatypes) {
                assertTypeAndName(datatype, rdfsDatatypeClass);
            } 
        }
    }
    
    public class SystemFramesAsserter {
        protected FrameStore fs;
        protected Collection<Cls> annotationObjectPropertyTypes = new HashSet<Cls>();
        protected Collection<Cls> annotationDatatypePropertyTypes = new HashSet<Cls>();
        protected Collection<Cls> functionalDatatypePropertyTypes = new HashSet<Cls>();
        
        
        
        public SystemFramesAsserter(FrameStore fs) {
            this.fs = fs;
            annotationObjectPropertyTypes.add(owlAnnotationPropertyClass);
            annotationObjectPropertyTypes.add(owlObjectPropertyClass);
            
            annotationDatatypePropertyTypes.add(owlAnnotationPropertyClass);
            annotationDatatypePropertyTypes.add(owlDatatypePropertyClass);
            
            functionalDatatypePropertyTypes.add(owlDatatypePropertyClass);
            functionalDatatypePropertyTypes.add(owlFunctionalPropertyClass);
        }
        
        /* ***********************************************************
         * Utilities
         */
        
        protected Cls assertTypeAndSubclasses(Cls cls, Cls type, Cls[] subclasses) {
            assertTypeAndName(cls, type);
            for (Cls subclass : subclasses) {
                fs.addDirectSuperclass(subclass, cls);
            }
            return cls;
        }
        
        protected void assertFunctional(Slot slot) {
            fs.setDirectOwnSlotValues(slot, getMaximumCardinalitySlot(),
                                      MaximumCardinalityConstraint.getValues(false));
        }
        
        protected void assertTypeAndName(Instance frame, Cls type) {
            assertTypeAndName(frame, Collections.singleton(type));
        }

        protected void assertValueType(Slot slot, ValueType vt) {
            fs.setDirectOwnSlotValues(slot, getValueTypeSlot(),
                                      ValueTypeConstraint.getValues(vt));
        }
        
        protected void assertTypeAndName(Frame frame, Collection<Cls> types) {
            OWLSystemFrames.this.assertTypeAndName(fs, frame, types);
            fs.setDirectOwnSlotValues(frame, getRdfTypeProperty(), types);
        }
        
        protected void assertDomain(Slot slot) {
            assertDomains(slot, new Cls[] { });
        }
        
        protected void assertDomain(Slot slot, Cls domain) {
            assertDomains(slot, new Cls[] { domain });
        }
        
        protected void assertDomains(Slot slot, Cls[] domains) {
            if (domains.length == 0) {
                fs.addDirectTemplateSlot(getOwlThingClass(), slot);
                return;
            }
            for (Cls domain : domains) {
                fs.addDirectTemplateSlot(domain, slot);
            }
            if (domains.length == 1) {
                fs.setDirectOwnSlotValues(slot, getRdfsDomainProperty(), Collections.singleton(domains[0]));
            }
        }
        
        protected void assertRange(Slot slot, Cls cls) {
            fs.setDirectOwnSlotValues(slot, getRdfsRangeProperty(), Collections.singleton(cls));
            fs.setDirectOwnSlotValues(slot, getValueTypeSlot(),
                                      ValueTypeConstraint.getValues(ValueType.INSTANCE, 
                                                                    Collections.singleton(cls)));
        }

        
    }
    
    private class NonSwizzlingFactory implements FrameFactory {

        public void addJavaPackage(String packageName) {
            ;
        }

        public Cls createCls(FrameID id, Collection directTypes) {
            return (Cls) getFrame(id);
        }

        public Facet createFacet(FrameID id, Collection directTypes) {
            return (Facet) getFrame(id);
        }

        public Frame createFrameFromClassId(int javaClassId, FrameID id) {
            return getFrame(id);
        }

        public SimpleInstance createSimpleInstance(FrameID id,
                                                   Collection directTypes) {
            return (SimpleInstance) getFrame(id);
        }

        public Slot createSlot(FrameID id, Collection directTypes) {
            return (Slot) getFrame(id);
        }

        public Collection getClsJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public Collection getFacetJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public int getJavaClassId(Frame value) {
            throw new UnsupportedOperationException();
        }

        public Collection getSimpleInstanceJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public Collection getSlotJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public boolean isCorrectJavaImplementationClass(FrameID id,
                                                        Collection directTypes,
                                                        Class clas) {
            return true;
        }

        public void removeJavaPackage(String packageName) {
            throw new UnsupportedOperationException();
        }

        public Frame rename(Frame original, String name) {
            throw new UnsupportedOperationException();
        }
        
    }
    
    /*
     * ----------------------------------------------------------------------
     * The getters - automatically generated by eclipse.
     */
    
    /*
     * Getters for the important datatypes.  We don't need all 43...
     */
    public RDFSDatatype getXsdBoolean() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.BOOLEAN));
    }

    public RDFSDatatype getXsdDouble() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DOUBLE));
    }

    public RDFSDatatype getXsdFloat() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.FLOAT));
    }

    public RDFSDatatype getXsdLong() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.LONG));
    }

    public RDFSDatatype getXsdInt() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.INT));
    }

    public RDFSDatatype getXsdShort() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.SHORT));
    }

    public RDFSDatatype getXsdByte() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.BYTE));
    }

    public RDFSDatatype getXsdString() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.STRING));
    }

    public RDFSDatatype getXsdBase64Binary() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.BASE_64_BINARY));
    }

    public RDFSDatatype getXsdDate() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DATE));
    }

    public RDFSDatatype getXsdTime() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.TIME));
    }

    public RDFSDatatype getXsdDateTime() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DATE_TIME));
    }

    public RDFSDatatype getXsdDuration() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DURATION));
    }

    public RDFSDatatype getXsdAnyURI() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.ANY_URI));
    }

    public RDFSDatatype getXsdDecimal() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DECIMAL));
    }

    public RDFSDatatype getXsdInteger() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.INTEGER));
    }
    
    public RDFSDatatype getXsdNonNegativeInteger() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.NON_NEGATIVE_INTEGER));
    }

    public RDFSDatatype getXmlLiteralType() {
        return (RDFSDatatype) getFrame(new FrameID(RDFNames.XML_LITERAL));
    }

    
    /*
     * Getters for all the important system frames.  IDE's can generate this for you.
     */

    /**
     * @return the owlThingClass
     */
    public OWLNamedClass getOwlThingClass() {
        return owlThingClass;
    }

    /**
     * @return the owlClassMetaCls
     */
    public RDFSNamedClass getOwlClassMetaCls() {
        return owlClassMetaCls;
    }

    /**
     * @return the rdfsNamedClassClass
     */
    public OWLNamedClass getRdfsNamedClassClass() {
        return rdfsNamedClassClass;
    }

    /**
     * @return the owlNamedClassClass
     */
    public OWLNamedClass getOwlNamedClassClass() {
        return owlNamedClassClass;
    }

    /**
     * @return the owlDeprecatedClassClass
     */
    public RDFSNamedClass getOwlDeprecatedClassClass() {
        return owlDeprecatedClassClass;
    }

    /**
     * @return the anonymousClassMetaCls
     */
    public OWLNamedClass getAnonymousClassMetaCls() {
        return anonymousClassMetaCls;
    }

    /**
     * @return the owlEnumeratedClassClass
     */
    public RDFSNamedClass getOwlEnumeratedClassClass() {
        return owlEnumeratedClassClass;
    }

    /**
     * @return the owlRestrictionClass
     */
    public RDFSNamedClass getOwlRestrictionClass() {
        return owlRestrictionClass;
    }

    /**
     * @return the owlAllValuesFromClass
     */
    public RDFSNamedClass getOwlAllValuesFromClass() {
        return owlAllValuesFromClass;
    }

    /**
     * @return the owlHasValueClass
     */
    public RDFSNamedClass getOwlHasValueClass() {
        return owlHasValueClass;
    }

    /**
     * @return the owlMaxCardinalityClass
     */
    public RDFSNamedClass getOwlMaxCardinalityClass() {
        return owlMaxCardinalityClass;
    }

    /**
     * @return the owlMinCardinalityClass
     */
    public RDFSNamedClass getOwlMinCardinalityClass() {
        return owlMinCardinalityClass;
    }

    /**
     * @return the owlCardinalityClass
     */
    public RDFSNamedClass getOwlCardinalityClass() {
        return owlCardinalityClass;
    }

    /**
     * @return the owlSomeValuesFromClass
     */
    public RDFSNamedClass getOwlSomeValuesFromClass() {
        return owlSomeValuesFromClass;
    }

    /**
     * @return the owlLogicalClassClass
     */
    public RDFSNamedClass getOwlLogicalClassClass() {
        return owlLogicalClassClass;
    }

    /**
     * @return the owlComplementClassClass
     */
    public RDFSNamedClass getOwlComplementClassClass() {
        return owlComplementClassClass;
    }

    /**
     * @return the owlIntersectionClassClass
     */
    public RDFSNamedClass getOwlIntersectionClassClass() {
        return owlIntersectionClassClass;
    }

    /**
     * @return the owlUnionClassClass
     */
    public RDFSNamedClass getOwlUnionClassClass() {
        return owlUnionClassClass;
    }

    /**
     * @return the rdfPropertyClass
     */
    public OWLNamedClass getRdfPropertyClass() {
        return rdfPropertyClass;
    }

    /**
     * @return the owlDatatypePropertyClass
     */
    public OWLNamedClass getOwlDatatypePropertyClass() {
        return owlDatatypePropertyClass;
    }

    /**
     * @return the owlObjectPropertyClass
     */
    public OWLNamedClass getOwlObjectPropertyClass() {
        return owlObjectPropertyClass;
    }

    /**
     * @return the owlInverseFunctionalPropertyClass
     */
    public OWLNamedClass getOwlInverseFunctionalPropertyClass() {
        return owlInverseFunctionalPropertyClass;
    }

    /**
     * @return the owlSymmetricPropertyClass
     */
    public OWLNamedClass getOwlSymmetricPropertyClass() {
        return owlSymmetricPropertyClass;
    }

    /**
     * @return the owlTransitivePropertyClass
     */
    public OWLNamedClass getOwlTransitivePropertyClass() {
        return owlTransitivePropertyClass;
    }

    /**
     * @return the owlAnnotationPropertyClass
     */
    public OWLNamedClass getOwlAnnotationPropertyClass() {
        return owlAnnotationPropertyClass;
    }

    /**
     * @return the owlFunctionalPropertyClass
     */
    public OWLNamedClass getOwlFunctionalPropertyClass() {
        return owlFunctionalPropertyClass;
    }

    /**
     * @return the owlDeprecatedPropertyClass
     */
    public RDFSNamedClass getOwlDeprecatedPropertyClass() {
        return owlDeprecatedPropertyClass;
    }

    /**
     * @return the rdfsDatatypeClass
     */
    public RDFSNamedClass getRdfsDatatypeClass() {
        return rdfsDatatypeClass;
    }

    /**
     * @return the owlOntologyClass
     */
    public OWLNamedClass getOwlOntologyClass() {
        return owlOntologyClass;
    }

    /**
     * @return the owlNothingClass
     */
    public OWLNamedClass getOwlNothingClass() {
        return owlNothingClass;
    }

    /**
     * @return the rdfListClass
     */
    public RDFSNamedClass getRdfListClass() {
        return rdfListClass;
    }

    /**
     * @return the owlAllDifferentClass
     */
    public RDFSNamedClass getOwlAllDifferentClass() {
        return owlAllDifferentClass;
    }
    
    /**
     * @return the rdfsLiteralClass
     * @deprecated This is based on a wrong premise.  This is not a class.  Using this can cause serious problems.
     */
    public RDFSNamedClass getRdfsLiteralClass() {
        return new DefaultRDFSNamedClass(owlModel, new FrameID(RDFSNames.LITERAL));
    }

    /**
     * @return the rdfsContainerClass
     */
    public RDFSNamedClass getRdfsContainerClass() {
        return rdfsContainerClass;
    }

    /**
     * @return the rdfAltClass
     */
    public RDFSNamedClass getRdfAltClass() {
        return rdfAltClass;
    }

    /**
     * @return the rdfBagClass
     */
    public RDFSNamedClass getRdfBagClass() {
        return rdfBagClass;
    }

    /**
     * @return the rdfSeqClass
     */
    public RDFSNamedClass getRdfSeqClass() {
        return rdfSeqClass;
    }

    /**
     * @return the rdfStatementClass
     */
    public RDFSNamedClass getRdfStatementClass() {
        return rdfStatementClass;
    }

    /**
     * @return the owlDataRangeClass
     */
    public RDFSNamedClass getOwlDataRangeClass() {
        return owlDataRangeClass;
    }

    /**
     * @return the anonymousRootCls
     */
    public RDFSNamedClass getAnonymousRootCls() {
        return anonymousRootCls;
    }

    /**
     * @return the rdfExternalResourceClass
     */
    public RDFSNamedClass getRdfExternalResourceClass() {
        return rdfExternalResourceClass;
    }
    
    /**
     * @return the rdfExternalClassClass
     */
    public RDFSNamedClass getRdfExternalClassClass() {
        return rdfExternalClassClass;
    }
    
    /**
     * @return the rdfExternalPropertyClass
     */
    public RDFSNamedClass getRdfExternalPropertyClass() {
        return rdfExternalPropertyClass;
    }
    
    /**
     * @return the topOWLOntologyClass
     */
    public RDFSNamedClass getOwlOntologyPointerClass() {
        return owlOntologyPointerClass;
    }

    /**
     * @return the owlAllValuesFromProperty
     */
    public RDFProperty getOwlAllValuesFromProperty() {
        return owlAllValuesFromProperty;
    }

    /**
     * @return the owlBackwardCompatibleWithProperty
     */
    public RDFProperty getOwlBackwardCompatibleWithProperty() {
        return owlBackwardCompatibleWithProperty;
    }

    /**
     * @return the owlCardinalityProperty
     */
    public RDFProperty getOwlCardinalityProperty() {
        return owlCardinalityProperty;
    }

    /**
     * @return the owlComplementOfProperty
     */
    public RDFProperty getOwlComplementOfProperty() {
        return owlComplementOfProperty;
    }

    /**
     * @return the owlDifferentFromProperty
     */
    public RDFProperty getOwlDifferentFromProperty() {
        return owlDifferentFromProperty;
    }

    /**
     * @return the owlDisjointWithProperty
     */
    public RDFProperty getOwlDisjointWithProperty() {
        return owlDisjointWithProperty;
    }

    /**
     * @return the owlDistinctMembersProperty
     */
    public RDFProperty getOwlDistinctMembersProperty() {
        return owlDistinctMembersProperty;
    }

    /**
     * @return the owlEquivalentClassProperty
     */
    public RDFProperty getOwlEquivalentClassProperty() {
        return owlEquivalentClassProperty;
    }

    /**
     * @return the owlEquivalentPropertyProperty
     */
    public RDFProperty getOwlEquivalentPropertyProperty() {
        return owlEquivalentPropertyProperty;
    }

    /**
     * @return the owlHasValueProperty
     */
    public RDFProperty getOwlHasValueProperty() {
        return owlHasValueProperty;
    }

    /**
     * @return the owlImportsProperty
     */
    public RDFProperty getOwlImportsProperty() {
        return owlImportsProperty;
    }

    /**
     * @return the owlIncompatibleWithProperty
     */
    public RDFProperty getOwlIncompatibleWithProperty() {
        return owlIncompatibleWithProperty;
    }

    /**
     * @return the owlIntersectionOfProperty
     */
    public RDFProperty getOwlIntersectionOfProperty() {
        return owlIntersectionOfProperty;
    }

    /**
     * @return the owlInverseOfProperty
     */
    public RDFProperty getOwlInverseOfProperty() {
        return owlInverseOfProperty;
    }

    /**
     * @return the owlMaxCardinalityProperty
     */
    public RDFProperty getOwlMaxCardinalityProperty() {
        return owlMaxCardinalityProperty;
    }

    /**
     * @return the owlMinCardinalityProperty
     */
    public RDFProperty getOwlMinCardinalityProperty() {
        return owlMinCardinalityProperty;
    }

    /**
     * @return the owlOneOfProperty
     */
    public RDFProperty getOwlOneOfProperty() {
        return owlOneOfProperty;
    }

    /**
     * @return the owlOnPropertyProperty
     */
    public RDFProperty getOwlOnPropertyProperty() {
        return owlOnPropertyProperty;
    }

    /**
     * @return the owlPriorVersionProperty
     */
    public RDFProperty getOwlPriorVersionProperty() {
        return owlPriorVersionProperty;
    }

    /**
     * @return the owlSameAsProperty
     */
    public RDFProperty getOwlSameAsProperty() {
        return owlSameAsProperty;
    }

    /**
     * @return the owlSomeValuesFromProperty
     */
    public RDFProperty getOwlSomeValuesFromProperty() {
        return owlSomeValuesFromProperty;
    }

    /**
     * @return the owlUnionOfProperty
     */
    public RDFProperty getOwlUnionOfProperty() {
        return owlUnionOfProperty;
    }

    /**
     * @return the owlValuesFromProperty
     */
    public RDFProperty getOwlValuesFromProperty() {
        return owlValuesFromProperty;
    }

    /**
     * @return the owlVersionInfoProperty
     */
    public RDFProperty getOwlVersionInfoProperty() {
        return owlVersionInfoProperty;
    }

    /**
     * @return the protegeClassificationStatusProperty
     */
    public RDFProperty getProtegeClassificationStatusProperty() {
        return protegeClassificationStatusProperty;
    }

    /**
     * @return the protegeInferredSubclassesProperty
     */
    public RDFProperty getProtegeInferredSubclassesProperty() {
        return protegeInferredSubclassesProperty;
    }

    /**
     * @return the protegeInferredSuperclassesProperty
     */
    public RDFProperty getProtegeInferredSuperclassesProperty() {
        return protegeInferredSuperclassesProperty;
    }

    /**
     * @return the protegeInferredTypeProperty
     */
    public RDFProperty getProtegeInferredTypeProperty() {
        return protegeInferredTypeProperty;
    }

    /**
     * @return the rdfFirstProperty
     */
    public RDFProperty getRdfFirstProperty() {
        return rdfFirstProperty;
    }

    /**
     * @return the rdfObjectProperty
     */
    public RDFProperty getRdfObjectProperty() {
        return rdfObjectProperty;
    }

    /**
     * @return the rdfPredicateProperty
     */
    public RDFProperty getRdfPredicateProperty() {
        return rdfPredicateProperty;
    }

    /**
     * @return the rdfRestProperty
     */
    public RDFProperty getRdfRestProperty() {
        return rdfRestProperty;
    }

    /**
     * @return the rdfSubjectProperty
     */
    public RDFProperty getRdfSubjectProperty() {
        return rdfSubjectProperty;
    }

    /**
     * @return the rdfTypeProperty
     */
    public RDFProperty getRdfTypeProperty() {
        return rdfTypeProperty;
    }

    /**
     * @return the rdfValueProperty
     */
    public RDFProperty getRdfValueProperty() {
        return rdfValueProperty;
    }

    /**
     * @return the rdfsCommentProperty
     */
    public OWLDatatypeProperty getRdfsCommentProperty() {
        return rdfsCommentProperty;
    }

    /**
     * @return the rdfsDomainProperty
     */
    public RDFProperty getRdfsDomainProperty() {
        return rdfsDomainProperty;
    }

    /**
     * @return the rdfsIsDefinedByProperty
     */
    public RDFProperty getRdfsIsDefinedByProperty() {
        return rdfsIsDefinedByProperty;
    }

    /**
     * @return the rdfsLabelProperty
     */
    public RDFProperty getRdfsLabelProperty() {
        return rdfsLabelProperty;
    }

    /**
     * @return the rdfsMemberProperty
     */
    public RDFProperty getRdfsMemberProperty() {
        return rdfsMemberProperty;
    }

    /**
     * @return the rdfsRangeProperty
     */
    public RDFProperty getRdfsRangeProperty() {
        return rdfsRangeProperty;
    }

    /**
     * @return the rdfsSeeAlsoProperty
     */
    public RDFProperty getRdfsSeeAlsoProperty() {
        return rdfsSeeAlsoProperty;
    }

    /**
     * @return the rdfsSubClassOfProperty
     */
    public RDFProperty getRdfsSubClassOfProperty() {
        return rdfsSubClassOfProperty;
    }

    /**
     * @return the rdfsSubPropertyOf
     */
    public RDFProperty getRdfsSubPropertyOf() {
        return rdfsSubPropertyOf;
    }

    /**
     * @return the owlOntologyPrefixesProperty
     */
    public RDFProperty getOwlOntologyPrefixesProperty() {
        return owlOntologyPrefixesProperty;
    }

    /**
     * @return the owlResourceURIProperty
     */
    public RDFProperty getOwlResourceURIProperty() {
        return owlResourceURIProperty;
    }

    /**
     * @return the topOWLOntologyURISlot
     */
    public RDFProperty getOwlOntologyPointerProperty() {
        return owlOntologyPointerProperty;
    }

    /**
     * @return the rdfNil
     */
    public RDFList getRdfNil() {
        return rdfNil;
    }

    /**
     * @return the rdfDatatypes
     */
    public Set<RDFSDatatype> getRdfDatatypes() {
        return rdfDatatypes;
    }

    /**
     * @return the floatDatatypes
     */
    public Set<RDFSDatatype> getFloatDatatypes() {
        return floatDatatypes;
    }

    /**
     * @return the integerDatatypes
     */
    public Set<RDFSDatatype> getIntegerDatatypes() {
        return integerDatatypes;
    }
}
