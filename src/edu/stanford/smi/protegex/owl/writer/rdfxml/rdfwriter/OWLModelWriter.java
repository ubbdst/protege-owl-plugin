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

package edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.Util;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriter;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriterFactory;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriterNamespaceManager;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The <code>OWLModelWriter</code> can be used to write out a
 * <code>TripleStore</code> in a specified <code>OWLModel</code>
 * in RDF/XML format.
 */
public class OWLModelWriter {

    private OWLModel model;

    private TripleStore tripleStore;

    private XMLWriter xmlWriter;


    /**
     * Creates an object that can be used to write an <code>OWLModel</code>
     * in RDF/XML format to a specified rdfwriter.
     *
     * @param model       The model to be serialized to the rdfwriter.
     * @param tripleStore The triplestore that contains the triples to be
     *                    serialised.
     * @param writer      The <code>Writer</code> that the RDF/XML representation of the
     *                    model should be written to.
     */
    public OWLModelWriter(OWLModel model,
                          TripleStore tripleStore,
                          Writer writer) {
        this.model = model;
        this.tripleStore = tripleStore;
        XMLWriterNamespaceManager xmlnsm;
        NamespaceManager nsm = tripleStore.getNamespaceManager();        
        String defaultNamespace = nsm.getDefaultNamespace();
        if (defaultNamespace == null) {
        	defaultNamespace = tripleStore.getName() + "#";
        }
		xmlnsm = Util.getNamespacePrefixes(nsm, defaultNamespace);
        String ontologyName = Util.getOntologyName(model, tripleStore);
        this.xmlWriter = XMLWriterFactory.getInstance().createXMLWriter(writer, xmlnsm, ontologyName);
    }


    /**
     * Creates an object that can be used to write an <code>OWLModel</code>
     * in RDF/XML format to a specified <code>XMLWriter</code>.  This version
     * of the constructor takes an <code>XMLWriter</code> to allow fine grained
     * control over namespaces etc.
     *
     * @param model       The model to be serialized to the rdfwriter.
     * @param tripleStore The triplestore that contains the triples to be
     *                    serialised.
     * @param xmlWriter   The <code>XMLWriter</code> that will do the work of
     *                    creating the RDF/XML.
     */
    public OWLModelWriter(OWLModel model,
                          TripleStore tripleStore,
                          XMLWriter xmlWriter) {
        this.model = model;
        this.tripleStore = tripleStore;
        this.xmlWriter = xmlWriter;
    }


	public XMLWriter getXmlWriter() {
		return xmlWriter;
	}


    /**
     * Causes the RDF/XML representation of the model to be written.
     */
    public void write()
            throws IOException {
        RDFXMLDocumentWriter docWriter = new RDFXMLDocumentWriter(xmlWriter, Collections.singleton(
                getContentWriter(model, tripleStore)));
        docWriter.writeDocument();
    }


    protected RDFXMLContentWriter getContentWriter(OWLModel model, TripleStore tripleStore) {
        return new OWLModelContentWriter(model, tripleStore);
    }
}

