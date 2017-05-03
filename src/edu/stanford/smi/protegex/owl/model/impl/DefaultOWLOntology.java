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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFExternalResource;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * The default implementation of the OWLObjectProperty interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLOntology extends DefaultRDFIndividual implements OWLOntology {
    private static  final Logger log = Log.getLogger(DefaultOWLOntology.class);

    public DefaultOWLOntology(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLOntology() {
    }


    @Override
    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLOntology(this);
    }


    public void addBackwardCompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        addOwnSlotValue(slot, resource);
    }


    /**
     * Imprtant - for usage see <CODE>OWLOntology</CODE>
     * 
     * @param uri
     */
    public void addImports(String uri) {
        Frame ontology = getKnowledgeBase().getFrame(uri);
        if (ontology  == null) {
            // Other developers have asked "why use untyped resources?"
            // the answer is that the owl spec is broken with regard to ontology declarations
            // if there is only one ontology declaration in a file everything is ok but otherwise things become ill-defined
            ontology = getOWLModel().createRDFUntypedResource(uri);
        }
        if (ontology != null 
                && (ontology instanceof RDFExternalResource || ontology instanceof RDFUntypedResource || ontology instanceof OWLOntology)) {
            addImportsHelper(ontology);
        }
        else {
          log.warning("could not add import " + uri + "to the import tree.");  
        }
    }

    /*
     * Depends on addImports(String) which is known to be wrong.
     */
    public void addImports(URI uri) {
        addImports(uri.toString());
    }


    public void addImports(RDFExternalResource resource) {
        addImportsHelper(resource);
    }


    public void addImports(RDFUntypedResource resource) {
        addImportsHelper(resource);
    }


    public void addImports(OWLOntology ontology) {
        addImportsHelper(ontology);
    }


    private void addImportsHelper(Object resource) {
        Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.IMPORTS);
        addOwnSlotValue(slot, resource);
    }


    public void addIncompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.INCOMPATIBLE_WITH);
        addOwnSlotValue(slot, resource);
    }


    public void addPriorVersion(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.PRIOR_VERSION);
        addOwnSlotValue(slot, resource);
    }


    @Override
    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof OWLOntology) {
            OWLOntology ontology = (OWLOntology) object;
            return getURI().equals(ontology.getURI());
        }
        return false;
    }


    public Collection getBackwardCompatibleWith() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        return getOwnSlotValues(slot);
    }


    @Override
    public String getBrowserText() {
        String uri = getOntologyURI();
        if (uri == null) {
            return "DefaultOntology";
        }
        else {
            return "Ontology(" + uri + ")";
        }
    }


    @Override
    public Icon getIcon() {
        Icon icon = OWLIcons.getImageIcon("OWLOntology");
        if (!isAssociatedTriplestoreEditable() || !isActive()) {
            icon = OWLIcons.getReadOnlyIcon((ImageIcon) icon,
                                            OWLIcons.RDF_INDIVIDUAL_FRAME);
        }
        return icon;
    }


    @SuppressWarnings("unchecked")
    public Collection<String> getImports() {
        Collection resources = getImportResources();
        List<String> results = new ArrayList<String>();
        for (Iterator it = resources.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFResource) {
            	results.add(((RDFResource) o).getURI());
            }
        }
        return results;
    }


    public Collection getImportResources() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.IMPORTS);
        return getOwnSlotValues(slot);
    }


    public Collection getIncompatibleWith() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.INCOMPATIBLE_WITH);
        return getOwnSlotValues(slot);
    }


    public String getOntologyURI() {
        return getURI();
    }


    public Collection getPriorVersions() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.PRIOR_VERSION);
        return getOwnSlotValues(slot);
    }


    public void removeBackwardCompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        removeOwnSlotValue(slot, resource);
    }


    public void removeImports(String uri) {
        Object removeValue = null;
        Collection values = getImportResources();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            String otherURI = ((RDFResource) o).getURI();
            if (otherURI.equals(uri)) {
                removeValue = o;
                break;
            }
        }
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.IMPORTS);
        removeOwnSlotValue(slot, removeValue);
    }


    public void removeIncompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.INCOMPATIBLE_WITH);
        removeOwnSlotValue(slot, resource);
    }


    public void removePriorVersion(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.PRIOR_VERSION);
        removeOwnSlotValue(slot, resource);
    }

    public boolean isAssociatedTriplestoreEditable() {
        boolean result = false;
        OWLModel owlModel = getOWLModel();
        TripleStore top = owlModel.getTripleStoreModel().getTopTripleStore();
        if (this.equals(top.getOWLOntology())) {
            result = true;
        }
        else {
            RepositoryManager man = owlModel.getRepositoryManager();
            URI ontURI = null;
            try {
                ontURI = new URI(getURI());
                Repository rep = man.getRepository(ontURI);
                if (rep != null) {
                    result = rep.isWritable(ontURI);
                }
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean isActive() {
        TripleStore active = getOWLModel().getTripleStoreModel().getActiveTripleStore();
        return this.equals(active.getOWLOntology());
    }
    
    @Override
    public String toString() {
    	return "OWLOntology(" + getName() + ")";
    }
}
