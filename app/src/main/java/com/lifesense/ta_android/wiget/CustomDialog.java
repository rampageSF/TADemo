package com.lifesense.ta_android.wiget;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.lifesense.rpm.doctor.dev.R;

public class CustomDialog extends Dialog {
        public CustomDialog(Context context, int width, int height, int layou,
                            int gravity) {

            super(context, R.style.LSTheme_dialog);
            // set content
            setContentView(layou);
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            params.height = height;
            params.gravity = gravity;
            window.setAttributes(params);
        }

    }