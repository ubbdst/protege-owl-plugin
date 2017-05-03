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

package edu.stanford.smi.protegex.owl.ui.clsdesc.manchester;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 7, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SuggestionList extends JList {

    private MouseListener mouseListener;

    private SuggestionPopup popup;


    public SuggestionList(SuggestionPopup popup) {
        this.popup = popup;
        mouseListener = new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    performSelectedSuggestion();
                }
            }
        };
        if (System.getProperty("os.name").indexOf("Mac") != -1) {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
        else {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2))
            );
        }
        addMouseListener(mouseListener);
        setCellRenderer(new SuggestionRenderer());
    }


    private void performSelectedSuggestion() {
        Suggestion suggestion = (Suggestion) getSelectedValue();
        if (suggestion != null) {
            suggestion.performSuggestion();
            popup.updateCurrentEditorPane();
            popup.reset();
        }
    }
}


