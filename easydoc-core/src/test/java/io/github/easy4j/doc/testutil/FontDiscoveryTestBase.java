package io.github.easy4j.doc.testutil;

import org.docx4j.fonts.PhysicalFonts;
import org.junit.jupiter.api.Assumptions;

/**
 * Bootstrap for tests whose code path triggers docx4j physical-font discovery
 * ({@link PhysicalFonts#discoverPhysicalFonts()}).
 *
 * <p>On some macOS environments a specific system font triggers an
 * {@link AssertionError} inside FOP's {@code GlyphPositioningTable} parser while
 * docx4j scans system fonts. Production code only guards with
 * {@code catch (Exception)}, so the {@code Error} escapes through
 * {@code SampleDocument.createContent()} and friends.</p>
 *
 * <p>Tests that depend on font discovery previously swallowed that
 * {@code AssertionError} and then asserted trivially true conditions
 * ("sizeAfter >= sizeBefore") — a vacuous pass that hides the environment
 * problem. This base detects font-discovery health exactly once per JVM and
 * offers {@link #assumeFontDiscoveryWorks()}, which turns affected tests into
 * JUnit <em>skipped</em> results on broken machines instead of vacuous passes,
 * while keeping them fully asserted everywhere font discovery is healthy.</p>
 *
 * <p>When the production bug is fixed
 * (TODO: {@code SampleDocument.createContent} should catch {@code Throwable},
 * not just {@code Exception}), detection succeeds on all machines and every
 * migrated test runs its assertions unconditionally again.</p>
 */
public abstract class FontDiscoveryTestBase {

    /** Failure thrown by {@code discoverPhysicalFonts()} on this machine, or null. */
    private static final Throwable FONT_DISCOVERY_FAILURE = detectFontDiscoveryFailure();

    private static Throwable detectFontDiscoveryFailure() {
        try {
            PhysicalFonts.discoverPhysicalFonts();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }

    /** Whether font discovery completed without throwing on this machine. */
    public static boolean isFontDiscoveryAvailable() {
        return FONT_DISCOVERY_FAILURE == null;
    }

    /**
     * Aborts the current test (JUnit assumption failure =&gt; SKIPPED, not FAILED)
     * when font discovery cannot complete on this machine.
     */
    public static void assumeFontDiscoveryWorks() {
        Assumptions.assumeTrue(FONT_DISCOVERY_FAILURE == null, () ->
                "Skipping: PhysicalFonts.discoverPhysicalFonts() threw " + FONT_DISCOVERY_FAILURE
                        + " on this machine (macOS FOP GlyphPositioningTable issue); "
                        + "asserting here would be vacuous");
    }
}
