package tests.util;

import org.junit.jupiter.api.Test;
import util.Util;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TestImage {
    @Test
    public void testImage() {
        BufferedImage image = Util.imageFromURL("https://www.cs.ubc.ca/~norm");
        assertNotNull(image);
        assertEquals(128,image.getWidth());
        assertEquals(128,image.getHeight());
    }
}
