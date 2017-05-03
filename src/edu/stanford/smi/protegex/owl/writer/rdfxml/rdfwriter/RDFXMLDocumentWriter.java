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
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriter;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RDFXMLDocumentWriter {

    private XMLWriter xmlWriter;

    private Collection contentWriters;


    public RDFXMLDocumentWriter(XMLWriter xmlWriter,
                                Collection contentWriters) {
        this.xmlWriter = xmlWriter;
        this.contentWriters = contentWriters;
    }


    public void writeDocument() throws IOException {
        writeDocStart();
        for (Iterator it = contentWriters.iterator(); it.hasNext();) {
            RDFXMLContentWriter contentWriter = (RDFXMLContentWriter) it.next();
            contentWriter.writeContent(xmlWriter);
        }
        writeDocEnd();

    }


    private void writeDocStart() throws IOException {
        xmlWriter.startDocument(RDFNames.RDF);
    }


    private void writeDocEnd() throws IOException {
        xmlWriter.endDocument(); // Close off, finishing with our rdf:RDF element
    }
}

