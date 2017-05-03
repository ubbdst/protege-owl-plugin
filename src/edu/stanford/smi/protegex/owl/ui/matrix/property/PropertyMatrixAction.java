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
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixFilter;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixPanel;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyMatrixAction extends AbstractAction {

    private OWLModel owlModel;


    public PropertyMatrixAction(OWLModel owlModel) {
        super("Show properties list", OWLIcons.getImageIcon(OWLIcons.PROPERTY_MATRIX));
        this.owlModel = owlModel;
    }


    public void actionPerformed(ActionEvent e) {
        MatrixFilter filter = new MatrixFilter() {

            public Collection getInitialValues() {
                Collection properties = owlModel.getVisibleUserDefinedRDFProperties();
                for (Iterator it = properties.iterator(); it.hasNext();) {
                    RDFResource slot = (RDFResource) it.next();
                    if (!isSuitable(slot)) {
                        it.remove();
                    }
                }
                return properties;
            }


            public String getName() {
                return "All Properties";
            }


            public boolean isSuitable(RDFResource instance) {
                return instance instanceof RDFProperty &&
                        instance.isVisible() &&
                        (instance.isEditable() || instance.isIncluded());
            }
        };
        MatrixPanel panel = new MatrixPanel(owlModel, filter, new PropertyMatrixTableModel(owlModel, filter));
        ResultsPanelManager.addResultsPanel(owlModel, panel, true);
    }
}
