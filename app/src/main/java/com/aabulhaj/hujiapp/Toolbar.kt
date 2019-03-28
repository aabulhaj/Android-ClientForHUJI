package com.aabulhaj.hujiapp

import Session
import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import com.aabulhaj.hujiapp.activities.ExtendSessionActivity
import kotlinx.android.synthetic.main.toolbar.view.*


class Toolbar : Toolbar, View.OnClickListener {
    private var animating = false
    private var currentExtendString = 0

    private val extendStrings = intArrayOf(R.string.cached_data, R.string.tap_to_refresh)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.toolbarStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val needsExtend = intent.getBooleanExtra(Session.EXTRA_NEEDS_EXTENDING, false)
            setShowsExtendButton(needsExtend)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        LocalBroadcastManager.getInstance(context)
                .registerReceiver(broadcastReceiver,
                        IntentFilter(Session.ACTION_SESSION_STATE_CHANGED))
        updateExtendState()
    }

    override fun onDetachedFromWindow() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
        super.onDetachedFromWindow()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        extendButton.setOnClickListener(this)
        updateExtendState()
    }

    private fun updateExtendState() {
        setShowsExtendButton(Session.sessionExpired)
    }

    private fun setShowsExtendButton(show: Boolean) {
        if ((extendButton.visibility == View.VISIBLE && show)
                || (extendButton.visibility == View.GONE && !show)) {
            return
        }

        if (extendButton != null) {
            extendButton.visibility = if (show) View.VISIBLE else View.GONE

            if (!animating && show) {
                startFadeAnimation()
            } else if (animating && !show) {
                animating = true
            }
        }
    }

    private fun doAnimation() {
        extendButton.animate()
                .setStartDelay(2000)
                .alpha(0f)
                .setListener(object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animator: Animator) {
                        currentExtendString = 1 - currentExtendString
                        extendButton.setText(extendStrings[currentExtendString])

                        extendButton.animate()
                                .setStartDelay(10)
                                .alpha(1f)
                                .setListener(object : SimpleAnimationListener() {
                                    override fun onAnimationEnd(animator: Animator) {
                                        if (animating) {
                                            animating = false
                                        } else {
                                            doAnimation()
                                        }
                                    }
                                }).start()
                    }
                }).start()
    }

    private fun startFadeAnimation() {
        if (animating) {
            return
        }

        doAnimation()
    }

    private open inner class SimpleAnimationListener : Animator.AnimatorListener {
        override fun onAnimationEnd(animator: Animator) {

        }

        override fun onAnimationRepeat(animator: Animator) {

        }

        override fun onAnimationStart(animator: Animator) {

        }

        override fun onAnimationCancel(animator: Animator) {

        }
    }

    override fun onClick(view: View?) {
        if (extendButton == view) {
            context.startActivity(Intent(context, ExtendSessionActivity::class.java))
        }
    }
}