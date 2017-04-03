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

import com.hp.hpl.jena.rdf.model.impl.Util;

import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;

public class NamespaceUtil {
    
    public static final String PREFIX_TO_NAME_SEPARATOR = ":";

	public static String getNameSpace(String fullURI) {
		if (!URIUtilities.isAbsoluteURI(fullURI)) {
			return null;
		}
		return fullURI.substring(0, Util.splitNamespace(fullURI) );
	}

	
	public static String getLocalName(String fullURI)	{
		if (fullURI == null) { return null; }
		if (!URIUtilities.isAbsoluteURI(fullURI)) {
			return fullURI;
		}
		return fullURI.substring(Util.splitNamespace(fullURI) );
	}
	
	public static String getPrefixForResourceName(OWLModel owlModel, String fullURI) {
	    String namespace = getNameSpace(fullURI);
	    return owlModel.getNamespaceManager().getPrefix(namespace);
	}
	
	public static String getPrefixedName(OWLModel owlModel, String fullURI)	{	
	    return getPrefixedName(owlModel.getNamespaceManager(), fullURI);
	}
	
	
	public static String getPrefixedName(NamespaceManager names, String fullURI)   {
		String uri = getNameSpace(fullURI);

		if (uri == null) {
			return fullURI;
		}

		String localName = getLocalName(fullURI);
		
		
		String prefix = names.getPrefix(uri);

		if (prefix == null) {
			return fullURI; //do we want another strategy here?
		} else if (prefix.equals(OWLNamespaceManager.DEFAULT_NAMESPACE_PREFIX)) {
			return localName;
		} else {
			StringBuffer buffer = new StringBuffer();
			buffer.append(prefix);
			buffer.append(':');
			buffer.append(localName);
			return buffer.toString();
		}
	}
	
	public static String getFullName(OWLModel owlModel, String prefixedName) {
		int ind = prefixedName.indexOf(PREFIX_TO_NAME_SEPARATOR);
		
		NamespaceManager nsm = owlModel.getNamespaceManager();
		
		if (ind == -1) { //no ":" in the prefixed name, this is a name in the default namespace
			String defaultNamespace = nsm.getDefaultNamespace();
			
			if (defaultNamespace == null) {
				return prefixedName;
			}
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(defaultNamespace);
			buffer.append(prefixedName);
			
			return buffer.toString();	
		}
						
		if (ind == 0) {//e.g. ":protege" - what to do in this case?
			//TODO: what should happen in this case?
			return prefixedName;
		}
		
		String prefix = prefixedName.substring(0, ind);
		String localName = prefixedName.substring(ind + 1);
		
		String namespace = nsm.getNamespaceForPrefix(prefix);
		
		if (namespace == null) { // no namespace for prefix defined
			return null;
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(namespace);
		buffer.append(localName);
		
		return buffer.toString();	
				
	}
	
}
