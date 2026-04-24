package com.utc2.appreborn.ui.home;

import android.content.Intent;
import android.net.Uri;
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
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.ui.news.NewsDetailActivity;

/**
 * HomeFragment — UPDATED
 * ──────────────────────────────────────────────────────────────
 * Thay đổi trong version này:
 *   • Nút "Xem thêm thông báo" mở browser → utc2.edu.vn/sinh-vien/thong-bao
 *   • loadNews() → fetchNewsIfNeeded() (cache 24h)
 *   • forceRefresh() khi cần làm mới thủ công
 *
 * Package: com.utc2.appreborn.ui.home
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // URL trang thông báo sinh viên trên web UTC2
    private static final String URL_ALL_NEWS =
            "https://utc2.edu.vn/sinh-vien/thong-bao";

    private FragmentHomeBinding binding;
    private HomeViewModel       viewModel;
    private NewsAdapter         newsAdapter;
    private FeatureAdapter      featureAdapter;

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

        // AndroidViewModel cần Application — ViewModelProvider tự xử lý
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupFeatureGrid();
        setupNewsFeed();
        observeViewModel();
        setupClickListeners();

        // Chỉ gọi API nếu cache hết hạn (> 24h)
        viewModel.loadNews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ═══════════════════════════════════════════════════════════
    //  Setup
    // ═══════════════════════════════════════════════════════════

    private void setupFeatureGrid() {
        featureAdapter = new FeatureAdapter(
                viewModel.getFeatureList(), this::handleFeatureClick);
        binding.rvFeatures.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvFeatures.setNestedScrollingEnabled(false);
        binding.rvFeatures.setHasFixedSize(true);
        binding.rvFeatures.setAdapter(featureAdapter);
    }

    private void setupNewsFeed() {
        newsAdapter = new NewsAdapter(this::handleNewsClick);
        binding.rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNews.setNestedScrollingEnabled(false);
        binding.rvNews.setHasFixedSize(false);
        binding.rvNews.setAdapter(newsAdapter);
    }

    // ═══════════════════════════════════════════════════════════
    //  Observers
    // ═══════════════════════════════════════════════════════════

    private void observeViewModel() {
        viewModel.getStudentProfileLiveData()
                .observe(getViewLifecycleOwner(), this::bindStudentProfile);

        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(), newsList -> {
            if (newsList != null) newsAdapter.submitList(newsList);
        });

        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            // Hiện/ẩn loading nếu có ProgressBar trong layout
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            Log.d(TAG, "Loading: " + isLoading);
        });
    }

    private void bindStudentProfile(StudentProfile profile) {
        if (profile == null) return;
        binding.tvUsername.setText(profile.getFullName());
    }

    // ═══════════════════════════════════════════════════════════
    //  Click handlers
    // ═══════════════════════════════════════════════════════════

    private void setupClickListeners() {
        binding.ivAvatar.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Trang cá nhân", Toast.LENGTH_SHORT).show());

        binding.ivAddBtn.setOnClickListener(v -> openQrFragment());

        // Nút "Xem thêm thông báo" → mở trình duyệt
        binding.btnViewAllNews.setOnClickListener(v -> openAllNewsInBrowser());
    }

    /**
     * Mở trang thông báo UTC2 trong trình duyệt mặc định.
     */
    private void openAllNewsInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(URL_ALL_NEWS));
        // Fallback nếu không có trình duyệt (rất hiếm)
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(),
                    "Không tìm thấy trình duyệt", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFeatureClick(String featureId) {
        switch (featureId) {
            case "hoc_phi":
                Toast.makeText(requireContext(), "Học phí", Toast.LENGTH_SHORT).show();
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
                Log.w(TAG, "Unknown feature: " + featureId);
        }
    }

    private void handleNewsClick(NewsItem item) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_TITLE,   item.getTitle());
        intent.putExtra(NewsDetailActivity.EXTRA_DATE,    item.getDate());
        intent.putExtra(NewsDetailActivity.EXTRA_CONTENT, item.getContent());
        startActivity(intent);
    }

    // ═══════════════════════════════════════════════════════════
    //  Navigation
    // ═══════════════════════════════════════════════════════════

    private void openQrFragment() {
        StudentProfile profile = viewModel.getStudentProfileLiveData().getValue();
        String name = (profile != null) ? profile.getFullName()    : "";
        String code = (profile != null) ? profile.getStudentCode() : "";
        ((MainActivity) requireActivity())
                .pushFragment(QrFragment.newInstance(name, code), QrFragment.TAG);
    }
}