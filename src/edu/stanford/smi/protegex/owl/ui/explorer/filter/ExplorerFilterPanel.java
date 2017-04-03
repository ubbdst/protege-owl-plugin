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

package edu.stanford.smi.protegex.owl.ui.explorer.filter;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ExplorerFilterPanel extends JPanel {

    private DefaultExplorerFilter filter;


    public ExplorerFilterPanel(OWLModel owlModel) {
        this(owlModel, new DefaultExplorerFilter());
    }


    public ExplorerFilterPanel(OWLModel owlModel, DefaultExplorerFilter filter) {
        this.filter = filter;
        ValidClassesPanel validClassesPanel = new ValidClassesPanel(filter);
        setLayout(new BorderLayout(8, 8));
        add(BorderLayout.WEST, validClassesPanel);
        add(BorderLayout.EAST, new ValidPropertyPanel(owlModel, filter));
    }


    public static boolean show(OWLModel owlModel, DefaultExplorerFilter filter) {
        ExplorerFilterPanel panel = new ExplorerFilterPanel(owlModel, filter);
        Component parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        return ProtegeUI.getModalDialogFactory().showDialog(parent, panel, "Explore superclass relationships",
                ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK;
    }
}
