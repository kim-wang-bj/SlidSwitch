package com.wq.android.lib.slidswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qwang on 2016/9/2.
 */

public class SlidSwitch extends FrameLayout {

    public interface OnSwitchListener {
        public void onSwitch(int selectedIndex, String text);
    }

    private static final int DEFAULT_SWITCH_BUTTON_WIDTH = 60; //DP
    private static final int DEFAULT_CORNERS = 5; //DP

    private List<String> mSwitchTexts = new ArrayList<String>() {
        @Override
        public boolean add(String o) {
            if (o == null) {
                return false;
            }
            return super.add(o);
        }
    };
    private LinearLayout mBackgroundTextsContainer;
    private OnSwitchListener mOnSwitchListener;
    private Drawable mSwitchButtonBackground;
    private int mSwitchButtonTextColor = Color.WHITE;
    private int mBackgroundTextColor = Color.DKGRAY;
    private int mBackgroundColor = Color.LTGRAY;
    private int mSwitchButtonColor = Color.rgb(0x00, 0x99, 0xCB);
    private int mSwitchButtonCorners = 0;
    private int mBackgroundCorners = 0;
    private int mSwitchButtonWidth = 0;
    private int mSwitchButtonTextSize = 0;
    private int mBackgroundTextSize = 0;
    private int mDividerColor = Color.TRANSPARENT;
    private int mDividerPadding = 0;
    private int mDividerWidth = 0;
    private int mSelectedIndex = 0;
    private TextView mSwitchButton;

    public SlidSwitch(Context context) {
        super(context);
        init(context, null);
    }

    public SlidSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlidSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlidSwitch);
            CharSequence[] textsArray = typedArray.getTextArray(R.styleable.SlidSwitch_textArray);

            if (textsArray != null) {
                for (CharSequence c : textsArray) {
                    mSwitchTexts.add(c.toString());
                }
            } else {
                mSwitchTexts.add(typedArray.getString(R.styleable.SlidSwitch_textLeft));
                mSwitchTexts.add(typedArray.getString(R.styleable.SlidSwitch_textCenter));
                mSwitchTexts.add(typedArray.getString(R.styleable.SlidSwitch_textRight));
            }

            mBackgroundColor = typedArray.getColor(R.styleable.SlidSwitch_backgroundColor, mBackgroundColor);
            mBackgroundTextColor = typedArray.getColor(R.styleable.SlidSwitch_backgroundTextColor, mBackgroundTextColor);
            mBackgroundCorners = (int) typedArray.getDimension(R.styleable.SlidSwitch_backgroundCorners, dp2px(context, DEFAULT_CORNERS));
            mSwitchButtonBackground = typedArray.getDrawable(R.styleable.SlidSwitch_switchButtonBackground);
            mSwitchButtonTextColor = typedArray.getColor(R.styleable.SlidSwitch_switchButtonTextColor, mSwitchButtonTextColor);
            mSwitchButtonCorners = (int) typedArray.getDimension(R.styleable.SlidSwitch_switchButtonCorners, dp2px(context, DEFAULT_CORNERS));
            mSwitchButtonWidth = (int) typedArray.getDimension(R.styleable.SlidSwitch_switchButtonWidth, dp2px(context, DEFAULT_SWITCH_BUTTON_WIDTH));
            mSwitchButtonColor = typedArray.getColor(R.styleable.SlidSwitch_switchButtonColor, mSwitchButtonColor);
            mSwitchButtonTextSize = (int) typedArray.getDimension(R.styleable.SlidSwitch_switchButtonTextSize, 0);
            mBackgroundTextSize = (int) typedArray.getDimension(R.styleable.SlidSwitch_backgroundTextSize, 0);
            mDividerColor = typedArray.getColor(R.styleable.SlidSwitch_dividerColor, mDividerColor);
            mDividerPadding = (int) typedArray.getDimension(R.styleable.SlidSwitch_dividerPadding, 0);
            mDividerWidth = (int) typedArray.getDimension(R.styleable.SlidSwitch_dividerWidth, 0);
        }
        initBackground();
        initTextContainer();
        initSwitchButton();
    }

    private void initBackground() {
        if (getBackground() == null) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(mBackgroundCorners);
            drawable.setColor(mBackgroundColor);
            setBackground(drawable);
        }
    }

    private void initTextContainer() {
        mBackgroundTextsContainer = new LinearLayout(getContext());
        if (mDividerColor != Color.TRANSPARENT) {
            mBackgroundTextsContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(mDividerColor);
            drawable.setSize(mDividerWidth, 1);
            mBackgroundTextsContainer.setDividerPadding(mDividerPadding);
            mBackgroundTextsContainer.setDividerDrawable(drawable);
        }
        mBackgroundTextsContainer.setOrientation(LinearLayout.HORIZONTAL);

        addView(mBackgroundTextsContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (String txt : mSwitchTexts) {
            TextView tv = new TextView(getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setText(txt);
            tv.setTextColor(mBackgroundTextColor);
            if (mBackgroundTextSize > 0) {
                tv.setTextSize(mBackgroundTextSize);
            }
            mBackgroundTextsContainer.addView(tv, mSwitchButtonWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mBackgroundTextsContainer.setOnTouchListener(new OnTouchListener() {
            float switchBtnWidth = 0;
            float parentWidth = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        switchBtnWidth = mSwitchButton.getMeasuredWidth();
                        parentWidth = getMeasuredWidth();
                        break;
                    case MotionEvent.ACTION_UP:
                        float position = event.getX();
                        for (int i = 0, size = mSwitchTexts.size(); i < size; i++) {
                            float start = i * switchBtnWidth + mDividerWidth * i;
                            float end = i * switchBtnWidth + switchBtnWidth + mDividerWidth * i;
                            if (position >= start && position <= end) {
                                animateSwitchButtonAndFireEvent(i, start);
                                break;
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void initSwitchButton() {
        mSwitchButton = new TextView(getContext());
        mSwitchButton.setGravity(Gravity.CENTER);
        mSwitchButton.setText(mSwitchTexts.size() > 0 ? mSwitchTexts.get(0) : "");
        mSwitchButton.setTextColor(mSwitchButtonTextColor);
        if (mSwitchButtonTextSize > 0) {
            mSwitchButton.setTextSize(mSwitchButtonTextSize);
        }
        if (mSwitchButtonBackground != null) {
            mSwitchButton.setBackground(mSwitchButtonBackground);
        } else {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(mSwitchButtonCorners);
            drawable.setColor(mSwitchButtonColor);
            mSwitchButton.setBackground(drawable);
        }
        addView(mSwitchButton, mSwitchButtonWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwitchButton.setOnTouchListener(new OnTouchListener() {
            float switchBtnWidth = 0;
            float parentWidth = 0;
            float transX = 0f;
            float downX = 0f;
            boolean moved = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moved = false;
                        switchBtnWidth = mSwitchButton.getMeasuredWidth();
                        parentWidth = getMeasuredWidth();
                        downX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moved = true;
                        transX = event.getRawX() - getLeft() - downX;
                        transX = transX < 0 ? 0 : transX;
                        transX = transX > parentWidth - switchBtnWidth ? parentWidth - switchBtnWidth : transX;
                        v.setTranslationX(transX);
                        //log("SWitchButton Move: getRawX=" + event.getRawX() + " transX=" + transX);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (!moved) {
                            return true;
                        }
                        float position = (transX + switchBtnWidth / 2);
                        for (int i = 0, size = mSwitchTexts.size(); i < size; i++) {
                            float start = i * switchBtnWidth;
                            float end = i * switchBtnWidth + switchBtnWidth;
                            if (position >= start && position <= end) {
                                animateSwitchButtonAndFireEvent(i, start + mDividerWidth * i);
                                //log("SWitchButton Up: " + (start + mDividerWidth * i));
                                break;
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void animateSwitchButtonAndFireEvent(final int index, float position) {
        final String text = mSwitchTexts.get(index);
        if (mOnSwitchListener != null && index != mSelectedIndex) {
            mOnSwitchListener.onSwitch(index, text);
        }
        mSelectedIndex = index;
        mSwitchButton.animate().setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSwitchButton.setText(text);
            }
        }).setDuration(450).translationX(position);
    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setOnSwitchListener(OnSwitchListener l) {
        mOnSwitchListener = l;
    }

    public void setLabels(String... labels) {
        setLabels(Arrays.asList(labels));
    }

    public void setSelectedIndex(final int i) {
        post(new Runnable() {
            @Override
            public void run() {
                float position = i * mSwitchButton.getWidth() + mDividerWidth * i;
                animateSwitchButtonAndFireEvent(i, position);
            }
        });
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setLabels(List<String> labels) {
        mSwitchTexts.clear();
        mSwitchTexts.addAll(labels);
        removeAllViews();
        initTextContainer();
        initSwitchButton();
    }

    private void log(String msg) {
        Log.i(getClass().getSimpleName(), msg);
    }
}
