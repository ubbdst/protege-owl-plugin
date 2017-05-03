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

package edu.stanford.smi.protegex.owl.ui.repository;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.impl.AbstractLocalRepository;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.repository.action.RepositoryAction;
import edu.stanford.smi.protegex.owl.ui.repository.action.RepositoryActionManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 18, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A panel that displays the contents of an ontology
 * repository.
 */
public class RepositoryPanel extends JPanel {

    private OWLModel owlModel;

    private RepositoryManager manager;

    private Repository repository;

    private AbstractRepositoriesPanel manPanel;

    private JComponent entryHolderPanel;

    private Action removeAction;

    private Action bumpUpAction;

    private Action bumpDownAction;

    private JCheckBox forceReadOnlyCheckBox;

    private LabeledComponent lc;

    private boolean collapsed = false;

    private Set importedOntologies;


    public RepositoryPanel(OWLModel model,
                           RepositoryManager man,
                           Repository rep,
                           AbstractRepositoriesPanel manPan) {
        this.owlModel = model;
        manager = man;
        this.repository = rep;
        manPanel = manPan;
        importedOntologies = this.owlModel.getAllImports();
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        JPanel holderPanel = new JPanel(new BorderLayout(2, 2));
        holderPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        entryHolderPanel = new JPanel();
        entryHolderPanel.setOpaque(false);
        holderPanel.add(entryHolderPanel);
        lc = new LabeledComponent(rep.getRepositoryDescription(), holderPanel);
        entryHolderPanel.setLayout(new BoxLayout(entryHolderPanel, BoxLayout.Y_AXIS));
        setLayout(new BorderLayout());
        add(lc);
        removeAction = new AbstractAction("Remove", OWLIcons.getRemoveIcon()) {
            public void actionPerformed(ActionEvent e) {
                manager.remove(repository);
                manPanel.reloadUI();
            }
        };
        bumpUpAction = new AbstractAction("Move up", OWLIcons.getUpIcon()) {
            public void actionPerformed(ActionEvent e) {
                manager.moveUp(repository);
                manPanel.reloadUI();
            }
        };
        bumpDownAction = new AbstractAction("Move down", OWLIcons.getDownIcon()) {
            public void actionPerformed(ActionEvent e) {
                manager.moveDown(repository);
                manPanel.reloadUI();
            }
        };
        if (repository instanceof AbstractLocalRepository && repository.isSystem() == false) {
            forceReadOnlyCheckBox = new JCheckBox("Force Read-Only");
            forceReadOnlyCheckBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    AbstractLocalRepository localRepository = (AbstractLocalRepository) repository;
                    localRepository.setForceReadOnly(forceReadOnlyCheckBox.isSelected());
                }
            });
            forceReadOnlyCheckBox.setSelected(((AbstractLocalRepository) repository).isForceReadOnly());
            holderPanel.add(forceReadOnlyCheckBox, BorderLayout.NORTH);
        }
        lc.addHeaderButton(bumpUpAction);
        lc.addHeaderButton(bumpDownAction);
        lc.addHeaderButton(removeAction);
        lc.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.getClickCount() == 1 && new Rectangle(0, 0, 20, 20).contains(e.getPoint())) {
                    collapsed = !collapsed;
                    refreshList();
                }

                if (e.isPopupTrigger()) {
                    showRepositoryActionMenu(e);
                }
            }


            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showRepositoryActionMenu(e);
                }
            }
        });
        lc.setOpaque(false);
        removeAction.setEnabled(repository.isSystem() == false);
        refreshList();
        setNotOpaque(this);
    }


    private void showRepositoryActionMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        RepositoryActionManager actionMan = RepositoryActionManager.getInstance();
        for (Iterator it = actionMan.getActions(repository, owlModel).iterator(); it.hasNext();) {
            final RepositoryAction curAction = (RepositoryAction) it.next();
            menu.add(new AbstractAction(curAction.getName()) {
                public void actionPerformed(ActionEvent e) {
                    curAction.actionPerformed(repository, manPanel, owlModel);
                }
            });
        }
        menu.show(lc, e.getX(), e.getY());
    }


    private void refreshList() {
        entryHolderPanel.removeAll();
        UIDefaults uiDefaults = UIManager.getLookAndFeel().getDefaults();
        Icon icon;
        if (collapsed) {
            icon = uiDefaults.getIcon("Tree.collapsedIcon");
        }
        else {
            icon = uiDefaults.getIcon("Tree.expandedIcon");
            TreeSet ts = new TreeSet(repository.getOntologies());
            for (Iterator it = ts.iterator(); it.hasNext();) {
                URI uri = (URI) it.next();
                boolean imported = false;
                if (importedOntologies.contains(uri.toString())) {
                    imported = manager.getRepository(uri) == repository;
                }
                entryHolderPanel.add(new RepositoryEntryLabel(owlModel, uri, repository.getOntologyLocationDescription(uri), imported));
            }
        }
        lc.setHeaderIcon(icon);
        setNotOpaque(this);
        manPanel.revalidate();

    }


    private void setNotOpaque(JComponent c) {
        c.setOpaque(false);
        Component[] components = c.getComponents();
        for (int i = 0; i < components.length; i++) {
            Component curComp = components[i];
            if (curComp instanceof JComponent) {
                setNotOpaque((JComponent) curComp);
            }
        }
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color oldColor = g.getColor();
        g.setColor(Color.GRAY);
        g.drawLine(3, getHeight() - 1, getWidth() - 3, getHeight() - 1);
        g.setColor(oldColor);
    }

}

