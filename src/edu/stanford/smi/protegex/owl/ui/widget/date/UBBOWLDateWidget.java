package edu.stanford.smi.protegex.owl.ui.widget.date;


import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLDateWidget;

import javax.swing.*;
import java.util.Collection;
import java.util.Date;

/**
 * @author Hemed Ali, Universitetsbiblioteket i Bergen.
 * <br>
 * The widget automatically copies the current date into the specified slot when the instance
 * is created thereby marking a "creation date".
 * <p>
 * Afterwards, the date can also be manually modified.
 */
public class UBBOWLDateWidget extends OWLDateWidget {

    public UBBOWLDateWidget() {
        //Disable add action
        super(false);
    }

    // A method that triggers a change of a slot value and update the instance accordingly.
    // If the slot value is null (for example, when the instance is created),
    // then the slot-value is set to today's date.
    @Override
    public void setValues(Collection values) {
        if (CollectionUtilities.getFirstItem(values) == null) {
            super.setPropertyValue(new Date());
        }
        super.setValues(values);
    }

    @Override
    public Icon getDeleteActionIcon() {
        return OWLIcons.getImageIcon(OWLIcons.RESET);
    }

    @Override
    public String getDeleteActionText() {
        return "Reset to todays date";
    }

}
