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

package edu.stanford.smi.protegex.owl.ui.matrix.property;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixFilter;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixPanel;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.search.SearchNamedClassAction;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SubpropertiesMatrixAction extends ResourceAction {

    public SubpropertiesMatrixAction() {
        super("Show list of subproperties",
                OWLIcons.getImageIcon(OWLIcons.PROPERTY_MATRIX),
                SearchNamedClassAction.GROUP);
    }


    public void actionPerformed(ActionEvent e) {
        final OWLModel owlModel = getOWLModel();
        final RDFProperty parentProperty = (RDFProperty) getResource();
        MatrixFilter filter = new MatrixFilter() {

            public Collection getInitialValues() {
                Collection results = new ArrayList();
                Iterator it = parentProperty.getSubproperties(true).iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (next instanceof RDFProperty) {
                        results.add(next);
                    }
                }
                results.remove(parentProperty);
                return results;
            }


            public String getName() {
                return "Subproperties of " + parentProperty.getBrowserText();
            }


            public boolean isSuitable(RDFResource instance) {
                return instance instanceof RDFProperty &&
                        instance.isVisible() &&
                        (instance.isEditable() || instance.isIncluded()) &&
                        ((RDFProperty) instance).isSubpropertyOf(parentProperty, true);
            }
        };
        MatrixPanel panel = new MatrixPanel(owlModel, filter, new PropertyMatrixTableModel(owlModel, filter));
        ResultsPanelManager.addResultsPanel(owlModel, panel, true);
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return resource instanceof RDFProperty;
    }
}
