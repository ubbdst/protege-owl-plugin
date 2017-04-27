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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.components.rdflist.RDFListComponent;

/**
 * The default widget for properties of type rdf:List (or a subclass of rdf:List).
 * It looks similar to a normal instance list widget but operates on a linked RDF list.
 * <p/>
 * It is possible to subclass this widget class to operate on simulated lists with first
 * and rest properties.  To do so, overload the corresponding methods of the RDFListComponent class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFListWidget extends AbstractPropertyValuesWidget {


    public RDFListWidget() {
        setPreferredColumns(2);
        setPreferredRows(2);
    }


    protected PropertyValuesComponent createComponent(RDFProperty predicate) {
        return new RDFListComponent(predicate, getLabel(), isReadOnlyConfiguredWidget());
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return OWLWidgetMapper.isSuitable(RDFListWidget.class, cls, slot);
    }
}