package edu.stanford.smi.protegex.owl.ui.components.singleresource;


import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.UBBSlotNames;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that performs merge action
 *
 * @author Hemed Al Ruwehy
 *
 * Universitetsbiblioteket i Bergen
 * 04.09.2017
 */
public class MergeResourceAction extends SetResourceAction {

    private PropertyValuesComponent component;


    public MergeResourceAction(PropertyValuesComponent component) {
        super("Select resource to merge", OWLIcons.getAddIcon(OWLIcons.RDF_INDIVIDUAL), component);
        this.component = component;
    }


    @Override
    public void resourceSelected(RDFResource resource) {
        if (isMergeConfirmed(resource)) {
            //TODO: Do a merge logic
             /*
              Step 1:  Copy all values from resource to target resource, except dct:identifier, ubbont:uuid og classHierarchyURI.
              When a predicate in the target resource has a functional property, do not copy to that property

              Case 2: Make all references from the source subject point to target subject
              */
             onMerge(resource);
        }
    }


    protected void onMerge(RDFResource resource) {
        beforeMerge(resource);
        doMerge(resource);
        afterMerge(resource);
    }


    protected void doMerge(RDFResource resource) {
        copyValues(resource);
        Log.getLogger().info("Instance " + resource.getName() + " has been merged into " + getSubject().getName());
    }


    /**
     * Pre validate instance before merge and provide useful message to the user
     *
     * @param resource a resource to be merged
     */
    protected void beforeMerge(RDFResource resource) {
        if (resource.equals(getSubject())) {
            showMessageDialog(resource, "Instance cannot be merged to itself");
            throw new UnsupportedOperationException("Cannot merge instance to itself: [" + resource.getName() + "]");
        } else if (!resource.getProtegeType().equals(getSubject().getProtegeType())) {
            showMessageDialog(resource, "Cannot merge instances that belong to different classes");
            throw new UnsupportedOperationException("Cannot merge instances with different classes " +
                    "[" + resource.getName() + " , " + getSubject().getName() + "]");
        }
    }


    protected void afterMerge(RDFResource resource) {
        if (isDeleteConfirmed(resource)) {
            deleteResource(resource);
        }
    }


    private void showMessageDialog(RDFResource source, String text) {
        ProtegeUI.getModalDialogFactory().showMessageDialog(source.getOWLModel(), text);
    }


    protected void deleteResource(RDFResource resource) {
        resource.delete();
        Log.getLogger().info("Resource " + resource.getName() + " has been deleted after merge");
    }


    private void copyValues(RDFResource resource) {
        Collection<RDFProperty> properties = resource.getRDFProperties();
        for (RDFProperty property : properties) {
            if (!property.isFunctional()) {
                if (!property.getName().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") &&
                        !property.getName().equals(UBBSlotNames.UUID) &&
                        !property.getName().equals(UBBSlotNames.IDENTIFIER) &&
                        !property.getName().equals(UBBSlotNames.CLASS_HIERARCHY_URI)) {

                    Collection combinedValues = new ArrayList();
                    Collection valuesToBeCopied = resource.getPropertyValues(property);
                    Collection existingValues = getSubject().getPropertyValues(property);

                    if (valuesToBeCopied != null && !valuesToBeCopied.isEmpty()) {
                        combinedValues.addAll(valuesToBeCopied);
                    }
                    if (existingValues != null && !existingValues.isEmpty()) {
                        combinedValues.addAll(existingValues);
                    }
                    getSubject().setPropertyValues(property, combinedValues);
                }
            }

        }

    }


    /**
     * Get subject (this is a selected instance in the instance browser hierarchy)
     */
    private RDFResource getSubject() {
        return component.getSubject();
    }


    protected boolean isDeleteConfirmed(RDFResource resource) {
        String text = "Do you want to delete the merged instance \"" + resource.getBrowserText() + "\"?";
        return ProtegeUI.getModalDialogFactory()
                .showConfirmDialog(resource.getOWLModel(), text, "Confirm deletion");
    }


    /**
     * Shows a dialog with Yes/No options.
     *
     * @param source an instance to be merged to another instance
     */
    private boolean isMergeConfirmed(RDFResource source) {
        String text = "Are you sure you want to merge \"" + source.getBrowserText() + "\" into \""
                + getSubject().getBrowserText() + "\"?";
        return ProtegeUI.getModalDialogFactory()
                .showConfirmDialog(source.getOWLModel(), text, "Confirm merge");
    }
}

