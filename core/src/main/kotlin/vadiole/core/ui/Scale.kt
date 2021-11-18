package vadiole.core.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.transition.TransitionValues
import androidx.transition.Visibility
import androidx.transition.doOnEnd

class Scale(private val scaleStart: Float = 1f, private val scaleEnd: Float = 0.9f) : Visibility() {

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        transitionValues.values[TRANSITION_SCALE_X] = transitionValues.view.scaleX
        transitionValues.values[TRANSITION_SCALE_Y] = transitionValues.view.scaleY
    }

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?,
    ): Animator {
        val startScaleX = getStartScaleValue(startValues, TRANSITION_SCALE_X, scaleEnd)
        val startScaleY = getStartScaleValue(startValues, TRANSITION_SCALE_Y, scaleEnd)

        return createScaleAnimation(
            view = view,
            startScaleX = if (startScaleX == scaleStart) scaleEnd else startScaleX,
            startScaleY = if (startScaleY == scaleStart) scaleEnd else startScaleY,
            endScaleX = scaleStart,
            endScaleY = scaleStart
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?,
    ): Animator {
        val startScaleX = getStartScaleValue(startValues, TRANSITION_SCALE_X, scaleStart)
        val startScaleY = getStartScaleValue(startValues, TRANSITION_SCALE_Y, scaleStart)

        return createScaleAnimation(
            view = view,
            startScaleX = startScaleX,
            startScaleY = startScaleY,
            endScaleX = scaleEnd,
            endScaleY = scaleEnd
        )
    }

    private fun createScaleAnimation(
        view: View,
        startScaleX: Float,
        startScaleY: Float,
        endScaleX: Float,
        endScaleY: Float,
    ): Animator {
        val animScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, startScaleX, endScaleX)
        val animScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, startScaleY, endScaleY)

        val animator = AnimatorSet()
        animator.playTogether(animScaleX, animScaleY)

        animator.doOnEnd {
            doOnEnd(view)
        }

        doOnEnd {
            doOnEnd(view)
        }

        return animator
    }

    private fun getStartScaleValue(scaleValue: TransitionValues?, propName: String, fallbackValue: Float): Float {
        return scaleValue?.values?.get(propName) as? Float ?: fallbackValue
    }

    private fun doOnEnd(view: View) {
        view.scaleX = scaleStart
        view.scaleY = scaleStart
    }

    companion object {
        private const val TRANSITION_SCALE_X = "transition.scale.scaleX"
        private const val TRANSITION_SCALE_Y = "transition.scale.scaleY"
    }
}