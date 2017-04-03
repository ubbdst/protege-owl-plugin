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

package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TimePanel extends JPanel {

    private Listener listener;

    private JTextField hoursField;

    private JTextField minutesField;

    private JTextField secondsField;


    public TimePanel(Listener listener) {
        this.listener = listener;
        hoursField = new JTextField(2);
        minutesField = new JTextField(2);
        secondsField = new JTextField(2);
        FocusListener focusListener = new FocusListener() {
            public void focusGained(FocusEvent e) {
            }


            public void focusLost(FocusEvent e) {
                notifyListener();
            }
        };
        hoursField.addFocusListener(focusListener);
        minutesField.addFocusListener(focusListener);
        secondsField.addFocusListener(focusListener);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.add(hoursField);
        leftPanel.add(new JLabel(":"));
        leftPanel.add(minutesField);
        leftPanel.add(new JLabel(":"));
        leftPanel.add(secondsField);
        setLayout(new BorderLayout());
        add(BorderLayout.WEST, leftPanel);
        add(BorderLayout.CENTER, Box.createHorizontalGlue());
    }


    public void clear() {
        hoursField.setText("");
        minutesField.setText("");
        secondsField.setText("");
    }


    private int getInt(JTextField textField, int max) {
        String str = textField.getText();
        return getInt(str, max);
    }


    private int getInt(String str, int max) {
        try {
            int value = Integer.parseInt(str);
            if (value >= 0 && value < max) {
                return value;
            }
        }
        catch (Exception ex) {
        }
        return 0;
    }


    private String getString(int x) {
        if (x < 10) {
            return "0" + x;
        }
        else {
            return "" + x;
        }
    }


    public String getTime() {
        int hours = getHours();
        int minutes = getMinutes();
        int seconds = getSeconds();
        return XMLSchemaDatatypes.getTimeString(hours, minutes, seconds);
    }

    public int getHours() {
    	return getInt(hoursField, 24);
    }

    public int getMinutes(){
    	return getInt(minutesField, 60); 
    }
    
    public int getSeconds() {
    	return getInt(secondsField, 60);
    }

    public boolean isNull() {
        return hoursField.getText().length() +
                minutesField.getText().length() +
                secondsField.getText().length() == 0;
    }


    private void notifyListener() {
        if (listener != null) {
            listener.timeChanged(this);
        }
    }


    public void setTime(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    }


    public void setTime(String literal) {
        String[] ss = literal.split(":");
        int hours = ss.length > 0 ? getInt(ss[0], 24) : 0;
        int minutes = ss.length > 1 ? getInt(ss[1], 60) : 0;
        int seconds = ss.length > 2 ? getInt(ss[2], 60) : 0;
        setTime(hours, minutes, seconds);
    }


    public void setTime(int hours, int minutes, int seconds) {
        hoursField.setText("" + getString(hours));
        minutesField.setText("" + getString(minutes));
        secondsField.setText("" + getString(seconds));
    }


    public static interface Listener {

        void timeChanged(TimePanel timePanel);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
    	hoursField.setEnabled(enabled);
    	minutesField.setEnabled(enabled);
    	secondsField.setEnabled(enabled);
    	
    	super.setEnabled(enabled);
    }
}
