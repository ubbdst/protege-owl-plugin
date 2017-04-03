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

package edu.stanford.smi.protegex.owl.ui.actions.triple;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeleteTripleAction extends AbstractTripleAction {

    public DeleteTripleAction() {
        super("Delete property value", OWLIcons.DELETE, OWLIcons.class);
    }


    public boolean isSuitable(Triple triple) {
        return isSuitable(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    /* 
     * WARNING!
     *    See OWLModel.getProtegeReadOnlyProperty javadoc for explanation of protege:readOnly property.
     */
    public static boolean isSuitable(RDFResource subject, RDFProperty predicate, Object object) {
        if (!predicate.isReadOnly()) {
            if (object instanceof RDFResource) {
                RDFResource resource = (RDFResource) object;
                if (resource.isAnonymous()) {
                    return false;
                }
            }
            TripleStoreModel tsm = predicate.getOWLModel().getTripleStoreModel();
            if (tsm.isEditableTriple(subject, predicate, object)) {
                if (object instanceof RDFSNamedClass &&
                        (predicate.equals(predicate.getOWLModel().getRDFSSubClassOfProperty()) ||
                                predicate.equals(predicate.getOWLModel().getRDFTypeProperty()))) {
                    int count = getNamedClassValues(subject, predicate);
                    if (count == 1) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }


    private static int getNamedClassValues(RDFResource subject, RDFProperty property) {
        int count = 0;
        Iterator it = subject.listPropertyValues(property);
        while (it.hasNext()) {
            if (it.next() instanceof RDFSNamedClass) {
                count++;
            }
        }
        return count;
    }


    public void run(Triple triple) {
        triple.getSubject().removePropertyValue(triple.getPredicate(), triple.getObject());
    }
}
