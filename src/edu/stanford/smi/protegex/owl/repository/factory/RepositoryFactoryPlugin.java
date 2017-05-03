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

package edu.stanford.smi.protegex.owl.repository.factory;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 19, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface RepositoryFactoryPlugin {

    public static final String PLUGIN_TYPE = "RepositoryFactoryPlugin";


    /**
     * Determines if this plugin can create the appropriate
     * repository from the specified <code>String</code>.
     *
     * @param repositoryDescriptor The repository descriptor (This is typically a URI)
     * @return <code>true</code> if the plugin can create an
     *         appropriate repository given the specified repository descriptor,
     *         or <code>false</code> if the plugin cannot create the appropriate
     *         repository.
     */
    public boolean isSuitable(OWLModel model, String repositoryDescriptor);


    /**
     * Creates a repository from the specified repository descriptor.
     *
     * @param model
     * @param repositoryDescriptor The repository descriptor.
     * @return A <code>Repository</code> that is based on the specified
     *         repository descriptor.
     */
    public Repository createRepository(OWLModel model, String repositoryDescriptor);
}
