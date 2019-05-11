package edu.utdallas.objectutils;

import java.io.Serializable;

public interface Wrapped extends Serializable {
    <T> T reconstruct() throws Exception;

    <T> T reconstruct(boolean updateStaticFields) throws Exception;
}
