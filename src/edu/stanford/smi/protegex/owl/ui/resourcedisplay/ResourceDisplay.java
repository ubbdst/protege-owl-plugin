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


package edu.stanford.smi.protegex.owl.ui.resourcedisplay;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.ui.InstanceDisplay;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.swrl.ui.actions.FindRulesAction;
import edu.stanford.smi.protegex.owl.testing.OWLTestManager;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesComponent;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.search.FindUsageAction;
import edu.stanford.smi.protegex.owl.ui.testing.OWLTestInstanceAction;
import edu.stanford.smi.protegex.owl.ui.widget.InferredModeWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * An InstanceDisplay with the "type" actions instead of the yellow sticky ones on top. For classes this can also be used to switch between asserted and
 * inferred view.
 * 
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class ResourceDisplay extends InstanceDisplay implements ResourcePanel
{

	/**
	 * @deprecated
	 */
	@Deprecated
	public final static int DEFAULT_TYPE_CLS = ResourcePanel.DEFAULT_TYPE_CLASS;

	/**
	 * @deprecated
	 */
	@Deprecated
	public final static int DEFAULT_TYPE_SLOT = ResourcePanel.DEFAULT_TYPE_PROPERTY;

	/**
	 * @deprecated
	 */
	@Deprecated
	public final static int DEFAULT_TYPE_INSTANCE = ResourcePanel.DEFAULT_TYPE_INDIVIDUAL;

	private AddPropertyWidgetToFormAction addPropertyWidgetToFormAction;

	private JComponent centerComponent;

	private int defaultType;

	private EditTypeAction editTypeAction;

	private EditTypeFormAction editTypeFormAction;

	private FindUsageAction findUsageAction;

	private FindRulesAction findRulesAction;

	private JCheckBox inferredBox;

	private static boolean inferredBoxVisible = true; // TODO: Better solution? Shouldn't be static

	private InstanceNameComponent instanceNameComponent;

	private JPanel mainPanel;

	private JToolBar northToolBar;

	private OWLModel owlModel;

	private RemovePropertyWidgetFromFormAction removePropertyWidgetFromFormAction;

	private JScrollPane scrollPane;

	public final static int SMALL_BUTTON_WIDTH = 15;

	private JPanel southEastPanel;

	private JToolBar southToolBar;

	private Set suppressedTypes = new HashSet();

	private OWLTestInstanceAction testInstanceAction;

	private TriplesComponent triplesComponent;

	private Set actionRefreshProperties;

	private PropertyValueListener propertyValueListener = new PropertyValueAdapter() {
		@Override
		public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues)
		{
			if (actionRefreshProperties.contains(property)) {
				initInstanceDisplayActions(resource);
			}
		}
	};

	public ResourceDisplay(Project project, boolean showHeader, boolean showHeaderLabel)
	{
		this(project);
	}

	public ResourceDisplay(Project project)
	{
		this(project, DEFAULT_TYPE_INSTANCE);
	}

	public ResourceDisplay(Project project, int defaultType)
	{
		super(project);
		this.defaultType = defaultType;
		owlModel = (OWLModel)project.getKnowledgeBase();

		reworkHeaderComponent();

		actionRefreshProperties = new HashSet();
		if (defaultType == DEFAULT_TYPE_CLASS) {
			actionRefreshProperties.add(owlModel.getRDFSSubClassOfProperty());
		}

		centerComponent = (JComponent)getComponent(0);
		remove(centerComponent);

		for (int i = 0; i < centerComponent.getComponentCount(); i++) {
			if (centerComponent.getComponent(i) instanceof JScrollPane) {
				scrollPane = (JScrollPane)centerComponent.getComponent(i);
			}
		}

		northToolBar = OWLUI.createToolBar();
		northToolBar.setOpaque(false);
		Container titlePanel = (Container)getHeaderComponent().getComponent(0);
		titlePanel.add(BorderLayout.EAST, northToolBar);

		findUsageAction = new FindUsageAction(this);

		findRulesAction = new FindRulesAction(this);

		OWLTestManager testManager = owlModel;
		testInstanceAction = new OWLTestInstanceAction(owlModel, testManager, this);
		testInstanceAction.setEnabled(false);
		// setLayout(new BorderLayout());

		suppressedTypes.add(owlModel.getOWLFunctionalPropertyClass());
		// suppressedTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.ANNOTATION_PROPERTY));
		suppressedTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY));
		suppressedTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY));
		suppressedTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY));
		suppressedTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.DEPRECATED_CLASS));
		suppressedTypes.add(owlModel.getRDFSNamedClass(OWLNames.Cls.DEPRECATED_PROPERTY));

		mainPanel = new JPanel(new BorderLayout());
		addDefaultComponentsToMainPanel();
		add(mainPanel);

		inferredBox = new JCheckBox("Inferred View");
		inferredBox.setVisible(inferredBoxVisible);
		inferredBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setInferredMode(inferredBox.isSelected());
			}
		});
		loadHeader();
	}

	private void addDefaultComponentsToMainPanel()
	{
		mainPanel.add(BorderLayout.CENTER, centerComponent);
		southToolBar = ComponentFactory.createToolBar(); // SwingConstants.LEFT);
		southEastPanel = new JPanel();
		southEastPanel.setLayout(new BoxLayout(southEastPanel, BoxLayout.X_AXIS));
		addDefaultToolBarButtons();
		JPanel fillerPanel = new JPanel(new BorderLayout());
		// JPanel linePanel = new JPanel();
		// linePanel.setBackground(Color.gray);
		// linePanel.setPreferredSize(new Dimension(10, 1));
		// fillerPanel.add(BorderLayout.NORTH, linePanel);
		fillerPanel.add(BorderLayout.CENTER, southToolBar);
		fillerPanel.add(BorderLayout.EAST, southEastPanel);
		mainPanel.add(BorderLayout.SOUTH, fillerPanel);
	}

	private void addDefaultToolBarButtons()
	{
		southToolBar.addSeparator(new Dimension(7, 0));
		if (defaultType != ResourcePanel.DEFAULT_TYPE_PROPERTY) {
			ComponentFactory.addToolBarButton(southToolBar, findUsageAction);
			southToolBar.addSeparator();
		}
		ComponentFactory.addToolBarButton(southToolBar, findRulesAction);
		southToolBar.addSeparator();
		ComponentFactory.addToolBarButton(southToolBar, testInstanceAction);
	}

	public void addSelectionListener(SelectionListener listener)
	{
	}

	public void clearSelection()
	{
	}

	protected void reworkHeaderComponent()
	{
		if (defaultType != ResourceDisplay.DEFAULT_TYPE_ONTOLOGY) {
			instanceNameComponent = new InstanceNameComponent();
			HeaderComponent hc = getHeaderComponent();
			Component comp = hc.getComponent();
			Container cont = comp.getParent();
			cont.remove(comp);
			cont.add(BorderLayout.CENTER, instanceNameComponent);
		}
	}

	/**
	 * @see #displayHostResource
	 * @deprecated
	 */
	@Deprecated
	public boolean displayHostInstance(Instance instance)
	{
		if (instance instanceof RDFResource) {
			return displayHostResource((RDFResource)instance);
		} else {
			return false;
		}
	}

	public boolean displayHostResource(RDFResource resource)
	{
		Component comp = this;
		while (comp != null) {
			comp = comp.getParent();
			if (comp instanceof HostResourceDisplay) {
				return ((HostResourceDisplay)comp).displayHostResource(resource);
			}
		}
		return false;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		if (triplesComponent != null) {
			triplesComponent.dispose();
		}
		testInstanceAction = null;
		if (getCurrentInstance() != null && getCurrentInstance() instanceof RDFResource) {
			((RDFResource)getCurrentInstance()).removePropertyValueListener(propertyValueListener);
		}
	}

	protected InstanceNameComponent getInstanceNameComponent()
	{
		return instanceNameComponent;
	}

	@Override
	public Dimension getPreferredSize()
	{
		return mainPanel.getPreferredSize();
	}

	public RDFResource getResource()
	{
		Instance instance = getCurrentInstance();
		if (instance instanceof RDFResource) {
			return (RDFResource)instance;
		} else {
			return null;
		}
	}

	public Collection getSelection()
	{
		if (getCurrentInstance() != null) {
			return Collections.singleton(getCurrentInstance());
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	protected ClsWidget getWidget(Cls type, Instance instance, Cls associatedCls)
	{
		if (isSuppressedType(type, instance)) {
			return null;
		} else {
			return super.getWidget(type, instance, associatedCls);
		}
	}

	private void initInstanceDisplayActions(RDFResource resource)
	{

		findUsageAction.setEnabled(resource != null);
		findRulesAction.setEnabled(resource != null);
		testInstanceAction.setEnabled(resource != null);

		southToolBar.removeAll();
		southEastPanel.removeAll();
		addDefaultToolBarButtons();

		if (resource != null) {
			ResourceActionManager.addResourceActions(southToolBar, this, resource);
			ResourceDisplayPluginManager.initInstanceDisplay(resource, southEastPanel);
		}

		removeYellowStickyButtons();

		northToolBar.removeAll();
		editTypeAction = new EditTypeAction(resource);
		editTypeFormAction = new EditTypeFormAction(resource);
		addPropertyWidgetToFormAction = new AddPropertyWidgetToFormAction(resource, this);
		removePropertyWidgetFromFormAction = new RemovePropertyWidgetFromFormAction(resource, this);
		JButton addSlotWidgetToFormButton = ComponentFactory.addToolBarButton(northToolBar, addPropertyWidgetToFormAction, SMALL_BUTTON_WIDTH);
		addSlotWidgetToFormButton.setOpaque(false);
		addPropertyWidgetToFormAction.activateComboBox(addSlotWidgetToFormButton);
		JButton removeSlotWidgetFromFormAction = ComponentFactory.addToolBarButton(northToolBar, this.removePropertyWidgetFromFormAction, SMALL_BUTTON_WIDTH);
		removeSlotWidgetFromFormAction.setOpaque(false);
		this.removePropertyWidgetFromFormAction.activateComboBox(removeSlotWidgetFromFormAction);
		ComponentFactory.addToolBarButton(northToolBar, editTypeFormAction, SMALL_BUTTON_WIDTH).setOpaque(false);
		ComponentFactory.addToolBarButton(northToolBar, editTypeAction, SMALL_BUTTON_WIDTH).setOpaque(false);
		boolean enabled = false;
		if (resource != null) {
			enabled = resource.isEditable();
		}
		addPropertyWidgetToFormAction.setEnabled(enabled);
		this.removePropertyWidgetFromFormAction.setEnabled(enabled);

		boolean viewEnabled = resource != null;
		editTypeAction.setEnabled(viewEnabled);
		editTypeFormAction.setEnabled(viewEnabled);
	}

	protected boolean isSuppressedType(Cls type, Instance instance)
	{
		if (type instanceof RDFResource) {
			if (((RDFResource)type).isAnonymous()) {
				return true;
			}
		}
		return suppressedTypes.contains(type) && hasUnsuppressedTypes(instance);
	}

	protected boolean hasUnsuppressedTypes(Instance instance)
	{
		Collection<Cls> types = instance.getDirectTypes();

		for (Cls type : types) {
			if (!suppressedTypes.contains(type)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isSuppressedType(Cls type)
	{
		return suppressedTypes.contains(type);
	}

	public boolean isTriplesDisplayed()
	{
		return scrollPane.getParent() == null;
	}

	@Override
	protected void loadHeader()
	{
		if (getCurrentInstance() == null) {
			switch (defaultType) {
			case ResourcePanel.DEFAULT_TYPE_CLASS:
				loadHeaderWithCls(null);
				break;
			case ResourcePanel.DEFAULT_TYPE_PROPERTY:
				loadHeaderWithSlot(null);
				break;
			case ResourcePanel.DEFAULT_TYPE_ONTOLOGY:
				loadHeaderWithOntology(null);
				break;
			default:
				loadHeaderWithSimpleInstance(null);
			}
		} else {
			if (getCurrentInstance() instanceof OWLNamedClass) {
				getHeaderComponent().add(BorderLayout.EAST, inferredBox);
			}
			super.loadHeader();
		}
	}

	@Override
	protected void loadHeaderLabel(Instance instance)
	{

		if (instanceNameComponent != null) {
			instanceNameComponent.setInstance(instance);
		} else {
			super.loadHeaderLabel(instance);
		}

		if (instance instanceof RDFResource) {
			RDFResource RDFResource = (RDFResource)instance;
			Collection inf = RDFResource.getInferredTypes();
			if (!inf.isEmpty()) {
				JLabel label = getHeaderLabel();
				String str = "  (inferred types: ";
				for (Iterator it = inf.iterator(); it.hasNext();) {
					RDFSClass cls = (RDFSClass)it.next();
					str += cls.getBrowserText();
					if (it.hasNext()) {
						str += ", ";
					}
				}
				str += ")";
				label.setText(label.getText() + str);
			}
		}
	}

	@Override
	protected void loadHeaderWithCls(Cls cls)
	{
		super.loadHeaderWithCls(cls);
		String className = cls == null ? "" : ": " + cls.getBrowserText();
		getHeaderComponent().setTitle(getTitleString(cls, "CLASS EDITOR"), false);
	}

	@Override
	protected void loadHeaderWithSimpleInstance(Instance instance)
	{
		super.loadHeaderWithSimpleInstance(instance);
		getHeaderComponent().setTitle(getTitleString(instance, "INDIVIDUAL EDITOR"), false);
		getHeaderComponent().setComponentLabel("For Individual:");
	}

	@Override
	protected void loadHeaderWithSlot(Slot slot)
	{
		super.loadHeaderWithSlot(slot);
		getHeaderComponent().setTitle(getTitleString(slot, "PROPERTY EDITOR"), false);
		getHeaderComponent().setComponentLabel("For Property:");
	}

	protected void loadHeaderWithOntology(OWLOntology owlOntology)
	{
		super.loadHeaderWithSimpleInstance(owlOntology);
		getHeaderComponent().setTitle("Ontology Editor");
		getHeaderComponent().setComponentLabel("For Ontology:");
		getHeaderComponent().setColor(Colors.getInstanceColor());
	}

	protected String getTitleString(Instance instance, String title)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(title);

		if (instance != null) {
			buffer.append(" for ");
			buffer.append(instance.getBrowserText());
			buffer.append("   (instance of ");
			buffer.append(getTypeText(instance));
			buffer.append(")");
		}

		return buffer.toString();
	}

	@Override
	protected String getTypeText(Instance instance)
	{
		if (instance == null) {
			return "";
		}

		StringBuffer typeText = new StringBuffer();
		Iterator i = instance.getDirectTypes().iterator();
		while (i.hasNext()) {
			Cls type = (Cls)i.next();
			typeText.append(type.getBrowserText());
			if (i.hasNext()) {
				typeText.append(", ");
			}
		}
		return typeText.toString();
	}

	public void notifySelectionListeners()
	{
	}

	@Override
	protected void onDirectTypeAdded(Cls type)
	{
		if (!isSuppressedType(type)) {
			super.onDirectTypeAdded(type);
		} else {
			loadHeader();
		}
	}

	@Override
	protected void onDirectTypeRemoved(Cls type)
	{
		if (!isSuppressedType(type)) {
			super.onDirectTypeRemoved(type);
		} else {
			loadHeader();
		}
	}

	public void removeSelectionListener(SelectionListener listener)
	{
	}

	private void removeYellowStickyButtons()
	{
		getHeaderComponent().getToolBar().removeAll();
	}

	public void setInferredMode(boolean value)
	{
		if (value) {
			inferredBox.setVisible(true);
			inferredBoxVisible = true;
		}
		if (inferredBox.isSelected() != value) {
			inferredBox.setSelected(value);
		}
		Container root = (Container)getFirstClsWidget();
		setInferredMode(value, root);
	}

	private void setInferredMode(boolean value, Container cont)
	{
		for (int i = 0; i < cont.getComponentCount(); i++) {
			Component comp = cont.getComponent(i);
			if (comp instanceof InferredModeWidget) {
				((InferredModeWidget)comp).setInferredMode(value);
			}
			if (comp instanceof Container) {
				setInferredMode(value, (Container)comp);
			}
		}
	}

	public void setMode(boolean formView, boolean triplesView)
	{
		Component newComponent = null;
		if (formView) {
			if (triplesView) {
				updateTriplesComponent();
				JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, triplesComponent);
				splitPane.setDividerLocation(0.5);
				newComponent = splitPane;
			} else {
				newComponent = scrollPane;
			}
		} else { // Only triplesView
			updateTriplesComponent();
			newComponent = triplesComponent;
		}
		centerComponent.removeAll();
		centerComponent.add(BorderLayout.NORTH, getHeaderComponent());
		centerComponent.add(BorderLayout.CENTER, newComponent);
		revalidate();
	}

	@Override
	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);
		mainPanel.setBounds(0, 0, width, height);
	}

	@Override
	public void setInstance(Instance instance)
	{
		if (getCurrentInstance() instanceof RDFResource) {
			((RDFResource)getCurrentInstance()).removePropertyValueListener(propertyValueListener);
		}
		if (instance instanceof RDFResource) {
			((RDFResource)instance).addPropertyValueListener(propertyValueListener);
		}

		super.setInstance(instance);
		if (triplesComponent != null) {
			if (instance instanceof RDFResource) {
				triplesComponent.setSubject((RDFResource)instance);
				triplesComponent.setVisible(true);
			} else {
				triplesComponent.setSubject(null);
				triplesComponent.setVisible(false);
			}
		}
		initInstanceDisplayActions(instance instanceof RDFResource ? (RDFResource)instance : null);
	}

	@Override
	public void setInstance(Instance instance, Cls associatedCls)
	{
		super.setInstance(instance, associatedCls);
		initInstanceDisplayActions(instance instanceof RDFResource ? (RDFResource)instance : null);
	}

	public void setResource(RDFResource resource)
	{
		setInstance(resource);
	}

	@Override
	protected boolean shouldDisplaySlot(Cls cls, Slot slot)
	{
		if (slot instanceof RDFProperty && !((RDFProperty)slot).isDomainDefined()) {
			// WidgetMapper mapper = cls.getProject().getWidgetMapper();
			// return mapper.getDefaultWidgetClassName(cls, slot, null) != null;
			return false;
		} else {
			return true;
		}
	}

	public void updateInferredModeOfWidgets()
	{
		setInferredMode(inferredBox.isSelected(), this);
	}

	private void updateTriplesComponent()
	{
		if (triplesComponent == null) {
			triplesComponent = new TriplesComponent(owlModel.getOWLVersionInfoProperty());
		}
		triplesComponent.setSubject((RDFResource)getCurrentInstance());
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		edu.stanford.smi.protege.widget.WidgetUtilities.setEnabledInstanceDisplay(this, enabled);

		if (instanceNameComponent != null) {
			instanceNameComponent.setEnabled(enabled);
		}

		super.setEnabled(enabled);
	};

}
