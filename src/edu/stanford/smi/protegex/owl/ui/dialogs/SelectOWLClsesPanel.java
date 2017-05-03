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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.DefaultRenderer;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.Validatable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTree;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTreeRoot;
import edu.stanford.smi.protegex.owl.ui.search.finder.DefaultClassFind;
import edu.stanford.smi.protegex.owl.ui.search.finder.Find;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindAction;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindInDialogAction;
import edu.stanford.smi.protegex.owl.ui.search.finder.ResourceFinder;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

public class SelectOWLClsesPanel extends JComponent implements Validatable {

	private static final long serialVersionUID = -1715739135005096261L;
	
	protected ClassTree _tree;
    private boolean _allowsMultiple;
    protected OWLModel _owlModel;

    public SelectOWLClsesPanel(OWLModel kb) {
        this(kb, Collections.EMPTY_SET);
    }

    public SelectOWLClsesPanel(OWLModel kb, DefaultRenderer renderer) {
        this(kb, Collections.EMPTY_SET);
        _tree.setCellRenderer(renderer);
    }

    public SelectOWLClsesPanel(OWLModel kb, Collection clses) {
        this(kb, clses, true);
    }

    public SelectOWLClsesPanel(OWLModel kb, Collection clses, boolean allowsMultiple) {
    	_owlModel = kb;
        _allowsMultiple = allowsMultiple;
        if (clses.isEmpty()) {
            clses = CollectionUtilities.createCollection(kb.getOWLThingClass());
        }
        LazyTreeRoot root = new ClassTreeRoot(clses, OWLUI.getSortClassTreeOption());
        _tree = new ClassTree(null, root);
         
        _tree.setCellRenderer(new ResourceRenderer(false));
        int rows = _tree.getRowCount();
        int diff = rows - clses.size();
        for (int i = rows - 1; i > diff; --i) {
            _tree.expandRow(i);
        }
        _tree.setSelectionRow(0);
        setLayout(new BorderLayout());
        add(new JScrollPane(_tree), BorderLayout.CENTER);
        
        FindAction fAction = new FindInDialogAction(new DefaultClassFind(_owlModel, Find.CONTAINS),
                Icons.getFindClsIcon(),
                _tree, true);
		ResourceFinder finder = new ResourceFinder(fAction);
        add(finder, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(300, 300));
    }


	public Collection getSelection() {
        return _tree.getSelection();
    }
    

	public boolean validateContents() {
        boolean isValid = _allowsMultiple || getSelection().size() <= 1;
        if (!isValid) {
            ModalDialog.showMessageDialog(this, "Only 1 class can be selected", ModalDialog.MODE_CLOSE);
        }
        return isValid;
    }


	public void saveContents() {
        // do nothing
    }
	

	
}
