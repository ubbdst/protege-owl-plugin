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

import edu.stanford.smi.protegex.owl.model.util.DLExpressivityChecker;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ExpressivityIcons {


	private static final String EXTENSION = ".png";

	private static  HashMap map;

	static {
		map = new HashMap();
		map.put(DLExpressivityChecker.FL0, loadIcon("FLO"));
		map.put(DLExpressivityChecker.FL_MINUS, loadIcon( "FLM"));
		map.put(DLExpressivityChecker.AL, loadIcon( "AL"));
		map.put(DLExpressivityChecker.C, loadIcon( "C"));
		map.put(DLExpressivityChecker.U, loadIcon( "U"));
		map.put(DLExpressivityChecker.E, loadIcon( "E"));
		map.put(DLExpressivityChecker.N, loadIcon( "N"));
		map.put(DLExpressivityChecker.Q, loadIcon( "Q"));
		map.put(DLExpressivityChecker.H, loadIcon( "H"));
		map.put(DLExpressivityChecker.I, loadIcon( "I"));
		map.put(DLExpressivityChecker.O, loadIcon( "O"));
		map.put(DLExpressivityChecker.F, loadIcon( "F"));
		map.put(DLExpressivityChecker.S, loadIcon( "S"));
		map.put(DLExpressivityChecker.DATATYPE, loadIcon( "Datatype"));
	}

	public static ImageIcon getIcon(String letters) {
		return (ImageIcon) map.get(letters);
	}

	private static ImageIcon loadIcon(String name) {
		return new ImageIcon(getURL(name));
	}

	private static URL getURL(String iconName) {
		return ExpressivityIcons.class.getResource("icons/" + iconName + EXTENSION);
	}

	public static void main(String [] args) {
		ArrayList features = new ArrayList();
		features.add(DLExpressivityChecker.S);
		features.add(DLExpressivityChecker.H);
		features.add(DLExpressivityChecker.O);
		features.add(DLExpressivityChecker.I);
		features.add(DLExpressivityChecker.N);
		features.add(DLExpressivityChecker.DATATYPE);
		JPanel panel = new DLNamePanel(features);
		JFrame f = new JFrame();
		f.setContentPane(panel);
		f.show();
	}

}

