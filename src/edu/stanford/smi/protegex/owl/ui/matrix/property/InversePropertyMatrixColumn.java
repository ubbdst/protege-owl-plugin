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
import edu.stanford.smi.protegex.owl.ui.matrix.AbstractMatrixColumn;

import javax.swing.table.TableCellRenderer;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InversePropertyMatrixColumn extends AbstractMatrixColumn {

    public InversePropertyMatrixColumn() {
        super("Inverse", 150);
    }


    public TableCellRenderer getCellRenderer() {
        return new FrameRenderer() {

            private boolean calling = false;


            protected void loadSlot(Slot slot) {
                if (calling) {
                    super.loadSlot(slot);
                }
                else {
                    Slot inverseSlot = slot.getInverseSlot();
                    if (inverseSlot != null) {
                        calling = true;
                        loadSlot(inverseSlot);
                        calling = false;
                    }
                }
            }
        };
    }
}
