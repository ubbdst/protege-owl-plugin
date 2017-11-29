package edu.stanford.smi.protegex.owl.util;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.HashMap;
import java.util.Map;


/**
 * TODO: convert this store into MultiValued/MultiKeyed store.
 */
public class SubjectPredicateStore {

    private static Map<RDFResource, RDFProperty> store = new HashMap<RDFResource, RDFProperty>();

    /*SubjectPredicateStore(RDFResource resource, RDFProperty property) {
        store.put(resource, property);
     }*/

    private  SubjectPredicateStore() {}

    /**
     * Stores this pair in the warehouse :)
     */
    public static void add(RDFResource resource, RDFProperty property) {
        store.put(resource, property);
    }

    /**
      Removes the entry for the specified key only if it is currently mapped to the specified value.
     */
    public static boolean remove(RDFResource resource, RDFProperty property) {
        RDFProperty currVal = store.get(resource);
        if(store.containsKey(resource) && currVal.equals(property)){
            store.remove(resource);
            return true;
        }
        return false;
    }


    /**
     * Checks if this pair exists in the store
     */
    public static boolean exists(RDFResource resource, RDFProperty property) {
        return  resource != null && property != null &&
                store.containsKey(resource) && store.get(resource).equals(property);
    }


}
