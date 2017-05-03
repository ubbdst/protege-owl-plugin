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

package edu.stanford.smi.protegex.owl.ui.components.annotations;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesTable;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A JTable optimized for displaying a AnnotationsTableModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Matthew Horridge <matthew.horridge@cs.man.ac.uk>
 */
public class AnnotationsTable extends TriplesTable {

    private static HashSet singleLineProperties = new HashSet();


    static {
        singleLineProperties.add(RDFSNames.Slot.SEE_ALSO);
        singleLineProperties.add(RDFSNames.Slot.LABEL);
    }


    public AnnotationsTable(Project project, AnnotationsTableModel model) {
        super(project, model, "annotation");
       
        TableColumn valueColumn = getColumnModel().getColumn(TriplesTableModel.COL_VALUE);
        AnnotationsValueRenderer renderer = new AnnotationsValueRenderer();
        
        OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
        
        valueColumn.setCellRenderer(renderer);
        valueColumn.setCellEditor(new AnnotationsValueEditor(owlModel, this));
	    valueColumn.setPreferredWidth(600);
	    
	    TableColumn propertyColumn = getColumnModel().getColumn(AnnotationsTableModel.COL_PROPERTY);
	    propertyColumn.setCellRenderer(renderer);
	    propertyColumn.setPreferredWidth(200);
		
	    // Lang Column (always the last column)
	    TableColumn langColumn = getColumnModel().getColumn(getColumnCount() - 1);
        langColumn.setCellRenderer(renderer);
        langColumn.setCellEditor(new AnnotationsLangEditor(owlModel, this));
        // Set the default row height to be that of a text field
        
        setRowHeight(new JTextField().getPreferredSize().height);
        setGridColor(Color.LIGHT_GRAY);
        setShowGrid(true);
        setIntercellSpacing(new Dimension(1, 1));
    }


    public static Collection getSingleLineProperties() {
        return Collections.unmodifiableCollection(singleLineProperties);
    }


    public static boolean isMultiLineProperty(RDFProperty property) {
        return singleLineProperties.contains(property.getName()) == false;
    }

	
}
