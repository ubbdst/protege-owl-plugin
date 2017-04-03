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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
abstract class AbstractFindAction extends AbstractAction implements FindAction {

    private JTextComponent searchBox;

    protected ResultsViewModelFind findModel;

    protected boolean allowSave;

    private HostResourceDisplay hostResourceDisplay;

    public AbstractFindAction(ResultsViewModelFind find, Icon icon) {
        this(find, icon, null);
    }

    public AbstractFindAction(ResultsViewModelFind find, Icon icon, HostResourceDisplay hrd) {
        this(find, icon, hrd, false);
    }

    public AbstractFindAction(ResultsViewModelFind find, Icon icon, HostResourceDisplay hrd, boolean allowSave) {
        super(find.getFind().getDescription(), icon);
        this.findModel = find;
        this.hostResourceDisplay = hrd;
        this.allowSave = allowSave;
    }

    public void setTextBox(JTextComponent textBox) {
        this.searchBox = textBox;
    }

    public void actionPerformed(ActionEvent e) {

        String txt = "";

        if (searchBox != null) {
            txt = searchBox.getText();
        }

        findModel.getFind().startSearch(txt);
//        Map results = find.getResults();

        // for a single result, just go and select the resource
//        if ((results != null) && (results.size() == 1)) {
//            RDFResource result = (RDFResource) results.keySet().iterator().next();
//            OWLUI.selectResource(result, hrd);
//        }
//        else {
        // determine whether to show a table or a simple list
        if (findModel.getFind().getNumSearchProperties() > 1) {
            showResults(new FindResultsTableView(findModel, hostResourceDisplay));
        }
        else {
            showResults(new FindResultsListView(findModel, hostResourceDisplay));
        }
//        }

    }

    protected abstract void showResults(AbstractFindResultsView view);
}
