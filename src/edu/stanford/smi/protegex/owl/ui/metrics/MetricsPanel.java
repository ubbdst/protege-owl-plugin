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

package edu.stanford.smi.protegex.owl.ui.metrics;

import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.util.ModelMetrics;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 20, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MetricsPanel extends JPanel {

    private JTree tree;

    public static final Icon headerIcon = OWLIcons.getImageIcon("metricsheadericon.png");

    public static final Icon dataIcon = OWLIcons.getImageIcon("metricsdataicon.png");


    public MetricsPanel(OWLModel model) {
        ModelMetrics ModelMetrics = new ModelMetrics(model);
        ModelMetrics.calculateMetrics();
        tree = new JTree(createRootNode(ModelMetrics));
        tree.setCellRenderer(new MetricRenderer());
        setLayout(new BorderLayout(7, 7));
        add(new JScrollPane(tree));
        expandTree();
    }


    public void expandTree() {
        tree.setSelectionRow(0);
        ComponentUtilities.fullSelectionExpand(tree, 100);
    }


    private TreeNode createRootNode(ModelMetrics metrics) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Metrics");

        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Classes");
        rootNode.add(node);
        DefaultMutableTreeNode namedClsNode = new DefaultMutableTreeNode("Named classes");
        node.add(namedClsNode);
        namedClsNode.add(createMetricNode("Total", metrics.getNamedClassCount()));
        namedClsNode.add(createMetricNode("Primitive", metrics.getPrimitiveClassCount()));
        namedClsNode.add(createMetricNode("Defined", metrics.getDefinedClassCount()));

        DefaultMutableTreeNode parentsNode = new DefaultMutableTreeNode("Parents");
        namedClsNode.add(parentsNode);
        parentsNode.add(createMetricNode("Mean (named)", metrics.getMeanParents()));
        parentsNode.add(createMetricNode("Mode (named)", metrics.getModeParents()));
        parentsNode.add(createMetricNode("Max (named)", metrics.getMaxParents()));

        DefaultMutableTreeNode inferredParentsNode = new DefaultMutableTreeNode("Inferred parents");
        namedClsNode.add(inferredParentsNode);
        inferredParentsNode.add(createMetricNode("Mean (named)", metrics.getMeanInferredParents()));
        inferredParentsNode.add(createMetricNode("Mode (named)", metrics.getModeInferredParents()));
        inferredParentsNode.add(createMetricNode("Max (named)", metrics.getMaxInferredParents()));


        DefaultMutableTreeNode siblingsNode = new DefaultMutableTreeNode("Siblings");
        namedClsNode.add(siblingsNode);
        siblingsNode.add(createMetricNode("Mean", metrics.getMeanSiblings()));
        siblingsNode.add(createMetricNode("Mode", metrics.getModeSiblings()));
        siblingsNode.add(createMetricNode("Max", metrics.getMaxSiblings()));


        DefaultMutableTreeNode anonClsNode = new DefaultMutableTreeNode("Anonymous Classes");
        node.add(anonClsNode);
        // Restrictions
        DefaultMutableTreeNode restrictionNode = new DefaultMutableTreeNode("Restrictions");
        anonClsNode.add(restrictionNode);
        restrictionNode.add(createMetricNode("Total", metrics.getRestrictionCount()));
        restrictionNode.add(createMetricNode("Existential", metrics.getSomeValuesFromCount()));
        restrictionNode.add(createMetricNode("Universal", metrics.getAllValuesFromCount()));
        restrictionNode.add(createMetricNode("Cardinality", metrics.getCardinalityCount()));
        restrictionNode.add(createMetricNode("MinCardinality", metrics.getMinCardinalityCount()));
        restrictionNode.add(createMetricNode("MaxCardinality", metrics.getMaxCardinalityCount()));
        restrictionNode.add(createMetricNode("HasValue", metrics.getHasValueCount()));

        DefaultMutableTreeNode propertiesNode = new DefaultMutableTreeNode("Properties");
        rootNode.add(propertiesNode);
        propertiesNode.add(createMetricNode("Total", metrics.getObjectPropertyCount()));
        propertiesNode.add(createMetricNode("Object", metrics.getObjectPropertyCount()));
        propertiesNode.add(createMetricNode("Datatype", metrics.getDatatypePropertyCount()));
        propertiesNode.add(createMetricNode("Annotation", metrics.getAnnotationPropertyCount()));
        propertiesNode.add(createMetricNode("Properties with a domain specified", metrics.getPropertDomainCount()));
        propertiesNode.add(createMetricNode("Properties with a range specified", metrics.getPropertyRangeCount()));
        propertiesNode.add(createMetricNode("Properties with an inverse specified", metrics.getInversePropertyCount()));


        return rootNode;
    }


    private DefaultMutableTreeNode createMetricNode(String name, Object value) {
        return new DefaultMutableTreeNode(new MetricObject(name, value));
    }


    private DefaultMutableTreeNode createMetricNode(String name, int value) {
        return createMetricNode(name, new Integer(value));
    }


    public Dimension getPreferredSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(400, screenSize.height / 2);
    }

}

