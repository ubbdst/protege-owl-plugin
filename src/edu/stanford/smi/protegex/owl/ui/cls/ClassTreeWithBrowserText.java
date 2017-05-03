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

package edu.stanford.smi.protegex.owl.ui.cls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.swing.Action;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ResourceRendererWithBrowserText;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

public class ClassTreeWithBrowserText extends ClassTree {
    private static final long serialVersionUID = -841459836070720483L;

    private OWLModel owlModel = null;

    public ClassTreeWithBrowserText(OWLModel owlModel, Action doubleClickAction, ClassTreeWithBrowserTextRoot root) {
        super(doubleClickAction, root);
        this.owlModel = owlModel;
        setCellRenderer(new ResourceRendererWithBrowserText(owlModel));
    }

    /*
     * Get selection as a collection of Cls
     */
    @Override
    public Collection getSelection() {
        Collection<Cls> selectedClses = new TreeSet<Cls>();
        Collection sel = ComponentUtilities.getSelection(this);
        for (Object object : sel) {
            if (object instanceof FrameWithBrowserText) {
                FrameWithBrowserText fbt = (FrameWithBrowserText) object;
                Frame frame = fbt.getFrame();
                if (frame instanceof Cls) {
                    selectedClses.add((Cls) frame);
                }// TODO: what to do about the other ones?
            }
        }
        return selectedClses;
    }

    public void setSelectedCls(Cls cls) {
        if (!getSelection().contains(cls) && !cls.isDeleted()) {
            SelectableTree tree = this;
            if (cls instanceof RDFResource) {
                if (tree instanceof ClassTreeWithBrowserText) {
                    Collection path = (Collection) CollectionUtilities.getFirstItem(OWLUI
                            .getPathsToRoot((RDFResource) cls));
                    Collection<FrameWithBrowserText> fbtPath = new ArrayList<FrameWithBrowserText>();
                    if (path == null) {
                        fbtPath.add(new FrameWithBrowserText((Frame) owlModel.getOWLThingClass()));
                    } else {
                        for (Object object : path) {
                            if (object instanceof Frame) {
                                fbtPath.add(FrameWithBrowserText.getFrameWithBrowserText((Frame) object));
                            }
                        }
                    }
                    setSelectedObjectPath(tree, fbtPath);
                } else {
                    OWLUI.setSelectedNodeInTree(tree, (RDFResource) cls);
                }
            } else {
                Collection path = ModelUtilities.getPathToRoot(cls);
                setSelectedObjectPath(tree, path);
            }
        }
    }

    protected void setSelectedObjectPath(final JTree tree, Collection objectPath) {
        final TreePath path = ComponentUtilities.getTreePath(tree, objectPath);

        if (path != null) {
            final WaitCursor cursor = new WaitCursor(tree);
            tree.scrollPathToVisible(path);
            tree.setSelectionPath(path);
            cursor.hide();
            tree.updateUI();
        }
    }

}
