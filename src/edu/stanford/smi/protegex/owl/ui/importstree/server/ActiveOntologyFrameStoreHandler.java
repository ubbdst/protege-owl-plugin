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

package edu.stanford.smi.protegex.owl.ui.importstree.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.framestore.AbstractFrameStoreInvocationHandler;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.query.Query;
import edu.stanford.smi.protege.model.query.QueryCallback;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.transaction.TransactionMonitor;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

public class ActiveOntologyFrameStoreHandler extends
        AbstractFrameStoreInvocationHandler {
    private static Logger log = Log.getLogger(ActiveOntologyFrameStoreHandler.class);
    private OWLModel owlModel;
    private Map<RemoteSession, NarrowFrameStore> activeFrameStoreMap = new HashMap<RemoteSession, NarrowFrameStore>();
    private NarrowFrameStore topFrameStore;
    private MergingNarrowFrameStore mnfs;
    
    public ActiveOntologyFrameStoreHandler(OWLModel owlModel) {
        this.owlModel = owlModel;
        topFrameStore = owlModel.getTripleStoreModel().getTopTripleStore().getNarrowFrameStore();
        mnfs = MergingNarrowFrameStore.get(owlModel);
    }
      
    public void setActiveOntology(OWLOntology ontology) {
        TransactionMonitor tm = getDelegate().getTransactionStatusMonitor();
        if (tm != null && tm.inTransaction()) {
            throw new IllegalStateException("Can't change the active ontology while in a transaction");
        }
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        TripleStore tripleStore = tripleStoreModel.getHomeTripleStore(ontology);
        NarrowFrameStore activeFrameStore = tripleStore.getNarrowFrameStore();
        if (activeFrameStore.equals(topFrameStore)) {
            activeFrameStoreMap.remove(ServerFrameStore.getCurrentSession());
        }
        else {
            activeFrameStoreMap.put(ServerFrameStore.getCurrentSession(), activeFrameStore);
        }
    }
    
    private static Set<Method> transactionMethods = new HashSet<Method>();
    static {
        try {
            transactionMethods.add(FrameStore.class.getMethod("beginTransaction", new Class[] { String.class }));
            transactionMethods.add(FrameStore.class.getMethod("commitTransaction", new Class[] { }));
            transactionMethods.add(FrameStore.class.getMethod("rollbackTransaction", new Class[] { }));
        }
        catch (NoSuchMethodException nsme) {
            log.severe("Could not install active ontology configurtion on the server.");
            throw new RuntimeException(nsme);
        }
    };

    @Override
    protected void executeQuery(Query q, QueryCallback qc) {
        getDelegate().executeQuery(q, qc);
    }

    @Override
    protected Object handleInvoke(Method method, Object[] args) {
        NarrowFrameStore activeFrameStore = activeFrameStoreMap.get(ServerFrameStore.getCurrentSession());
        if (isQuery(method) || activeFrameStore == null) {
            return invoke(method, args);
        }
        else if (transactionMethods.contains(method)) {
            mnfs.setActiveFrameStore(topFrameStore);
            return invoke(method, args);
        }
        else {
            try {
                mnfs.setActiveFrameStore(activeFrameStore);
                return invoke(method, args);
            }
            finally {
                mnfs.setActiveFrameStore(topFrameStore);
            }
        }
    }

}
