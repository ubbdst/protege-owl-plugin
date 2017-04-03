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

package edu.stanford.smi.protegex.owl.jena;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.CreateProjectFromFilePlugin;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromUriCreator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLProjectFromFilePlugin implements CreateProjectFromFilePlugin {


    @SuppressWarnings("unchecked")
    public Project createProject(File file, Collection errors) {
        OwlProjectFromUriCreator creator = new OwlProjectFromUriCreator();
        try {   
            JenaKnowledgeBaseFactory.useStandalone = false;
            creator.setOntologyUri(URIUtilities.createURI(file.getPath()).toString());            
            creator.create(errors);
        }
        catch (Exception ex) {
            errors.add(new MessageError(ex, "Ontology content might be incomplete or corrupted.\nSee console or log for the full stack trace."));
            Log.getLogger().log(Level.SEVERE, "Error loading file with the CreateOWLProjectFromFilePlugin", ex);
        }
        return creator.getProject();
    }


    public String[] getSuffixes() {
        return new String[]{
                "owl"
        };
    }


    public String getDescription() {
        return "OWL Files";
    }
    
    protected URI getBuildProjectURI(File file) {
    	String pprjFilePath = FileUtilities.replaceExtension(file.getAbsolutePath(), ".pprj");

    	try {
          	File pprjFile = new File(pprjFilePath);
                    	
            if (pprjFile.exists()) {
            	// Uncomment this part of the code if you want the user to be asked whether the pprj file should be loaded or not
            	/*int selection = ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getMainPanel(), "A project file corresponding" +
                   				" to the OWL file has been found at:" +
                   				"\n" + pprjFilePath + "\nDo you want to load it?", ModalDialog.MODE_YES_NO);
                if (selection == ModalDialog.OPTION_YES)*/
                	return URIUtilities.createURI(pprjFilePath);
                /*else 
                	return null;*/
            }
       }
         catch (Exception ex) {
            Log.emptyCatchBlock(ex);
       }
         return null;
    }
    
}
