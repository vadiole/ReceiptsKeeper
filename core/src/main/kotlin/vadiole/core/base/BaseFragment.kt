package vadiole.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : BaseViewModel, V : ViewBinding> : Fragment() {

    abstract val viewModel: T

    private var _binding: V? = null
    val binding: V get() = _binding!!

    abstract fun onCreateBinding(inflater: LayoutInflater): V

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = onCreateBinding(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}