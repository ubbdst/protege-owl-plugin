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

package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protegex.owl.model.*;

import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultRDFSDatatypeFactory implements RDFSDatatypeFactory {

    private OWLModel owlModel;


    public DefaultRDFSDatatypeFactory(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    public RDFSDatatype createAnonymousDatatype(RDFSDatatype baseType) {
        String name = owlModel.getNextAnonymousResourceName();
        return createDatatype(baseType, name);
    }


    public RDFSDatatype createDatatype(RDFSDatatype baseType, String name) {
        RDFSDatatype datatype = owlModel.createRDFSDatatype(name);
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_BASE);
        datatype.setPropertyValue(property, baseType);
        return datatype;
    }


    public void setLength(RDFSDatatype datatype, int value) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_LENGTH);
        if (value >= 0) {
            datatype.setPropertyValue(property, new Integer(value));
        }
        else {
            datatype.setPropertyValues(property, Collections.EMPTY_LIST);
        }
    }


    public void setMaxExclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MAX_EXCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMaxInclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MAX_INCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMaxLength(RDFSDatatype datatype, int value) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MAX_LENGTH);
        if (value >= 0) {
            datatype.setPropertyValue(property, new Integer(value));
        }
        else {
            datatype.setPropertyValues(property, Collections.EMPTY_LIST);
        }
    }


    public void setMinExclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MIN_EXCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMinInclusive(RDFSDatatype datatype, RDFSLiteral literal) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MIN_INCLUSIVE);
        datatype.setPropertyValue(property, literal);
    }


    public void setMinLength(RDFSDatatype datatype, int value) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_MIN_LENGTH);
        if (value >= 0) {
            datatype.setPropertyValue(property, new Integer(value));
        }
        else {
            datatype.setPropertyValues(property, Collections.EMPTY_LIST);
        }
    }


    public void setPattern(RDFSDatatype datatype, String value) {
        RDFProperty property = XSPNames.getRDFProperty(owlModel, XSPNames.XSP_PATTERN);
        datatype.setPropertyValue(property, value);
    }
}
