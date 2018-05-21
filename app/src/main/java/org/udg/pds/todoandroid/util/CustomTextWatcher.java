package org.udg.pds.todoandroid.util;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

public class CustomTextWatcher implements TextWatcher {

    private TextInputLayout textInputLayout;

    public CustomTextWatcher(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textInputLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
