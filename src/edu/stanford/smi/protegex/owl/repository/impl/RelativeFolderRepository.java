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

package edu.stanford.smi.protegex.owl.repository.impl;

import edu.stanford.smi.protegex.owl.repository.util.RepositoryUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RelativeFolderRepository extends LocalFolderRepository {

    private String relativeURL;


    public RelativeFolderRepository(URL baseURL, String relativeURL) throws MalformedURLException,
            URISyntaxException {
        this(baseURL, relativeURL, RepositoryUtil.isForcedToBeReadOnly(getURI(baseURL, relativeURL).getQuery()));
    }


    public RelativeFolderRepository(URL baseURL, String relativeURL, boolean forceReadOnly) throws MalformedURLException,
            URISyntaxException {

        super(new File(getURI(baseURL, relativeURL).getPath()),
                forceReadOnly);
        this.relativeURL = RepositoryUtil.stripQuery(relativeURL);
    }


    private static URI getURI(URL baseURL, String relativeURL) throws MalformedURLException,
            URISyntaxException {
        return new URI(new URL(baseURL, relativeURL).toString());
    }





    public String getRepositoryDescriptor() {
        try {
            URI uri = new URI(relativeURL);
            return uri.toString() + "?" + RepositoryUtil.FORCE_READ_ONLY_FLAG + "=" + isForceReadOnly();
        }
        catch (URISyntaxException e) {
            return "";
        }
    }


    public String getRepositoryDescription() {
        return "Relative URL: " + relativeURL + "  (" + getFile().toString() + ")";
    }
}

