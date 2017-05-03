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

package edu.stanford.smi.protegex.owl.ui.classform.component;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

import javax.swing.*;
import java.awt.*;

/**
 * @author Matthew Horridge  <matthew.horridge@cs.man.ac.uk>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class FormComponent extends JComponent implements Disposable {

    public static final int BORDER_THICKNESS = 4;

    private JComponent content;

    private boolean designTime;


    public FormComponent() {
        setBorder(BorderFactory.createEmptyBorder(BORDER_THICKNESS,
                BORDER_THICKNESS,
                BORDER_THICKNESS,
                BORDER_THICKNESS));
        setLayout(new BorderLayout());
    }


    public JComponent getContent() {
        return content;
    }


    public abstract void init(OWLModel owlModel);


    public boolean isDesignTime() {
        return designTime;
    }


    public void setContent(JComponent content) {
        this.content = content;
        removeAll();
        if (content != null) {
            add(BorderLayout.CENTER, content);
        }
    }

    // Was: setClass, but this name would be too generic.  Other options are setSubject, setEditedClass


    /**
     * Propagates the currently edited class of the form into this FormComponent.
     *
     * @param namedClass the new named class
     */
    public abstract void setNamedClass(OWLNamedClass namedClass);


    public void setDesignTime(boolean b) {
        designTime = b;
    }
}
