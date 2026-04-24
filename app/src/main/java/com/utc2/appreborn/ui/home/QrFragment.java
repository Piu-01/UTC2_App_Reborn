package com.utc2.appreborn.ui.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.utc2.appreborn.R;
import com.utc2.appreborn.databinding.FragmentQrBinding;
import com.utc2.appreborn.utils.MockHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * QrFragment  — FIXED
 * ──────────────────────────────────────────────────────────────
 * Fixes in this version:
 *
 *  FIX 1 — Status bar overlap
 *    applyWindowInsets() adds top padding to the toolbar
 *    dynamically so it always sits below the status bar on
 *    every API level and screen size.
 *
 *  FIX 2 — Back button not working on emulator
 *    • Back button now calls requireActivity()
 *      .getOnBackPressedDispatcher().onBackPressed()
 *      which is the modern, always-reliable approach.
 *    • Also registers an OnBackPressedCallback so the
 *      hardware / gesture back also works identically
 *      on emulator and real device.
 *
 *  FIX 3 — Color inconsistency across devices
 *    Colors are driven by @color/qr_btn_primary defined in
 *    colors.xml — no longer rely on ?attr/colorPrimary which
 *    differs between Material2 / Material3 and OEM themes.
 *
 * Package: com.utc2.appreborn.ui.home
 */
public class QrFragment extends Fragment {

    // ── Tag for FragmentManager / back-stack ──────────────────
    public static final String TAG = "tag_qr";

    // ── Argument keys ─────────────────────────────────────────
    private static final String ARG_FULL_NAME    = "arg_full_name";
    private static final String ARG_STUDENT_CODE = "arg_student_code";

    // ── QR bitmap size (px) ───────────────────────────────────
    private static final int QR_PX = 700;

    // ── View Binding ──────────────────────────────────────────
    private FragmentQrBinding binding;

    // ── Data ──────────────────────────────────────────────────
    private String studentName;
    private String studentCode;

    // ═══════════════════════════════════════════════════════════
    //  Factory
    // ═══════════════════════════════════════════════════════════

    public static QrFragment newInstance(String fullName, String studentCode) {
        QrFragment f   = new QrFragment();
        Bundle     args = new Bundle();
        args.putString(ARG_FULL_NAME,    fullName    != null ? fullName    : "");
        args.putString(ARG_STUDENT_CODE, studentCode != null ? studentCode : "");
        f.setArguments(args);
        return f;
    }

    // ═══════════════════════════════════════════════════════════
    //  Lifecycle
    // ═══════════════════════════════════════════════════════════

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        studentName = (args != null)
                ? args.getString(ARG_FULL_NAME,    MockHelper.getMockFullName())
                : MockHelper.getMockFullName();
        studentCode = (args != null)
                ? args.getString(ARG_STUDENT_CODE, MockHelper.getMockStudentCode())
                : MockHelper.getMockStudentCode();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FIX 1: push toolbar below status bar on every device
        applyWindowInsets();

        bindHeader();
        renderQrCode();
        setupClickListeners();

        // FIX 2: register hardware/gesture back so emulator behaves
        // the same as a real device
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ═══════════════════════════════════════════════════════════
    //  FIX 1 — Window insets (status-bar overlap)
    // ═══════════════════════════════════════════════════════════

    /**
     * Applies the system window top inset as extra top-padding on
     * the toolbar container so the title is never hidden behind
     * the status bar.
     *
     * Works on API 21+ including edge-to-edge mode (API 30+).
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.toolbarContainer,
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());
                    // Add status bar height as top padding
                    v.setPadding(
                            v.getPaddingLeft(),
                            systemBars.top,            // ← status bar height
                            v.getPaddingRight(),
                            v.getPaddingBottom()
                    );
                    // Adjust the view's height to accommodate the extra padding
                    ViewGroup.LayoutParams lp = v.getLayoutParams();
                    lp.height = getResources().getDimensionPixelSize(
                            R.dimen.qr_toolbar_height) + systemBars.top;
                    v.setLayoutParams(lp);
                    return WindowInsetsCompat.CONSUMED;
                }
        );
    }

    // ═══════════════════════════════════════════════════════════
    //  UI Setup
    // ═══════════════════════════════════════════════════════════

    private void bindHeader() {
        binding.tvQrStudentName.setText(studentName);
        binding.tvQrStudentCode.setText(studentCode);
    }

    private void renderQrCode() {
        Bitmap qr = generateQrBitmap(studentCode, QR_PX);
        if (qr != null) {
            binding.ivQrCode.setImageBitmap(qr);
        } else {
            Toast.makeText(requireContext(),
                    "Không thể tạo mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        // FIX 2: use dispatcher — works on emulator AND real device
        binding.btnBack.setOnClickListener(v -> navigateBack());

        binding.btnCopyCode.setOnClickListener(v -> copyCodeToClipboard());

        binding.btnSaveQr.setOnClickListener(v -> {
            Bitmap bmp = generateQrBitmap(studentCode, QR_PX);
            if (bmp != null) saveQrToGallery(bmp);
        });

        binding.btnShareQr.setOnClickListener(v -> {
            Bitmap bmp = generateQrBitmap(studentCode, QR_PX);
            if (bmp != null) shareQrBitmap(bmp);
        });
    }

    // ═══════════════════════════════════════════════════════════
    //  FIX 2 — Reliable back navigation
    // ═══════════════════════════════════════════════════════════

    /**
     * Single back-navigation method used by both the toolbar
     * button and the OnBackPressedCallback.
     *
     * Uses getOnBackPressedDispatcher().onBackPressed() which
     * respects the back-stack correctly on both emulator and
     * real devices across all API levels.
     */
    private void navigateBack() {
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    // ═══════════════════════════════════════════════════════════
    //  QR generation  (ZXing core)
    // ═══════════════════════════════════════════════════════════

    @Nullable
    private Bitmap generateQrBitmap(String content, int sizePx) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx);
            return bitMatrixToBitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    private Bitmap bitMatrixToBitmap(@NonNull BitMatrix matrix) {
        int    width  = matrix.getWidth();
        int    height = matrix.getHeight();
        int[]  pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = matrix.get(x, y)
                        ? 0xFF000000  // black module
                        : 0xFFFFFFFF; // white background
            }
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp;
    }

    // ═══════════════════════════════════════════════════════════
    //  Actions
    // ═══════════════════════════════════════════════════════════

    private void copyCodeToClipboard() {
        ClipboardManager cm = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("MSSV", studentCode));
            Toast.makeText(requireContext(),
                    "Đã sao chép: " + studentCode, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQrToGallery(@NonNull Bitmap bitmap) {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_" + studentCode + ".png");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cv.put(MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/UTC2");
        }

        Uri uri = requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        if (uri == null) {
            Toast.makeText(requireContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show();
            return;
        }
        try (OutputStream out =
                     requireContext().getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(requireContext(),
                    "Đã lưu ảnh QR vào thư viện", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQrBitmap(@NonNull Bitmap bitmap) {
        try {
            File cacheDir = new File(requireContext().getCacheDir(), "images");
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdirs();
            File shareFile = new File(cacheDir, "qr_share.png");
            try (FileOutputStream fos = new FileOutputStream(shareFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

            Uri contentUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    shareFile
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.putExtra(Intent.EXTRA_TEXT,
                    "Mã QR sinh viên: " + studentCode);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Chia sẻ mã QR"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi khi chia sẻ", Toast.LENGTH_SHORT).show();
        }
    }
}