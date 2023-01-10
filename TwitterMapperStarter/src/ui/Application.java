package ui;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import query.Query;
import twitter.PlaybackTwitterSource;
import twitter.TwitterSource;
import util.ImageCache;
import util.SphericalGeometry;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * The Twitter viewer application
 * Derived from a JMapViewer demo program written by Jan Peter Stotz
 */
public class Application extends JFrame {
    // The content panel, which contains the entire UI
    private final ContentPanel contentPanel;
    // The provider of the tiles for the map, we use the Bing source
    private final BingAerialTileSource bing;
    // All of the active queries
    private List<Query> queries = new ArrayList<>();
    // The source of tweets, a TwitterSource, either live or playback
    private TwitterSource twitterSource;

    private void initialize() {
        // To use the live twitter stream, use the following line
        // twitterSource = new LiveTwitterSource();

        // To use the recorded twitter stream, use the following line
        // The number passed to the constructor is a speedup value:
        //  1.0 - play back at the recorded speed
        //  2.0 - play back twice as fast
        twitterSource = new PlaybackTwitterSource(60.0);

        queries = new ArrayList<>();
    }

    /**
     * A new query has been entered via the User Interface
     * @param   query   The new query object
     */
    public void addQuery(Query query) {
        queries.add(query);
        Set<String> allterms = getQueryTerms();
        twitterSource.setFilterTerms(allterms);
        contentPanel.addQuery(query);
        // Task 4: This is the place where you should connect the new query to the twitter source
        twitterSource.addObserver(query);
    }

    /**
     * The live twitter source uses this
     * to request matching tweets from the Twitter API.
     * @return A list of all terms mentioned in all queries
     */
    private Set<String> getQueryTerms() {
        Set<String> ans = new HashSet<>();
        for (Query q : queries) {
            ans.addAll(q.getFilter().terms());
        }
        return ans;
    }

    /**
     * Constructs the {@code Application}.
     */
    public Application() {
        super("Twitter content viewer");
        setSize(300, 300);
        initialize();

        bing = new BingAerialTileSource();

        // Do UI initialization
        contentPanel = new ContentPanel(this);
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Always have map markers showing.
        map().setMapMarkerVisible(true);
        // Always have zoom controls showing,
        // and allow scrolling of the map around the edge of the world.
        map().setZoomContolsVisible(true);
        map().setScrollWrapEnabled(true);

        // Use the Bing tile provider
        map().setTileSource(bing);

        //NOTE This is so that the map eventually loads the tiles once Bing attribution is ready.
        Coordinate coord = new Coordinate(0, 0);

        Timer bingTimer = new Timer();
        TimerTask bingAttributionCheck = new TimerTask() {
            @Override
            public void run() {
                // This is the best method we've found to determine when the Bing data has been loaded.
                // We use this to trigger zooming the map so that the entire world is visible.
                if (!bing.getAttributionText(0, coord, coord).equals("Error loading Bing attribution data")) {
                    map().setZoom(2);
                    bingTimer.cancel();
                }
            }
        };
        bingTimer.schedule(bingAttributionCheck, 100, 200);

        // Set up a motion listener to create a tooltip showing the tweets at the pointer position
        map().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                ICoordinate pos = map().getPosition(p);

                Collection<MapMarker> markers = getMarkersCovering(pos,pixelWidth(p));
                StringBuilder text = new StringBuilder();
                text.append("<html><table width=400>");
                int count = 0;
                for (MapMarker m :markers) {
                    if(count++>9) break; // limit to avoid overflow
                    MapMarkerImage marker = (MapMarkerImage) m;
                    String url = "file:data/boring.png";
                    if(ImageCache.getInstance().getImage(marker.getURL())!=Util.defaultImage)
                        url = marker.getURL();
                    text.append("<tr><td colspan=2>").append(marker.getName()).append(":</td></tr>")
                            .append("<tr><td><img width='48' height='48' src=\"").append(url).append("\"></td>")
                            .append("<td>").append(marker.getText()).append("</td>")
                            .append("</tr>");
                }
                if(markers.size()>0)
                    map().setToolTipText(text.toString());
                else
                    map().setToolTipText("");
            }
        });
        map().addJMVListener(jmvCommandEvent -> {
            if (jmvCommandEvent.getCommand() == JMVCommandEvent.COMMAND.ZOOM
                    || jmvCommandEvent.getCommand() == JMVCommandEvent.COMMAND.MOVE
            ) {
                MapMarkerImage.clearPaintedPositions();
                map().repaint();
            }
        });
    }

    /**
     * We use this to compute which tweet markers are at the current most position.
     * @param point Represents a pixel
     * @return How big is a single pixel on the map?
     */
    private double pixelWidth(Point point) {
        ICoordinate center = map().getPosition(point);
        ICoordinate edge = map().getPosition(new Point(point.x + 1, point.y));
        return SphericalGeometry.distanceBetween(center, edge);
    }

    /**
     *  Get those layers (of tweet markers) that are visible because their corresponding query is enabled
     * @return Layers set
     */
    private Set<Layer> getVisibleLayers() {
        Set<Layer> ans = new HashSet<>();
        for (Query q : queries) {
            if (q.getVisible()) {
                ans.add(q.getLayer());
            }
        }
        return ans;
    }

    /**
     * Get all the unique markers at the given map position, at the current map zoom setting
     * @param pos Coordinte
     * @param pixelWidth How big is a single pixel on the map
     * @return All the markers at the given map position
     */
    private Collection<MapMarker> getMarkersCovering(ICoordinate pos, double pixelWidth) {
        Map<Long,MapMarker> answer = new HashMap<>();
        Set<Layer> visibleLayers = getVisibleLayers();
        synchronized(this) {
            for (MapMarker m : map().getMapMarkerList()) {
                if (!visibleLayers.contains(m.getLayer())) continue;
                double distance = SphericalGeometry.distanceBetween(m.getCoordinate(), pos);
                if (distance < m.getRadius() * pixelWidth) {
                    answer.put(((MapMarkerImage)m).getId(),m);
                }
            }
        }
        return answer.values();
    }

    public JMapViewer map() {
        return contentPanel.getViewer();
    }

    /**
     * @param args Application program arguments (which are ignored)
     */
    public static void main(String[] args) {
        new Application().setVisible(true);
    }

    /**
     * Updates which queries are visible after any checkBox has been changed
     */
    public void updateVisibility() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Recomputing visible queries");
            for (Query q : queries) {
                JCheckBox box = q.getCheckBox();
                boolean state = box.isSelected();
                q.setVisible(state);
            }
            map().repaint();
        });
    }

    /**
     * After query has been deleted, remove all traces of it
     * @param query Expired query
     */
    public void terminateQuery(Query query) {
        // Task 4: This is the place where the expiring query disconnects from the twitter source
        twitterSource.deleteObserver(query);
        queries.remove(query);
        Set<String> allterms = getQueryTerms();
        twitterSource.setFilterTerms(allterms);
    }
}
