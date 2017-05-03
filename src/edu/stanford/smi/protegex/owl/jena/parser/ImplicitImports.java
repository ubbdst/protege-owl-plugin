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

package edu.stanford.smi.protegex.owl.jena.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;

/**
 * A Singleton that manages the list of namespaces (such as DC) that shall be
 * imported internally if resources from it are referenced as untyped in a file.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImplicitImports {

    public final static String LOCAL_FILE_NAME = "implicit-imports.repository";

    public final static File FILE =
            new File(ProtegeOWL.getPluginFolder(),
                    LOCAL_FILE_NAME);

    private static Set namespaces;


    /**
     * Checks whether a given namespace shall be imported even if no explicit
     * owl:imports statement is found.
     *
     * @param namespace the namespace in question
     * @return true  if the namespace shall be imported
     */
    public static boolean isImplicitImport(String namespace) {
        if (namespaces == null) {
            namespaces = new HashSet();
            File file = FILE;
            if (file.exists()) {
                try {
                    FileReader reader = new FileReader(file);
                    BufferedReader br = new BufferedReader(reader);
                    for (; ;) {
                        String line = br.readLine();
                        if (line == null || line.trim().length() == 0) {
                            break;
                        }
                        namespaces.add(line);
                    }
                }
                catch (Exception ex) {
                  Log.getLogger().warning("[ImplicitImports]  Warning: Could not load " + file);
                    Log.getLogger().log(Level.WARNING, "Exception caught", ex);
                }
            }
        }
        return namespaces.contains(namespace);
    }
}
