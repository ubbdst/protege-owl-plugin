package edu.stanford.smi.protegex.owl.ui.widget.date;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.toedter.calendar.JDateChooser;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.ReadOnlyWidgetConfigurationPanel;
import edu.stanford.smi.protege.widget.WidgetConfigurationPanel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;

/**
 * @author Hemed Ali, Universitetsbiblioteket i Bergen.
 *         <br>
 *         Description: This plugin is a clone of OWLDateWidget from the Protege 3.5 owl-plugin with some custom modifications.
 *         This widget automatically copies the current date into the specified slot when the instance is created thereby marking a "creation date".
 *         Afterwards, the date can also be manually modified.
 */
public class UBBOWLDateWidget extends AbstractPropertyWidget {

    //A method to delete a date value when delete icon is clicked.
    //Now, when delete button is clicked, the date is set to a current date. See setValues(Collection col) method.
    private final Action deleteAction = new AbstractAction("Delete value", OWLIcons.getDeleteIcon()) {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteValue();
        }
    };
    private final Action setAction = new AbstractAction("Set value", OWLIcons.getAddIcon()) {
        @Override
        public void actionPerformed(ActionEvent e) {
            setPropertyValue(new Date());
        }
    };
    private JDateChooser dateChooser;
    private LabeledComponent lc;
    private boolean ignoreUpdate = false;
    //A listener to update values if change in date is triggered.
    private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("date".equals(evt.getPropertyName())) {
                updateValues();
            }
        }
    };

    public static Date getDate(String s) {
        if (s == null) {
            return null;
        }
        Date date = new Date();

        int index = s.indexOf("T");
        if (index >= 0) {
            s = s.substring(0, index);
        }
        //TODO: Does not consider the timezone!
        int zindex = s.indexOf("Z");
        if (zindex >= 0) {
            s = s.substring(0, zindex);
        }
        String[] ss = s.split("-");
        if (ss.length >= 3) {
            try {
                int year = Integer.parseInt(ss[0]);
                int month = Integer.parseInt(ss[1]) - 1;
                int day = Integer.parseInt(ss[2]);
                date = new Date(new GregorianCalendar(year, month, day).getTimeInMillis());
            } catch (Exception ex) {
                Log.getLogger().log(Level.WARNING, "Could not parse value {0}: {1}", new Object[]{s, ex.getMessage()});
            }
        }
        return date;
    }

    //Always display the widget.
    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return true; //OWLWidgetMapper.isSuitable(UBBOWLDateWidget.class, cls, slot);
    }

    //This method is called first to initialize the plugin.
    @Override
    public void initialize() {
        setLayout(new BorderLayout());
        setPreferredColumns(2);
        setPreferredRows(1);

        dateChooser = new JDateChooser();
        lc = new LabeledComponent(getRDFProperty().getBrowserText(), getCenterComponent());
        lc.addHeaderButton(setAction);
        lc.addHeaderButton(deleteAction);
        add(BorderLayout.CENTER, lc);
        enabledCompListeners();

    }

    protected RDFSLiteral createPropertyValue(Date date) {
        String value = XMLSchemaDatatypes.getDateString(date);
        RDFSDatatype datatype = getOWLModel().getRDFSDatatypeByURI(XSDDatatype.XSDdate.getURI());
        return getOWLModel().createRDFSLiteral(value, datatype);
    }

    protected void deleteValue() {
        getEditedResource().setPropertyValue(getRDFProperty(), null);
    }

    protected Component getCenterComponent() {
        return dateChooser;
    }

    protected Date getDate() {
        return dateChooser.getDate();
    }

    private void setDateChooserValue() {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        Object value = resource.getPropertyValue(property);
        setValue(value == null ? null : value.toString());
    }

    protected void setValue(String s) {
        disableCompListeners();
        Date date = getDate(s);
        dateChooser.setDate(date);
        enabledCompListeners();
    }

    private void setPropertyValue(Date date) {
        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        if (resource != null && property != null) {
            if (date == null) {
                resource.setPropertyValue(property, null);
            } else {
                Object value = createPropertyValue(date);
                resource.setPropertyValue(property, value);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(!isReadOnlyConfiguredWidget() && enabled);
        updateComponents();
    }

    @Override
    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        disableCompListeners();
        if (newInstance != null) {
            setDateChooserValue();
        }
        updateComponents();
        enabledCompListeners();
    }

    //A method that triggers a change of a slot value and update the instance accordingly.
    //If the slot value is null (for example, when the instance is created),
    //then the slot-value is set to be today's date.
    @Override
    public void setValues(Collection values) {

        String dateString = (String) CollectionUtilities.getFirstItem(values);

        if (dateString == null) {
            setPropertyValue(new Date());
        } else {
            super.setValues(values);
        }

        updateComponents();
        ignoreUpdate = true;
        setDateChooserValue();
        ignoreUpdate = false;
    }

    protected void updateComponents() {
        boolean isEditable = !isReadOnlyConfiguredWidget();

        RDFResource resource = getEditedResource();
        RDFProperty property = getRDFProperty();
        if (resource != null && property != null && resource.isEditable()) {
            boolean value = resource.getPropertyValue(property) != null;
            setAction.setEnabled(isEditable && !value);
            deleteAction.setEnabled(isEditable && value);
            enableDateChooser(isEditable && value);
            lc.revalidate();
        } else {
            setAction.setEnabled(false);
            deleteAction.setEnabled(false);
            enableDateChooser(false);
        }


    }

    private void enableDateChooser(boolean enable) {
        for (Component comp : dateChooser.getComponents()) {
            comp.setEnabled(enable);
        }
    }

    protected void updateValues() {
        if (!ignoreUpdate) {
            Date date = getDate();
            setPropertyValue(date);
        }
    }

    @Override
    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        WidgetConfigurationPanel confPanel = super.createWidgetConfigurationPanel();

        confPanel.addTab("Options", new ReadOnlyWidgetConfigurationPanel(this));

        return confPanel;
    }

    protected void enabledCompListeners() {
        dateChooser.addPropertyChangeListener(propertyChangeListener);
    }

    protected void disableCompListeners() {
        dateChooser.removePropertyChangeListener(propertyChangeListener);
    }
}
