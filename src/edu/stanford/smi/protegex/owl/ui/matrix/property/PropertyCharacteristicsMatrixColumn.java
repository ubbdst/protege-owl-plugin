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

package edu.stanford.smi.protegex.owl.ui.matrix.property;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.matrix.MatrixColumn;

import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyCharacteristicsMatrixColumn implements MatrixColumn {

    public TableCellRenderer getCellRenderer() {
        return new FrameRenderer() {
            protected void loadSlot(Slot slot) {
                RDFProperty rdfProperty = (RDFProperty) slot;
                setGrayedSecondaryText(false);
                String text = getText(rdfProperty);
                if (text.length() > 0) {
                    addText(text);
                }
            }
        };
    }


    private String getText(RDFProperty rdfProperty) {
        Collection cs = new ArrayList();
        if (rdfProperty.isFunctional()) {
            cs.add("Functional");
        }
        if (rdfProperty instanceof OWLProperty) {
            if (((OWLProperty) rdfProperty).isInverseFunctional()) {
                cs.add("InverseFunctional");
            }
            if (rdfProperty instanceof OWLObjectProperty) {
                OWLObjectProperty objectSlot = (OWLObjectProperty) rdfProperty;
                if (objectSlot.isSymmetric()) {
                    cs.add("Symmetric");
                }
                if (objectSlot.isTransitive()) {
                    cs.add("Transitive");
                }
            }
        }
        Collection equis = rdfProperty.getEquivalentProperties();
        if (!equis.isEmpty()) {
            String str = "Equivalents: {";
            for (Iterator it = equis.iterator(); it.hasNext();) {
                Slot equi = (Slot) it.next();
                str += equi.getBrowserText();
                if (it.hasNext()) {
                    str += ", ";
                }
            }
            str += "}";
            cs.add(str);
        }
        Collection supers = rdfProperty.getSuperproperties(false);
        if (!supers.isEmpty()) {
            String str = "Super properties: {";
            for (Iterator it = supers.iterator(); it.hasNext();) {
                Slot equi = (Slot) it.next();
                str += equi.getBrowserText();
                if (it.hasNext()) {
                    str += ", ";
                }
            }
            str += "}";
            cs.add(str);
        }
        String str = "";
        if (!cs.isEmpty()) {
            for (Iterator it = cs.iterator(); it.hasNext();) {
                String s = (String) it.next();
                str += s;
                if (it.hasNext()) {
                    str += ", ";
                }
            }
        }
        return str;
    }


    public String getName() {
        return "Other Characteristics";
    }


    public int getWidth() {
        return 400;
    }
}
