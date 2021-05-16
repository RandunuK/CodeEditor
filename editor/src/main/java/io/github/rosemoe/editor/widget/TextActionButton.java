/*
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;

import io.github.rosemoe.editor.R;

public class TextActionButton extends CardView implements View.OnClickListener {
    private LayoutInflater mLayoutInflater;
    private CardView mCardView;
    private MaterialButton mTextView;
    private View.OnClickListener mOnCardClickListener;

    public TextActionButton(@NonNull Context context) {
        super(context);
        inflateLayout(context);
    }

    public TextActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context, attrs);
    }

    public TextActionButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateLayout(context, attrs);
    }

    private void inflateLayout(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mLayoutInflater.inflate(R.layout.text_action_button, this);
        mTextView = findViewById(R.id.text_action_text_view_name);
        setOnClickListener(this);
    }

    private void inflateLayout(Context context, AttributeSet attrs) {
        inflateLayout(context);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.text_action, 0, 0);
        CharSequence name = a.getString(R.styleable.text_action_ta_text);
        Drawable drawableTop = a.getDrawable(R.styleable.text_action_ta_drawableTop);
        mTextView.setIcon(drawableTop);
        //mTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableTop, null, null);
        mTextView.setText(name);
    }

    public void setOnCardClickListener(OnClickListener onCardClickListener) {
        this.mOnCardClickListener = onCardClickListener;
    }

    @Override
    public void onClick(View v) {
        if (this.mOnCardClickListener != null) {
            this.mOnCardClickListener.onClick(v);
        }
    }
}
