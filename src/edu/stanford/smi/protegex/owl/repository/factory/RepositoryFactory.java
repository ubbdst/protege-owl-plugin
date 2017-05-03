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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.DatabaseRepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.impl.DublinCoreDLVersionRedirectRepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.impl.FTPRepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.impl.HTTPRepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.impl.RelativeFileRepositoryFactoryPlugin;
import edu.stanford.smi.protegex.owl.repository.impl.RelativeFolderRepositoryFactoryPlugin;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 19, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryFactory {

    private static RepositoryFactory instance;

    private ArrayList<RepositoryFactoryPlugin> factories;


    @SuppressWarnings("unchecked")
    private RepositoryFactory() {
        factories = new ArrayList<RepositoryFactoryPlugin>();
        factories.add(new DublinCoreDLVersionRedirectRepositoryFactoryPlugin());
        factories.add(new LocalFileRepositoryFactoryPlugin());
        factories.add(new RelativeFileRepositoryFactoryPlugin());
        factories.add(new LocalFolderRepositoryFactoryPlugin());
        factories.add(new HTTPRepositoryFactoryPlugin());
        factories.add(new RelativeFolderRepositoryFactoryPlugin());
        factories.add(new FTPRepositoryFactoryPlugin());
        factories.add(new DatabaseRepositoryFactoryPlugin());
        Collection<Class> plugins = PluginUtilities.getClassesWithAttribute(RepositoryFactoryPlugin.PLUGIN_TYPE, "True");
        for (Iterator<Class> it = plugins.iterator(); it.hasNext();) {
            Class cls =  it.next();
            try {
                factories.add((RepositoryFactoryPlugin) cls.newInstance());
            }
            catch (InstantiationException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
            catch (IllegalAccessException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
        }
    }


    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }


    public void registerRepositoryFactoryPlugin(RepositoryFactoryPlugin factory) {
        factories.add(factory);
    }


    public Repository createOntRepository(OWLModel model, String s) {
        for (Iterator<RepositoryFactoryPlugin> it = factories.iterator(); it.hasNext();) {
            RepositoryFactoryPlugin curPlugin = it.next();
            if (curPlugin.isSuitable(model, s)) {
                return curPlugin.createRepository(model, s);
            }
        }
        return null;
    }


    public Collection<RepositoryFactoryPlugin> getFactories() {
        return new ArrayList<RepositoryFactoryPlugin>(factories);
    }
}

