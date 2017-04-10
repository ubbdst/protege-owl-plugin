package edu.stanford.smi.protegex.owl.util;

/**
 * A contract for generating instance URIs.
 * There may be different implementations of generating instance URI
 *
 * @author Hemed Ali Al Ruwehy
 *         The University of Bergen Library
 *         2017-04-10
 */
public interface InstanceURI {

    /**
     * Generate unique instance URI (name).
     * The method should check the knowledgebase (model) if the instance exists before creating a new one
     *
     * @return a new and unique instance URI
     */
    String generateUniqueInstanceName();

}
