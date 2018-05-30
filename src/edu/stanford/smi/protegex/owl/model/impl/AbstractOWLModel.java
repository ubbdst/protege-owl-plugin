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

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.server.framestore.background.ServerCacheStateMachine;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.graph.JenaModelFactory;
import edu.stanford.smi.protegex.owl.jena.parser.GlobalParserCache;
import edu.stanford.smi.protegex.owl.jena.parser.OWLImportsCache;
import edu.stanford.smi.protegex.owl.jena.parser.UnresolvedImportHandler;
import edu.stanford.smi.protegex.owl.jena.writersettings.JenaWriterSettings;
import edu.stanford.smi.protegex.owl.jena.writersettings.WriterSettings;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplayFactory;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.event.*;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.framestore.FacetUpdateFrameStore;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStore;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStoreManager;
import edu.stanford.smi.protegex.owl.model.framestore.TypeUpdateFrameStore;
import edu.stanford.smi.protegex.owl.model.project.DefaultOWLProject;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.model.project.SettingsMap;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import edu.stanford.smi.protegex.owl.model.query.SPARQLQueryResults;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTuple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.TripleStoreModelImpl;
import edu.stanford.smi.protegex.owl.model.validator.DefaultPropertyValueValidator;
import edu.stanford.smi.protegex.owl.model.validator.PropertyValueValidator;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;
import edu.stanford.smi.protegex.owl.server.OwlStateMachine;
import edu.stanford.smi.protegex.owl.server.triplestore.ClientTripleStoreModel;
import edu.stanford.smi.protegex.owl.swrl.SWRLSystemFrames;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.testing.OWLTest;
import edu.stanford.smi.protegex.owl.testing.OWLTestLibrary;
import edu.stanford.smi.protegex.owl.ui.menu.preferences.RenderingPanel;
import edu.stanford.smi.protegex.owl.ui.repository.UnresolvedImportUIHandler;
import edu.stanford.smi.protegex.owl.ui.widget.OWLFormWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.OWLWidgetMapper;
import edu.stanford.smi.protegex.owl.util.OWLBrowserSlotPattern;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.ProtegeWriterSettings;
import junit.framework.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLModel extends DefaultKnowledgeBase
        implements OWLModel {

    private static transient Logger log = Log.getLogger(AbstractOWLModel.class);

    /**
     * If the value of this property key is true in protege.properties,
     * the default mechanism of the underlying frame model will be used
     * at class, property and individual creation time.
     * It false, the defaults mechanism will not be used, which might bring
     * performance improvements.
     * If false, the default mechanism will be disabled.
     * The default value of this property is false.
     */
    public static final String OWL_MODEL_INIT_DEFAULTS_AT_CREATION = "owlmodel.init.defaults";

    public static final String OWL_MODEL_EXPAND_SHORT_NAME_IN_METHODS = "owlmodel.expand.short.name.in.methods";

    public static final String OWL_MODEL_IMPORT_NAMESPACES = "owlmodel.import.namespaces";

    private static final String SEARCH_SYNONYMS_KEY = "OWL-SEARCH-SYNONYMS-SLOTS";

    private static final String SEARCH_SYNONYMS_SEPARATOR = ",";

    private static final String DEFAULT_ANNOT_PROP_IN_VIEW_KEY = "OWL-DEFAULT-ANNOT-PROP-IN-VIEW";

    public static final String DEFAULT_TODO_PREFIX = "TODO";

    public static final String[] DEFAULT_USED_LANGUAGES = {"no", "en", "de", "es", "fr", "it", "nl", "pt" };

    public static final String ANONYMOUS_BASE = "@";

    public static final String UNIQUE_SESSION_ID = UUID.randomUUID().toString().replace("-", "_");

    public static final String DEFAULT_ANNOTATION_PROPERTY_NAME = "annotationProperty";

    public static final String DEFAULT_CLASS_NAME = "Class";

    public static final String DEFAULT_DATATYPE_PROPERTY_NAME = "datatypeProperty";

    public static final String DEFAULT_INDIVIDUAL_NAME = "Individual";

    public static final String DEFAULT_OBJECT_PROPERTY_NAME = "objectProperty";

    public static final String DEFAULT_PROPERTY_NAME = "property";

    /**
     * The characters that are valid name parts (in addition to the Java identifier chars)
     */
    private final static String VALID_SYMBOLS = "-."; // "-.+/:";

    private final static String AUTO_REPAIR_ENABLED = "TestAutoRepairEnabled";

    private final static String TEST_LIST_NAME = "DisabledTest";

    private final static String TESTGROUP_LIST_NAME = "DisabledTestGroups";


    private boolean loadDefaults = false;

    private boolean expandShortNameInMethods = true;

    private boolean importNamespaces = true;


    private static UnresolvedImportHandler unresolvedImportHandler = new UnresolvedImportUIHandler();

    private com.hp.hpl.jena.rdf.model.Model jenaModel;


    private OWLClassDisplay owlClassRenderer = OWLClassDisplayFactory.getDefaultDisplay();

    private OWLProject owlProject;

    private PropertyValueValidator propertyValueValidator = new DefaultPropertyValueValidator();


    private Set<Cls> defaultAnonymousTypes = new HashSet<Cls>();

    private RDFSDatatypeFactory rdfsDatatypeFactory = new DefaultRDFSDatatypeFactory(this);

    private TaskManager taskManager;

    private GlobalParserCache globalParserCache;

    private RepositoryManager repositoryManager;
    
    private Collection<MessageError> parserErrors = new ArrayList<MessageError>();

    /**
     * The top OWL ontology is the top level ontology from the model.
     * E.g. in file mode, it is the ontology that is loaded from an URI
     * or an input stream and that may import other ontologies.
     * The top OWL ontology is stored in the top triple store.
     * The top OWL ontology is not dependent on the active ontology.
     */
    private OWLOntology topOWLOntology;

    private Slot protegeSubclassesDisjointProperty;

    private TripleStoreModel tripleStoreModel;

    private String defaultLanguage;
    //just for optimization purpose
    private boolean defaultLanguageInitialized = false;
    private FrameListener defaultLanguageListener;


    public AbstractOWLModel(KnowledgeBaseFactory factory) {
        super(factory);

        setFrameFactory(new SWRLJavaFactory(this));

        initializeLoadDefaults();
        initializeExpandShortNamesInMethods();
        intializeImportOwlNamespaces();

        initialize();

        //init namespace manager
        //getNamespaceManager();
    }

    protected void initializeLoadDefaults() {
    	loadDefaults = ApplicationProperties.getBooleanProperty(OWL_MODEL_INIT_DEFAULTS_AT_CREATION, false);
    }


    protected void initializeExpandShortNamesInMethods() {
    	expandShortNameInMethods = ApplicationProperties.getBooleanProperty(OWL_MODEL_EXPAND_SHORT_NAME_IN_METHODS, true);
    }

    protected void intializeImportOwlNamespaces() {
        importNamespaces = ApplicationProperties.getBooleanProperty(OWL_MODEL_IMPORT_NAMESPACES, true);
    }


    public void initialize() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Phase 2 initialization of OWL Model starts");
        }

        boolean eventEnabled = setGenerateEventsEnabled(false);

        setGenerateDeletingFrameEventsEnabled(true);

        setDefaultClsMetaCls(getOWLNamedClassClass());
        setDefaultSlotMetaCls(getOWLDatatypePropertyClass());

        //TODO - change name validation
        setFrameNameValidator(new FrameNameValidator() {

            public String getErrorMessage(String name, Frame frame) {
                if (frame instanceof RDFUntypedResource) {
                    return "The name \"" + name + "\" is not a valid URI.";
                }
                else {
                    String validName = getValidNamespaceFrameName(name);
                    return "The name \"" + name + "\" is not a valid OWL identifier.\n" +
                    "You might want to use \"" + validName + "\" instead.";
                }
            }


            public boolean isValid(String name, Frame frame) {
                if (frame != null && !frame.isEditable()) {
                    return true;
                }
                else if (frame instanceof RDFUntypedResource) {
                    if (name.equals(name.trim()) && name.length() > 4) {
                        try {
                            new URI(name);
                            return true;
                        }
                        catch (Exception ex) {
                        }
                    }
                    return false;
                }
                else {
                    NamespaceManager nsm = getNamespaceManager();
                    return AbstractOWLModel.isValidOWLFrameName(nsm, name);
                }
            }
        });

        defaultAnonymousTypes.add(getRDFListClass());
        defaultAnonymousTypes.add(getOWLAllValuesFromClass());
        defaultAnonymousTypes.add(getOWLSomeValuesFromClass());
        defaultAnonymousTypes.add(getOWLHasValueClass());
        defaultAnonymousTypes.add(getOWLMinCardinalityClass());
        defaultAnonymousTypes.add(getOWLMaxCardinalityClass());
        defaultAnonymousTypes.add(getOWLCardinalityClass());
        defaultAnonymousTypes.add(getOWLComplementClassClass());
        defaultAnonymousTypes.add(getOWLIntersectionClassClass());
        defaultAnonymousTypes.add(getOWLUnionClassClass());
        defaultAnonymousTypes.add(getOWLEnumeratedClassClass());
        defaultAnonymousTypes.add(getOWLAllDifferentClass());
        defaultAnonymousTypes.add(getOWLDataRangeClass());

        taskManager = new DefaultTaskManager();
        taskManager.setProgressDisplay(new NoopProgressDisplay());

        // getFrameStoreManager().setOwlFrameStoresEnabled(true);

        setGenerateEventsEnabled(eventEnabled);

    }


    /**
     * @deprecated - Not called anymore
     */
    @Deprecated
    protected void initializeDefaultAnnotationView() {
    	try {
    		Collection existingConfig = getDefaultAnnotationPropertiesInView();
    		if (existingConfig == null || existingConfig.size() == 0) {
    			setDefaultAnnotationPropertiesInView(CollectionUtilities.createCollection(getRDFSCommentProperty()));
    		}
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at initializing the default annotation view configuration (it's not bad)", e);
		}
	}

    /**
     * This method is not intended for general consumption - use the ImportHelper instead.
     *
     * This is called by internal methods such as the ProtegeOWLParser and the  various creators.
     * Generally it is called to load the data from an import when an import statement is detected.
     * It will not ensure that the importing ontology is actually declared to import the imported uri -
     * that is the job of the caller.
     *
     * @param ontologyName The name of the ontology to be imported.
     */
    public URI loadImportedAssertions(URI ontologyName) throws OntologyLoadException {
        try {
            if (log.isLoggable(Level.FINE)) {
                log.fine("=======================================================");
                log.fine("Processing import " + ontologyName);
            }
            TripleStoreModel tripleStoreModel = getTripleStoreModel();
            for (TripleStore tripleStore : tripleStoreModel.getTripleStores()) {
                if (tripleStore.getIOAddresses().contains(ontologyName.toString())) {
                    log.info(ontologyName.toString() + " already imported as triple store " + tripleStore.getName());
                    return new URI(tripleStore.getName());
                }
            }
            TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
            Repository rep = getRepository(activeTripleStore, ontologyName);
            if(rep != null) {
                log.info("Importing " + ontologyName + " from location: " + rep.getOntologyLocationDescription(ontologyName));
                // ParserUtil.resetUniqueSessionId(); //needed to handle properly anonymous nodeIds across imports
                TripleStore importedTripleStore = rep.loadImportedAssertions(this, ontologyName);
                importedTripleStore.addIOAddress(ontologyName.toString());
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Import Processing of " + ontologyName  + " done");
                    log.fine("=======================================================");
                }
                getNamespaceManager().addImport(importedTripleStore);
                return new URI(importedTripleStore.getName());
            }
            else {
                return null;
            }
        }
        catch (URISyntaxException e) {
            throw new OntologyLoadException(e, e.getMessage());
        }
    }

    private Repository getRepository(TripleStore tripleStore,
                                            URI ontologyName) {
        RepositoryManager rm = getRepositoryManager();
        // Get the repository (ask the system to create a HTTP repository if necessary)
        Repository rep = rm.getRepository(ontologyName, true);
        if(rep == null) {
            rep = unresolvedImportHandler.handleUnresolvableImport(this, tripleStore, ontologyName);
            if(rep != null) {
                rm.addProjectRepository(0, rep);
            }
        }
        return rep;
    }
    
    public Collection<MessageError> getParserErrors() {
        return parserErrors;
    }
    
    public void addParserError(MessageError error) {
        parserErrors.add(error);
    }

    public void addClassListener(ClassListener listener) {
        if (!(listener instanceof ClassAdapter)) {
            throw new IllegalArgumentException("Listener must be a ClassAdapter");
        }
        addClsListener(listener);
    }


    public void addModelListener(ModelListener listener) {
        if (!(listener instanceof ModelAdapter)) {
            throw new IllegalArgumentException("Listener must be a ModelAdapter");
        }
        addKnowledgeBaseListener(listener);
    }


    public void addPropertyListener(PropertyListener listener) {
        if (!(listener instanceof PropertyAdapter)) {
            throw new IllegalArgumentException("Listener must be a PropertyAdapter");
        }
        addSlotListener(listener);
    }


    public void addPropertyValueListener(PropertyValueListener listener) {
        if (!(listener instanceof PropertyValueAdapter)) {
            throw new IllegalArgumentException("Listener must be a PropertyValueAdapter");
        }
        addFrameListener(listener);
    }


    public void addResourceListener(ResourceListener listener) {
        if (!(listener instanceof ResourceAdapter)) {
            throw new IllegalArgumentException("Listener must be a ResourceAdapter");
        }
        addFrameListener(listener);
    }

    @Override
    protected SWRLJavaFactory createFrameFactory() {
        return new SWRLJavaFactory(this);
    }

    @Override
    protected SWRLSystemFrames createSystemFrames() {
        return new SWRLSystemFrames(this);
    }

    @Override
    public synchronized SWRLSystemFrames getSystemFrames() {
        return (SWRLSystemFrames) super.getSystemFrames();
    }


    public TaskManager getTaskManager() {
        if (taskManager == null) {
            taskManager = new DefaultTaskManager();
        }
        return taskManager;
    }

    public void removeClassListener(ClassListener listener) {
        removeClsListener(listener);
    }


    public void removeModelListener(ModelListener listener) {
        removeKnowledgeBaseListener(listener);
    }


    public void removePropertyListener(PropertyListener listener) {
        removeSlotListener(listener);
    }


    public void removePropertyValueListener(PropertyValueListener listener) {
        removeFrameListener(listener);
    }


    public void removeResourceListener(ResourceListener listener) {
        removeFrameListener(listener);
    }


    public void setOWLClassDisplay(OWLClassDisplay renderer) {
        assert renderer != null;
        this.owlClassRenderer = renderer;
    }


    public OWLJavaFactory getOWLJavaFactory() {
        return (OWLJavaFactory) super.getFrameFactory();
    }


    public void setOWLJavaFactory(OWLJavaFactory factory) {
        super.setFrameFactory(factory);
    }


    public void setOWLProject(OWLProject owlProject) {
        this.owlProject = owlProject;
    }


    public PropertyValueValidator getPropertyValueValidator() {
        return propertyValueValidator;
    }


    public boolean isValidPropertyValue(RDFResource subject, RDFProperty predicate, Object value) {
        if (getPropertyValueValidator() == null) {
            return true;
        }
        else {
            return getPropertyValueValidator().isValidPropertyValue(subject, predicate, value);
        }
    }


    public void setPropertyValueValidator(PropertyValueValidator validator) {
        this.propertyValueValidator = validator;
    }


    /**
     * Copies all facet values of restriction superclasses into their named subclasses
     * and thus makes sure that both values are synchronized.
     * This method must be called after a file has been loaded.
     */
    public void copyFacetValuesIntoNamedClses() {
      FacetUpdateFrameStore facetUpdateFrameStore = getFrameStoreManager().getFacetUpdateFrameStore();
      if (FrameStoreManager.isEnabled(facetUpdateFrameStore)) {
          facetUpdateFrameStore.copyFacetValuesIntoNamedClses();
      }
    }

    public void copyFacetValuesIntoProperties() {
        TypeUpdateFrameStore typeUpdateFrameStore = getFrameStoreManager().getTypeUpdateFrameStore();
        if (FrameStoreManager.isEnabled(typeUpdateFrameStore)) {
            typeUpdateFrameStore.updateFacetValues(this);
        }
    }


    public String createNewResourceName(String partialLocalName) {
    	TripleStore activeTripleStore = getTripleStoreModel().getActiveTripleStore();
    	String activeNamespace = activeTripleStore.getDefaultNamespace();

    	if (activeNamespace == null) { //TODO TT - how to handle this?
    		activeNamespace = activeTripleStore.getName() + "#";
    	}

    	return getUniqueFrameName(activeNamespace + partialLocalName);
    }

    public OWLAllDifferent createOWLAllDifferent() {
        return (OWLAllDifferent) createInstance(null, getOWLAllDifferentClass());
    }


    public OWLAllValuesFrom createOWLAllValuesFrom() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLAllValuesFrom) createCls(null, parents, getOWLAllValuesFromClass(), false);
    }


    public OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFResource filler) {
        OWLAllValuesFrom result = createOWLAllValuesFrom();
        result.setOnProperty(property);
        result.setFiller(filler);
        return result;
    }


    public OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues) {
        OWLAllValuesFrom allRestriction = createOWLAllValuesFrom();
        allRestriction.setOnProperty(property);
        OWLDataRange dataRange = createOWLDataRange(oneOfValues);
        allRestriction.setFiller(dataRange);
        return allRestriction;
    }


    public RDFProperty createAnnotationProperty(String name) {
    	if (name == null) {
    		name = createUniqueNewFrameName(DEFAULT_ANNOTATION_PROPERTY_NAME);
    	}

    	RDFProperty annotationProperty = createRDFProperty(name);
    	annotationProperty.setProtegeType(getOWLAnnotationPropertyClass());

    	return annotationProperty;
    }


    public OWLDatatypeProperty createAnnotationOWLDatatypeProperty(String name) {
        OWLDatatypeProperty property = createOWLDatatypeProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        ((Slot) property).addDirectType(getOWLAnnotationPropertyClass());
        return property;
    }


    public OWLObjectProperty createAnnotationOWLObjectProperty(String name) {
        OWLObjectProperty property = createOWLObjectProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        ((Slot) property).addDirectType(getOWLAnnotationPropertyClass());
        return property;
    }


    public OWLCardinality createOWLCardinality() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLCardinality) createCls(null, parents, getOWLCardinalityClass(), false);
    }


    public OWLCardinality createOWLCardinality(RDFProperty property, int value) {
        OWLCardinality cardiRestriction = createOWLCardinality();
        cardiRestriction.setOnProperty(property);
        cardiRestriction.setCardinality(value);
        return cardiRestriction;
    }


    public OWLCardinality createOWLCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLCardinality owlCardinality = createOWLCardinality(property, value);
        owlCardinality.setValuesFrom(qualifier);
        return owlCardinality;
    }

    @Override
    public synchronized Cls createCls(FrameID id,
                                      Collection directSuperclasses,
                                      Collection directTypes,
                                      boolean loadDefaults) {
        if (id == null || id.getName() == null) {
            if (isDefaultAnonymousType(directTypes)) {
                id = new FrameID(getNextAnonymousResourceName());
            }
            else {
                id = new FrameID(createUniqueNewFrameName(DEFAULT_CLASS_NAME));
            }
        }

        return super.createCls(id, directSuperclasses, directTypes, loadDefaults);
    }


    public OWLComplementClass createOWLComplementClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLComplementClass) createCls(null, parents, getOWLComplementClassClass(), false);
    }


    public OWLComplementClass createOWLComplementClass(RDFSClass complement) {
        OWLComplementClass complementCls = createOWLComplementClass();
        complementCls.setComplement(complement);
        return complementCls;
    }


    public OWLDataRange createOWLDataRange() {
        return (OWLDataRange) getOWLDataRangeClass().createAnonymousInstance();
    }


    public OWLDataRange createOWLDataRange(RDFSLiteral[] values) {
        OWLDataRange dataRange = createOWLDataRange();
        RDFList list = createRDFList(Arrays.asList(values).iterator());
        dataRange.setPropertyValue(getOWLOneOfProperty(), list);
        return dataRange;
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name) {
        return createOWLDatatypeProperty(name, getOWLDatatypePropertyClass());
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name, OWLNamedClass metaCls) {
        if (name == null) {
            name = createUniqueNewFrameName(DEFAULT_DATATYPE_PROPERTY_NAME);
        }
        String fullName =  OWLUtil.getInternalFullName(this, name);
        OWLDatatypeProperty slot = (OWLDatatypeProperty) createSlot(fullName, metaCls, loadDefaults);

        ((Slot) slot).setAllowsMultipleValues(true);
        ((Slot) slot).setValueType(ValueType.ANY);
        return slot;
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name, RDFSDatatype datatype) {
        OWLDatatypeProperty property = createOWLDatatypeProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        property.setRange(datatype);
        return property;
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name, RDFSLiteral[] dataRangeLiterals) {
        OWLDatatypeProperty property = createOWLDatatypeProperty(name);
        OWLDataRange range = createOWLDataRange(dataRangeLiterals);
        property.setRange(range);
        return property;
    }


    @Override
	protected FrameStoreManager createFrameStoreManager() {
        return new OWLFrameStoreManager(this);
    }

    @Override
    public OWLFrameStoreManager getFrameStoreManager() {
        return (OWLFrameStoreManager) super.getFrameStoreManager();
    }


    public OWLEnumeratedClass createOWLEnumeratedClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLEnumeratedClass) createCls(null, parents, getOWLEnumeratedClassClass(), false);
    }


    public OWLEnumeratedClass createOWLEnumeratedClass(Collection instances) {
        OWLEnumeratedClass enumerationCls = createOWLEnumeratedClass();
        enumerationCls.setOneOf(instances);
        return enumerationCls;
    }


    /**
     * @deprecated
     */
    @Deprecated
	public RDFExternalResource createRDFExternalResource(String uri) {
        throw new RuntimeException("The class RDFExternalResource has been replaced with RDFUntypedResource");
    }


    public OWLHasValue createOWLHasValue() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLHasValue) createCls(null, parents, getOWLHasValueClass(), false);
    }


    public OWLHasValue createOWLHasValue(RDFProperty property, Object value) {
        OWLHasValue restriction = createOWLHasValue();
        restriction.setOnProperty(property);
        restriction.setHasValue(value);
        return restriction;
    }

    @Override
    public synchronized Instance createInstance(String name, Cls directType) {
    	// TT: should we ignore the loadDefaults for non-system classes?
    	return createInstance(new FrameID(name), CollectionUtilities.createCollection(directType), loadDefaults);
    }

    @Override
    public synchronized Instance createInstance(String name, Collection directTypes) {
         // TT: should we ignore the loadDefaults for non-system classes?
        String fullName = OWLUtil.getInternalFullName(this, name);
        return createInstance(new FrameID(fullName), directTypes, loadDefaults);
    }

    @Override
    public synchronized Instance createInstance(FrameID id, Collection directTypes, boolean initializeDefaults) {
        if (id == null || id.getName() == null) {
            if (isDefaultAnonymousType(directTypes)) {
                id = new FrameID(getNextAnonymousResourceName());
            }
            else {
                Cls firstType = (Cls) directTypes.iterator().next();
                if (firstType instanceof RDFSNamedClass) {
                    id = new FrameID(createNewResourceName(((RDFSClass) firstType).getLocalName()));
                }
            }
        }
        return super.createInstance(id,directTypes, initializeDefaults);
    }


    public OWLIntersectionClass createOWLIntersectionClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLIntersectionClass) createCls(null, parents, getOWLIntersectionClassClass(), false);
    }


    public OWLIntersectionClass createOWLIntersectionClass(Collection clses) {
        OWLIntersectionClass intersectionCls = createOWLIntersectionClass();
        for (Iterator it = clses.iterator(); it.hasNext();) {
            RDFSClass cls = (RDFSClass) it.next();
            intersectionCls.addOperand(cls);
        }
        return intersectionCls;
    }


    public RDFList createRDFList() {
        return (RDFList) getRDFListClass().createDirectInstance(null);
    }


    public RDFList createRDFList(Iterator values) {
        return createListInstance(values, getRDFListClass());
    }


    public RDFSClass createRDFSClassFromExpression(String parsableExpression) {
        try {
            OWLClassParser parser = getOWLClassDisplay().getParser();
            return parser.parseClass(this, parsableExpression);
        }
        catch (Exception ex) {
            // Don't print Exception but return null
            return null;
        }
    }


    public RDFSDatatype createRDFSDatatype(String name) {
        return (RDFSDatatype) getRDFSDatatypeClass().createDirectInstance(name);
    }


    private RDFList createListInstance(Iterator values, Cls listCls) {
        if (!values.hasNext()) {
            return getRDFNil();
        }
        else {
            RDFList li = (RDFList) listCls.createDirectInstance(null);
            RDFList currentNode = li;
            while (values.hasNext()) {
                Object value = values.next();
                currentNode.setFirst(value);
                if (values.hasNext()) {
                    RDFList rest = (RDFList) listCls.createDirectInstance(null);
                    currentNode.setRest(rest);
                    currentNode = rest;
                }
            }
            currentNode.setRest(getRDFNil());
            return li;
        }
    }


    public OWLMaxCardinality createOWLMaxCardinality() {
        Collection parents = Collections.singleton(getAnonymousRootCls());
        return (OWLMaxCardinality) createCls(null, parents, getOWLMaxCardinalityClass(), false);
    }


    public OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value) {
        OWLMaxCardinality maxCardiRestriction = createOWLMaxCardinality();
        maxCardiRestriction.setOnProperty(property);
        maxCardiRestriction.setCardinality(value);
        return maxCardiRestriction;
    }


    public OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLMaxCardinality owlMaxCardinality = createOWLMaxCardinality(property, value);
        owlMaxCardinality.setValuesFrom(qualifier);
        return owlMaxCardinality;
    }


    public OWLMinCardinality createOWLMinCardinality() {
        Collection parents = Collections.singleton(getAnonymousRootCls());
        return (OWLMinCardinality) createCls(null, parents, getOWLMinCardinalityClass(), false);
    }


    public OWLMinCardinality createOWLMinCardinality(RDFProperty property, int value) {
        OWLMinCardinality minCardiRestriction = createOWLMinCardinality();
        minCardiRestriction.setOnProperty(property);
        minCardiRestriction.setCardinality(value);
        return minCardiRestriction;
    }


    public OWLMinCardinality createOWLMinCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLMinCardinality owlMinCardinality = createOWLMinCardinality(property, value);
        owlMinCardinality.setValuesFrom(qualifier);
        return owlMinCardinality;
    }


    public OWLNamedClass createOWLNamedClass(String name) {
        return createOWLNamedClass(name, loadDefaults);
    }


    public OWLNamedClass createOWLNamedClass(String name, boolean loadDefaults) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        return (OWLNamedClass) createCls(fullName, getRootClses(), getOWLNamedClassClass(), loadDefaults);
    }


    public OWLNamedClass createOWLNamedClass(String name, OWLNamedClass metaCls) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        return (OWLNamedClass) createCls(fullName, getRootClses(), metaCls, loadDefaults);
    }


    public OWLNamedClass createOWLNamedSubclass(String name, OWLNamedClass superclass) {
        OWLNamedClass result = createOWLNamedClass(name);
        if (!superclass.equals(getOWLThingClass())) {
            result.addSuperclass(superclass);
            result.removeSuperclass(getOWLThingClass());
        }
        return result;
    }


    public OWLObjectProperty createOWLObjectProperty(String name) {
        return createOWLObjectProperty(name, getOWLObjectPropertyClass());
    }


    public OWLObjectProperty createOWLObjectProperty(String name, OWLNamedClass metaCls) {
        if (name == null) {
            name = createUniqueNewFrameName(DEFAULT_OBJECT_PROPERTY_NAME);
        }
        String fullName = OWLUtil.getInternalFullName(this, name);
        OWLObjectProperty result = (OWLObjectProperty) createSlot(fullName, metaCls, loadDefaults);

        ((Slot) result).setAllowsMultipleValues(true);
        ((Slot) result).setValueType(ValueType.INSTANCE);
        return result;
    }


    public OWLObjectProperty createOWLObjectProperty(String name, Collection allowedClasses) {
        OWLObjectProperty slot = createOWLObjectProperty(name);
        slot.setUnionRangeClasses(allowedClasses);
        return slot;
    }


    public OWLOntology createOWLOntology(String uri) throws AlreadyImportedException {
        if (getDefaultOWLOntology() == null) {
            FactoryUtils.addOntologyToTripleStore(this, tripleStoreModel.getTopTripleStore(), uri);
        }
        else {
            OWLUtil.renameOntology(this, getDefaultOWLOntology(), uri);
        }
        return getDefaultOWLOntology();
    }


    /**
     * @deprecated
     */
    @Deprecated
	public OWLOntology createOWLOntology(String name, String uri) throws AlreadyImportedException {
        //String prefix = getNamespaceManager().getPrefix(uri);
        return createOWLOntology(uri);
    }


    public RDFSNamedClass createRDFSNamedClass(String name) {
        return createRDFSNamedClass(name, true);
    }


    public RDFSNamedClass createRDFSNamedClass(String name, boolean loadDefaults) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        return (RDFSNamedClass) createCls(fullName, getRootClses(), getRDFSNamedClassClass(), loadDefaults);
    }


    public RDFSNamedClass createRDFSNamedClass(String name, Collection parents, RDFSClass rdfType) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        return (RDFSNamedClass) createCls(fullName, parents, rdfType);
    }


    public RDFSNamedClass createRDFSNamedSubclass(String name, RDFSNamedClass superclass) {
        RDFSNamedClass result = createRDFSNamedClass(name);
        if (!superclass.equals(getOWLThingClass())) {
            result.addSuperclass(superclass);
            result.removeSuperclass(getOWLThingClass());
        }
        return result;
    }


    public RDFUntypedResource createRDFUntypedResource(String uri) {
        Instance instance = getRDFExternalResourceClass().createDirectInstance(uri);
        return (RDFUntypedResource) instance;
    }


    public RDFProperty createRDFProperty(String name) {
        if (name == null) {
            name = createUniqueNewFrameName(DEFAULT_PROPERTY_NAME);
        }
        String fullName = OWLUtil.getInternalFullName(this, name);
        RDFProperty property = (RDFProperty) createSlot(fullName, getRDFPropertyClass(), loadDefaults);
        ((Slot) property).setValueType(ValueType.ANY);
        ((Slot) property).setAllowsMultipleValues(true);
        return property;
    }


    public Triple createTriple(RDFResource subject, RDFProperty predicate, Object object) {
        return new DefaultTriple(subject, predicate, object);
    }


    public Set<String> getAllImports() {
        Set<String> imports = new HashSet<String>();
        for (Iterator it = getOWLOntologies().iterator(); it.hasNext();) {
            Object curImport = it.next();
            OWLOntology ontology = (OWLOntology) curImport;
            for (Object impo : ontology.getImports()) {
                if (impo instanceof RDFResource) {
                    imports.add(((RDFResource) impo).getURI());
                }
                else if (impo instanceof String) {
                    imports.add((String) impo);
                }
            }
        }
        return imports;
    }


    public RDFSNamedClass createSubclass(String name, RDFSNamedClass superclass) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        return (RDFSNamedClass) createCls(fullName, Collections.singleton(superclass));
    }


    public RDFSNamedClass createSubclass(String name, Collection superclasses) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        return (RDFSNamedClass) createCls(fullName, superclasses);
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLSomeValuesFrom) createCls(null, parents, getOWLSomeValuesFromClass(), false);
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFResource filler) {
        OWLSomeValuesFrom someRestriction = createOWLSomeValuesFrom();
        someRestriction.setOnProperty(property);
        someRestriction.setFiller(filler);
        return someRestriction;
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues) {
        OWLSomeValuesFrom someRestriction = createOWLSomeValuesFrom();
        someRestriction.setOnProperty(property);
        OWLDataRange dataRange = createOWLDataRange(oneOfValues);
        someRestriction.setFiller(dataRange);
        return someRestriction;
    }


    public RDFProperty createSubproperty(String name, RDFProperty superProperty) {
    	Collection<Cls> metaClses = new ArrayList<Cls>(superProperty.getProtegeTypes());
    	Cls firstMetaCls = CollectionUtilities.getFirstItem(metaClses);
        String fullName = OWLUtil.getInternalFullName(this, name);
        Slot slot = createSlot(fullName, firstMetaCls, Collections.singleton(superProperty), true);
        metaClses.remove(firstMetaCls);
        for (Object element : metaClses) {
			Cls metacls = (Cls) element;
			slot.addDirectType(metacls);
		}
        return (RDFProperty)slot;
    }


    @Override
	public boolean endTransaction() {
        return commitTransaction();
    }


    public QueryResults executeSPARQLQuery(String partialQueryText) throws Exception {
        String queryString = SPARQLQueryResults.createPrefixDeclarations(this) + partialQueryText;
        return SPARQLQueryResults.create(this, queryString);
    }


    public OWLUnionClass createOWLUnionClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLUnionClass) createCls(null, parents, getOWLUnionClassClass(), false);
    }


    public OWLUnionClass createOWLUnionClass(Collection clses) {
        OWLUnionClass unionCls = createOWLUnionClass();
        for (Iterator it = clses.iterator(); it.hasNext();) {
        	Cls cls = (Cls) it.next();
        	if (cls instanceof RDFSClass) {
        		unionCls.addOperand((RDFSClass)cls);
        	} else {
        		Log.getLogger().warning(cls + " is not an RDFSClass. It will not be added to the OWLUnionClass.");
        	}
        }
        return unionCls;
    }


    public RDFSLiteral createRDFSLiteral(Object value) {
        return DefaultRDFSLiteral.create(this, value);
    }


    public RDFSLiteral createRDFSLiteral(String lexicalValue, RDFSDatatype datatype) {
        return DefaultRDFSLiteral.create(this, lexicalValue, datatype);
    }


    public RDFSLiteral createRDFSLiteral(String value, String language) {
        return DefaultRDFSLiteral.create(this, value, language);
    }


    public Object createRDFSLiteralOrString(String value, String language) {
        if (language != null) {
            language = language.trim();
            if (language.length() > 0) {
                return createRDFSLiteral(value, language);
            }
        }
        return value;
    }


    /**
     * Deletes all Frames that are not system classes (including OWL system classes).
     */
    public static void deleteAllFrames(OWLModel owlModel) {
        KnowledgeBase kb = owlModel;
        for (Object element : kb.getInstances()) {
            Instance instance = (Instance) element;
            if (!(instance instanceof Cls) &&
                (instance.isEditable() || instance.isIncluded()) && !instance.isSystem()) {
                kb.deleteFrame(instance);
            }
        }

        Collection metaClasses = new ArrayList();
        for (Iterator it = owlModel.getUserDefinedOWLNamedClasses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if ((cls.isEditable() || cls.isIncluded()) && !cls.isSystem()) {
                if (cls.isMetaCls()) {
                    metaClasses.add(cls);
                }
            }
        }
        for (Iterator it = metaClasses.iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            for (Object element : cls.getInstances()) {
                Frame frame = (Frame) element;
                kb.deleteFrame(frame);
            }
        }
        for (Iterator it = owlModel.getUserDefinedOWLNamedClasses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls.getDirectType() != null && (cls.isEditable() || cls.isIncluded()) && !cls.isSystem()) {
                kb.deleteFrame(cls);
            }
        }
    }


    @Override
	public void deleteCls(Cls cls) {
    	getHeadFrameStore().deleteCls(cls);
    	cls.markDeleted(true);
    }


    public Collection getOWLAllDifferents() {
        Cls metaCls = getOWLAllDifferentClass();
        return metaCls.getDirectInstances();
    }


    public Collection<RDFProperty> getOWLAnnotationProperties() {
    	return getOWLAnnotationPropertyClass().getInstances(true);
    }


    @Override
	public synchronized String getBrowserText(Instance instance) {
    	if (!(instance instanceof RDFResource)) {
			return super.getBrowserText(instance);
		}
    	if (instance.isDeleted()) {
			return "<deleted>";
		}
    	if (instance instanceof OWLAnonymousClass) {
			return instance.getBrowserText();
		}
    	if (getProject() == null) {
			return getName(instance);
		}

    	Cls directType = (instance instanceof RDFResource) ?
    			OWLUI.getOneNamedDirectTypeWithBrowserPattern((RDFResource) instance) : instance.getDirectType();

       	if (directType == null) {
        	return getMissingTypeString(instance);
       	}

       	OWLBrowserSlotPattern slotPattern = null;

       	try {
       		slotPattern = (OWLBrowserSlotPattern) getProject().getBrowserSlotPattern(directType);
       	} catch (ClassCastException e) {
       		try {
			slotPattern = OWLUI.fixBrowserSlotPattern(getProject(), directType);
			//Log.getLogger().warning("Non-OWL browser slot for: " + directType + " ... Convert it to OWLBrowserSlotPattern");
       		} catch (Exception ex) {
       			Log.getLogger().log(Level.WARNING, "Error at getting browser slot pattern for " + instance, ex);
       		}
		} catch(Exception e) {
       		slotPattern = null;
       		Log.getLogger().log(Level.WARNING, "Unknown error at getting the browser slot for: " + directType, e);
       	}

         if (slotPattern == null) {
			return getDisplaySlotNotSetString(instance);
		}

         String value = slotPattern.getBrowserText(instance);
         if (value == null) {
             value = getDisplaySlotPatternValueNotSetString(instance, slotPattern);
         }

         return value;
	}


    @Override
	protected String getDisplaySlotPatternValueNotSetString(Instance instance, BrowserSlotPattern slotPattern) {
    	return NamespaceUtil.getPrefixedName(this, instance.getName());
    }

    @Override
	protected String getDisplaySlotNotSetString(Instance instance) {
    	return NamespaceUtil.getPrefixedName(this, instance.getName());
    }



    public Collection getChangedInferredClasses() {
        return getClsesWithClassificationStatus(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED);
    }

    /**
     * Gets the most specific common named superclasses of a given collection of named classes.
     *
     * @param classes the RDFSNamedClasses to get the superclasses of (at least one)
     * @return the most specific common superclasses of all classes, e.g. owl:Thing
     */
    public Set<RDFSNamedClass> getCommonSuperclasses(Collection<RDFSNamedClass> classes) {
        Set<RDFSNamedClass> commonSupers = new HashSet<RDFSNamedClass>();
        boolean firstTime = true;
        for (RDFSNamedClass inputClass : classes) {
            if (firstTime) {
                Collection firstInputClassSupers = inputClass.getSuperclasses(true);
                for (Object aFirstSuper  : firstInputClassSupers) {
                    if (aFirstSuper instanceof RDFSNamedClass) {
                        commonSupers.add((RDFSNamedClass) aFirstSuper);
                    }
                }
                firstTime = false;
            }
            else {
                commonSupers.retainAll(inputClass.getSuperclasses(true));
            }
        }

        // the mysterious if check is to ensure no problems in the case getCommonSuperclasses({ A }) where
        //   A \subseteq B \subseteq C \subseteq B
        // We don't want to remove both B and C but either one will do for keeping.
        List<RDFSNamedClass> commonSupersCopy = new ArrayList<RDFSNamedClass>(commonSupers);
        for (RDFSNamedClass namedClass : commonSupersCopy) {
            if (commonSupers.contains(namedClass)) {
                Collection superClasses = namedClass.getSuperclasses(true);
                if (superClasses.contains(namedClass)) {
                    superClasses = new HashSet(superClasses);
                    superClasses.remove(namedClass);
                }
                commonSupers.removeAll(superClasses);
            }
        }
        return commonSupers;
    }

    /**
     * Chooses a most specific common named superclass of a given collection of named classes.
     *
     * @param classes the RDFSNamedClasses to get the superclass of (at least one)
     * @return the most specific common superclass of all classes, e.g. owl:Thing
     */
    public RDFSNamedClass getCommonSuperclass(Collection<RDFSNamedClass> classes) {
        return getCommonSuperclasses(classes).iterator().next();
    }


    private Collection getClsesWithClassificationStatus(int status) {
        final Slot slot = getProtegeClassificationStatusProperty();
        Collection matches = getFramesWithValue(slot, null, false,
                                                new Integer(status));
        List result = new ArrayList();
        for (Iterator it = matches.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (frame instanceof OWLNamedClass) {
                result.add(frame);
            }
        }
        return result;
    }


    public OWLDatatypeProperty getOWLDatatypeProperty(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }
        return (OWLDatatypeProperty) getSlot(fullName);
    }

    public String getDefaultLanguage() {
        String langaugeFromProperty = ApplicationProperties.getString(RenderingPanel.DEFAULT_LANGUATE_PROPERTY);
        if (langaugeFromProperty != null) {
            return langaugeFromProperty;
        }
        if (defaultLanguageInitialized == true) {
            return defaultLanguage;
        }
        //default language not initialized
        defaultLanguage = null;
        RDFProperty metaSlot = getDefaultLanguageProperty();
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                Object value = oi.getPropertyValue(metaSlot);
                if (value instanceof String) {
                	String stringValue = (String) value;
                	if (stringValue != null && stringValue.length() > 0) {
                		defaultLanguage = stringValue;
                	}
                }
            }
        }
        defaultLanguageInitialized = true;
        return defaultLanguage;
    }

    public RDFProperty getDefaultLanguageProperty() {
        return getRDFProperty(ProtegeNames.getDefaultLanguageSlotName());
    }

    public void resetOntologyCache() {
        detachDefaultLanguageListener();
        topOWLOntology = null;
    }


    public OWLOntology getDefaultOWLOntology() {
        if (topOWLOntology == null) {
            topOWLOntology= getTripleStoreModel().getTopTripleStore().getOWLOntology();
            attachDefaultLanguageListener();
        }
        return topOWLOntology;
    }


    @SuppressWarnings("deprecation")
    protected void attachDefaultLanguageListener() {
        if (topOWLOntology == null) { return; }
        defaultLanguageListener = createDefaultLanguageListener();
        topOWLOntology.addFrameListener(defaultLanguageListener);
    }

    protected void detachDefaultLanguageListener() {
        if (topOWLOntology == null) { return; }
        try {
            topOWLOntology.removeFrameListener(defaultLanguageListener);
        }  catch (Throwable t) {
            Log.getLogger().log(Level.WARNING, "Error in dispose of OWL Model: Could not detach default language listener", t);
        }
        defaultLanguage = null;
        defaultLanguageInitialized = false;
    }

    public Collection getDomainlessProperties() {
        return getRootCls().getDirectTemplateSlots();
    }


    public OWLProject getOWLProject() {
    	if (owlProject == null) {
    		owlProject = new DefaultOWLProject(getProject());
    	}
        return owlProject;
    }


    public RepositoryManager getRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = new RepositoryManager(this);
            RepositoryFileManager man = new RepositoryFileManager(this);
            man.loadGlobalRepositories();
        }
        return repositoryManager;
    }


    /**
     * @deprecated
     */
    @Deprecated
	public RDFExternalResource getRDFExternalResource(String uri) {
    	return (RDFExternalResource) getRDFResource(uri);
    }



    public Collection getInconsistentClasses() {
        return getOWLNothing().getInferredSubclasses();
    }


    public com.hp.hpl.jena.rdf.model.Model getJenaModel() {
        if (jenaModel == null) {
            jenaModel = JenaModelFactory.createModel(this);
        }
        return jenaModel;
    }



    public OWLClassParser getOWLClassParser() {
        return getOWLClassDisplay().getParser();
    }


    public GlobalParserCache getGlobalParserCache() {
    	if (globalParserCache == null) {
    		globalParserCache = new GlobalParserCache(this);
    	}

    	return globalParserCache;
    }


    public OWLClassDisplay getOWLClassDisplay() {
        return owlClassRenderer;
    }


    /*
     * Seems pretty hokey.  The magic number is adjusted to make the
     * OWLModelTestCase work...  It  assumes  that the number  of non
     * rdf frames  in the system is constant.  Graphviz anyone?
     */
    public int getRDFResourceCount() {
        return getFrameCount() - 62;
    }


    public Collection getRDFResources() {
        Collection frames = getFrames();
        return getRDFResources(this, frames);
    }


    public Collection getRDFResourcesWithPropertyValue(RDFProperty property, Object value) {
        Collection frames = getFramesWithValue(property, null, false, value);
        return getRDFResources(this, frames);
    }


    public static Collection<RDFResource> getRDFResources(KnowledgeBase kb, Collection<? extends Frame> frames) {
        ArrayList<RDFResource> result = new ArrayList<RDFResource>();
        for (Frame frame : frames) {
            if (frame instanceof RDFResource) {
                result.add((RDFResource) frame);
            }
        }
        removeProtegeSystemResources(kb, result);
        return result;
    }


    public RDFUntypedResource getRDFUntypedResource(String uri, boolean createOnDemand) {
        RDFUntypedResource r = (RDFUntypedResource) getFrame(uri);
        if (createOnDemand && r == null) {
            r = createRDFUntypedResource(uri);
        }
        return r;
    }


    public Collection<RDFResource> getResourceNameMatches(String nameExpression, int maxMatches) {
        Collection frames = getFrameNameMatches(nameExpression, maxMatches);
        return getRDFResources(this, frames);
    }


    public OWLNamedClass getOWLNamedClass(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }
        return (OWLNamedClass) getCls(fullName);
    }


    public Collection getMatchingResources(RDFProperty property, String matchString, int maxMatches) {
        Collection frames = getMatchingFrames(property, null, false, matchString, maxMatches);
        return getRDFResources(this, frames);
    }


    private static int anonCount = 1;

    public String getNextAnonymousResourceName() {
        return AbstractOWLModel.getNextAnonymousResourceNameStatic();
    }

    /*
     * values must be guaranteed to be distinct from values
     */
    public static String getNextAnonymousResourceNameStatic() {
        StringBuffer sb = new StringBuffer(ANONYMOUS_BASE);
        sb.append(anonCount++);
        sb.append('_');
        sb.append(UNIQUE_SESSION_ID);
        return sb.toString();
    }



    public Collection getRDFProperties() {
        return getRDFResources(this, getSlots());
    }


    public RDFResource getRDFResource(String name) {
    	String internalName = OWLUtil.getInternalFullName(this, name);
    	if (internalName == null) {
    		return null;
    	}
    	Frame frame = getFrame(internalName);
        return frame instanceof RDFResource ? (RDFResource) frame : null ;
    }


    public RDFResource getRDFResourceAs(String name, Class javaInterface) {
        RDFResource resource = getRDFResource(name);
        if (resource != null) {
            return resource.as(javaInterface);
        }
        else {
            return null;
        }
    }


    public OWLIndividual getOWLIndividual(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }
        return (OWLIndividual) getFrame(fullName);
    }


    public Collection getOWLRestrictionsOnProperty(RDFProperty property) {
        return getFramesWithValue(getOWLOnPropertyProperty(), null, false, property);
    }


    public Collection getSearchSynonymProperties() {
        Collection results = new HashSet();
        String syns = getOWLProject().getSettingsMap().getString(SEARCH_SYNONYMS_KEY);
        if (syns != null) {
            String[] ss = syns.split(SEARCH_SYNONYMS_SEPARATOR);
            for (String s : ss) {
                Frame frame = getFrame(s);
                if (frame instanceof Slot) {
                    results.add(frame);
                }
            }
        }
        return results;
    }


    public Collection getDefaultAnnotationPropertiesInView() {
        Collection results = new HashSet();
        String syns = getOWLProject().getSettingsMap().getString(DEFAULT_ANNOT_PROP_IN_VIEW_KEY);
        if (syns != null) {
            String[] ss = syns.split(SEARCH_SYNONYMS_SEPARATOR);
            for (String s : ss) {
                Frame frame = getFrame(s);
                if (frame instanceof Slot) {
                    results.add(frame);
                }
            }
        } else { //the default case
        	results.add(getRDFSCommentProperty());
        }
        return results;
    }

    public OWLObjectProperty getOWLObjectProperty(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }
        return (OWLObjectProperty) getSlot(fullName);
    }


    public Collection getOWLOntologies() {
        return getOWLOntologyClass().getInstances();
    }


    public OWLOntology getOWLOntologyByURI(String uri) {
        OWLOntology ont = null;
        try {
            URI properURI = new URI(uri);
            ont = (OWLOntology) getOWLOntologyByURI(properURI);
        }
        catch (URISyntaxException e) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        return ont;
    }


    public RDFResource getOWLOntologyByURI(URI uri) {
        return getRDFResource(uri.toString());
    }


    public Collection getOWLOntologyProperties() {
        return Arrays.asList(new Slot[]{
                getOWLBackwardCompatibleWithProperty(),
                getOWLIncompatibleWithProperty(),
                getOWLPriorVersionProperty()
        });
    }


    /**
     * @deprecated use getRDFSClasses instead
     */
    @Deprecated
    public Collection getOWLClasses() {
        return getRDFSClasses();
    }


    public OWLFrameStore getOWLFrameStore() {
        return getFrameStoreManager().getOWLFrameStore();
    }


    public Collection getOWLIndividuals() {
        return getOWLIndividuals(false);
    }


    public Collection getOWLIndividuals(boolean onlyVisibleClasses) {
        Collection<RDFIndividual> result = getRDFIndividuals(listOWLNamedClasses(), onlyVisibleClasses);
        Iterator<RDFIndividual> resultIt = result.iterator();
        while (resultIt.hasNext()) {
            if (resultIt.next().isSystem()) {
                resultIt.remove();
            }
        }
        Iterator it = listOWLAnonymousClasses();
        while (it.hasNext()) {
            OWLAnonymousClass c = (OWLAnonymousClass) it.next();
            Collection instances = c.getInstances(false);
            for (Iterator is = instances.iterator(); is.hasNext();) {
                Instance instance = (Instance) is.next();
                if (instance instanceof RDFIndividual && !instance.isSystem()) {
                    result.add((RDFIndividual) instance);
                }
            }
        }
        return result;
    }


    public OWLProperty getOWLProperty(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }
        return (OWLProperty) getSlot(fullName);
    }


    public Collection getOWLSystemResources() {
        Collection result = new ArrayList(Arrays.asList(getOWLSystemFramesArray()));
        result.addAll(getRDFSDatatypeClass().getDirectInstances());
        return result;
    }


    /**
     * Gets the array of OWL System frames.
     * <p/>
     * An important side effect of this method is that it initializes
     * several of the variables used by the AbstractOWLModel.
     */

    protected Frame[] getOWLSystemFramesArray() {
        Collection<Frame> frameSet = getSystemFrames().getFrames();
        Set<Frame> owlFrameSet = new HashSet<Frame>();
        for (Frame frame : frameSet) {
            if (frame instanceof RDFResource) {
                owlFrameSet.add(frame);
            }
        }
        return owlFrameSet.toArray(new Frame[owlFrameSet.size()]);
    }



    public RDFProperty getProtegeAllowedParentProperty() {
        return getRDFProperty(ProtegeNames.PROTEGE_OWL_NAMESPACE + ProtegeNames.ALLOWED_PARENT);
    }


    public ValueType getOWLValueType(String uri) {
        return XMLSchemaDatatypes.getValueType(uri);
    }


    public RDFIndividual getRDFIndividual(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }
        return (RDFIndividual) getFrame(fullName);
    }


    public Collection<RDFIndividual> getRDFIndividuals() {
        return getRDFIndividuals(false);
    }


    public Collection<RDFIndividual> getRDFIndividuals(boolean onlyVisibleClasses) {
        return getRDFIndividuals(listRDFSNamedClasses(), onlyVisibleClasses);
    }


    private Collection<RDFIndividual> getRDFIndividuals(Iterator it, boolean onlyVisibleClasses) {
        Collection<RDFIndividual> result = new HashSet<RDFIndividual>();
        while (it.hasNext()) {
            RDFSNamedClass c = (RDFSNamedClass) it.next();
            if (c.isVisible() || !onlyVisibleClasses) {
                Collection instances = c.getInstances(false);
                for (Iterator is = instances.iterator(); is.hasNext();) {
                    Instance instance = (Instance) is.next();
                    if (instance instanceof RDFIndividual &&
                        !(instance instanceof OWLOntology) &&
                        !(instance instanceof RDFList) &&
                        !(instance instanceof OWLAllDifferent) &&
                        !instance.isSystem()) {
                        result.add((RDFIndividual) instance);
                    }
                }
            }
        }
        return result;
    }


    public RDFSNamedClass getRDFSNamedClass(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return  null;
        }
        return (RDFSNamedClass) getCls(fullName);
    }


    public RDFProperty getRDFProperty(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }

        return (RDFProperty) getSlot(fullName);
    }


    /*
     * This is a bit hacky but it should run faster than this.getSlot(...).  As long
     * as we don't try to determine that protege.owl is not imported by checking the
     * result of this call.
     *
     * See OWLModel.getProtegeReadOnlyProperty javadoc for explanation of protege:readOnly property.
     */
    public RDFProperty getProtegeReadOnlyProperty() {
        return new DefaultOWLDatatypeProperty(this, new FrameID(ProtegeNames.getReadOnlySlotName()));
    }


    public RDFSDatatype getRDFSDatatypeByName(String name) {
        String fullName = OWLUtil.getInternalFullName(this, name);
        if (fullName == null) {
            return null;
        }
        return getRDFSDatatypeByURI(fullName);
    }

    public RDFSDatatype getRDFSDatatypeByURI(String uri) {
    	 return uri == null ? null : (RDFSDatatype) getFrame(uri);
    }


    public RDFSDatatype getRDFSDatatypeOfValue(Object valueOrRDFSLiteral) {
        if (valueOrRDFSLiteral instanceof RDFSLiteral) {
            return ((RDFSLiteral) valueOrRDFSLiteral).getDatatype();
        }
        else {
            return DefaultRDFSLiteral.create(this, valueOrRDFSLiteral).getDatatype();
        }
    }


    public Collection<RDFSDatatype> getRDFSDatatypes() {
        List<RDFSDatatype> results = new ArrayList<RDFSDatatype>();
        Iterator it = getRDFSDatatypeClass().getDirectInstances().iterator();
        while (it.hasNext()) {
            RDFSDatatype datatype = (RDFSDatatype) it.next();
            if (!datatype.isAnonymous()) {
                results.add(datatype);
            }
        }
        return results;
    }

    public RDFSNamedClass[] getOWLRestrictionMetaclasses() {
        return new RDFSNamedClass[]{// Order may be used for sorting and should be preserved
                getOWLAllValuesFromClass(),
                getOWLSomeValuesFromClass(),
                getOWLHasValueClass(),
                getOWLCardinalityClass(),
                getOWLMinCardinalityClass(),
                getOWLMaxCardinalityClass()
        };
    }


    public RDFProperty[] getSystemAnnotationProperties() {
        return new RDFProperty[]{
                getRDFSSeeAlsoProperty(),
                getRDFSIsDefinedByProperty(),
                getRDFSLabelProperty(),
                getOWLVersionInfoProperty(),
                getOWLBackwardCompatibleWithProperty(),
                getOWLIncompatibleWithProperty(),
                getOWLPriorVersionProperty(),
                getRDFSCommentProperty()
        };
    }


    public String getTodoAnnotationPrefix() {
        RDFProperty metaSlot = getRDFProperty(ProtegeNames.getTodoPrefixSlotName());
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                String value = (String) oi.getPropertyValue(metaSlot);
                if (value != null) {
                    return value;
                }
            }
        }
        return DEFAULT_TODO_PREFIX;
    }


    public OWLDatatypeProperty getTodoAnnotationProperty() {
        RDFProperty metaSlot = getRDFProperty(ProtegeNames.getTodoPropertySlotName());
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                Object slot = oi.getPropertyValue(metaSlot);
                if (slot instanceof OWLDatatypeProperty) {
                    return (OWLDatatypeProperty) slot;
                }
            }
        }
        return (OWLDatatypeProperty) getOWLVersionInfoProperty();
    }

    public TripleStoreModel getTripleStoreModel() {
        if (tripleStoreModel == null) {
            tripleStoreModel = new TripleStoreModelImpl(this);
        }
        return tripleStoreModel;
    }


    public String[] getUsedLanguages() {
        RDFProperty metaSlot = getRDFProperty(ProtegeNames.getUsedLanguagesSlotName());
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                Collection values = oi.getPropertyValues(metaSlot);
                if (values.size() > 0) {
                    return (String[]) values.toArray(new String[0]);
                }
            }
        }
        return DEFAULT_USED_LANGUAGES;
    }


    public Collection getUserDefinedOWLNamedClasses() {
        return getUserDefinedInstances(getOWLNamedClassClass());
    }


    public Collection getUserDefinedRDFIndividuals(boolean onlyVisibleClasses) {
        List result = new ArrayList();
        Iterator it = getRDFIndividuals(onlyVisibleClasses).iterator();
        while (it.hasNext()) {
            RDFResource resource = (RDFResource) it.next();
            if (!resource.isSystem()) {
                result.add(resource);
            }
        }
        return result;
    }


    private Collection getUserDefinedInstances(Cls cls) {
        Collection<Instance> instances = cls.getInstances();
        return getUserDefinedInstances(instances);
    }


    private Collection getUserDefinedInstances(Collection instances) {
        List results = new ArrayList();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (!frame.isSystem()) {
                results.add(frame);
            }
        }
        return results;
    }


    public Collection getUserDefinedOWLProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLDatatypePropertyClass().getInstances());
        instances.addAll(getOWLObjectPropertyClass().getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedOWLObjectProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLObjectPropertyClass().getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedOWLDatatypeProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLDatatypePropertyClass().getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedRDFProperties() {
        return getUserDefinedInstances(getRDFPropertyClass());
    }


    public Collection getUserDefinedRDFSNamedClasses() {
        return getUserDefinedInstances(getRDFSNamedClassClass());
    }


    public String getValueTypeURI(ValueType valueType) {
        return XMLSchemaDatatypes.getValueTypeURI(valueType);
    }


    public Collection getVisibleUserDefinedOWLProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLDatatypePropertyClass().getInstances());
        instances.addAll(getOWLObjectPropertyClass().getInstances());
        return getVisibleUserDefinedInstances(instances);
    }


    private Collection getVisibleUserDefinedInstances(Cls cls) {
        Collection instances = cls.getInstances();
        return getVisibleUserDefinedInstances(instances);
    }


    private Collection getVisibleUserDefinedInstances(Collection instances) {
        List results = new ArrayList();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (!frame.isSystem() && frame.isVisible()) {
                results.add(frame);
            }
        }
        return results;
    }


    public Collection getVisibleUserDefinedRDFProperties() {
        // TODO: Maybe remove protege:slots
        return getVisibleUserDefinedInstances(getRDFPropertyClass());
    }


    public RDFSLiteral asRDFSLiteral(Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof RDFSLiteral) {
            return (RDFSLiteral) value;
        }
        else {
            return createRDFSLiteral(value);
        }
    }


    public RDFObject asRDFObject(Object object) {
        if (object == null) {
            return null;
        }
        else if (object instanceof RDFExternalResource) {
            return null;
        }
        else if (object instanceof RDFResource) {
            return (RDFObject) object;
        }
        else {
            return createRDFSLiteral(object);
        }
    }


    public List asRDFSLiterals(Collection values) {
        List result = new LinkedList();
        for (Iterator it = values.iterator(); it.hasNext();) {
        	Object o = it.next();
        	result.add(asRDFSLiteral(o));
        }
        return result;
    }



    /**
     * @deprecated
     */
    @Deprecated
	public boolean isAnonymousResource(RDFResource resource) {
        return resource.isAnonymous();
    }


    public boolean isAnonymousResourceName(String name) {
        return name.length() > 0 && name.charAt(0) == '@';    // name.startsWith(ANONYMOUS_BASE); but faster
    }


    public boolean isDefaultAnonymousType(Cls type) {
        return defaultAnonymousTypes.contains(type);
    }


    public boolean isDefaultAnonymousType(Collection types) {
        for (Iterator it = types.iterator(); it.hasNext();) {
            Cls type = (Cls) it.next();
            if (isDefaultAnonymousType(type)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @deprecated
     */
    @Deprecated
	public boolean isOWLSystemFrame(Frame frame) {
        return getOWLSystemResources().contains(frame);
    }


    /*
     * This fix is incomplete.  It should be completed before being merged back into the
     * head.  The intention is that we do not continually as the knowledge base whether there
     * is a frame with a particular name.  This can be expensive in the case that there is no
     * such frame because caching the result is difficult.
     *
     * The problem with this fix is that the stored result is not updated when the protege
     * ontology is imported.
     */
     /*
      * TT: The fix should be easy, but it needs to be tested. We can keep the flag from below
      * and when we import in the ImportHelper, we set this flag, if necessary.
      * We should do this fix soon.
      */

    // private Boolean protegeMetaOntologyImported = null;


    public boolean isProtegeMetaOntologyImported() {
    	//TT: this should be reimplemented in a more efficient way
    	//Should use a listener on the ontology imports and a class field

    	//TT: Nice implementation, but I don't know how efficient it is.
    	/*
        for (Iterator iter = getAllImports().iterator(); iter.hasNext();) {
			String uriImport = (String) iter.next();
			if (uriImport.equals(ProtegeNames.FILE))
				return true;
		}
    	return false;
    	*/
        String slotName = ProtegeNames.getSubclassesDisjointSlotName();
        return getSlot(slotName) != null;
    }


    public boolean isTrueInstance(Instance instance) {
        return !(instance instanceof Slot) &&
               !(instance instanceof Cls) &&
               !(instance instanceof Facet) &&
               !(instance instanceof OWLAllDifferent) &&
               !(instance instanceof RDFList) &&
               !(instance instanceof OWLOntology);
    }


    public Iterator<OWLAnonymousClass> listOWLAnonymousClasses() {
        Collection<OWLAnonymousClass> result = new ArrayList<OWLAnonymousClass>();
        for (Object o : getCls(OWLNames.Cls.ANONYMOUS_CLASS).getInstances()) {
            if (o instanceof OWLAnonymousClass) {
                result.add((OWLAnonymousClass) o);
            }
        }
        return result.iterator();
    }


    public Iterator listOWLNamedClasses() {
        Collection result = new ArrayList();
        for (Iterator it = getOWLNamedClassClass().getInstances(true).iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof OWLNamedClass) {
                result.add(o);
            }
        }
        return result.iterator();
    }


    public Iterator listRDFProperties() {
        return getRDFProperties().iterator();
    }


    public Iterator listRDFSNamedClasses() {
        Collection result = new ArrayList();
        for (Iterator it = getRDFSNamedClassClass().getInstances(true).iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSNamedClass) {
                result.add(o);
            }
        }
        return result.iterator();
    }


    public Iterator listReferences(Object object, int maxResults) {
        if (object instanceof DefaultRDFSLiteral) {
            object = ((DefaultRDFSLiteral) object).getRawValue();
        }
        Iterator refs = getReferences(object, maxResults).iterator();
        List results = new ArrayList();
        while (refs.hasNext()) {
            Reference ref = (Reference) refs.next();
            if (ref.getFrame() instanceof RDFResource && ref.getSlot() instanceof RDFProperty) {
                results.add(new DefaultTuple((RDFResource) ref.getFrame(), (RDFProperty) ref.getSlot()));
            }
        }
        return results.iterator();
    }


    public Iterator listSubjects(RDFProperty property) {
        return getHeadFrameStore().getFramesWithAnyDirectOwnSlotValue(property).iterator();
    }


    @Override
	public synchronized Instance setDirectType(Instance instance, Cls type) {
        if (instance instanceof OWLProperty && type != null) {
            deleteRestrictionsDependingOnPropertyType((OWLProperty) instance, type);
        }
        if (instance instanceof OWLNamedClass && type.equals(getRDFSNamedClassClass())) {
            deleteAnonymousSuperclasses((OWLNamedClass) instance);
        }
        return super.setDirectType(instance, type);
    }


    private void deleteAnonymousSuperclasses(OWLNamedClass namedOWLClass) {
        Collection oldNamedParents = namedOWLClass.getNamedSuperclasses();
        Iterator it = new ArrayList(namedOWLClass.getSuperclasses(false)).iterator();
        while (it.hasNext()) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof OWLAnonymousClass) {
                superCls.delete();
            }
        }
        for (Iterator sit = oldNamedParents.iterator(); sit.hasNext();) {
            RDFSClass superCls = (RDFSClass) sit.next();
            if (!namedOWLClass.isSubclassOf(superCls)) {
                namedOWLClass.addSuperclass(superCls);
            }
        }
    }


    private void deleteRestrictionsDependingOnPropertyType(OWLProperty owlProperty, Cls type) {
        Cls metaClass = null;
        if (owlProperty instanceof OWLDatatypeProperty) {
            metaClass = getOWLDatatypePropertyClass();
            //newType = ValueType.INSTANCE;
        }
        else {
            metaClass = getOWLObjectPropertyClass();
            //newType = ValueType.ANY;
        }
        if (!type.hasSuperclass(metaClass)) {
            getOWLFrameStore().deleteQuantifierRestrictions(owlProperty);
            //if (!type.equals(getRDFPropertyClass())) {
            // owlProperty.setValueType(newType);
            //}
        }
    }


    public void setLoadDefaults(boolean value) {
        loadDefaults = value;
    }


    @Override
    public void setProject(Project project) {
        super.setProject(project);

        project.setPrettyPrintSlotWidgetLabels(false);

        Slot nameSlot = getSlot(Model.Slot.NAME);

        getRootCls().setDirectBrowserSlotPattern(new OWLBrowserSlotPattern(nameSlot));

        project.setDefaultClsWidgetClassName(OWLFormWidget.class.getName());

        project.setWidgetMapper(new OWLWidgetMapper(this));

        getProtegeClassificationStatusProperty().setVisible(false);
        getProtegeInferredSuperclassesProperty().setVisible(false);
        getProtegeInferredSubclassesProperty().setVisible(false);
        getOWLOntologyClass().setVisible(false);
    }


    protected FrameListener createDefaultLanguageListener() {
        return new FrameAdapter() {
            @Override
            public void ownSlotValueChanged(FrameEvent event) {
                Slot slot = event.getSlot();
                if (slot.equals(getDefaultLanguageProperty())) {
                    defaultLanguageInitialized = false;
                    defaultLanguage = getDefaultLanguage();
                }
            }
        };
    }


    public void setSearchSynonymProperties(Collection slots) {
        if (slots.isEmpty()) {
            getOWLProject().getSettingsMap().setString(SEARCH_SYNONYMS_KEY, null);
        }
        else {
            String str = "";
            for (Iterator it = slots.iterator(); it.hasNext();) {
                Slot slot = (Slot) it.next();
                str += slot.getName();
                if (it.hasNext()) {
                    str += SEARCH_SYNONYMS_SEPARATOR;
                }
            }
            getOWLProject().getSettingsMap().setString(SEARCH_SYNONYMS_KEY, str);
        }
    }


    public void setDefaultAnnotationPropertiesInView(Collection slots) {
        if (slots.isEmpty()) {
            getOWLProject().getSettingsMap().setString(DEFAULT_ANNOT_PROP_IN_VIEW_KEY, null);
        }
        else {
            String str = "";
            for (Iterator it = slots.iterator(); it.hasNext();) {
                Slot slot = (Slot) it.next();
                str += slot.getName();
                if (it.hasNext()) {
                    str += SEARCH_SYNONYMS_SEPARATOR;
                }
            }
            getOWLProject().getSettingsMap().setString(DEFAULT_ANNOT_PROP_IN_VIEW_KEY, str);
        }
    }


    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    public RDFResource getRDFResourceByBrowserText(String text) {
        for (Object element : getInstances()) {
            Instance instance = (Instance) element;
            if (instance instanceof RDFResource && instance.getBrowserText().equals(text)) {
                return (RDFResource) getInstance(instance.getName());
            }
        }
        return null;
    }


    public RDFResource getRDFResourceByNameOrBrowserText(String text) {
        String fullName = OWLUtil.getInternalFullName(this, text);
        if (fullName == null) {
            return null;
        }
        Instance result = getInstance(fullName);
        if (result instanceof RDFResource) {
            return (RDFResource) result;
        }
        result = getRDFResourceByBrowserText(text);
        if (result instanceof RDFResource) {
            return (RDFResource) result;
        }
        else {
            return null;
        }
    }


    /*
     * This number is adjusted by the OWLModelTestCase. The magic number
     * is counting the number of frames  removed from getRDFSClasses by the
     * removeProtegeSystemResources call.
     */
    public int getRDFSClassCount() {
        return getRDFSNamedClassClass().getInstanceCount() - 3;
    }


    public Collection getRDFSClasses() {
        Collection classes = new ArrayList(getOWLClassMetaCls().getInstances());
        removeProtegeSystemResources(this, classes);
        return classes;
    }


    public RDFSDatatypeFactory getRDFSDatatypeFactory() {
        return rdfsDatatypeFactory;
    }


    private static void removeProtegeSystemResources(KnowledgeBase kb, Collection frames) {
        if (frames.size() > 0) {

            //Protege OWL - system frames
            if (kb instanceof OWLModel) {
            	OWLModel owlModel = (OWLModel) kb;
            	frames.remove(owlModel.getSystemFrames().getOwlOntologyPrefixesProperty());
            	frames.remove(owlModel.getSystemFrames().getOwlOntologyPointerClass());
            	frames.remove(owlModel.getSystemFrames().getOwlOntologyPointerProperty());
            	frames.remove(owlModel.getSystemFrames().getOwlResourceURIProperty());
            	frames.remove(owlModel.getSystemFrames().getAnonymousRootCls());
            	frames.remove(owlModel.getSystemFrames().getOwlClassMetaCls());
            }
        }
    }


    public Collection getResourcesWithPrefix(String prefix) {
        Collection result = new ArrayList();

        String namespace = getNamespaceManager().getNamespaceForPrefix(prefix);

        if (namespace == null) {
        	return result;
        }

        result = getMatchingFrames(getNameSlot(), null, false, namespace + "*", -1);

        return getRDFResources(this, result);
    }


    public List getVisibleResources(Iterator iterator) {
        List result = new ArrayList();
        while (iterator.hasNext()) {
            RDFResource resource = (RDFResource) iterator.next();
            if (resource.isVisible()) {
                result.add(resource);
            }
        }
        return result;
    }


    @Override
	public synchronized boolean isSlotMetaCls(Cls cls) {
        return getRDFPropertyClass().equals(cls) || hasSuperclass(cls, getRootSlotMetaCls());
    }


    public void resetJenaModel() {
    	if (jenaModel != null) {
    		jenaModel.close();
    	}
        jenaModel = null;
    }


    public Collection getPropertyValueLiterals(RDFResource resource, RDFProperty property) {
        final List values = new ArrayList(OWLUtil.getPropertyValues(resource, property, false));
        if (!values.isEmpty()) {
            return getValueLiterals(values);
        }
        else {
            return values;
        }
    }


    public List getValueLiterals(List values) {
        List result = new ArrayList();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSLiteral) {
                result.add(o);
            }
            else {
                result.add(createRDFSLiteral(o));
            }
        }
        return result;
    }



    @Override
	public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {

        final int valueCount = values.size();
        if (valueCount > 0) {
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object o = it.next();
                if (o instanceof RDFSLiteral) {
                    values = convertRDFSLiteralsToInternalFormat(values);
                    break;
                }
            }
        }
        super.setDirectOwnSlotValues(frame, slot, values);
    }

    @SuppressWarnings("unchecked")
    public static List convertRDFSLiteralsToInternalFormat(Collection values) {
      final List result = new LinkedList();
      for (Iterator it = values.iterator(); it.hasNext();) {
          final Object o = it.next();
          result.add(convertRDFSLiteralToInternalFormat(o));
      }
      return result;
    }

    public static Object convertRDFSLiteralToInternalFormat(Object o) {
        if (o instanceof RDFSLiteral) {
            final DefaultRDFSLiteral literal = (DefaultRDFSLiteral) o;
            final Object optimized = literal.getPlainValue();
            if (optimized != null) {
                return optimized;
            }
            else {
                return literal.getRawValue();
            }
        }
        else {
            return o;
        }
    }

    /*
     * OWLTest methods
     */

    public void addOWLTest(OWLTest test) {
        getOWLTestsSettingsMap().remove(test.getClass().getName());
    }


    public OWLTest[] getOWLTests() {
        Class[] classes = OWLTestLibrary.getOWLTestClasses();
        Collection tests = new ArrayList();
        Set disabledTestClasses = new HashSet();
        Iterator it = getOWLTestsSettingsMap().listKeys();
        while (it.hasNext()) {
            disabledTestClasses.add(it.next());
        }
        for (Class c : classes) {
            if (!disabledTestClasses.contains(c.getName())) {
                tests.add(OWLTestLibrary.getOWLTest(c));
            }
        }
        return (OWLTest[]) tests.toArray(new OWLTest[0]);
    }


    private SettingsMap getOWLTestGroupsSettingsMap() {
        return getOWLProject().getSettingsMap().getSettingsMap(TESTGROUP_LIST_NAME);
    }


    private SettingsMap getOWLTestsSettingsMap() {
        return getOWLProject().getSettingsMap().getSettingsMap(TEST_LIST_NAME);
    }


    public boolean isAutoRepairEnabled() {
        return !Boolean.FALSE.equals(getOWLProject().getSettingsMap().getBoolean(AUTO_REPAIR_ENABLED));
    }


    public boolean isOWLTestGroupEnabled(String groupName) {
        return getOWLTestGroupsSettingsMap().getBoolean(groupName) == null;
    }


    public void removeOWLTest(OWLTest test) {
        final String key = test.getClass().getName();
        getOWLTestsSettingsMap().setBoolean(key, true);
    }


    public void setAutoRepairEnabled(boolean value) {
        getOWLProject().getSettingsMap().setBoolean(AUTO_REPAIR_ENABLED, Boolean.valueOf(value));
    }


    public void setOWLTestGroupEnabled(String groupName, boolean value) {
        if (value) {
            getOWLTestGroupsSettingsMap().remove(groupName);
            Class[] classes = OWLTestLibrary.getOWLTestClasses();
            for (Class c : classes) {
                OWLTest test = OWLTestLibrary.getOWLTest(c);
                if (groupName.equals(test.getGroup())) {
                    addOWLTest(test);
                }
            }
        }
        else {
            getOWLTestGroupsSettingsMap().setBoolean(groupName, true);
            OWLTest[] tests = getOWLTests();
            for (OWLTest test : tests) {
                if (groupName.equals(test.getGroup())) {
                    removeOWLTest(test);
                }
            }
        }
    }

    /*
     * End OWLTest methods
     */




    /*
     * Client and Server related methods
     */

    @Override
    public ServerCacheStateMachine getCacheMachine() {
        return new OwlStateMachine(getHeadFrameStore(), this);
    }

    @Override
    public void setCacheMachine(ServerCacheStateMachine machine) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected void adjustForClient() {
        super.adjustForClient();
        OWLFrameStoreManager fsm = getFrameStoreManager();
        fsm.setOwlFrameStoresEnabled(false);
        tripleStoreModel=new ClientTripleStoreModel(this);
    }


    /*
     * Utilities for naming methods
     */


    public boolean isValidResourceName(String name, RDFResource resource) {
        return isValidFrameName(name, resource);
    }

    public String createUniqueNewFrameName(String baseName) {
    	return createNewResourceName(baseName);
        //return getUniqueFrameName(getName() + "_" + baseName);
    }


    public String getResourceNameForURI(String uri) {
    	return uri;
    }

    public boolean isValidOWLFrameName(String name) {
        return isValidOWLFrameName(getNamespaceManager(), name);
    }

    public static boolean isValidOWLFrameName(NamespaceManager nsm, String name) {
    	//TODO TT - add maybe extra checks; for now it should be fine
    	return URIUtilities.isValidURI(name);
    }

    public String getValidNamespaceFrameName(String suggestedName) {
        return getValidOWLFrameName(this, suggestedName);
    }

    //TODO: change this
    public static String getValidOWLFrameName(AbstractOWLModel kb, String suggestedName) {
        Assert.assertNotNull(suggestedName);
        String name = suggestedName;
        if (name.startsWith(":")) {
            name = "_" + name.substring(1);
        }
        int first = name.indexOf(':') + 1;
        for (int i = 0; i < name.length(); i++) {
            if (i != first - 1) {
                char c = name.charAt(i);
                if (!Character.isJavaIdentifierPart(c) && VALID_SYMBOLS.indexOf(c) < 0) {
                    name = name.replace(c, '_');
                }
            }
        }
        if (name.length() == first) {
            name = "_" + name;
        }
        else if (!Character.isJavaIdentifierStart(name.charAt(first))) {
            int x = first == 0 ? first : first - 1;
            name = name.substring(0, x) + "_" + name.substring(first);
        }
        if (!name.equals(suggestedName)) {
            if (kb != null && kb.getFrame(name) != null) {
                suggestedName = kb.getUniqueFrameName(name);
            }
            else {
                suggestedName = name;
            }
        }
        return suggestedName;
    }


    public String getLocalNameForURI(String uri) {
    	return NamespaceUtil.getLocalName(uri);
    }

    private ImportingOwlNamespaceManager importingNamespaceManager;

    public NamespaceManager getNamespaceManager() {
        if (importNamespaces) {
            if (importingNamespaceManager == null) {
                importingNamespaceManager = new ImportingOwlNamespaceManager(this);
            }
            return importingNamespaceManager;
        }
        else {
            importingNamespaceManager = null;
            return tripleStoreModel.getActiveTripleStore().getNamespaceManager();
        }
    }

    public String getNamespaceForURI(String uri) {
    	return NamespaceUtil.getNameSpace(uri);
    }


    public String getPrefixForResourceName(String name) {
    	return NamespaceUtil.getPrefixForResourceName(this, OWLUtil.getInternalFullName(this, name));
    }


    private int lastGen = 1;

    public String getUniqueFrameName(String name) {
        String baseName = name;
        int i = lastGen;
        do {
            name = baseName + "_" + i++;
        }
        while (getFrame(name) != null);
        lastGen = i;
        return name;
    }




    public String getURIForResourceName(String name) {
    	return OWLUtil.getInternalFullName(this, name);
    }


    /**
     * @deprecated Use {@link #getNamespaceForURI(String)}
     */
    @Deprecated
    public String getNamespaceForResourceName(String resourceName) {
       return getNamespaceForURI(resourceName);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getFrameNameForURI(String uri, boolean generatePrefix) {
    	return uri;
    }

    /**
     * @deprecated Use {@link #getLocalNameForURI(String)}
     */
    @Deprecated
    public String getLocalNameForResourceName(String frameName) {
    	return getLocalNameForURI(frameName);
    }



    /*
     * Getter methods for the OWL entities
     * These essentially come from the system frames but there is some issues with the naming.
     */


    public RDFSNamedClass getRDFUntypedResourcesClass() {
        return getRDFExternalResourceClass();
    }

    public OWLNamedClass getOWLDatatypePropertyMetaClassCls() {
        return getOWLDatatypePropertyClass();
    }

    public OWLNamedClass getOWLObjectPropertyMetaClassCls() {
        return getOWLObjectPropertyClass();
    }

    public RDFSNamedClass getOWLAllDifferentClassCls() {
        return getOWLAllDifferentClass();
    }

    public OWLNamedClass getOWLNamedClassMetaClassCls() {
        return getOWLNamedClassClass();
    }

    public OWLNamedClass getOWLOntologyCls() {
        return getOWLOntologyClass();
    }

    public RDFSNamedClass getRDFListCls() {
        return getRDFListClass();
    }

    public OWLNamedClass getRDFSClassMetaClassCls() {
        return getRDFSNamedClassClass();
    }

    public OWLNamedClass getOWLNothing() {
        return getOWLNothingClass();
    }

    public RDFSNamedClass getOWLSomeValuesFromRestrictionClass() {
        return getOWLSomeValuesFromClass();
    }

    public RDFProperty getRDFSSubPropertyOfProperty() {
        return getRDFSSubPropertyOf();
    }

    /*
     * Getters retrieved from system frames.  Note that the case is slightly
     * different
     *    Owl -> OWL
     *    Rdf -> RDF
     *    Rdfs -> RDFS
     */

    /**
     * @return the owlThingClass
     */
    public OWLNamedClass getOWLThingClass() {
        return getSystemFrames().getOwlThingClass();
    }

    /**
     * @return the owlClassMetaCls
     */
    public RDFSNamedClass getOWLClassMetaCls() {
        return getSystemFrames().getOwlClassMetaCls();
    }

    /**
     * @return the rdfsNamedClassClass
     */
    public OWLNamedClass getRDFSNamedClassClass() {
        return getSystemFrames().getRdfsNamedClassClass();
    }

    /**
     * @return the owlNamedClassClass
     */
    public OWLNamedClass getOWLNamedClassClass() {
        return getSystemFrames().getOwlNamedClassClass();
    }

    /**
     * @return the owlDeprecatedClassClass
     */
    public RDFSNamedClass getOWLDeprecatedClassClass() {
        return getSystemFrames().getOwlDeprecatedClassClass();
    }

    /**
     * @return the anonymousClassMetaCls
     */
    public OWLNamedClass getAnonymousClassMetaCls() {
        return getSystemFrames().getAnonymousClassMetaCls();
    }

    /**
     * @return the owlEnumeratedClassClass
     */
    public RDFSNamedClass getOWLEnumeratedClassClass() {
        return getSystemFrames().getOwlEnumeratedClassClass();
    }

    /**
     * @return the owlRestrictionClass
     */
    public RDFSNamedClass getOWLRestrictionClass() {
        return getSystemFrames().getOwlRestrictionClass();
    }

    /**
     * @return the owlAllValuesFromClass
     */
    public RDFSNamedClass getOWLAllValuesFromClass() {
        return getSystemFrames().getOwlAllValuesFromClass();
    }

    /**
     * @return the owlHasValueClass
     */
    public RDFSNamedClass getOWLHasValueClass() {
        return getSystemFrames().getOwlHasValueClass();
    }

    /**
     * @return the owlMaxCardinalityClass
     */
    public RDFSNamedClass getOWLMaxCardinalityClass() {
        return getSystemFrames().getOwlMaxCardinalityClass();
    }

    /**
     * @return the owlMinCardinalityClass
     */
    public RDFSNamedClass getOWLMinCardinalityClass() {
        return getSystemFrames().getOwlMinCardinalityClass();
    }

    /**
     * @return the owlCardinalityClass
     */
    public RDFSNamedClass getOWLCardinalityClass() {
        return getSystemFrames().getOwlCardinalityClass();
    }

    /**
     * @return the owlSomeValuesFromClass
     */
    public RDFSNamedClass getOWLSomeValuesFromClass() {
        return getSystemFrames().getOwlSomeValuesFromClass();
    }

    /**
     * @return the owlLogicalClassClass
     */
    public RDFSNamedClass getOWLLogicalClassClass() {
        return getSystemFrames().getOwlLogicalClassClass();
    }

    /**
     * @return the owlComplementClassClass
     */
    public RDFSNamedClass getOWLComplementClassClass() {
        return getSystemFrames().getOwlComplementClassClass();
    }

    /**
     * @return the owlIntersectionClassClass
     */
    public RDFSNamedClass getOWLIntersectionClassClass() {
        return getSystemFrames().getOwlIntersectionClassClass();
    }

    /**
     * @return the owlUnionClassClass
     */
    public RDFSNamedClass getOWLUnionClassClass() {
        return getSystemFrames().getOwlUnionClassClass();
    }

    /**
     * @return the rdfPropertyClass
     */
    public OWLNamedClass getRDFPropertyClass() {
        return getSystemFrames().getRdfPropertyClass();
    }

    /**
     * @return the owlDatatypePropertyClass
     */
    public OWLNamedClass getOWLDatatypePropertyClass() {
        return getSystemFrames().getOwlDatatypePropertyClass();
    }

    /**
     * @return the owlObjectPropertyClass
     */
    public OWLNamedClass getOWLObjectPropertyClass() {
        return getSystemFrames().getOwlObjectPropertyClass();
    }

    /**
     * @return the owlInverseFunctionalPropertyClass
     */
    public OWLNamedClass getOWLInverseFunctionalPropertyClass() {
        return getSystemFrames().getOwlInverseFunctionalPropertyClass();
    }

    /**
     * @return the owlSymmetricPropertyClass
     */
    public OWLNamedClass getOWLSymmetricPropertyClass() {
        return getSystemFrames().getOwlSymmetricPropertyClass();
    }

    /**
     * @return the owlTransitivePropertyClass
     */
    public OWLNamedClass getOWLTransitivePropertyClass() {
        return getSystemFrames().getOwlTransitivePropertyClass();
    }

    /**
     * @return the owlAnnotationPropertyClass
     */
    public OWLNamedClass getOWLAnnotationPropertyClass() {
        return getSystemFrames().getOwlAnnotationPropertyClass();
    }

    /**
     * @return the owlFunctionalPropertyClass
     */
    public OWLNamedClass getOWLFunctionalPropertyClass() {
        return getSystemFrames().getOwlFunctionalPropertyClass();
    }

    /**
     * @return the owlDeprecatedPropertyClass
     */
    public RDFSNamedClass getOWLDeprecatedPropertyClass() {
        return getSystemFrames().getOwlDeprecatedPropertyClass();
    }

    /**
     * @return the rdfsDatatypeClass
     */
    public RDFSNamedClass getRDFSDatatypeClass() {
        return getSystemFrames().getRdfsDatatypeClass();
    }

    /**
     * @return the owlOntologyClass
     */
    public OWLNamedClass getOWLOntologyClass() {
        return getSystemFrames().getOwlOntologyClass();
    }

    /**
     * @return the owlNothingClass
     */
    public OWLNamedClass getOWLNothingClass() {
        return getSystemFrames().getOwlNothingClass();
    }

    /**
     * @return the rdfListClass
     */
    public RDFSNamedClass getRDFListClass() {
        return getSystemFrames().getRdfListClass();
    }

    /**
     * @return the owlAllDifferentClass
     */
    public RDFSNamedClass getOWLAllDifferentClass() {
        return getSystemFrames().getOwlAllDifferentClass();
    }
    
    /**
     * @return the rdfsLiteralClass
     * @deprecated This is based on a wrong premise - there is no such class.  Using this method can cause problems.
     */
    public RDFSNamedClass getRDFSLiteralClass() {
        return getSystemFrames().getRdfsLiteralClass();
    }


    /**
     * @return the rdfsContainerClass
     */
    public RDFSNamedClass getRDFSContainerClass() {
        return getSystemFrames().getRdfsContainerClass();
    }

    /**
     * @return the rdfAltClass
     */
    public RDFSNamedClass getRDFAltClass() {
        return getSystemFrames().getRdfAltClass();
    }

    /**
     * @return the rdfBagClass
     */
    public RDFSNamedClass getRDFBagClass() {
        return getSystemFrames().getRdfBagClass();
    }

    /**
     * @return the rdfSeqClass
     */
    public RDFSNamedClass getRDFSeqClass() {
        return getSystemFrames().getRdfSeqClass();
    }

    /**
     * @return the rdfStatementClass
     */
    public RDFSNamedClass getRDFStatementClass() {
        return getSystemFrames().getRdfStatementClass();
    }

    /**
     * @return the owlDataRangeClass
     */
    public RDFSNamedClass getOWLDataRangeClass() {
        return getSystemFrames().getOwlDataRangeClass();
    }

    /**
     * @return the anonymousRootCls
     */
    public RDFSNamedClass getAnonymousRootCls() {
        return getSystemFrames().getAnonymousRootCls();
    }

    /**
     * @return the rdfExternalResourceClass
     */
    public RDFSNamedClass getRDFExternalResourceClass() {
        return getSystemFrames().getRdfExternalResourceClass();
    }

    /**
     * @return the rdfExternalClassClass
     */
    public RDFSNamedClass getRDFExternalClassClass(){
    	return getSystemFrames().getRdfExternalClassClass();
    }

    /**
     * @return the rdfExternalPropertyClass
     */
    public RDFSNamedClass getRDFExternalPropertyClass(){
    	return getSystemFrames().getRdfExternalPropertyClass();
    }


    /**
     * @return the topOWLOntologyClass
     */
    public RDFSNamedClass getTopOWLOntologyClass() {
        return getSystemFrames().getOwlOntologyPointerClass();
    }

    /**
     * @return the owlAllValuesFromProperty
     */
    public RDFProperty getOWLAllValuesFromProperty() {
        return getSystemFrames().getOwlAllValuesFromProperty();
    }

    /**
     * @return the owlBackwardCompatibleWithProperty
     */
    public RDFProperty getOWLBackwardCompatibleWithProperty() {
        return getSystemFrames().getOwlBackwardCompatibleWithProperty();
    }

    /**
     * @return the owlCardinalityProperty
     */
    public RDFProperty getOWLCardinalityProperty() {
        return getSystemFrames().getOwlCardinalityProperty();
    }

    /**
     * @return the owlComplementOfProperty
     */
    public RDFProperty getOWLComplementOfProperty() {
        return getSystemFrames().getOwlComplementOfProperty();
    }

    /**
     * @return the owlDifferentFromProperty
     */
    public RDFProperty getOWLDifferentFromProperty() {
        return getSystemFrames().getOwlDifferentFromProperty();
    }

    /**
     * @return the owlDisjointWithProperty
     */
    public RDFProperty getOWLDisjointWithProperty() {
        return getSystemFrames().getOwlDisjointWithProperty();
    }

    /**
     * @return the owlDistinctMembersProperty
     */
    public RDFProperty getOWLDistinctMembersProperty() {
        return getSystemFrames().getOwlDistinctMembersProperty();
    }

    /**
     * @return the owlEquivalentClassProperty
     */
    public RDFProperty getOWLEquivalentClassProperty() {
        return getSystemFrames().getOwlEquivalentClassProperty();
    }

    /**
     * @return the owlEquivalentPropertyProperty
     */
    public RDFProperty getOWLEquivalentPropertyProperty() {
        return getSystemFrames().getOwlEquivalentPropertyProperty();
    }

    /**
     * @return the owlHasValueProperty
     */
    public RDFProperty getOWLHasValueProperty() {
        return getSystemFrames().getOwlHasValueProperty();
    }

    /**
     * @return the owlImportsProperty
     */
    public RDFProperty getOWLImportsProperty() {
        return getSystemFrames().getOwlImportsProperty();
    }

    /**
     * @return the owlIncompatibleWithProperty
     */
    public RDFProperty getOWLIncompatibleWithProperty() {
        return getSystemFrames().getOwlIncompatibleWithProperty();
    }

    /**
     * @return the owlIntersectionOfProperty
     */
    public RDFProperty getOWLIntersectionOfProperty() {
        return getSystemFrames().getOwlIntersectionOfProperty();
    }

    /**
     * @return the owlInverseOfProperty
     */
    public RDFProperty getOWLInverseOfProperty() {
        return getSystemFrames().getOwlInverseOfProperty();
    }

    /**
     * @return the owlMaxCardinalityProperty
     */
    public RDFProperty getOWLMaxCardinalityProperty() {
        return getSystemFrames().getOwlMaxCardinalityProperty();
    }

    /**
     * @return the owlMinCardinalityProperty
     */
    public RDFProperty getOWLMinCardinalityProperty() {
        return getSystemFrames().getOwlMinCardinalityProperty();
    }

    /**
     * @return the owlOneOfProperty
     */
    public RDFProperty getOWLOneOfProperty() {
        return getSystemFrames().getOwlOneOfProperty();
    }

    /**
     * @return the owlOnPropertyProperty
     */
    public RDFProperty getOWLOnPropertyProperty() {
        return getSystemFrames().getOwlOnPropertyProperty();
    }

    /**
     * @return the owlPriorVersionProperty
     */
    public RDFProperty getOWLPriorVersionProperty() {
        return getSystemFrames().getOwlPriorVersionProperty();
    }

    /**
     * @return the owlSameAsProperty
     */
    public RDFProperty getOWLSameAsProperty() {
        return getSystemFrames().getOwlSameAsProperty();
    }

    /**
     * @return the owlSomeValuesFromProperty
     */
    public RDFProperty getOWLSomeValuesFromProperty() {
        return getSystemFrames().getOwlSomeValuesFromProperty();
    }

    /**
     * @return the owlUnionOfProperty
     */
    public RDFProperty getOWLUnionOfProperty() {
        return getSystemFrames().getOwlUnionOfProperty();
    }

    /**
     * @return the owlValuesFromProperty
     */
    public RDFProperty getOWLValuesFromProperty() {
        return getSystemFrames().getOwlValuesFromProperty();
    }

    /**
     * @return the owlVersionInfoProperty
     */
    public RDFProperty getOWLVersionInfoProperty() {
        return getSystemFrames().getOwlVersionInfoProperty();
    }

    /**
     * @return the protegeClassificationStatusProperty
     */
    public RDFProperty getProtegeClassificationStatusProperty() {
        return getSystemFrames().getProtegeClassificationStatusProperty();
    }

    /**
     * @return the protegeInferredSubclassesProperty
     */
    public RDFProperty getProtegeInferredSubclassesProperty() {
        return getSystemFrames().getProtegeInferredSubclassesProperty();
    }

    /**
     * @return the protegeInferredSuperclassesProperty
     */
    public RDFProperty getProtegeInferredSuperclassesProperty() {
        return getSystemFrames().getProtegeInferredSuperclassesProperty();
    }

    /**
     * @return the protegeInferredTypeProperty
     */
    public RDFProperty getProtegeInferredTypeProperty() {
        return getSystemFrames().getProtegeInferredTypeProperty();
    }

    /**
     * @return the rdfFirstProperty
     */
    public RDFProperty getRDFFirstProperty() {
        return getSystemFrames().getRdfFirstProperty();
    }

    /**
     * @return the rdfObjectProperty
     */
    public RDFProperty getRDFObjectProperty() {
        return getSystemFrames().getRdfObjectProperty();
    }

    /**
     * @return the rdfPredicateProperty
     */
    public RDFProperty getRDFPredicateProperty() {
        return getSystemFrames().getRdfPredicateProperty();
    }

    /**
     * @return the rdfRestSlot
     */
    public RDFProperty getRDFRestProperty() {
        return getSystemFrames().getRdfRestProperty();
    }

    /**
     * @return the rdfSubjectProperty
     */
    public RDFProperty getRDFSubjectProperty() {
        return getSystemFrames().getRdfSubjectProperty();
    }

    /**
     * @return the rdfTypeProperty
     */
    public RDFProperty getRDFTypeProperty() {
        return getSystemFrames().getRdfTypeProperty();
    }

    /**
     * @return the rdfValueProperty
     */
    public RDFProperty getRDFValueProperty() {
        return getSystemFrames().getRdfValueProperty();
    }

    /**
     * @return the rdfsCommentProperty
     */
    public OWLDatatypeProperty getRDFSCommentProperty() {
        return getSystemFrames().getRdfsCommentProperty();
    }

    /**
     * @return the rdfsDomainProperty
     */
    public RDFProperty getRDFSDomainProperty() {
        return getSystemFrames().getRdfsDomainProperty();
    }

    /**
     * @return the rdfsIsDefinedByProperty
     */
    public RDFProperty getRDFSIsDefinedByProperty() {
        return getSystemFrames().getRdfsIsDefinedByProperty();
    }

    /**
     * @return the rdfsLabelProperty
     */
    public RDFProperty getRDFSLabelProperty() {
        return getSystemFrames().getRdfsLabelProperty();
    }

    /**
     * @return the rdfsMemberProperty
     */
    public RDFProperty getRDFSMemberProperty() {
        return getSystemFrames().getRdfsMemberProperty();
    }

    /**
     * @return the rdfsRangeProperty
     */
    public RDFProperty getRDFSRangeProperty() {
        return getSystemFrames().getRdfsRangeProperty();
    }

    /**
     * @return the rdfsSeeAlsoProperty
     */
    public RDFProperty getRDFSSeeAlsoProperty() {
        return getSystemFrames().getRdfsSeeAlsoProperty();
    }

    /**
     * @return the rdfsSubClassOfProperty
     */
    public RDFProperty getRDFSSubClassOfProperty() {
        return getSystemFrames().getRdfsSubClassOfProperty();
    }

    /**
     * @return the rdfsSubPropertyOf
     */
    public RDFProperty getRDFSSubPropertyOf() {
        return getSystemFrames().getRdfsSubPropertyOf();
    }

    /**
     * @return the owlOntologyPrefixesProperty
     */
    public RDFProperty getOWLOntologyPrefixesProperty() {
        return getSystemFrames().getOwlOntologyPrefixesProperty();
    }

    /**
     * @return the owlResourceURIProperty
     */
    public RDFProperty getOWLResourceURIProperty() {
        return getSystemFrames().getOwlResourceURIProperty();
    }

    /**
     * @return the topOWLOntologyURISlot
     */
    public RDFProperty getTopOWLOntologyURISlot() {
        return getSystemFrames().getOwlOntologyPointerProperty();
    }

    /**
     * @return the rdfNilIndividual
     */
    public RDFList getRDFNil() {
        return getSystemFrames().getRdfNil();
    }

    /**
     * @return the rdfDatatypes
     */
    public Set<RDFSDatatype> getRDFDatatypes() {
        return getSystemFrames().getRdfDatatypes();
    }

    /**
     * @return the floatDatatypes
     */
    public Set<RDFSDatatype> getFloatDatatypes() {
        return getSystemFrames().getFloatDatatypes();
    }

    /**
     * @return the integerDatatypes
     */
    public Set<RDFSDatatype> getIntegerDatatypes() {
        return getSystemFrames().getIntegerDatatypes();
    }

    /*
     * Getters for the important datatypes.  We don't need all 43...
     */
    public RDFSDatatype getXSDboolean() {
        return getSystemFrames().getXsdBoolean();
    }

    public RDFSDatatype getXSDdouble() {
        return getSystemFrames().getXsdDouble();
    }

    public RDFSDatatype getXSDfloat() {
        return getSystemFrames().getXsdFloat();
    }

    public RDFSDatatype getXSDlong() {
        return getSystemFrames().getXsdLong();
    }

    public RDFSDatatype getXSDint() {
        return getSystemFrames().getXsdInt();
    }

    public RDFSDatatype getXSDNonNegativeInteger() {
        return getSystemFrames().getXsdNonNegativeInteger();
    }

    public RDFSDatatype getXSDshort() {
        return getSystemFrames().getXsdShort();
    }

    public RDFSDatatype getXSDbyte() {
        return getSystemFrames().getXsdByte();
    }

    public RDFSDatatype getXSDstring() {
        return getSystemFrames().getXsdString();
    }

    public RDFSDatatype getXSDbase64Binary() {
        return getSystemFrames().getXsdBase64Binary();
    }

    public RDFSDatatype getXSDdate() {
        return getSystemFrames().getXsdDate();
    }

    public RDFSDatatype getXSDtime() {
        return getSystemFrames().getXsdTime();
    }

    public RDFSDatatype getXSDdateTime() {
        return getSystemFrames().getXsdDateTime();
    }

    public RDFSDatatype getXSDduration() {
        return getSystemFrames().getXsdDuration();
    }

    public RDFSDatatype getXSDanyURI() {
        return getSystemFrames().getXsdAnyURI();
    }

    public RDFSDatatype getXSDdecimal() {
        return getSystemFrames().getXsdDecimal();
    }

    public RDFSDatatype getXSDinteger() {
        return getSystemFrames().getXsdInteger();
    }

    public RDFSDatatype getRDFXMLLiteralType() {
        return getSystemFrames().getXmlLiteralType();
    }

    /*
     * End getter methods for OWL entities
     */

	public boolean isExpandShortNameInMethods() {
		return expandShortNameInMethods;
	}

	public void setExpandShortNameInMethods(boolean expandShortNameInMethods) {
		this.expandShortNameInMethods = expandShortNameInMethods;
	}


    public RDFProperty getProtegeSubclassesDisjointProperty() {
        //TT: is it safe to cache this value? What if an import is added/removed?
        if (protegeSubclassesDisjointProperty == null) {
            protegeSubclassesDisjointProperty = getSlot(ProtegeNames.getSubclassesDisjointSlotName());
        }
        return (RDFProperty) protegeSubclassesDisjointProperty;
    }

    public WriterSettings getWriterSettings() {
        String value = getOWLProject().getSettingsMap().getString(JenaOWLModel.WRITER_SETTINGS_PROPERTY);
        if (JenaOWLModel.WRITER_PROTEGE.equals(value)) {
            return new ProtegeWriterSettings(this);
        }
        else {
            return new JenaWriterSettings(this);
        }
    }

    public void setWriterSettings(WriterSettings writerSettings) {
        if (writerSettings instanceof ProtegeWriterSettings) {
            getOWLProject().getSettingsMap().setString(JenaOWLModel.WRITER_SETTINGS_PROPERTY, JenaOWLModel.WRITER_PROTEGE);
        }
        else {
            getOWLProject().getSettingsMap().remove(JenaOWLModel.WRITER_SETTINGS_PROPERTY);
        }
    }

    @Override
    public synchronized void dispose() {
        detachDefaultLanguageListener();

    	super.dispose();

    	if (jenaModel != null) {
    		jenaModel.close();
    		jenaModel = null;
    	}
    	owlProject = null;

    	if (globalParserCache != null) {
    		globalParserCache.dispose();
    		globalParserCache = null;
    	}

    	OWLImportsCache.dispose();

    	if (tripleStoreModel != null) {
    		tripleStoreModel.dispose();
    		tripleStoreModel = null;
    	}

    	ReasonerManager.getInstance().disposeReasoner(this);

    	repositoryManager = null;
    	taskManager = null;
    }

}