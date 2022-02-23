package io.s92.rewardbuttonapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * Allows user to control an analog joystick
 */
class JoystickView : View {

    private var _circleColor: Int = Color.RED
    private var _targetColor: Int = Color.GREEN

    private lateinit var circlePaint: Paint

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
        }

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    private fun invalidateTextPaintAndMeasurements() {
//        textPaint.let {
//            it.textSize = exampleDimension
//            it.color = exampleColor
//            textWidth = it.measureText(exampleString)
//            textHeight = it.fontMetrics.bottom
//        }

        circlePaint.let {
            it.color = circleColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), width.toFloat(), circlePaint);


        // Draw the example drawable on top of the text.
        exampleDrawable?.let {
            it.setBounds(
                paddingLeft, paddingTop,
                paddingLeft + contentWidth, paddingTop + contentHeight
            )
            it.draw(canvas)
        }
    }
}