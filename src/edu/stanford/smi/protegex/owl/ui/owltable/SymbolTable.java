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

package edu.stanford.smi.protegex.owl.ui.owltable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.CellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.SelectableTable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protege.util.TablePopupMenuMouseListener;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.TripleSelectable;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.code.SymbolCellEditor;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.code.SymbolPanel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

/**
 * A JTable in which one column displays an expression in an expression
 * language like the OWL compact syntax or SWRL.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class SymbolTable extends SelectableTable implements TripleSelectable {

    protected OWLTableAction editAnnotationsAction =
            new AbstractOWLTableAction("Edit Annotation Properties...",
                                       OWLIcons.getImageIcon("Annotation")) {
                public void actionPerformed(ActionEvent e) {
                    editAnnotations();
                }


                public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
                    if (cls instanceof OWLAnonymousClass) {
                        return cls.isEditable();
                    }
                    else {
                        return false;
                    }
                }
            };

    protected OWLTableAction editMultiLineAction =
            new AbstractOWLTableAction("Edit expression in multi-line editor...",
                                       Icons.getBlankIcon()) {
                public void actionPerformed(ActionEvent e) {
                    editMultiLine();
                }


                public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
                    if (tableModel.isCellEditable(rowIndex, 0)) {
                        return cls != null && cls.isEditable();
                    }
                    else {
                        return false;
                    }
                }
            };

    public final static int INFINITE_TIME = 1000000;

    private OWLModel owlModel;

    private SymbolCellEditor symbolCellEditor;

    private SymbolEditorComponent symbolEditorComponent;

    private SymbolPanel symbolPanel;

    private SymbolTableModel tableModel;

    /**
     * The list of OWLTableAction objects that shall be disabled or enabled depending on
     * the selected row
     */
    private java.util.List actions = new ArrayList();

    private boolean withIcon;


    public SymbolTable(SymbolTableModel tableModel,
                       OWLModel owlModel,
                       boolean withIcon,
                       SymbolPanel symbolPanel) {
        this.owlModel = owlModel;
        this.withIcon = withIcon;
        this.symbolPanel = symbolPanel;
        this.tableModel = tableModel;
        setRowHeight(Math.max(getFontMetrics(getFont()).getHeight(), 16));
        setModel(tableModel);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        getTableHeader().setReorderingAllowed(false);
        setShowGrid(false);
        setRowMargin(0);
        setIntercellSpacing(new Dimension(0, 0));
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initSymbolColumn();
        addSelectionListener(new SelectionListener() {
			public void selectionChanged(SelectionEvent event) {
				enableActions();				
			}        	
        });
        final int oldDelay = ToolTipManager.sharedInstance().getDismissDelay();
        addMouseListener(new MouseAdapter() {
            @Override
			public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setDismissDelay(oldDelay);
            }
        });
        addMouseListener(new TablePopupMenuMouseListener(this) {
            @Override
			public JPopupMenu getPopupMenu() {
                return createPopupMenu();
            }


            // Overloaded to fix bug
            @Override
			public void setSelection(JComponent c, int x, int y) {
                if (getSelectedRowCount() > 0) {
                    super.setSelection(c, x, y);
                }
            }
        });
    }


    protected void addNavigationMenuItems(RDFResource resource, JPopupMenu menu) {
        Collection set = getNavigationMenuItems(resource);
        if (!set.isEmpty()) {
            RDFResource[] resources = (RDFResource[]) set.toArray(new RDFResource[0]);
            Arrays.sort(resources);
            for (int i = 0; i < resources.length; i++) {
                final RDFResource targetResource = resources[i];
                menu.add(new AbstractAction("Navigate to " + targetResource.getBrowserText(),
                                            ProtegeUI.getIcon(targetResource)) {
                    public void actionPerformed(ActionEvent e) {
                        navigateTo(targetResource);
                    }
                });
            }
        }
    }


    public SymbolEditorComponent getSymbolEditorComponent() {
        return symbolEditorComponent;
    }


    protected abstract Collection getNavigationMenuItems(RDFResource RDFResource);


    /**
     * Overloaded to prevent the creation of the table header.
     * Found at <a href="http://www.codeguru.com/java/articles/180.shtml">CodeGuru</A>.
     */
    @Override
	protected void configureEnclosingScrollPane() {
        if (isTableHeaderHidden()) {
            Container p = getParent();
            if (p instanceof JViewport) {
                Container gp = p.getParent();
                if (gp instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) gp;
                    // Make certain we are the viewPort's view and not, for
                    // example, the rowHeaderView of the scrollPane -
                    // an implementor of fixed columns might do this.
                    JViewport viewport = scrollPane.getViewport();
                    if (viewport == null || viewport.getView() != this) {
                        return;
                    }
                    // scrollPane.setColumnHeaderView(getTableHeader());
                    scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
                    scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
                }
            }
        }
        else {
            super.configureEnclosingScrollPane();
        }
    }


    protected ResourceRenderer createOWLFrameRenderer() {
        return new ResourceRenderer();
    }


    protected JPopupMenu createPopupMenu() {
        RDFResource selectedInstance = getSelectedOWLInstance();
        JPopupMenu menu = new JPopupMenu();
        if (selectedInstance != null) {
            addNavigationMenuItems(selectedInstance, menu);
        }
        for (Iterator it = actions.iterator(); it.hasNext();) {
            OWLTableAction action = (OWLTableAction) it.next();
            if (action == null) {
                if (it.hasNext()) {
                    menu.addSeparator();
                }
            }
            else {  //if (action.isEnabledFor(cls)) {
                menu.add(action);
            }
        }
        if (selectedInstance != null) {
            ResourceActionManager.addResourceActions(menu, this, selectedInstance);
        }
        if (menu.getComponentCount() > 0) {
            return menu;
        }
        else {
            return null;
        }
    }


    protected abstract SymbolEditorComponent createSymbolEditorComponent(OWLModel model,
                                                                         SymbolErrorDisplay errorDisplay);


    protected void editAnnotations() {
        RDFResource selectedInstance = getSelectedOWLInstance();
        selectedInstance.getProject().show(selectedInstance);
    }


    @Override
	public boolean editCellAt(int row, int column, EventObject e) {
        boolean result = super.editCellAt(row, column, e);
        if (column == tableModel.getSymbolColumnIndex()) {
            symbolEditorComponent.getTextComponent().requestFocus();
            if (e instanceof MouseEvent) {
                int mouseX = ((MouseEvent) e).getX();
                if (withIcon) {
                    mouseX -= symbolEditorComponent.getX();
                }
                String str = symbolEditorComponent.getTextComponent().getText();
                FontMetrics fm = symbolEditorComponent.getTextComponent().getFontMetrics(symbolEditorComponent.getTextComponent().getFont());
                for (int index = 1; index < str.length(); index++) {
                    if (fm.stringWidth(str.substring(0, index)) >= mouseX) {
                        symbolEditorComponent.getTextComponent().setCaretPosition(index - 1);
                        break;
                    }
                }
            }
        }
        return result;
    }


    /**
     * Programmatically starts editing the OWL Expression of a given row.
     */
    public void editExpression(int row) {
        int owlColumn = tableModel.getSymbolColumnIndex();
        getSelectionModel().setSelectionInterval(row, row);
        scrollRectToVisible(getCellRect(row, 0, true));
        editCellAt(row, owlColumn);
        symbolEditorComponent.getTextComponent().requestFocus();
    }


    protected void editMultiLine() {
        RDFResource input = getSelectedOWLInstance();
        String newExpression = editMultiLine(input);
        if (newExpression != null) {
            tableModel.setValueAt(newExpression, getSelectedRow(), tableModel.getSymbolColumnIndex());
        }
    }


    protected abstract String editMultiLine(RDFResource input);


    public void enableActions() {
        enableActions(actions);
    }


    protected void enableActions(Collection actions) {
    }


    public Clipboard getClipboard() {
        return getToolkit().getSystemClipboard();
    }


    @Override
	public Dimension getPreferredSize() {
        Dimension s = super.getPreferredSize();
        int h = getRowHeight();   // was: 20
        return new Dimension(s.width, Math.max(h, s.height));
    }


    public ResourceRenderer getOWLFrameRenderer() {
        TableColumn owlColumn = getColumnModel().getColumn(tableModel.getSymbolColumnIndex());
        return (ResourceRenderer) owlColumn.getCellRenderer();
    }


    public OWLModel getOWLModel() {
        return owlModel;
    }


    private JLayeredPane getParentLayeredPane() {
        Component c = getParent();
        while (c != null && !(c instanceof JLayeredPane)) {
            c = c.getParent();
        }
        return (JLayeredPane) c;
    }


    public List getPrototypeTriples() {
        int[] sels = getSelectedRows();
        if (sels.length == 0) {
            RDFResource subject = tableModel.getSubject();
            RDFProperty predicate = tableModel.getPredicate(-1);
            if (subject != null && predicate != null) {
                return Collections.singletonList(new DefaultTriple(subject, predicate, null));
            }
            else {
                return Collections.EMPTY_LIST;
            }
        }
        else {
            Triple triple = (Triple) getSelectedTriples().get(0);
            return Collections.singletonList(new DefaultTriple(triple.getSubject(), triple.getPredicate(), null));
        }
    }


    public RDFResource getSelectedResource() {
        int index = getSelectedRow();
        if (index >= 0 && index < tableModel.getRowCount()) {
            return tableModel.getRDFResource(index);
        }
        else {
            return null;
        }
    }


    /**
     * Overloaded to treat a nasty Swing bug (returns sometimes values outside
     * of range etc).
     *
     * @return the (valid) indices of the selected values
     */
    @Override
	public int[] getSelectedRows() {
        int[] sels = super.getSelectedRows();
        List oks = new ArrayList();
        for (int i = 0; i < sels.length; i++) {
            int sel = sels[i];
            if (sel >= 0 && sel < tableModel.getRowCount()) {
                oks.add(new Integer(sel));
            }
        }
        int[] result = new int[oks.size()];
        Iterator it = oks.iterator();
        for (int i = 0; it.hasNext(); i++) {
            Integer integer = (Integer) it.next();
            result[i] = integer.intValue();
        }
        return result;
    }


    /**
     * @see #getSelectedResource()
     * @deprecated renamed to getSelectedResource
     */
    @Deprecated
	public RDFResource getSelectedOWLInstance() {
        return getSelectedResource();
    }


    public List getSelectedTriples() {
        int[] sels = getSelectedRows();
        if (sels.length == 0) {
            return Collections.EMPTY_LIST;
        }
        else {
            List result = new ArrayList();
            RDFResource subject = tableModel.getSubject();
            if (subject != null) {
                for (int i = 0; i < sels.length; i++) {
                    int row = sels[i];
                    if (row >= 0 && row < tableModel.getRowCount()) {
                        RDFProperty predicate = tableModel.getPredicate(row);
                        Object object = tableModel.getRDFResource(row);
                        if (predicate != null && object != null) {
                            result.add(new DefaultTriple(subject, predicate, object));
                        }
                    }
                }
            }
            return result;
        }
    }


    protected CellEditor getSymbolCellEditor() {
        return symbolCellEditor;
    }

//    protected SymbolTextField getSymbolTextField() {
//        return symbolTextField;
//    }


    protected SymbolPanel getSymbolPanel() {
        return symbolPanel;
    }


    protected SymbolTableModel getSymbolTableModel() {
        return tableModel;
    }


    @Override
	public String getToolTipText(MouseEvent event) {
        ToolTipManager.sharedInstance().setDismissDelay(INFINITE_TIME);
//        if (OWLMenuProjectPlugin.isProseActivated()) {
        int rowCount = getModel().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Rectangle r = getCellRect(i, 0, false);
            r.setSize(getWidth(), r.height);
            if (r.contains(event.getPoint())) {
                RDFResource RDFResource = tableModel.getRDFResource(i);
                return getToolTipText(RDFResource);
            }
        }
//        }
        return null;
    }


    protected abstract String getToolTipText(RDFResource RDFResource);


    public void hideSymbolPanel() {
        if (symbolPanel != null) {
            Container parent = symbolPanel.getParent();
            if (parent != null) {
                parent.remove(symbolPanel);
                Container top = getTopLevelAncestor();
                if (top instanceof JFrame) {
                    ((JFrame) top).getContentPane().repaint();
                }
                else if (top instanceof JWindow) {
                    ((JWindow) top).getContentPane().repaint();
                }
                else if (top instanceof JDialog) {
                    ((JDialog) top).getContentPane().repaint();
                }
                else {
                    ProjectView projectView = ProtegeUI.getProjectView(getOWLModel().getProject());
                    projectView.getTabbedPane().repaint();
                }
            }
        }
    }


    protected int getPreferredRowHeight(JTable table, SymbolEditorComponent editorComponent) {
        return getRowHeight();
    }


    private void initSymbolColumn() {

        int owlColumnIndex = tableModel.getSymbolColumnIndex();
        TableColumn owlColumn = getColumnModel().getColumn(owlColumnIndex);

        symbolEditorComponent = createSymbolEditorComponent(owlModel, symbolPanel);
        symbolPanel.setSymbolEditor(symbolEditorComponent);
        symbolCellEditor = new SymbolCellEditor(symbolEditorComponent) {
            @Override
			public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected,
                                                         int row, int column) {
                Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
                table.setRowHeight(row, getPreferredRowHeight(table, symbolEditorComponent));
                if (withIcon) {
                    JPanel result = new JPanel(new BorderLayout(1, 0));
                    result.setOpaque(false);
                    result.add(BorderLayout.CENTER, c);
                    RDFResource resource = tableModel.getRDFResource(row);
                    Icon icon = getIcon(resource);
                    result.add(BorderLayout.WEST, new JLabel(icon));
                    return result;
                }
                else {
                    return c;
                }

            }
        };
        symbolEditorComponent.setSymbolEditorHandler(symbolCellEditor);
        symbolCellEditor.addCellEditorListener(new CellEditorListener() {

            public void editingCanceled(ChangeEvent e) {
                hideSymbolPanel();
            }


            public void editingStopped(ChangeEvent e) {
                hideSymbolPanel();
            }
        });

        initSymbolColumnRenderer();
    }


    protected Icon getIcon(RDFResource resource) {
        Icon icon = null;
        if (resource == null) {
            icon = getDefaultCellEditorIcon(resource);
        }
        else {
            icon = tableModel.getIcon(resource);
        }
        return icon;
    }


    protected Icon getDefaultCellEditorIcon(RDFResource RDFResource) {
        return OWLIcons.getPrimitiveClsIcon();
    }


    private void initSymbolColumnRenderer() {
        if (tableModel != null) {
            int owlColumnIndex = tableModel.getSymbolColumnIndex();
            TableColumn owlColumn = getColumnModel().getColumn(owlColumnIndex);
            if (withIcon) {
                FrameRenderer renderer = createOWLFrameRenderer();
                renderer.setDisplayHiddenIcon(false);
                owlColumn.setCellRenderer(renderer);
            }
            owlColumn.setCellEditor(symbolCellEditor);
        }
    }


    protected boolean isTableHeaderHidden() {
        return getModel().getColumnCount() == 1;
    }


    protected void navigateTo(RDFResource resource) {
        ResultsPanelManager.showHostResource(resource);
    }


    @Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
        Component c = super.prepareEditor(editor, row, column);
        if (column == tableModel.getSymbolColumnIndex()) {
            Object value = getModel().getValueAt(row, column);
            String expression = (value instanceof RDFResource) ?
                    ((RDFResource) value).getBrowserText() : (String) value;
            showSymbolPanel(expression.length() == 0);
        }
        return c;
    }


    @Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        if (column == tableModel.getSymbolColumnIndex() && renderer instanceof FrameRenderer) {
            RDFResource RDFResource = tableModel.getRDFResource(row);
            boolean isSelected = isCellSelected(row, column);
            boolean rowIsAnchor = (selectionModel.getAnchorSelectionIndex() == row);
            boolean colIsAnchor =
                    (columnModel.getSelectionModel().getAnchorSelectionIndex() == column);
            boolean hasFocus = (rowIsAnchor && colIsAnchor) && isFocusOwner();
            return renderer.getTableCellRendererComponent(this, RDFResource,
                                                          isSelected, hasFocus,
                                                          row, column);
        }
        else {
            return super.prepareRenderer(renderer, row, column);
        }
    }


    /**
     * Registers a OWLTableAction so that it will be enabled or disabled in the future.
     * This will set the Action to disabled by default, because initially, the table does not
     * have a row selected.
     *
     * @param action the Action to register
     */
    public void registerAction(OWLTableAction action) {
        actions.add(action);
        action.setEnabled(false);
    }


    /**
     * Registers a OWLTableAction so that it will be enabled or disabled in the future.
     * This will set the Action to disabled by default, because initially, the table does not
     * have a row selected.
     *
     * @param action the Action to register
     * @param index  the index in the context menu
     */
    public void registerAction(OWLTableAction action, int index) {
        actions.add(index, action);
        action.setEnabled(false);
    }


    /**
     * Inserts a separator behind the previously added action.
     * This separator will display in the popup menu of the selected class.
     */
    public void registerActionSeparator() {
        actions.add(null);
    }


    @Override
	public void setModel(TableModel newModel) {
        TableModel oldModel = getModel();
        if (oldModel instanceof Disposable) {
            ((Disposable) oldModel).dispose();
        }
        super.setModel(newModel);
        if (newModel instanceof SymbolTableModel) {
            tableModel = (SymbolTableModel) newModel;
        }
        initSymbolColumnRenderer();
    }


    public void setSelectedRow(RDFResource resource) {
        final String browserText = resource.getBrowserText();
        setSelectedRow(browserText);
    }


    public void setSelectedRow(final String browserText) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            RDFResource rowCls = tableModel.getRDFResource(i);
            if (rowCls != null && browserText.equals(rowCls.getBrowserText())) {
                getSelectionModel().setSelectionInterval(i, i);
            }
        }
    }


    public void setSelectedRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tableModel.getRowCount()) {
            getSelectionModel().clearSelection();
        }
        else {
            getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
        }
    }


    public void setSelectedTriples(Collection triples) {
        getSelectionModel().clearSelection();
        for (Iterator it = triples.iterator(); it.hasNext();) {
            Triple triple = (Triple) it.next();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (triple.getObject().equals(tableModel.getRDFResource(i))) {
                    getSelectionModel().addSelectionInterval(i, i);
                    break;
                }
            }
        }
    }


    protected void showSymbolPanel(boolean errorFlag) {
        JLayeredPane desktop = getParentLayeredPane();
        int owlColumn = tableModel.getSymbolColumnIndex();
        Rectangle r = getCellRect(getSelectedRow(), owlColumn, true);
        Point tableLocation = getLocationOnScreen();
        Point desktopLocation = desktop.getLocationOnScreen();
        r.translate(tableLocation.x - desktopLocation.x, tableLocation.y - desktopLocation.y);
        Dimension pref = symbolPanel.getPreferredSize();
        int x = getX() + r.x;
        if (pref.width > r.width) {
            x = Math.max(0, getX() + r.x - (pref.width - r.width));
        }
        int y = r.y + r.height + 4;
        if (y + symbolPanel.getHeight() >= desktop.getHeight()) {
            y = r.y - symbolPanel.getHeight();
        }
        symbolPanel.setLocation(x, y);
        symbolPanel.displayError((Throwable) null);
        symbolPanel.setErrorFlag(errorFlag);
        symbolPanel.enableActions(true, true);
        desktop.setLayer(symbolPanel, JLayeredPane.POPUP_LAYER.intValue());
        desktop.add(symbolPanel);
    }
}
