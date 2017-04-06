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

package edu.stanford.smi.protegex.owl.ui.individuals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.TransferHandler;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.AddAction;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.RemoveAction;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.util.SimpleListModel;
import edu.stanford.smi.protege.util.TransferableCollection;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.event.ResourceAdapter;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.code.OWLTextAreaPanel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * A panel showing the asserted types of the currently selected instance.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class AssertedTypesListPanel extends SelectableContainer {   
	private static final long serialVersionUID = -5577903429790898408L;

	private Action addTypeAction;
	private Action addExpressionAction;
	private SelectableList list;
	private OWLModel owlModel;
	private RDFResource resource;

	private ResourceListener resourceListener = new ResourceAdapter() {
		@Override
		public void typeAdded(RDFResource resource, RDFSClass type) {
			ComponentUtilities.addListValue(list, type);
		}
		@Override
		public void typeRemoved(RDFResource resource, RDFSClass type) {
			ComponentUtilities.removeListValue(list, type);
		}
	};


	public AssertedTypesListPanel(OWLModel owlModel) {
		this.owlModel = owlModel;
		list = ComponentFactory.createSelectableList(createDoubleClickAction());
		list.setCellRenderer(new ResourceRenderer(false));        
		setSelectable(list);

		OWLLabeledComponent lc = new OWLLabeledComponent("Asserted Types", new JScrollPane(list));
		lc.addHeaderButton(createAddTypeAction());
		lc.addHeaderButton(createAddExpressionAction());
		lc.addHeaderButton(createRemoveTypeAction());
		setLayout(new BorderLayout());
		add(lc);
		setPreferredSize(new Dimension(0, 100));
		updateAddButton();

		list.setDragEnabled(true);
		list.setTransferHandler(new FrameTransferHandler());

	}


	public void setResource(RDFResource newResource) {
		if (resource != null) {
			resource.removeResourceListener(resourceListener);
		}
		resource = newResource;
		if (resource != null) {
			resource.addResourceListener(resourceListener);
		}
		updateModel();
		updateAddButton();
	}


	public void updateModel() {
		ListModel model;
		if (resource == null) {
			model = new DefaultListModel();
		}
		else {
			Collection types = resource.getRDFTypes();
			model = new SimpleListModel(types);
		}
		list.setModel(model);
	}


	public void updateAddButton() {
		addTypeAction.setEnabled(resource != null);
		addExpressionAction.setEnabled(resource != null);
	}


	private Action createAddTypeAction() {
		addTypeAction = new AddAction("Add type...", OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS)) {
			@Override
			public void onAdd() {
				Collection clses = ProtegeUI.getSelectionDialogFactory().selectClasses(AssertedTypesListPanel.this, owlModel, "Select type to add");
				Iterator i = clses.iterator();
				while (i.hasNext()) {
					RDFSClass cls = (RDFSClass) i.next();
					resource.addProtegeType(cls);
				}
			}
		};
		return addTypeAction;
	}


	private Action createRemoveTypeAction() {
		return new RemoveAction("Remove selected type", list, OWLIcons.getRemoveIcon(OWLIcons.PRIMITIVE_OWL_CLASS)) {
			@Override
			public void onRemove(Object o) {
				if (o instanceof RDFSClass) {
					if (resource.getRDFTypes().size() > 1) {
						resource.removeProtegeType((RDFSClass) o);
					}
					else {
						ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
						"Resources must have at least one remaining type.");
					}
				}
			}
		};
	}

	private Action createAddExpressionAction() {
		addExpressionAction = new AddAction("Add OWL class expression as type...", OWLIcons.getCreateIcon(OWLIcons.ANONYMOUS_OWL_CLASS)) {
			@Override
			public void onAdd() {
				OWLModel owlModel = resource.getOWLModel();
				String expression = OWLTextAreaPanel.showEditDialog(AssertedTypesListPanel.this, owlModel, null);
				if (expression != null) {
					try {
						OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
						RDFSClass type = parser.parseClass(owlModel, expression);
						if (type != null) {
							resource.addProtegeType(type);
						}
					}
					catch (OWLClassParseException e) {
						ModalDialog.showMessageDialog(AssertedTypesListPanel.this, "Invalid class expression", "Invalid expression");
						return;
					}
				}
			}
		};
		return addExpressionAction;
	}

	private Action createDoubleClickAction() {
		return new AbstractAction(){		
			public void actionPerformed(ActionEvent arg0) {
				Collection selection = list.getSelection();
				for (Iterator iterator = selection.iterator(); iterator
				.hasNext();) {
					Object o = iterator.next();
					if (o instanceof RDFResource) {
						RDFResource resource = (RDFResource) o;
						if (resource instanceof OWLAnonymousClass) {
							editAnonExpression((OWLAnonymousClass)resource);
						} else {
							owlModel.getProject().show((Instance)o);
						}
					} else if (o instanceof Instance) {
						owlModel.getProject().show((Instance)o);
					}					
				}
			}
		};
	}  

	protected void editAnonExpression(OWLAnonymousClass anonClass) {		
		OWLModel owlModel = resource.getOWLModel();
		String expression = OWLTextAreaPanel.showEditDialog(AssertedTypesListPanel.this, owlModel, anonClass);
		if (expression != null) {
			RDFSClass type = null;
			try {
				OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
				type = parser.parseClass(owlModel, expression);					
			} catch (OWLClassParseException e) {
				ModalDialog.showMessageDialog(AssertedTypesListPanel.this, "Invalid class expression", "Invalid expression");
				return;
			}
			if (type != null) {
				try {
					owlModel.beginTransaction("Change type of " + resource.getBrowserText() + 
							" from: '" + anonClass.getBrowserText() + "' to: '" + expression + "'" , resource.getName());
					resource.addProtegeType(type);
					resource.removeProtegeType(anonClass);
					owlModel.commitTransaction();
				} catch (Exception e) {
					Log.getLogger().log(Level.WARNING, "Change type for " + resource.getName() + " failed.", e);
					owlModel.rollbackTransaction();
					ModalDialog.showMessageDialog(AssertedTypesListPanel.this, 
							"Changing the type of " + resource.getBrowserText() + " failed.\nSee console for details.", "Error");
					return;
				}
				try {
					list.setSelectedValue(type);
				} catch (Exception e) {
					Log.emptyCatchBlock(e);
				}
			}
		}
	}


	private class FrameTransferHandler extends TransferHandler {
		@Override
		protected Transferable createTransferable(JComponent c) {
			Collection collection = getSelection();
			return collection.isEmpty() ? null : new TransferableCollection(collection);
		}
		@Override
		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			return true;
		}
		@Override
		public boolean importData(JComponent component, Transferable data) {
			return true;
		}
		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			if (action == MOVE) {
				Iterator i = getSelection().iterator();
				while (i.hasNext()) {
					RDFSClass type = (RDFSClass) i.next();
					int index = 0;
					Log.getLogger().info("Move " + type + " to: " + index);
					resource.moveDirectType(type, index);
					updateModel();
				}
			}
		}
		@Override
		public int getSourceActions(JComponent c) {
			return MOVE;
		}
	}

}