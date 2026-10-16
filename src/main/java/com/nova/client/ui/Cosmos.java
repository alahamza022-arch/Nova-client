package com.nova.client.ui;

import com.nova.client.NovaConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

import java.util.Random;

/** Animated universe background: gradient sky, nebula glows, parallax twinkling stars, shooting stars. */
public final class Cosmos {
    public static final String[] NAMES = {"Nebula", "Aurora", "Supernova", "Cosmos"};
    // top, bottom, accent, accent2
    private static final int[][] T = {
            {0xFF05010F, 0xFF1A0B3A, 0xFFB388FF, 0xFF4FC3F7},
            {0xFF010A0F, 0xFF042B2A, 0xFF4DFFB0, 0xFF3D7BFF},
            {0xFF0F0301, 0xFF3A0F0B, 0xFFFF8A3D, 0xFFFF3D81},
            {0xFF010510, 0xFF0A1E4A, 0xFF5CA8FF, 0xFFB388FF}
    };
    private static final int N = 240;
    private static final float[] X = new float[N], Y = new float[N], Z = new float[N], P = new float[N];

    static {
        Random r = new Random(1337);
        for (int i = 0; i < N; i++) {
            X[i] = r.nextFloat(); Y[i] = r.nextFloat();
            Z[i] = 0.2f + r.nextFloat() * 0.8f; P[i] = r.nextFloat() * 6.28f;
        }
    }

    private static int idx() { return Math.floorMod(NovaConfig.get().theme, T.length); }
    public static int accent() { return T[idx()][2]; }
    public static int accent2() { return T[idx()][3]; }

    public static int lerp(int a, int b, float t) {
        int r = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int g = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int bl = (int) ((a & 255) * (1 - t) + (b & 255) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }

    private static void glow(DrawContext c, int cx, int cy, int r, int rgb) {
        int layers = 5;
        for (int l = 0; l < layers; l++) {
            int rr = r * (layers - l) / layers;
            for (int dy = -rr; dy <= rr; dy += 3) {
                int hw = (int) Math.sqrt((double) rr * rr - (double) dy * dy);
                c.fill(cx - hw, cy + dy, cx + hw, cy + dy + 3, (12 << 24) | (rgb & 0xFFFFFF));
            }
        }
    }

    public static void render(DrawContext c, int w, int h, int mx, int my, boolean dim) {
        int[] th = T[idx()];
        double t = Util.getMeasuringTimeMs();

        c.fillGradient(0, 0, w, h, th[0], th[1]);
        glow(c, (int) (w * 0.22 + Math.sin(t / 5000) * 30), (int) (h * 0.35), (int) (h * 0.42), th[2]);
        glow(c, (int) (w * 0.80 + Math.cos(t / 6500) * 30), (int) (h * 0.70), (int) (h * 0.38), th[3]);

        float px = mx - w / 2f, py = my - h / 2f;
        for (int i = 0; i < N; i++) {
            float z = Z[i];
            float sx = (X[i] * w + (float) t * 0.01f * z - px * 0.04f * z) % w; if (sx < 0) sx += w;
            float sy = (Y[i] * h - py * 0.04f * z) % h; if (sy < 0) sy += h;
            float tw = 0.55f + 0.45f * (float) Math.sin(t / 450.0 + P[i] * 3);
            int a = (int) (255 * Math.min(1f, z * tw + 0.15f));
            int s = z > 0.9f ? 2 : 1;
            int rgb = (i % 7 == 0) ? (th[2] & 0xFFFFFF) : 0xFFFFFF;
            c.fill((int) sx, (int) sy, (int) sx + s, (int) sy + s, (a << 24) | rgb);
        }

        // shooting star
        long id = (long) (t / 3500);
        float p = (float) ((t % 3500) / 900.0);
        if (p < 1f) {
            Random r = new Random(id * 31 + 7);
            float sx = r.nextFloat() * w * 0.8f, sy = r.nextFloat() * h * 0.45f;
            for (int k = 0; k < 14; k++) {
                int fx = (int) (sx + p * w * 0.35f - k * 5), fy = (int) (sy + p * h * 0.2f - k * 2.5f);
                int a = (int) (220 * (1 - p) * (1 - k / 14f));
                c.fill(fx, fy, fx + 2, fy + 1, (a << 24) | 0xFFFFFF);
            }
        }
        if (dim) c.fill(0, 0, w, h, 0x66000000);
    }
}
