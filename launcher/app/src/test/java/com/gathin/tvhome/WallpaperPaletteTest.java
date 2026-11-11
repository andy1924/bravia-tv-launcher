package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.*;

public class WallpaperPaletteTest {
    @Test public void darkTealWallpaperKeepsItsHue() {
        int[] result = WallpaperPalette.fromPixels(new int[]{0xFF10383D, 0xFF0D3038, 0xFF000000});
        assertTrue(result.length > 0);
        int c = result[0];
        assertTrue((c & 255) > ((c >> 16) & 255));
        assertTrue(((c >> 8) & 255) > ((c >> 16) & 255));
    }

    @Test public void sparseWarmPixelDoesNotChangeCoolWallpaper() {
        int[] pixels = new int[41];
        java.util.Arrays.fill(pixels, 0xFF153E55);
        pixels[0] = 0xFFFF2000;
        for (int c : WallpaperPalette.fromPixels(pixels)) {
            assertTrue((c & 255) > ((c >> 16) & 255));
        }
    }

    @Test public void neutralOrEmptyWallpaperHasUsableFallback() {
        assertTrue(WallpaperPalette.fromPixels(new int[0]).length > 0);
        assertTrue(WallpaperPalette.fromPixels(new int[]{0xFF000000, 0xFF333333}).length > 0);
    }
}
