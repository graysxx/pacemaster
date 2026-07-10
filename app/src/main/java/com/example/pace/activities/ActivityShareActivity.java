package com.example.pace.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.example.pace.R;
import com.example.pace.models.ActivityRecord;
import java.io.File;
import java.io.FileOutputStream;

public class ActivityShareActivity extends AppCompatActivity {

    private static final int THEME_COUNT = 5;

    private FrameLayout shareFrame;
    private View tabPaceTheme, tabDefault, paceStyleSelector, defaultControls;
    private LinearLayout dotsContainer;
    private ViewGroup bottomControls;
    private String jarak, waktu, elev, pace;
    private ActivityRecord activityData;

    private int currentThemeIndex = 0;
    private boolean isDefaultMode = true;
    private float lastX, lastY;
    private Uri selectedBgUri;

    private View selectedView;
    private View elementEditor;
    private TextView selectedElementName;
    private android.widget.SeekBar sbSize, sbRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Get data from intent
        activityData = (ActivityRecord) getIntent().getSerializableExtra("ACTIVITY_DATA");

        if (activityData != null) {
            jarak = String.valueOf(activityData.getDistance());
            waktu = activityData.getDuration();
            elev = activityData.getElevationGain() + " m";
            pace = activityData.getAvgPace();
        } else {
            jarak = getIntent().getStringExtra("jarak");
            waktu = getIntent().getStringExtra("waktu");
            elev = getIntent().getStringExtra("elev");
            pace = getIntent().getStringExtra("pace");
        }

        initViews();
        populateTabPreview();
        setupTabs();
        setupThemeNavigator();
        setupDefaultMode();
        setupSwipePreview();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveShare).setOnClickListener(v -> saveAndShare());
    }

    private void initViews() {
        shareFrame = findViewById(R.id.shareFrame);
        tabPaceTheme = findViewById(R.id.tabPaceTheme);
        tabDefault = findViewById(R.id.tabDefault);
        paceStyleSelector = findViewById(R.id.paceStyleSelector);
        defaultControls = findViewById(R.id.defaultControls);
        dotsContainer = findViewById(R.id.dotsContainer);
        bottomControls = findViewById(R.id.panelContent);

        elementEditor = findViewById(R.id.elementEditor);
        selectedElementName = findViewById(R.id.selectedElementName);
        sbSize = findViewById(R.id.sbSize);
        sbRotation = findViewById(R.id.sbRotation);

        findViewById(R.id.btnAddImage).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });

        setupSliders();
    }

    private void setupSwipePreview() {
        shareFrame.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isDefaultMode) return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        return true;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float deltaX = startX - endX;

                        if (Math.abs(deltaX) > 150) {
                            if (deltaX > 0) {
                                currentThemeIndex = (currentThemeIndex + 1) % THEME_COUNT;
                            } else {
                                currentThemeIndex = (currentThemeIndex - 1 + THEME_COUNT) % THEME_COUNT;
                            }
                            applyTheme(currentThemeIndex);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    // Menampilkan ringkasan data asli di kartu "Polos" pada mode selector atas
    private void populateTabPreview() {
        TextView tvValue = findViewById(R.id.tvTabPolosValue);
        TextView tvSub = findViewById(R.id.tvTabPolosSub);
        if (tvValue != null && jarak != null) tvValue.setText(jarak);
        if (tvSub != null) tvSub.setText(pace + " /km · " + waktu);
    }

    private void setupTabs() {
        tabPaceTheme.setOnClickListener(v -> {
            isDefaultMode = false;
            smoothLayoutChange();
            deselectElement();

            tabPaceTheme.setBackgroundResource(R.drawable.card_tab_selected);
            ((TextView) findViewById(R.id.tvTabThemeLabel)).setTextColor(getResources().getColor(R.color.lime));
            tabDefault.setBackgroundResource(R.drawable.card_glass);
            ((TextView) findViewById(R.id.tvTabPolosMode)).setTextColor(getResources().getColor(R.color.muted_fg));

            paceStyleSelector.setVisibility(View.VISIBLE);
            // Di mode PACE Theme, elemen TIDAK bisa diedit/di-drag lagi,
            // jadi panel editor elemen disembunyikan sepenuhnya.
            defaultControls.setVisibility(View.GONE);
            applyTheme(currentThemeIndex);
        });

        tabDefault.setOnClickListener(v -> {
            isDefaultMode = true;
            smoothLayoutChange();
            deselectElement();

            tabDefault.setBackgroundResource(R.drawable.card_tab_selected);
            ((TextView) findViewById(R.id.tvTabPolosMode)).setTextColor(getResources().getColor(R.color.lime));
            tabPaceTheme.setBackgroundResource(R.drawable.card_glass);
            ((TextView) findViewById(R.id.tvTabThemeLabel)).setTextColor(getResources().getColor(R.color.muted_fg));

            paceStyleSelector.setVisibility(View.GONE);
            defaultControls.setVisibility(View.VISIBLE);
            setupDefaultMode();
        });
    }

    // Navigator tema: panah kiri/kanan + dot indicator, menggantikan daftar thumbnail
    private void setupThemeNavigator() {
        dotsContainer.removeAllViews();
        for (int i = 0; i < THEME_COUNT; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    (int) dpToPx(8), (int) dpToPx(8));
            lp.setMarginEnd((int) dpToPx(6));
            dot.setLayoutParams(lp);
            dotsContainer.addView(dot);
        }
        updateDots();

        findViewById(R.id.btnThemePrev).setOnClickListener(v -> {
            currentThemeIndex = (currentThemeIndex - 1 + THEME_COUNT) % THEME_COUNT;
            applyTheme(currentThemeIndex);
        });
        findViewById(R.id.btnThemeNext).setOnClickListener(v -> {
            currentThemeIndex = (currentThemeIndex + 1) % THEME_COUNT;
            applyTheme(currentThemeIndex);
        });
    }

    private void updateDots() {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            View dot = dotsContainer.getChildAt(i);
            boolean active = i == currentThemeIndex;
            dot.setBackgroundResource(active ? R.drawable.dot_active : R.drawable.dot_inactive);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dot.getLayoutParams();
            lp.width = (int) dpToPx(active ? 20 : 8);
            lp.height = (int) dpToPx(8);
            dot.setLayoutParams(lp);
        }
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private void applyTheme(int index) {
        currentThemeIndex = index;
        updateDots();
        shareFrame.removeAllViews();
        int layoutRes = getResources().getIdentifier("layout_share_pace_" + (index + 1), "layout", getPackageName());
        if (layoutRes == 0) return;
        View themeView = LayoutInflater.from(this).inflate(layoutRes, shareFrame, false);

        updateTextView(themeView, R.id.tvShareJarak, jarak);
        updateTextView(themeView, R.id.tvShareWaktu, waktu);
        updateTextView(themeView, R.id.tvShareElev, elev);
        updateTextView(themeView, R.id.tvSharePace, pace);

        themeView.setAlpha(0f);
        shareFrame.addView(themeView);
        // Sengaja TIDAK memanggil setupDraggableElements() di sini: elemen pada
        // PACE Theme bersifat final/tidak bisa diedit, sesuai desain tema.
        themeView.animate().alpha(1f).setDuration(180).start();
        scaleToFitPreview(themeView);
        applyCurrentBackground();
    }

    private void applyCurrentBackground() {
        if (shareFrame.getChildCount() == 0) return;
        View currentView = shareFrame.getChildAt(0);

        ImageView bg = currentView.findViewById(R.id.ivShareBg);

        // Jika layout tidak punya ivShareBg (seperti theme tertentu), kita tambahkan secara dinamis
        if (bg == null && currentView instanceof android.view.ViewGroup) {
            bg = new ImageView(this);
            bg.setId(R.id.ivShareBg);
            android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            ((android.view.ViewGroup) currentView).addView(bg, 0, lp);
            bg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if (bg != null) {
            if (selectedBgUri != null) {
                bg.setVisibility(View.VISIBLE);
                bg.setImageURI(selectedBgUri);
                // Matikan background bawaan layout agar foto terlihat full
                currentView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            } else {
                bg.setVisibility(View.GONE);
                // Biarkan background asli dari XML muncul
            }
        }
    }

    private void setupDraggableElements(View root) {
        if (root == null) return;
        setupElement(root.findViewById(R.id.tvShareJarak), "Jarak");
        setupElement(root.findViewById(R.id.tvShareElev), "Elevasi");
        setupElement(root.findViewById(R.id.tvShareWaktu), "Waktu");
        setupElement(root.findViewById(R.id.ivSharePath), "Rute");
        setupElement(root.findViewById(R.id.tvPaceWatermark), "Watermark");
    }

    private void scaleToFitPreview(View content) {
        content.post(() -> {
            int contentW = content.getWidth();
            int contentH = content.getHeight();
            int boxW = shareFrame.getWidth();
            int boxH = shareFrame.getHeight();
            if (contentW == 0 || contentH == 0 || boxW == 0 || boxH == 0) return;

            // Fit sepenuhnya di dalam box (tidak pernah terpotong), lalu diberi
            // sedikit margin (0.92f) dan selalu di-center secara horizontal & vertikal.
            float scale = Math.min((float) boxW / contentW, (float) boxH / contentH) * 0.92f;
            content.setPivotX(contentW / 2f);
            content.setPivotY(contentH / 2f);
            content.setScaleX(scale);
            content.setScaleY(scale);

            content.setTranslationX((boxW - contentW) / 2f);
            content.setTranslationY((boxH - contentH) / 2f);
        });
    }

    private void updateTextView(View root, int id, String text) {
        TextView tv = root.findViewById(id);
        if (tv != null) tv.setText(text);
    }

    private void setupSliders() {
        sbSize.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && selectedView != null) {
                    if (selectedView instanceof TextView) {
                        ((TextView) selectedView).setTextSize(10 + progress);
                    } else {
                        float scale = 0.5f + (progress / 50.0f);
                        selectedView.setScaleX(scale);
                        selectedView.setScaleY(scale);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });

        sbRotation.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && selectedView != null) {
                    selectedView.setRotation(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
            }
        });
    }

    private void setupDefaultMode() {
        shareFrame.removeAllViews();
        View defaultView = LayoutInflater.from(this).inflate(R.layout.layout_share_activity, shareFrame, false);

        // Hanya di mode Polos elemen bisa di-drag & diedit lewat panel di bawah
        setupElement(defaultView.findViewById(R.id.tvShareJarak), "Jarak");
        setupElement(defaultView.findViewById(R.id.tvShareElev), "Elevasi");
        setupElement(defaultView.findViewById(R.id.tvShareWaktu), "Waktu");
        setupElement(defaultView.findViewById(R.id.ivSharePath), "Rute");
        setupElement(defaultView.findViewById(R.id.tvPaceWatermark), "Watermark");

        // Update data
        ((TextView) defaultView.findViewById(R.id.tvShareJarak)).setText(jarak + " km");
        ((TextView) defaultView.findViewById(R.id.tvShareWaktu)).setText(waktu);
        ((TextView) defaultView.findViewById(R.id.tvShareElev)).setText(elev);

        defaultView.setAlpha(0f);
        shareFrame.addView(defaultView);
        defaultView.animate().alpha(1f).setDuration(180).start();
        scaleToFitPreview(defaultView);
        applyCurrentBackground();

        // Click on background to deselect
        defaultView.setOnClickListener(v -> deselectElement());
    }

    private void setupElement(View view, String name) {
        if (view == null) return;
        view.setTag(name);

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    selectElement(v);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - lastX;
                    float deltaY = event.getRawY() - lastY;
                    // Parent (kanvas) mungkin di-scale supaya muat penuh di preview,
                    // jadi delta gerakan jari perlu disesuaikan skalanya biar elemen
                    // ngikutin jari secara akurat, bukan lebih cepat/lambat.
                    float parentScale = (v.getParent() instanceof View) ? ((View) v.getParent()).getScaleX() : 1f;
                    if (parentScale <= 0f) parentScale = 1f;
                    v.setX(v.getX() + deltaX / parentScale);
                    v.setY(v.getY() + deltaY / parentScale);
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
            }
            return true;
        });
    }

    // Highlight elemen terpilih pakai FOREGROUND (bukan background+padding) supaya
    // ukuran/posisi elemen tidak berubah/lompat saat dipilih atau dilepas, dan
    // pakai fade halus alih-alih border tegas yang instan.
    private void selectElement(View view) {
        if (isDefaultMode == false) return; // Elemen PACE Theme tidak bisa dipilih/diedit

        if (selectedView != null && selectedView != view) {
            clearSelectionVisual(selectedView);
        }
        selectedView = view;
        applySelectionVisual(selectedView);

        boolean firstOpen = elementEditor.getVisibility() != View.VISIBLE;
        if (firstOpen) {
            smoothLayoutChange();
            elementEditor.setVisibility(View.VISIBLE);
            elementEditor.setAlpha(0f);
            elementEditor.animate().alpha(1f).setDuration(180).start();
        }
        selectedElementName.setText(view.getTag().toString());

        sbSize.setEnabled(true);
        if (view instanceof TextView) {
            float size = ((TextView) view).getTextSize() / getResources().getDisplayMetrics().scaledDensity;
            sbSize.setProgress((int) (size - 10));
        } else {
            sbSize.setProgress((int) ((view.getScaleX() - 0.5f) * 50));
        }

        sbRotation.setProgress((int) view.getRotation());
    }

    private void deselectElement() {
        if (selectedView != null) {
            clearSelectionVisual(selectedView);
            selectedView = null;
        }
        if (elementEditor.getVisibility() == View.VISIBLE) {
            smoothLayoutChange();
            elementEditor.setVisibility(View.GONE);
        }
    }

    private void applySelectionVisual(View view) {
        android.graphics.drawable.Drawable glow =
                androidx.core.content.ContextCompat.getDrawable(this, R.drawable.element_select_glow).mutate();
        glow.setAlpha(0);
        view.setForeground(glow);
        android.animation.ObjectAnimator.ofInt(glow, "alpha", 0, 255).setDuration(150).start();
    }

    private void clearSelectionVisual(View view) {
        android.graphics.drawable.Drawable fg = view.getForeground();
        if (fg != null) {
            android.animation.ObjectAnimator anim = android.animation.ObjectAnimator.ofInt(fg, "alpha", 255, 0);
            anim.setDuration(150);
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    view.setForeground(null);
                }
            });
            anim.start();
        } else {
            view.setForeground(null);
        }
    }

    // Animasi transisi ukuran panel bawah (mis. saat editor slider muncul/hilang)
    // supaya perubahan tinggi bottomControls terasa smooth, bukan patah/instan.
    private void smoothLayoutChange() {
        AutoTransition transition = new AutoTransition();
        transition.setDuration(220);
        TransitionManager.beginDelayedTransition(bottomControls, transition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedBgUri = data.getData();
            applyCurrentBackground();
        }
    }

    private void saveAndShare() {
        // Simple bitmap capture of shareFrame
        shareFrame.post(() -> {
            Bitmap bitmap = Bitmap.createBitmap(shareFrame.getWidth(), shareFrame.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareFrame.draw(canvas);

            try {
                File cachePath = new File(getCacheDir(), "images");
                cachePath.mkdirs();
                File file = new File(cachePath, "activity_share.png");
                FileOutputStream stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();

                Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, "Bagikan Aktivitas"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
