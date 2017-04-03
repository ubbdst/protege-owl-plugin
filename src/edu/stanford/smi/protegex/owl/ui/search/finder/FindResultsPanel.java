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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

/**
 * A JPanel for modal dialogs that can be used to String-matching search for a certain
 * value of a selected Slot.
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         12-Oct-2005
 */
public class FindResultsPanel extends JComponent {
    private static Logger log = Log.getLogger(FindResultsPanel.class);

    private static final String SEARCH_PATTERN_LABEL = "Search Pattern";
    private static final String SAVE_RESULTS_LABEL = "Save Results";

    //private java.util.List renameListeners = new ArrayList();

    private Find find;

    private JButton saveResultButton;

    private JComboBox searchTypeCombo;

    private JTextField textField;
    
    private JButton searchButton;

    private JPanel mainPanel;

    private AbstractFindResultsView view;
    
    // action to send these results to the Results panel at the south of the main window
    private Action saveResultsAction = new AbstractAction(SAVE_RESULTS_LABEL, Icons.getDownIcon()) {
        public void actionPerformed(ActionEvent e) {
            view.requestDispose();
            setSaveResultsEnabled(false);
            RetainFindResultsPanel resultsPanel =
                    new RetainFindResultsPanel(find.getModel(), FindResultsPanel.this);
            ResultsPanelManager.addResultsPanel(find.getModel(), resultsPanel, true);
        }
    };


    public FindResultsPanel(final Find find, AbstractFindResultsView view) {
        super();

        setLayout(new BorderLayout());

        this.find = find;
        this.view = view;

        textField = createTextField();
        find.addResultListener(new SearchAdapter() {
            @Override
            public void searchCancelledEvent(Find source) {
                if (textField != null) {
                    textField.setEnabled(true);
                }
            }
            
            @Override
            public void searchCompleteEvent(int numResults, Find source) {  
                if (textField != null) {
                    textField.setEnabled(true);
                }
            }
        });
        textField.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               refresh();
            } 
        });

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               refresh();
            } 
        });
        
        // when in the dialog, this component never gets a componentShown
        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                requestFocus();
            }
        });

        view.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectResource();
                }
            }
        });

        searchTypeCombo = createTypeCombo();

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel(SEARCH_PATTERN_LABEL));
        northPanel.add(searchTypeCombo);
        northPanel.add(textField);
        northPanel.add(searchButton);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(view, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

//        fireResultsChanged();
    }

    public void setSaveResultsEnabled(boolean canSave) {
        if (canSave) {
            if (saveResultButton == null) {
                saveResultButton = new JButton(saveResultsAction);
                mainPanel.add(saveResultButton, BorderLayout.SOUTH);
            }
        } else {
            if (saveResultButton != null) {
                mainPanel.remove(saveResultButton);
                saveResultButton = null;
            }
        }
    }

    public void refresh() {
        refresh(find.getSearchType());
    }

    /**
     * Should run the search and refresh in a seperate thread
     *
     * @param searchType
     */
    public void refresh(int searchType) {
        textField.setEnabled(false);
        String searchString = textField.getText();
        if (!find.getLastSearch().equals(searchString) || find.getSearchType() != searchType) {
            find.startSearch(searchString, searchType);
        }
        else {
            textField.setEnabled(true);
        }
    }

//    private void fireResultsChanged() {
//        for (Iterator i = renameListeners.iterator(); i.hasNext();) {
//            ((RenameListener) i.next()).rename(find.getSummaryText(), this);
//        }
//    }

    public void selectResource() {
        view.selectResource();
    }

//    public void addRenameListener(RenameListener listener) {
//        renameListeners.add(listener);
//    }

    public void requestFocus() {
        textField.requestFocus();
    }


    private JTextField createTextField() {
        JTextField tField = new JTextField();
        FontMetrics metrics = getFontMetrics(tField.getFont());
        int textheight = metrics.getHeight() + metrics.getDescent();
        tField.setPreferredSize(new Dimension(120, textheight));
        tField.setText(find.getLastSearch());

        tField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        view.requestFocus();
                        break;
                }
            }
        });

        return tField;
    }


    private JComboBox createTypeCombo() {
        JComboBox combo = new JComboBox();
        combo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                value = Find.searchTypeString[((Integer) value).intValue()];
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        combo.addItem(new Integer(Find.CONTAINS));
        combo.addItem(new Integer(Find.STARTS_WITH));
        combo.addItem(new Integer(Find.ENDS_WITH));
        combo.addItem(new Integer(Find.EXACTLY_MATCHES));
        combo.setSelectedItem(new Integer(find.getSearchType()));
        combo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int newValue = ((Integer) e.getItem()).intValue();
                int oldValue = FindResultsPanel.this.find.getSearchType();
                if (oldValue != newValue) {
                    refresh(newValue);
                }
            }
        });

        return combo;
    }
}