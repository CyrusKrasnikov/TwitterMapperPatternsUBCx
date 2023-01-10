package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton that caches images loaded from twitter urls.
 */
public class ImageCache {
    private static final ImageCache theInstance = new ImageCache();
    private final BufferedImage defaultImage = null;

    public static ImageCache getInstance() {
        return theInstance;
    }

    private final Map<String, BufferedImage> cache = new HashMap<>();
    private final Map<String, String> pathCache = new HashMap<>();

    private ImageCache() {

    }

    public BufferedImage getImage(String url) {
        BufferedImage ans = cache.get(url);
        if (ans == null) {
            ans = Util.imageFromURL(url);
            cache.put(url, ans);
        }
        return ans;
    }

    public void loadImage(String url) {
        BufferedImage ans = cache.get(url);
        if (ans == null) {
            cache.put(url, defaultImage);
            Thread t = new Thread(() -> {
                BufferedImage ans1 = Util.imageFromURL(url);
                SwingUtilities.invokeLater(() -> cache.put(url, ans1));

            });
            t.start();
        }
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Hashes string using sha256 algorithm
     * @param data input string
     * @return Hash
     * @throws RuntimeException if SHA-256 algorithm instance is not found
     */
    private String sha256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = data.getBytes();
            md.update(bytes);
            byte[] hash = md.digest();
            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't find SHA-256");
        }
    }
    private String hashURL(String url) {
        String hash = pathCache.get(url);
        if (hash == null) {
            hash = sha256(url);
            pathCache.put(url,hash);
        }
        return hash;
    }

    // I'm going to assume that hashing is good enough and collisions are rare enough
    private String saveImage(BufferedImage image, String path) {
        File dir = new File("data/imagecache");
        if (!dir.isDirectory()) {
            boolean result = dir.mkdir();
            if(!result)
                throw new RuntimeException("mkdir returned false");
        }
        String pathString = "data/imagecache/" + path + ".png";
        File f = new File(pathString);
        pathString = f.getAbsolutePath();
        if (f.canRead()) return pathString;
        try {
            ImageIO.write(image, "png", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathString;
    }

    public String imagePath(String url) {
        String path = hashURL(url);
        path = saveImage(getImage(url), path);
        return path;
    }

    public BufferedImage getDefaultImage() {
        return defaultImage;
    }
}