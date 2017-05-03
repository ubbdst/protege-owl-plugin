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

package edu.stanford.smi.protegex.owl.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.ListFinder;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;

/**
 *  Panel to allow a user to pick a single instance from a collection of instances.
 *
 * @author    Ray Fergerson <fergerson@smi.stanford.edu>
 */
public class SelectInstanceFromCollectionPanelWithFinder extends JComponent {
    private JList _list;

    public SelectInstanceFromCollectionPanelWithFinder(Collection c, int initialSelection) {
        setLayout(new BorderLayout());
        _list = ComponentFactory.createList(ModalDialog.getCloseAction(this));
        c = removeHidden(c);
        
        ArrayList<Instance> instanceList = new ArrayList<Instance>(c);
        Collections.sort(instanceList);
        
        _list.setListData(instanceList.toArray());
        configureRenderer();
        if (initialSelection >= 0) {
            setSelection(initialSelection);
        }
        add(new JScrollPane(_list), BorderLayout.CENTER);
        add(new ListFinder(_list, "Find"), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(300, 150));
    }

    private boolean isMultiUserClient() {
        boolean isMultiUserClient = false;
        if (_list.getModel().getSize() > 0) {
            Object o = _list.getModel().getElementAt(0);
            if (o instanceof Frame) {
                Frame frame = (Frame) o;
                Project p = frame.getProject();
                isMultiUserClient = p.isMultiUserClient();
            }
        }
        return isMultiUserClient;
    }

    private Icon _clsIcon;

    private void configureRenderer() {
        FrameRenderer renderer;
        if (isMultiUserClient()) {
            // a really strange performance hack
            renderer = new FrameRenderer() {
                protected Icon getIcon(Cls cls) {
                    Icon icon;
                    if (_clsIcon == null) {
                        icon = cls.getIcon();
                        if (!cls.isMetaCls()) {
                            _clsIcon = icon;
                        }
                    } else {
                        icon = _clsIcon;
                    }
                    return icon;
                }
            };
        } else {
            renderer = FrameRenderer.createInstance();
            renderer.setDisplayTrailingIcons(false);
        }
        _list.setCellRenderer(renderer);
    }

    public Instance getSelection() {
        return (Instance) _list.getSelectedValue();
    }

    private static Collection removeHidden(Collection instances) {
        Collection result;
        Project p = ((Instance) (CollectionUtilities.getFirstItem(instances))).getProject();
        if (p.getDisplayHiddenClasses()) {
            result = instances;
        } else {
            result = new ArrayList();
            Iterator i = instances.iterator();
            while (i.hasNext()) {
                Instance instance = (Instance) i.next();
                if (instance.isVisible()) {
                    result.add(instance);
                }
            }
        }
        return result;
    }

    private void setSelection(final int index) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                _list.setSelectedIndex(index);
                _list.ensureIndexIsVisible(index);
            }
        });
    }
}
