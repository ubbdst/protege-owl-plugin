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

package edu.stanford.smi.protegex.owl.javacode;

import java.util.Collection;

import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.jena.rdf2owl.RDF2OWL;
import edu.stanford.smi.protegex.owl.model.*;

/**
 * An object representing metadata about a property at a class
 * useful for Java code generation.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFPropertyAtClassCode implements Comparable {

    private RDFSNamedClass cls;

    private RDFProperty property;

    private boolean usePrefix;


    public RDFPropertyAtClassCode(RDFSNamedClass cls, RDFProperty property, boolean usePrefixInNames) {
        this.cls = cls;
        this.property = property;
        this.usePrefix = usePrefixInNames;
    }


    public int compareTo(Object o) {
        if (o instanceof RDFPropertyAtClassCode) {
            RDFPropertyAtClassCode other = (RDFPropertyAtClassCode) o;
            return getJavaName().compareTo(other.getJavaName());
        }
        return 0;
    }


    public String getJavaName() {
		String prefix = property.getNamespacePrefix();
		if ( usePrefix && prefix != null && (! prefix.equals("")) ) {
			prefix = prefix.toUpperCase() + "_";
			return RDFSClassCode.getValidJavaName(prefix + property.getLocalName());
		}
        return RDFSClassCode.getValidJavaName(property.getLocalName());
    }

    public String getJavaType() {
        RDFResource range = ((OWLNamedClass) cls).getAllValuesFrom(property);
        if (range instanceof OWLDataRange) {
        	range = ((OWLDataRange) range).getRDFDatatype();
        }
        
        if (range instanceof RDFSDatatype) {
            OWLModel owlModel = cls.getOWLModel();
            if (owlModel.getXSDboolean().equals(range)) {
                return "boolean";
            }
            else if (owlModel.getXSDfloat().equals(range)) {
                return "float";
            }
            else if (owlModel.getXSDint().equals(range)) {
                return "int";
            }
            else if (owlModel.getXSDstring().equals(range)) {
                return "String";
            }
            else {
                return "RDFSLiteral";
            }
        }
        else if (range instanceof RDFSNamedClass) {
            return new RDFSClassCode((RDFSNamedClass) range, usePrefix).getJavaName();
        } else if (range instanceof OWLAnonymousClass) {
        	RDFResource propRange = property.getRange();
        	
        	if (propRange != null && propRange instanceof RDFSNamedClass) {
        		return new RDFSClassCode((RDFSNamedClass) propRange, usePrefix).getJavaName();
        	} else {
        		return "Object";
        	}
        }
        else {
            return "Object";
        }
    }


    public RDFProperty getRDFProperty() {
        return property;
    }


    public String getUpperCaseJavaName() {
        String name = getJavaName();
        if (name.length() > 1) {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        else {
            return name.toUpperCase();
        }
    }


    public boolean isCustomType() {
        RDFResource range = ((OWLNamedClass) cls).getAllValuesFrom(property);
        return range instanceof RDFSNamedClass;
    }


    public boolean isMultiple() {
        if (cls instanceof OWLNamedClass) {
            int max = ((OWLNamedClass) cls).getMaxCardinality(property);
            return max < 0 || max > 1;
        }
        else {  // RDFSNamedClass only
            return !property.isFunctional();
        }
    }


    public boolean isPrimitive() {
        RDFResource range = ((OWLNamedClass) cls).getAllValuesFrom(property);
        if (range instanceof RDFSDatatype) {
            OWLModel owlModel = cls.getOWLModel();
            if (owlModel.getXSDboolean().equals(range) ||
                    owlModel.getXSDfloat().equals(range) ||
                    owlModel.getXSDint().equals(range)) {
                return true;
            }
        }
        return false;
    }
}
