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

package edu.stanford.smi.protegex.owl.javacode;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;

import java.io.File;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProjectBasedJavaCodeGeneratorOptions implements EditableJavaCodeGeneratorOptions {

    public final static String ABSTRACT_MODE = "JavaCodeAbstract";

    public final static String FACTORY_CLASS_NAME = "JavaCodeFactoryClassName";

    public final static String FILE_NAME = "JavaCodeFileName";

    public final static String PACKAGE = "JavaCodePackage";

    public final static String SET_MODE = "JavaCodeSet";

    public final static String PREFIX_MODE = "JavaCodeUsePrefix";

    private OWLProject project;


    public ProjectBasedJavaCodeGeneratorOptions(OWLModel owlModel) {
        this.project = owlModel.getOWLProject();
    }


    public boolean getAbstractMode() {
        Boolean b = project.getSettingsMap().getBoolean(ABSTRACT_MODE);
        if (b != null) {
            return b.booleanValue();
        }
        else {
            return false;
        }
    }


    public String getFactoryClassName() {
        String value = project.getSettingsMap().getString(FACTORY_CLASS_NAME);
        if (value == null) {
            return "MyFactory";
        }
        else {
            return value;
        }
    }


    public File getOutputFolder() {
        String fileName = project.getSettingsMap().getString(FILE_NAME);
        if (fileName == null) {
            return new File("");
        }
        else {
            return new File(fileName);
        }
    }


    public String getPackage() {
        return project.getSettingsMap().getString(PACKAGE);
    }


    public boolean getSetMode() {
        Boolean b = project.getSettingsMap().getBoolean(SET_MODE);
        if (b != null) {
            return b.booleanValue();
        }
        else {
            return false;
        }
    }


    public boolean getPrefixMode() {
    	Boolean b = project.getSettingsMap().getBoolean(PREFIX_MODE);
    	if (b != null) {
    		return b.booleanValue();
    	}
    	else {
    		return false;
    	}
    }


    public void setAbstractMode(boolean value) {
        project.getSettingsMap().setBoolean(ABSTRACT_MODE, value);
    }


    public void setOutputFolder(File file) {
        if (file == null) {
            project.getSettingsMap().remove(FILE_NAME);
        }
        else {
            project.getSettingsMap().setString(FILE_NAME, file.getAbsolutePath());
        }
    }


    public void setFactoryClassName(String value) {
        if (value == null || value.length() == 0) {
            project.getSettingsMap().remove(FACTORY_CLASS_NAME);
        }
        else {
            project.getSettingsMap().setString(FACTORY_CLASS_NAME, value);
        }
    }


    public void setPackage(String value) {
        if (value == null || value.length() == 0) {
            project.getSettingsMap().remove(PACKAGE);
        }
        else {
            project.getSettingsMap().setString(PACKAGE, value);
        }
    }


    public void setSetMode(boolean value) {
        project.getSettingsMap().setBoolean(SET_MODE, value);
    }
    
    
    public void setPrefixMode(boolean value) {
    	project.getSettingsMap().setBoolean(PREFIX_MODE, value);
}
}
