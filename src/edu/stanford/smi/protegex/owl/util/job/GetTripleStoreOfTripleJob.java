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

package edu.stanford.smi.protegex.owl.util.job;

import java.util.Iterator;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

/*
 * This is  a little twisted for a protege job - it does more processing
 * in execute than expected  and execute returns a different type than run.
 */

public class GetTripleStoreOfTripleJob extends ProtegeJob {
    private static final long serialVersionUID = -406814600433675121L;

    private RDFResource subject;
    private Slot slot;
    private Object object;
    
    public GetTripleStoreOfTripleJob(RDFResource subject, Slot slot, Object object) {
        super(subject.getOWLModel());
        this.subject = subject;
        this.slot = slot;
        this.object = object;
    }

    @Override
    public OWLModel getKnowledgeBase() {
        return (OWLModel) super.getKnowledgeBase();
    }
    /*
     * we had a problem with generics and rmi so we don't use them.
     */
    
    @Override
    public String run() throws ProtegeException {
        TripleStore tripleStore = getTripleStore();
        if (tripleStore != null) {
            return tripleStore.getName();
        }
        else {
            return null;
        }
    }
    
    private TripleStore getTripleStore() {
        if (object instanceof RDFSLiteral) {
            object = ((DefaultRDFSLiteral) object).getRawValue();
        }
        OWLModel owlModel = subject.getOWLModel();
        Iterator<TripleStore> it = owlModel.getTripleStoreModel().getTripleStores().iterator();
        while (it.hasNext()) {
            TripleStore ts = it.next();
            if (ts.getNarrowFrameStore().getValues(subject, slot, null, false).contains(object)) {
                return ts;
            }
        }
        return null;
    }
    
    @Override
    public TripleStore execute() throws ProtegeException {
        if  (!getKnowledgeBase().getProject().isMultiUserClient()) {
            return getTripleStore();
        }
        else {
            String tripleStoreName = (String) super.execute();
            for (TripleStore tripleStore : getKnowledgeBase().getTripleStoreModel().getTripleStores()) {
                if (tripleStore.getName().equals(tripleStoreName)) {
                    return tripleStore;
                }
            }
            return null;
        }
    }
    
    @Override
    public void localize(KnowledgeBase kb) {
        super.localize(kb);
        LocalizeUtils.localize(subject, kb);
        LocalizeUtils.localize(slot, kb);
        LocalizeUtils.localize(object, kb);
    }

}