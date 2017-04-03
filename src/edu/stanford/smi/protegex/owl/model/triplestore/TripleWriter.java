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

package edu.stanford.smi.protegex.owl.model.triplestore;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * An interface for objects that can write Triples into a file or other stream.
 * This can be used as a uniform way of generating OWL files.
 * <p/>
 * The usual contract to use TripleWriters is
 * <p/>
 * <OL>
 * <LI><CODE>init()</CODE></LI>
 * <LI><CODE>writePrefix()</CODE>*</LI>
 * <LI><CODE>addImport()</CODE>*</LI>
 * <LI><CODE>write()</CODE>*</LI>
 * <LI><CODE>close()</CODE></LI>
 * </OL>
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface TripleWriter {

    /**
     * Adds an owl:imports statement to the default ontology in this writer.
     * This is a convenience method because the same can typically be achieved
     * by adding a corresponding triple.
     *
     * @param uri the URI to add
     */
    void addImport(String uri);


    /**
     * Tells the writer that we're finished.
     * This method should be called at the end of a writing process.
     * After this method has been called, no other calls to this object are permitted.
     */
    void close() throws Exception;


    /**
     * Initializes this writer to work with a given base URI.
     * This URI will also be the URI of the default ontology in this file,
     * and future calls of <CODE>addImport</CODE> will add to this ontology.
     *
     * @param baseURI the base URI, such as <CODE>http://www.a.com/ontology.owl</CODE>
     */
    void init(String baseURI);


    /**
     * Writes a given Triples into this writer.
     */
    void write(RDFResource resource, RDFProperty property, Object object) throws Exception;


    /**
     * Writes a prefix declaration into this writer.
     * Since namespace declarations are typically written to the beginning of a file,
     * implementations of this method may require that any calls to this method are
     * done before the first triple is written out.
     *
     * @param prefix    the prefix to define ("" for the default namespace)
     * @param namespace the namespace to assign to the prefix
     */
    void writePrefix(String prefix, String namespace) throws Exception;
}
