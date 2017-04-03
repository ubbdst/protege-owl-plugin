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

import edu.stanford.smi.protegex.owl.model.*;

/**
 * An object representing metadata about a property 
 * useful for Java code generation.
 *
 * @author Csongor Nyulas 
 */
public class RDFPropertyCode implements Comparable {

    private RDFProperty property;

    private boolean usePrefix;

    public RDFPropertyCode(RDFProperty property, boolean usePrefixInNames) {
        this.property = property;
        this.usePrefix = usePrefixInNames;
    }


    public int compareTo(Object o) {
        if (o instanceof RDFPropertyCode) {
            RDFPropertyCode other = (RDFPropertyCode) o;
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


    //commented parts may be useful in the future ....

/*    
    public String getJavaType() {
        RDFResource range = property.getRange();
        if (range instanceof RDFSDatatype) {
            OWLModel owlModel = property.getOWLModel();
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
            return new RDFSClassCode((RDFSNamedClass) range).getJavaName();
        } else if (range instanceof OWLAnonymousClass) {
        	RDFResource propRange = property.getRange();
        	
        	if (propRange != null && propRange instanceof RDFSNamedClass) {
        		return new RDFSClassCode((RDFSNamedClass) propRange).getJavaName();
        	} else {
        		return "Object";
        	}
        }
        else {
            return "Object";
        }
    }
*/

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


    //commented parts may be useful in the future ....

/*
    public boolean isCustomType() {
        RDFResource range = property.getRange();
        return range instanceof RDFSNamedClass;
    }


    public boolean isMultiple() {
        return !property.isFunctional();
    }


    public boolean isPrimitive() {
        RDFResource range = property.getRange();
        if (range instanceof RDFSDatatype) {
            OWLModel owlModel = property.getOWLModel();
            if (owlModel.getXSDboolean().equals(range) ||
                    owlModel.getXSDfloat().equals(range) ||
                    owlModel.getXSDint().equals(range)) {
                return true;
            }
        }
        return false;
    }
*/
}
