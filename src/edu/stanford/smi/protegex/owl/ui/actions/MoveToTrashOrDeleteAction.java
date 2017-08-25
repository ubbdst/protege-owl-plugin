package edu.stanford.smi.protegex.owl.ui.actions;


import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;

public class MoveToTrashOrDeleteAction extends AllowableAction {
    public static final String CLASS_TRASH = "Trash";
    private static final long serialVersionUID = -1874566858726067172L;

    public MoveToTrashOrDeleteAction(ResourceKey key, Selectable selectable) {
        super(key, selectable);
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        if (isAllowed()) {
            WaitCursor var2 = new WaitCursor((JComponent)this.getSelectable());
            try {
                onMoveOrDelete();
            } finally {
                var2.hide();
            }
        }
    }

    /**
     * Show modal dialog with Yes/No options
     */
    private boolean isDeleteConfirmed(String text) {
        int option = ModalDialog.showMessageDialog((JComponent)this.getSelectable(), text, ModalDialogFactory.MODE_YES_NO);
        return option == ModalDialogFactory.OPTION_YES;
    }


    /**
     * Show modal dialog with OK/Cancel options
     */
    private boolean isMoveConfirmed(String text) {
        int option = ModalDialog.showMessageDialog((JComponent)this.getSelectable(), text, ModalDialogFactory.MODE_OK_CANCEL);
        return option == ModalDialogFactory.OPTION_OK;
    }

    private boolean canDelete(Instance instance) {
        boolean var2 = true;
        if (instance instanceof Cls) {
            Cls var3 = (Cls) instance;
            int var4 = var3.getInstanceCount();
            var2 = var4 == 0;
            if (!var2) {
                String var5 = LocalizedText.getText(ResourceKey.DELETE_CLASS_FAILED_DIALOG_TEXT);
                ModalDialog.showMessageDialog((JComponent) this.getSelectable(), var5);
            }
        }
        return var2;
    }


    public void onDelete(Object var1) {
        Instance instance = (Instance) var1;
        if (canDelete(instance)) {
            if (isDeleteConfirmed("Are you sure you want to permanently delete this instance?")) {
                onAboutToDelete(instance);
                deleteInstance(instance);
                onAfterDelete(instance);
            }
        }

    }

    /**
     * Move instance to a specified class
     *
     * @param instance an instance to move
     * @param clazz a destination class
     */
    private void moveInstance(Instance instance, OWLNamedClass clazz) {
        if (isMoveConfirmed("This instance will be moved to class " + clazz.getLocalName())) {
            instance.setDirectType(clazz);
            Log.getLogger().info("Instance " + instance.getName() + " moved to " + clazz.getLocalName());
        }
    }


    /**
     * Deletes this instance from the model. This will first remove all references of the
     * resource to other resources and then destroy the object.
     */
    public void deleteInstance(Instance instance) {
        Log.getLogger().info("Deleting instance " + instance.getName());
        instance.getKnowledgeBase().deleteFrame(instance);
    }


    @Override
    public void onSelectionChange() {
        boolean var1 = true;
        Iterator var2 = this.getSelection().iterator();
        while (var2.hasNext()) {
            Instance var3 = (Instance) var2.next();
            if (!var3.isEditable()) {
                var1 = false;
                break;
            }
        }
        this.setAllowed(var1);
    }


    /**
     * For each selected instance, decide whether to move to Trash or delete permanently
     */
    protected void onMoveOrDelete() {
        Iterator resource = this.getSelection().iterator();
        while (resource.hasNext()) {
            Instance selectedInstance = (Instance)resource.next();
            moveOrDelete(selectedInstance);
        }
    }


    /**
     * Get Trash class or create new one if it does not exist
     */
    private OWLNamedClass getTrashClass(OWLModel model) {
        OWLNamedClass trashClass = model.getOWLNamedClass(CLASS_TRASH);
        //If it does not exists, create it.
        if (trashClass == null) {
            trashClass = model.createOWLNamedClass(CLASS_TRASH);
        }
        return trashClass;
    }


    protected void onAboutToDelete(Object var1) { }

    protected void onAfterDelete(Object var1) { }



    /**
     * Move or delete instance based on which class it belongs.
     *
     * Idea: If instance has direct type of class Trash, and deletion is confirmed,
     * then delete permanently. Otherwise, move instance to Trash class.
     *
     * @param sourceInstance instance to move or delete
     */
    protected void moveOrDelete(Instance sourceInstance) {
        OWLNamedClass targetCls = getTrashClass((OWLModel) sourceInstance.getKnowledgeBase());
        //If the instance belongs to Trash class and deletion is confirmed, then delete permanently
        if (sourceInstance.hasDirectType(targetCls)) {
            if (canDelete(sourceInstance)) {
                onDelete(sourceInstance);
            }
        } else {//Otherwise, move instance to Trash class if confirmed by a user
            if (sourceInstance instanceof Cls) {
                if (targetCls.isMetaclass()) {
                    moveInstance(sourceInstance, targetCls);
                }
            } else if (!targetCls.isMetaclass()) {
                moveInstance(sourceInstance, targetCls);
            }
        }
    }
}
