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

package edu.stanford.smi.protegex.owl.repository;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.impl.DublinCoreDLVersionRedirectRepository;
import edu.stanford.smi.protegex.owl.repository.impl.ForcedURLRetrievalRepository;
import edu.stanford.smi.protegex.owl.repository.impl.HTTPRepository;
import edu.stanford.smi.protegex.owl.repository.impl.ProtegeOWLPluginFolderRepository;


/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 18, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryManager {

    private ArrayList<Repository> projectRepositories;

    private ArrayList<Repository> globalRepositories;

    private OWLModel model;


    public RepositoryManager(OWLModel model) {
        this.model = model;
        projectRepositories = new ArrayList<Repository>();
        globalRepositories = new ArrayList<Repository>();
    }


    public void addDefaultRepositories() {
        projectRepositories.add(new DublinCoreDLVersionRedirectRepository());
    }

    public void removeAllProjectRepositories() {
        projectRepositories.clear();
    }


    public List<Repository> getAllRepositories() {
        ArrayList<Repository> list = new ArrayList<Repository>();
        list.addAll(projectRepositories);
        list.addAll(globalRepositories);
        return list;
    }


    public void removeAllGlobalRepositories() {
        globalRepositories.clear();
    }


    public List<Repository> getProjectRepositories() {
        return Collections.unmodifiableList(projectRepositories);
    }


    public List<Repository> getGlobalRepositories() {
        return Collections.unmodifiableList(globalRepositories);
    }


    public void addProjectRepository(Repository repository) {
        addProjectRepository(projectRepositories.size(), repository);
    }


    public void addProjectRepository(int index, Repository repository) {
        if (projectRepositories.contains(repository) == false) {
            projectRepositories.add(index, repository);
        }
    }


    public void addGlobalRepository(Repository repository) {
        addGlobalRepository(globalRepositories.size(), repository);
    }


    public void addGlobalRepository(int index, Repository repository) {
        if (globalRepositories.contains(repository) == false) {
            globalRepositories.add(index, repository);
        }
    }


    public void moveUp(Repository repository) {
        List<Repository> repositories = selectRepositories(repository);
        int index = repositories.indexOf(repository);
        if (index != -1 && index > 0) {
            repositories.remove(index);
            repositories.add(index - 1, repository);
        }
    }


    public void moveDown(Repository repository) {
        List<Repository> repositories = selectRepositories(repository);
        int index = repositories.indexOf(repository);
        if (index != -1 && index < repositories.size() - 1) {
            repositories.remove(index);
            repositories.add(index + 1, repository);
        }
    }


    private List<Repository> selectRepositories(Repository repository) {
        List<Repository> repositories;
        if (isGlobalRepository(repository)) {
            repositories = globalRepositories;
        }
        else {
            repositories = projectRepositories;
        }
        return repositories;
    }


    public boolean isGlobalRepository(Repository repository) {
        return globalRepositories.contains(repository);
    }


    public void remove(Repository repository) {
        selectRepositories(repository).remove(repository);
    }


    public Repository getRepository(URI ontologyName) {
        // Process local projectRepositories first.
        for (Repository curRepository : projectRepositories) {
            if (curRepository.contains(ontologyName)) {
                return curRepository;
            }
        }
        for (Repository curRepository : globalRepositories) {
            if (curRepository.contains(ontologyName)) {
                return curRepository;
            }
        }
        return null;
    }


    public Repository getRepository(URI ontologyName, boolean createRep) {
        Repository rep = getRepository(ontologyName);
        if (rep == null) {
            if (createRep) {
                try {
                    rep = new HTTPRepository(ontologyName.toURL());
                    if (rep.contains(ontologyName)) {
                        addProjectRepository(rep);
                    }
                    else {
                        ForcedURLRetrievalRepository fr = new ForcedURLRetrievalRepository(ontologyName.toURL());
                        if (fr.contains(ontologyName)) {
                            addProjectRepository(fr);
                            return fr;
                        }
                        else {
                            return null;
                        }
                    }
                }
                catch (MalformedURLException e) {
                    return null;
                }
            }
        }
        return rep;
    }


}

