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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 8, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class XMLWriterNamespaceManager {

    private Map<String, String> prefixNamespaceMap;

    private Map<String, String> namespacePrefixMap;

    private String defaultNamespace;


    public XMLWriterNamespaceManager(String defaultNamespace) {
        prefixNamespaceMap = new HashMap<String, String>();
        namespacePrefixMap = new HashMap<String, String>();
        this.defaultNamespace = defaultNamespace;
    }


    public void setPrefix(String prefix, String namespace) {
        prefixNamespaceMap.put(prefix, namespace);
        namespacePrefixMap.put(namespace, prefix);
    }


    public String getPrefixForNamespace(String namespace) {
        return namespacePrefixMap.get(namespace);
    }


    public String getNamespaceForPrefix(String prefix) {
        return (String) prefixNamespaceMap.get(prefix);
    }


    public void createPrefixForNamespace(String namespace) {
        if (namespacePrefixMap.containsKey(namespace) == false) {
            int counter = 1;
            while (prefixNamespaceMap.get("p" + counter) != null) {
                counter++;
            }
            setPrefix("p" + counter, namespace);
        }
    }


    public String getDefaultNamespace() {
        return defaultNamespace;
    }


    public Collection<String> getPrefixes() {
        return new ArrayList<String>(prefixNamespaceMap.keySet());
    }


    public Collection<String> getNamespaces() {
        return new ArrayList<String>(namespacePrefixMap.keySet());
    }
}

