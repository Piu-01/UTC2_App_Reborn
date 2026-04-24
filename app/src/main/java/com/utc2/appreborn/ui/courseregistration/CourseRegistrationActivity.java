package com.utc2.appreborn.ui.courseregistration;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.utc2.appreborn.R;
import com.utc2.appreborn.ui.courseregistration.adapter.CourseAdapter;
import com.utc2.appreborn.ui.courseregistration.exception.CourseException;
import com.utc2.appreborn.ui.courseregistration.model.Course;
import com.utc2.appreborn.ui.courseregistration.model.CourseRegistration;
import com.utc2.appreborn.ui.courseregistration.model.CourseRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Đăng ký học phần – 1 Activity chứa 2 tab.
 *
 * Tab Đăng ký: tìm kiếm, lọc, danh sách môn, khung đã chọn
 * Tab Kết quả: hiển thị các môn đã đăng ký
 *
 * Cấu trúc giống DormitoryActivity (KTX).
 */
public class CourseRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "CourseRegActivity";

    // ── Tab & Pages ──────────────────────────────────────────────────────────
    private TextView     tabDangKy, tabKetQua;
    private LinearLayout pageDangKy, pageKetQua;

    // ── Views trang Đăng ký ──────────────────────────────────────────────────
    private TextView     btnBack, btnHocKy, btnKhoaHoc, btnNganh, btnConfirm;
    private EditText     searchBox;
    private RecyclerView rvCourses;
    private LinearLayout layoutSelected, layoutSelectedItems;
    private TextView     txtTongTinChi;

    // ── Views trang Kết quả ──────────────────────────────────────────────────
    private TextView tvKetQuaContent;

    // ── Repository & Adapter ─────────────────────────────────────────────────
    private CourseRepository courseRepo;
    private CourseAdapter    courseAdapter;

    // ── Filter state – rỗng = "Tất cả" ───────────────────────────────────────
    private String filterSemester = "";
    private String filterKhoaHoc  = "";
    private String filterMajor    = "";

    // ── Dữ liệu dropdown ──────────────────────────────────────────────────────
    private static final String[] SEMESTERS = {"Tất cả", "HK1", "HK2", "HK3"};
    private static final String[] KHOAHOC   = {
            "Tất cả", "K51", "K52", "K53", "K54", "K55",
            "K56", "K57", "K58", "K59", "K60",
            "K61", "K62", "K63", "K64", "K65",
            "K66", "K67", "K68", "K69", "K70"
    };
    private static final String[] NGANH = {
            "Tất cả", "CNTT", "KTPM", "HTTT", "MMT",
            "CK", "XD", "KT", "MT", "DTVT"
    };

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_registration);

        courseRepo = CourseRepository.getInstance();

        bindViews();
        setupTabs();
        setupBackButton();
        setupRecyclerView();
        setupFilterButtons();
        setupSearchBox();
        setupConfirmButton();

        showPage(true);
    }

    // ── Ánh xạ views ─────────────────────────────────────────────────────────
    private void bindViews() {
        tabDangKy           = findViewById(R.id.tabDangKy);
        tabKetQua           = findViewById(R.id.tabKetQua);
        pageDangKy          = findViewById(R.id.pageDangKy);
        pageKetQua          = findViewById(R.id.pageKetQua);

        btnBack             = findViewById(R.id.btnBack);
        btnHocKy            = findViewById(R.id.btnHocKy);
        btnKhoaHoc          = findViewById(R.id.btnKhoa);   // id giữ nguyên btnKhoa trong XML
        btnNganh            = findViewById(R.id.btnNganh);
        btnConfirm          = findViewById(R.id.btnConfirm);

        searchBox           = findViewById(R.id.searchBox);
        rvCourses           = findViewById(R.id.rvCourses);
        layoutSelected      = findViewById(R.id.layoutSelected);
        layoutSelectedItems = findViewById(R.id.layoutSelectedItems);
        txtTongTinChi       = findViewById(R.id.txtTongTinChi);

        tvKetQuaContent     = findViewById(R.id.tvKetQuaContent);

        // Label mặc định = Tất cả
        btnHocKy.setText("Học kỳ ▾");
        btnKhoaHoc.setText("Khóa học ▾");
        btnNganh.setText("Ngành ▾");
    }

    // ── Chuyển tab ────────────────────────────────────────────────────────────
    private void showPage(boolean showDangKy) {
        if (showDangKy) {
            pageDangKy.setVisibility(View.VISIBLE);
            pageKetQua.setVisibility(View.GONE);

            tabDangKy.setBackgroundResource(R.drawable.bg_tab_selected);
            tabDangKy.setTextColor(Color.WHITE);
            tabKetQua.setBackground(null);
            tabKetQua.setTextColor(Color.BLACK);
        } else {
            pageDangKy.setVisibility(View.GONE);
            pageKetQua.setVisibility(View.VISIBLE);

            tabKetQua.setBackgroundResource(R.drawable.bg_tab_selected);
            tabKetQua.setTextColor(Color.WHITE);
            tabDangKy.setBackground(null);
            tabDangKy.setTextColor(Color.BLACK);

            updateKetQua();
        }
    }

    private void setupTabs() {
        tabDangKy.setOnClickListener(v -> showPage(true));
        tabKetQua.setOnClickListener(v -> showPage(false));
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG ĐĂNG KÝ
    // ══════════════════════════════════════════════════════════════════════════

    private void setupRecyclerView() {
        // Hiển thị tất cả học phần ban đầu (không lọc)
        List<Course> courses = courseRepo.getAllCourses();
        courseAdapter = new CourseAdapter(courses, this::handleRegisterClick);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setNestedScrollingEnabled(false);
        rvCourses.setAdapter(courseAdapter);
    }

    /** [Chương 4] try-catch-finally xử lý đăng ký học phần. */
    private void handleRegisterClick(Course course) {
        try {
            courseRepo.registerCourse(course.getId());
            updateSelectedPanel();
            refreshAdapterRegistered();
            Toast.makeText(this,
                    "Đăng ký \"" + course.getName() + "\" thành công!",
                    Toast.LENGTH_SHORT).show();

        } catch (CourseException e) {
            Log.w(TAG, "CourseException: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e(TAG, "Unexpected error khi đăng ký", e);
            Toast.makeText(this, "Lỗi không xác định, vui lòng thử lại.", Toast.LENGTH_SHORT).show();

        } finally {
            Log.d(TAG, "handleRegisterClick() done – courseId: " + course.getId());
        }
    }

    /** Cập nhật khung "Đã chọn" bên dưới danh sách. */
    private void updateSelectedPanel() {
        Map<String, CourseRegistration> registrations = courseRepo.getRegistrations();

        if (registrations.isEmpty()) {
            layoutSelected.setVisibility(View.GONE);
            return;
        }

        layoutSelected.setVisibility(View.VISIBLE);
        layoutSelectedItems.removeAllViews();

        for (CourseRegistration reg : registrations.values()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 6, 0, 6);

            TextView tvName = new TextView(this);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tvName.setText("• " + reg.getCourse().getName());
            tvName.setTextColor(Color.parseColor("#333333"));
            tvName.setTextSize(13);

            TextView tvCredits = new TextView(this);
            tvCredits.setText(String.valueOf(reg.getCourse().getCredits()));
            tvCredits.setTextColor(Color.parseColor("#555555"));
            tvCredits.setTextSize(13);

            row.addView(tvName);
            row.addView(tvCredits);
            layoutSelectedItems.addView(row);
        }

        txtTongTinChi.setText("Tổng số tín chỉ: " + courseRepo.getTotalRegisteredCredits());
    }

    /** Cập nhật trạng thái "Đã đăng ký" trên các item trong RecyclerView. */
    private void refreshAdapterRegistered() {
        List<String> regIds = new ArrayList<>(courseRepo.getRegistrations().keySet());
        courseAdapter.setRegisteredIds(regIds);
    }

    private void setupConfirmButton() {
        btnConfirm.setOnClickListener(v -> {
            if (courseRepo.getRegistrations().isEmpty()) {
                Toast.makeText(this, "Chưa có môn học nào được chọn!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this,
                    "Đã xác nhận đăng ký " + courseRepo.getTotalRegisteredCredits() + " tín chỉ!",
                    Toast.LENGTH_LONG).show();
        });
    }

    private void setupFilterButtons() {
        btnHocKy.setOnClickListener(v ->
                showScrollableDialog("Chọn học kỳ", SEMESTERS, choice -> {
                    filterSemester = "Tất cả".equals(choice) ? "" : choice;
                    btnHocKy.setText("Tất cả".equals(choice) ? "Học kỳ ▾" : "Học kỳ: " + choice + " ▾");
                    applyFilter();
                }));

        btnKhoaHoc.setOnClickListener(v ->
                showScrollableDialog("Chọn khóa học", KHOAHOC, choice -> {
                    filterKhoaHoc = "Tất cả".equals(choice) ? "" : choice;
                    btnKhoaHoc.setText("Tất cả".equals(choice) ? "Khóa học ▾" : choice + " ▾");
                    applyFilter();
                }));

        btnNganh.setOnClickListener(v ->
                showScrollableDialog("Chọn ngành", NGANH, choice -> {
                    filterMajor = "Tất cả".equals(choice) ? "" : choice;
                    btnNganh.setText("Tất cả".equals(choice) ? "Ngành ▾" : "Ngành: " + choice + " ▾");
                    applyFilter();
                }));
    }

    private void applyFilter() {
        try {
            List<Course> filtered = courseRepo.filterCourses(filterSemester, filterKhoaHoc, filterMajor);
            courseAdapter.updateData(filtered);
        } finally {
            Log.d(TAG, "applyFilter() done.");
        }
    }

    private void setupSearchBox() {
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String query = s.toString().trim().toLowerCase();
                    if (query.isEmpty()) {
                        courseAdapter.updateData(courseRepo.filterCourses(filterSemester, filterKhoaHoc, filterMajor));
                        return;
                    }
                    List<Course> base = courseRepo.filterCourses(filterSemester, filterKhoaHoc, filterMajor);
                    List<Course> filtered = new ArrayList<>();
                    for (Course c : base) {
                        if (c.getName().toLowerCase().contains(query)
                                || c.getCourseCode().toLowerCase().contains(query)) {
                            filtered.add(c);
                        }
                    }
                    courseAdapter.updateData(filtered);
                } catch (Exception e) {
                    Log.e(TAG, "Search error", e);
                }
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANG KẾT QUẢ
    // ══════════════════════════════════════════════════════════════════════════

    private void updateKetQua() {
        Map<String, CourseRegistration> regs = courseRepo.getRegistrations();
        if (regs.isEmpty()) {
            tvKetQuaContent.setText("Chưa có đăng ký nào được xác nhận.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (CourseRegistration reg : regs.values()) {
            sb.append("✓  ")
                    .append(reg.getCourse().getName())
                    .append("   –   ")
                    .append(reg.getCourse().getCredits())
                    .append(" tín chỉ\n");
        }
        sb.append("\nTổng: ").append(courseRepo.getTotalRegisteredCredits()).append(" tín chỉ");
        tvKetQuaContent.setText(sb.toString());
    }

    // ── Helper: Dialog danh sách có thanh cuộn ────────────────────────────────
    private interface MenuCallback { void onSelected(String choice); }

    private void showScrollableDialog(String title, String[] options, MenuCallback cb) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        // Root layout
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        int radius = (int) (20 * getResources().getDisplayMetrics().density);
        root.setPadding(0, 0, 0, 0);

        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvTitle.setPadding(48, 40, 48, 32);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        root.addView(tvTitle);

        // Divider
        View div = new View(this);
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        div.setLayoutParams(divParams);
        div.setBackgroundColor(Color.parseColor("#EEEEEE"));
        root.addView(div);

        // ScrollView chứa danh sách
        ScrollView scrollView = new ScrollView(this);
        int maxHeightPx = (int) (320 * getResources().getDisplayMetrics().density);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, maxHeightPx));

        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);

        for (String opt : options) {
            TextView item = new TextView(this);
            item.setText(opt);
            item.setTextSize(15);
            item.setTextColor(Color.parseColor("#222222"));
            item.setPadding(48, 36, 48, 36);
            item.setClickable(true);
            item.setFocusable(true);
            item.setBackground(getItemRippleBackground());

            item.setOnClickListener(v -> {
                cb.onSelected(opt);
                dialog.dismiss();
            });
            listLayout.addView(item);

            // separator
            View sep = new View(this);
            LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            sepLp.setMargins(48, 0, 48, 0);
            sep.setLayoutParams(sepLp);
            sep.setBackgroundColor(Color.parseColor("#F0F0F0"));
            listLayout.addView(sep);
        }

        scrollView.addView(listLayout);
        root.addView(scrollView);

        dialog.setContentView(root);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (300 * getResources().getDisplayMetrics().density),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.dialog_holo_light_frame);
        }
        dialog.show();
    }

    private android.graphics.drawable.Drawable getItemRippleBackground() {
        android.graphics.drawable.ColorDrawable bg =
                new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT);
        return bg;
    }
}
