package tests.query;

import filters.BasicFilter;
import org.junit.jupiter.api.Test;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import query.Query;
import twitter4j.Status;

import javax.swing.*;
import java.awt.*;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the query.
 */
public class TestQuery {
    @Test
    public void testUpdateTerminate() {
        JMapViewer map = makeMap();
        Query query = new Query("Fred", Color.CYAN,map);
        assertEquals(Color.CYAN,query.getColor());
        assertEquals("Fred",query.getQueryString());
        assertTrue(query.getFilter() instanceof BasicFilter);
        assertNotNull(query.getLayer());

        out.println("created query: "+query);

        Status status = tests.filters.TestFilters.makeStatus("Fred Flintstone");
        query.update(null, status);

        assertEquals(1, query.getLayer().getElements().size());

        query.terminate();
        assertFalse(query.getVisible());

        query.setVisible(true);
        assertTrue(query.getVisible());

        query.setCheckBox(new JCheckBox());
        assertNotNull(query.getCheckBox());
    }

    private JMapViewer makeMap() {
        return new JMapViewer() {

        };
    }
}
