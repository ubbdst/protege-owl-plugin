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

package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFrameStoreManager extends FrameStoreManager {    
    private OWLModel  owlModel;
    
    private OWLFrameStore owlFrameStore;
    private DuplicateValuesFrameStore duplicateValuesFrameStore;
    private DomainUpdateFrameStore domainUpdateFrameStore;
    private FacetUpdateFrameStore facetUpdateFrameStore;
    private RangeUpdateFrameStore rangeUpdateFrameStore;
    private OwlSubclassFrameStore owlSubclassFrameStore;
    private TypeUpdateFrameStore typeUpdateFrameStore;
    private LocalClassificationFrameStore localClassificationFrameStore;
    
    private List<FrameStore> frameStores = new ArrayList<FrameStore>();

    public OWLFrameStoreManager(OWLModel owlModel) {
        super(owlModel);
        this.owlModel = owlModel;
        initializeOwlFrameStores();
    }
    
    private void initializeOwlFrameStores() {
    	// Core Owl Frame Stores
        addFrameStore(owlFrameStore = new OWLFrameStore((AbstractOWLModel) owlModel));
        addFrameStore(duplicateValuesFrameStore = new DuplicateValuesFrameStore());
        addFrameStore(domainUpdateFrameStore = new DomainUpdateFrameStore(owlModel));
        addFrameStore(facetUpdateFrameStore = new FacetUpdateFrameStore(owlModel));
        addFrameStore(rangeUpdateFrameStore = new RangeUpdateFrameStore(owlModel));
        addFrameStore(owlSubclassFrameStore = new OwlSubclassFrameStore(owlModel));
        addFrameStore(typeUpdateFrameStore = new TypeUpdateFrameStore(owlModel));  // this goes near the end so that the others see the swizzle.
        
        for (FrameStore fs : frameStores) {
            insertFrameStore(fs);
        }
        int lastPostion = getFrameStores().size() - 1;
        insertFrameStore(localClassificationFrameStore = new LocalClassificationFrameStore(owlModel), 
        				 lastPostion);
    }
    
    /*
     * add the frame stores in reverse order
     */
    private void addFrameStore(FrameStore fs) {
        frameStores.add(0, fs);
    }
    

    @Override
    protected FrameStore create(Class clas) {
        if (clas == DeleteSimplificationFrameStore.class) {
            return new OWLDeleteSimplificationFrameStore();
        }
        else {
            return super.create(clas);
        }
    }
    
    public void setOwlFrameStoresEnabled(boolean enabled) {
        for (FrameStore fs : frameStores) {
            setEnabled(fs, enabled);
        }
    }
    
    
    /*
     * getters
     */
    
    public OWLFrameStore getOWLFrameStore() {
        return owlFrameStore;
    }
    
    public DuplicateValuesFrameStore getDuplicateValuesFrameStore() {
        return duplicateValuesFrameStore;
    }    

    public DomainUpdateFrameStore getDomainUpdateFrameStore() {
        return domainUpdateFrameStore;
    }

    public FacetUpdateFrameStore getFacetUpdateFrameStore() {
        return facetUpdateFrameStore;
    }
    
    public RangeUpdateFrameStore getRangeUpdateFrameStore() {
        return rangeUpdateFrameStore;
    }

    public OwlSubclassFrameStore getOwlSubclassFrameStore() {
        return owlSubclassFrameStore;
    }

    public TypeUpdateFrameStore getTypeUpdateFrameStore() {
        return typeUpdateFrameStore;
    }

    public LocalClassificationFrameStore getLocalClassificationFrameStore() {
        return localClassificationFrameStore;
    }    
}
