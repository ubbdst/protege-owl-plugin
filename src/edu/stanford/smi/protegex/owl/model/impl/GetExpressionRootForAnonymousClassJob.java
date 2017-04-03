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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFProperty;


public class GetExpressionRootForAnonymousClassJob extends ProtegeJob {
  
    private static final long serialVersionUID = 6042855610878771146L;
    
    private OWLAnonymousClass anonClass;
    
    public GetExpressionRootForAnonymousClassJob(KnowledgeBase kb, OWLAnonymousClass anonClass) {
        super(kb);
        this.anonClass = anonClass;
    }

    @Override
    public Object run() throws ProtegeException {
        final RDFProperty rdfFirstProperty = ((OWLModel)getKnowledgeBase()).getRDFFirstProperty();
        Collection refs = anonClass.getReferences();
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (reference.getFrame() instanceof OWLAnonymousClass) {
                OWLAnonymousClass reverend = (OWLAnonymousClass) reference.getFrame();
                return reverend.getExpressionRoot();
            }
            else if (reference.getFrame() instanceof RDFList && rdfFirstProperty.equals(reference.getSlot())) {
                RDFList list = (RDFList) reference.getFrame();
                RDFList start = list.getStart();
                Set set = new HashSet();
                OWLUtil.getReferringLogicalClasses(start, set);
                if (set.size() > 0) {
                    OWLAnonymousClass reverend = (OWLAnonymousClass) set.iterator().next();
                    return reverend.getExpressionRoot();
                }
            }
        }
        return anonClass;
    }
    
    @Override
    public void localize(KnowledgeBase kb) {     
        super.localize(kb);
        LocalizeUtils.localize(anonClass, kb);
    }

}
