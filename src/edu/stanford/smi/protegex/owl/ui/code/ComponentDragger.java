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

package edu.stanford.smi.protegex.owl.ui.code;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * An object that can be used to drag and drop a Component in its parent.
 * It has to be attached to the component both as a MouseListener and a
 * MouseMotionListener.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ComponentDragger extends MouseAdapter implements MouseMotionListener {

    private int baseX;

    private int baseY;

    private Component component;


    public ComponentDragger(Component comp) {
        this.component = comp;
    }


    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - baseX;
        int dy = e.getY() - baseY;
        int newX = component.getX() + dx;
        int newY = component.getY() + dy;
        component.setLocation(newX, newY);
    }


    public void mouseMoved(MouseEvent e) {
        // Ignore
    }


    public void mousePressed(MouseEvent e) {
        baseX = e.getX();
        baseY = e.getY();
    }
}
