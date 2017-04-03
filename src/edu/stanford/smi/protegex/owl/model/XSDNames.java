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

package edu.stanford.smi.protegex.owl.model;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface XSDNames {

    final static String PREFIX = "xsd:";
    
    //TT: replace PREFIX with XSD_NAMESPACE
    final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";


    final static String ANY_URI = (XSD_NAMESPACE + "anyURI").intern();

    final static String BASE_64_BINARY = (XSD_NAMESPACE + "base64Binary").intern();

    final static String BOOLEAN = (XSD_NAMESPACE + "boolean").intern();

    final static String BYTE = (XSD_NAMESPACE + "byte").intern();

    final static String DATE = (XSD_NAMESPACE + "date").intern();

    final static String DATE_TIME = (XSD_NAMESPACE + "dateTime").intern();

    final static String DECIMAL = (XSD_NAMESPACE + "decimal").intern();

    final static String DOUBLE = (XSD_NAMESPACE + "double").intern();

    final static String DURATION = (XSD_NAMESPACE + "duration").intern();

    final static String FLOAT = (XSD_NAMESPACE + "float").intern();

    final static String INT = (XSD_NAMESPACE + "int").intern();

    final static String INTEGER = (XSD_NAMESPACE + "integer").intern();
    
    final static String NON_NEGATIVE_INTEGER = (XSD_NAMESPACE + "nonNegativeInteger").intern();

    final static String LONG = (XSD_NAMESPACE + "long").intern();

    final static String SHORT = (XSD_NAMESPACE + "short").intern();

    final static String STRING = (XSD_NAMESPACE + "string").intern();

    final static String TIME = (XSD_NAMESPACE + "time").intern();


    static interface Facet {

        final static String LENGTH = (XSD_NAMESPACE + "length").intern();

        final static String MAX_EXCLUSIVE = (XSD_NAMESPACE + "maxExclusive").intern();

        final static String MAX_INCLUSIVE = (XSD_NAMESPACE + "maxInclusive").intern();

        final static String MAX_LENGTH = (XSD_NAMESPACE + "maxLength").intern();

        final static String MIN_EXCLUSIVE = (XSD_NAMESPACE + "minExclusive").intern();

        final static String MIN_INCLUSIVE = (XSD_NAMESPACE + "minInclusive").intern();

        final static String MIN_LENGTH = (XSD_NAMESPACE + "minLength").intern();

        final static String PATTERN = (XSD_NAMESPACE + "pattern").intern();
    }
}
