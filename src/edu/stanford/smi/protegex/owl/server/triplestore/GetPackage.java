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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.server.narrowframestore.RemoteServerNarrowFrameStore;
import edu.stanford.smi.protege.server.narrowframestore.ServerNarrowFrameStore;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

public class GetPackage extends ProtegeJob {
    private static final long serialVersionUID = 6836555738304528191L;

    public GetPackage(OWLModel owlModel) {
        super(owlModel);
    }
    
    @Override
    public OWLModel getKnowledgeBase() {
        return (OWLModel) super.getKnowledgeBase();
    }
    
    @Override
    public Object run() throws ProtegeException {
        try {
            List<RemoteServerNarrowFrameStore> frameStores = new ArrayList<RemoteServerNarrowFrameStore>();
            List<NamespaceManager> namespaceManagers = new ArrayList<NamespaceManager>();
            List<String> tripleStoreNames = new ArrayList<String>();
            String activeTripleStore;
            String systemTripleStore;
            
            TripleStoreModel tripleStoreModel = getKnowledgeBase().getTripleStoreModel();
            activeTripleStore = tripleStoreModel.getActiveTripleStore().getName();
            systemTripleStore = tripleStoreModel.getSystemTripleStore().getName();
            for (TripleStore tripleStore : tripleStoreModel.getTripleStores()) {
                NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
                RemoteServerNarrowFrameStore remoteNarrowFrameStore = new ServerNarrowFrameStore(nfs, getKnowledgeBase());
                // remoteNarrowFrameStore = (RemoteServerNarrowFrameStore) UnicastRemoteObject.exportObject(remoteNarrowFrameStore);

                frameStores.add(remoteNarrowFrameStore);
                namespaceManagers.add(tripleStore.getNamespaceManager());
                tripleStoreNames.add(tripleStore.getName());
            }
            return new Package(frameStores, namespaceManagers, tripleStoreNames, activeTripleStore, systemTripleStore);
        }
        catch (RemoteException re) {
            throw new ProtegeException(re);
        }
    }
}
