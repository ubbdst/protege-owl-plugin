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

package edu.stanford.smi.protegex.owl.model.framestore.updater;

import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * An object capable of mapping restrictions into facet overrides, and vice versa.
 * The goal of these classes is to ensure a maximum of compatibility to existing Protege
 * components, such as the template slots widget, that allows to edit facet overrides
 * on a per class basis.  Furthermore, this class makes sure that legacy Protege
 * ontologies can be converted into OWL (so that restrictions are created for facet
 * overrides) and that OWL files can be saved in other formats as well (because restrictions
 * are mirrored in restrictions that are not exported in most formats).
 * <BR>
 * Depending on the type of event, this class does the following:
 * <UL>
 * <LI>directSuperclassAdded/Removed (at OWLNamedClass): Updates facet overrides </LI>
 * <LI>directTemplateSlotAdded/Removed (at OWLRestriction): Updates facet overrides </LI>
 * <LI>templateFacetValueChanged (at OWLRestriction, i.e. filler changed): Updates facet overrides </LI>
 * <LI>templateFacetValueChanged (at OWLNamedClass): Updates restrictions </LI>
 * </UL>
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RestrictionUpdater {

    void copyFacetValuesIntoNamedClass(RDFSNamedClass cls, OWLRestriction restriction);


    void updateRestrictions(OWLNamedClass cls, RDFProperty property, Facet facet);
}
