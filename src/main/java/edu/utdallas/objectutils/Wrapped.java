package edu.utdallas.objectutils;

import java.io.Serializable;

/**
 * Represents a wrapped object which is <code>Serializable</code> and implements
 * <code>equals</code> and <code>hashCode</code> methods appropriately.
 *
 * @author Ali Ghanbari
 */
public interface Wrapped extends Serializable {
    <T> T reconstruct() throws Exception;

    <T> T reconstruct(boolean updateStaticFields) throws Exception;
}
