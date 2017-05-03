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

package edu.stanford.smi.protegex.owl.ui.clsdesc;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.ui.owltable.AbstractOWLTableModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A ClassDescriptionTableModel that displays those parts of a class definition
 * that are not restrictions.  This implementation assumes that the edited class is
 * a defined class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertiesDefinitionTableModel extends AbstractOWLTableModel implements ClassDescriptionTableModel {

    public PropertiesDefinitionTableModel() {
        super(0);
    }


    public boolean addRow(RDFSClass aClass, int selectedRow) {
        RDFSClass oldDefinition = getEditedCls().getDefinition();
        if (oldDefinition instanceof OWLIntersectionClass) {
            OWLIntersectionClass intersectionCls = (OWLIntersectionClass) oldDefinition;
            for (Iterator it = intersectionCls.getOperands().iterator(); it.hasNext();) {
                Cls operand = (Cls) it.next();
                if (operand.getBrowserText().equals(aClass.getBrowserText())) {
                    return false;
                }
            }
            OWLIntersectionClass newDefinition = (OWLIntersectionClass) intersectionCls.createClone();
            newDefinition.addOperand(aClass);
            oldDefinition.delete();
            getEditedCls().addEquivalentClass(newDefinition);
            if (aClass instanceof RDFSNamedClass) {
                getEditedCls().addSuperclass(aClass);
            }
        }
        else {
            if (oldDefinition.getBrowserText().equals(aClass.getBrowserText())) {
                return false;
            }
            OWLIntersectionClass intersectionCls = aClass.getOWLModel().createOWLIntersectionClass();
            intersectionCls.addOperand(oldDefinition);
            intersectionCls.addOperand(aClass);
            getEditedCls().setDefinition(intersectionCls);
        }
        return true;
    }


    protected void addRows() {
        for (Iterator it = getDefinitionOperands().iterator(); it.hasNext();) {
            RDFSClass aClass = (RDFSClass) it.next();
            insertRow(aClass);
        }
    }


    public void deleteRow(int index) {
        Cls oldCls = getClass(index);
        if (oldCls instanceof OWLAnonymousClass) {
            Cls definition = getEditedCls().getDefinition();
            if (definition instanceof OWLIntersectionClass) {
                OWLIntersectionClass oldIntersectionCls = ((OWLIntersectionClass) definition);
                Collection operands = new ArrayList(oldIntersectionCls.getOperands());
                operands.remove(oldCls);
                if (operands.size() > 1) {
                    OWLModel owlModel = (OWLModel) oldCls.getKnowledgeBase();
                    OWLIntersectionClass newIntersectionCls = owlModel.createOWLIntersectionClass();
                    for (Iterator it = operands.iterator(); it.hasNext();) {
                        RDFSClass operand = (RDFSClass) it.next();
                        newIntersectionCls.addOperand(operand.createClone());
                    }
                    definition.delete();
                    getEditedCls().addEquivalentClass(newIntersectionCls);
                }
                else {
                    RDFSClass remainingOperand = (RDFSClass) operands.iterator().next();
                    RDFSClass clone = remainingOperand.createClone();
                    definition.delete();
                    getEditedCls().addEquivalentClass(clone);
                }
            }
            else {
                definition.delete();
            }
        }
        else {
            super.deleteRow(index);
        }
    }


    private Collection getDefinitionOperands() {
        List result = new ArrayList();
        if (getEditedCls() != null) {
            RDFSClass definition = getEditedCls().getDefinition();
            if (definition != null) {
                if (definition instanceof OWLIntersectionClass) {
                    result.addAll(((OWLIntersectionClass) definition).getOperands());
                }
                else {
                    result.add(definition);
                }
            }
        }
        return result;
    }


    protected int getInsertRowIndex(Cls cls) {
        if (cls instanceof RDFSNamedClass) {
            return 0;
        }
        else {
            return getRowCount();
        }
    }


    public RDFProperty getPredicate(int row) {
        return null;
    }


    public boolean isDeleteEnabledFor(RDFSClass cls) {
        return cls instanceof OWLAnonymousClass;
    }


    public boolean isRemoveEnabledFor(Cls cls) {
        return cls instanceof RDFSNamedClass &&
                getRowCount() > 1 &&
                getEditedCls().getNamedSuperclasses().size() > 1;
    }


    protected boolean isSuitable(Cls cls) {
        if (cls instanceof RDFSClass && !getEditedCls().equals(cls) && getDefinitionOperands().contains(cls)) {
            if (!(cls instanceof OWLRestriction)) {
                if (cls instanceof OWLAnonymousClass || cls.isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }


    protected void setValueAt(int rowIndex, OWLModel owlModel, String parsableText) throws Exception {
        if (rowIndex >= getRowCount()) {
            return;
        }
        OWLNamedClass cls = getEditedCls();
        OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
        RDFSClass newClass = parser.parseClass(owlModel, parsableText);
        RDFSClass oldClass = getClass(rowIndex);
        final String newBrowserCls = newClass.getBrowserText();
        if (oldClass == null) {
            if (!addRow(newClass, 0)) {
                displaySemanticError("The class " + newBrowserCls +
                        " is already a superclass of " + cls.getBrowserText() + ".");
                if (newClass instanceof OWLAnonymousClass) {
                    newClass.delete();
                }
            }
        }
        else if (oldClass.getBrowserText().equals(newBrowserCls)) {
            if (newClass instanceof OWLAnonymousClass) {
                newClass.delete();
            }
        }
        else {
            Cls definition = cls.getDefinition();
            if (definition instanceof OWLIntersectionClass) {
                OWLIntersectionClass oldIntersectionCls = (OWLIntersectionClass) definition;
                OWLIntersectionClass newIntersectionCls = owlModel.createOWLIntersectionClass();
                for (Iterator it = oldIntersectionCls.getOperands().iterator(); it.hasNext();) {
                    RDFSClass operand = (RDFSClass) it.next();
                    if (operand.equals(oldClass)) {
                        newIntersectionCls.addOperand(newClass);
                    }
                    else {
                        newIntersectionCls.addOperand(operand.createClone());
                    }
                }
                if (oldClass instanceof RDFSNamedClass) {
                    cls.removeSuperclass(oldClass);
                }
                oldIntersectionCls.delete();
                cls.addEquivalentClass(newIntersectionCls);
                if (newClass instanceof RDFSNamedClass) {
                    cls.addSuperclass(newClass);
                }
                if (cls.getSuperclassCount() == 1) {
                    cls.addSuperclass(owlModel.getOWLThingClass());
                }
            }
            else {
                cls.addEquivalentClass(newClass);
                if (oldClass instanceof OWLAnonymousClass) {
                    oldClass.delete();
                }
                else {
                    cls.removeSuperclass(oldClass);
                    if (cls.getSuperclassCount() == 1 && newClass instanceof OWLAnonymousClass) {
                        // Replaced named class with anonymous class
                        cls.addSuperclass(owlModel.getOWLThingClass());
                    }
                }
            }
        }
    }
}
