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

package edu.stanford.smi.protegex.owl.server.triplestore;

import java.io.Serializable;
import java.util.List;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Localizable;
import edu.stanford.smi.protege.server.narrowframestore.RemoteServerNarrowFrameStore;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;

public class Package implements Localizable, Serializable {
    private static final long serialVersionUID = 6422510439112012894L;
    
    private List<RemoteServerNarrowFrameStore> frameStores;
    private List<NamespaceManager> namespaceManagers;
    private List<String> tripleStoreNames;
    private String activeTripleStore;
    private String systemTripleStore;
    
    public Package(List<RemoteServerNarrowFrameStore> frameStores,
                   List<NamespaceManager> namespaceManagers,
                   List<String>  tripleStoreNames,
                   String activeTripleStore,
                   String systemTripleStore) {
        this.frameStores = frameStores;
        this.namespaceManagers = namespaceManagers;
        this.tripleStoreNames = tripleStoreNames;
        this.activeTripleStore = activeTripleStore;
        this.systemTripleStore = systemTripleStore;
    }

    public List<RemoteServerNarrowFrameStore> getFrameStores() {
        return frameStores;
    }



    public List<NamespaceManager> getNamespaceManagers() {
        return namespaceManagers;
    }
    
    public List<String> getTripleStoreNames() {
        return tripleStoreNames;
    }
    
    public String getActiveTripleStore() {
        return activeTripleStore;
    }

    public String getSystemTripleStore() {
        return systemTripleStore;
    }

    public void localize(KnowledgeBase kb) {
        for (NamespaceManager ns : namespaceManagers) {
            LocalizeUtils.localize(ns, kb);
        }
    }
}
