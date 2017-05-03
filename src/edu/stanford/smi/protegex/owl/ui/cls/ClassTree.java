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
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.TripleSelectable;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.subsumption.TooltippedSelectableTree;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class ClassTree extends TooltippedSelectableTree implements TripleSelectable, HostResourceDisplay {

    public ClassTree(Action doubleClickAction, LazyTreeRoot root) {
        super(doubleClickAction, root);
        setCellRenderer(new ResourceRenderer() {
            @Override
            public void setMainText(String text) {
                super.setMainText(removeAllQuotes(text));
            }
        });
    }

    private static final char SINGLE_QUOTE = '\'';

    private String removeAllQuotes(String text) {
        if (text != null && text.length() > 0 && text.charAt(0) == SINGLE_QUOTE
                && text.charAt(text.length() - 1) == SINGLE_QUOTE) {
            return text.replaceAll("'", "");
        }
        return text;
    }

    public List getPrototypeTriples() {
        List triples = new ArrayList();
        Iterator it = getSelection().iterator();
        while (it.hasNext()) {
            Object sel = it.next();
            if (sel instanceof RDFSNamedClass) {
                RDFSNamedClass object = (RDFSNamedClass) sel;
                RDFProperty predicate = object.getOWLModel().getRDFSSubClassOfProperty();
                triples.add(new DefaultTriple(null, predicate, object));
            }
        }
        return triples;
    }

    public List getSelectedTriples() {
        List results = new ArrayList();
        TreePath[] paths = getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                if (path.getPathCount() > 1 && path.getLastPathComponent() instanceof LazyTreeNode) {
                    LazyTreeNode node = (LazyTreeNode) path.getLastPathComponent();
                    Object subject = node.getUserObject();
                    if (subject instanceof RDFSNamedClass) {
                        RDFSNamedClass subjectClass = (RDFSNamedClass) subject;
                        TreeNode parent = node.getParent();
                        if (parent instanceof LazyTreeNode) {
                            Object object = ((LazyTreeNode) parent).getUserObject();
                            if (object instanceof RDFSNamedClass) {
                                RDFSNamedClass objectClass = (RDFSNamedClass) object;
                                RDFProperty predicate = objectClass.getOWLModel().getRDFSSubClassOfProperty();
                                Triple triple = new DefaultTriple(subjectClass, predicate, objectClass);
                                results.add(triple);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    public void setSelectedTriples(Collection triples) {
        // TODO
    }

    public boolean displayHostResource(RDFResource resource) {
        return OWLUI.setSelectedNodeInTree(this, resource);
    }

    public Collection getRoots() {
        return (Collection) ((LazyTreeRoot) getModel().getRoot()).getUserObject();
    }

}
