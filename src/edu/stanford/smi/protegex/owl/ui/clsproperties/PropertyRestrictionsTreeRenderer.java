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

package edu.stanford.smi.protegex.owl.ui.clsproperties;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyRestrictionsTreeRenderer extends ResourceRenderer {

    private boolean displayRestrictions;

    private boolean expanded;

    private boolean hideGlobalCharacteristics = true;

    private Cls originCls;


    public PropertyRestrictionsTreeRenderer(boolean displayRestrictions, boolean hideGlobalCharacteristics) {
        this.displayRestrictions = displayRestrictions;
        this.hideGlobalCharacteristics = hideGlobalCharacteristics;
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.expanded = expanded;
        return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }


    public void load(Object value) {
        if (value instanceof RestrictionTreeNode) {
            RestrictionTreeNode restrictionTreeNode = (RestrictionTreeNode) value;
            Icon icon = restrictionTreeNode.getIcon();
            clear();
            setMainIcon(icon);
            setMainText(restrictionTreeNode.getFillerText());
            originCls = restrictionTreeNode.getInheritedFromClass();
            Object userObject = restrictionTreeNode.getUserObject();
            if (userObject instanceof OWLQuantifierRestriction) {
                RDFResource filler = ((OWLQuantifierRestriction) userObject).getFiller();
                if (filler instanceof OWLClass) {
                    loadedClass = (OWLClass) filler;
                }
            }
            else if (userObject instanceof OWLCardinalityBase) {
                RDFResource filler = ((OWLCardinalityBase) userObject).getQualifier();
                if (filler instanceof OWLClass) {
                    loadedClass = (OWLClass) filler;
                }
            }
            else if (userObject instanceof OWLHasValue) {
                Object filler = ((OWLHasValue) userObject).getHasValue();
                if (filler instanceof OWLClass) {
                    loadedClass = (OWLClass) filler;
                }
            }
        }
        else if (value instanceof PropertyTreeNode) {
            PropertyTreeNode treeNode = (PropertyTreeNode) value;
            RDFProperty property = treeNode.getRDFProperty();
            Icon icon = treeNode.getIcon();
            setMainIcon(icon);
            String mainText = property.getBrowserText();
            setMainText(mainText);
            originCls = null;
            if (!hideGlobalCharacteristics) {
                Cls allRestrictionMetaCls = property.getOWLModel().getRDFSNamedClass(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION);
                boolean allValuesFromRestriction = displayRestrictions && treeNode.hasRestrictionOfType(allRestrictionMetaCls);
                if (!allValuesFromRestriction) {
                    RDFResource range = property.getRange(true);
                    String prefix = property.isFunctional() ? "single" : "multiple";
                    if (range != null) {
                        addText("    (" + prefix + " " + range.getBrowserText() + ")");
                    }
                    else if (property.isFunctional()) {
                        addText("    (" + prefix + ")");
                    }
                }
            }
            if (displayRestrictions && treeNode.getChildCount() > 0) { // && !expanded ) {
                addText("     (");
                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    RestrictionTreeNode restrictionTreeNode = (RestrictionTreeNode) treeNode.getChildAt(i);
                    addText(restrictionTreeNode.getOperatorName() + " ");
                    addText(restrictionTreeNode.getFillerText());
                    if (i < treeNode.getChildCount() - 1) {
                        addText(",  ");
                    }
                }
                addText(")");
            }
        }
        else {
            super.load(value);
        }
    }


    public void paint(Graphics g) {
        super.paint(g);
        int width = getWidth();
        FontMetrics fm = _fontMetrics;
        int mainTextLength = fm.stringWidth(getMainText());
        int ascent = fm.getAscent();
        if (originCls != null) {
            final String browserText = originCls.getBrowserText();
            String str = "[from " + browserText + "]";
            int strWidth = fm.stringWidth(str);
            int c = browserText.length();
            final int x = width - 3;//getMainIcon().getIconWidth();
            while (c > 3 && x - strWidth - 2 <= mainTextLength + 4) {
                c--;
                str = "[from " + browserText.substring(0, c) + "...]";
                strWidth = fm.stringWidth(str);
            }
            if (x - strWidth - 2 > mainTextLength + 4) {
                g.setColor(Color.gray);
                g.drawString(str, x - strWidth - 2, ascent);
            }
        }
    }
}
