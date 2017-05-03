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
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

/**
 * An interface for user interface dialogs to select resources from
 * a list or tree.
 * <p/>
 * An instance of this can be accessed from <CODE>ProtegeUI.getSelectionDialogFactory()</CODE>.
 * <p/>
 * All methods take a Component as first argument which can be left null: if no pointer
 * to an existing component is available then the system will use the ProjectView registered
 * for the OWLModel.  However, it is generally recommended to use a non-null parent.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface SelectionDialogFactory {

    // selectClass --------------------------------------------


    RDFSNamedClass selectClass(Component parent, OWLModel owlModel);


    RDFSNamedClass selectClass(Component parent, OWLModel owlModel, String title);


    RDFSNamedClass selectClass(Component parent, OWLModel owlModel, Collection rootClasses);


    RDFSNamedClass selectClass(Component parent, OWLModel owlModel, Collection rootClasses, String title);


    RDFSNamedClass selectClass(Component parent, OWLModel owlModel, RDFSNamedClass rootClass, String title);

    // selectClasses ------------------------------------------


    Set selectClasses(Component parent, OWLModel owlModel, String title);


    Set selectClasses(Component parent, OWLModel owlModel, RDFSNamedClass rootClass, String title);


    Set selectClasses(Component parent, OWLModel owlModel, Collection rootClasses, String title);

    // selectProperty -----------------------------------------


    RDFProperty selectProperty(Component parent, OWLModel owlModel, Collection allowedProperties);


    RDFProperty selectProperty(Component parent, OWLModel owlModel, Collection allowedProperties, String title);

    RDFSDatatype selectDatatype(Component parent, OWLModel owlModel);
    
    // selectResource -----------------------------------------


    RDFResource selectResourceByType(Component parent, OWLModel owlModel, Collection allowedClasses);


    RDFResource selectResourceByType(Component parent, OWLModel owlModel, Collection allowedClasses, String title);


    RDFResource selectResourceFromCollection(Component parent, OWLModel owlModel, Collection resources, String title);

    // selectResources ----------------------------------------


    Set selectResourcesByType(Component parent, OWLModel owlModel, Collection allowedClasses);


    Set selectResourcesByType(Component parent, OWLModel owlModel, Collection allowedClasses, String title);


    Set selectResourcesFromCollection(Component parent, OWLModel owlModel, Collection resources, String title);
}
