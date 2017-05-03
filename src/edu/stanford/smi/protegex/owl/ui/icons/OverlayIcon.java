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

package edu.stanford.smi.protegex.owl.ui.icons;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OverlayIcon implements Icon {

    private Image baseImage;

    private int baseX;

    private int baseY;

    private int height;

    private Image topImage;

    private int topX;

    private int topY;

    private int width;


    public OverlayIcon(String baseIconName, int baseX, int baseY,
                       String topIconName, int topX, int topY) {
        this(baseIconName, baseX, baseY, topIconName, topX, topY, OWLIcons.class);
    }


    public OverlayIcon(String baseIconName, int baseX, int baseY,
                       String topIconName, int topX, int topY, Class clazz) {
        this(Toolkit.getDefaultToolkit().
                getImage(OWLIcons.getImageURL(clazz, baseIconName)),
                baseX, baseY,
                Toolkit.getDefaultToolkit().
                        getImage(OWLIcons.getImageURL(OWLIcons.class, topIconName)),
                topX, topY);
    }


    public OverlayIcon(Image baseImage, int baseX, int baseY,
                       Image topImage, int topX, int topY) {
        this(baseImage, baseX, baseY, topImage, topX, topY, 15, 15);
    }


    public OverlayIcon(Image baseImage, int baseX, int baseY,
                       Image topImage, int topX, int topY, int width, int height) {
        this.baseImage = baseImage;
        this.baseX = baseX;
        this.baseY = baseY;
        this.topImage = topImage;
        this.topX = topX;
        this.topY = topY;
        this.width = width;
        this.height = height;
    }


    public int getIconHeight() {
        return height;
    }


    public int getIconWidth() {
        return width;
    }


    public Icon getGrayedIcon() {
        Image grayBaseImage = GrayFilter.createDisabledImage(baseImage);
        Image grayTopImage = GrayFilter.createDisabledImage(topImage);
        return new OverlayIcon(grayBaseImage, baseX, baseY, grayTopImage, topX, topY);
    }


    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (y > 0 && (baseX != 0 || baseY != 0 || topX != 0 || topY != 0)) {
            x = 0;
            y = 0;
        }
        new ImageIcon(baseImage).paintIcon(c, g, baseX + x, baseY + y);
        new ImageIcon(topImage).paintIcon(c, g, topX + x, topY + y);
    }
}
