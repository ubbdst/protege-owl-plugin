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

package edu.stanford.smi.protegex.owl.jena.triplestore;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.impl.XMLLiteralType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.protege2jena.Protege2Jena;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * A TripleWriter that writes into a given OntModel, which is then written to a
 * file when done.  This TripleWriter keeps the whole model in memory, i.e. it is
 * mandatory to make sure that the model does not become too big.  However, the
 * advantage is that this can be used to write triples and prefixes in an arbitrary
 * order.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaMemoryTripleWriter implements TripleWriter {

    private Node defaultOntologyNode;

    private OntModel ontModel;

    private OutputStream os;


    /**
     * Creates a JenaMemoryTripleWriter for in-memory use only.
     * The method close shall not be called on this.
     * This constructor is useful for testing purposes, to test the output into a writer
     * without having to generate a File.
     */
    public JenaMemoryTripleWriter() {
        this(null);
    }


    public JenaMemoryTripleWriter(OutputStream os) {
        this(os, ModelFactory.createOntologyModel());
    }


    public JenaMemoryTripleWriter(OutputStream os, OntModel ontModel) {
        this.ontModel = ontModel;
        this.os = os;
    }


    public void addImport(String uri) {
        addTriple(defaultOntologyNode, OWL.imports.asNode(), Node.createURI(uri));
    }


    public void addTriple(Node subject, Node predicate, Node object) {
        ontModel.getBaseModel().getGraph().add(new com.hp.hpl.jena.graph.Triple(subject, predicate, object));
    }


    public void close() throws Exception {
        String lang = FileUtils.langXMLAbbrev;
        RDFWriter writer = ontModel.getWriter(lang);
        String defaultNamespace = ontModel.getNsPrefixURI("");
        Jena.prepareWriter(writer, lang, defaultNamespace);
        Writer ow = new OutputStreamWriter(os, "UTF-8");
        String baseURI = defaultNamespace;
        if (baseURI.endsWith("#")) {
            baseURI = baseURI.substring(0, baseURI.length() - 1);
        }
        Protege2Jena.removeRedundantRDFLists(ontModel);
        writer.write(ontModel.getBaseModel(), ow, baseURI);
        os.close();
    }


    private Node createNodeFromRDFResource(RDFResource resource) {
        if (resource.isAnonymous()) {
            AnonId anonId = new AnonId("_:" + resource.getName());
            return Node.createAnon(anonId);
        }
        else {
            return Node.createURI(resource.getURI());
        }
    }


    private Node createNodeFromRDFSLiteral(RDFSLiteral literal, OWLModel owlModel) {
        String language = literal.getLanguage();
        if (language != null) {
            String value = literal.getString();
            return Node.createLiteral(value, language, XMLSchemaDatatypes.getXSDDatatype(owlModel.getXSDstring()));
        }
        else {
            RDFDatatype datatype = XMLSchemaDatatypes.getXSDDatatype(literal.getDatatype());
            if (datatype == null && owlModel.getRDFXMLLiteralType().equals(literal.getDatatype())) {
                datatype = XMLLiteralType.theXMLLiteralType;
            }
            return Node.createLiteral(literal.getString(), "", datatype);
        }
    }


    public OntModel getOntModel() {
        return ontModel;
    }


    public void init(String baseURI) {
        defaultOntologyNode = Node.createURI(baseURI);
        addTriple(defaultOntologyNode, RDF.type.asNode(), OWL.Ontology.asNode());
    }


    public void write(RDFResource tsubject, RDFProperty tpredicate, Object tobject) {
        Node subject = createNodeFromRDFResource(tsubject);
        Node predicate = createNodeFromRDFResource(tpredicate);
        Node object = null;
        if (tobject instanceof RDFResource) {
            object = createNodeFromRDFResource((RDFResource) tobject);
        }
        else if (tobject instanceof RDFSLiteral) {
            object = createNodeFromRDFSLiteral((RDFSLiteral) tobject, tsubject.getOWLModel());
        }
        else {
            OWLModel owlModel = tsubject.getOWLModel();
            RDFSLiteral literal = owlModel.asRDFSLiteral(tobject);
            object = createNodeFromRDFSLiteral(literal, owlModel);
        }
        addTriple(subject, predicate, object);
    }


    public void writePrefix(String prefix, String namespace) {
        ontModel.setNsPrefix(prefix, namespace);
    }
}
