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

package edu.stanford.smi.protegex.owl.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import edu.stanford.smi.protege.resource.Text;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Mar-2006
 */
public final class OWLText {
    private static String buildFile = "build.properties";
    private static String directory = "files";
    private static Properties props;

    static {
        try {
            props = new Properties();
            InputStream stream = FileUtilities.getResourceStream(OWLText.class, directory, buildFile);
            props.load(stream);
        } catch (IOException e) {
            Log.getLogger().severe(Log.toString(e));
        }
    }

    public static String getName() {
        return props.getProperty("name", "Prot\u00E9g\u00E9-OWL");
    }

    public static String getBuildInfo() {
        return Text.getBuildInfo();
    }

    public static String getBuildNumber() {
        return Text.getBuildNumber();
    }

    public static String getStatus() {
        return props.getProperty("build.status");
    }

    public static String getVersion() {
        return Text.getVersion();
    }

    /**
     * This method is deprecated. The latest compatible build version is not maintained.
     * @return 0
     */
    @Deprecated
    public static int getLatestCompatibleBuild(){
        return Integer.parseInt(props.getProperty("build.compatible.latest", "0"));
    }

    public static URL getAboutURL() {
    	URL aboutURL = OWLText.class.getResource("files/about-owl.html");
        return aboutURL;
    }
}
