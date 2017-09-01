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
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.components.multiresource.MergeResourceComponent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Hemed Al Ruwehy
 *
 * Mostly a copy of
 *
 * 24-08-2017
 * University of Bergen Library
 */
public class MergeResourceWidget extends AbstractPropertyValuesWidget {


    public MergeResourceWidget() {
        setPreferredColumns(2);
        setPreferredRows(2);
    }


    protected PropertyValuesComponent createComponent(RDFProperty predicate) {
        boolean symmetric = false;
        if (predicate instanceof OWLObjectProperty) {
            symmetric = ((OWLObjectProperty) predicate).isSymmetric();
        }
        else {
            OWLModel owlModel = getOwlModel(predicate);
            if (owlModel.getOWLSameAsProperty().equals(predicate) ||
                    owlModel.getOWLDifferentFromProperty().equals(predicate)) {
                symmetric = true;
            }
        }
        return new MergeResourceComponent(predicate, symmetric, getLabel(), isReadOnlyConfiguredWidget());
    }


    private OWLModel getOwlModel(RDFProperty predicate) {
        return predicate.getOWLModel();
    }


    /**
     * Where MultiResourceWidget is suitable, this widget is also suitable.
     */
    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return OWLWidgetMapper.isSuitable(MultiResourceWidget.class, cls, slot);
    }
}
