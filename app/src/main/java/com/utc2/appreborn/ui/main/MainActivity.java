package com.utc2.appreborn.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.components.LiquidBarKt;
import com.utc2.appreborn.ui.home.HomeFragment;

/**
 * MainActivity
 * ──────────────────────────────────────────────────────────────
 * Single-Activity host for the entire application.
 *
 * Responsibilities:
 *  1. Inflates activity_main.xml:
 *       • R.id.fragment_container  — FrameLayout hosting all Fragments
 *       • R.id.bottom_bar_compose  — ComposeView for LiquidBar
 *  2. Wires LiquidBar tab selections to Fragment swaps via switchTab().
 *  3. Exposes pushFragment() so child Fragments can navigate forward
 *     without coupling to MainActivity directly (cast to this class).
 *
 * Navigation contract:
 *  • Top-level tabs (switchTab)  → NO back-stack entry, fade animation.
 *  • Detail screens (pushFragment) → added to back-stack, slide animation.
 *
 * Package: com.utc2.appreborn.ui.main
 */
public class MainActivity extends AppCompatActivity {

    // ── Fragment tags (public so child fragments can reference) ──
    public static final String TAG_HOME     = "tag_home";
    public static final String TAG_SCHEDULE = "tag_schedule";
    public static final String TAG_REGISTER = "tag_register";
    public static final String TAG_RESULT   = "tag_result";
    public static final String TAG_PROFILE  = "tag_profile";

    // ═══════════════════════════════════════════════════════════
    //  Lifecycle
    // ═══════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupLiquidBar();

        // Show HomeFragment on first launch only
        // (savedInstanceState != null → rotation → FM restores state automatically)
        if (savedInstanceState == null) {
            switchTab(HomeFragment.class, TAG_HOME);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  LiquidBar wiring
    // ═══════════════════════════════════════════════════════════

    private void setupLiquidBar() {
        ComposeView bottomBarCompose = findViewById(R.id.bottom_bar_compose);

        LiquidBarKt.setupLiquidBottomBar(bottomBarCompose, navId -> {
            handleBottomNavSelection(navId);
            return null; // Kotlin Unit bridge
        });
    }

    /**
     * Routes LiquidBar item IDs (defined in bottom_nav_menu.xml)
     * to the corresponding Fragment.
     */
    private void handleBottomNavSelection(int navId) {
        if (navId == R.id.nav_home) {
            switchTab(HomeFragment.class, TAG_HOME);

        } else if (navId == R.id.nav_schedule) {
            // TODO: switchTab(ScheduleFragment.class, TAG_SCHEDULE);

        } else if (navId == R.id.nav_register) {
            // TODO: switchTab(RegisterFragment.class, TAG_REGISTER);

        } else if (navId == R.id.nav_result) {
            // TODO: switchTab(ResultFragment.class, TAG_RESULT);

        } else if (navId == R.id.nav_profile) {
            // TODO: switchTab(ProfileFragment.class, TAG_PROFILE);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  Public navigation helper
    //  (used by HomeFragment, and future detail Fragments)
    // ═══════════════════════════════════════════════════════════

    /**
     * Pushes a detail Fragment on top of the current screen.
     * Added to the back-stack → Back press pops it naturally.
     *
     * Usage from any Fragment:
     *   ((MainActivity) requireActivity())
     *       .pushFragment(SomeDetailFragment.newInstance(...), "tag_detail");
     *
     * @param fragment  the Fragment to display
     * @param tag       unique, stable back-stack / FM tag
     */
    public void pushFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,   // enter
                        android.R.anim.fade_out,         // exit
                        android.R.anim.fade_in,          // popEnter
                        android.R.anim.slide_out_right)  // popExit
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    // ═══════════════════════════════════════════════════════════
    //  Private navigation helpers
    // ═══════════════════════════════════════════════════════════

    /**
     * Switches to a top-level tab Fragment.
     *
     * Behaviour:
     *  • Fragment already visible           → no-op (prevents flicker).
     *  • Fragment exists but not visible    → reused (avoids re-creation).
     *  • Fragment not yet created           → instantiated via reflection.
     *  • Clears the entire back-stack first → tapping a tab always resets
     *    to the tab root (standard Android UX pattern).
     *
     * Animation: simple fade (cross-fade between tabs).
     *
     * @param fragmentClass  class to instantiate if not cached
     * @param tag            stable FragmentManager tag
     */
    private <T extends Fragment> void switchTab(Class<T> fragmentClass, String tag) {
        FragmentManager fm       = getSupportFragmentManager();
        Fragment        existing = fm.findFragmentByTag(tag);

        // Guard: already on screen
        if (existing != null && existing.isVisible()) return;

        try {
            // Reuse cached instance, or create a new one
            Fragment target = (existing != null)
                    ? existing
                    : fragmentClass.getDeclaredConstructor().newInstance();

            // Pop all detail screens that may have been pushed on this tab
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            fm.beginTransaction()
                    .setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    .replace(R.id.fragment_container, target, tag)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}