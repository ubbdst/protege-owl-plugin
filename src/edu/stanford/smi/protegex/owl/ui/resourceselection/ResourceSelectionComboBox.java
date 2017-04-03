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

package edu.stanford.smi.protegex.owl.ui.resourceselection;

import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.code.OWLTextField;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

/**
 * A JComboBox that can be used to select an RDFResource from a sorted list.
 * That special thing about this is that users can type the first letters of the frame
 * name and the list will automatically filter those that match this prefix.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceSelectionComboBox extends JComboBox implements KeyListener {

    private Color oldBackground;

    private Component host;

    private ResourceSelectionListener listener;

    private ResourceFilterListModel model;


    public ResourceSelectionComboBox(Collection resources, ResourceSelectionListener aListener) {
        this(null, resources, aListener);
    }


    public ResourceSelectionComboBox(Component aHost, Collection resources, ResourceSelectionListener aListener) {
        this(aHost, resources, aListener, new FrameRenderer());
    }


    public ResourceSelectionComboBox(Component aHost, Collection resources, ResourceSelectionListener aListener, ListCellRenderer renderer) {
        this.host = aHost;
        this.listener = aListener;
        setRenderer(renderer);
        setBackground(Color.white);
        model = new ResourceFilterListModel(resources);
        setModel(model);

        if (host != null) {
            oldBackground = host.getBackground();
            host.setBackground(oldBackground.darker()); //Color.gray);
            updateBounds();

            addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuCanceled(PopupMenuEvent e) {
                }


                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    host.setBackground(oldBackground);
                }


                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }
            });
        }

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((e.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0) {
                    okay();
                }
            }
        });
    }


    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            setPopupVisible(false);
            model.backspace();
            updateBounds();
            showPopup();
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            okay();
        }
        if (code != KeyEvent.VK_BACK_SPACE && code != KeyEvent.VK_ESCAPE) {
            char ch = e.getKeyChar();
            if (OWLTextField.isIdChar(ch)) {
                setPopupVisible(false);
                model.addChar(ch);
                updateBounds();
                showPopup();
            }
        }
    }


    public void firePopupMenuWillBecomeInvisible() {
        super.firePopupMenuWillBecomeInvisible();
        getParent().remove(this);
        setVisible(false);
    }


    public void keyReleased(KeyEvent e) {
    }


    public void keyTyped(KeyEvent e) {
    }


    private void okay() {
        RDFResource selected = (RDFResource) getSelectedItem();
        if (selected != null) {
            listener.resourceSelected(selected);
        }
        setPopupVisible(false);
    }


    public static void selectFrame(Collection resources, JComponent host,
                                   int x, ResourceSelectionListener listener) {
        selectResource(resources, host, x, listener, new FrameRenderer());
    }


    public static void selectResource(Collection resources, JComponent host,
                                      int x, ResourceSelectionListener listener,
                                      ListCellRenderer renderer) {
        ResourceSelectionComboBox cb = new ResourceSelectionComboBox(host, resources, listener, renderer);
        host.removeAll();
        host.add(cb);
        cb.setLocation(x, host.getHeight());
        cb.addKeyListener(cb);
        cb.showPopup();
        cb.requestFocus();
    }


    private void updateBounds() {
        if (host != null) {
            int width = getPreferredSize().width + 20;
            setSize(width, 0);
            setLocation(0, host.getHeight());
        }
    }
}
