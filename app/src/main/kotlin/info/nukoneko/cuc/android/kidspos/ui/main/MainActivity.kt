package info.nukoneko.cuc.android.kidspos.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationView
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.orhanobut.logger.Logger
import info.nukoneko.cuc.android.kidspos.ProjectSettings
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.databinding.ActivityMainBinding
import info.nukoneko.cuc.android.kidspos.ui.common.BaseBarcodeReadableActivity
import info.nukoneko.cuc.android.kidspos.ui.common.ErrorDialogFragment
import info.nukoneko.cuc.android.kidspos.ui.main.itemlist.ItemListFragment
import info.nukoneko.cuc.android.kidspos.ui.main.storelist.StoreListDialogFragment
import info.nukoneko.cuc.android.kidspos.ui.setting.SettingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class MainActivity : BaseBarcodeReadableActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null
    private var isCameraModeEnabled = false
    private val longPressHandler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null

    private val listener = object : MainViewModel.Listener {
        override fun onShouldShowMessage(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun onNotReachableServer(errorMessage: String) {
            showNotReachableErrorDialog(errorMessage)
        }

        override fun onShouldChangeTitleSuffix(titleSuffix: String) {
            binding.toolbar.title = "${getString(R.string.app_name)} $titleSuffix"
        }
    }
    private val myViewModel: MainViewModel by inject()
    private val navigationListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.setting -> SettingActivity.createIntent(this@MainActivity)
            R.id.change_store -> {
                val fragment = StoreListDialogFragment.newInstance()
                fragment.isCancelable = false
                fragment.show(supportFragmentManager, "changeStore")
            }

            R.id.input_dummy_item -> onBarcodeInput("1234567890")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(navigationListener)
        binding.viewModel = myViewModel.also {
            it.listener = listener
        }
        binding.lifecycleOwner = this
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ItemListFragment.newInstance(), "itemList")
            .commit()

        setupCharacterImageLongPress()
    }

    override fun onStart() {
        super.onStart()
        myViewModel.onStart()
    }

    override fun onStop() {
        myViewModel.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        longPressRunnable?.let { longPressHandler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()
        myViewModel.onResume()
        @Suppress("KotlinConstantConditions")
        binding.navView.menu.setGroupVisible(R.id.beta_test, ProjectSettings.DEMO_MODE)
    }

    override fun onBarcodeInput(barcode: String) {
        myViewModel.onBarcodeInput(barcode)
    }

    private fun showNotReachableErrorDialog(errorMessage: String) {
        launch {
            val result = ErrorDialogFragment.showWithSuspend(
                supportFragmentManager,
                errorMessage
            )
            when (result) {
                ErrorDialogFragment.DialogResult.OK -> SettingActivity.createIntent(this@MainActivity)
            }
        }
    }

    private fun setupCharacterImageLongPress() {
        val headerView = binding.navView.getHeaderView(0)
        val characterImage = headerView.findViewById<ImageView>(R.id.character_image)

        characterImage.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    longPressRunnable = Runnable {
                        toggleCameraMode()
                    }
                    longPressHandler.postDelayed(longPressRunnable!!, LONG_PRESS_DURATION)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    longPressRunnable?.let { longPressHandler.removeCallbacks(it) }
                    true
                }
                else -> false
            }
        }
    }

    private fun toggleCameraMode() {
        if (isCameraModeEnabled) {
            disableCameraMode()
        } else {
            enableCameraMode()
        }
    }

    private fun enableCameraMode() {
        if (checkCameraPermission()) {
            isCameraModeEnabled = true
            binding.cameraPreview.visibility = View.VISIBLE
            binding.cameraPreview.layoutParams =
                (binding.cameraPreview.layoutParams as android.widget.LinearLayout.LayoutParams).apply {
                    width = 0
                    height = android.widget.LinearLayout.LayoutParams.MATCH_PARENT
                    weight = 0.33f
                }
            binding.fragmentContainer.layoutParams =
                (binding.fragmentContainer.layoutParams as android.widget.LinearLayout.LayoutParams).apply {
                    width = 0
                    height = android.widget.LinearLayout.LayoutParams.MATCH_PARENT
                    weight = 0.67f
                }
            startCamera()
            Toast.makeText(this, R.string.camera_mode_enabled, Toast.LENGTH_SHORT).show()
            Logger.i("Camera mode enabled")
        } else {
            requestCameraPermission()
        }
    }

    private fun disableCameraMode() {
        isCameraModeEnabled = false
        binding.cameraPreview.visibility = View.GONE
        binding.cameraPreview.layoutParams =
            (binding.cameraPreview.layoutParams as android.widget.LinearLayout.LayoutParams).apply {
                width = 0
                height = android.widget.LinearLayout.LayoutParams.MATCH_PARENT
                weight = 0f
            }
        binding.fragmentContainer.layoutParams =
            (binding.fragmentContainer.layoutParams as android.widget.LinearLayout.LayoutParams).apply {
                width = 0
                height = android.widget.LinearLayout.LayoutParams.MATCH_PARENT
                weight = 1f
            }
        camera = null
        Toast.makeText(this, R.string.camera_mode_disabled, Toast.LENGTH_SHORT).show()
        Logger.i("Camera mode disabled")
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableCameraMode()
            } else {
                Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        Logger.i("Barcode detected from camera: $barcode")
                        runOnUiThread {
                            onBarcodeInput(barcode)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (e: Exception) {
                Logger.e(e, "Camera binding failed")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private class BarcodeAnalyzer(
        private val onBarcodeDetected: (String) -> Unit
    ) : ImageAnalysis.Analyzer {
        private val reader = MultiFormatReader()
        private var lastScannedTime = 0L
        private val scanThrottleMs = 2000L

        @androidx.camera.core.ExperimentalGetImage
        override fun analyze(imageProxy: androidx.camera.core.ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastScannedTime < scanThrottleMs) {
                    imageProxy.close()
                    return
                }

                try {
                    val buffer = mediaImage.planes[0].buffer
                    val data = ByteArray(buffer.remaining())
                    buffer.get(data)

                    val source = PlanarYUVLuminanceSource(
                        data,
                        mediaImage.width,
                        mediaImage.height,
                        0,
                        0,
                        mediaImage.width,
                        mediaImage.height,
                        false
                    )

                    val bitmap = BinaryBitmap(HybridBinarizer(source))
                    val result = reader.decode(bitmap)

                    result.text?.let { barcode ->
                        lastScannedTime = currentTime
                        Logger.d("ZXing barcode detected: $barcode")
                        onBarcodeDetected(barcode)
                    }
                } catch (e: Exception) {
                    // バーコードが見つからない場合は正常（スキャン中）
                } finally {
                    imageProxy.close()
                }
            } else {
                imageProxy.close()
            }
        }
    }

    companion object {
        private const val LONG_PRESS_DURATION = 5000L
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001

        fun createIntentWithClearTask(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
