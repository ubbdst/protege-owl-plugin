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

package edu.stanford.smi.protegex.owl.ui.restrictions;

import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.Hashtable;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RestrictionKindRenderer extends FrameRenderer {

    private static Hashtable iconHash = new Hashtable();


    static {
        iconHash.put(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION, "OWLAllValuesFrom");
        iconHash.put(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION, "OWLSomeValuesFrom");
        iconHash.put(OWLNames.Cls.HAS_VALUE_RESTRICTION, "OWLHasValue");
        iconHash.put(OWLNames.Cls.CARDINALITY_RESTRICTION, "OWLCardinality");
        iconHash.put(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION, "OWLMaxCardinality");
        iconHash.put(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION, "OWLMinCardinality");
    }


    private static Hashtable nameHash = new Hashtable();


    static {
        nameHash.put(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION, "allValuesFrom");
        nameHash.put(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION, "someValuesFrom");
        nameHash.put(OWLNames.Cls.HAS_VALUE_RESTRICTION, "hasValue");
        nameHash.put(OWLNames.Cls.CARDINALITY_RESTRICTION, "cardinality");
        nameHash.put(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION, "maxCardinality");
        nameHash.put(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION, "minCardinality");
    }


    protected Icon createMainIcon(edu.stanford.smi.protege.model.Cls cls) {
        return getClsIcon(cls);
    }


    protected String createMainText(edu.stanford.smi.protege.model.Cls cls) {
        return getClsName(cls);
    }


    public static Icon getClsIcon(edu.stanford.smi.protege.model.Cls metaCls) {
        return OWLIcons.getImageIcon(getClsIconName(metaCls));
    }


    public static String getClsIconName(edu.stanford.smi.protege.model.Cls metaCls) {
        return iconHash.get(metaCls.getName()).toString();
    }


    public static String getClsName(edu.stanford.smi.protege.model.Cls metaCls) {
        return (String) nameHash.get(metaCls.getName());
    }


    protected void loadCls(edu.stanford.smi.protege.model.Cls cls) {
        String typeName = createMainText(cls);
        Icon typeIcon = createMainIcon(cls);
        setMainIcon(typeIcon);
        setMainText(typeName);
    }
}
