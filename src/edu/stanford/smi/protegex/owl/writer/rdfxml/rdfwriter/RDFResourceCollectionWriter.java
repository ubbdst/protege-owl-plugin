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
import java.util.Collection;
import java.util.Collections;

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
 */
public class RDFResourceCollectionWriter {

    private RDFResourceCollectionContentWriter collectionContentWriter;

    private boolean enclose;

    private XMLWriter xmlWriter;


    public RDFResourceCollectionWriter(OWLModel model,
                                       TripleStore tripleStore,
                                       Collection resources,
                                       Writer writer,
                                       boolean encloseInRDFElement) {
        String ontologyName = Util.getOntologyName(model, tripleStore);
        String defaultNamespace = model.getNamespaceManager().getDefaultNamespace();
        if (defaultNamespace == null) {
        	defaultNamespace = tripleStore.getName() + "#";
        }
        XMLWriterNamespaceManager nsm = Util.getNamespacePrefixes(model.getNamespaceManager(), defaultNamespace);                
        this.xmlWriter = XMLWriterFactory.getInstance().createXMLWriter(writer, nsm, ontologyName);
        this.enclose = encloseInRDFElement;
        collectionContentWriter = new RDFResourceCollectionContentWriter(resources, tripleStore);
    }


    public RDFResourceCollectionWriter(OWLModel model,
                                       TripleStore tripleStore,
                                       Collection resources,
                                       XMLWriter xmlWriter,
                                       boolean encloseInRDFElement) {
        this.xmlWriter = xmlWriter;
        this.enclose = encloseInRDFElement;
        collectionContentWriter = new RDFResourceCollectionContentWriter(resources, tripleStore);
    }


    public void write()
            throws IOException {
        if (enclose) {
            RDFXMLDocumentWriter docWriter = new RDFXMLDocumentWriter(xmlWriter, Collections.singleton(collectionContentWriter));
            docWriter.writeDocument();
        }
        else {
            collectionContentWriter.writeContent(xmlWriter);
        }
    }
}

