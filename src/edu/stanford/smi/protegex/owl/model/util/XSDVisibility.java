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

package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A class that manages the (in) visibility of RDFSDatatypes, depending
 * on their usage.  Default datatypes (xsd:int etc) are always visible
 * unless the user has explicitly made them invisible, and other datatypes
 * are visible if they are used (have References to them).
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class XSDVisibility {

    public static Set<RDFSDatatype> getDefaultDatatypes(OWLModel owlModel) {
        Set<RDFSDatatype> set = new HashSet<RDFSDatatype>();
        set.add(owlModel.getXSDboolean());
        set.add(owlModel.getXSDfloat());
        set.add(owlModel.getXSDint());
        set.add(owlModel.getXSDstring());
        set.add(owlModel.getXSDdate());
        set.add(owlModel.getXSDdateTime());
        set.add(owlModel.getXSDtime());
        return set;
    }


    public static void updateVisibility(OWLModel owlModel) {
        Set<RDFSDatatype> defaultDatatypes = getDefaultDatatypes(owlModel);
        Iterator<RDFSDatatype> types = owlModel.getRDFSDatatypes().iterator();
        while (types.hasNext()) {
            RDFSDatatype datatype = types.next();
            if (datatype.isSystem() && !defaultDatatypes.contains(datatype)) {
                Collection<Reference> refs = ((KnowledgeBase) owlModel).getReferences(datatype, 10);
                boolean visible = false;
                for (Reference ref : refs) {
                    if (ref.getFrame().isSystem() 
                            && ref.getSlot().isSystem() 
                            && (ref.getFacet() == null || ref.getFacet().isSystem())) {
                        continue;
                    }
                    else {
                        visible = true;
                        break;
                    }
                }
                datatype.setVisible(visible);
            }
        }
    }
}
