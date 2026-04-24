package com.utc2.appreborn.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.data.repository.NewsRepository;
import com.utc2.appreborn.data.repository.StudentRepository;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.utils.MockHelper;

import java.util.List;

/**
 * HomeViewModel
 * ──────────────────────────────────────────────────────────────
 * MVVM ViewModel for {@link HomeFragment}.
 *
 * Responsibilities:
 *  • Holds and exposes all UI state as {@link LiveData}.
 *  • Delegates data fetching to repositories.
 *  • Survives configuration changes (rotation, etc.).
 *  • Cleans up resources (cancels active calls) in onCleared().
 *
 * Dependencies:
 *   implementation 'androidx.lifecycle:lifecycle-viewmodel:2.8.3'
 *   implementation 'androidx.lifecycle:lifecycle-livedata:2.8.3'
 *   (usually included transitively via fragment:1.8.3)
 *
 * Package: com.utc2.appreborn.ui.home
 */
public class HomeViewModel extends ViewModel {

    // ── Repositories ──────────────────────────────────────────
    private final NewsRepository    newsRepository;
    private final StudentRepository studentRepository;

    // ── Exposed LiveData ──────────────────────────────────────

    /** List of news items (mock → real after API responds). */
    private final LiveData<List<NewsItem>> newsLiveData;

    /** True while the news API call is in-flight. */
    private final LiveData<Boolean> isLoadingLiveData;

    /**
     * Student profile (name + MSSV).
     * Merged via MediatorLiveData so HomeFragment observes one stream.
     */
    private final MediatorLiveData<StudentProfile> studentProfileLiveData =
            new MediatorLiveData<>();

    /** The 6 feature items — static, no need for LiveData. */
    private final List<FeatureItem> featureList;

    // ═══════════════════════════════════════════════════════════
    //  Constructor
    // ═══════════════════════════════════════════════════════════

    public HomeViewModel() {
        newsRepository    = NewsRepository.getInstance();
        studentRepository = StudentRepository.getInstance();

        newsLiveData      = newsRepository.getNewsLiveData();
        isLoadingLiveData = newsRepository.getIsLoadingLiveData();
        featureList       = MockHelper.getFeatureList();

        // Wire student profile from repository into MediatorLiveData
        studentProfileLiveData.addSource(
                studentRepository.getStudentProfile(),
                studentProfileLiveData::setValue
        );
    }

    // ═══════════════════════════════════════════════════════════
    //  Public API — observed by HomeFragment
    // ═══════════════════════════════════════════════════════════

    public LiveData<List<NewsItem>> getNewsLiveData() {
        return newsLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<StudentProfile> getStudentProfileLiveData() {
        return studentProfileLiveData;
    }

    public List<FeatureItem> getFeatureList() {
        return featureList;
    }

    /**
     * Triggers a fresh news fetch from the API.
     * Called from HomeFragment's onViewCreated.
     */
    public void loadNews() {
        newsRepository.fetchNews();
    }

    // ═══════════════════════════════════════════════════════════
    //  Cleanup
    // ═══════════════════════════════════════════════════════════

    @Override
    protected void onCleared() {
        super.onCleared();
        // Cancel any in-flight Retrofit call to prevent memory leaks
        newsRepository.cancelActiveCall();
    }
}