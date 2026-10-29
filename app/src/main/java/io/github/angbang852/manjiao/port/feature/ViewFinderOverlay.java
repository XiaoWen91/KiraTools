package io.github.angbang852.manjiao.port.feature;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.github.angbang852.manjiao.port.config.CustomViewLocator;

/** Minimal View-based finder overlay for KiraTools. No Miuix/Compose dependency. */
public final class ViewFinderOverlay {
    public interface Listener {
        void onSelected(View view, CustomViewLocator locator);
        void onClosed();
    }

    private final Activity activity;
    private final View windowRoot;
    private final View feedRoot;
    private final Listener listener;
    private FrameLayout overlay;
    private BoundsView boundsView;
    private TextView info;
    private View selected;
    private CustomViewLocator locator;

    private ViewFinderOverlay(Activity activity, View windowRoot, View feedRoot, Listener listener) {
        this.activity = activity;
        this.windowRoot = windowRoot;
        this.feedRoot = feedRoot;
        this.listener = listener;
    }

    public static ViewFinderOverlay show(Activity activity, View root, Listener listener) {
        return show(activity, root, null, listener);
    }

    public static ViewFinderOverlay show(Activity activity, View root, View feedRoot, Listener listener) {
        ViewFinderOverlay finder = new ViewFinderOverlay(activity, root, feedRoot, listener);
        finder.attach();
        return finder;
    }

    private void attach() {
        overlay = new FrameLayout(activity);
        overlay.setTag("kira_view_finder_overlay");
        overlay.setBackgroundColor(Color.TRANSPARENT);
        overlay.setClickable(true);

        boundsView = new BoundsView(activity);
        overlay.addView(boundsView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        info = new TextView(activity);
        info.setTextColor(Color.WHITE);
        info.setTextSize(12f);
        info.setPadding(dp(10), dp(8), dp(10), dp(8));
        info.setBackground(bg(0xCC1E1E22, 14));
        info.setMaxLines(3);
        FrameLayout.LayoutParams infoLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoLp.gravity = Gravity.TOP;
        infoLp.setMargins(dp(12), dp(48), dp(12), 0);
        overlay.addView(info, infoLp);

        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER);
        bar.setPadding(dp(8), dp(8), dp(8), dp(8));
        bar.setBackground(bg(0xCC1E1E22, 18));
        bar.addView(button("复制", v -> copyInfo()));
        bar.addView(button("保存", v -> {
            if (selected != null && locator != null && listener != null) {
                listener.onSelected(selected, locator);
            }
        }));
        bar.addView(button("关闭", v -> close()));
        FrameLayout.LayoutParams barLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        barLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        barLp.setMargins(0, 0, 0, dp(24));
        overlay.addView(bar, barLp);

        overlay.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                pick(event.getRawX(), event.getRawY());
                v.performClick();
            }
            return true;
        });
        overlay.setOnClickListener(v -> { });

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        decor.addView(overlay, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        info.setText("点击选择控件，点“保存”写入定位");
    }

    private void pick(float rawX, float rawY) {
        List<View> candidates = ViewInspector.hitCandidates(windowRoot, (int) rawX, (int) rawY);
        if (candidates == null || candidates.isEmpty()) return;
        for (View candidate : candidates) {
            if (candidate == overlay || candidate == boundsView || candidate == info) continue;
            if (candidate.getTag() != null && "kira_view_finder_overlay".equals(candidate.getTag())) continue;
            select(candidate);
            return;
        }
    }

    private void select(View view) {
        selected = view;
        locator = CustomViewLocator.capture(windowRoot, feedRoot, view);
        boundsView.setTarget(view);
        String label = locator != null ? locator.label : view.getClass().getSimpleName();
        Rect r = ViewInspector.screenBounds(view);
        info.setText(label + "\n" + ViewInspector.describe(view) + "\n" + r.width() + "×" + r.height());
    }

    private void copyInfo() {
        if (info == null) return;
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("KiraViewFinder", info.getText()));
            toast("已复制");
        }
    }

    public void close() {
        try {
            if (overlay != null && overlay.getParent() instanceof ViewGroup) {
                ((ViewGroup) overlay.getParent()).removeView(overlay);
            }
        } catch (Throwable ignored) { }
        overlay = null;
        boundsView = null;
        info = null;
        if (listener != null) listener.onClosed();
    }

    public View getSelected() { return selected; }
    public CustomViewLocator getLocator() { return locator; }

    private TextView button(String text, View.OnClickListener click) {
        TextView tv = new TextView(activity);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(13f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(16), dp(8), dp(16), dp(8));
        tv.setBackground(bg(0x33FFFFFF, 12));
        tv.setOnClickListener(click);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(4), 0, dp(4), 0);
        tv.setLayoutParams(lp);
        return tv;
    }

    private GradientDrawable bg(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radiusDp));
        d.setStroke(dp(1), 0x33FFFFFF);
        return d;
    }

    private int dp(int v) {
        return Math.round(v * activity.getResources().getDisplayMetrics().density);
    }

    private void toast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    private static final class BoundsView extends View {
        private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        private View target;

        BoundsView(Context context) {
            super(context);
            fill.setColor(0x3300A8FF);
            stroke.setColor(0xFF0A84FF);
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2f);
        }

        void setTarget(View v) {
            target = v;
            invalidate();
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (target == null) return;
            Rect r = ViewInspector.screenBounds(target);
            int[] rootLoc = new int[2];
            getLocationOnScreen(rootLoc);
            float left = r.left - rootLoc[0];
            float top = r.top - rootLoc[1];
            float right = r.right - rootLoc[0];
            float bottom = r.bottom - rootLoc[1];
            canvas.drawRect(left, top, right, bottom, fill);
            canvas.drawRect(left, top, right, bottom, stroke);
        }
    }
}
