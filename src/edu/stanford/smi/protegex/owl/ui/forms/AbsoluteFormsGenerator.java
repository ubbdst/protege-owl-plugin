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

package edu.stanford.smi.protegex.owl.ui.forms;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;

/**
 * A utility class that creates a Jena Model in the absolute forms ontology
 * from the forms represented in a Protege project.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AbsoluteFormsGenerator {

    private final static Set<String> ignoreSystemClasses = new HashSet<String>();

    static {
        ignoreSystemClasses.add(OWLNames.Cls.ALL_DIFFERENT);
        ignoreSystemClasses.add(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION);
        ignoreSystemClasses.add(OWLNames.Cls.ANNOTATION_PROPERTY);
        ignoreSystemClasses.add(OWLNames.Cls.ANONYMOUS_ROOT);
        ignoreSystemClasses.add(OWLNames.Cls.CARDINALITY_RESTRICTION);
        ignoreSystemClasses.add(OWLNames.Cls.COMPLEMENT_CLASS);
        ignoreSystemClasses.add(OWLNames.Cls.DATA_RANGE);
        ignoreSystemClasses.add(OWLNames.Cls.DEPRECATED_CLASS);
        ignoreSystemClasses.add(OWLNames.Cls.DEPRECATED_PROPERTY);
        ignoreSystemClasses.add(OWLNames.Cls.ENUMERATED_CLASS);
        ignoreSystemClasses.add(OWLNames.Cls.FUNCTIONAL_PROPERTY);
        ignoreSystemClasses.add(OWLNames.Cls.HAS_VALUE_RESTRICTION);
        ignoreSystemClasses.add(OWLNames.Cls.INTERSECTION_CLASS);
        ignoreSystemClasses.add(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY);
        ignoreSystemClasses.add(OWLNames.Cls.LOGICAL_CLASS);
        ignoreSystemClasses.add(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION);
        ignoreSystemClasses.add(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION);
        ignoreSystemClasses.add(OWLNames.Cls.NOTHING);
        ignoreSystemClasses.add(OWLNames.Cls.RESTRICTION);
        ignoreSystemClasses.add(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
        ignoreSystemClasses.add(OWLNames.Cls.SYMMETRIC_PROPERTY);
        ignoreSystemClasses.add(OWLNames.Cls.TRANSITIVE_PROPERTY);
        ignoreSystemClasses.add(OWLNames.Cls.UNION_CLASS);
        ignoreSystemClasses.add(RDFNames.Cls.ALT);
        ignoreSystemClasses.add(RDFNames.Cls.BAG);
        ignoreSystemClasses.add(RDFNames.Cls.DESCRIPTION);
        ignoreSystemClasses.add(RDFNames.Cls.LIST);
        ignoreSystemClasses.add(RDFNames.Cls.SEQ);
        ignoreSystemClasses.add(RDFNames.Cls.STATEMENT);
        ignoreSystemClasses.add(RDFSNames.Cls.CONTAINER);
        ignoreSystemClasses.add(RDFSNames.Cls.LITERAL);
    }

    public static boolean optional = true;

    public final static String SAVE_FORMS_KEY = AbsoluteFormsGenerator.class.getName() + ".saveForms";

    public final static String ALL = "all";

    public final static String MODIFIED = "modified";

    private OWLModel owlModel;

    private Project project;

    private WidgetClassMapper widgetClassMapper = new DefaultWidgetClassMapper();

    public static final String SUFFIX = ".forms";

    public final static String FILE_NAME = "protege-system.forms";

    public static final String PROTEGE_SYSTEM_FORMS_URI = "http://www.owl-ontologies.com/forms/" + FILE_NAME;


    public AbsoluteFormsGenerator(OWLModel owlModel) {
        this.owlModel = owlModel;
        this.project = owlModel.getProject();
    }


    private void createFormWidget(Model model, RDFSNamedClass cls, boolean all) {
        if (all || project.hasCustomizedDescriptor(cls)) {
            ClsWidget widget = project.getDesignTimeClsWidget(cls);
            if (widget != null && !cls.getName().startsWith(ProtegeNames.PREFIX)) {
                createFormWidget(model, cls, widget);
            }
        }
    }


    private void createFormWidget(Model model, RDFSNamedClass cls, ClsWidget clsWidget) {
        String baseURI = model.getNsPrefixURI("");
        String formName = cls.getName().replace(':', '_');
        Resource formWidget = model.createResource(baseURI + formName, FormsNames.FormWidget);
        formWidget.addProperty(FormsNames.forClass, model.createResource(cls.getURI()));
        Iterator it = ((KnowledgeBase) owlModel).getSlots().iterator();
        while (it.hasNext()) {
            Slot slot = (Slot) it.next();
            SlotWidget slotWidget = clsWidget.getSlotWidget(slot);
                       
            if (slotWidget != null) {
                String widgetClassName = slotWidget.getDescriptor().getWidgetClassName();
                String typeURI = widgetClassMapper.getWidgetClassURI(widgetClassName);
                Resource type = FormsNames.Widget;
                if (typeURI != null) {
                    type = model.createResource(typeURI);
                }
                Resource widget = model.createResource(null, type);
                formWidget.addProperty(FormsNames.widgets, widget);
                if (slot instanceof RDFProperty) {
                    RDFProperty property = (RDFProperty) slot;
                    String prefix = property.getNamespace();
                    if (prefix != null) {
                        widget.addProperty(FormsNames.forProperty, model.createResource(property.getURI()));
                    }
                }
                else {
                    widget.addProperty(RDFS.comment, slot.getName());
                }
                if (type.equals(FormsNames.Widget)) {
                    widget.addProperty(ProtegeFormsNames.javaClassName, widgetClassName);
                }
                
                OWLWidgetPropertyListUtil.createOWLPropertyList(owlModel, widget, slotWidget.getDescriptor().getPropertyList());
                
                Resource layoutData = model.createResource(null, AbsoluteLayoutNames.AbsoluteLayoutData);
                widget.addProperty(FormsNames.layoutData, layoutData);
                Rectangle bounds = slotWidget.getDescriptor().getBounds();
                layoutData.addProperty(AbsoluteLayoutNames.x, model.createTypedLiteral((int) bounds.getX()));
                layoutData.addProperty(AbsoluteLayoutNames.y, model.createTypedLiteral((int) bounds.getY()));
                layoutData.addProperty(AbsoluteLayoutNames.width, model.createTypedLiteral((int) bounds.getWidth()));
                layoutData.addProperty(AbsoluteLayoutNames.height, model.createTypedLiteral((int) bounds.getHeight()));
            }
        }
    }


    /**
     * Creates a Jena Model that can be saved etc.
     *
     * @return the Jena Model for the current Project
     */
    public Model createModel(TripleStore ts, boolean all) {
        Model model = ModelFactory.createDefaultModel();
        String baseURI = getFormsFileURI(ts);
        model.setNsPrefix("", baseURI + "#");
        model.setNsPrefix("owl", OWL.NS);
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("forms", FormsNames.NS);
        model.setNsPrefix("absolute", AbsoluteLayoutNames.NS);
        model.setNsPrefix("pforms", ProtegeFormsNames.NS);
        Resource ontology = model.createResource(baseURI);
        ontology.addProperty(RDF.type, OWL.Ontology);
        String pns = ProtegeFormsNames.NS;
        String ans = AbsoluteLayoutNames.NS;
        ontology.addProperty(OWL.imports, model.createResource(pns.substring(0, pns.length() - 1)));
        ontology.addProperty(OWL.imports, model.createResource(ans.substring(0, ans.length() - 1)));
        if (ts == owlModel.getTripleStoreModel().getSystemTripleStore()) {
            Iterator it = ((KnowledgeBase)owlModel).getClses().iterator();
            while(it.hasNext()) {
                Cls cls = (Cls) it.next();
                if(cls.isSystem() && cls instanceof RDFSNamedClass &&
                        !AbsoluteFormsGenerator.ignoreSystemClasses.contains(cls.getName())) {
                    RDFSNamedClass c = (RDFSNamedClass) cls;
                    createFormWidget(model, c, all);
                }
            }
        }
        else {
            Iterator it = ts.listHomeResources();
            while (it.hasNext()) {
                RDFResource resource = (RDFResource) it.next();
                if (resource instanceof RDFSNamedClass) {
                    RDFSNamedClass cls = (RDFSNamedClass) resource;
                    createFormWidget(model, cls, all);
                }
            }
        }
        return model;
    }


    private String getFormsFileURI(TripleStore ts) {
        return getFormsFileURI(ts, owlModel);
    }


    public static String getFormsFileURI(TripleStore ts, OWLModel owlModel) {
        String baseURI = ts.getName();
        if (ts == owlModel.getTripleStoreModel().getTopTripleStore()) {
            baseURI = owlModel.getDefaultOWLOntology().getURI();
        }
        else if(ts == owlModel.getTripleStoreModel().getSystemTripleStore()) {
            return PROTEGE_SYSTEM_FORMS_URI;
        }
        if (baseURI.endsWith("#")) {
            baseURI = baseURI.substring(0, baseURI.length() - 1);
        }
        baseURI += SUFFIX;
        return baseURI;
    }


    public void generateFiles(String option) throws Exception {
    	OutputStream os = null;
    	
        Iterator it = owlModel.getTripleStoreModel().listUserTripleStores();
        while (it.hasNext()) {
            TripleStore ts = (TripleStore) it.next();
            String formsFileName = getFormsFileURI(ts);
            URI ontologyFileURI = new URI(ts.getName());
            URI formsFileURI = new URI(formsFileName);
            Repository rep = owlModel.getRepositoryManager().getRepository(ontologyFileURI);

            if (rep != null) {
                if (rep.isWritable(ontologyFileURI) && rep.contains(formsFileURI)) {
                    Log.getLogger().info("Saving .forms model " + formsFileURI + " to " + rep.getOntologyLocationDescription(formsFileURI));
                    os = rep.getOutputStream(formsFileURI);             
                }
            }
            
            if (os == null && owlModel instanceof JenaOWLModel && ts == owlModel.getTripleStoreModel().getTopTripleStore()) {
                String path = ((JenaOWLModel) owlModel).getOWLFilePath() + SUFFIX;
            	URI ontologyURI = OWLUtil.getOWLFileURI(owlModel);
            	           	
            	if (ontologyURI != null)
            		path = ontologyURI.getPath() + AbsoluteFormsGenerator.SUFFIX;
            	
                File file = new File(path);
                Log.getLogger().info("Saving forms for model " + formsFileURI + " to " + file);
                os = new FileOutputStream(file);
                
            } 
            
            //write file
            if (os != null) {
	            Model model = createModel(ts, ALL.equals(option));
	            String language = FileUtils.langXMLAbbrev;
	            PrintStream ps = new PrintStream(os);
	            String namespace = model.getNsPrefixURI("");
	            RDFWriter writer = model.getWriter(language);
	            Jena.prepareWriter(writer, language, namespace);
	            writer.write(model, ps, namespace);
	            os.close();
            }
            
        }

        // saveSystemForms(option);
    }


    private void saveSystemForms(String option) throws IOException {
        File file = new File(FILE_NAME);
        Log.getLogger().info("Saving system forms model to " + file);
        OutputStream os = new FileOutputStream(file);
        TripleStore ts = owlModel.getTripleStoreModel().getSystemTripleStore();
        Model model = createModel(ts, ALL.equals(option));
        String language = FileUtils.langXMLAbbrev;
        PrintStream ps = new PrintStream(os);
        String namespace = PROTEGE_SYSTEM_FORMS_URI + "#";
        RDFWriter writer = model.getWriter(language);
        Jena.prepareWriter(writer, language, namespace);
        writer.write(model, ps, namespace);
        os.close();
    }
}
