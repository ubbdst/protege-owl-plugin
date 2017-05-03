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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import edu.stanford.smi.protegex.owl.model.RDFNames;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultXMLWriter implements XMLWriter {


    private Stack elementStack;

    private Writer writer;

    private String encoding;

    private String xmlBase;

    private XMLWriterNamespaceManager xmlWriterNamespaceManager;
    
    private int newNamespacePrefixIndex = 0;

    private Map<String, String> entities;

    private static final int TEXT_CONTENT_WRAP_LIMIT = 15;


    public DefaultXMLWriter(Writer writer, XMLWriterNamespaceManager xmlWriterNamespaceManager) {
        this(writer, xmlWriterNamespaceManager, "");
    }


    public DefaultXMLWriter(Writer writer,
                            XMLWriterNamespaceManager xmlWriterNamespaceManager,
                            String xmlBase) {
        this.writer = writer;
        this.xmlWriterNamespaceManager = xmlWriterNamespaceManager;
        this.xmlBase = xmlBase;
	    this.encoding = "";
	    elementStack = new Stack();
        setupEntities();
    }

    private void setupEntities() {
        ArrayList<String> namespaces = new ArrayList<String>(xmlWriterNamespaceManager.getNamespaces());
        Collections.sort(namespaces, new Comparator<String>() {
            public int compare(String o1,
                               String o2) {
                // Shortest string first
                return o1.length() - o2.length();
            }
        });
        entities = new LinkedHashMap<String, String>();
        for (Iterator<String> it = namespaces.iterator(); it.hasNext();) {
            String curNamespace = it.next();
            String curPrefix = xmlWriterNamespaceManager.getPrefixForNamespace(curNamespace);
            entities.put(curNamespace, "&" + curPrefix + ";");
        }
    }


    private String swapForEntity(String value) {
        String repVal;
        for (Iterator<String> it = entities.keySet().iterator(); it.hasNext();) {
            String curEntity = it.next();
            String entityVal = entities.get(curEntity);
            if (value.length() > curEntity.length()) {
                repVal = StringUtils.replaceOnce(value, curEntity, entityVal);
                if (repVal.length() < value.length()) {
                    return repVal;
                }
            }
        }
        return value;
    }


    public String getDefaultNamespace() {
        return xmlWriterNamespaceManager.getDefaultNamespace();
    }


    public String getXMLBase() {
        return xmlBase;
    }


    public XMLWriterNamespaceManager getNamespacePrefixes() {
        return xmlWriterNamespaceManager;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }


    public void setWrapAttributes(boolean b) {
        if (elementStack.size() > 0) {
            XMLElement element = (XMLElement) elementStack.peek();
            element.setWrapAttributes(b);
        }
    }


    public void writeStartElement(String name) throws IOException {
        XMLElement element = new XMLElement(name, elementStack.size());
        if (elementStack.size() > 0) {
            XMLElement topElement = (XMLElement) elementStack.peek();
            if (topElement != null) {
                topElement.writeElementStart(false);
            }
        }
        elementStack.push(element);
    }
    
    public void writeStartElement(String namespace, String name) throws IOException {
        boolean needsLocalNamespaceDecl = false;
        String prefix = null;
        String tagName = RDFNames.Slot.RESOURCE;
        if (getDefaultNamespace().equals(namespace)) {
            tagName = name;
        }
        else if ((prefix = getNamespacePrefixes().getPrefixForNamespace(namespace)) != null) {
            tagName = prefix + ":" + name;
        }
        else {
            do {
                prefix = "p" + (newNamespacePrefixIndex++);
            }
            while (getNamespacePrefixes().getPrefixes().contains(prefix));
            tagName = prefix + ":" + name;
            needsLocalNamespaceDecl = true;
        }
        writeStartElement(tagName);
        if (needsLocalNamespaceDecl) {
            writeAttribute("xmlns:" + prefix, namespace);
        }
    }


    public void writeEndElement() throws IOException {
        // Pop the element off the stack and write it out
        if (elementStack.size() > 0) {
            XMLElement element = (XMLElement) elementStack.pop();
            element.writeElementEnd();
        }
    }


    public void writeAttribute(String attr,
                               String val) {
        XMLElement element = (XMLElement) elementStack.peek();
        element.setAttribute(attr, val);
    }


    public void writeTextContent(String text) {
        XMLElement element = (XMLElement) elementStack.peek();
        element.setText(text);
    }


    private void writeEntities(String rootName) throws IOException {
        writer.write("\n\n<!DOCTYPE " + rootName + " [\n");
        for (Iterator<String> it = entities.keySet().iterator(); it.hasNext();) {
            String entityVal = it.next();
            String entity = entities.get(entityVal);
            entity = entity.substring(1, entity.length() - 1);
            writer.write("    <!ENTITY ");
            writer.write(entity);
            writer.write(" \"");
            writer.write(entityVal);
            writer.write("\" >\n");
        }
        writer.write("]>\n\n\n");
    }


    public void startDocument(String rootElementName) throws IOException {
	    String encodingString = "";
	    if(encoding.length() > 0) {
		    encodingString = " encoding=\"" + encoding + "\"";
	    }
	    writer.write("<?xml version=\"1.0\"" + encodingString + "?>\n");
        if (XMLWriterPreferences.getInstance().isUseNamespaceEntities()) {
            writeEntities(rootElementName);
        }
        writeStartElement(rootElementName);
        setWrapAttributes(true);
        writeAttribute("xmlns", xmlWriterNamespaceManager.getDefaultNamespace());
        if (xmlBase.equals("") == false) {
            writeAttribute("xml:base", xmlBase);
        }
        for (Iterator it = xmlWriterNamespaceManager.getPrefixes().iterator(); it.hasNext();) {
            String curPrefix = (String) it.next();
            writeAttribute("xmlns:" + curPrefix, xmlWriterNamespaceManager.getNamespaceForPrefix(curPrefix));
        }
    }


    public void endDocument() throws IOException {
        // Pop of each element
        while (elementStack.size() > 0) {
            writeEndElement();
        }
    }


    public class XMLElement {

        private String name;

        private Map attributes;

        String textContent;

        private boolean startWritten;

        private int indentation;

        private boolean wrapAttributes;


        public XMLElement(String name) {
            this(name, 0);
            wrapAttributes = false;
        }


        public XMLElement(String name, int indentation) {
            this.name = name;
            attributes = new LinkedHashMap();
            this.indentation = indentation;
            textContent = null;
            startWritten = false;
        }


        public void setWrapAttributes(boolean b) {
            wrapAttributes = true;
        }


        public void setAttribute(String attribute, String value) {
            attributes.put(attribute, value);
        }


        public void setText(String content) {
            textContent = content;
        }


        public void writeElementStart(boolean close) throws IOException {
            if (startWritten == false) {
                startWritten = true;
                insertIndentation();
                writer.write('<');
                writer.write(name);
                writeAttributes();
                if (textContent != null) {
                    boolean wrap = textContent.length() > TEXT_CONTENT_WRAP_LIMIT;
                    if (wrap) {
                        writeNewLine();
                        indentation++;
                        insertIndentation();
                    }
                    writer.write('>');
                    writeTextContent();
                    if (wrap) {
                        indentation--;
                    }
                }
                if (close) {
                    if (textContent != null) {
                        writeElementEnd();
                    }
                    else {
                        writer.write("/>");
                        writeNewLine();
                    }
                }
                else {
                    if (textContent == null) {
                        writer.write('>');
                        writeNewLine();
                    }
                }
            }
        }


        public void writeElementEnd() throws IOException {
            if (startWritten == false) {
                writeElementStart(true);
            }
            else {
                if (textContent == null) {
                    insertIndentation();
                }
                writer.write("</");
                writer.write(name);
                writer.write(">");
                writeNewLine();
            }
        }


        private void writeAttribute(String attr, String val) throws IOException {
            writer.write(attr);
            writer.write('=');
            writer.write('"');
            if (XMLWriterPreferences.getInstance().isUseNamespaceEntities()) {
                writer.write(swapForEntity(StringEscapeUtils.escapeXml(val)));
            }
            else {
                writer.write(StringEscapeUtils.escapeXml(val));
            }
            writer.write('"');
        }


        private void writeAttributes() throws IOException {
            for (Iterator it = attributes.keySet().iterator(); it.hasNext();) {
                String attr = (String) it.next();
                String val = (String) attributes.get(attr);
                writer.write(' ');
                writeAttribute(attr, val);
                if (it.hasNext() && wrapAttributes) {
                    writer.write("\n");
                    indentation++;
                    insertIndentation();
                    indentation--;
                }
            }
        }


        private void writeTextContent() throws IOException {
            if (textContent != null) {
                writer.write(StringEscapeUtils.escapeXml(textContent));
            }
        }


        private void insertIndentation() throws IOException {
            if (XMLWriterPreferences.getInstance().isIndenting()) {
                for (int i = 0; i < indentation * XMLWriterPreferences.getInstance().getIndentSize(); i++) {
                    writer.write(' ');
                }
            }
        }


        private void writeNewLine() throws IOException {
            writer.write('\n');
        }

    }
}

