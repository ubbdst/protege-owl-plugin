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


package edu.stanford.smi.protegex.owl.swrl.ui.table;

import java.awt.BorderLayout;

import javax.swing.text.JTextComponent;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLTextField;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorHandler;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;

public class SWRLSymbolEditor extends SymbolEditorComponent
{
	private final SWRLTextField textField;

	public SWRLSymbolEditor(OWLModel model, SymbolErrorDisplay errorDisplay)
	{
		super(model, errorDisplay, false);
		this.textField = new SWRLTextField(model, errorDisplay);
		setLayout(new BorderLayout());
		add(this.textField);
	}

	@Override
	public JTextComponent getTextComponent()
	{
		return this.textField;
	}

	@Override
	protected void parseExpression() throws Exception
	{
		SWRLParser parser = new SWRLParser(getModel());
		parser.parse(this.textField.getText());
	}

	@Override
	public void setSymbolEditorHandler(SymbolEditorHandler symbolEditorHandler)
	{
		super.setSymbolEditorHandler(symbolEditorHandler);
		this.textField.setSymbolEditorHandler(symbolEditorHandler);
	}
}
