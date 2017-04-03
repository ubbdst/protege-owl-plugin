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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.util.ComponentFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         19-Oct-2005
 */
public class ResourceFinder extends JComponent {

    private JTextComponent textEdit;
    private JToolBar toolbar;
    private FindAction advSearchAction;
    private JComboBox combo;

    public ResourceFinder(FindAction action) {
        super();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 0, 0));

        combo = new JComboBox();
        combo.setEditable(true);
        textEdit = (JTextComponent) combo.getEditor().getEditorComponent();
        textEdit.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if ((e.getKeyChar() == '\n') || (e.getKeyChar() == '\r')) {
                    advSearchAction.actionPerformed(null);
                    Object newStr = textEdit.getText();
                    int items = combo.getItemCount();
                    boolean found = false;
                    for (int i = 0; i < items && found == false; i++) {
                        if (newStr == combo.getItemAt(i)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        combo.addItem(newStr);
                    }
                }
            }
        });

        FontMetrics fontMetrics = textEdit.getFontMetrics(textEdit.getFont());
        int height = fontMetrics.getHeight() + fontMetrics.getDescent();

        combo.setPreferredSize(new Dimension(450, height));

        toolbar = new JToolBar();
        toolbar.setPreferredSize(new Dimension(200, 28));
        toolbar.setRollover(true);
        toolbar.setFloatable(false);

        advSearchAction = action;
        advSearchAction.setTextBox(textEdit);

        toolbar.add(combo);
        toolbar.add(advSearchAction);
        toolbar.addSeparator();

        add(toolbar, BorderLayout.SOUTH);
    }

    public JButton addButton(Action action) {
        return ComponentFactory.addToolBarButton(toolbar, action);
    }

    public JTextComponent getTextComponent() {
        return textEdit;
    }
}
