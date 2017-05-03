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

package edu.stanford.smi.protegex.owl.jena.graph;

import com.hp.hpl.jena.shared.PrefixMapping;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegePrefixMapping implements PrefixMapping {

    private OWLModel owlModel;

    private TripleStore ts;


    public ProtegePrefixMapping(OWLModel owlModel, TripleStore ts) {
        this.owlModel = owlModel;
        this.ts = ts;
    }


    public PrefixMapping setNsPrefix(String string, String string1) {
        throw new RuntimeException("Not supported yet");
    }


    public PrefixMapping removeNsPrefix(String string) {
        throw new RuntimeException("Not supported yet");
    }


    public PrefixMapping setNsPrefixes(PrefixMapping prefixMapping) {
        throw new RuntimeException("Not supported yet");
    }


    public PrefixMapping setNsPrefixes(Map map) {
        throw new RuntimeException("Not supported yet");
    }


    public PrefixMapping withDefaultMappings(PrefixMapping prefixMapping) {
        return this;
    }


    public String getNsPrefixURI(String prefix) {
        if (prefix.length() == 0) {
            return ts.getDefaultNamespace();
        }
        else {
            return ts.getNamespaceForPrefix(prefix);
        }
    }


    public String getNsURIPrefix(String uri) {
        return ts.getPrefix(uri);
    }


    public Map getNsPrefixMap() {
        Map map = new HashMap();
        for (Iterator it = ts.getPrefixes().iterator(); it.hasNext();) {
            String prefix = (String) it.next();
            String uri = ts.getNamespaceForPrefix(prefix);
            map.put(prefix, uri);
        }
        map.put("", ts.getDefaultNamespace());
        return map;
    }


    public String expandPrefix(String qname) {
        String prefix = owlModel.getPrefixForResourceName(qname);
        if (prefix == null) {
            prefix = "";
        }
        String namespace = owlModel.getNamespaceManager().getNamespaceForPrefix(prefix);
        if (namespace != null) {
            String uri = owlModel.getURIForResourceName(qname);
            if (uri != null) {
                return uri;
            }
        }
        return qname;
    }


    public String shortForm(String uri) {
        String namespace = owlModel.getNamespaceForURI(uri);
        if (namespace != null) {
            String prefix = owlModel.getNamespaceManager().getPrefix(namespace);
            if (prefix != null) {
                String localName = owlModel.getLocalNameForURI(uri);
                if (localName != null) {
                    if (prefix.length() > 0) {
                        return prefix + ProtegeNames.PREFIX_LOCALNAME_SEPARATOR + localName;
                    }
                    else {
                        return localName;
                    }
                }
            }
        }
        return uri;
    }


    public String usePrefix(String string) {
        throw new RuntimeException("Not supported yet");
    }


    public String qnameFor(String string) {
        throw new RuntimeException("Not supported yet");
    }


    public PrefixMapping lock() {
        throw new RuntimeException("Not supported yet");
    }


    public boolean samePrefixMappingAs(PrefixMapping arg0) {
      throw new UnsupportedOperationException("Not implemented yet");
    }
}
