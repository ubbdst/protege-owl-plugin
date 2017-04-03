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

package edu.stanford.smi.protegex.owl.repository.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactory;
import edu.stanford.smi.protegex.owl.repository.impl.ProtegeOWLPluginFolderRepository;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 24, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryFileManager {

    public static final String REPOSITORY_EXTENTION = ".repository";

    public static final String GLOBAL_REPOSITORY_FILE_NAME = "global.repository";

    private RepositoryManager manager;

    private OWLModel model;

    public static void loadProjectRepositories(OWLModel owlModel) {
        // Load any project repositories
        RepositoryFileManager man = new RepositoryFileManager(owlModel);
        man.loadProjectRepositories();
    }

    public static void saveProjectRepositories(OWLModel owlModel) {
        RepositoryFileManager man = new RepositoryFileManager(owlModel);
        man.saveGlobalRepositories();
        man.saveProjectRepositories();
    }

    public RepositoryFileManager(OWLModel model) {
        this.model = model;
        this.manager = this.model.getRepositoryManager();
    }


    public void loadProjectRepositories() {
    	URI uri = getProjectRepositoryURI();
    	if (uri != null) {
            manager.removeAllProjectRepositories();
            loadRepositoriesFromURI(uri);
    	}
    }


    private void loadGlobalRepositories(File file) {
        if (file.exists()) {
            loadRepositoriesFromFile(file, true);
        }
    }


    public void loadGlobalRepositories() {
    	try {
            File f = ProtegeOWL.getPluginFolder();
            f = new File(f, GLOBAL_REPOSITORY_FILE_NAME);
            if (f.exists()) {
                loadGlobalRepositories(f);
            }
            manager.addGlobalRepository(new ProtegeOWLPluginFolderRepository());
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Failed to load global repositories", e);
		}
    }


    public void saveProjectRepositories(URI owlFileURI) {
        File file = getProjectRepositoryFile(owlFileURI);
        if (file != null) {
            saveProjectRepositories(file);
        }
    }


    public void saveProjectRepositories() {
        Project project = model.getProject();
        if (project != null) {
            if (project.getProjectURI() != null) {
                File file = getProjectRepositoryFile();
                if (file != null) {
                    saveProjectRepositories(file);
                }
            }
        }
    }


    private void saveProjectRepositories(File file) {
        ArrayList list = new ArrayList();
        for (Object element : manager.getProjectRepositories()) {
            Repository rep = (Repository) element;
            if (rep.isSystem() == false) {
                list.add(rep);
            }
        }
        if (list.size() > 0) {
            saveRepositories(list, file);
        }
        else {
            file.delete();
        }
    }


    public void saveGlobalRepositories() {
        File pluginsDirectory = PluginUtilities.getPluginsDirectory();
        if (pluginsDirectory != null && pluginsDirectory.exists()) {
            File f = ProtegeOWL.getPluginFolder();
            f = new File(f, GLOBAL_REPOSITORY_FILE_NAME);
            saveRepositories(manager.getGlobalRepositories(), f);
        }
    }


    private void saveRepositories(List repositories, File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (Iterator it = repositories.iterator(); it.hasNext();) {
                Repository curRep = (Repository) it.next();
                if (curRep.isSystem() == false) {
                    String descriptor = curRep.getRepositoryDescriptor();
                    if (descriptor != null && descriptor.length() > 0) {
                        writer.write(descriptor);
                        writer.write("\n");
                    }
                }
            }
            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException ioEx) {
            System.err.println(ioEx.getMessage());
        }
    }



    private void loadRepositoriesFromFile(final File f,
                                          final boolean global) {
        try {
                    FileInputStream fis = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					String line;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						if (line.length() > 0) {
							RepositoryFactory factory = RepositoryFactory.getInstance();
							Repository rep = factory.createOntRepository(model, line);
							if (rep != null) {
								if (global) {
									manager.addGlobalRepository(rep);
								}
								else {
									manager.addProjectRepository(rep);
								}
							}
						}
					}

        }
        catch (IOException e) {
            Log.getLogger().warning("[Repository Manager] Could not find repository file: " + f.toString());
        }
    }


    private void loadRepositoriesFromURI(final URI uri) {
		try {
			URL url = new URL(uri.toString());
			InputStream fis = ProtegeOWLParser.getInputStream(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					RepositoryFactory factory = RepositoryFactory.getInstance();
					Repository rep = factory.createOntRepository(model, line);
					if (rep != null) {
						manager.addProjectRepository(rep);
					}
				}
			}
			fis.close();
		} catch (OntologyLoadException e) {
			if (Log.getLogger().isLoggable(Level.FINE)) {
				Log.getLogger().fine("[Repository Manager] Could not find repository file: "
							+ uri.toString());
			}
		} catch (IOException e) {
			if (Log.getLogger().isLoggable(Level.FINE)) {
				Log.getLogger().fine("[Repository Manager] Could not find repository file: "
							+ uri.toString());
			}
		}
	}


    private File getProjectRepositoryFile() {
        Project project = model.getProject();
        if (project != null) {
            URI projectURI = project.getProjectURI();

            //If the project does not have a name, try to retrieve the repository file from the owl file path
            try {
                if (projectURI == null && project.getKnowledgeBaseFactory() instanceof JenaKnowledgeBaseFactory) {
                	String owlFileUriString = JenaKnowledgeBaseFactory.getOWLFilePath(project.getSources());
                	projectURI = URIUtilities.createURI(owlFileUriString);
                }
			} catch (Exception e) {
				Log.getLogger().warning("Failed to find repository file for " + project);
			}

            if (projectURI != null) {
                return getProjectRepositoryFile(projectURI);
            }
        }
        return null;
    }


    private URI getProjectRepositoryURI() {
        Project project = model.getProject();
        if (project != null) {
            URI projectURI = project.getProjectURI();

            //If the project does not have a name, try to retrieve the repository file from the owl file path
            try {
                if (projectURI == null && project.getKnowledgeBaseFactory() instanceof JenaKnowledgeBaseFactory) {
                	String owlFileUriString = JenaKnowledgeBaseFactory.getOWLFilePath(project.getSources());
                	projectURI = URIUtilities.createURI(owlFileUriString);
                }
			} catch (Exception e) {
				Log.getLogger().warning("Failed to find repository file for " + project);
			}

            if (projectURI != null) {
                return getProjectRepositoryURI(projectURI);
            }
        }
        return null;
    }


    private URI getProjectRepositoryURI(URI owlFileURI) {
    	return URIUtilities.replaceExtension(owlFileURI, REPOSITORY_EXTENTION);
    }

    private File getProjectRepositoryFile(URI owlFileURI) {
    	File f = new File(owlFileURI);
        String repName = f.getName();
        repName = FileUtilities.replaceExtension(repName, REPOSITORY_EXTENTION);
        repName = FileUtilities.ensureExtension(repName, REPOSITORY_EXTENTION);
        f = new File(f.getParentFile(), repName);
        return f;
    }
}
