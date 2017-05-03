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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.widget.AbstractSlotWidget;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protege.widget.WidgetConfigurationPanel;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * An AbstractPropertyWidget that allows to toggle between various nested Widgets
 * using a JTabbedPane.  Optionally, if you only have two widgets, they can
 * also be displayed beside each other (restricted to two so that this could
 * be implemented using a JSplitPane).
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class MultiWidgetPropertyWidget extends AbstractPropertyWidget {

    /**
     * Indicates whether this is in "all" mode
     */
    private boolean allMode = false;

    /**
     * The JPanel that is used to display all widgets at the same time
     */
    private JPanel allPanel = new JPanel(new GridLayout(2, 1));

    protected FrameListener valueListener = new FrameAdapter() {
        public void ownSlotValueChanged(FrameEvent event) {
            Slot slot = event.getSlot();
            for (Iterator it = widgets.iterator(); it.hasNext();) {
                AbstractSlotWidget slotWidget = (AbstractSlotWidget) it.next();
                if (slot.equals(slotWidget.getSlot())) {
                    slotWidget.loadValues();
                }
            }
        }
    };

    protected Instance instance;

    /**
     * The JTabbedPane the widgets reside in
     */
    private JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * The list of sub-widgets
     */
    private java.util.List widgets = new ArrayList();

    /**
     * Keys: SlotWidgets, Values: WidgetHolder
     */
    private Hashtable widgetContainers = new Hashtable();


    /**
     * Constructs a new MultiWidgetPropertyWidget.  This calls <CODE>addNestedWidget</CODE>.
     */
    public MultiWidgetPropertyWidget() {
       // tabbedPane.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 4));
        //tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        createNestedWidgets();
    }


    /**
     * Adds a new nested widget.  This should only be called during initialization.
     *
     * @param widget     the SlotWidget to add
     * @param slotName   the name of the Slot represented by the widget (null for default)
     * @param tabName    the name to appear on the tab
     * @param widgetName the name to appear in the LabeledComponent
     */
    public void addNestedWidget(SlotWidget widget, String slotName,
                                String tabName, String widgetName) {
        widgets.add(widget);
        WidgetHolder wh = new WidgetHolder();
        tabbedPane.add(tabName, (Component) widget);
        wh.tabName = tabName;
        wh.widgetName = widgetName;
        wh.slotName = slotName;
        widgetContainers.put(widget, wh);
    }


    /**
     * Adds the user-defined tabs to the widget.
     * Overload this method and call <CODE>addNestedWidget(...)</CODE> for each nested
     * widget.  Note that all tab components must be instance of SlotWidget.
     */
    protected abstract void createNestedWidgets();


    protected WidgetDescriptor createWidgetDescriptor(SlotWidget widget, Cls cls, Slot slot) {
        WidgetHolder wc = (WidgetHolder) widgetContainers.get(widget);
        WidgetDescriptor d = getPropertyList().createWidgetDescriptor(slot.getName());
        if (wc != null && wc.widgetName != null) {
            d.setLabel(wc.widgetName);
        }
        return d;
    }


    public void dispose() {
        super.dispose();
        for (Iterator it = widgets.iterator(); it.hasNext();) {
            SlotWidget widget = (SlotWidget) it.next();
            widget.dispose();
        }
        
      //  widgets = null;
        widgetContainers = null;
        
        if (instance != null) {
            instance.removeFrameListener(valueListener);
        }
    }


    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        return new MultiWidgetConfigurationPanel(this);
    }


    public RDFResource getEditedResource() {
        return (RDFResource) instance;
    }


    protected JTabbedPane getTabbedPane() {
        return tabbedPane;
    }


    private WidgetHolder getWidgetHolder(SlotWidget widget) {
        return (WidgetHolder) widgetContainers.get(widget);
    }


    /**
     * Overload this to set layout, add static components, and otherwise modify the appearance
     * of the panel if this is in "all" mode.
     *
     * @param allPanel the Panel that will host all widgets
     */
    protected void initAllPanel(JPanel allPanel, java.util.List widgets) {
        allPanel.setLayout(new GridLayout(widgets.size(), 1));
        for (int i = widgets.size() - 1; i >= 0; i--) {
            Component widget = (Component) widgets.get(i);
            allPanel.add(widget);
        }
    }


    /**
     * Overloaded to forward the nested calls into all nested components.
     */
    public void initialize() {
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, tabbedPane);
        for (Iterator it = widgets.iterator(); it.hasNext();) {
            SlotWidget widget = (SlotWidget) it.next();
            widget.initialize();
        }
    }


    public Iterator listWidgets() {
        return widgets.iterator();
    }


    public void setAllMode(boolean allMode) {
        if (allMode != this.allMode) {
            this.allMode = allMode;
            if (allMode) {
                reinitAllPanel();
                add(BorderLayout.CENTER, allPanel);
            }
            else {
                for (Iterator it = widgets.iterator(); it.hasNext();) {
                    SlotWidget widget = (SlotWidget) it.next();
                    WidgetHolder wc = (WidgetHolder) widgetContainers.get(widget);
                    tabbedPane.addTab(wc.tabName, (Component) widget);
                }
                add(BorderLayout.CENTER, tabbedPane);
            }
        }
    }


    public void reinitAllPanel() {
        allPanel.removeAll();
        initAllPanel(allPanel, widgets);
    }


    public void setAssociatedCls(Cls cls) {
        super.setAssociatedCls(cls);
        for (Iterator it = widgets.iterator(); it.hasNext();) {
            SlotWidget slotWidget = (SlotWidget) it.next();
            if (slotWidget instanceof AbstractSlotWidget) {
                slotWidget.setAssociatedCls(cls);
            }
        }
    }


    public void setBorder(Border border) {
        if (border instanceof EmptyBorder) {
            // Intercept and catch to prevent extra outer border
            super.setBorder(null);
        }
        else {
            super.setBorder(border);
        }
    }


    public void setEditable(boolean b) {
        super.setEditable(b);
        for (Iterator it = widgets.iterator(); it.hasNext();) {
            SlotWidget slotWidget = (SlotWidget) it.next();
            if (slotWidget instanceof AbstractSlotWidget) {
                ((AbstractSlotWidget) slotWidget).setEditable(b);
            }
        }
    }


    /**
     * Overloaded to forward the method call into all nested components.
     */
    public void setInstance(Instance newInstance) {
        if (instance != null) {
            instance.removeFrameListener(valueListener);
        }
        for (Iterator it = widgets.iterator(); it.hasNext();) {
            SlotWidget widget = (SlotWidget) it.next();
            widget.setInstance(newInstance);
        }
        instance = newInstance;
        if (instance != null) {
            instance.addFrameListener(valueListener);
        }
    }


    protected void setSelectedTab(Component tab) {
        tabbedPane.setSelectedComponent(tab);
    }


    /**
     * Overloaded to forward the method call into all nested components.
     */
    public void setup(WidgetDescriptor descriptor, boolean isDesignTime,
                      Project project, Cls cls, Slot slot) {
        super.setup(descriptor, isDesignTime, project, cls, slot);
        setupSubWidgets(slot, cls, isDesignTime, project);
    }


    protected void setupSubWidgets(Slot slot, Cls cls, boolean isDesignTime, Project project) {
        for (Iterator it = widgets.iterator(); it.hasNext();) {
            SlotWidget slotWidget = (SlotWidget) it.next();
            WidgetHolder wc = (WidgetHolder) widgetContainers.get(slotWidget);
            String name = wc.slotName;
            Slot widgetSlot = name == null ? slot : getKnowledgeBase().getSlot(name);
            WidgetDescriptor neo = getPropertyList().getWidgetDescriptor(name);
            if (neo == null) {
                neo = createWidgetDescriptor(slotWidget, cls, widgetSlot);
            }
            slotWidget.setup(neo, isDesignTime, project, cls, widgetSlot);
        }
    }


    private class WidgetHolder {

        String slotName;

        String tabName;

        String widgetName;
    }


    private class MultiWidgetConfigurationPanel extends WidgetConfigurationPanel {

        private java.util.List panels;


        MultiWidgetConfigurationPanel(MultiWidgetPropertyWidget widget) {
            super(widget);
            panels = new ArrayList();
            getTabbedPane().removeAll();
            for (Iterator it = widgets.iterator(); it.hasNext();) {
                SlotWidget slotWidget = (SlotWidget) it.next();
                if (slotWidget instanceof AbstractSlotWidget) {
                    WidgetConfigurationPanel panel =
                            ((AbstractSlotWidget) slotWidget).createWidgetConfigurationPanel();
                    if (panel != null) {
                        panels.add(panel);
                        WidgetHolder holder = getWidgetHolder(slotWidget);
                        getTabbedPane().addTab(holder.tabName, panel);
                    }
                }
            }
        }
    }
    
    public Instance getMultiWidgetInstance(){
    	return instance;
    }
}
