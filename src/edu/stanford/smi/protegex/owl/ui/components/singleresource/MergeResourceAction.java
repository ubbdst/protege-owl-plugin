package edu.stanford.smi.protegex.owl.ui.components.singleresource;


import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.UBBSlotNames;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.Collection;
import java.util.HashSet;

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
             onMerge(resource);
        }
    }


    protected void onMerge(RDFResource resource) {
        beforeMerge(resource);
        doMerge(resource);
        afterMerge(resource);
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
            throw new UnsupportedOperationException("Cannot merge instances from different classes " +
                    "[" + resource.getName() + " , " + getSubject().getName() + "]");
        }
    }


    protected void doMerge(RDFResource resource) {
        copyValues(resource);
        Log.getLogger().info("Instance " + resource.getName() + " has been merged into " + getSubject().getName());
    }


    protected void afterMerge(RDFResource resource) {
        getSubject().setPropertyValue(getPredicate(), resource);
        if (isDeleteConfirmed(resource)) {
            deleteResource(resource);
        }
    }



    /**
     * Copy all values from given resource to a target resource, except values for properties <tt>dct:identifier</tt>,
     * <tt>ubbont:uuid</tt> and <tt>ubbont:classHierarchyURI</tt>.
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
                    if(property.isFunctional() && hasValues(existingValues)){
                        continue;
                    }
                    Collection combinedValues = new HashSet();
                    if (hasValues(valuesToBeCopied)) {
                        combinedValues.addAll(valuesToBeCopied);
                    }
                    if (hasValues(existingValues)) {
                        combinedValues.addAll(existingValues);
                    }
                    //Execute change to the target resource
                    if(hasValues(combinedValues)) {
                        getSubject().setPropertyValues(property, combinedValues);
                    }
                }
          }

    }


    /**
     *  A wrapper for checking if a given collection has values
     */
    private boolean hasValues(Collection collection) {
        return collection != null && !collection.isEmpty();
    }


    protected void deleteResource(RDFResource resource) {
        resource.delete();
        Log.getLogger().info("Resource " + resource.getName() + " has been deleted after merge");
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


    private OWLModel getOWLModel() {
        return getSubject().getOWLModel();
    }

    private RDFProperty getPredicate() {
        return component.getPredicate();
    }


    private void showMessageDialog(RDFResource source, String text) {
        ProtegeUI.getModalDialogFactory().showMessageDialog(source.getOWLModel(), text);
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

