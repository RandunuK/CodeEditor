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

import android.content.res.Resources;
import android.graphics.RectF;
import android.util.Log;
import android.view.Gravity;
import android.widget.PopupWindow;

/**
 * Editor base panel class
 *
 * @author Rose
 */
class EditorTextActionBasePopupWindow extends PopupWindow {
    public static int HIDE_BY_DISMISS = 0;
    public static int HIDE_BY_DRAG = 1;
    public static int HIDE_BY_SCROLL = 2;
    private CodeEditor mEditor;
    private int[] mLocation;
    private int mTop;
    private int mLeft;
    protected int popHeightPx = -1;
    private float textSizePx = -1;
    private int widthPixels;
    private int heightPixels;
    private int hideType = -1;


    /**
     * Create a panel for editor
     *
     * @param editor Target editor
     */
    public EditorTextActionBasePopupWindow(CodeEditor editor) {
        if (editor == null) {
            throw new IllegalArgumentException();
        }
        mLocation = new int[2];
        mEditor = editor;
        super.setTouchable(true);
        textSizePx = mEditor.getTextSizePx();
        widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * Set the left position on the editor rect
     *
     * @param x X on editor
     */
    public void setExtendedX(float x) {
        mLeft = (int) x;
    }

    /**
     * Set the top position on the editor rect
     *
     * @param y Y on editor
     */
    public void setExtendedY(float y) {
        mTop = (int) y;
    }

    public void updatePosition() {
        int width = mEditor.getWidth();
        if (mLeft > width - getWidth()) {
            mLeft = width - getWidth();
        }
        int height = mEditor.getHeight();
        if (mTop > height - getHeight()) {
            mTop = height - getHeight();
        }
        if (mTop < 0) {
            mTop = 0;
        }
        if (mLeft < 0) {
            mLeft = 0;
        }
        mEditor.getLocationInWindow(mLocation);
        if (isShowing()) {
            update(mLocation[0] + mLeft, mLocation[1] + mTop, getWidth(), getHeight());
        }
    }

    private final RectF selectionRect = new RectF();

    /**
     * Show the panel or update its position(If already shown)
     */
    public void show() {
        RectF leftHandleRect = mEditor.getLeftHandleRect();
        RectF rightHandleRect = mEditor.getRightHandleRect();

        if (rightHandleRect.isEmpty()) {
            rightHandleRect.top = heightPixels;
            rightHandleRect.left = widthPixels;
            rightHandleRect.bottom = heightPixels;
            rightHandleRect.right = widthPixels;
        }

        if (leftHandleRect.isEmpty()) {
            Log.d("EMPTY", "leftHandleRect");
        }

        float handleHeight = leftHandleRect.height();
        selectionRect.top = Math.min(leftHandleRect.top, rightHandleRect.top);
        selectionRect.bottom = Math.max(leftHandleRect.bottom, rightHandleRect.bottom);
        selectionRect.left = Math.min(leftHandleRect.left, rightHandleRect.left);
        selectionRect.right = Math.max(leftHandleRect.right, rightHandleRect.right);

        int width = mEditor.getWidth();
        if (mLeft > width - getWidth()) {
            mLeft = width - getWidth();
        }
        int height = mEditor.getHeight();
        if (mTop > height - getHeight()) {
            mTop = height - getHeight();
        }


        if (mTop < 0) {
            mTop = 0;
        }
        if (mLeft < 0) {
            mLeft = 0;
        }
        mEditor.getLocationInWindow(mLocation);
        boolean topCovered = mLocation[1] > selectionRect.top - textSizePx - popHeightPx - handleHeight;

        if (topCovered) {
            mTop = (int) (selectionRect.bottom + (handleHeight));
        } else {
            mTop = (int) (selectionRect.top - textSizePx - popHeightPx - handleHeight);
        }

        if (isShowing()) {
            update(mLocation[0] + mLeft, mLocation[1] + mTop, getWidth(), getHeight());
            return;
        }
        super.showAtLocation(mEditor,
                Gravity.START | Gravity.TOP,
                mLocation[0] + mLeft, mLocation[1] + mTop);
    }

    /**
     * Hide the panel (If shown)
     */
    public void hide(int type) {
        if (isShowing()) {
            hideType = type;
            if ((hideType == HIDE_BY_DRAG || hideType == HIDE_BY_SCROLL) && mEditor.getEventHandler() != null) {
                mEditor.getEventHandler().notifyInteractionEnd();
            }
            Log.d("BEGIN_HIDE", "hide: " + type);
            super.dismiss();
        }

    }

}

