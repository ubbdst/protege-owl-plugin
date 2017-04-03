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

package edu.stanford.smi.protegex.owl.ui.individuals;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JScrollPane;

import edu.stanford.smi.protege.action.DeleteInstancesAction;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * A JPanel to display the inferred instances of a collection of classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InferredInstancesListPanel extends SelectableContainer {

    private AllowableAction deleteAction;
    private OWLLabeledComponent lc;
    private InstancesList list;
    private Collection types;


    public InferredInstancesListPanel() {
        list = new InstancesList(null);
        lc = new OWLLabeledComponent("Inferred Instances", new JScrollPane(list));
        deleteAction = new DeleteInstancesAction(this) {
            @Override
			protected void onAfterDelete(Object o) {
                refill();
            }
        };
        deleteAction.putValue(Action.SMALL_ICON, OWLIcons.getDeleteIcon(OWLIcons.RDF_INDIVIDUAL));
        lc.addHeaderButton(deleteAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        setSelectable(list);
    }


    @Override
	public void dispose() {
    }


    private void refill() {
        Set set = new HashSet();
        for (Iterator it = types.iterator(); it.hasNext();) {
            RDFSClass type = (RDFSClass) it.next();
            set.addAll(type.getInferredInstances(false));
        }
        ArrayList instances = new ArrayList(set);
        Collections.sort(instances, new FrameComparator());
        list.setListData(instances.toArray());
    }


    public void setTypes(Collection types) {
        this.types = types;
        refill();
    }
}
