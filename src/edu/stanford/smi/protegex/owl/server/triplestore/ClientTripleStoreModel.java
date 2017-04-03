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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.narrowframestore.RemoteClientInvocationHandler;
import edu.stanford.smi.protege.server.narrowframestore.RemoteServerNarrowFrameStore;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.UnmodifiableNamespaceManager;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.TripleStoreImpl;
import edu.stanford.smi.protegex.owl.util.OWLFrameStoreUtils;

public class ClientTripleStoreModel implements TripleStoreModel {
    private  OWLModel owlModel;
    private Map<String, TripleStore> tripleStores = new LinkedHashMap<String, TripleStore>();
    private String activeTripleStoreName;
    private String systemTripleStoreName;

    public ClientTripleStoreModel(OWLModel owlModel) {
        this.owlModel = owlModel;
        initialize();
    }

    private void initialize() {
        initialize(getRemoteSession());
    }

    private RemoteSession getRemoteSession() {
        RemoteClientFrameStore frameStore = owlModel.getFrameStoreManager().getFrameStoreFromClass(RemoteClientFrameStore.class);
        return frameStore.getSession();
    }

    private void initialize(RemoteSession  session) {
        Package p = (Package) new GetPackage(owlModel).execute();
        activeTripleStoreName = p.getActiveTripleStore();
        systemTripleStoreName = p.getSystemTripleStore();
        for (int i = 0; i < p.getFrameStores().size(); i++) {
            RemoteServerNarrowFrameStore remoteNarrowFrameStore = p.getFrameStores().get(i);
            NamespaceManager namespaceManager = p.getNamespaceManagers().get(i);
            namespaceManager = new UnmodifiableNamespaceManager(namespaceManager);
            String name = p.getTripleStoreNames().get(i);

            NarrowFrameStore narrowFrameStore = new RemoteClientInvocationHandler(owlModel, remoteNarrowFrameStore, session).getNarrowFrameStore();
            TripleStore tripleStore = new TripleStoreImpl(owlModel, narrowFrameStore ,this, namespaceManager, name);
            tripleStores.put(name, tripleStore);
        }
    }


    public TripleStore createActiveImportedTripleStore(NarrowFrameStore frameStore) {
        throw new UnsupportedOperationException();
    }

    public void deleteTripleStore(TripleStore tripleStore) {
        throw new UnsupportedOperationException();
    }

    public void dispose() {
        ;
    }

    public TripleStore getActiveTripleStore() {
        return tripleStores.get(activeTripleStoreName);
    }

    public TripleStore getHomeTripleStore(RDFResource resource) {
        return getActiveTripleStore();
    }

	public TripleStore getHomeTripleStore(Instance subject, Slot predicate,	Object object) {
		throw new UnsupportedOperationException();
	}

    public Collection getPropertyValues(RDFResource resource,
                                        RDFProperty property) {
        Collection values = ((KnowledgeBase) owlModel).getOwnSlotValues(resource, property);
        return OWLFrameStoreUtils.convertValueListToRDFLiterals(owlModel, values);
    }

    public Collection getSlotValues(Instance instance, Slot slot) {
        return ((KnowledgeBase) owlModel).getOwnSlotValues(instance, slot);
    }

    public TripleStore getSystemTripleStore() {
        return tripleStores.get(systemTripleStoreName);
    }

    public TripleStore getTopTripleStore() {
        return getActiveTripleStore();
    }

    public TripleStore getTripleStore(String name) {
        return tripleStores.get(name);
    }

    public TripleStore getTripleStoreByDefaultNamespace(String namespace) {
        return tripleStores.get(namespace);
    }

    public List<TripleStore> getTripleStores() {
        return new ArrayList(tripleStores.values());
    }

    public boolean isActiveTriple(RDFResource subject, RDFProperty predicate,
                                  Object object) {
        return getActiveTripleStore().contains(subject, predicate, object);
    }

    public boolean isEditableTriple(RDFResource subject, RDFProperty predicate,
                                    Object object) {
        return isActiveTriple(subject, predicate, object);
    }

    public boolean isEditableTripleStore(TripleStore ts) {
        return ts.equals(getActiveTripleStore());
    }

    public Iterator<RDFResource> listSubjects(RDFProperty property) {
        throw new UnsupportedOperationException();
    }

    public Iterator<Triple> listTriplesWithSubject(RDFResource subject) {
        throw new UnsupportedOperationException();
    }

    public Iterator<TripleStore> listUserTripleStores() {
    	List<TripleStore> tripleStores = getTripleStores();    	
        Iterator<TripleStore> it = tripleStores.iterator();
        it.next(); // drop the system triple store.
        if (tripleStores.size() > 1 && tripleStores.get(1).getName() == null) {
            it.next();  // this is really funky - owl db = drop the extra triple store
        }
        return it;
    }
    
    public void replaceJavaObject(RDFResource subject) {
        throw new UnsupportedOperationException();
    }

    public void setActiveTripleStore(TripleStore tripleStore) {
        activeTripleStoreName = tripleStore.getName();
    }

    public void setHomeTripleStore(RDFResource resource, TripleStore tripleStore) {
        throw new UnsupportedOperationException();
    }

    public void setTopTripleStore(TripleStore tripleStore) {
        throw new UnsupportedOperationException();
    }

    public void setViewActiveOnly(boolean viewActiveOnly) {
        throw new UnsupportedOperationException();
    }

    public void updateEditableResourceState() {
        ;
    }

}






