package com.gathin.tvhome;

/** Small, dependency-free palette for the StandBy clock's low-frequency ambient light. */
final class WallpaperPalette {
    private WallpaperPalette() { }

    static int[] fromPixels(int[] pixels) {
        long[][] sums = new long[12][4];
        int total = 0;
        for (int pixel : pixels) {
            int r = (pixel >> 16) & 255, g = (pixel >> 8) & 255, b = pixel & 255;
            int max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
            int delta = max - min;
            if (max < 16 || delta < max * 0.22f) continue;
            float hue = max == r ? ((float) (g - b) / delta + 6f) % 6f
                    : max == g ? (float) (b - r) / delta + 2f : (float) (r - g) / delta + 4f;
            int bin = Math.min(11, (int) (hue * 2f));
            sums[bin][0] += r; sums[bin][1] += g; sums[bin][2] += b; sums[bin][3]++;
            total++;
        }
        int[] colors = new int[3];
        int count = 0;
        while (count < colors.length) {
            int best = -1;
            for (int i = 0; i < sums.length; i++) {
                if (sums[i][3] > 0 && (best < 0 || sums[i][3] > sums[best][3])) best = i;
            }
            if (best < 0 || sums[best][3] < total * 0.08f) break;
            long n = sums[best][3];
            float r = (float) sums[best][0] / n, g = (float) sums[best][1] / n, b = (float) sums[best][2] / n;
            float lift = 144f / Math.max(r, Math.max(g, b));
            colors[count++] = 0xFF000000 | (Math.round(r * lift) << 16)
                    | (Math.round(g * lift) << 8) | Math.round(b * lift);
            sums[best][3] = 0;
        }
        if (count == 0) return new int[]{0xFF244875, 0xFF286E78, 0xFF584875};
        return java.util.Arrays.copyOf(colors, count);
    }
}
