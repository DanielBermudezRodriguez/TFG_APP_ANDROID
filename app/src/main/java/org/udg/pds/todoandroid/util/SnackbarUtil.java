package org.udg.pds.todoandroid.util;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import org.udg.pds.todoandroid.R;

public class SnackbarUtil {

    public static void showSnackBar(View viewById, String message, int lengthLong, boolean isErrorMessage) {
        Snackbar mSnackBar = Snackbar.make(viewById, message, lengthLong);
        View view = mSnackBar.getView();
        TextView mainTextView = (view).findViewById(android.support.design.R.id.snackbar_text);
        if (isErrorMessage)view.setBackgroundColor(Color.RED);
        else view.setBackgroundColor(Color.GREEN);
        mainTextView.setTextColor(Color.WHITE);
        mSnackBar.show();
    }
}
