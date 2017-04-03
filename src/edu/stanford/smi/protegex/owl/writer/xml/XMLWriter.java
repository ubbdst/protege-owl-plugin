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

package edu.stanford.smi.protegex.owl.writer.xml;

import java.io.IOException;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface XMLWriter {

    /**
     * Sets the encoding for the document that the rdfwriter produces.
     * The default encoding is "UTF-8".
     *
     * @param encoding The encoding.
     */
    public void setEncoding(String encoding);


    /**
     * Gets the default namespace that the rdfwriter uses.
     */
    public String getDefaultNamespace();


    /**
     * Gets the rdfwriter's namespace manager.
     */
    public XMLWriterNamespaceManager getNamespacePrefixes();


    public String getXMLBase();


    /**
     * Causes the current element's attributes to be wrapped in the
     * output.
     */
    public void setWrapAttributes(boolean b);


    /**
     * Starts writing the document.  The root element will contain
     * the namespace declarations and xml:base attribute.
     *
     * @param rootElementName The name of the root element.
     */
    public void startDocument(String rootElementName) throws IOException;


    /**
     * Causes all open elements, including the document root
     * element, to be closed.
     */
    public void endDocument() throws IOException;


    /**
     * Writes the start of an element
     *
     * @param name The tag name of the element to be written.
     */
    public void writeStartElement(String name) throws IOException;

    /**
     * Writes the start of an element with namespace and a name
     * 
     * @param namespace The namespace of the element to be written (e.g. http://smi-protege.stanford.edu/ontologes/test.owl#)
     */
    public void writeStartElement(String namespace, String name) throws IOException;

    

    /**
     * Writes the closing tag of the last element to be started.
     */
    public void writeEndElement() throws IOException;


    /**
     * Writes an attribute of the last element to be started (that
     * has not been closed).
     *
     * @param attr The name of the attribute
     * @param val  The value of the attribute
     */
    public void writeAttribute(String attr, String val) throws IOException;


    /**
     * Writes a text element
     * @param text The text to be written
     */
    public void writeTextContent(String text) throws IOException;
}

