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

package edu.stanford.smi.protegex.owl.ui.components.annotations;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.project.SettingsMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 20, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultAnnotations {

    public final static String SETTINGS_MAP_KEY = DefaultAnnotations.class.getName();

    private OWLModel owlModel;


    public DefaultAnnotations(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    public void addDefaultAnnotationProperty(RDFProperty property) {
        getSettingsMap().setBoolean(property.getName(), true);
    }


    public Collection getDefaultAnnotationProperties() {
        SettingsMap map = getSettingsMap();
        Collection results = new ArrayList();
        Iterator names = map.listKeys();
        while(names.hasNext()) {
            String name = (String) names.next();
            RDFResource resource = owlModel.getRDFResource(name);
            if(resource instanceof RDFProperty) {
                results.add(resource);
            }
        }
        return results;
    }


    private SettingsMap getSettingsMap() {
        return owlModel.getOWLProject().getSettingsMap().getSettingsMap(SETTINGS_MAP_KEY);
    }


    public void removeDefaultAnnotationProperty(RDFProperty property) {
        getSettingsMap().remove(property.getName());
    }


    public void setDefaultAnnotationProperties(Collection properties) {
        owlModel.getOWLProject().getSettingsMap().remove(SETTINGS_MAP_KEY);
        SettingsMap newMap = getSettingsMap();
        for (Iterator it = properties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            newMap.setBoolean(property.getName(), true);
        }
    }
}

