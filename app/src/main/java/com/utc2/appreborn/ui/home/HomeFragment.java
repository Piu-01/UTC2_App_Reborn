package com.utc2.appreborn.ui.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.utc2.appreborn.R;
import com.utc2.appreborn.data.local.StudentProfile;
import com.utc2.appreborn.databinding.FragmentHomeBinding;
import com.utc2.appreborn.ui.home.adapter.FeatureAdapter;
import com.utc2.appreborn.ui.home.adapter.NewsAdapter;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.ui.news.NewsDetailActivity;
import com.utc2.appreborn.utils.MockHelper;

/**
 * HomeFragment — REDESIGNED
 * ──────────────────────────────────────────────────────────────
 * Thay đổi:
 *   • Toolbar sticky với 3 nút mới: QR, Search, Notification
 *   • Scroll listener: icon toolbar đổi tint trắng → vàng khi
 *     AppBarLayout collapsed (nền chuyển sang #1E1E1E)
 *   • Tách riêng hàm xử lý cho từng nút icon
 *
 * Package: com.utc2.appreborn.ui.home
 */
public class HomeFragment extends Fragment {

    private static final String TAG          = "HomeFragment";
    private static final String URL_ALL_NEWS = "https://utc2.edu.vn/sinh-vien/thong-bao";

    // Ngưỡng % collapse để coi là "đã collapsed" (90%)
    private static final float COLLAPSE_THRESHOLD = 0.9f;

    private FragmentHomeBinding binding;
    private HomeViewModel       viewModel;
    private NewsAdapter         newsAdapter;
    private FeatureAdapter      featureAdapter;

    // Trạng thái toolbar — tránh set màu lặp liên tục
    private boolean isToolbarCollapsed = false;

    public HomeFragment() { super(R.layout.fragment_home); }

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

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupFeatureGrid();
        setupNewsFeed();
        observeViewModel();
        setupClickListeners();
        setupToolbarScrollBehavior();

        viewModel.loadNews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ═══════════════════════════════════════════════════════════
    //  Scroll behavior — toolbar color change
    // ═══════════════════════════════════════════════════════════

    /**
     * Lắng nghe AppBarLayout offset để biết khi nào toolbar
     * đã collapsed hoàn toàn, rồi đổi tint icon theo nền.
     *
     * Logic:
     *   • Expanded (ảnh hiện): icon trắng (#FFFFFF)
     *   • Collapsed (nền đen #1E1E1E): icon vàng (#FFC107) để
     *     contrast tốt hơn trên nền tối
     *
     * app:contentScrim="@color/toolbar_collapsed_bg" trong XML đã
     * tự xử lý việc đổi màu nền Toolbar khi cuộn — đây chỉ là
     * phần đổi màu icon đi kèm.
     */
    private void setupToolbarScrollBehavior() {
        binding.appBarLayout.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        int totalScrollRange = appBarLayout.getTotalScrollRange();
                        if (totalScrollRange == 0) return;

                        // collapsePercent: 0.0 = fully expanded, 1.0 = fully collapsed
                        float collapsePercent =
                                Math.abs(verticalOffset) / (float) totalScrollRange;

                        boolean shouldBeCollapsed = collapsePercent >= COLLAPSE_THRESHOLD;

                        // Chỉ update khi trạng thái thay đổi (tránh redraw liên tục)
                        if (shouldBeCollapsed != isToolbarCollapsed) {
                            isToolbarCollapsed = shouldBeCollapsed;
                            updateToolbarIconTint(isToolbarCollapsed);
                        }
                    }
                });
    }

    /**
     * Đổi tint của 3 icon toolbar + các ImageView trong toolbar.
     *
     * @param collapsed true = toolbar đang collapsed (nền tối)
     */
    private void updateToolbarIconTint(boolean collapsed) {
        // Expanded → trắng / Collapsed → vàng (accent_yellow)
        int tintColor = collapsed
                ? getResources().getColor(R.color.accent_yellow, null)
                : Color.WHITE;

        ColorStateList tintList = ColorStateList.valueOf(tintColor);

        binding.btnQr.setImageTintList(tintList);
        binding.btnSearch.setImageTintList(tintList);
        binding.btnNotification.setImageTintList(tintList);

        // iv_add_btn và username text cũng cần đổi màu
        binding.ivAddBtn.setImageTintList(tintList);
        binding.tvUsername.setTextColor(tintColor);
    }

    // ═══════════════════════════════════════════════════════════
    //  RecyclerViews
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
        binding.rvNews.setAdapter(newsAdapter);
    }

    // ═══════════════════════════════════════════════════════════
    //  Observers
    // ═══════════════════════════════════════════════════════════

    private void observeViewModel() {
        viewModel.getStudentProfileLiveData()
                .observe(getViewLifecycleOwner(), this::bindStudentProfile);

        viewModel.getNewsLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list != null) newsAdapter.submitList(list);
        });

        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), loading ->
                Log.d(TAG, "Loading: " + loading));
    }

    private void bindStudentProfile(StudentProfile profile) {
        if (profile == null) return;
        binding.tvUsername.setText(profile.getFullName());
    }

    // ═══════════════════════════════════════════════════════════
    //  Click listeners
    // ═══════════════════════════════════════════════════════════

    private void setupClickListeners() {
        // Cũ — giữ nguyên
        binding.ivAvatar.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Trang cá nhân", Toast.LENGTH_SHORT).show());

        // iv_add_btn → vẫn mở QR (giống trước)
        binding.ivAddBtn.setOnClickListener(v -> openQrFragment());

        // ── 3 NÚT MỚI TRÊN TOOLBAR ───────────────────────────

        // Nút QR (icon bên phải)
        binding.btnQr.setOnClickListener(v -> openQrFragment());

        // Nút Tìm kiếm
        binding.btnSearch.setOnClickListener(v -> handleSearchClick());

        // Nút Thông báo
        binding.btnNotification.setOnClickListener(v -> handleNotificationClick());

        // Nút xem thêm trên web
        binding.btnViewAllNews.setOnClickListener(v -> openAllNewsInBrowser());
    }

    // ── Feature grid clicks ───────────────────────────────────

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

    // ── News click ────────────────────────────────────────────

    private void handleNewsClick(NewsItem item) {
        Intent intent = new Intent(requireContext(), NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.EXTRA_TITLE,   item.getTitle());
        intent.putExtra(NewsDetailActivity.EXTRA_DATE,    item.getDate());
        intent.putExtra(NewsDetailActivity.EXTRA_CONTENT, item.getContent());
        startActivity(intent);
    }

    // ── 3 toolbar button handlers ─────────────────────────────

    /**
     * Tìm kiếm — TODO: mở SearchFragment hoặc Activity.
     * Hiện tại show Toast để giữ chỗ.
     */
    private void handleSearchClick() {
        Toast.makeText(requireContext(), "Tìm kiếm", Toast.LENGTH_SHORT).show();
        // TODO: startActivity(new Intent(requireContext(), SearchActivity.class));
    }

    /**
     * Thông báo — TODO: mở NotificationFragment.
     * Hiện tại show Toast để giữ chỗ.
     */
    private void handleNotificationClick() {
        Toast.makeText(requireContext(), "Thông báo", Toast.LENGTH_SHORT).show();
        // TODO: ((MainActivity) requireActivity()).pushFragment(new NotificationFragment(), "tag_notification");
    }

    // ═══════════════════════════════════════════════════════════
    //  Navigation helpers
    // ═══════════════════════════════════════════════════════════

    private void openQrFragment() {
        StudentProfile profile = viewModel.getStudentProfileLiveData().getValue();
        String name = (profile != null) ? profile.getFullName()    : MockHelper.getMockFullName();
        String code = (profile != null) ? profile.getStudentCode() : MockHelper.getMockStudentCode();
        ((MainActivity) requireActivity())
                .pushFragment(QrFragment.newInstance(name, code), QrFragment.TAG);
    }

    /**
     * Mở trang thông báo web UTC2 trong browser.
     * Fix Android 11+: try/catch thay vì resolveActivity().
     */
    private void openAllNewsInBrowser() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_ALL_NEWS));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(requireContext(),
                    "Không tìm thấy trình duyệt", Toast.LENGTH_SHORT).show();
        }
    }
}