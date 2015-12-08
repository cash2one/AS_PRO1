package com.linkage.mobile72.sh.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.Checkable;
import android.widget.TextView;

public class TextViewForCheck extends TextView implements Checkable {  
	  
    private final int[] STATE_CHECKED = { android.R.attr.state_checked };  

    private int[] mSavedState;  
    private boolean mChecked = false;  

    public TextViewForCheck(Context context) {  
        super(context);  
    }  

    public void setChecked(boolean checked) {  
        if (mChecked != checked) {  
            mChecked = checked;  
            updateBackground();  
        }  
    }  

    public boolean isChecked() {  
        return mChecked;  
    }  

    public void toggle() {  
        setChecked(!mChecked);  
    }  

    private void updateBackground() {  
        Drawable bg = this.getBackground();  

        // 在这里切换checked/unchecked状态  
        if (bg.getClass().equals(StateListDrawable.class)) {  
            if (isChecked()) {  
                mSavedState = bg.getState();  
                bg.setState(STATE_CHECKED);  
            } else if (mSavedState != null) {  
                bg.setState(mSavedState);  
            }  
        }  
    }  
}  
