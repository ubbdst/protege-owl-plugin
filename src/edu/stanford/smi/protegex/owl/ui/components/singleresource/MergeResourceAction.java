package edu.stanford.smi.protegex.owl.ui.components.singleresource;


import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.UBBSlotNames;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static edu.stanford.smi.protegex.owl.ui.actions.DeleteInstanceOrMoveToTrashAction.getTrashClass;

/**
 * A class that performs merge action
 *
 * @author Hemed Al Ruwehy
 * <p>
 * Universitetsbiblioteket i Bergen
 * 04.09.2017
 */
public class MergeResourceAction extends SetResourceAction {

    private PropertyValuesComponent component;


    public MergeResourceAction(PropertyValuesComponent component) {
        super("Select resource to merge", OWLIcons.getAddIcon(OWLIcons.RDF_INDIVIDUAL), component);
        this.component = component;
    }

    /**
     * Move a given instance to class Trash
     */
    private static void moveToTrash(RDFResource resource) {
        resource.setProtegeType(getTrashClass(resource.getOWLModel()));
    }

    /**
     * A wrapper for checking if a given collection has values
     */
    public static boolean hasValues(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Deletes a given resource
     */
    public static void deleteResource(RDFResource resource) {
        resource.delete();
    }

    @Override
    public void resourceSelected(RDFResource resource) {
        String text = "Are you sure you want to merge \"" + resource.getBrowserText() + "\" into \""
                + getSubject().getBrowserText() + "\"?";

        if (isMergeConfirmed(resource, text)) {
            onMerge(resource);
        }
    }

    /**
     * Performs merge, step by step.
     *
     * @param resource a resource to be merged
     */
    public void onMerge(RDFResource resource) {
        beforeMerge(resource);
        doMerge(resource);
        afterMerge(resource);
    }

    /**
     * Pre-validates instance before merge and provide useful message to the user
     *
     * @param resource a resource to be merged
     */
    public void beforeMerge(RDFResource resource) {
        if (resource.equals(getSubject())) {
            showMessageDialog(resource, "Instance cannot be merged to itself");
            throw new UnsupportedOperationException("Cannot merge instance to itself: [" + resource.getName() + "]");
        } else if (!resource.getProtegeType().equals(getSubject().getProtegeType())) {
            showMessageDialog(resource, "Cannot merge instances that belong to different classes");
            throw new UnsupportedOperationException("Cannot merge instances from different classes " +
                    "[" + resource.getName() + " , " + getSubject().getName() + "]");
        }
    }

    protected void doMerge(RDFResource resource) {
        copyValues(resource);
    }

    /*
        TODO: After merge, do the following:-
         a) Do not delete object, rather move it to class trash and give that information to user
            via Dialog box.
         b) Copy resource UUID and identifier to previousIdentifier of the target resource,
            in the form of "uuid:76547646" and "identifier:ubb-ms-0001"
         c) Copy resource URI to ubbont:previousURI of the target resource
    */
    protected void afterMerge(RDFResource resource) {
        moveToTrash(resource);
        assignPropertyValue(resource);
        showMessageDialog(resource, "Merging was successful and the merged instance has been moved to Trash");
        Log.getLogger().info("Instance " + resource.getName() + " merged into " + getSubject().getName());
    }

    /**
     * Assigns a given resource as a value to the property in which this action takes place
     */
    private void assignPropertyValue(Object resource) {
        getSubject().setPropertyValue(getPredicate(), resource);
    }

    /**
     * Copies all values from given resource to a target resource, except values for properties {@code ct:identifier},
     * {@code ubbont:uuid} and {@code ubbont:classHierarchyURI}.
     * If a predicate is of functional property, do not copy, user must decide which value to pick manually.
     *
     * @param resource a resource in which values of its properties need to be copied
     */
    @SuppressWarnings("unchecked")
    private void copyValues(RDFResource resource) {
        Collection<RDFProperty> properties = resource.getRDFProperties();
        for (RDFProperty property : properties) {
            if (!property.getName().equals(UBBSlotNames.UUID) &&
                    /*!property.equals(getOWLModel().getRDFTypeProperty()) &&*/
                    !property.getName().equals(UBBSlotNames.IDENTIFIER) &&
                    !property.getName().equals(UBBSlotNames.CLASS_HIERARCHY_URI)) {

                Collection valuesToBeCopied = resource.getPropertyValues(property);
                Collection existingValues = getSubject().getPropertyValues(property);
                    /*
                     Skip the iteration if we meet a functional property and
                     there exist values for such property in the target resource.
                     Otherwise, we wont be able to chose which value to keep.
                     */
                if (property.isFunctional() && hasValues(existingValues)) {
                    continue;
                }
                Collection combinedValues = new ArrayList();
                if (hasValues(valuesToBeCopied)) {
                    combinedValues.addAll(valuesToBeCopied);
                }
                if (hasValues(existingValues)) {
                    combinedValues.addAll(existingValues);
                }
                //Execute change to the target resource
                if (hasValues(combinedValues)) {
                    //Ensure no duplicates
                    getSubject().setPropertyValues(property, new HashSet(combinedValues));
                }
            }
        }

    }

    /**
     * Gets subject in which this action takes place
     */
    protected RDFResource getSubject() {
        return component.getSubject();
    }


    protected boolean isDeleteConfirmed(RDFResource resource, String text) {
        return ProtegeUI.getModalDialogFactory().showConfirmDialog(resource.getOWLModel(), text, "Confirm deletion");
    }


    protected OWLModel getOWLModel() {
        return getSubject().getOWLModel();
    }

    protected RDFProperty getPredicate() {
        return component.getPredicate();
    }


    private void showMessageDialog(RDFResource source, String text) {
        ProtegeUI.getModalDialogFactory().showMessageDialog(source.getOWLModel(), text);
    }

    /**
     * Shows a dialog with Yes/No options.
     *
     * @param source an instance to be merged
     */
    private boolean isMergeConfirmed(RDFResource source, String text) {
        return ProtegeUI.getModalDialogFactory().showConfirmDialog(source.getOWLModel(), text, "Confirm Merge");
    }
}

