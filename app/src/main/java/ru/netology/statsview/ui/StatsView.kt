package ru.netology.statsview.ui

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import java.lang.Integer.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {
    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor())
            )
        }

    }

    private var progress: MutableList<Float> = mutableListOf()
    private var valueAnimator: List<ValueAnimator> = emptyList()

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    private fun update() {
        valueAnimator.forEach {
            it.removeAllListeners()
            it.cancel()
        }
        progress = (0..data.count()).map { 0f } as MutableList<Float>

        valueAnimator = (0..data.count()).map {
            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener { anim ->
                    progress[it] = anim.animatedValue as Float
                    //rotationProgress = progress * 360
                    invalidate()
                }
                duration = 500
                startDelay = 500
                interpolator = LinearInterpolator()
            }
        }

        AnimatorSet().apply {
            startDelay = 500
            playSequentially(valueAnimator)
        }.start()
    }

    var full: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)

        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }
        var startAngle = -90F

        paint.color = Color.LTGRAY
        canvas.drawCircle(center.x, center.y, radius, paint)

        data.forEachIndexed { index, datum ->
            val percent = datum / full
            val angle = percent * 360
            paint.color = colors.getOrElse(index) { generateRandomColor() }
             canvas.drawArc(oval, startAngle, angle*progress[index], false, paint)
            startAngle += angle
        }

        paint.color = colors[0]
        canvas.drawArc(oval,-90f,progress[0],false,paint)

        canvas.drawText(
            "%.2f%%".format(data.sum() / full * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())


}