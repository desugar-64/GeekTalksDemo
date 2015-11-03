package com.android.sergeyfitis.geektalksdemo.helpers;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;

/**
 * Created by serge on 31.10.2015.
 */
public class TextUtils {

    public static String optTilText(@NonNull TextInputLayout inputLayout) {
        String text = null;
        if (inputLayout.getEditText() != null) {
            text = inputLayout.getEditText()
                    .getText()
                    .toString();
        }
        return text;
    }

    public static void tilError(@NonNull TextInputLayout inputLayout, boolean enable, String errorMessage) {
        inputLayout.setError(errorMessage);
        inputLayout.setErrorEnabled(enable);
    }
}
