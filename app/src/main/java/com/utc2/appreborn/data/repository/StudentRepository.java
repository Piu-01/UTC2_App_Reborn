package com.utc2.appreborn.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utc2.appreborn.data.local.StudentLocalDataSource;
import com.utc2.appreborn.data.local.StudentProfile;

/**
 * StudentRepository
 * ──────────────────────────────────────────────────────────────
 * Single source of truth for the logged-in student's profile.
 *
 * Priority order for display name:
 *   1. Firebase Auth → displayName
 *   2. Firebase Auth → email prefix (before "@")
 *   3. StudentLocalDataSource → fullName  (mock or DB)
 *
 * The student_code (MSSV) always comes from the local data source
 * (database table STUDENT_PROFILE) — not from Firebase Auth.
 *
 * Package: com.utc2.appreborn.data.repository
 */
public class StudentRepository {

    // ── Singleton ─────────────────────────────────────────────
    private static StudentRepository instance;

    public static StudentRepository getInstance() {
        if (instance == null) {
            instance = new StudentRepository(StudentLocalDataSource.getInstance());
        }
        return instance;
    }

    private final StudentLocalDataSource localDataSource;

    private StudentRepository(StudentLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    // ═══════════════════════════════════════════════════════════
    //  Public API
    // ═══════════════════════════════════════════════════════════

    /**
     * Returns a {@link LiveData<StudentProfile>} that emits once
     * immediately with the best available data.
     *
     * LiveData is used here so the ViewModel can observe it
     * consistently alongside other LiveData streams.
     */
    public LiveData<StudentProfile> getStudentProfile() {
        MutableLiveData<StudentProfile> liveData = new MutableLiveData<>();

        // Read local profile first (fast — no I/O in current mock impl)
        StudentProfile localProfile = localDataSource.getStudentProfile();

        // Resolve the best display name
        String displayName = resolveDisplayName(localProfile.getFullName());

        liveData.setValue(new StudentProfile(
                localProfile.getStudentCode(),
                displayName
        ));

        return liveData;
    }

    // ── Private helpers ───────────────────────────────────────

    /**
     * Walks the priority chain for the user's display name:
     *   Firebase displayName → email prefix → fallback name.
     */
    private String resolveDisplayName(String fallbackName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Priority 1 — explicit display name set in Firebase
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                return displayName;
            }

            // Priority 2 — email prefix
            String email = user.getEmail();
            if (email != null && email.contains("@")) {
                return email.split("@")[0];
            }
        }

        // Priority 3 — DB / mock value
        return fallbackName;
    }
}