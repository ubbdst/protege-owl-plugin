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

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

import java.util.*;

/**
 * An object providing metadata about an RDFSNamedClass
 * or OWLNamedClass, suitable for Java code generation.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFSClassCode {

    private RDFSNamedClass cls;

    private boolean usePrefix; 


    public RDFSClassCode(RDFSNamedClass cls, boolean usePrefixInNames) {
        this.cls = cls;
        this.usePrefix = usePrefixInNames;
    }


    public String getJavaName() {
		String prefix = cls.getNamespacePrefix();
		if ( usePrefix && prefix != null && (! prefix.equals("")) ) {
			prefix = prefix.toUpperCase() + "_";
			return getValidJavaName(prefix + cls.getLocalName());
		}
        return getValidJavaName(cls.getLocalName());
    }


    /**
     * @return a List of RDFPropertyAtClassCodes
     * @see RDFPropertyAtClassCode
     */
    public List getPropertyCodes(boolean transitive) {
        Set properties = new HashSet();
        List codes = new ArrayList();
        Collection unionDomainProperties = cls.getUnionDomainProperties(transitive);
        Set relevantProperties = new HashSet(unionDomainProperties);
        if (cls instanceof OWLNamedClass) {
            OWLNamedClass owlNamedClass = (OWLNamedClass) cls;
            for (Iterator rit = owlNamedClass.getRestrictions().iterator(); rit.hasNext();) {
                OWLRestriction restriction = (OWLRestriction) rit.next();
                relevantProperties.add(restriction.getOnProperty());
            }
        }
        for (Iterator it = relevantProperties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (property.isSystem()) {
            	//skip system properties
            	continue;
            }
            properties.add(property);
            RDFPropertyAtClassCode code = new RDFPropertyAtClassCode(cls, property, usePrefix);
            codes.add(code);
            Collection subproperties = property.getSubproperties(true);
            Iterator sit = subproperties.iterator();
            while (sit.hasNext()) {
                RDFProperty subproperty = (RDFProperty) sit.next();
                if (!subproperty.isDomainDefined() && !properties.contains(subproperty)) {
                    codes.add(new RDFPropertyAtClassCode(cls, subproperty, usePrefix));
                    properties.add(subproperty);
                }
            }
        }
        Collections.sort(codes);
        return codes;
    }


    public static String getValidJavaName(String name) {
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isJavaIdentifierPart(c)) {
                name = name.replace(c, '_');
            }
        }
        return name;
    }
}
