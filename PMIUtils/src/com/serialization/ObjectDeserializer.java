package com.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * Created by kevin on 05.05.17.
 */
public class ObjectDeserializer {

    /**
     * Read the object from Base64 string.
     */
    public static <T extends Serializable> T fromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getUrlDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return (T)o;
    }

}
