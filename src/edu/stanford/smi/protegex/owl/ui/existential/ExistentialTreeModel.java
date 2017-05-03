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

package edu.stanford.smi.protegex.owl.ui.existential;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Feb 5, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ExistentialTreeModel implements TreeModel {

    private OWLClass root;

    private ExistentialFillerProvider fillerProvider;

    public ExistentialTreeModel(OWLClass root, OWLObjectProperty property) {
        this.root = root;
        this.fillerProvider = new ExistentialFillerProvider(property);
    }

    public Object getRoot() {
        return root;
    }

    public List getChildren(Object object) {
        fillerProvider.reset();
        OWLClass cls = (OWLClass) object;
        cls.accept(fillerProvider);
        ArrayList list = new ArrayList(fillerProvider.getFillers());
        return list;
    }

    public Object getChild(Object object, int i) {
        return getChildren(object).toArray()[i];
    }

    public int getChildCount(Object object) {
        return getChildren(object).size();
    }

    public boolean isLeaf(Object object) {
        return getChildCount(object) == 0;
    }

    public void valueForPathChanged(TreePath treePath, Object object) {
    }

    public int getIndexOfChild(Object object, Object object1) {
        return getChildren(object).indexOf(object1);
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
    }
}
