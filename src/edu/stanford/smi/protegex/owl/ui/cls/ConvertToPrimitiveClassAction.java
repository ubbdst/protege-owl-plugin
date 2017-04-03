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

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A ResourceAction to convert a primitive class into a defined class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ConvertToPrimitiveClassAction extends ResourceAction {

    public ConvertToPrimitiveClassAction() {
        super("Convert to primitive class",
                OWLIcons.getImageIcon(OWLIcons.PRIMITIVE_OWL_CLASS),
                AddSubclassAction.GROUP,
                true);
    }


    public void actionPerformed(ActionEvent e) {
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(getComponent());
       
        try {
            final OWLNamedClass cls = (OWLNamedClass) getResource();
            getOWLModel().beginTransaction("Convert " + cls.getBrowserText() + " to primitive class", (cls == null ? null : cls.getName()));
            performAction(cls);
            getOWLModel().commitTransaction();
        }
        catch (Exception ex) {
        	getOWLModel().rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);
        }

        if (tab != null) {
            tab.ensureClassSelected((OWLNamedClass) getResource(), -1);
        }
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof ResourceDisplay &&
                resource instanceof OWLNamedClass &&
                resource.isEditable() &&
                ((OWLNamedClass) resource).getDefinition() != null;
    }


    public static void performAction(OWLNamedClass cls) {
        Collection equis = new ArrayList(cls.getEquivalentClasses());
        for (Iterator it = equis.iterator(); it.hasNext();) {
            RDFSClass equiClass = (RDFSClass) it.next();
            if (equiClass instanceof OWLIntersectionClass) {
                OWLIntersectionClass intersectionClass = (OWLIntersectionClass) equiClass;
                Collection operands = intersectionClass.getOperands();
                for (Iterator ot = operands.iterator(); ot.hasNext();) {
                    RDFSClass operand = (RDFSClass) ot.next();
                    cls.addSuperclass(operand.createClone());
                }
                intersectionClass.delete();
                for (Iterator oit = operands.iterator(); oit.hasNext();) {
                    RDFSClass operand = (RDFSClass) oit.next();
                    if (operand instanceof RDFSNamedClass) {
                        cls.addSuperclass(operand);
                    }
                }
            }
            else if (equiClass != null) {
                equiClass.removeSuperclass(cls);
                if (equiClass instanceof RDFSNamedClass && equiClass.getSuperclassCount() == 0) {
                    equiClass.addSuperclass(equiClass.getOWLModel().getOWLThingClass());
                }
            }
        }
    }
}
