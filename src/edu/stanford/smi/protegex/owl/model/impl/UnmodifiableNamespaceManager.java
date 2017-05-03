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

import java.net.URI;
import java.util.Collection;

import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

public class UnmodifiableNamespaceManager implements NamespaceManager {
    private NamespaceManager manager;
    
    public UnmodifiableNamespaceManager(NamespaceManager manager) {
        this.manager = manager;
    }

    public void addNamespaceManagerListener(NamespaceManagerListener listener) {
    }

    public void init(OWLModel owlModel) {
    }

    public boolean isModifiable(String prefix) {
        return false;
    }

    public void removeNamespaceManagerListener(NamespaceManagerListener listener) {
    }

    public void setModifiable(String prefix, boolean value) {
        throw new UnsupportedOperationException();
    }

    public String getDefaultNamespace() {
        return manager.getDefaultNamespace();
    }

    public String getNamespaceForPrefix(String prefix) {
        return manager.getNamespaceForPrefix(prefix);
    }

    public String getPrefix(String namespace) {
        return manager.getPrefix(namespace);
    }

    public Collection<String> getPrefixes() {
        return manager.getPrefixes();
    }

    public void removePrefix(String prefix) {
        throw new UnsupportedOperationException();
    }

    public void setDefaultNamespace(String value) {
        throw new UnsupportedOperationException();
    }

    public void setDefaultNamespace(URI uri) {
        throw new UnsupportedOperationException();
    }

    public void setPrefix(String namespace, String prefix) {
        throw new UnsupportedOperationException();
    }

    public void setPrefix(URI namespace, String prefix) {
        throw new UnsupportedOperationException();
    }

    public void addImport(TripleStore imported) {
    }

}
