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

package edu.stanford.smi.protegex.owl.ui.cls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.JFileChooser;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;

/**
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class ExtractTaxonomyAction extends ResourceAction {

    private JFileChooser fileChooser;

    public static final String GROUP = "Extract/";

    public ExtractTaxonomyAction() {
        super("Extract (sub) taxonomy to text file...", Icons.getBlankIcon(), GROUP);
    }

    public void actionPerformed(ActionEvent e) {

        if (fileChooser == null) {
            fileChooser = new JFileChooser(".");
        }
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter fw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(fw);
                performAction((RDFSNamedClass) getResource(), pw);
                fw.close();
                ProtegeUI.getModalDialogFactory().showMessageDialog(getOWLModel(),
                        "Taxonomy has been exported to " + file);
            } catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(getOWLModel(),
                        "File could not be exported:\n" + ex);
            }
        }
    }

    @Override
    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof OWLSubclassPane && resource instanceof RDFSNamedClass;
    }

    public static void performAction(RDFSNamedClass rootClass, PrintWriter pw) {
        Set reached = new HashSet();
        performAction(rootClass, reached, "", pw);
    }

    public static void performAction(Cls cls, Set reached, String baseStr, PrintWriter pw) {
        reached.add(cls);
        pw.println(baseStr + cls.getBrowserText());
        baseStr += "\t";
        List<Cls> directSubclasses = new ArrayList<Cls>(cls.getDirectSubclasses());
        Collections.sort(directSubclasses, new FrameComparator());
        for (Object element : directSubclasses) {
            Cls subCls = (Cls) element;
            if (subCls instanceof RDFSNamedClass && subCls.isVisible()) {
                if (reached.contains(subCls)) {
                    pw.println(baseStr + "(" + subCls.getBrowserText() + ")...");
                } else {
                    performAction(subCls, reached, baseStr, pw);
                }
            }
        }
    }
}
