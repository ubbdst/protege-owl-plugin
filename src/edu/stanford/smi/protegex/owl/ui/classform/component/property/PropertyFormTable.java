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

package edu.stanford.smi.protegex.owl.ui.classform.component.property;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.util.ClosureAxiomFactory;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.clsdesc.ClassDescriptionEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.OWLSymbolPanel;
import edu.stanford.smi.protegex.owl.ui.code.OWLTextAreaPanel;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.owltable.SymbolTable;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A SymbolTable showing a PropertyFormTableModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyFormTable extends SymbolTable implements Disposable {

    private PropertyFormTableModel tableModel;


    public PropertyFormTable(PropertyFormTableModel tableModel, OWLNamedClass namedClass, RDFProperty property) {
        super(tableModel, namedClass.getOWLModel(), true, new OWLSymbolPanel(namedClass.getOWLModel(), true, true));
        this.tableModel = tableModel;
    }


    protected SymbolEditorComponent createSymbolEditorComponent(OWLModel model, SymbolErrorDisplay errorDisplay) {
        return new ClassDescriptionEditorComponent(model, errorDisplay, true);
    }


    public void dispose() {
        tableModel.dispose();
    }


    protected String editMultiLine(RDFResource input) {
        if (input instanceof RDFSClass) {
            return OWLTextAreaPanel.showEditDialog(this, getOWLModel(), (RDFSClass) input);
        }
        else {
            return null;
        }
    }


    protected Collection getNavigationMenuItems(RDFResource resource) {
        if (resource instanceof RDFSClass) {
            Set set = new HashSet();
            ((RDFSClass) resource).getNestedNamedClasses(set);
            return set;
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    public PropertyFormTableModel getTableModel() {
        return tableModel;
    }


    protected String getToolTipText(RDFResource resource) {
        if (resource instanceof RDFSClass) {
            String str = OWLUI.getOWLToolTipText((RDFSClass) resource);
            if (str != null && str.length() > 0) {
                return str;
            }
        }
        return null;
    }


    public boolean isClosed() {
        if (tableModel.getRowCount() > 0) {
            OWLExistentialRestriction restriction = tableModel.getRestriction(0);
            return ClosureAxiomFactory.getClosureAxiom(tableModel.getNamedClass(), restriction) != null;
        }
        else {
            return false;
        }
    }


    protected void navigateTo(final RDFResource instance) {
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(this);
        if (tab != null && instance instanceof RDFSNamedClass) {
            tab.setSelectedCls((RDFSNamedClass) instance);
        }
    }


    public void setClosed(boolean closed) {
        // TODO
    }
}
