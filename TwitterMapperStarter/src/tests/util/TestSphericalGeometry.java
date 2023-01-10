package tests.util;

import org.junit.jupiter.api.Test;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import util.SphericalGeometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSphericalGeometry {
    @Test
    public void testDistanceBetween() {
        double distance = SphericalGeometry.distanceBetween(
            new Coordinate(50,25),
            new Coordinate(50,0)
        );
        assertEquals(1778496,(int)distance);
    }
}