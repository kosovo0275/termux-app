package com.termux.app;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class AutoRepeatButton extends Button
{
    public AutoRepeatButton(Context context) {
        super(context);
        init();
    }

    public AutoRepeatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoRepeatButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Delay between initial ACTION_DOWN event and first onClickListener call.
     */
    public void setInitialDelay(int initialDelayInMilliseconds) {
        mInitialDelayInMilliseconds = initialDelayInMilliseconds;
    }

    /**
     * Delay between each subsequent onClickListener call.
     */
    public void setRepeatPeriod(int repeatPeriodInMilliseconds) {
        mRepeatPeriodInMilliseconds = repeatPeriodInMilliseconds;
    }

    private int mInitialDelayInMilliseconds = AutoRepeatDefault.initialDelayInMilliseconds;
    private int mRepeatPeriodInMilliseconds = AutoRepeatDefault.repeatPeriodInMilliseconds;
    private final Handler mHandler = new Handler();
    private Object mLock = new Object();
    private boolean mInitialDelayElapsed;
    private final Action mAction = new Action(this);

    private final Runnable mActionRepeater = new Runnable() {
        @Override
        public void run() {
            // We run Action only after initial delay, because the action will
            // be performed once by the OnClickListener. This avoids double
            // actions on short clicks.
            if (mInitialDelayElapsed) {
                mAction.run();
                mHandler.postAtTime(this, SystemClock.uptimeMillis() + mRepeatPeriodInMilliseconds);
            } else {
                mInitialDelayElapsed = true;
                mHandler.postAtTime(this, SystemClock.uptimeMillis() + mInitialDelayInMilliseconds);
            }
        }
    };

    private class Action implements Runnable
    {
        public Action(View view) {
            mView = view;
        }

        @Override
        public void run() {
            synchronized (mLock) {
                mView.performClick();
            }
        }

        private final View mView;
    }

    private void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int motionAction = event.getAction();
                if (motionAction == MotionEvent.ACTION_DOWN) {
                    mInitialDelayElapsed = true;
                    mHandler.removeCallbacks(mActionRepeater);
                    mActionRepeater.run();
                } else if (motionAction == MotionEvent.ACTION_UP) {
                    mHandler.removeCallbacks(mActionRepeater);
                    setPressed(false);
                    view = null;
                    return true;
                } else if (motionAction == MotionEvent.ACTION_CANCEL) {
                    mHandler.removeCallbacks(mActionRepeater);
                    setPressed(false);
                    view = null;
                    return true;
                }
                return false;
            }
        });
    }
}
