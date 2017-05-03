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

import java.util.WeakHashMap;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;

public class CheckSetActiveImportAllowedJob extends ProtegeJob {
    private static final long serialVersionUID = 7234800355325524952L;
    
    private OWLOntology ontology;
    private static WeakHashMap<OWLOntology, Boolean> cachedResults = new WeakHashMap<OWLOntology, Boolean>();
    
    public static String ALLOW_MULTIUSER_SET_ACTIVE_ONTOLOGY = "allow.multiuser.client.set.active.ontology";

    public CheckSetActiveImportAllowedJob(OWLOntology ontology) {
        super(ontology.getOWLModel());
        this.ontology = ontology;
    }
    
    @Override
    public Boolean run() throws ProtegeException {
        if (getKnowledgeBase().getProject().isMultiUserServer() &&
                (!ApplicationProperties.getBooleanProperty(ALLOW_MULTIUSER_SET_ACTIVE_ONTOLOGY, false) ||
                        !serverSideCheckOperationAllowed(OwlMetaProjectConstants.SET_ACTIVE_IMPORT))) {
            return false;
        }
        return ((DefaultOWLOntology) ontology).isAssociatedTriplestoreEditable();
    }
    
    @Override
    public Boolean execute() throws ProtegeException {
        Boolean result = cachedResults.get(ontology);
        if (result == null) {
            result = (Boolean) super.execute();
            cachedResults.put(ontology, result);
        }
        return result;
    }
    
    @Override
    public void localize(KnowledgeBase kb) {
        super.localize(kb);
        if (ontology != null) {
            LocalizeUtils.localize(ontology, kb);
        }
    }

}
