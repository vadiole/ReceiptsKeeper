package vadiole.core.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Extension to observe flow like livedata
 */
fun <T> Flow<T>.observe(
    owner: LifecycleOwner,
    action: suspend (T) -> Unit,
): Job = flowWithLifecycle(owner.lifecycle, Lifecycle.State.STARTED)
    .onEach {
        withContext(Dispatchers.Main) {
            action.invoke(it)
        }
    }
    .launchIn(owner.lifecycleScope)