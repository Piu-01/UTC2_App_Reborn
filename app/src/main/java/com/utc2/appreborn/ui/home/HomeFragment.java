package com.utc2.appreborn.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.home.adapter.FeatureAdapter;
import com.utc2.appreborn.ui.home.adapter.NewsAdapter;
import com.utc2.appreborn.ui.home.model.FeatureItem;
import com.utc2.appreborn.ui.home.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvUsername;
    private RecyclerView rvFeatures;
    private RecyclerView rvNews;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tv_username);
        rvFeatures = view.findViewById(R.id.rv_features);
        rvNews     = view.findViewById(R.id.rv_news);

        View ivAvatar = view.findViewById(R.id.iv_avatar);
        View ivAddBtn = view.findViewById(R.id.iv_add_btn);

        loadUserInfo();
        setupFeatureGrid();
        setupNewsFeed();

        ivAvatar.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Trang cá nhân", Toast.LENGTH_SHORT).show();
            // TODO: mở ProfileFragment sau
        });

        ivAddBtn.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Thẻ sinh viên", Toast.LENGTH_SHORT).show();
            // TODO: mở StudentCardFragment sau
        });
    }

    // ─────────────────────────────────────────────
    //  User info from Firebase Auth
    // ─────────────────────────────────────────────
    private void loadUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvUsername.setText(displayName);
            } else {
                // Fallback: use email prefix
                String email = user.getEmail();
                if (email != null) {
                    tvUsername.setText(email.split("@")[0]);
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    //  Feature 3×2 Grid
    // ─────────────────────────────────────────────
    private void setupFeatureGrid() {
        rvFeatures.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvFeatures.setNestedScrollingEnabled(false);
        rvFeatures.setHasFixedSize(true);

        List<FeatureItem> features = buildFeatureList();
        FeatureAdapter adapter = new FeatureAdapter(features, this::handleFeatureClick);
        rvFeatures.setAdapter(adapter);
    }

    private List<FeatureItem> buildFeatureList() {
        List<FeatureItem> list = new ArrayList<>();
        list.add(new FeatureItem("hoc_phi",        R.drawable.ic_hoc_phi,        "Học phí"));
        list.add(new FeatureItem("dich_vu_cong",   R.drawable.ic_dich_vu_cong,   "Dịch vụ công"));
        list.add(new FeatureItem("danh_gia",       R.drawable.ic_danh_gia,       "Đánh giá"));
        list.add(new FeatureItem("ki_tuc_xa",      R.drawable.ic_ki_tuc_xa,      "Kí túc xá"));
        list.add(new FeatureItem("ho_tro",         R.drawable.ic_ho_tro,         "Hỗ trợ 24/7"));
        list.add(new FeatureItem("danh_muc_khac",  R.drawable.ic_danh_muc_khac,  "Danh mục khác"));
        return list;
    }

    // ─────────────────────────────────────────────
    //  News Feed (vertical list)
    // ─────────────────────────────────────────────
    private void setupNewsFeed() {
        rvNews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNews.setNestedScrollingEnabled(false);
        rvNews.setHasFixedSize(false);

        List<NewsItem> newsList = buildNewsList();
        NewsAdapter adapter = new NewsAdapter(newsList, this::handleNewsClick);
        rvNews.setAdapter(adapter);
    }

    /** Replace with a real API/Firebase call later */
    private List<NewsItem> buildNewsList() {
        List<NewsItem> list = new ArrayList<>();
        list.add(new NewsItem("1",
                "Thông báo lịch nghỉ lễ 30/4 và 1/5 năm 2025",
                "20/04/2025",
                "Nhà trường thông báo lịch nghỉ lễ Giải phóng miền Nam và Quốc tế Lao động."));
        list.add(new NewsItem("2",
                "Kết quả xét học bổng học kỳ 2 năm học 2024–2025",
                "18/04/2025",
                "Phòng Công tác Sinh viên thông báo danh sách sinh viên được xét học bổng."));
        list.add(new NewsItem("3",
                "Thông báo đăng ký học phần học kỳ 3 năm 2025",
                "15/04/2025",
                "Sinh viên đăng ký học phần từ ngày 01/05 đến 10/05/2025."));
        list.add(new NewsItem("4",
                "Hướng dẫn làm thẻ sinh viên kỳ mới",
                "10/04/2025",
                "Sinh viên năm nhất cần nộp ảnh 3×4 tại Phòng Đào tạo trước 30/04."));
        return list;
    }

    // ─────────────────────────────────────────────
    //  Click Handlers
    // ─────────────────────────────────────────────
    private void handleFeatureClick(String featureId) {
        // TODO: Replace Toasts with proper navigation to each feature screen
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
        }
    }

    private void handleNewsClick(NewsItem item) {
        // TODO: Navigate to NewsDetailActivity / NewsDetailFragment
        Toast.makeText(requireContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
