package util;

import java.io.*;
import java.nio.file.Files;

public class ObjectSink {
    private ObjectOutputStream outstream;

    public ObjectSink(String filename) {
        try {
            File file = new File(filename);
            outstream = new ObjectOutputStream(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeObject(Object o) {
        try {
            outstream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            outstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}