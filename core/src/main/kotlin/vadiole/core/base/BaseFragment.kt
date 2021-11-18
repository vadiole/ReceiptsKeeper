package vadiole.core.base

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionSet
import androidx.viewbinding.ViewBinding
import vadiole.core.ui.Scale

abstract class BaseFragment<T : BaseViewModel, V : ViewBinding> : Fragment() {

    abstract val viewModel: T

    private var _binding: V? = null
    val binding: V get() = _binding!!

    private var _navigator: Navigator? = null
    val navigator: Navigator get() = _navigator!!

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = onBackPressed()
            if (!isEnabled) requireActivity().onBackPressed()
        }
    }

    abstract fun onCreateBinding(inflater: LayoutInflater): V

    open fun onBackPressed(): Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = onCreateBinding(inflater)
        enterTransition = Slide(Gravity.END).setInterpolator(FastOutSlowInInterpolator())
        exitTransition = TransitionSet()
            .addTransition(Scale().addTarget(binding.root)).setInterpolator(FastOutSlowInInterpolator())
            .addTransition(Fade().addTarget(binding.root)).setInterpolator(FastOutSlowInInterpolator())

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _navigator = context as Navigator
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }
}