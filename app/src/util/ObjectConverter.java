package util;


import java.io.*;

/**
 * This static methods are used to convert and object to a byte array and back
 * again, this can then be written to a stream
 *
 * @author mathew
 */
public class ObjectConverter {

    /**
     * Converts object to byte array
     *
     * @param b Object to be converted
     * @return Byte array
     * @throws IOException
     */
    public static byte[] serialize(Object b) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(b);
            out.flush();
            byte[] ret = bos.toByteArray();
            bos.close();
            return ret;

        } catch (Exception e) {
            System.out.println("Failed to serialise object");
            e.printStackTrace();
            bos.close();
            return null;
        }

    }

    public static long getSerialVersionID(Object obj) {
        return ObjectStreamClass.lookup(obj.getClass()).getSerialVersionUID();
    }

    /**
     * Converts from a byte array back to an object
     *
     * @param bytes Array to be converted
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            bis.close();
            return o;
        } catch (Exception e) {
            System.out.println("Failed to deserialise object");
            e.printStackTrace();
            bis.close();
            return null;
        }

    }

}
