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


package edu.stanford.smi.protegex.owl.swrl.ui.code;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.InstanceDisplay;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.ui.widget.SWRLRuleSlotWidget;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * A panel which can be used to edit an OWL expression in a multi-line dialog.
 *
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class SWRLTextAreaPanel extends JPanel implements ModalDialogFactory.CloseCallback
{
	private static final String SWRL_RULE_PANEL_TITLE = "SWRL Rule";
	private OWLModel owlModel;
	private SWRLSymbolPanel symbolPanel;
	private SWRLTextArea textArea;

	public SWRLTextAreaPanel(OWLModel owlModel)
	{
		this(owlModel, null);
	}

	public SWRLTextAreaPanel(OWLModel anOWLModel, SWRLImp imp)
	{
		this.owlModel = anOWLModel;
		symbolPanel = new SWRLSymbolPanel(anOWLModel, false, false);
		textArea = new SWRLTextArea(anOWLModel, symbolPanel) {
			protected void checkExpression(String text) throws Throwable
			{
				owlModel.getOWLClassDisplay().getParser().checkClass(owlModel, text);
			}
		};
		if (imp != null && imp.getHead() != null) {
			String text = imp.getBrowserText();
			textArea.setText(text);
			textArea.reformatText();
		}
		symbolPanel.setSymbolEditor(textArea);
	}

	public boolean canClose(int result)
	{
		if (result == ModalDialogFactory.OPTION_OK) {
			String uniCodeText = textArea.getText();
			if (uniCodeText.length() == 0) {
				return false;
			} else {
				try {
					SWRLParser parser = new SWRLParser(owlModel);
					parser.parse(uniCodeText);
					return true;
				} catch (Exception ex) {
					symbolPanel.displayError(ex);
					return false;
				}
			}
		} else {
            return true;
        }
	}

	public SWRLImp getResultAsImp()
	{
		try {
			String uniCodeText = textArea.getText();
			SWRLParser parser = new SWRLParser(owlModel);
			parser.setParseOnly(false);
			return parser.parse(uniCodeText);
		} catch (Exception ex) {
			Log.getLogger().warning("Error at parsing SWRL rule " + (textArea == null ? "" : textArea.getText()));
			return null;
		}
	}

	public String getResultAsString()
	{
		return textArea.getText();
	}

	public static boolean showEditDialog(Component parent, OWLModel owlModel, SWRLImp imp)
	{
		if (imp == null) {
            return false;
        }

		InstanceDisplay instanceDisplay = new InstanceDisplay(owlModel.getProject(), false, false);
		instanceDisplay.setInstance(imp);

		showFrame(instanceDisplay, imp.isEditable());

		// TT: what should we return?
		return true;
	}

	private static JFrame showFrame(final InstanceDisplay display, boolean isEditable)
	{
		final JFrame frame = ComponentFactory.createFrame();

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{

				if (hasChangedRule(display)) {
					int ret = ModalDialog.showMessageDialog(display, "Rule has not been saved, probably because rule is invalid.\n"
							+ "If you continue changes will be lost.\n\n" + "Do you want to continue?", "Rule not saved", ModalDialog.MODE_YES_NO);

					if (ret == ModalDialog.OPTION_NO) {
						return;
					}
				}

				JFrame frame = (JFrame)e.getWindow();
				frame.setVisible(false);
				ComponentUtilities.dispose(frame);
				edu.stanford.smi.protege.Application.repaint();

			}

			private boolean hasChangedRule(InstanceDisplay display)
			{

				try {
					ClsWidget clsWidget = display.getFirstClsWidget();
					Slot swrlBodySlot = display.getCurrentInstance().getKnowledgeBase().getSlot(SWRLNames.Slot.BODY);

					SWRLRuleSlotWidget swrlWidget = (SWRLRuleSlotWidget)clsWidget.getSlotWidget(swrlBodySlot);

					if (swrlWidget.commitChanges()) {
						return false;
					}

					String newRuleText = swrlWidget.getSwrlTextAreaText();

					SWRLImp swrlImp = (SWRLImp)swrlWidget.getInstance();
					String oldRuleText = SWRLTextArea.reformatText(swrlImp.getBrowserText());

					return !(oldRuleText.equals(newRuleText));

				} catch (Exception e) {
					// do nothing, probably not using the SWRLRuleSlotWidget
					return false;
				}
			}
		});

		display.setResizeVertically(true);

		ClsWidget clsWidget = display.getFirstClsWidget();
		Slot swrlBodySlot = display.getCurrentInstance().getKnowledgeBase().getSlot(SWRLNames.Slot.BODY);
		SWRLRuleSlotWidget swrlWidget = (SWRLRuleSlotWidget)clsWidget.getSlotWidget(swrlBodySlot);
		swrlWidget.getSWRLTextArea().setEnabled(isEditable);

		frame.getContentPane().add(display, BorderLayout.CENTER);

		frame.setPreferredSize(new Dimension(800, 500));
		frame.setTitle(SWRL_RULE_PANEL_TITLE);

		ComponentUtilities.pack(frame);
		ComponentUtilities.centerInMainWindow(frame);
		frame.setVisible(true);

		return frame;
	}
}
