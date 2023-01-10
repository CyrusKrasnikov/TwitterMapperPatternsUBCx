package query;

import filters.Filter;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;
import twitter4j.Status;
import ui.MapMarkerImage;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * A query over the twitter stream.
 */
public class Query implements Observer { // Task 4: Complete the Query class
    // The map on which to display markers when the query matches
    private final JMapViewer map;
    // Each query has its own "layer" so they can be turned on and off all at once
    private final Layer layer;
    // The color of the outside area of the marker
    private final Color color;
    // The string representing the filter for this query
    private final String queryString;
    // The filter parsed from the queryString
    private final Filter filter;
    // The checkBox in the UI corresponding to this query (so we can turn it on and off and delete it)
    private JCheckBox checkBox;

    public Color getColor() {
        return color;
    }
    public String getQueryString() {
        return queryString;
    }
    public Filter getFilter() {
        return filter;
    }
    public Layer getLayer() {
        return layer;
    }
    public JCheckBox getCheckBox() {
        return checkBox;
    }
    public void setCheckBox(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }
    public void setVisible(boolean visible) {
        layer.setVisible(visible);
    }
    public boolean getVisible() { return layer.isVisible(); }

    public Query(String queryString, Color color, JMapViewer map) {
        this.queryString = queryString;
        this.filter = Filter.parse(queryString);
        this.color = color;
        this.layer = new Layer(queryString);
        this.map = map;
    }

    @Override
    public String toString() {
        return "Query: " + queryString;
    }

    /**
     * When the query is no longer interesting we terminate it and remove all traces of its existence.
     */
    public void terminate() { // Task 4
        java.util.List<MapObject> list = layer.getElements();
        layer.setVisible(false);
        for (MapObject marker : list) {
            map.removeMapMarker((MapMarker) marker);
        }
    }

    /**
     * EFFECTS: Called by TwitterSource when a new tweet is acquired. Matches filter adds marker to the map
     * MODIFIES: this
     * @param o     TwitterSource
     * @param arg   Status of tweet
     * @see     Status
     * @see     twitter.TwitterSource
     */
    @Override
    public void update(Observable o, Object arg) {
        Status status = (Status)arg;
        if(filter.matches(status)){
            Coordinate coordinate = Util.statusCoordinate(status);
            MapMarkerImage marker = new MapMarkerImage(layer,coordinate,color,
                    status.getId(),
                    status.getUser().getName(),
                    status.getText(),
                    status.getUser().getProfileImageURL()
            );
            layer.add(marker);
            layer.setVisibleTexts(false);
            map.addMapMarker(marker);
        }
    }
}