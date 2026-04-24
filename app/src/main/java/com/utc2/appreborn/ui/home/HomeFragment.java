package com.utc2.appreborn.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utc2.appreborn.R;
import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.databinding.FragmentHomeBinding;
import com.utc2.appreborn.ui.home.adapter.FeatureAdapter;
import com.utc2.appreborn.ui.home.adapter.NewsAdapter;
import com.utc2.appreborn.ui.main.MainActivity;

/**
 * HomeFragment
 * ──────────────────────────────────────────────────────────────
 * Primary landing screen of the app.
 *
 * Architecture:
 *  • View Binding  — no more findViewById() calls.
 *  • HomeViewModel — all data/logic lives there; Fragment only
 *    observes LiveData and updates the UI.
 *  • Repositories  — ViewModel delegates to them; Fragment never
 *    touches repositories or Retrofit directly.
 *
 * Layout:  res/layout/fragment_home.xml
 * Package: com.utc2.appreborn.ui.home
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // ── View Binding ──────────────────────────────────────────
    // Nulled out in onDestroyView to avoid memory leaks.
    private FragmentHomeBinding binding;

    // ── ViewModel ─────────────────────────────────────────────
    private HomeViewModel viewModel;

    // ── Adapters ──────────────────────────────────────────────
    private NewsAdapter    newsAdapter;
    private FeatureAdapter featureAdapter;

    // ── Constructor ───────────────────────────────────────────
    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    // ═══════════════════════════════════════════════════════════
    //  Lifecycle
    // ═══════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain ViewModel scoped to this Fragment
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupFeatureGrid();
        setupNewsFeed();
        observeViewModel();
        setupClickListeners();

        // Kick off the API call
        viewModel.loadNews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Prevent memory leak — binding holds a reference to the view hierarchy
        binding = null;
    }

    // ═══════════════════════════════════════════════════════════
    //  Setup helpers
    // ═══════════════════════════════════════════════════════════

    /** Initialises the 3-column feature RecyclerView. */
    private void setupFeatureGrid() {
        featureAdapter = new FeatureAdapter(
                viewModel.getFeatureList(),
                this::handleFeatureClick
        );

        binding.rvFeatures.setLayoutManager(
                new GridLayoutManager(requireContext(), 3));
        binding.rvFeatures.setNestedScrollingEnabled(false);
        binding.rvFeatures.setHasFixedSize(true);
        binding.rvFeatures.setAdapter(featureAdapter);
    }

    /**
     * Initialises the news RecyclerView.
     * NewsAdapter starts empty; LiveData observer fills it.
     */
    private void setupNewsFeed() {
        newsAdapter = new NewsAdapter(this::handleNewsClick);

        binding.rvNews.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.rvNews.setNestedScrollingEnabled(false);
        binding.rvNews.setHasFixedSize(false);
        binding.rvNews.setAdapter(newsAdapter);
    }

    // ═══════════════════════════════════════════════════════════
    //  LiveData observers
    // ═══════════════════════════════════════════════════════════

    private void observeViewModel() {
        // ── Student profile ───────────────────────────────────
        viewModel.getStudentProfileLiveData().observe(getViewLifecycleOwner(),
                this::bindStudentProfile);

        // ── News list ─────────────────────────────────────────
        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(),
                newsList -> {
                    if (newsList != null) {
                        newsAdapter.submitList(newsList);
                    }
                });

        // ── Loading indicator ─────────────────────────────────
        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(),
                isLoading -> {
                    // TODO: show/hide a ProgressBar or shimmer here
                    Log.d(TAG, "News loading: " + isLoading);
                });
    }

    /** Applies {@link StudentProfile} data to the header. */
    private void bindStudentProfile(StudentProfile profile) {
        if (profile == null) return;
        binding.tvUsername.setText(profile.getFullName());
        // MSSV is passed to QrFragment — no need to display it here
    }

    // ═══════════════════════════════════════════════════════════
    //  Click listeners
    // ═══════════════════════════════════════════════════════════

    private void setupClickListeners() {
        // Avatar → Profile (TODO: replace Toast once ProfileFragment is merged)
        binding.ivAvatar.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Trang cá nhân", Toast.LENGTH_SHORT).show()
        );

        // "+" icon → QR card screen
        binding.ivAddBtn.setOnClickListener(v -> openQrFragment());
    }

    /**
     * Handles taps on feature grid cards.
     * Each case shows a Toast as a placeholder until
     * the corresponding feature branch is merged.
     */
    private void handleFeatureClick(String featureId) {
        switch (featureId) {
            case "hoc_phi":
                Toast.makeText(requireContext(), "Học phí", Toast.LENGTH_SHORT).show();
                // TODO: navigate to HocPhiFragment
                break;
            case "dich_vu_cong":
                Toast.makeText(requireContext(), "Dịch vụ công", Toast.LENGTH_SHORT).show();
                break;
            case "danh_gia":
                Toast.makeText(requireContext(), "Đánh giá", Toast.LENGTH_SHORT).show();
                break;
            case "ki_tuc_xa":
                Toast.makeText(requireContext(), "Kí túc xá", Toast.LENGTH_SHORT).show();
                break;
            case "ho_tro":
                Toast.makeText(requireContext(), "Hỗ trợ 24/7", Toast.LENGTH_SHORT).show();
                break;
            case "danh_muc_khac":
                Toast.makeText(requireContext(), "Danh mục khác", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.w(TAG, "Unknown feature id: " + featureId);
        }
    }

    /** Handles taps on news rows. */
    private void handleNewsClick(com.utc2.appreborn.ui.home.model.NewsItem item) {
        // TODO: navigate to NewsDetailFragment with item.getId()
        Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    // ═══════════════════════════════════════════════════════════
    //  Navigation
    // ═══════════════════════════════════════════════════════════

    /**
     * Opens the QR-card screen.
     *
     * Student profile is read from the ViewModel's cached LiveData
     * value (already populated from the repository).
     */
    private void openQrFragment() {
        StudentProfile profile = viewModel.getStudentProfileLiveData().getValue();

        String name = (profile != null) ? profile.getFullName()    : "";
        String code = (profile != null) ? profile.getStudentCode() : "";

        QrFragment qrFragment = QrFragment.newInstance(name, code);

        ((MainActivity) requireActivity()).pushFragment(qrFragment, QrFragment.TAG);
    }
}