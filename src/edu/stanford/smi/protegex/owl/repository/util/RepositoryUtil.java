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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.Log;

import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.impl.AbstractStreamBasedRepositoryImpl;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactory;
import edu.stanford.smi.protegex.owl.repository.impl.DublinCoreDLVersionRedirectRepository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 22, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryUtil {
	private final static transient Logger log = Log.getLogger(RepositoryUtil.class);

	public static final String FORCE_READ_ONLY_FLAG = "forceReadOnly";

	public static boolean isForcedToBeReadOnly(String query) {
		return getBooleanProperty(FORCE_READ_ONLY_FLAG, query);
	}

	public static boolean getBooleanProperty(String propertyName, String query) {
		if (query != null && query.length() > 0) {
			StringTokenizer tok = new StringTokenizer(query, "&");
			while (tok.hasMoreTokens()) {
				String curNameValuePair = tok.nextToken();
				String curName = curNameValuePair.substring(0, curNameValuePair.indexOf('='));
				String curValue = curNameValuePair.substring(curNameValuePair.indexOf('=') + 1,
						curNameValuePair.length());
				if (curName.equals(propertyName)) {
					return Boolean.valueOf(curValue).booleanValue();
				}
			}
		}
		return false;
	}


	public static boolean createImportLocalCopy(OWLModel model, URI ontologyURI, File localFile) throws OntologyLoadException {
		Repository rep = model.getRepositoryManager().getRepository(ontologyURI);
		if (rep != null && rep instanceof AbstractStreamBasedRepositoryImpl) {
			InputStream is = ((AbstractStreamBasedRepositoryImpl) rep).getInputStream(ontologyURI);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localFile)));

				String line;
				while ((line = br.readLine()) != null) {
					bw.write(line);
				}
				bw.flush();
				bw.close();
				br.close();
			}
			catch (FileNotFoundException e) {
				throw new OntologyLoadException(e, "Could not find file " + localFile);
			} catch (IOException e) {
				throw new OntologyLoadException(e, "Could not load file " + localFile);
			}
			model.getRepositoryManager().addProjectRepository(0, new LocalFileRepository(localFile));
			return true;
		}
		else if (rep != null) {
			// for the non-stream based ontology we could also save the owl file but we will need to write this later.
			log.warning("Can't write non-stream based repositories yet (though it wouldn't be hard to fix this)");
		}
		return false;
	}


	public static boolean isDublinCoreRedirectedToDLVersion(RepositoryManager manager) {
		for (Iterator it = manager.getProjectRepositories().iterator(); it.hasNext();) {
			if (it.next() instanceof DublinCoreDLVersionRedirectRepository) {
				return true;
			}
		}
		return false;
	}


	public static void setDublinCoreRedirectedToDLVersion(RepositoryManager manager, boolean redirect) {
		if (redirect) {
			if (isDublinCoreRedirectedToDLVersion(manager) == false) {
				manager.addProjectRepository(0, new DublinCoreDLVersionRedirectRepository());
			}
		}
		else {
			for (Iterator it = manager.getProjectRepositories().iterator(); it.hasNext();) {
				Repository rep = (Repository) it.next();
				if (rep instanceof DublinCoreDLVersionRedirectRepository) {
					manager.remove(rep);
					break;
				}
			}
		}
	}

	public static URI getURI(URL baseURL, String relativeURL) throws MalformedURLException, URISyntaxException {
		return new URI(new URL(baseURL, relativeURL).toString());
	}

	public static File getRepositoryFileFromRelativePath(OWLModel model, String s) {
		File repositoryFile = null;
		try {
			URI owlFileUri = model.getProject().getProjectURI();

			if (owlFileUri == null && model.getKnowledgeBaseFactory() instanceof JenaKnowledgeBaseFactory) {
				String uriString = JenaKnowledgeBaseFactory.getOWLFilePath(model.getProject().getSources());
				owlFileUri = URIUtilities.createURI(uriString);
			} 

			if (owlFileUri == null) {
				return null;
			}
			URL absoluteURL = new URL(owlFileUri.toURL(), RepositoryUtil.stripQuery(s.trim()));

			repositoryFile = new File(absoluteURL.toURI());
			if (repositoryFile.canRead() && repositoryFile.isFile()) {
				return repositoryFile;
			}
		}
		catch (Throwable t) {
			return null;
		}
		return null;
	}


	public static String stripQuery(String relativeURL) {
		int index = relativeURL.indexOf('?');
		if (index != -1) {
			return relativeURL.substring(0, index);
		}
		else {
			return relativeURL;
		}
	}


	/**
	 * Loads the project repositories from a given URI. The file at the URI location should be in the 
	 * repository file format expected by Protege.
	 *  
	 * @param owlModel - the owl model
	 * @param uri - the uri of the repository file
	 * @param removeExistingRepositories - if true, it will remove all existing repository entries. If false,
	 * it will append to the old repositories entries the new ones loaded from uri.
	 */
	public static void loadProjectRepositoriesFromURI(OWLModel owlModel, URI uri, boolean removeExistingRepositories) {

		RepositoryManager manager = owlModel.getRepositoryManager();

		if (removeExistingRepositories == true) {
			manager.removeAllProjectRepositories();
		}

		try {
			URL url = new URL(uri.toString());
			InputStream fis = ProtegeOWLParser.getInputStream(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					RepositoryFactory factory = RepositoryFactory.getInstance();
					Repository rep = factory.createOntRepository(owlModel, line);
					if (rep != null) {						
						manager.addProjectRepository(rep);
					}
				}
			}			
			fis.close();
		} catch (IOException e) {
			Log.getLogger().warning("[Repository Manager] Could not find repository file: "
					+ uri.toString());
		} catch (OntologyLoadException e) {
			Log.getLogger().warning("[Repository Manager] Could not find repository file: "
					+ uri.toString());
		}
	}

}

