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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceIgnoreCaseComparator;

/**
 * A key listener that can be used for auto-completion. Usage:<br/><br/>  
 * <code>classNameTextField.addKeyListener(new ExpansionKeyListener(classNameTextField, owlModel));</code>
 * 
 * <br/>
 * The default expansion key is Ctrl + Space. To change the 
 * default expansion key, override the {@link #isExpansionEvent(KeyEvent)} method.
 */
public class ExpansionKeyListener extends KeyAdapter {
	private JTextComponent textComponent;
	private OWLModel owlModel;
	private OWLResourceNameMatcher resourceNameMatcher = new OWLResourceNameMatcher();

	public ExpansionKeyListener(JTextComponent textComponent, OWLModel owlModel) {
		this.textComponent = textComponent;
		this.owlModel = owlModel;
	}


	@Override
	public void keyPressed(KeyEvent e) {		
		try {
			// This nastiness is required to keep the Manchester class parser
			// (which is unsed by the expansionHandler) happy.
                  owlModel.getOWLClassDisplay().getParser().checkClass(owlModel, textComponent.getText());
		} catch (OWLClassParseException ex) {
                  Log.emptyCatchBlock(ex);
		} // try
		
		if (isExpansionEvent(e)) {
			handleExpansion();
			e.consume();
		}
	} // keyPressed
	
	protected boolean isExpansionEvent(KeyEvent e) {
		return e.getKeyChar() == ' ' &&
                (e.getModifiers() & java.awt.event.InputEvent.CTRL_MASK) != 0;
	}

	private void handleExpansion() {
		String text = textComponent.getText();
		final JComboBox comboBox;
		Set<RDFResource> matchingResources = resourceNameMatcher.getMatchingResources(text, "", owlModel);

		textComponent.removeAll();

		if (!matchingResources.isEmpty()) {
			Frame[] fs = matchingResources.toArray(new Frame[0]);
			Arrays.sort(fs, new ResourceIgnoreCaseComparator());

			comboBox = new JComboBox(fs);
			comboBox.setBackground(Color.white);
			comboBox.setRenderer(new FrameRenderer());
			comboBox.setSize(comboBox.getPreferredSize().width + 20, 0);
			comboBox.setLocation(textComponent.getX(), textComponent.getHeight());
			comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {					
					if ((e.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) != 0) {
						replacetText((RDFResource) comboBox.getSelectedItem());
					}
				}
			});
			comboBox.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int keyCode = e.getKeyCode();					
					if (keyCode == KeyEvent.VK_ENTER) {
						replacetText((RDFResource) comboBox.getSelectedItem());
						comboBox.setVisible(false);
						textComponent.remove(comboBox);
					}					
				}
			});
			textComponent.add(comboBox);
			comboBox.showPopup();
			comboBox.requestFocus();
		} // if
	} // handleExpansion
	
	protected void replacetText(RDFResource resource) {		
		textComponent.setText(resourceNameMatcher.getInsertString(resource));
		textComponent.requestFocusInWindow();
	}

} // ExpansionKeyListener 
