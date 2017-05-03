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

package edu.stanford.smi.protegex.owl.ui.importstree;

import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

import javax.swing.tree.TreePath;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImportsTree extends SelectableTree implements HostResourceDisplay {

    private static int MAX_EXPANSIONS = 50;

    private OWLOntology rootOntology;

    public ImportsTree(OWLOntology rootOntology) {
        super(null, new ImportsTreeRoot(rootOntology));
        this.rootOntology = rootOntology;
        setCellRenderer(new ResourceRenderer());
        setRootVisible(false);
        ComponentUtilities.fullSelectionExpand(this, MAX_EXPANSIONS);
    }


    private void addResources(Set result, ImportsTreeNode node) {
        Object value = node.getUserObject();
        if (!result.contains(value)) {
            result.add(value);
            for (int i = 0; i < node.getChildCount(); i++) {
                ImportsTreeNode childNode = (ImportsTreeNode) node.getChildAt(i);
                addResources(result, childNode);
            }
        }
    }


    public OWLOntology getRootOntology() {
        return rootOntology;
    }


    /**
     * Gets the currently selected resources.
     *
     * @return a Set of RDFResources
     */
    public Set getSelectedResources() {
        Set result = new HashSet();
        TreePath[] paths = getSelectionPaths();
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                ImportsTreeNode node = (ImportsTreeNode) path.getLastPathComponent();
                result.add(node.getUserObject());
            }
        }
        else {
            LazyTreeRoot root = (LazyTreeRoot) getModel().getRoot();
            addResources(result, (ImportsTreeNode) root.getChildAt(0));
        }
        return result;
    }


    public boolean displayHostResource(RDFResource resource) {
    	if (rootOntology == null) {
    		Log.getLogger().warning("Root ontology = null!");    		
    		return false;
    	}
    	
        boolean result = false;
        if (resource instanceof OWLOntology) {

            List importsPath = new ArrayList();
            importsPath.add(resource);
            Collection ontologies = rootOntology.getOWLModel().getOWLOntologies();
            boolean progress = true;
            while (!importsPath.contains(rootOntology) && progress) {
                progress = false;
                for (Iterator i = ontologies.iterator(); i.hasNext();) {
                    OWLOntology ont = (OWLOntology) i.next();
                    OWLOntology previous = (OWLOntology) importsPath.get(importsPath.size() - 1);
                    if (ont.getImports().contains(previous.getURI().toString()) &&
                        !importsPath.contains(ont)) {
                        progress = true;
                        importsPath.add(ont);
                    }
                }
            }
            setSelectionPath(ComponentUtilities.getTreePath(this, importsPath));
            result = true;
        }
        return result;
    }
}
