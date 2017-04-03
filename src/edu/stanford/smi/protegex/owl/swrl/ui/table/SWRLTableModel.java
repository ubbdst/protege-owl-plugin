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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import edu.stanford.smi.protege.event.KnowledgeBaseAdapter;
import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.owltable.SymbolTableModel;

public class SWRLTableModel extends AbstractTableModel implements Disposable, SymbolTableModel
{
	public final static int COL_ENABLED = 0;
	public final static int COL_NAME = 1;
	public final static int COL_EXPRESSION = 2;

	public final static int COL_COUNT = 3;

	private final List<SWRLImp> imps = new ArrayList<SWRLImp>();
	private RDFResource rdfResource;
	private final OWLModel owlModel;
	private final SWRLFactory factory;

	public SWRLTableModel(OWLModel owlModel)
	{
		this.owlModel = owlModel;
		this.factory = new SWRLFactory(owlModel);

		for (Object o : this.factory.getImps())
			if (o instanceof SWRLImp)
				this.imps.add((SWRLImp)o);

		sortImps();
		initListeners();
	}

	public SWRLTableModel(RDFResource resource)
	{
		this.rdfResource = resource;
		this.owlModel = resource.getOWLModel();
		this.factory = new SWRLFactory(this.owlModel);
		addReferencingImps();
		sortImps();
		initListeners();
	}

	public int getColumnCount()
	{
		return COL_COUNT;
	}

	public Icon getIcon(RDFResource resource)
	{
		return ProtegeUI.getIcon(resource);
	}

	public SWRLImp getImp(int row)
	{
		return this.imps.get(row);
	}

	public void setImp(int row, SWRLImp imp)
	{
		this.imps.remove(row);
		this.imps.add(row, imp);
	}

	public RDFProperty getPredicate(int row)
	{
		return null;
	}

	public RDFResource getRDFResource(int row)
	{
		return getImp(row);
	}

	public RDFResource getSubject()
	{
		return null;
	}

	public int getSymbolColumnIndex()
	{
		return COL_EXPRESSION;
	}

	public int getRowCount()
	{
		return this.imps.size();
	}

	public int indexOf(SWRLImp imp)
	{
		return this.imps.indexOf(imp);
	}

	public void dispose()
	{
		this.owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP).removeClassListener(this.clsListener);
		((KnowledgeBase)this.owlModel).removeKnowledgeBaseListener(this.kbListener);
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		if (column == COL_ENABLED)
			return Boolean.class;
		else
			return super.getColumnClass(column);
	}

	@Override
	public String getColumnName(int column)
	{
		if (column == COL_ENABLED)
			return "Enabled";
		else if (column == COL_NAME)
			return "Name";
		else if (column == COL_EXPRESSION)
			return "Expression";
		else
			return null;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (columnIndex == getSymbolColumnIndex())
			return getImp(rowIndex).getBrowserText();
		else if (columnIndex == COL_NAME)
			return NamespaceUtil.getPrefixedName(this.owlModel, getImp(rowIndex).getName());
		else if (columnIndex == COL_ENABLED)
			return new Boolean(getImp(rowIndex).isEnabled());
		else
			return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		if (columnIndex == COL_ENABLED)
			return true;
		else if (columnIndex == COL_NAME || columnIndex == COL_EXPRESSION) {
			SWRLImp imp = getImp(rowIndex);
			return imp.isEditable();
		} else
			return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		SWRLImp imp = getImp(rowIndex);
		if (columnIndex == COL_EXPRESSION) {
			String text = (String)aValue;
			try {
				imp.setExpression(text);
				if (!isSuitable(imp)) {
					ProtegeUI.getModalDialogFactory().showMessageDialog(
							this.owlModel,
							"The replacing rule no longer fits the selection\n" + "criteria of this rules list, and will therefore no\n"
									+ "longer be visible here.  But no reason to panic: It\n" + "should still show up on the SWRL tab.");
				}
			} catch (Exception ex) {
				Log.getLogger().warning("Exception caught defining rule " + ex);
			}
		} else if (columnIndex == COL_NAME) {
			String newName = (String)aValue;
			if (this.owlModel.isValidResourceName(newName, imp)) {
				RDFResource resource = this.owlModel.getRDFResource(newName);
				if (resource != null) {
					if (!imp.equals(resource)) {
						ProtegeUI.getModalDialogFactory().showErrorMessageDialog(this.owlModel, "The name " + newName + " is already used in this ontology.");
					}
				} else {
					imp = (SWRLImp)imp.rename(newName);
					setImp(rowIndex, imp);
				}
			} else
				ProtegeUI.getModalDialogFactory().showErrorMessageDialog(this.owlModel, newName + " is not a valid rule name.");
		} else if (columnIndex == COL_ENABLED) {
			Boolean enabled = (Boolean)aValue;
			if (enabled.booleanValue())
				imp.enable();
			else
				imp.disable();
		} 
	} 

	public void setRowOf(SWRLImp imp, int index)
	{
		int oldIndex = this.imps.indexOf(imp);
		this.imps.remove(oldIndex);
		fireTableRowsDeleted(oldIndex, oldIndex);
		this.imps.add(index, imp);
		fireTableRowsInserted(index, index);
	}

	public void enableAll()
	{
		for (SWRLImp imp : this.imps)
			imp.enable();

		fireTableRowsUpdated(0, getRowCount());
	}

	public void disableAll()
	{
		for (SWRLImp imp : this.imps)
			imp.disable();

		fireTableRowsUpdated(0, getRowCount());
	}

	private void addReferencingImps()
	{
		for (SWRLImp imp : this.factory.getImps())
			if (isSuitable(imp))
				this.imps.add(imp);
	}

	private int getRowFor(SWRLImp imp)
	{
		final String impName = imp.getName();
		int i = 0;
		while (i < this.imps.size() && impName.compareToIgnoreCase(getImp(i).getName()) >= 0) {
			i++;
		}
		return i;
	}

	private void initListeners()
	{
		this.owlModel.getRDFSNamedClass(SWRLNames.Cls.IMP).addClassListener(this.clsListener);
		((KnowledgeBase)this.owlModel).addKnowledgeBaseListener(this.kbListener);
	}

	private boolean isSuitable(SWRLImp imp)
	{
		if (this.rdfResource == null) {
			return true;
		} else {
			Set<RDFResource> set = imp.getReferencedInstances();
			return set.contains(this.rdfResource);
		}
	}

	private void perhapsAdd(SWRLImp imp)
	{
		if (isSuitable(imp)) {
			int row = getRowFor(imp);
			this.imps.add(row, imp);
			fireTableRowsInserted(row, row);
		}
	}

	private void perhapsRemove(SWRLImp imp)
	{
		int row = this.imps.indexOf(imp);
		if (row >= 0) {
			this.imps.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}

	private void sortImps()
	{
		Collections.sort(this.imps, new Comparator<SWRLImp>() {
			public int compare(SWRLImp a, SWRLImp b)
			{
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
	}

	private final ClassListener clsListener = new ClassAdapter() {
		@Override
		public void instanceAdded(RDFSClass cls, RDFResource instance)
		{
			final SWRLImp newImp = (SWRLImp)instance;
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					perhapsAdd(newImp);
				}
			});
		}

		@Override
		public void instanceRemoved(RDFSClass cls, RDFResource instance)
		{
			perhapsRemove((SWRLImp)instance);
		}
	};

	private final KnowledgeBaseListener kbListener = new KnowledgeBaseAdapter() {
		@Override
		public void frameReplaced(KnowledgeBaseEvent event)
		{
			if (event.getNewFrame() instanceof SWRLImp) {
				perhapsRemove((SWRLImp)event.getFrame());
				perhapsAdd((SWRLImp)event.getNewFrame());
			}
		};
	};
}
