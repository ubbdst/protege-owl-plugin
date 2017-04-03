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

package edu.stanford.smi.protegex.owl.database;

import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.dig.DIGReasoner;
import com.hp.hpl.jena.reasoner.dig.DIGReasonerFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.OntModelProvider;
import edu.stanford.smi.protegex.owl.jena.creator.JenaCreator;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.framestore.LocalClassificationFrameStore;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;

/**
 * An AbstractOWLModel with extra support for databases.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseModel
        extends AbstractOWLModel
        implements OntModelProvider {
    private static transient Logger log = Log.getLogger(OWLDatabaseModel.class);
    
    static int idCounter = 0;

    private OWLOntology defaultDBOWLOntology;


    public OWLDatabaseModel(KnowledgeBaseFactory factory) {
        super(factory);
    }


	public OntModel getOntModel() {

        long startTime = System.currentTimeMillis();
        JenaCreator creator = new JenaCreator(this, false, null,
                                              new ModalProgressBarManager("Converting Ontology"));
        OntModel ontModel = creator.createOntModel();
        long endTime = System.currentTimeMillis();
        Log.getLogger().info("[OWLDatabaseModel.getOntModel] Duration " + (endTime - startTime));
        return ontModel;
    }


    public OntModel getOWLDLOntModel() {
        // return new JenaDLConverter(getOntModel()).convertOntModel();
        long startTime = System.currentTimeMillis();
        JenaCreator creator = new JenaCreator(this, true, null,
                                              new ModalProgressBarManager("Preparing Ontology"));
        OntModel ontModel = creator.createOntModel();
        // ontModel.write(System.out, ModelLoader.langXMLAbbrev);
        long endTime = System.currentTimeMillis();
        log.info("[OWLDatabaseModel.getOWLDLOntModel] Duration " + (endTime - startTime));
        return ontModel;
    }


    public int getOWLSpecies() {
        OntModel ontModel = getOntModel();
        return Jena.getOWLSpecies(ontModel);
    }


    public OntModel getReasonerOntModel(String classifierURL) {

        com.hp.hpl.jena.rdf.model.Model newModel = ModelFactory.createDefaultModel();
        Resource resource = newModel.createResource("http://foo.de#foo");
        newModel.add(resource, ReasonerVocabulary.EXT_REASONER_URL, classifierURL);

        DIGReasoner reasoner = (DIGReasoner) ReasonerRegistry.theRegistry().
                create(DIGReasonerFactory.URI, resource);

        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
        spec.setReasoner(reasoner);
        OntModel m = Jena.cloneOntModel(getOntModel(), spec);
        return m;
    }


    public void initDefaultNamespaces() {
        NamespaceManager namespaceManager = getNamespaceManager();
        namespaceManager.setPrefix(OWL.getURI(), OWLNames.OWL_PREFIX);
        namespaceManager.setPrefix(RDF.getURI(), RDFNames.RDF_PREFIX);
        namespaceManager.setPrefix(RDFS.getURI(), RDFSNames.RDFS_PREFIX);
        namespaceManager.setPrefix(XSDDatatype.XSD + "#", RDFNames.XSD_PREFIX);
        namespaceManager.setModifiable(OWLNames.OWL_PREFIX, false);
        namespaceManager.setModifiable(RDFNames.RDF_PREFIX, false);
        namespaceManager.setModifiable(RDFSNames.RDFS_PREFIX, false);
        namespaceManager.setModifiable(RDFNames.XSD_PREFIX, false);
    }

}
