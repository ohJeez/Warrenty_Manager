
package com.example.warrantymanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Rect;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SafeArea {

    public static void apply(Activity activity) {
        View root = activity.findViewById(android.R.id.content);

        View content = ((ViewGroup) root).getChildAt(0);
        if (content == null) return;

        Rect initial = new Rect(
                content.getPaddingLeft(),
                content.getPaddingTop(),
                content.getPaddingRight(),
                content.getPaddingBottom()
        );

        ViewCompat.setOnApplyWindowInsetsListener(content, (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );

            view.setPadding(
                    initial.left + bars.left,
                    initial.top + bars.top,
                    initial.right + bars.right,
                    initial.bottom + bars.bottom
            );

            return insets;
        });

        ViewCompat.requestApplyInsets(content);
    }
}