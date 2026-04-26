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

/* ── BỎ COMMENT KHI CÓ ROOM DB ────────────────────────────────
import com.utc2.appreborn.data.local.AppDatabase;
import com.utc2.appreborn.data.local.UserDao;
import com.utc2.appreborn.data.local.StudentDao;
import com.utc2.appreborn.data.local.UserEntity;
import com.utc2.appreborn.data.local.StudentEntity;
import java.util.concurrent.Executors;
─────────────────────────────────────────────────────────────── */

import com.utc2.appreborn.databinding.FragmentHomeBinding;
import com.utc2.appreborn.ui.home.adapter.FeatureAdapter;
import com.utc2.appreborn.ui.home.adapter.NewsAdapter;
import com.utc2.appreborn.ui.home.model.NewsItem;
import com.utc2.appreborn.ui.main.MainActivity;
import com.utc2.appreborn.ui.news.NewsDetailActivity;
import com.utc2.appreborn.utils.MockHelper;

/**
 * HomeFragment
 * ──────────────────────────────────────────────────────────────
 * Thay đổi trong version này:
 *
 *  FIX: Nút "Xem thêm thông báo" không mở browser trên Android 11+.
 *    Root cause: intent.resolveActivity() trả về null trên API 30+
 *    vì thiếu <queries> trong AndroidManifest.xml.
 *    Fix: Dùng try/catch startActivity() trực tiếp thay vì
 *    kiểm tra resolveActivity().
 *
 *  NEW: Header hiển thị tên + MSSV từ Mock, sẵn sàng switch sang DB.
 *
 * Package: com.utc2.appreborn.ui.home
 */
public class HomeFragment extends Fragment {

    private static final String TAG          = "HomeFragment";
    private static final String URL_ALL_NEWS = "https://utc2.edu.vn/sinh-vien/thong-bao";

    private FragmentHomeBinding binding;
    private HomeViewModel       viewModel;
    private NewsAdapter         newsAdapter;
    private FeatureAdapter      featureAdapter;

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
        loadUserHeader();   // ← hiển thị tên người dùng

        viewModel.loadNews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ═══════════════════════════════════════════════════════════
    //  Header — tên + MSSV
    // ═══════════════════════════════════════════════════════════

    /**
     * Hiển thị tên người dùng trong header.
     *
     * HIỆN TẠI: lấy từ Mock qua StudentRepository (đã có sẵn).
     *
     * KHI CÓ ROOM DB: bỏ comment đoạn DB bên dưới,
     * xoá/comment đoạn Mock bên trên.
     */
    private void loadUserHeader() {

        // ── ĐANG DÙNG: Mock via ViewModel/Repository ──────────
        // (tự động được observe trong observeViewModel())

        /* ── BỎ COMMENT KHI CÓ ROOM DB ──────────────────────────
        long currentUserId = getCurrentUserIdFromFirebase();

        AppDatabase db = AppDatabase.getInstance(requireContext());
        UserDao    userDao    = db.userDao();
        StudentDao studentDao = db.studentDao();

        // Observe từ DB — tự cập nhật khi data thay đổi
        userDao.getUserById(currentUserId)
               .observe(getViewLifecycleOwner(), userEntity -> {
                   if (userEntity != null) {
                       binding.tvUsername.setText(userEntity.getFullName());
                   }
               });
        ─────────────────────────────────────────────────────── */
    }

    /* ── BỎ COMMENT KHI CÓ ROOM DB ────────────────────────────
    private long getCurrentUserIdFromFirebase() {
        // Map Firebase UID (String) sang user_id (Long) của bảng USER
        // Tuỳ cách bạn lưu mapping này — SharedPreferences hoặc Firestore
        return 1L; // placeholder
    }
    ─────────────────────────────────────────────────────────── */

    // ═══════════════════════════════════════════════════════════
    //  Setup RecyclerViews
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

        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading ->
                Log.d(TAG, "Loading: " + isLoading));
    }

    private void bindStudentProfile(StudentProfile profile) {
        if (profile == null) return;
        binding.tvUsername.setText(profile.getFullName());
    }

    // ═══════════════════════════════════════════════════════════
    //  Click listeners
    // ═══════════════════════════════════════════════════════════

    private void setupClickListeners() {
        binding.ivAvatar.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Trang cá nhân", Toast.LENGTH_SHORT).show());

        binding.ivAddBtn.setOnClickListener(v -> openQrFragment());

        // Nút xem thêm → mở trình duyệt
        binding.btnViewAllNews.setOnClickListener(v -> openAllNewsInBrowser());
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

    /**
     * FIX: Nút "Xem thêm thông báo" không hoạt động trên Android 11+.
     *
     * Nguyên nhân: resolveActivity() trả về null trên API 30+ khi
     * thiếu <queries> trong AndroidManifest.xml (package visibility).
     *
     * Fix: Dùng try/catch startActivity() trực tiếp — nếu không có
     * app nào xử lý được thì catch ActivityNotFoundException.
     */
    private void openAllNewsInBrowser() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(URL_ALL_NEWS));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(requireContext(),
                    "Không tìm thấy trình duyệt", Toast.LENGTH_SHORT).show();
        }
    }

    private void openQrFragment() {
        StudentProfile profile = viewModel.getStudentProfileLiveData().getValue();
        String name = (profile != null) ? profile.getFullName()    : MockHelper.getMockFullName();
        String code = (profile != null) ? profile.getStudentCode() : MockHelper.getMockStudentCode();

        ((MainActivity) requireActivity())
                .pushFragment(QrFragment.newInstance(name, code), QrFragment.TAG);
    }
}