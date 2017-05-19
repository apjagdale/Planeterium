package org.gearvrf.planeterium;

import org.gearvrf.GVRActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class PlaneteriumActivity extends GVRActivity implements
        VRTouchPadGestureDetector.OnTouchPadGestureListener {
    private VRTouchPadGestureDetector mDetector = null;
    private PlaneteriumMain planeteriumMainObj = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetector = new VRTouchPadGestureDetector(this);
        planeteriumMainObj = new PlaneteriumMain(this);
        setMain(planeteriumMainObj);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onSingleTap(MotionEvent e) {
        Log.i(TAG, " Yo onTap");
        planeteriumMainObj.handleTapActivity();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onSwipe(MotionEvent e, VRTouchPadGestureDetector.SwipeDirection swipeDirection, float velocityX, float velocityY) {
        Log.i(TAG, " Yo onSwipe");
        planeteriumMainObj.handleSwipeActivity();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }
}
