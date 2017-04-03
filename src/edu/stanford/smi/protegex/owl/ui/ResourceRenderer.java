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

package edu.stanford.smi.protegex.owl.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.Icon;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.StringUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.Deprecatable;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.manchester.ManchesterOWLParserUtil;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * A FrameRenderer which displays a small A behind those anonymous classes
 * that do have annotation properties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceRenderer extends FrameRenderer {
   
	private static final long serialVersionUID = 7348775773884916553L;

	/**
     * The Frame that is focused, i.e. displayed with a frame around it
     */
    private Frame focusedFrame;

    protected boolean showQuotes = true;
    
    protected RDFSClass loadedClass;

    protected SimpleInstance loadedInstance;

    public static final Color FOCUS_COLOR = new Color(128, 0, 255);

    private Slot directSuperclassesSlot;

    private HashMap<String, Color> colorMap;

    private HashMap<String, Color> greyedColorMap;

    private static final Color RESTRICTION_KEYWORD_COLOR = Color.MAGENTA.darker();

    private static final Color GREYED_RESTRICTION_KEYWORD_COLOR = new Color(180, 100, 200);

    private static final Color LOGICAL_OPERAND_KEYWORD_COLOR = Color.CYAN.darker();

    private static final Color GREYED_LOGICAL_OPERAND_COLOR = new Color(120, 160, 245);

    private static final Color COMMENT_COLOR = Color.GRAY;


    public ResourceRenderer() {
        this(null);
    }

    public ResourceRenderer(boolean showQuotes) {
        this(null, showQuotes);
    }
    
    public ResourceRenderer(Slot directSuperclassesSlot) {
    	this(directSuperclassesSlot, true);
    }
    
    public ResourceRenderer(Slot directSuperclassesSlot, boolean showQuotes) {
    	this.showQuotes = showQuotes;
        this.directSuperclassesSlot = directSuperclassesSlot;
        setDisplayHiddenIcon(false);
        colorMap = new HashMap<String, Color>();
        greyedColorMap = new HashMap<String, Color>();
        addKeyword(ManchesterOWLParserUtil.getMinKeyword(), RESTRICTION_KEYWORD_COLOR, GREYED_RESTRICTION_KEYWORD_COLOR);
        addKeyword(ManchesterOWLParserUtil.getExactKeyword(), RESTRICTION_KEYWORD_COLOR, GREYED_RESTRICTION_KEYWORD_COLOR);
        addKeyword(ManchesterOWLParserUtil.getMaxKeyword(), RESTRICTION_KEYWORD_COLOR, GREYED_RESTRICTION_KEYWORD_COLOR);
        addKeyword(ManchesterOWLParserUtil.getSomeKeyword(), RESTRICTION_KEYWORD_COLOR, GREYED_RESTRICTION_KEYWORD_COLOR);
        addKeyword(ManchesterOWLParserUtil.getAllKeyword(), RESTRICTION_KEYWORD_COLOR, GREYED_RESTRICTION_KEYWORD_COLOR);
        addKeyword(ManchesterOWLParserUtil.getHasKeyword(), RESTRICTION_KEYWORD_COLOR, GREYED_RESTRICTION_KEYWORD_COLOR);
        addKeyword(ManchesterOWLParserUtil.getAndKeyword(), LOGICAL_OPERAND_KEYWORD_COLOR, GREYED_LOGICAL_OPERAND_COLOR);
        addKeyword(ManchesterOWLParserUtil.getOrKeyword(), LOGICAL_OPERAND_KEYWORD_COLOR, GREYED_LOGICAL_OPERAND_COLOR);
        addKeyword(ManchesterOWLParserUtil.getNotKeyword(), LOGICAL_OPERAND_KEYWORD_COLOR, GREYED_LOGICAL_OPERAND_COLOR);

    }


    protected void addKeyword(String keyWord, Color color, Color greyedColor) {
        colorMap.put(keyWord, color);
        colorMap.put(keyWord.toLowerCase(), color);
        colorMap.put(keyWord.toUpperCase(), color);
        greyedColorMap.put(keyWord, greyedColor);
        greyedColorMap.put(keyWord.toLowerCase(), greyedColor);
        greyedColorMap.put(keyWord.toUpperCase(), greyedColor);
    }


    public static void addAnnotationFlag(FrameRenderer renderer, Cls cls) {
        for (Object element : cls.getOwnSlots()) {
            Slot slot = (Slot) element;
            if (slot instanceof OWLProperty && ((OWLProperty) slot).isAnnotationProperty()) {
                if (cls.getDirectOwnSlotValues(slot).size() > 0) {
                    renderer.appendIcon(OWLIcons.getImageIcon("Annotations"));
                    return;
                }
            }
        }
    }


    private void addInverseSlot(Slot slot) {
        Slot inverse = slot.getInverseSlot();
        if (inverse != null) {
            appendText(" " + (char) 0x2194 + " " + inverse.getBrowserText());
        }
    }


    private void addNamedEquivalentClses(OWLNamedClass cls) {
        for (Iterator it = ((Cls) cls).getDirectOwnSlotValues(directSuperclassesSlot).iterator(); it.hasNext();) {
            Cls nextCls = (Cls) it.next();
            if (nextCls instanceof OWLNamedClass) {
                OWLNamedClass otherCls = (OWLNamedClass) nextCls;
                if (((Cls) otherCls).getDirectOwnSlotValues(directSuperclassesSlot).contains(cls)) {
                    appendText(" " + (char) 0x2261 + " " + otherCls.getBrowserText());
                }
            }
        }
    }


    protected Icon getClsIcon(Cls cls) {
        return cls.getIcon();
    }


    private Color getTextColor(String text) {
        Color c = null;
        if (_grayedText == false) {
            c =colorMap.get(text);
        }
        else {
            c = greyedColorMap.get(text);
        }
        return c;
    }
    
    @Override
	public void setMainText(String text) {        	
		super.setMainText(showQuotes ? text : StringUtilities.unquote(text));
	}


    @Override
	protected void paintString(Graphics graphics,
                               String s,
                               Point point,
                               Color color,
                               Dimension dimension) {
        if (loadedClass != null) {
            if (loadedClass instanceof OWLClass) {
                if (color != null) {
                    graphics.setColor(color);
                }
                int y = (dimension.height + _fontMetrics.getAscent()) / 2 - 2; // -2 is a bizarre fudge factor that makes it look
                // better!
                StringTokenizer tok = new StringTokenizer(s, " ()[]{}", true);
                while (tok.hasMoreTokens()) {
                    String curTok = tok.nextToken();
                    Color oldColor = graphics.getColor();
                    Font oldFont = graphics.getFont();
                    Color highlightColor = null;
                    int fontStyle = Font.PLAIN;
                    if (loadedClass instanceof OWLAnonymousClass) {
                        if (s.startsWith("//")) {
                            highlightColor = COMMENT_COLOR;
                            fontStyle = Font.ITALIC;
                        }
                        else {
                            highlightColor = getTextColor(curTok);
                            fontStyle = Font.BOLD;
                        }
                    }
                    if (highlightColor != null) {
                        graphics.setColor(highlightColor);
                        graphics.setFont(graphics.getFont().deriveFont(fontStyle));
                    }
                    else {
                        OWLModel model = loadedClass.getOWLModel();
                        if (model instanceof JenaOWLModel) {
                            Frame f = model.getRDFResource(curTok);
                            if (f instanceof OWLNamedClass) {
                                if (((OWLNamedClass) f).isConsistent() == false) {
                                    graphics.setColor(Color.RED);
                                    graphics.setFont(graphics.getFont().deriveFont(Font.BOLD));
                                }
                            }
                        }
                    }
                    graphics.drawString(curTok, point.x, y);
                    point.x += graphics.getFontMetrics().stringWidth(curTok);
                    graphics.setColor(oldColor);
                    graphics.setFont(oldFont);
                }
            }
            else {
                super.paintString(graphics, s, point, color, dimension);
            }
        }
        else {
            if (loadedInstance != null) {
                OWLModel model = ((RDFResource) loadedInstance).getOWLModel();
                if (loadedInstance.getDirectType().equals(model.getOWLOntologyClass())) {
                    if (!((DefaultOWLOntology) loadedInstance).isAssociatedTriplestoreEditable()) {
                        color = Color.GRAY;
                        graphics.setFont(graphics.getFont().deriveFont(Font.ITALIC));
                    }
                }
            }
            super.paintString(graphics, s, point, color, dimension);
        }
    }


    @Override
	protected void loadCls(Cls cls) {
        setMainIcon(getClsIcon(cls));
        loadClsAfterIcon(cls);
    }


    @Override
	public void load(Object o) {
        super.load(o);
        if (o instanceof RDFSClass) {
            loadedClass = (RDFSClass) o;
        }
        else if (o instanceof SimpleInstance) {
            loadedInstance = (SimpleInstance) o;
        }
    }


    protected void loadClsAfterIcon(Cls cls) {
        setMainText(cls.getBrowserText());
        appendText(getInstanceCountString(cls));
        setBackgroundSelectionColor(Colors.getClsSelectionColor());
        if (cls instanceof RDFSClass) {
            if (cls instanceof OWLAnonymousClass) {
                addAnnotationFlag(this, cls);
            }
            else if (directSuperclassesSlot != null && cls instanceof OWLNamedClass) {
                addNamedEquivalentClses((OWLNamedClass) cls);
            }
            if (cls instanceof Deprecatable && ((Deprecatable) cls).isDeprecated()) {
                addIcon(OWLIcons.getDeprecatedIcon());
            }
            loadedClass = (RDFSClass) cls;
        }
        else {
            loadedClass = null;
        }
    }


    @Override
	protected void loadSlot(Slot slot) {
        super.loadSlot(slot);
        addInverseSlot(slot);
        if (slot instanceof Deprecatable && ((Deprecatable) slot).isDeprecated()) {
            addIcon(OWLIcons.getDeprecatedIcon());
        }
    }


    @Override
	public void paint(Graphics g) {
        super.paint(g);
        if (loadedClass != null && focusedFrame != null) {
            int ICON_TEXT_GAP = 3;
            int baseX = ICON_TEXT_GAP + getMainIcon().getIconWidth();
            String str = getMainText();
            int len = str.length();
            String browserText = focusedFrame.getBrowserText();
            int browserTextLen = browserText.length();
            for (int i = 0; i < len; i++) {
                if (i == 0 || !isOWLNameCharacter(str.charAt(i - 1))) {
                    if (str.regionMatches(i, browserText, 0, browserTextLen) &&
                        (i + browserTextLen >= len || !isOWLNameCharacter(str.charAt(i + browserTextLen)))) {
                        g.setColor(FOCUS_COLOR);
                        int x = baseX + _fontMetrics.stringWidth(str.substring(0, i));
                        int y = getHeight() - 1;
                        int width = _fontMetrics.stringWidth(browserText);
                        g.drawRect(x - 1, 0, width + 2, y);
                        i += browserTextLen;
                    }
                }
            }
        }
    }


    private boolean isOWLNameCharacter(char c) {
        return Character.isJavaIdentifierPart(c) || c == '-';
    }


    public void setFocusedFrame(Frame frame) {
        focusedFrame = frame;
    }
}
