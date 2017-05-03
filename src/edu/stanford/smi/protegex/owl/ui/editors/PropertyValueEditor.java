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

package edu.stanford.smi.protegex.owl.ui.editors;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.awt.*;

/**
 * An object that can be used to customize the appearance and behavior of (datatype) values
 * in visual editors.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface PropertyValueEditor {


    /**
     * Checks whether this is able to edit values for a certain predicate.
     *
     * @param subject
     * @param predicate
     * @param value
     * @return true  if this can edit such values
     */
    public boolean canEdit(RDFResource subject, RDFProperty predicate, Object value);


    /**
     * Creates a default value for a given predicate at a given subject.
     * The hosting widget should call this method on each of its registered editors
     * to see whether any of them defines a non-null default value.
     *
     * @param subject   the RDFResource to create a default value for
     * @param predicate the annotation predicate
     * @return the default value or null if this does not define a default for this predicate
     */
    public Object createDefaultValue(RDFResource subject, RDFProperty predicate);


    /**
     * Edits a given value for a given resource/predicate pair.  For example, after the user
     * has double-clicked on the value, the host widget could iterate on all editors to see
     * whether any of them is ready to edit this value (returning true).
     *
     * @param parent
     * @param subject
     * @param predicate
     * @param value
     * @return the new value (!= null) if this has handled editing for the value
     */
    public Object editValue(Component parent, RDFResource subject, RDFProperty predicate, Object value);


    /**
     * Checks whether this is the only way to edit values for a certain predicate.
     * This is used to force editing of value types like xsd:date, where simple string
     * input fields would not be sufficient.
     *
     * @param subject
     * @param predicate
     * @param value
     * @return true  if this can edit such values
     */
    public boolean mustEdit(RDFResource subject, RDFProperty predicate, Object value);
}
