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

package edu.stanford.smi.protegex.owl.ui.subsumption;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TooltippedSelectableTree extends SelectableTree {

    private static final long serialVersionUID = 6697428920472428637L;


    public TooltippedSelectableTree(Action doubleClickAction, LazyTreeRoot root) {
        super(doubleClickAction, root);
        final int oldDelay = ToolTipManager.sharedInstance().getDismissDelay();
        addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setDismissDelay(oldDelay);
            }
        });
        setToolTipText(""); // Dummy to initialize the mechanism
    }


    public String getToolTipText(MouseEvent event) {
        int row = getRowForLocation(event.getX(), event.getY());
        if (row >= 0) {
            TreePath path = getPathForRow(row);
            if (path != null) {
                Object last = path.getLastPathComponent();
                RDFResource res = null;
                if (last instanceof LazyTreeNode) {
                    LazyTreeNode node = (LazyTreeNode) path.getLastPathComponent();
                    Object userObject = node.getUserObject();
                    if (userObject instanceof RDFResource) {
                    	res = (RDFResource) userObject;
                    } else if (userObject instanceof FrameWithBrowserText) {
                        Frame frame = ((FrameWithBrowserText)userObject).getFrame();
                        if (frame instanceof RDFResource) {
                            res = (RDFResource) frame;
                        }                        
                    }                    
                }
                if (res != null) {
                    ToolTipManager.sharedInstance().setDismissDelay(OWLTable.INFINITE_TIME);
                    return OWLUI.getOWLToolTipText(res);
                }
            }
        }
        return null;
    }
}
