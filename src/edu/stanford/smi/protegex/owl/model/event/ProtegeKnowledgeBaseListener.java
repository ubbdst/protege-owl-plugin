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

package edu.stanford.smi.protegex.owl.model.event;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ProtegeKnowledgeBaseListener extends KnowledgeBaseListener {

    /**
     * @see ModelListener#classCreated
     * @deprecated
     */
    void clsCreated(KnowledgeBaseEvent event);


    /**
     * @see ModelListener#classDeleted
     * @deprecated
     */
    void clsDeleted(KnowledgeBaseEvent event);


    /**
     * @deprecated not needed
     */
    void defaultClsMetaClsChanged(KnowledgeBaseEvent event);


    /**
     * @deprecated not needed
     */
    void defaultFacetMetaClsChanged(KnowledgeBaseEvent event);


    /**
     * @deprecated not needed
     */
    void defaultSlotMetaClsChanged(KnowledgeBaseEvent event);


    /**
     * @deprecated not needed
     */
    void facetCreated(KnowledgeBaseEvent event);


    /**
     * @deprecated not needed
     */
    void facetDeleted(KnowledgeBaseEvent event);


    /**
     * @see ModelListener#resourceNameChanged
     * @deprecated
     */
    void frameNameChanged(KnowledgeBaseEvent event);


    /**
     * @see ModelListener#individualCreated
     * @deprecated
     */
    void instanceCreated(KnowledgeBaseEvent event);


    /**
     * @see ModelListener#individualDeleted
     * @deprecated
     */
    void instanceDeleted(KnowledgeBaseEvent event);


    /**
     * @see ModelListener#propertyCreated
     * @deprecated
     */
    void slotCreated(KnowledgeBaseEvent event);


    /**
     * @see ModelListener#propertyDeleted
     * @deprecated
     */
    void slotDeleted(KnowledgeBaseEvent event);
}
