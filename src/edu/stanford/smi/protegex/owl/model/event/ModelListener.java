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

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ModelListener extends ProtegeKnowledgeBaseListener {

    /**
     * Called after a new RDFSClass has been created.
     *
     * @param cls the new class
     */
    void classCreated(RDFSClass cls);


    /**
     * Called after an RDFSClass has been deleted.
     *
     * @param cls the deleted class
     */
    void classDeleted(RDFSClass cls);


    /**
     * Called after an individual has been created.
     *
     * @param resource the new resource
     */
    void individualCreated(RDFResource resource);


    /**
     * Called after an individual has been deleted.
     *
     * @param resource the old resource
     */
    void individualDeleted(RDFResource resource);


    /**
     * Called after a property has been created.
     *
     * @param property the new property
     */
    void propertyCreated(RDFProperty property);


    /**
     * Called after a property has been deleted.
     *
     * @param property the old property
     */
    void propertyDeleted(RDFProperty property);


    /**
     * Called after the name of a resource has changed.
     *
     * @param resource the resource that changed its name
     * @param oldName  the old name of the resource
     */
    void resourceNameChanged(RDFResource resource, String oldName);
}
