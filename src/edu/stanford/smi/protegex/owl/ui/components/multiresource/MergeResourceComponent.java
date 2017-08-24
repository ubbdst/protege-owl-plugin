
package edu.stanford.smi.protegex.owl.ui.components.multiresource;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.components.AddResourceAction;
import edu.stanford.smi.protegex.owl.ui.components.AddResourcesWithBrowserTextAction;
import edu.stanford.smi.protegex.owl.ui.components.AddablePropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Hemed Al Rywehy
 * @author Holger Knublauch  <holger@knublauch.com>
 *
 * 24-08-2017
 * University of Bergen Library
 */

public class MergeResourceComponent extends AddablePropertyValuesComponent {

    private Action createAction;

    private Action addAction;

    private MultiResourceList list;


    private Action removeAction = new AbstractAction("Remove selected values", OWLIcons.getRemoveIcon(OWLIcons.RDF_INDIVIDUAL)) {
        public void actionPerformed(ActionEvent e) {
            handleRemove();
        }
    };

    public MergeResourceComponent(RDFProperty predicate, boolean symmetric) {
        this(predicate, symmetric, null);
    }


    public MergeResourceComponent(RDFProperty predicate, boolean symmetric, String label) {
        this(predicate, symmetric, label, false);
    }


    public MergeResourceComponent(RDFProperty predicate, boolean symmetric, String label, boolean isReadOnly) {
        super(predicate, label, isReadOnly);

        list = new MultiResourceList(predicate, symmetric);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActions();
            }
        });

        OWLLabeledComponent lc = new OWLLabeledComponent((label == null ? getLabel():label), new JScrollPane(list));

        /*createAction = createCreateAction();
        if (createAction != null) {
            lc.addHeaderButton(createAction);
        }*/

        addAction = createAddAction(symmetric);
        if (addAction != null) {
            lc.addHeaderButton(addAction);
        }
        lc.addHeaderButton(removeAction);
        add(BorderLayout.CENTER, lc);
        updateActions();
    }

    protected AddResourceAction createAddAction(boolean symmetric) {
        return new AddResourcesWithBrowserTextAction(this, symmetric);
    }


    protected Action createCreateAction() {
        return new AbstractAction("Create new resource...", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
            public void actionPerformed(ActionEvent e) {
                list.handleCreate();
            }
        };
    }


    protected Object[] getSelectedObjects() {
        return list.getSelectedValues();
    }


    protected void handleRemove() {
        list.handleRemove();
    }


    public boolean isCreateEnabled() {
        return !isEnumerationProperty();
    }


    @Override
    public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        list.getListModel().setSubject(subject);
        updateActions();
    }


    public void valuesChanged() {
        list.getListModel().updateValues();
    }


    private void updateActions() {
        boolean isReadOnly = isReadOnly();

        /*if (createAction != null) {
            createAction.setEnabled(!isReadOnly && isCreateEnabled());
        }*/
        removeAction.setEnabled(!isReadOnly && list.isRemoveEnabled());
        addAction.setEnabled(!isReadOnly);
    }



}
