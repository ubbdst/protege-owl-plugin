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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.AbstractStreamBasedRepositoryImpl;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;

/**
 * An object capable of loading forms files into a Protege Project.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AbsoluteFormsLoader {

    private OWLModel owlModel;

    private Project project;


    public AbsoluteFormsLoader(OWLModel owlModel) {
        this.owlModel = owlModel;
        this.project = owlModel.getProject();
    }


    public void load(URL fileURL) throws IOException {
        InputStream is = fileURL.openStream();
        load(is);
    }


    public void load(InputStream is) throws IOException {
        OntModel model = ModelFactory.createOntologyModel();
        
        //tell Jena where the local form ontologies are
               
        String protegeOWLDir = null;
        if (ProtegeOWL.getPluginFolder().exists()) {        	
        	try {
				//protegeOWLDir = (new URI(ProtegeOWL.getPluginFolder().getAbsolutePath())).toString();
        		protegeOWLDir = ProtegeOWL.getPluginFolder().getAbsoluteFile().toURI().toString();
			} catch (Exception e) {
				Log.getLogger().warning("Error at retrieving Protege OWL plugin folder. Expected to be at: " + 
						ProtegeOWL.getPluginFolder().getAbsoluteFile());
			}
        }
        
        if (protegeOWLDir != null) {
        	OntDocumentManager docManager = model.getDocumentManager();
        
        	docManager.addAltEntry(ProtegeFormsNames.PROTEGE_FORMS_ONTOLOGY, protegeOWLDir + File.separator +  ProtegeFormsNames.PROTEGE_FORMS_FILENAME);
        	docManager.addAltEntry(ProtegeFormsNames.ABSOLUTE_FORMS_ONTOLOGY, protegeOWLDir + File.separator + ProtegeFormsNames.ABSOLUTE_FORMS_FILENAME);
        	docManager.addAltEntry(ProtegeFormsNames.FORMS_ONTOLOGY, protegeOWLDir + File.separator + ProtegeFormsNames.FORMS_FILENAME);
        }
        
        model.read(is, "http://dummy.de/aldi.owl", FileUtils.langXMLAbbrev);
        is.close();

        model.add(FormsNames.FormWidget, RDF.type, OWL.Class);
        OntClass formWidgetClass = model.getOntClass(FormsNames.FormWidget.getURI());

        Iterator formWidgets = formWidgetClass.listInstances();
        while (formWidgets.hasNext()) {
            Resource formWidget = (Resource) formWidgets.next();            
            addFormWidget(formWidget);
        }
        project.clearCachedWidgets();
    }


    public void loadAll() throws Exception {

		File systemFormsFile = new File(ProtegeOWL.getPluginFolder(), AbsoluteFormsGenerator.FILE_NAME);
		if (systemFormsFile.exists()) {
			Log.getLogger().info("Loading system forms from " + systemFormsFile);
			load(new FileInputStream(systemFormsFile));
		}
		

		ArrayList loadedForms = new ArrayList<String>();
		loadRecursive(owlModel.getDefaultOWLOntology(), loadedForms);
		
		//load form for top ontology
		if (!loadedForms.contains(owlModel.getDefaultOWLOntology().getURI()))
			loadForm(owlModel.getTripleStoreModel().getTopTripleStore());
	}


    private void loadRecursive(OWLOntology ont, Collection visited) {
    	visited.add(ont.getURI());
    	
    	Collection imports = ont.getImportResources();    	
    	
    	for (Iterator iter = imports.iterator(); iter.hasNext();) {
			Object importOnt = iter.next();
			
			if (importOnt instanceof OWLOntology) {
				if (!visited.contains(((OWLOntology)importOnt).getURI()))
					loadRecursive((OWLOntology) importOnt, visited);
			} else {
				Log.getLogger().warning("Imported ontology not found: " + importOnt);
			}			
		}
    	
    	try {
			
    		TripleStore ts = owlModel.getTripleStoreModel().getTripleStore(ont.getURI());			
			loadForm(ts);
			
		} catch (Exception ex) {
			Log.getLogger().warning("Absolute forms not found.");
		}
    	
    }


    private void addFormWidget(Resource formWidget) {
        Statement forClassStmt = formWidget.getProperty(FormsNames.forClass);
        if (forClassStmt != null) {
            String forClassURI = forClassStmt.getResource().getURI();
            String forClassName = owlModel.getResourceNameForURI(forClassURI);
            if (forClassName != null) {
                Cls cls = project.getKnowledgeBase().getCls(forClassName);
                if (cls != null) {
                    ClsWidget clsWidget = project.getDesignTimeClsWidget(cls);
                    
                    resetClsWidgetForm(cls);
                    
                    WidgetDescriptor wd = clsWidget.getDescriptor();
                    // wd.setName(forClassName);
                    wd.setDirectlyCustomizedByUser(true);
                    PropertyList propertyList = wd.getPropertyList();
                    StmtIterator widgets = formWidget.listProperties(FormsNames.widgets);
                    while (widgets.hasNext()) {
                        Statement s = widgets.nextStatement();
                        if (s.getResource() != null) {
                            Resource widgetResource = s.getResource();
                            addFormWidget(clsWidget, widgetResource);
                        }
                    }
                }
            }
        }
    }



	private void addFormWidget(ClsWidget clsWidget, Resource widgetResource) {
        Statement forPropertyStmt = widgetResource.getProperty(FormsNames.forProperty);
        String slotName = null;
        if (forPropertyStmt != null) {
            String forPropertyURI = forPropertyStmt.getResource().getURI();
            String forPropertyName = owlModel.getResourceNameForURI(forPropertyURI);
            if (forPropertyName != null && owlModel.getRDFResource(forPropertyName) instanceof RDFProperty) {
                slotName = forPropertyName;
            }
        }
        else {
            Statement commentStmt = widgetResource.getProperty(RDFS.comment);
            if(commentStmt != null) {
                slotName = commentStmt.getString();
            }
            else {
                slotName = ProtegeNames.Slot.INFERRED_TYPE;
            }
        }
        if (slotName != null) {
            Slot slot = project.getKnowledgeBase().getSlot(slotName);            
            String widgetClassName = getWidgetClassName(widgetResource);
            if (widgetClassName != null) {
                clsWidget.replaceWidget(slot, widgetClassName);
                SlotWidget slotWidget = clsWidget.getSlotWidget(slot);
                setWidgetBounds(slotWidget.getDescriptor(), widgetResource);
                OWLWidgetPropertyListUtil.loadFormsProperties(owlModel, slotWidget, widgetResource);
            }
        }
    }

	

    private void resetClsWidgetForm(Cls cls) {
    	ClsWidget clsWidget = project.getDesignTimeClsWidget(cls);
    	
    	if (clsWidget == null)
    		return;
    	
    	for (Iterator iter = cls.getTemplateSlots().iterator(); iter.hasNext();) {
			Slot slot = (Slot) iter.next();
			
			try {
				SlotWidget slotWidget = clsWidget.getSlotWidget(slot);
				
				if (slotWidget != null)
					clsWidget.replaceWidget(slot, null);
				
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING, "Couldn't remove slot widget for slot " + slot + " from class form " + cls);
			}
			
		}	
	}

	

    private int getInt(Resource widgetResource, Property property) {
        Statement s = widgetResource.getProperty(property);
        if (s != null) {
            return s.getInt();
        }
        else {
            return 0;
        }
    }


    private String getWidgetClassName(Resource widgetResource) {
        Statement typeStatement = widgetResource.getProperty(RDF.type);
        if (typeStatement != null) {
            Resource type = typeStatement.getResource();
            Statement classNameStmt = type.getProperty(ProtegeFormsNames.javaClassName);
            
            if (classNameStmt == null) {
            	classNameStmt = widgetResource.getProperty(ProtegeFormsNames.javaClassName);
            }
            
            if (classNameStmt != null) {
                return classNameStmt.getString();
            }
        }
        return null;
    }


    private void setWidgetBounds(WidgetDescriptor wd, Resource widgetResource) {
        Iterator layoutData = widgetResource.listProperties(FormsNames.layoutData);
        while (layoutData.hasNext()) {
            Statement layoutStatement = (Statement) layoutData.next();
            if (layoutStatement.getResource() != null) {
                Resource layoutResource = layoutStatement.getResource();
                if (layoutResource.hasProperty(RDF.type, AbsoluteLayoutNames.AbsoluteLayoutData)) {
                    int x = getInt(layoutResource, AbsoluteLayoutNames.x);
                    int y = getInt(layoutResource, AbsoluteLayoutNames.y);
                    int width = getInt(layoutResource, AbsoluteLayoutNames.width);
                    int height = getInt(layoutResource, AbsoluteLayoutNames.height);
                    wd.setBounds(new Rectangle(x, y, width, height));
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        //OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(new FileInputStream("travel.owl"));
    	OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(new FileInputStream("/work/protege/projects/test_forms_owl/imported.owl"));
        AbsoluteFormsLoader loader = new AbsoluteFormsLoader(owlModel);
        loader.load(new File("/work/protege/projects/test_forms_owl/imported.owl.forms").toURL());
    }


    /**
     * Don't use this method - just for testing right now
     */
    public static boolean useNewFormMechanism_DontUseThisMethod() {
        return ApplicationProperties.getBooleanProperty("NewOWLFormsMechanism", false);
    }
    
    
    private void loadForm(TripleStore ts) throws Exception {
    	if (ts == null)
    		return;
    
    	//try to get the forms file from the same location as the ontology
    	if (ts == owlModel.getTripleStoreModel().getTopTripleStore()) {
    		URI owlFileURI = OWLUtil.getOWLFileURI(owlModel);
    		
    		//put this is a try catch?
    		if (owlFileURI != null) {    		
	            String formsFileName = owlFileURI.toString() + AbsoluteFormsGenerator.SUFFIX;
	            File file = new File(new URI(formsFileName));
	            
	            if (file.exists()) {
	            	Log.getLogger().info("Loading forms from " + file);
	            	load(new FileInputStream(file));
	            	return;
	            }
	        }
    	}
                
    	URI formsFileURI = new URI(AbsoluteFormsGenerator.getFormsFileURI(ts, owlModel));
    	
    	//try to load the forms file by using the repositories
    	Repository rep = owlModel.getRepositoryManager().getRepository(formsFileURI);
		
    	if (rep != null) {
    		//If there is already a repository entry for the form file, use it
    		if (rep.contains(formsFileURI) && rep instanceof AbstractStreamBasedRepositoryImpl) {
    			Log.getLogger().info("Loading forms from " + formsFileURI);
    			load(((AbstractStreamBasedRepositoryImpl) rep).getInputStream(formsFileURI));
    			return;
    		}
    	}

    	URI ontologyFileURI = new URI(ts.getName());
    	
		//check whether there is a local folder repository and look for the forms file there
		Repository ontologyRep = owlModel.getRepositoryManager().getRepository(ontologyFileURI);
		
		if (ontologyRep != null && (ontologyRep instanceof LocalFolderRepository)) {
			//the location of the ontology
			String location = ontologyRep.getOntologyLocationDescription(ontologyFileURI);
			
			if (location != null) {				
	            String formsFileName = location + AbsoluteFormsGenerator.SUFFIX;
	            URI formsFileLocalURI = URIUtilities.createURI(formsFileName); 
	            File file = new File(formsFileLocalURI);
	            
	            if (file.exists()) {
	            	Log.getLogger().info("Loading forms from " + file);
	            	load(new FileInputStream(file));
	            	return;
	            }				
			}    			
    	}   	
    	
	}
  

}
