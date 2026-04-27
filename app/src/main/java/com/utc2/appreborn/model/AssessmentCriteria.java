// PATH: app/src/main/java/com/utc2/appreborn/model/AssessmentCriteria.java

package com.utc2.appreborn.model;

import java.util.List;

/**
 * Đại diện cho một tiêu chí đánh giá trong form RLSV hoặc CVHT.
 * viewType xác định cách RecyclerView render item này.
 */
public class AssessmentCriteria {

    // ─── View Types ───────────────────────────────────────────────────────────
    public static final int TYPE_SECTION_HEADER = 0; // Tiêu đề nhóm (ví dụ: "1. Ý thức học tập")
    public static final int TYPE_CRITERIA       = 1; // Tiêu chí thông thường
    public static final int TYPE_DEDUCTION      = 2; // Tiêu chí trừ điểm (điểm âm)
    public static final int TYPE_SUMMARY        = 3; // Dòng tổng kết cuối

    // ─── Fields ───────────────────────────────────────────────────────────────
    private final int    viewType;
    private final int    id;
    private final String title;
    private final float  maxScore;         // Điểm tối đa (âm nếu là trừ điểm)
    private       float  currentScore;     // Điểm hiện tại người dùng chọn
    private final boolean requiresEvidence; // Cần nộp minh chứng không?
    private       String evidenceUri;       // URI file minh chứng đã chọn (nullable)
    private final List<Float> scoreOptions; // Các mức điểm gợi ý

    // ─── Constructor ──────────────────────────────────────────────────────────

    public AssessmentCriteria(int viewType, int id, String title,
                              float maxScore, boolean requiresEvidence,
                              List<Float> scoreOptions) {
        this.viewType         = viewType;
        this.id               = id;
        this.title            = title;
        this.maxScore         = maxScore;
        this.currentScore     = maxScore; // Mặc định chọn điểm tối đa
        this.requiresEvidence = requiresEvidence;
        this.evidenceUri      = null;
        this.scoreOptions     = scoreOptions;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public int    getViewType()         { return viewType; }
    public int    getId()               { return id; }
    public String getTitle()            { return title; }
    public float  getMaxScore()         { return maxScore; }
    public float  getCurrentScore()     { return currentScore; }
    public boolean isRequiresEvidence() { return requiresEvidence; }
    public String getEvidenceUri()      { return evidenceUri; }
    public List<Float> getScoreOptions(){ return scoreOptions; }

    public void setCurrentScore(float score)  { this.currentScore = score; }
    public void setEvidenceUri(String uri)    { this.evidenceUri = uri; }

    public boolean hasEvidence() {
        return evidenceUri != null && !evidenceUri.isEmpty();
    }
}