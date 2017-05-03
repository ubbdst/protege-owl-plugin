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


package edu.stanford.smi.protegex.owl.swrl.ui.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.impl.DefaultSWRLImp;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLSymbolPanel;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLTextArea;

public class SWRLRuleSlotWidget extends AbstractSlotWidget
{
	private static final String SWRL_RULE_LABEL = "SWRL Rule";
	private static final String SWRL_RULE_INVALID_LABEL = "SWRL Rule: invalid rule - not savable!";
	private static final String SWRL_RULE_INCOMPLETE_LABEL = "SWRL Rule: incomplete rule - not yet savable";

	private SWRLTextArea swrlTextArea;
	private LabeledComponent swrlTextAreaLabeledComponent;

	private String ruleExpressionInKb = new String();
	private SWRLParser parser;

	private final FocusListener _focusListener = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent event)
		{
			commitChanges();
		}
	};

	public void initialize()
	{
		buildGUI();

		setPreferredColumns(2);
		setPreferredRows(5);

		this.parser = new SWRLParser((OWLModel)getKnowledgeBase());
	}

	protected void buildGUI()
	{
		final OWLModel owlModel = (OWLModel)getKnowledgeBase();

		SWRLSymbolPanel symbolPanel = new SWRLSymbolPanel(owlModel, false, false) {
			@Override
			public void setErrorFlag(boolean error)
			{
				if (error) {
					setInvalidValueBorder();
				}
				super.setErrorFlag(error);
			}
		};

		this.swrlTextArea = new SWRLTextArea(owlModel, symbolPanel) {
			@Override
			protected void updateErrorDisplay()
			{
				String uniCodeText = getText();
				try {
					checkUniCodeExpression(uniCodeText);
					getErrorSymbolDisplay().displayError((Throwable)null);
					SWRLRuleSlotWidget.this.swrlTextAreaLabeledComponent.setHeaderLabel(getWidgetIncompleteOrOKTitle());
					setBackground(Color.white);
					setNormalBorder();
				} catch (Throwable ex) {
					getErrorSymbolDisplay().setErrorFlag(true);
					getErrorSymbolDisplay().displayError(ex.getMessage());
					SWRLRuleSlotWidget.this.swrlTextAreaLabeledComponent.setHeaderLabel(SWRL_RULE_INVALID_LABEL);
					setBackground(new Color(240, 240, 240));
					setInvalidValueBorder();
				}
			}
		};

		this.swrlTextArea.addFocusListener(this._focusListener);
		// swrlTextArea.addKeyListener(_keyListener);

		symbolPanel.setSymbolEditor(this.swrlTextArea);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(this.swrlTextArea), BorderLayout.CENTER);
		panel.add(symbolPanel, BorderLayout.SOUTH);

		this.swrlTextAreaLabeledComponent = new LabeledComponent(SWRL_RULE_LABEL, panel, true);

		add(this.swrlTextAreaLabeledComponent);
	}

	protected String getWidgetIncompleteOrOKTitle()
	{
		String newRule = this.swrlTextArea.getText().trim();
		String oldRule = this.ruleExpressionInKb.trim();

		if (newRule.equals(oldRule))
			return SWRL_RULE_LABEL;
		else
			return (this.parser.isCorrectAndIncomplete(newRule) ? SWRL_RULE_INCOMPLETE_LABEL : SWRL_RULE_LABEL);

	}

	@Override
	public void setInstance(Instance newInstance)
	{

		commitChanges();

		updateGUI((SWRLImp)newInstance);

		super.setInstance(newInstance);
	}

	protected void updateGUI(SWRLImp imp)
	{
		this.swrlTextAreaLabeledComponent.setHeaderLabel(SWRL_RULE_LABEL);

		this.ruleExpressionInKb = imp == null ? "" : imp.getBrowserText();
		this.ruleExpressionInKb = SWRLTextArea.reformatText(this.ruleExpressionInKb);

		if (this.ruleExpressionInKb.equals(DefaultSWRLImp.EMPTY_RULE_TEXT))
			this.swrlTextArea.setText("");
		else
			this.swrlTextArea.setText(this.ruleExpressionInKb);

		this.swrlTextArea.getErrorSymbolDisplay().displayError((Throwable)null);
		this.swrlTextArea.setBackground(Color.white);
		setNormalBorder();
	}

	public boolean commitChanges()
	{
		SWRLImp swrlimp = (SWRLImp)getInstance();

		if (swrlimp == null) {
			return true;
		}
		if (swrlimp.isDeleted()) {
			return true;
		}

		try {
			swrlimp.setExpression(this.swrlTextArea.getText());
			this.ruleExpressionInKb = this.swrlTextArea.getText();

			this.swrlTextAreaLabeledComponent.setHeaderLabel(SWRL_RULE_LABEL);
		} catch (SWRLParseException e) {
			setInvalidValueBorder();
			this.swrlTextArea.getErrorSymbolDisplay().displayError(e);

			this.swrlTextAreaLabeledComponent.setHeaderLabel(SWRL_RULE_INVALID_LABEL);
			return false;
		}
		return true;
	}

	public static boolean isSuitable(Cls cls, Slot slot, Facet facet)
	{
		KnowledgeBase kb = cls.getKnowledgeBase();

		if (!(kb instanceof OWLModel)) {
			return false;
		}

		if (slot.getName().equals(SWRLNames.Slot.BODY)) {
			return true;
		}

		return false;
	}

	public String getSwrlTextAreaText()
	{
		return this.swrlTextArea.getText();
	}

	public SWRLTextArea getSWRLTextArea()
	{
		return this.swrlTextArea;
	}

	@Override
	public void dispose()
	{
		try {
			this.swrlTextArea.removeFocusListener(this._focusListener);
		} catch (Throwable t) {
			// do nothing
		}

		this.parser = null;

		super.dispose();
	}

}
