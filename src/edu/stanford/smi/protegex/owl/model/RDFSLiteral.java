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

import edu.stanford.smi.protegex.owl.model.visitor.Visitable;

/**
 * An interface to represent an RDF/XML Schema literal.
 * This encapsulates the value, the datatype and the language tag.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFSLiteral extends Comparable<RDFSLiteral>, RDFObject, Visitable {


    /**
     * Gets the value as a boolean.
     *
     * @return the boolean value
     */
    boolean getBoolean();


    /**
     * Gets the appropriate byte array if the value has datatype xsd:base64Binary
     *
     * @return the byte array, I guess
     */
    byte[] getBytes();


    /**
     * Gets the RDFSDatatype of this value.
     *
     * @return the RDFSDatatype
     */
    RDFSDatatype getDatatype();

    double getDouble();

    float getFloat();

    short getShort();


    /**
     * Gets the value as an int.
     *
     * @return the int value
     */
    int getInt();


    /**
     * Gets the language if it has been defined for this.
     *
     * @return a language or null
     */
    String getLanguage();


    long getLong();


    /**
     * If the datatype of this is one of the default datatypes, which can be
     * optimized by Protege, then this returns an optimized value.
     *
     * @return null or a Boolean, Integer, Float, or String
     */
    Object getPlainValue();
    
    String getRawValue();


    String getString();

}
