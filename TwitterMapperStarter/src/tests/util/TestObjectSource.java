package tests.util;

import org.junit.jupiter.api.Test;
import util.ObjectSource;

import static org.junit.jupiter.api.Assertions.assertNull;

public class TestObjectSource {
    @Test
    public void testObjectSource(){
        ObjectSource objectSource = new ObjectSource("111");
        assertNull(objectSource.getInputStream());
      }
}