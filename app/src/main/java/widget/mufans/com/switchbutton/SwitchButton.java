package widget.mufans.com.switchbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by liujun on 16-3-30.
 *
 * simple switchButton
 */
public class SwitchButton extends View {

    enum State{
        ON,
        OFF,
        SWITHING
    }


    private static final int DEF_TXT_SIZE = 12;
    private static final String DEF_TXT_ON = "ON";
    private static final String DEF_TXT_OFF = "OFF";

    private State currentState;


    private boolean isOn;


    private int left;

    private Paint paint;

    private String textOn = DEF_TXT_ON;
    private String textOff = DEF_TXT_OFF;

    private int textSize;
    private String currentTxt;





    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context, attrs);

        init();
    }



    private void init() {
        setBackgroundResource(R.drawable.switch_bg);

        if (isOn) {
            setSelected(true);
            currentState = State.ON;
        } else {
            setSelected(false);
            currentState = State.OFF;
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentState == State.SWITHING) {
                    return;
                }

                if (isOn) {
                    animOff();
                } else {
                    animOn();
                }
            }
        });
    }

    public void toggle() {
        if (isOn) {
            setSelected(true);
        } else {
            setSelected(false);
        }
        invalidate();
    }


    private void animOff() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(left, getWidth() / 3);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                left = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentState = State.OFF;
                isOn = false;
                setSelected(false);

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                currentState = State.SWITHING;
            }
        });
        valueAnimator.start();
    }

    private void animOn() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(left,0);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                left = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentState = State.ON;
                isOn = true;
                setSelected(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                currentState = State.SWITHING;
            }
        });
        valueAnimator.start();
    }

    private String getCurrentTxt() {

        switch (currentState) {
            case ON:
                currentTxt = textOn;
                break;
            case OFF:
                currentTxt = textOff;
                break;

        }

        if (currentTxt == null) {
            currentTxt = isOn ? textOn : textOff;
        }

        return currentTxt;
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        textOff = typedArray.getString(R.styleable.SwitchButton_switch_off_txt);
        if (TextUtils.isEmpty(textOff)) {
            textOff = DEF_TXT_OFF;
        }
        textOn = typedArray.getString(R.styleable.SwitchButton_switch_on_txt);
        if (TextUtils.isEmpty(textOn)) {
            textOn = DEF_TXT_ON;
        }
        textSize = typedArray.getDimensionPixelSize(R.styleable.SwitchButton_switch_txt_size, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEF_TXT_SIZE, context.getResources().getDisplayMetrics()));
        isOn = typedArray.getBoolean(R.styleable.SwitchButton_swit_on, true);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        int width = getWidth() *2/ 3;
        int height = getHeight();

        int top = 0;


        switch (currentState) {
            case ON:
                left = 0;
                break;
            case OFF:
                left = getWidth()/3;
                break;
        }

        Log.d("switch", left + "," + width + "");
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.color_btn));
        RectF rectF = new RectF();
        rectF.set(left, top, left + width, top + height);
        canvas.drawRoundRect(rectF, 20, 20, paint);

        paint.setColor(Color.BLACK);

        String txt = getCurrentTxt();

        float txtWidth = paint.measureText(txt);

        Paint.FontMetrics metrics = paint.getFontMetrics();

        float txtHeight = metrics.descent - metrics.ascent;

        float txtleft = width / 2 - txtWidth / 2 + left;

        float txtBottom = height / 2 + txtHeight / 2-metrics.descent;


        canvas.drawText(txt, txtleft, txtBottom, paint);
    }
}
