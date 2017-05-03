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

package edu.stanford.smi.protegex.owl.ui.widget;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Date;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTimeWidget extends AbstractPropertyWidget implements TimePanel.Listener {


    private Action deleteAction = new AbstractAction("Delete value", OWLIcons.getDeleteIcon()) {
        public void actionPerformed(ActionEvent e) {
            timePanel.clear();
            timeChanged(timePanel);
        }
    };

    private LabeledComponent lc;

    private Action nowAction = new AbstractAction("Set to now", OWLIcons.getAddIcon()) {
        public void actionPerformed(ActionEvent e) {
            timePanel.setTime(new Date());
            timeChanged(timePanel);
        }
    };

    private TimePanel timePanel;


    public void initialize() {
        timePanel = new TimePanel(this);
        setLayout(new BorderLayout());
        lc = new LabeledComponent(getRDFProperty().getBrowserText(), timePanel);
        lc.addHeaderButton(nowAction);
        lc.addHeaderButton(deleteAction);
        add(BorderLayout.CENTER, lc);
    }


    public void setEditable(boolean b) {
        super.setEditable(b);
        nowAction.setEnabled(b);
        deleteAction.setEnabled(b);
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance != null) {
            setTimePanelValue();
        }
    }


    private void setTimePanelValue() {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        Object value = resource.getPropertyValue(property);
        if (value == null) {
            timePanel.clear();
        }
        else {
            timePanel.setTime(value.toString());
        }
    }


    public void timeChanged(TimePanel timePanel) {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        if (!timePanel.isNull()) {
            String newDate = timePanel.getTime();
            RDFSLiteral literal = getOWLModel().createRDFSLiteral(newDate,
                    getOWLModel().getRDFSDatatypeByURI(XSDDatatype.XSDtime.getURI()));
            resource.setPropertyValue(property, literal);
        }
        else {
            resource.setPropertyValues(property, Collections.EMPTY_LIST);
        }
    }


    /**
     * @param cls
     * @param slot
     * @param facet
     * @deprecated
     */
    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return OWLWidgetMapper.isSuitable(OWLTimeWidget.class, cls, slot);
    }
}
