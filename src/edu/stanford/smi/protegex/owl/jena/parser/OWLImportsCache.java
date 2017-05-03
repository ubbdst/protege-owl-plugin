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

package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class OWLImportsCache {

	public static HashMap<String, Set<String>> owlOntoloyToImportsMap = new HashMap<String, Set<String>>();
	public static HashMap<String, String> owlOntologyLocToOntologName = new HashMap<String, String>();

	public static Set<String> getOWLImportsURI(String owlOntologyURI) {
		Set<String> imports = owlOntoloyToImportsMap.get(owlOntologyURI);

		return imports == null ? new HashSet<String>() : imports;
	}

	public static void addOWLImport(String owlOntologyURI, String importURI) {
		addImport(owlOntologyURI, importURI);
		
		//add support for imports by location for merging imports mode
		String currentlyParsingOntologyLocation = ProtegeOWLParser.getCurrentlyParsingOntologyLocation();
		if (currentlyParsingOntologyLocation != null) {
			addImport(currentlyParsingOntologyLocation, importURI);			
		}
	}
	
	private static void addImport(String owlOntologyURI, String importURI) {
		Set<String> imports = getOWLImportsURI(owlOntologyURI);
		imports.add(importURI);
		owlOntoloyToImportsMap.put(owlOntologyURI, imports);
	}

	public static Set<String> getImportedOntologies() {
		HashSet<String> set = new HashSet<String>();
		set.addAll(owlOntologyLocToOntologName.keySet());
		set.addAll(owlOntologyLocToOntologName.values());
		set.addAll(owlOntoloyToImportsMap.keySet());
		return set;
	}

	public static Set<String> getAllOntologies() {
		HashSet<String> set = new HashSet<String>();
		set.addAll(owlOntologyLocToOntologName.keySet());
		set.addAll(owlOntologyLocToOntologName.values());
		set.addAll(owlOntoloyToImportsMap.keySet());
		for (String ont : owlOntoloyToImportsMap.keySet()) {
			set.addAll(owlOntoloyToImportsMap.get(ont));
		}
		return set;
	}

	public static boolean isImported(String ontology) {
 		return owlOntologyLocToOntologName.keySet().contains(ontology) ||
 			owlOntologyLocToOntologName.values().contains(ontology) ||
 			owlOntoloyToImportsMap.keySet().contains(ontology);
	}


	public static String getOntologyName(String ontologyLoc) {
		return owlOntologyLocToOntologName.get(ontologyLoc);
	}

	public static void setOntologyName(String ontologyLoc, String ontologyName) {
		owlOntologyLocToOntologName.put(ontologyLoc, ontologyName);
	}


	public static void dispose() {
		owlOntologyLocToOntologName.clear();
		owlOntoloyToImportsMap.clear();
	}

}
