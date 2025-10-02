package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
): View(context, attrs, defStyleAttr, defStyleRes) {

    private var playImg: Drawable? = null
    private var pauseImg: Drawable? = null
    private var curImg: Drawable? = null
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private val colorFilter: PorterDuffColorFilter = PorterDuffColorFilter(
        ContextCompat.getColor(context, R.color.audioplayer_play_btn_tint),
        PorterDuff.Mode.SRC_IN
    )

    private var isPlayingState: Boolean = false

    init {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playImg = getDrawable(R.styleable.PlaybackButtonView_playBtnImageResId)
                pauseImg = getDrawable(R.styleable.PlaybackButtonView_pauseBtnImageResId)
                curImg = playImg
            } finally {
                recycle()
            }
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        curImg?.let {
            it.setBounds(imageRect.left.toInt(), imageRect.top.toInt(), imageRect.right.toInt(), imageRect.bottom.toInt())
            it.colorFilter = colorFilter
            it.draw(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                changePlaybackBtnState()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun changePlaybackBtnState() {
        isPlayingState = !isPlayingState
        curImg = if (!isPlayingState) pauseImg else playImg
        invalidate()
    }

    fun setPlaybackBtnState(isPlaying: Boolean) {
        if (isPlayingState != isPlaying) changePlaybackBtnState()
    }

}