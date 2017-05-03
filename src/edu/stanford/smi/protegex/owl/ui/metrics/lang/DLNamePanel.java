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

package edu.stanford.smi.protegex.owl.ui.metrics.lang;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DLNamePanel extends JPanel {

	private List icons;

	private int maxHeight = 0;

	private int maxWidth = 0;

	private Dimension prefSize;

	private static final int TRACKING_ADJUSTMENT = -7;

	public DLNamePanel(List langFeatures) {
		icons = new ArrayList();
		for(Iterator it = langFeatures.iterator(); it.hasNext(); ) {
			ImageIcon curIcon = ExpressivityIcons.getIcon((String) it.next());
			if(curIcon != null) {
				icons.add(curIcon);
				if(curIcon.getIconHeight() > maxHeight) {
					maxHeight = curIcon.getIconHeight();
				}
				maxWidth += curIcon.getIconWidth() + TRACKING_ADJUSTMENT;
			}
		}
		prefSize = new Dimension(maxWidth, maxHeight);
	}


	public Dimension getPreferredSize() {
		return prefSize;
	}


	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int x = 0;
		for(Iterator it = icons.iterator(); it.hasNext(); ) {
			ImageIcon curIcon = (ImageIcon) it.next();
			int y = maxHeight - curIcon.getIconHeight();
			g2.drawImage(curIcon.getImage(), x, y, null);
			x += curIcon.getIconWidth() + TRACKING_ADJUSTMENT;
		}

	}
}

