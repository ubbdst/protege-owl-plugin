package edu.stanford.smi.protegex.owl.ui.actions;


import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;

public class MoveToTrashAction extends AllowableAction {
    private static final long serialVersionUID = -1874566858726067172L;
    public static final String CLASS_TRASH = "Trash";

    public MoveToTrashAction(ResourceKey var1, Selectable var2) {
        super(var1, var2);
    }

    public void actionPerformed(ActionEvent var1) {
        if (this.isAllowed()) {
            WaitCursor var2 = new WaitCursor((JComponent) this.getSelectable());
            try {
                this.onMove();
            } finally {
                var2.hide();
            }
        }

    }

    public void onMove() {
        // var1 = new MakeCopiesPanel();
        String var2 = LocalizedText.getText(ResourceKey.COPY_DIALOG_TITLE);
        //int var3 = ModalDialog.showDialog((JComponent) this.getSelectable(), null, var2, 11);
        //if (var3 == 1) {
            prepareMove();
            /*Integer var4 = var1.getNumberOfCopies();
            if (var4 != null) {
                int var5 = var4.intValue();
                if (var5 > 0) {
                    boolean var6 = var1.getIsDeepCopy();
                    this.copy(var5, var6);
                }
            }*/
       // }

    }

    protected void prepareMove() {
        Iterator resource = this.getSelection().iterator();
        while (resource.hasNext()) {
            Instance selectedInstance = (Instance) resource.next();
            move(selectedInstance);
        }
    }


    private OWLNamedClass createTrashClass(OWLModel model){
        OWLNamedClass trashClass = model.getOWLNamedClass(CLASS_TRASH);
        //If it does not exists, create it.
        if(trashClass == null){
            trashClass = model.createOWLNamedClass(CLASS_TRASH);
        }
        return trashClass;
    }


    protected void move(Instance sourceInstance) {
        OWLNamedClass targetCls = createTrashClass((OWLModel)sourceInstance.getKnowledgeBase());
        if (sourceInstance.hasDirectType(targetCls)) {
            Log.getLogger().warning("Cannot move to the same class");
        } else {
            if (sourceInstance instanceof Cls) {
                if (targetCls.isMetaclass()) {
                    sourceInstance.setDirectType(targetCls);
                    Log.getLogger().info(sourceInstance.getName() + " moved to Trash");
                }
            } else if (!targetCls.isMetaclass()) {
                sourceInstance.setDirectType(targetCls);
                Log.getLogger().info("Instance with name " + sourceInstance.getName() + " moved to Trash");
            }
        }
    }
}
