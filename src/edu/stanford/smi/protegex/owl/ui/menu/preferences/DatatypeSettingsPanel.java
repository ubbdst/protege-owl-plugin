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

package edu.stanford.smi.protegex.owl.ui.menu.preferences;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.hp.hpl.jena.vocabulary.XSD;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.ui.ResourceComparator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DatatypeSettingsPanel extends JPanel {

    private boolean changed = false;

    private OWLModel owlModel;


    public DatatypeSettingsPanel(OWLModel owlModel) {
        this.owlModel = owlModel;
        JPanel visPanel = new JPanel();
        visPanel.setLayout(new BoxLayout(visPanel, BoxLayout.Y_AXIS));
        visPanel.setBorder(BorderFactory.createTitledBorder("Visibility"));
        java.util.List datatypes = new ArrayList();
        Iterator it = owlModel.getRDFSDatatypes().iterator();
        while (it.hasNext()) {
            RDFSDatatype datatype = (RDFSDatatype) it.next();
            if (datatype.isSystem()) {
                datatypes.add(datatype);
            }
        }

        Box defaultTypesBox = createBox("Default Types", datatypes, new RDFSDatatype[]{
                owlModel.getXSDboolean(),
                owlModel.getXSDfloat(),
                owlModel.getXSDint(),
                owlModel.getXSDstring()
        });
        Box dateTypesBox = createBox("Date/Time Types", datatypes, new RDFSDatatype[]{
                owlModel.getXSDdate(),
                owlModel.getXSDdateTime(),
                owlModel.getXSDduration(),
                owlModel.getXSDtime()
        });
        Box extendedTypesBox = createBox("Uncommon Numeric Types", datatypes, new RDFSDatatype[]{
                owlModel.getXSDbyte(),
                owlModel.getXSDdecimal(),
                owlModel.getXSDdouble(),
                owlModel.getRDFSDatatypeByURI(XSD.integer.getURI()),
                owlModel.getXSDlong(),
                owlModel.getRDFSDatatypeByURI(XSD.negativeInteger.getURI()),
                owlModel.getRDFSDatatypeByURI(XSD.nonNegativeInteger.getURI()),
                owlModel.getRDFSDatatypeByURI(XSD.nonPositiveInteger.getURI()),
                owlModel.getRDFSDatatypeByURI(XSD.positiveInteger.getURI()),
                owlModel.getRDFSDatatypeByURI(XSD.unsignedByte.getURI()),
                owlModel.getRDFSDatatypeByURI(XSD.unsignedInt.getURI()),
                owlModel.getRDFSDatatypeByURI(XSD.unsignedLong.getURI()),
                owlModel.getRDFSDatatypeByURI(XSD.unsignedShort.getURI()),
                owlModel.getXSDshort()
        });
        Collections.sort(datatypes, new ResourceComparator());
        Box restBox = createBox("Other Types", datatypes, (RDFSDatatype[]) datatypes.toArray(new RDFSDatatype[0]));
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(BorderLayout.NORTH, defaultTypesBox);
        leftPanel.add(BorderLayout.CENTER, dateTypesBox);
        leftPanel.add(BorderLayout.SOUTH, extendedTypesBox);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(BorderLayout.CENTER, restBox);
        visPanel.setLayout(new GridLayout(1, 2, 8, 8));
        visPanel.add(leftPanel);
        visPanel.add(rightPanel);

        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, visPanel);
    }


    private Box createBox(String title, java.util.List datatypes, RDFSDatatype[] types) {
        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createTitledBorder(title));
        for (int i = 0; i < types.length; i++) {
            RDFSDatatype datatype = types[i];
            datatypes.remove(datatype);
            box.add(new DatatypeCheckBox(datatype));
        }
        return box;
    }


    public boolean getRequiresReloadUI() {
        return false;
    }


    private class DatatypeCheckBox extends JCheckBox {

        private RDFSDatatype datatype;

        DatatypeCheckBox(RDFSDatatype datatype) {
            super(datatype.getBrowserText());
            this.datatype = datatype;
            setSelected(datatype.isVisible());
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleChanged();
                }
            });
        }

        private void handleChanged() {
            datatype.setVisible(isSelected());
            changed = true;
        }
    }
}
