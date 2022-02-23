package io.s92.rewardbuttonapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/**
 * Allows user to control an analog joystick
 */
class JoystickView : View {
    private var _circleColor: Int = Color.RED
    private var _targetColor: Int = Color.GREEN

    private lateinit var circlePaint: Paint
    private lateinit var inactiveTargetPaint: Paint
    private lateinit var targetPaint: Paint

    private var _active: Boolean = false
    private var _mX: Float = 0.0F
    private var _mY: Float = 0.0F



    // Color of background circle that you can press
    var circleColor: Int
        get() = _circleColor
        set(value) {
            _circleColor = value
            invalidateTextPaintAndMeasurements()
        }

    // Color of the target which appears when you press
    var targetColor: Int
        get() = _targetColor
        set(value) {
            _targetColor = value
            invalidateTextPaintAndMeasurements()
        }


    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.JoystickView, defStyle, 0
        )

        _circleColor = a.getColor(
            R.styleable.JoystickView_circleColor,
            circleColor
        )

        _targetColor = a.getColor(
            R.styleable.JoystickView_targetColor,
            targetColor
        )

        if (a.hasValue(R.styleable.JoystickView_exampleDrawable)) {
            exampleDrawable = a.getDrawable(
                R.styleable.JoystickView_exampleDrawable
            )
            exampleDrawable?.callback = this
        }

        a.recycle()

        // Set up a default TextPaint object
        circlePaint = Paint().apply {
            color = circleColor
            strokeWidth = 10.0F
            style = Paint.Style.STROKE
        }

        inactiveTargetPaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 10.0F
            style = Paint.Style.STROKE
        }

        targetPaint = Paint().apply {
            color = targetColor
            strokeWidth = 10.0F
            style = Paint.Style.STROKE
        }

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
        circlePaint.let {
            it.color = circleColor
        }

        targetPaint.let {
            it.color = targetColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = (width / 2).toFloat()
        val centerY = (height / 2).toFloat()
        val radius = min(width, height) / 2 * 0.95F
        val targetRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 8.0F, resources.displayMetrics)

        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        canvas.drawLine(centerX, 0.0F, centerX, height.toFloat(), circlePaint);
        canvas.drawLine(0.0F, centerY, width.toFloat(), centerY, circlePaint);

        if (_active) {
            canvas.drawCircle(_mX, _mY, targetRadius, targetPaint)
        }
        else {
            canvas.drawCircle(centerX, centerY, targetRadius, inactiveTargetPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            _active = true
            _mX = event.x
            _mY = event.y
        }
        else if (event.action == MotionEvent.ACTION_UP) {
            _active = false
        }
        else if (event.action == MotionEvent.ACTION_MOVE) {
            _mX = event.x
            _mY = event.y
        }


        invalidate()
        return true
    }
}