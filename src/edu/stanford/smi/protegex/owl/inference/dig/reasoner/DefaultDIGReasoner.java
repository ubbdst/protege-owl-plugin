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

package edu.stanford.smi.protegex.owl.inference.dig.reasoner;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGError;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGErrorException;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.logger.DIGLogger;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGTranslator;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGTranslatorFactory;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGVocabulary;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 4, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultDIGReasoner implements DIGReasoner {
  public static final String DEFAULT_URL_PROPERTY = "edu.stanford.smi.protegex.owl.jena.reasoner.URL";
  private static final String defaultURL = ApplicationProperties.getString(DEFAULT_URL_PROPERTY, "http://localhost:8080");

  private URL reasonerURL;

    private DocumentBuilderFactory docBuilderFactory;

    private DocumentBuilder docBuilder;

    private XMLSerializer serializer;

    private OutputFormat format;

    private DIGTranslator translator;

    /**
     * @deprecated Use DIGReasonerPreferences to set logging
     */
    @Deprecated
    public static boolean log = true;


    public DefaultDIGReasoner() {
        translator = DIGTranslatorFactory.getInstance().createTranslator();
	    try {
		    reasonerURL = new URL(defaultURL);
	    }
	    catch(MalformedURLException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
	    }
	    // this.connection = connection;

        // Set up the XML Serializer that will convert DIG XML Documents
        // into streams to send to the external DIG reasoner
        format = new OutputFormat();
        format.setIndent(4);
        format.setIndenting(true);
        format.setPreserveSpace(false);
        serializer = new XMLSerializer(format);
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);

        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();

        }
        catch (ParserConfigurationException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    /**
     * Sets the URL of the inference.
     *
     * @param url The URL
     */
    public void setReasonerURL(String url) {
        // Pass this on to the reasoner connection
	    try {
		    reasonerURL = new URL(url);
	    }
	    catch(MalformedURLException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
	    }
    }


    /**
     * Gets the URL of the reasoner.
     */
    public String getReasonerURL() {
        return reasonerURL.toString();
    }


    /**
     * Gets the identity of
     * the inference
     *
     * @return A<code>DIGReasonerIdentity</code> object that encapsulates the
     *         information about the inference.
     */
    public DIGReasonerIdentity getIdentity() throws DIGReasonerException {

        // Format the request - a simple <getIdentifier/> request
        Document request = translator.createDIGDocument(DIGVocabulary.Management.GET_IDENTIFIER);

        // Send the request to the reasoner and get the response
        Document response = performRequest(request);

        // Create a new DIGReasonerIdentity object that we
        // can parse the response into
        DIGReasonerIdentity id = new DIGReasonerIdentity();

        id.parseIdentityDescrtiption(response);

        return id;

    }


    /**
     * A helper method that asks the reasoner to create
     * a new knowledgebase.
     *
     * @return A <code>String</code> that represents a URI that is an identifier for
     *         the newly created knowledgebase.
     */
    public String createKnowledgeBase() throws DIGReasonerException {
        Document request = translator.createDIGDocument(DIGVocabulary.Management.NEW_KNOWLEDGE_BASE);
        Document doc = performRequest(request);
        Element kbElement = (Element) doc.getDocumentElement().getElementsByTagName("kb").item(0);
        return kbElement.getAttribute("uri");
    }


    /**
     * A helper method that releases a previously created
     * knowledgebase.
     *
     * @param kbURI The <code>URI</code> of the knowledgebase
     */
    public void releaseKnowledgeBase(String kbURI) throws DIGReasonerException {
        Document doc = translator.createDIGDocument(DIGVocabulary.Management.RELEASE_KNOWLEDGE_BASE);
        doc.getDocumentElement().setAttribute("uri", kbURI);
        performRequest(doc);
    }


    /**
     * Clears the knowledge base
     *
     * @param kbURI The uri that identifies the knowledge
     *              base to be cleared.
     */
    public void clearKnowledgeBase(String kbURI) throws DIGReasonerException {
        Document doc = translator.createDIGDocument(DIGVocabulary.TELLS, kbURI);
        Element element = doc.createElement(DIGVocabulary.Management.CLEAR_KNOWLEDGE_BASE);
        doc.getDocumentElement().appendChild(element);
        performRequest(doc);
    }


    public Document performRequest(Document request) throws DIGReasonerException {
        log(Level.FINE, request);

        try {
            StringWriter writer = new StringWriter();
            serializer.setOutputCharStream(writer);
            serializer.serialize(request);


            HttpURLConnection conn = (HttpURLConnection) reasonerURL.openConnection();
	        conn.setRequestProperty("Content-Type", "text/xml");
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setDoOutput(true);
	        StringBuffer buffer = writer.getBuffer();
            conn.setRequestProperty("Content-Length", "" + buffer.length());
            conn.connect();

	        OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
	        osw.write(buffer.toString());
	        osw.flush();
	        osw.close();
			// Get the response
            Reader reader = new InputStreamReader(conn.getInputStream());
            Document doc = docBuilder.parse(new InputSource(reader));
            reader.close();
	        conn.disconnect();
	        log(Level.FINE, doc);
            performErrorCheck(doc);
            return doc;

        }
        catch (IOException e) {
            // Convert the IOException into a DIGReasonerException
            throw new DIGReasonerException(e.getMessage(), e);
        }
        catch (SAXException saxEx) {
            // Convert the SAXException into a DIGReasonerException
            throw new DIGReasonerException(saxEx.getMessage(), saxEx);
        }


    }


    /**
     * This method checks for any errors in the DIG response
     * and throws a DIGErrorException if there are any errors.
     *
     * @param doc The XML DIG Document that contains that response
     *            from the reasoner.
     */
    protected void performErrorCheck(Document doc) throws DIGErrorException {
        // Sift out the error elements.
        NodeList errors = doc.getDocumentElement().getElementsByTagName(DIGVocabulary.Response.ERROR);

        if (errors.getLength() > 0) {
            ArrayList errorList = new ArrayList(errors.getLength());

            // Process each error, getting the error message and error code.
            for (int i = 0; i < errors.getLength(); i++) {
                Element element = (Element) errors.item(i);
                final String message = element.getAttribute("message") + " [ID: " + element.getAttribute("id") + "]";
                final String code = ((Element) errors.item(i)).getAttribute("code");
                DIGError error = new DIGError(element.getAttribute("id"), message, code);
                errorList.add(error);
            }
            if (DIGReasonerPreferences.getInstance().isTreatErrorsAsWarnings() == false) {
                throw new DIGErrorException(errorList);
            }
            else {
                DIGLogger logger = DIGLogger.getInstance(this);
                for (Iterator it = errorList.iterator(); it.hasNext();) {
                    logger.logError((DIGError) it.next());
                }
            }
        }

    }


    /**
     * A helper method that lets us log
     * the DIG XML used to communicate with the reasoner.
     */
    public static void log(Level level, Document doc) {
        if (!digLogger.isLoggable(level)) {
            return;
        }
        StringWriter writer = new StringWriter();
        OutputFormat format = new OutputFormat();
        format.setIndent(4);
        format.setIndenting(true);
        XMLSerializer serializer = new XMLSerializer(DIGReasonerPreferences.getInstance().getLogOutputStream(),
                format);
        try {
            serializer.serialize(doc);
        }
        catch (IOException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        digLogger.log(level, writer.getBuffer().toString());

    }
}

