package ui;

import org.openstreetmap.gui.jmapviewer.*;
import util.Util;

import java.awt.*;
import java.util.ArrayList;

/**
 * Color filled circle with a conditionally painted centered image.
 * Stores previously painted positions to determine eclipsing
 * and to choose appropriate painting radius.
 */
public class MapMarkerImage extends MapMarkerCircle { // Task 5: Make Prettier Markers
    public static final double defaultMarkerSize = 32.0;
    public static final int smallMarkerRadius = 5;
    public static ArrayList<Point> paintedPositions = new ArrayList<>();
    private final long id;
    private final Image image;

    private final String url;
    private final String text;

    public MapMarkerImage(Layer layer, Coordinate coordinate, Color backColor, long id, String name, String text, String url) {
        super(layer, name, coordinate, defaultMarkerSize/2, STYLE.FIXED, getDefaultStyle());
        setColor(null);
        setBackColor(backColor);

        this.id = id;
        this.image = Util.imageFromURL(url);
        this.url = url;
        this.text = text;
    }

    public long getId() { return id; }
    public String getURL() { return url; }
    public String getText() { return text; }
    public Image getImage() { return image; }

    /**
     * MODIFIES: this.paintedPositions
     * EFFECTS: Paints circle and image above it if radius is not small.
     * @param graphics Graphics instance
     * @param position Current position to paint marker at
     * @param radius Current radius to consider
     */
    @Override
    public void paint(Graphics graphics, Point position, int radius) {
        int paintRadius = calculatePaintRadius(position,radius);
        if (!paintedPositions.contains(position))
            paintedPositions.add(position);
        super.paint(graphics, position, paintRadius);

        int size = (int) defaultMarkerSize - 8;
        if (paintRadius == radius) // if the marker is big
            graphics.drawImage(image, position.x - size / 2, position.y - size / 2, size, size,
                (img, infoflags, x, y, width, height) -> false);
    }

    /**
     * EFFECTS: Compares positions, where other markers were painted, with current position
     * to determine eclipsing. Adds current position to paintedPositions if it has not added already.
     * @param position Position of the current marker
     * @param radius Default radius of the current marker
     * @return smallMarkerRadius or radius
     */
    public static int calculatePaintRadius(Point position, int radius) {
        for (Point point : paintedPositions) {
            double distance = position.distance(point);
            if (point!=position && distance!=0 && distance < radius * 2) {
                return smallMarkerRadius;
            }
        }
        return radius;
    }

    /**
    * MODIFIES: this.paintedPositions
    */
    public static void clearPaintedPositions() {
        paintedPositions = new ArrayList<>();
    }
}