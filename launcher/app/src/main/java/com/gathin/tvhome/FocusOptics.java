package com.gathin.tvhome;

/** Bounds the glass response so distant jumps and rapid navigation never distort the focus target. */
final class FocusOptics {
    static final float SCALE = 1.075f;
    static final long DURATION_MS = 280;

    private FocusOptics() { }

    static float stretch(float distanceDp) {
        if (Float.isNaN(distanceDp)) return 0f;
        return Math.min(0.055f, Math.abs(distanceDp) / 4000f);
    }

    static float envelope(float progress) {
        float t = Math.max(0f, Math.min(1f, progress));
        return (float) Math.sin(Math.PI * t);
    }
}
