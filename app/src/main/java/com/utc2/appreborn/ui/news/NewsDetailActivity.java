package com.utc2.appreborn.ui.news;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.utc2.appreborn.databinding.ActivityNewsDetailBinding;

/**
 * NewsDetailActivity
 * ──────────────────────────────────────────────────────────────
 * Displays the full HTML content of a news post in a WebView.
 *
 * Receives data via Intent extras:
 *   EXTRA_TITLE   — post title shown in the toolbar
 *   EXTRA_DATE    — formatted date string shown below the title
 *   EXTRA_CONTENT — raw HTML body from the API  (PostDto.content)
 *
 * WebView config:
 *   • JavaScript enabled (some CMS posts use inline scripts)
 *   • CSS wrapper: max-width 100% on all images to prevent overflow
 *   • Links open INSIDE the WebView (no external browser launch)
 *
 * Add to AndroidManifest.xml inside <application>:
 *   <activity
 *       android:name=".ui.news.NewsDetailActivity"
 *       android:parentActivityName=".ui.main.MainActivity"
 *       android:exported="false" />
 *
 * Package: com.utc2.appreborn.ui.news
 */
public class NewsDetailActivity extends AppCompatActivity {

    // ── Intent extra keys (public so HomeFragment can reference them) ──
    public static final String EXTRA_TITLE   = "extra_title";
    public static final String EXTRA_DATE    = "extra_date";
    public static final String EXTRA_CONTENT = "extra_content";

    private ActivityNewsDetailBinding binding;

    // ═══════════════════════════════════════════════════════════
    //  Lifecycle
    // ═══════════════════════════════════════════════════════════

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Read Intent extras
        String title   = getIntent().getStringExtra(EXTRA_TITLE);
        String date    = getIntent().getStringExtra(EXTRA_DATE);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);

        bindHeader(title, date);
        setupWebView();
        loadContent(content);

        // Back button in toolbar
        binding.btnBack.setOnClickListener(v -> finish());
    }

    // ═══════════════════════════════════════════════════════════
    //  UI
    // ═══════════════════════════════════════════════════════════

    private void bindHeader(String title, String date) {
        if (title != null) binding.tvDetailTitle.setText(title);
        if (date  != null) binding.tvDetailDate.setText(date);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = binding.webViewContent.getSettings();

        // Enable JS — some CMS-generated HTML needs it
        settings.setJavaScriptEnabled(true);

        // Responsive text & layout
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);

        // Keep all links inside the WebView (don't open Chrome)
        binding.webViewContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                // Return false → WebView handles the URL itself
                return false;
            }
        });
    }

    /**
     * Wraps the raw HTML from the API in a minimal CSS shell that:
     *   • Sets a readable font and line height
     *   • Clamps image widths so they never overflow the screen
     *   • Adds comfortable horizontal padding
     */
    private void loadContent(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            htmlContent = "<p>Không có nội dung.</p>";
        }

        String fullHtml =
                "<!DOCTYPE html><html><head>"
                        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                        + "<style>"
                        + "  body {"
                        + "    font-family: -apple-system, 'Roboto', sans-serif;"
                        + "    font-size: 16px;"
                        + "    line-height: 1.7;"
                        + "    color: #1A1A1A;"
                        + "    padding: 0 16px 32px 16px;"
                        + "    margin: 0;"
                        + "    word-break: break-word;"
                        + "  }"
                        // Prevent any image, video, or table from overflowing
                        + "  img, video, table {"
                        + "    max-width: 100% !important;"
                        + "    height: auto !important;"
                        + "  }"
                        + "  a { color: #6B47DC; }"
                        + "  p  { margin: 0 0 12px 0; }"
                        + "</style>"
                        + "</head><body>"
                        + htmlContent
                        + "</body></html>";

        // loadDataWithBaseURL lets relative links (images, CSS) resolve correctly
        binding.webViewContent.loadDataWithBaseURL(
                "https://utc2.edu.vn/",  // base URL for relative resources
                fullHtml,
                "text/html",
                "UTF-8",
                null
        );
    }

    @Override
    protected void onDestroy() {
        // Prevent WebView memory leak
        if (binding != null) {
            binding.webViewContent.stopLoading();
            binding.webViewContent.destroy();
        }
        super.onDestroy();
    }
}