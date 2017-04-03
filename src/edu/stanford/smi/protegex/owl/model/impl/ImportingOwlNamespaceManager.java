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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MultiMap;
import edu.stanford.smi.protege.util.SetMultiMap;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public class ImportingOwlNamespaceManager extends AbstractNamespaceManager {
    private static final long serialVersionUID = -5443609678477899634L;
    private static final transient Logger log = Log.getLogger(ImportingOwlNamespaceManager.class);
    
    private OWLModel owlModel;
    
    private boolean needsRebuild = true;
    
    // these  are only modified in checkDb
    private HashMap<String, String> prefix2namespaceMap = new HashMap<String, String>();
    private HashMap<String, String> namespace2prefixMap = new HashMap<String, String>();
    
    private Collection<String> unmodifiablePrefixes;

    public ImportingOwlNamespaceManager(OWLModel owlModel) {
        this.owlModel = owlModel;
    }
    
    @Override
    public void addImport(TripleStore imported) {
        needsRebuild = true;
        super.addImport(imported);
    }
    
    private void checkDb() {
        if (!needsRebuild)  {
            return;
        }
        TripleStore topTripleStore = owlModel.getTripleStoreModel().getTopTripleStore();
        
        prefix2namespaceMap = new HashMap<String, String>();
        namespace2prefixMap = new HashMap<String, String>();
        
        MultiMap<String, String> prefix2namespaceMultiMap = new SetMultiMap<String, String>();
        MultiMap<String, String> namespace2prefixMultiMap = new SetMultiMap<String, String>();
        for (TripleStore tripleStore : owlModel.getTripleStoreModel().getTripleStores()) {
            NamespaceManager names = tripleStore.getNamespaceManager();
            for (String prefix : names.getPrefixes()) {
                if (tripleStore != topTripleStore  && prefix.equals(DEFAULT_NAMESPACE_PREFIX)) {
                    continue;
                }
                String namespace = names.getNamespaceForPrefix(prefix);
                prefix2namespaceMultiMap.addValue(prefix, namespace);
                namespace2prefixMultiMap.addValue(namespace, prefix);
            }
        }
        NamespaceManager topNamespaceManager = topTripleStore.getNamespaceManager();
        Collection<String> topPrefixes = topNamespaceManager.getPrefixes();
        for (String prefix : topPrefixes) {
            String namespace = topNamespaceManager.getNamespaceForPrefix(prefix);
            prefix2namespaceMap.put(prefix, namespace);
            namespace2prefixMap.put(namespace, prefix);
        }
        Collection<String> otherPrefixes = prefix2namespaceMultiMap.getKeys();
        otherPrefixes.removeAll(topPrefixes);
        for (String prefix : otherPrefixes) {
            Collection<String> namespaces = prefix2namespaceMultiMap.getValues(prefix);
            if (namespaces == null || namespaces.size() != 1) {
                continue;
            }
            String namespace = namespaces.iterator().next();
            Collection<String> prefixes = namespace2prefixMultiMap.getValues(namespace);
            if (prefixes.size() != 1) {
                continue;
            }
            prefix2namespaceMap.put(prefix, namespace);
            namespace2prefixMap.put(namespace, prefix);
        }
        unmodifiablePrefixes = new HashSet<String>();
        for (String prefix : topNamespaceManager.getPrefixes()) {
            if (!topNamespaceManager.isModifiable(prefix)) {
                unmodifiablePrefixes.add(prefix);
            }
        }
        needsRebuild = false;
    }
    
    private void tellDiffs(Map<String, String> oldPrefix2namespaceMap,
                           Map<String, String> oldNamespace2prefixMap) {
        tellPrefixDiffs(oldPrefix2namespaceMap, oldNamespace2prefixMap);
        tellNamespaceDiffs(oldPrefix2namespaceMap, oldNamespace2prefixMap);
    }
    
    private void tellPrefixDiffs(Map<String, String> oldPrefix2namespaceMap,
                                 Map<String, String> oldNamespace2prefixMap) {
        Set<String> prefixes = new HashSet<String>();
        prefixes.addAll(oldPrefix2namespaceMap.keySet());
        prefixes.addAll(prefix2namespaceMap.keySet());
        for (String prefix : prefixes) {
            String oldNamespace = oldPrefix2namespaceMap.get(prefix);
            String newNamespace = prefix2namespaceMap.get(prefix);
            if (oldNamespace == null && newNamespace == null) {
                continue;
            }
            if (oldNamespace != null && oldNamespace.equals(newNamespace)) {
                continue;
            }
            tellNamespaceChanged(prefix, oldNamespace, newNamespace);
        }
    }
    
    private void tellNamespaceDiffs(Map<String, String> oldPrefix2namespaceMap,
                                    Map<String, String> oldNamespace2prefixMap) {
        Set<String> namespaces = new HashSet<String>();
        namespaces.addAll(oldNamespace2prefixMap.keySet());
        namespaces.addAll(namespace2prefixMap.keySet());
        for (String namespace : namespaces) {
            String oldPrefix = oldNamespace2prefixMap.get(namespace);
            String newPrefix = namespace2prefixMap.get(namespace);
            if (oldPrefix == null && newPrefix == null) {
                continue;
            }
            if (oldPrefix != null && oldPrefix.equals(newPrefix)) {
                continue;
            }
            tellPrefixChanged(namespace, oldPrefix, newPrefix);
        }
    }
    
    /*
     * Implementation of interfaces
     */
    
    public boolean isModifiable(String prefix) {
        checkDb();
        return !unmodifiablePrefixes.contains(prefix);
    }

    public void setModifiable(String prefix, boolean value) {
        checkDb();
        owlModel.getTripleStoreModel().getTopTripleStore().getNamespaceManager().setModifiable(prefix, value);
    }

    public String getNamespaceForPrefix(String prefix) {
        checkDb();
        return prefix2namespaceMap.get(prefix);
    }

    public String getPrefix(String namespace) {
        checkDb();
        return namespace2prefixMap.get(namespace);
    }

    public Collection<String> getPrefixes() {
        checkDb();
        return prefix2namespaceMap.keySet();
    }

    public void removePrefix(String prefix) {
        checkDb();
        Map<String, String> oldPrefix2namespaceMap = prefix2namespaceMap;
        Map<String, String> oldNamespace2prefixMap = namespace2prefixMap;
        needsRebuild=true;
        owlModel.getTripleStoreModel().getTopTripleStore().getNamespaceManager().removePrefix(prefix);
        checkDb();
        tellDiffs(oldPrefix2namespaceMap, oldNamespace2prefixMap);
    }

    public void setPrefix(String namespace, String prefix) {
        checkDb();
        Map<String, String> oldPrefix2namespaceMap = prefix2namespaceMap;
        Map<String, String> oldNamespace2prefixMap = namespace2prefixMap;
        needsRebuild=true;
        owlModel.getTripleStoreModel().getTopTripleStore().getNamespaceManager().setPrefix(namespace, prefix);
        checkDb();
        tellDiffs(oldPrefix2namespaceMap, oldNamespace2prefixMap);
    }

}
