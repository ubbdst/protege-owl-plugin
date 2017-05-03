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

import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protegex.owl.ui.search.ResourceListFinder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SelectResourcesFromCollectionPanel extends JPanel {

    private JList list;


    public SelectResourcesFromCollectionPanel(Collection instances) {
        ArrayList slotList = new ArrayList(instances);
        Collections.sort(slotList, new FrameComparator());
        list = ComponentFactory.createList(edu.stanford.smi.protege.util.ModalDialog.getCloseAction(this));
        list.setListData(slotList.toArray());
        list.setCellRenderer(FrameRenderer.createInstance());
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(new ResourceListFinder(list, "Find"), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(300, 300));
    }


    public Collection getSelection() {
        return ComponentUtilities.getSelection(list);
    }
}
