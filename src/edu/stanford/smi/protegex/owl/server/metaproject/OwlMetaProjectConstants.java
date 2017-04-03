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

package edu.stanford.smi.protegex.owl.server.metaproject;

import edu.stanford.smi.protege.server.metaproject.Operation;
import edu.stanford.smi.protege.server.metaproject.impl.UnbackedOperationImpl;

public class OwlMetaProjectConstants {
    public final static Operation SET_ACTIVE_IMPORT   = new  UnbackedOperationImpl("SetActiveImport", null);
    public final static Operation USE_OWL_CLASSES_TAB = new UnbackedOperationImpl("UseOwlClassesTab", null);
    public final static Operation USE_PROPERTY_TAB    = new UnbackedOperationImpl("UsePropertiesTab", null);
    
    public final static Operation OPERATION_PROPERTY_TAB_WRITE = new UnbackedOperationImpl("PropertyTabWrite", null);

    public final static Operation OPERATION_ONTOLOGY_TAB_WRITE = new UnbackedOperationImpl("OntologyTabWrite", null);
}
