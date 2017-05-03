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

import edu.stanford.smi.protege.util.ListDragSourceListener;

import javax.swing.*;
import java.util.Collection;

/**
 * Source side handling of the drag and drop operations on the instances tab.  Note that the source is always the
 * instances list and the target is always the classes pane.
 *
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
class AssertedInstancesListDragSourceListener extends ListDragSourceListener {

    public void doCopy(JComponent c, int[] indices, Collection draggedObjects) {
    }


    public void doMove(final JComponent c, int[] indices, Collection draggedObjects) {
    }
}
