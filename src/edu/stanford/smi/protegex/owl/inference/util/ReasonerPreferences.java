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

package edu.stanford.smi.protegex.owl.inference.util;

import edu.stanford.smi.protege.util.ApplicationProperties;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 30, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ReasonerPreferences {

    public static final String DEFAULT_REASONER_URL = "http://localhost:8080";

    public static final String REASONER_URL_KEY = "edu.stanford.smi.protegex.owl.jena.reasoner.URL";

    private static ReasonerPreferences instance;

    private boolean warningsAsErrors = false;

    private boolean showWarningResultsPanel = true;

    private boolean showReasonerResultsPanel = true;

    private boolean automaticallyShowInferredHierarchy = true;


    protected ReasonerPreferences() {

    }


    public static synchronized ReasonerPreferences getInstance() {
        if (instance == null) {
            instance = new ReasonerPreferences();
        }

        return instance;
    }


    /**
     * Gets the URL for the external DIG Reasoner.
     *
     * @return A String that represents the URL.
     */
    public String getReasonerURL() {
        String url;

        url = ApplicationProperties.getString(REASONER_URL_KEY, getDefaultReasonerURL());

        if (url == null || url.equals("")) {
            url = DEFAULT_REASONER_URL;
            ApplicationProperties.setString(REASONER_URL_KEY, DEFAULT_REASONER_URL);
        }

        return url;
    }


    /**
     * Gets the Default URL for the extenal DIG Reasoner
     *
     * @return A String representing the URL.
     */
    public String getDefaultReasonerURL() {
        return DEFAULT_REASONER_URL;
    }


    /**
     * Sets the URL for the external DIG Reasoner
     *
     * @param url A <code>String</code> that represents the
     *            URL.
     */
    public void setReasonerURL(String url) {
        ApplicationProperties.setString(REASONER_URL_KEY, url);
    }


    public boolean isWarningAsErrors() {
        return warningsAsErrors;
    }


    public void setWarningsAsErrors(boolean b) {
        warningsAsErrors = b;
    }


    public boolean isShowWarningResultsPanel() {
        return showWarningResultsPanel;
    }


    public void setShowWarningResultsPanel(boolean showWarningResultsPanel) {
        this.showWarningResultsPanel = showWarningResultsPanel;
    }


    public boolean isShowReasonerResultsPanel() {
        return showReasonerResultsPanel;
    }


    public void setShowReasonerResultsPanel(boolean showReasonerResultsPanel) {
        this.showReasonerResultsPanel = showReasonerResultsPanel;
    }


    public boolean isAutomaticallyShowInferredHierarchy() {
        return automaticallyShowInferredHierarchy;
    }


    public void setAutomaticallyShowInferredHierarchy(boolean automaticallyShowInferredHierarchy) {
        this.automaticallyShowInferredHierarchy = automaticallyShowInferredHierarchy;
    }
}

