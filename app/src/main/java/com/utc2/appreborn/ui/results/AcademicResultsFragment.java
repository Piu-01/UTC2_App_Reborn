package com.utc2.appreborn.ui.results;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.results.grades.GradesFragment;
import com.utc2.appreborn.ui.results.leaderboard.LeaderboardFragment;
import com.utc2.appreborn.ui.results.scholarship.ScholarshipFragment;
import com.utc2.appreborn.ui.results.warning.WarningsFragment;

/**
 * AcademicResultsFragment — Dashboard "Kết quả học tập"
 *
 * Hiển thị thẻ thông tin sinh viên + 4 card điều hướng.
 * Mỗi card click → push Fragment tương ứng vào back stack.
 * Không dùng TabLayout / ViewPager2.
 */
public class AcademicResultsFragment extends Fragment {

    public AcademicResultsFragment() {
        super(R.layout.fragment_academic_results);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_academic_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fix Status Bar overlap — đẩy nội dung xuống dưới status bar
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        CardView cardGrades      = view.findViewById(R.id.card_grades);
        CardView cardLeaderboard = view.findViewById(R.id.card_leaderboard);
        CardView cardScholarship = view.findViewById(R.id.card_scholarship);
        CardView cardWarnings    = view.findViewById(R.id.card_warnings);

        cardGrades.setOnClickListener(v      -> navigateTo(new GradesFragment()));
        cardLeaderboard.setOnClickListener(v -> navigateTo(new LeaderboardFragment()));
        cardScholarship.setOnClickListener(v -> navigateTo(new ScholarshipFragment()));
        cardWarnings.setOnClickListener(v    -> navigateTo(new WarningsFragment()));
    }

    /**
     * Đẩy fragment mới vào back stack của Activity.
     * Nhấn Back sẽ quay lại Dashboard này.
     */
    private void navigateTo(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
