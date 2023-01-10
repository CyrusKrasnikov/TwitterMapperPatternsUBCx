package tests.ui;

import org.junit.jupiter.api.Test;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import ui.MapMarkerImage;
import util.Util;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMapMarkerImage {
    @Test
    public void testDefaultImage() {
        Layer layer = new Layer("test");
        Coordinate coord = new Coordinate(1,2);
        MapMarkerImage marker = new MapMarkerImage(layer,coord, Color.black,1,"name","text","url");
        assertEquals(Util.defaultImage,marker.getImage());

        assertEquals(1,marker.getId());
        assertEquals("url",marker.getURL());
        assertEquals("text",marker.getText());
    }

    @Test
    public void testPaintRadius() {
        Point position = new Point(20,20);
        int radius = 20;
        assertEquals(radius,MapMarkerImage.calculatePaintRadius(position,radius));

        MapMarkerImage.paintedPositions.add(position);
        Point position2 = new Point(25,25);
        assertEquals(MapMarkerImage.smallMarkerRadius,MapMarkerImage.calculatePaintRadius(position2,radius));
    }

    @Test
    public void testClearPaintedPoints() {

        MapMarkerImage.paintedPositions.clear();
        Point position = new Point(20, 20);
        MapMarkerImage.paintedPositions.add(position);
        assertEquals(MapMarkerImage.paintedPositions.size(),1);
        MapMarkerImage.clearPaintedPositions();

        assertEquals(MapMarkerImage.paintedPositions.size(),0);
    }
}