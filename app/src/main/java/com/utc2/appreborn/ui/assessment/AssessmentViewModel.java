// PATH: app/src/main/java/com/utc2/appreborn/ui/assessment/AssessmentViewModel.java

package com.utc2.appreborn.ui.assessment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.utc2.appreborn.data.local.AppDatabase;
import com.utc2.appreborn.data.local.dao.AdvisorDao;
import com.utc2.appreborn.data.local.dao.UserDao;
import com.utc2.appreborn.data.local.entity.AdvisorEntity;
import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.repository.AssessmentRepository;
import com.utc2.appreborn.model.AssessmentCriteria;
import com.utc2.appreborn.model.AssessmentPeriod;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AssessmentViewModel extends AndroidViewModel {

    // ─── Deps ─────────────────────────────────────────────────────────────────
    private final AssessmentRepository repository;
    private final UserDao              userDao;
    private final AdvisorDao           advisorDao;
    private final ExecutorService      executor = Executors.newSingleThreadExecutor();

    // ─── State ────────────────────────────────────────────────────────────────
    /** true = đang xem tab RLSV, false = CVHT */
    private final MutableLiveData<Boolean> isStudentTab = new MutableLiveData<>(true);

    /** Đợt đánh giá đang được chọn */
    private final MutableLiveData<AssessmentPeriod> selectedPeriod = new MutableLiveData<>();

    /** Tổng điểm tính real-time */
    private final MutableLiveData<Float> totalScore = new MutableLiveData<>(0f);

    /** Xếp loại dựa trên tổng điểm */
    private final MutableLiveData<String> classification = new MutableLiveData<>("");

    /** Danh sách tiêu chí theo tab đang chọn */
    private final MediatorLiveData<List<AssessmentCriteria>> criteria = new MediatorLiveData<>();
    private LiveData<List<AssessmentCriteria>> currentCriteriaSource;

    /** Danh sách học kỳ */
    private final LiveData<List<AssessmentPeriod>> periods;

    /** Thông tin sinh viên (mock userId = 1 để test) */
    private final MutableLiveData<String> studentCode     = new MutableLiveData<>("");
    private final MutableLiveData<String> advisorName     = new MutableLiveData<>("");

    // ─── Constructor ──────────────────────────────────────────────────────────

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        repository  = AssessmentRepository.getInstance();
        userDao     = AppDatabase.getInstance(application).userDao();
        advisorDao  = AppDatabase.getInstance(application).advisorDao();
        periods     = repository.getAssessmentPeriods();

        // Load student info từ local DB (dùng userId = 1 cho mock)
        loadStudentInfo(1L);

        // Lần đầu load criteria theo tab mặc định (RLSV)
        switchTab(true);
    }

    // ─── Public API ───────────────────────────────────────────────────────────

    public LiveData<List<AssessmentCriteria>> getCriteria()       { return criteria; }
    public LiveData<List<AssessmentPeriod>>   getPeriods()        { return periods; }
    public LiveData<Float>                    getTotalScore()     { return totalScore; }
    public LiveData<String>                   getClassification() { return classification; }
    public LiveData<Boolean>                  getIsStudentTab()   { return isStudentTab; }
    public LiveData<String>                   getStudentCode()    { return studentCode; }
    public LiveData<String>                   getAdvisorName()    { return advisorName; }

    /** Gọi khi người dùng bấm tab RLSV hoặc CVHT */
    public void switchTab(boolean toStudentTab) {
        isStudentTab.setValue(toStudentTab);

        // Xóa nguồn cũ nếu có
        if (currentCriteriaSource != null) {
            criteria.removeSource(currentCriteriaSource);
        }

        currentCriteriaSource = repository.getAssessmentCriteria(toStudentTab);
        criteria.addSource(currentCriteriaSource, list -> {
            criteria.setValue(list);
            recalculate(list);
        });
    }

    /** Gọi từ Adapter khi người dùng thay đổi điểm của một tiêu chí */
    public void onScoreChanged(List<AssessmentCriteria> currentList) {
        recalculate(currentList);
    }

    /** Cập nhật URI minh chứng cho tiêu chí có id tương ứng */
    public void updateEvidenceUri(int criteriaId, String uri) {
        List<AssessmentCriteria> list = criteria.getValue();
        if (list == null) return;
        for (AssessmentCriteria c : list) {
            if (c.getId() == criteriaId) {
                c.setEvidenceUri(uri);
                break;
            }
        }
        criteria.setValue(list); // Trigger observer để refresh item
    }

    public void setSelectedPeriod(AssessmentPeriod period) {
        selectedPeriod.setValue(period);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    /**
     * Tính tổng điểm và xếp loại real-time.
     * Chỉ cộng TYPE_CRITERIA và TYPE_DEDUCTION, bỏ qua SECTION_HEADER.
     */
    private void recalculate(List<AssessmentCriteria> list) {
        if (list == null) return;
        float sum = 0f;
        for (AssessmentCriteria c : list) {
            if (c.getViewType() == AssessmentCriteria.TYPE_CRITERIA
                    || c.getViewType() == AssessmentCriteria.TYPE_DEDUCTION) {
                sum += c.getCurrentScore();
            }
        }
        totalScore.setValue(sum);
        classification.setValue(classify(sum, Boolean.TRUE.equals(isStudentTab.getValue())));
    }

    /**
     * Xếp loại theo thang điểm.
     * RLSV: 90–100 Xuất sắc | 80–<90 Tốt | 65–<80 Khá | 50–<65 Trung bình |
     *        35–<50 Yếu | <35 Kém
     * CVHT: tổng 12 tiêu chí × 5 = 60 điểm tối đa. Tỷ lệ tương đương.
     */
    private String classify(float score, boolean isRlsv) {
        if (isRlsv) {
            if (score >= 90) return "Xuất sắc";
            if (score >= 80) return "Tốt";
            if (score >= 65) return "Khá";
            if (score >= 50) return "Trung bình";
            if (score >= 35) return "Yếu";
            return "Kém";
        } else {
            // CVHT: 60 điểm tối đa → quy đổi sang 100
            float pct = (score / 60f) * 100f;
            if (pct >= 90) return "Xuất sắc";
            if (pct >= 80) return "Tốt";
            if (pct >= 65) return "Khá";
            if (pct >= 50) return "Trung bình";
            return "Yếu";
        }
    }

    /** Load thông tin sinh viên và cố vấn từ Room DB */
    private void loadStudentInfo(long userId) {
        executor.execute(() -> {
            StudentProfileEntity profile = userDao.getStudentProfileByUserId(userId);
            if (profile != null) {
                studentCode.postValue(profile.studentCode != null ? profile.studentCode : "N/A");

                if (profile.advisorId != null) {
                    AdvisorEntity advisor = advisorDao.getAdvisorById(profile.advisorId);
                    if (advisor != null) {
                        advisorName.postValue(advisor.fullName);
                        return;
                    }
                }
            } else {
                // === Mock data khi chưa có DB thực ===
                studentCode.postValue("2251060xxx");
            }
            advisorName.postValue("ThS. Nguyễn Văn A"); // Mock
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}