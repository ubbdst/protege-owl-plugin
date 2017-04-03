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

/**
 * 
 */
package edu.stanford.smi.protegex.owl.util.job;

import java.util.Collection;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

public class GetIsIncludedJob extends ProtegeJob {
    private RDFResource resource;
    
    /*
     * I had some mysterious trouble with generics and rmi which is why the 
     * Protege Job does not work that way.
     */
    
    public GetIsIncludedJob(RDFResource resource) {
        super(resource.getOWLModel());
        this.resource = resource;
    }
    
    private static final long serialVersionUID = -8370182309400148050L;

    @Override
    public Boolean run() throws ProtegeException {
        OWLModel owlModel = (OWLModel) getKnowledgeBase();
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        NarrowFrameStore activeNfs = tripleStoreModel.getActiveTripleStore().getNarrowFrameStore();
        MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(owlModel);
        for (NarrowFrameStore nfs : mnfs.getAvailableFrameStores()) {
            if (nfs.equals(activeNfs)) {
                continue;
            }
            Collection names = nfs.getValues(resource, owlModel.getSystemFrames().getNameSlot(), null, false);
            if (names != null && names.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public Boolean execute() {
        return (Boolean) super.execute();
    }
    
    @Override
    public void localize(KnowledgeBase kb) {
        super.localize(kb);
        LocalizeUtils.localize(resource, kb);
    }
}