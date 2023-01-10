package tests.util;

import org.junit.jupiter.api.Test;
import tests.filters.TestFilters;
import twitter4j.GeoLocation;
import twitter4j.Status;
import util.ImageCache;
import util.Util;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {
    @Test
    public void testSaveImage() {
        ImageCache imageCache = ImageCache.getInstance();
        String path = imageCache.imagePath("myurl");

        String pathString = "data/imagecache/E312BF07D4EB43E6AC55CE936D73403DBE0332E2BB76790227B7B75F968687B7.png";
        File f = new File(pathString);
        pathString = f.getAbsolutePath();

        assertEquals(path,pathString);
        assertTrue(f.exists());
    }

    @Test
    public void testStatusLocation(){
        Status status = TestFilters.makeStatus("text");
        GeoLocation location = Util.statusLocation(status);
        assertEquals(0,location.getLatitude());
        assertEquals(1,location.getLongitude());
    }
}