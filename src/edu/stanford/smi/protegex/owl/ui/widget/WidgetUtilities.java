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

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.InstanceDisplay;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLNames;

import javax.swing.*;
import java.awt.*;

/**
 * This class provides generic methods used by several
 * OWL widgets to fullfill its tasks.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class WidgetUtilities {

    public static void addViewButton(LabeledComponent lc, Action viewAction) {
        if (ApplicationProperties.getBooleanProperty("showViewButtons", false)) {
            lc.addHeaderButton(viewAction);
        }
    }


    public static Cls getDefaultRestrictionMetaCls(KnowledgeBase kb) {
        return kb.getCls(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
    }


    public static JToolBar getJToolBar(InstanceDisplay instanceDisplay) {
        Container child = (Container) instanceDisplay.getComponent(0);
        return getJToolBar((Container) child.getComponent(0));
    }


    public static JToolBar getJToolBar(Container container) {
        return searchFakeToolBarRecursive(container);
    }


    private static JToolBar searchFakeToolBarRecursive(Container c) {

        if (c instanceof JToolBar) {
            return (JToolBar) c;
        }

        if (c.getComponents().length > 0) {
            Component[] comps = c.getComponents();
            for (int i = 0; i < comps.length; i++) {
                JToolBar f = searchFakeToolBarRecursive((Container) comps[i]);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }
}
