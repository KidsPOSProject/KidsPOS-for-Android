package info.nukoneko.cuc.android.kidspos.ui.launch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.ui.main.MainActivity

class LaunchActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val task = Runnable {
        if (isFinishing) return@Runnable
        MainActivity.createIntentWithClearTask(this@LaunchActivity)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(task, 2000)
    }

    public override fun onPause() {
        handler.removeCallbacks(task)
        super.onPause()
    }
}
