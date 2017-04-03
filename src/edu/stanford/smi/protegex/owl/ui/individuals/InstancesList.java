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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ListModel;

import edu.stanford.smi.protege.util.DoubleClickActionAdapter;
import edu.stanford.smi.protege.util.SimpleListModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.TripleSelectable;
import edu.stanford.smi.protegex.owl.ui.components.TooltippedSelectableList;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InstancesList extends TooltippedSelectableList
        implements TripleSelectable, HostResourceDisplay {

    private List classes = Collections.EMPTY_LIST;

    public InstancesList(Action action) {
        super();
        setModel(new SimpleListModel());
        if (action != null) {
            addMouseListener(new DoubleClickActionAdapter(action));
        }
        setCellRenderer(new ResourceRenderer());
        setFixedCellHeight(getHeight(this));
    }


    private static int getHeight(Component c) {
        return c.getFontMetrics(c.getFont()).getHeight() + 4;
    }


    public List getPrototypeTriples() {
        List results = new ArrayList();
        Iterator it = classes.iterator();
        while (it.hasNext()) {
            Object c = it.next();
            if (c instanceof RDFSNamedClass) {
                RDFResource subject = (RDFResource) c;
                RDFProperty predicate = subject.getOWLModel().getRDFTypeProperty();
                results.add(new DefaultTriple(subject, predicate, null));
            }
        }
        return results;
    }


    public List getSelectedTriples() {
        List results = new ArrayList();
        Set classesSet = new HashSet(classes);
        Iterator instances = getSelection().iterator();
        while (instances.hasNext()) {
            Object instance = instances.next();
            if (instance instanceof RDFResource) {
                RDFResource object = (RDFResource) instance;
                RDFProperty predicate = object.getOWLModel().getRDFTypeProperty();
                Iterator types = object.listRDFTypes();
                while (types.hasNext()) {
                    RDFSClass type = (RDFSClass) types.next();
                    if (classesSet.contains(type)) {
                        results.add(new DefaultTriple(type, predicate, object));
                    }
                }
            }
        }
        return results;
    }


    public void setClasses(Collection classes) {
        this.classes = new ArrayList(classes);
    }


    public void setSelectedTriples(Collection triples) {
        Set indices = new HashSet();
        ListModel listModel = getModel();
        for (Iterator it = triples.iterator(); it.hasNext();) {
            Triple triple = (Triple) it.next();
            for (int i = 0; i < listModel.getSize(); i++) {
                if (listModel.getElementAt(i).equals(triple.getObject())) {
                    indices.add(new Integer(i));
                    break;
                }
            }
        }

        int[] sels = new int[indices.size()];
        Iterator it = indices.iterator();
        for (int i = 0; it.hasNext(); i++) {
            Integer integer = (Integer) it.next();
            sels[i] = integer.intValue();
        }
        setSelectedIndices(sels);
    }

    public boolean displayHostResource(RDFResource resource) {
        for (int i = getModel().getSize() - 1; i >= 0; i--) {
            if (resource.equals(getModel().getElementAt(i))) {
                setSelectedValue(resource);
                return true;
            }
        }
        return false;
    }
}
