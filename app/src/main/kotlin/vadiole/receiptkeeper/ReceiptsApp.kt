package vadiole.receiptkeeper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReceiptsApp : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}