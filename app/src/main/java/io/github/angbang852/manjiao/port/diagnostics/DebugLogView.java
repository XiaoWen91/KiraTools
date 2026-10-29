package io.github.angbang852.manjiao.port.diagnostics;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.RandomAccessFile;

/** Minimal View-based log viewer for KiraTools. No Miuix/Compose dependency. */
public final class DebugLogView {
    private static final String LOG_PATH =
            "/sdcard/Android/media/io.github.angbang852.manjiao/slowkick.log";
    private static final int MAX_READ = 200_000;

    private DebugLogView() { }

    public static void show(Context context) {
        String content = DebugLogRedactor.text(readTail(LOG_PATH, MAX_READ));
        if (content.isEmpty()) content = "暂无日志文件：" + LOG_PATH;

        TextView text = new TextView(context);
        text.setText(content);
        text.setTextSize(11f);
        text.setTextColor(Color.WHITE);
        text.setBackgroundColor(0xFF1E1E22);
        text.setPadding(dp(context, 12), dp(context, 12), dp(context, 12), dp(context, 12));
        text.setHorizontallyScrolling(true);
        text.setMovementMethod(new ScrollingMovementMethod());

        ScrollView scroll = new ScrollView(context);
        scroll.setBackgroundColor(0xFF1E1E22);
        scroll.addView(text);

        final String finalContent = content;
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("KiraTools 日志")
                .setView(scroll)
                .setPositiveButton("关闭", null)
                .setNeutralButton("复制", (dialog, which) -> {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("KiraToolsLog", finalContent));
                        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    private static String readTail(String path, int maxBytes) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(path, "r");
            long length = file.length();
            long start = Math.max(0, length - maxBytes);
            file.seek(start);
            byte[] buffer = new byte[(int) Math.min(maxBytes, length - start)];
            int read = file.read(buffer);
            if (read <= 0) return "";
            return new String(buffer, 0, read, "UTF-8");
        } catch (Throwable ignored) {
            return "";
        } finally {
            if (file != null) {
                try { file.close(); } catch (Throwable ignored) { }
            }
        }
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
