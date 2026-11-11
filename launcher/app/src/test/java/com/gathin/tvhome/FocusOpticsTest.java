package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.*;

public class FocusOpticsTest {
    @Test public void longAndReverseJumpsStayWithinTheSameDistortionLimit() {
        assertEquals(FocusOptics.stretch(10000f), FocusOptics.stretch(-10000f), 0f);
        assertTrue(FocusOptics.stretch(10000f) <= 0.055f);
        assertEquals(0f, FocusOptics.stretch(0f), 0f);
    }

    @Test public void settledFocusHasNoLingeringStretch() {
        assertEquals(0f, FocusOptics.envelope(-1f), 0.00001f);
        assertEquals(0f, FocusOptics.envelope(1f), 0.00001f);
        assertEquals(0f, FocusOptics.envelope(2f), 0.00001f);
        assertEquals(1f, FocusOptics.envelope(0.5f), 0.00001f);
    }
}
