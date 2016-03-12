package ru.mit.au.spb.olga.catendar.view.calendar;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SimpleGestureFilter extends SimpleOnGestureListener {
    public enum Swipe_direction {UP, DOWN, LEFT, RIGHT}

    private final static int ACTION_FAKE = -13;

    private boolean tapIndicator = false;

    private Activity context;
    private GestureDetector detector;
    private SimpleGestureListener listener;

    public SimpleGestureFilter(Activity context,SimpleGestureListener sgl) {

        this.context = context;
        this.detector = new GestureDetector(context, this);
        this.listener = sgl;
    }

    public void onTouchEvent(MotionEvent event) {
        boolean result = this.detector.onTouchEvent(event);
        if (event.getAction() == ACTION_FAKE)
            event.setAction(MotionEvent.ACTION_UP);
        else if (result)
            event.setAction(MotionEvent.ACTION_CANCEL);
        else if (this.tapIndicator) {
            event.setAction(MotionEvent.ACTION_DOWN);
            this.tapIndicator = false;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        final float xDistance = Math.abs(e1.getX() - e2.getX());
        final float yDistance = Math.abs(e1.getY() - e2.getY());

        int swipe_Max_Distance = 350;
        if(xDistance > swipe_Max_Distance || yDistance > swipe_Max_Distance)
            return false;

        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);
        boolean result = false;

        int swipe_Min_Velocity = 100;
        int swipe_Min_Distance = 100;
        if(velocityX > swipe_Min_Velocity && xDistance > swipe_Min_Distance){
            if(e1.getX() > e2.getX())
                this.listener.onSwipe(Swipe_direction.LEFT);
            else
                this.listener.onSwipe(Swipe_direction.RIGHT);

            result = true;
        }
        else if(velocityY > swipe_Min_Velocity && yDistance > swipe_Min_Distance){
            if(e1.getY() > e2.getY())
                this.listener.onSwipe(Swipe_direction.UP);
            else
                this.listener.onSwipe(Swipe_direction.DOWN);

            result = true;
        }

        return result;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        this.tapIndicator = true;
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent arg) {
        this.listener.onDoubleTap();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent arg) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent arg) {
            arg.setAction(ACTION_FAKE);
            this.context.dispatchTouchEvent(arg);
        return false;
    }

    public interface SimpleGestureListener{
        void onSwipe(Swipe_direction direction);
        void onDoubleTap();
    }
}