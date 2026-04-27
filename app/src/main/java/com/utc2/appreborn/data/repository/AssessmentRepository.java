// PATH: app/src/main/java/com/utc2/appreborn/data/repository/AssessmentRepository.java

package com.utc2.appreborn.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.utc2.appreborn.model.AssessmentCriteria;
import com.utc2.appreborn.model.AssessmentPeriod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repository cho tính năng Đánh giá.
 *
 * Hiện tại dùng Mock Data (hardcode) để test UI.
 * === TODO: Thay thế các hàm mock bằng Retrofit call tới MySQL API ===
 */
public class AssessmentRepository {

    private static volatile AssessmentRepository instance;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private AssessmentRepository() {}

    public static AssessmentRepository getInstance() {
        if (instance == null) {
            synchronized (AssessmentRepository.class) {
                if (instance == null) instance = new AssessmentRepository();
            }
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Trả về danh sách tiêu chí đánh giá.
     *
     * @param isStudentAssessment true = RLSV (Đánh giá rèn luyện sinh viên)
     *                            false = CVHT (Đánh giá cố vấn học tập)
     *
     * === TODO: Thay block mock bên dưới bằng:
     *     ApiService api = RetrofitClient.getInstance().getApiService();
     *     api.getAssessmentCriteria(semesterId, isStudentAssessment)
     *        .enqueue(new Callback<List<AssessmentCriteriaDto>>() { ... });
     * ===
     */
    public LiveData<List<AssessmentCriteria>> getAssessmentCriteria(boolean isStudentAssessment) {
        MutableLiveData<List<AssessmentCriteria>> liveData = new MutableLiveData<>();

        // Giả lập network delay nhỏ (50 ms) để mimick async call
        mainHandler.postDelayed(() -> {
            List<AssessmentCriteria> data = isStudentAssessment
                    ? buildRlsvCriteria()
                    : buildCvhtCriteria();
            liveData.setValue(data);
        }, 50);

        return liveData;
    }

    /**
     * Trả về danh sách đợt/học kỳ đánh giá.
     *
     * === TODO: Gọi API: GET /api/assessment/periods ===
     */
    public LiveData<List<AssessmentPeriod>> getAssessmentPeriods() {
        MutableLiveData<List<AssessmentPeriod>> liveData = new MutableLiveData<>();
        mainHandler.postDelayed(() -> liveData.setValue(buildMockPeriods()), 50);
        return liveData;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MOCK DATA – RLSV
    // Dựa trên Quy chế Đánh giá Rèn luyện Sinh viên UTC2
    // ─────────────────────────────────────────────────────────────────────────

    private List<AssessmentCriteria> buildRlsvCriteria() {
        List<AssessmentCriteria> list = new ArrayList<>();
        int id = 0;

        // ── Mục 1: Ý thức học tập (tối đa 20 điểm) ──────────────────────────
        list.add(header(id++, "1. Đánh giá về ý thức tham gia học tập (Tối đa: 20 điểm)"));

        list.add(criteria(id++,
                "a. Có ý thức học tập tốt, có tinh thần vượt khó vươn lên\n" +
                        "(Tất cả học phần điểm D trở lên; mỗi điểm F hoặc F+ trừ 1 điểm)",
                5f, false, opts(0f, 1f, 2f, 3f, 4f, 5f)));

        list.add(criteria(id++,
                "b. Đạt được 1 trong các điều kiện: Tham gia NCKH, CLB học thuật,\n" +
                        "Đội tuyển thi học thuật, Chứng chỉ nâng cao\n(Nộp minh chứng – xác nhận CVHT & TT lớp)",
                5f, true, opts(0f, 5f)));

        list.add(criteria(id++,
                "c. Thực hiện quy chế thi và kiểm tra\n" +
                        "(Chấp hành tốt: 5đ | Bị kỷ luật thi: 0đ)",
                5f, false, opts(0f, 5f)));

        list.add(criteria(id++,
                "d. Cộng điểm theo TBCHT\n" +
                        "(1→<1.5: 1đ | 1.5→<2.0: 2đ | 2.0→<2.5: 3đ | 2.5→<3.2: 4đ | ≥3.2: 5đ)",
                5f, false, opts(0f, 1f, 2f, 3f, 4f, 5f)));

        // ── Mục 2: Ý thức chấp hành nội quy (tối đa 25 điểm) ────────────────
        list.add(header(id++, "2. Đánh giá ý thức chấp hành nội quy, quy chế (Tối đa: 25 điểm)"));

        list.add(criteria(id++, "a. Tham gia bảo hiểm y tế", 5f, false, opts(0f, 5f)));
        list.add(criteria(id++, "b. Đóng học phí đúng quy định", 5f, false, opts(0f, 5f)));
        list.add(criteria(id++, "c. Đánh giá Giảng viên", 2.5f, false, opts(0f, 2.5f)));
        list.add(criteria(id++, "d. Đánh giá Cố vấn học tập", 2.5f, false, opts(0f, 2.5f)));

        list.add(criteria(id++,
                "e. Tham gia tuần sinh hoạt công dân\n(Tham gia: 2.5đ | Không: 0đ)",
                2.5f, false, opts(0f, 2.5f)));

        list.add(criteria(id++,
                "f. Bài thu hoạch tuần sinh hoạt công dân\n(Thực hiện tốt: 2.5đ | Không: 0đ)",
                2.5f, false, opts(0f, 2.5f)));

        list.add(criteria(id++,
                "g. Tham gia họp lớp, chi đoàn…\n(Đầy đủ: 5đ | Vắng 1: 2đ | Vắng ≥2: 0đ)",
                5f, false, opts(0f, 2f, 5f)));

        // ── Mục 3: Hoạt động chính trị – XH (tối đa 20 điểm) ────────────────
        list.add(header(id++, "3. Hoạt động chính trị - xã hội, văn hóa, thể thao (Tối đa: 20 điểm)"));

        list.add(criteria(id++,
                "a. Tham gia hội thảo, tọa đàm, cuộc thi\n(Có: 6đ | Không: 0đ)",
                6f, false, opts(0f, 6f)));

        list.add(criteria(id++,
                "b. Hoạt động tình nguyện, công ích\n(Có: 6đ | Không: 0đ)",
                6f, false, opts(0f, 6f)));

        list.add(criteria(id++,
                "c. Văn hóa, văn nghệ, thể thao\n(Có: 6đ | Không: 0đ)",
                6f, false, opts(0f, 6f)));

        list.add(criteria(id++,
                "d. Tuyên truyền phòng chống tệ nạn\n(Có: 2đ | Không: 0đ)",
                2f, false, opts(0f, 2f)));

        // ── Mục 4: Ý thức công dân (tối đa 25 điểm) ─────────────────────────
        list.add(header(id++, "4. Ý thức công dân và quan hệ cộng đồng (Tối đa: 25 điểm)"));

        list.add(criteria(id++,
                "a. Chấp hành pháp luật và quy định\n(Tốt: 6đ | Không tốt: 0đ)",
                6f, false, opts(0f, 6f)));

        list.add(criteria(id++,
                "b. Có thành tích xã hội\n(Cần minh chứng)",
                4f, true, opts(0f, 4f)));

        list.add(criteria(id++,
                "c. Giúp đỡ người khác, bạn bè\n(Có: 5đ | Không: 0đ)",
                5f, false, opts(0f, 5f)));

        list.add(criteria(id++,
                "d. Cập nhật thông tin cá nhân đầy đủ\n(Có: 5đ | Không: 0đ)",
                5f, false, opts(0f, 5f)));

        list.add(criteria(id++,
                "e. Giữ gìn trật tự, môi trường, hình ảnh trường\n(Có: 5đ | Không: 0đ)",
                5f, false, opts(0f, 5f)));

        // ── Mục 5: Cán bộ lớp, đoàn thể (tối đa 10 điểm) ───────────────────
        list.add(header(id++, "5. Công tác cán bộ lớp, đoàn thể (Tối đa: 10 điểm)"));

        list.add(criteria(id++,
                "a. Tham gia tích cực Đoàn/Hội/CLB, đạt giải học thuật, được khen thưởng…\n(Cần minh chứng)",
                5f, true, opts(0f, 5f)));

        list.add(criteria(id++,
                "b. Tham gia tổ chức hoạt động\n(Có: 5đ | Không: 0đ)",
                5f, false, opts(0f, 5f)));

        // ── Mục 6: Bị trừ điểm ───────────────────────────────────────────────
        list.add(header(id++, "6. SINH VIÊN BỊ TRỪ ĐIỂM (Cấp lớp, Khoa/BM trừ điểm)"));

        list.add(deduction(id++,
                "Không tự đánh giá rèn luyện trên hệ thống",
                -10f, opts(0f, -10f)));

        list.add(deduction(id++,
                "Có thông báo vi phạm pháp luật",
                -10f, opts(0f, -10f)));

        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MOCK DATA – CVHT
    // Dựa trên Phiếu Đánh giá Cố vấn Học tập UTC2
    // Thang điểm: Rất tốt 5 | Tốt 4 | Khá 3 | Trung bình 2 | Yếu 1
    // ─────────────────────────────────────────────────────────────────────────

    private List<AssessmentCriteria> buildCvhtCriteria() {
        List<AssessmentCriteria> list = new ArrayList<>();
        int id = 0;

        list.add(header(id++,
                "1. Phần đánh giá theo tiêu chí\n" +
                        "(Rất tốt: 5đ | Tốt: 4đ | Khá: 3đ | Trung bình: 2đ | Yếu: 1đ)"));

        list.add(cvhtCriteria(id++,
                "1. CVHT đã chuẩn bị tốt nội dung và chủ trì các buổi họp lớp theo kế hoạch của Khoa và Nhà trường."));

        list.add(cvhtCriteria(id++,
                "2. CVHT đã công khai về thời gian biểu, cách thức trao đổi, liên lạc với sinh viên."));

        list.add(cvhtCriteria(id++,
                "3. CVHT đã tạo điều kiện thuận lợi để sinh viên được tư vấn, trao đổi qua nhiều hình thức (ít nhất 2 tuần 1 lần)."));

        list.add(cvhtCriteria(id++,
                "4. CVHT nắm chắc quy chế học tập và rèn luyện để tư vấn cho sinh viên."));

        list.add(cvhtCriteria(id++,
                "5. CVHT đã quan tâm theo dõi kết quả học tập để tư vấn về đăng ký học, kế hoạch và phương pháp học tập."));

        list.add(cvhtCriteria(id++,
                "6. CVHT đã tư vấn cho sinh viên tham gia các hoạt động hỗ trợ học tập: NCKH, tiếp cận doanh nghiệp, thực tế…"));

        list.add(cvhtCriteria(id++,
                "7. CVHT đã khuyến khích, động viên sinh viên tham gia các hoạt động xã hội, văn – thể – mỹ lành mạnh."));

        list.add(cvhtCriteria(id++,
                "8. CVHT đã kịp thời thông báo, đôn đốc, nhắc nhở sinh viên thực hiện các kế hoạch của Khoa và Nhà trường."));

        list.add(cvhtCriteria(id++,
                "9. CVHT phổ biến, hướng dẫn và chủ trì họp đánh giá RLSV, đảm bảo đúng quy trình và tiến độ."));

        list.add(cvhtCriteria(id++,
                "10. CVHT đã thường xuyên cập nhật thông tin sinh viên (địa chỉ, điện thoại, email)."));

        list.add(cvhtCriteria(id++,
                "11. CVHT đã kịp thời giải quyết các vấn đề phát sinh theo yêu cầu của sinh viên hoặc lớp."));

        list.add(cvhtCriteria(id++,
                "12. CVHT có thái độ ứng xử thân thiện, đúng mực với sinh viên."));

        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MOCK DATA – Học kỳ
    // ─────────────────────────────────────────────────────────────────────────

    private List<AssessmentPeriod> buildMockPeriods() {
        return Arrays.asList(
                new AssessmentPeriod("HK1_2024_2025", "Học kỳ 1 – 2024-2025"),
                new AssessmentPeriod("HK2_2024_2025", "Học kỳ 2 – 2024-2025"),
                new AssessmentPeriod("HK1_2025_2026", "Học kỳ 1 – 2025-2026"),
                new AssessmentPeriod("HK2_2025_2026", "Học kỳ 2 – 2025-2026")
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER FACTORY METHODS
    // ─────────────────────────────────────────────────────────────────────────

    private AssessmentCriteria header(int id, String title) {
        return new AssessmentCriteria(
                AssessmentCriteria.TYPE_SECTION_HEADER, id, title, 0f, false, null);
    }

    private AssessmentCriteria criteria(int id, String title, float max,
                                        boolean needsEvidence, List<Float> opts) {
        return new AssessmentCriteria(
                AssessmentCriteria.TYPE_CRITERIA, id, title, max, needsEvidence, opts);
    }

    private AssessmentCriteria deduction(int id, String title,
                                         float penalty, List<Float> opts) {
        return new AssessmentCriteria(
                AssessmentCriteria.TYPE_DEDUCTION, id, title, penalty, false, opts);
    }

    /** Tiêu chí CVHT: thang 1–5 */
    private AssessmentCriteria cvhtCriteria(int id, String title) {
        return new AssessmentCriteria(
                AssessmentCriteria.TYPE_CRITERIA, id, title, 5f, false,
                opts(1f, 2f, 3f, 4f, 5f));
    }

    private List<Float> opts(Float... values) {
        return Arrays.asList(values);
    }
}