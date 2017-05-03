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

package edu.stanford.smi.protegex.owl.ui.dialogs;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractSelectionDialogFactory implements SelectionDialogFactory {

    public final static String SELECT_CLASS_TITLE = "Select a class";

    public final static String SELECT_PROPERTY_TITLE = "Select a property";

    public final static String SELECT_RESOURCE_TITLE = "Select a resource";

    public final static String SELECT_RESOURCES_TITLE = "Select resources";


    public RDFSNamedClass selectClass(Component parent, OWLModel owlModel) {
        return selectClass(parent, owlModel, SELECT_CLASS_TITLE);
    }


    public Set selectClasses(Component parent, OWLModel owlModel, RDFSNamedClass rootClass, String title) {
        if (rootClass == null) {
            return null;
        }
        else {
            return selectClasses(parent, owlModel, Collections.singleton(rootClass), title);
        }
    }


    public RDFSNamedClass selectClass(Component parent, OWLModel owlModel, RDFSNamedClass rootClass, String title) {
        if (rootClass == null) {
            return null;
        }
        else {
            return selectClass(parent, owlModel, Collections.singleton(rootClass), title);
        }
    }


    public RDFSNamedClass selectClass(Component parent, OWLModel owlModel, Collection rootClasses, String title) {
        return selectClass(parent, owlModel, rootClasses, title);
    }


    public RDFSNamedClass selectClass(Component parent, OWLModel owlModel, Collection rootClasses) {
        return selectClass(parent, owlModel, rootClasses, SELECT_CLASS_TITLE);
    }


    public RDFSNamedClass selectClass(Component parent, OWLModel owlModel, String title) {
        return selectClass(parent, owlModel, owlModel.getOWLThingClass(), title);
    }


    public Set selectClasses(Component parent, OWLModel owlModel, String title) {
        return selectClasses(parent, owlModel, owlModel.getOWLThingClass(), title);
    }


    public RDFProperty selectProperty(Component parent, OWLModel owlModel, Collection allowedProperties) {
        return selectProperty(parent, owlModel, allowedProperties, SELECT_PROPERTY_TITLE);
    }


    public RDFResource selectResourceByType(Component parent, OWLModel owlModel, Collection allowedClasses) {
        return selectResourceByType(parent, owlModel, allowedClasses, SELECT_RESOURCE_TITLE);
    }


    public Set selectResourcesByType(Component parent, OWLModel owlModel, Collection allowedClasses) {
        return selectResourcesByType(parent, owlModel, allowedClasses, SELECT_RESOURCES_TITLE);
    }
}
