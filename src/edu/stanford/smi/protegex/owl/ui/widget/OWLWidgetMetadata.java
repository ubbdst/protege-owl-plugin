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

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * An interface for objects that provide metadata about a widget type.
 * This is used by the OWLWidgetMapper to determine whether a certain widget
 * class shall be chosen for a certain class/property combination.
 * <p/>
 * The widget class should register an implementation of this in a static block.
 * <p/>
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLWidgetMetadata {

    /**
     * Indicates that this widget is not suitable for the given class/property combination
     */
    final static int NOT_SUITABLE = 0;

    /**
     * Indicates that this widget is suitable (but not necessarily default) for the given
     * class/property combination
     */
    final static int SUITABLE = 1;

    /**
     * Indicates that this widget is the default widget unless some other plugin declares
     * a widget with higher suitability
     */
    final static int DEFAULT = 2;


    /**
     * <P>Gets the suitability index of a widget for a given class/property pair.
     * The suitability indicates whether a widget shall be selected as default
     * widget on a form, or whether it shall be listed as one of the options in
     * the form configuration component.</P>
     * <P>The system widgets of Protege-OWL all return one of the predefined constants
     * <CODE>NOT_SUITABLE</CODE>, <CODE>SUITABLE</CODE> or <CODE>DEFAULT</CODE>.
     * Custom widget classes can also return the same constants, or they can return
     * higher values to override the current default.  For example, returning
     * <CODE>DEFAULT + 1</CODE> will make sure that the widget will have higher
     * preferences than any of the system widgets.</P>
     *
     * @param cls      the named class of the form
     * @param property the property to get the suitability for
     * @return an index >= 0
     */
    int getSuitability(RDFSNamedClass cls, RDFProperty property);
}
