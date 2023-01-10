package util;

import java.io.*;
import java.nio.file.Files;

/**
 * Read objects from a file
 */
public class ObjectSource {
    private ObjectInputStream inputStream;

    public ObjectSource(String filename)  {
        File file = new File(filename);
        try {
            inputStream = new ObjectInputStream(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public Object readObject() {
        Object o = null;
        try {
            o = getInputStream().readObject();
        } catch (EOFException e) {
            // Do nothing, EOF is expected to happen eventually
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    public void close() {
        try {
            getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}