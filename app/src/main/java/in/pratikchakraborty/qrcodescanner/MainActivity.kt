package `in`.pratikchakraborty.qrcodescanner

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var codescanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        copy.setOnClickListener {
                val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", result_tv.text)
                clipboard.setPrimaryClip(clip)
            result_tv.text.toString().isEmpty()
            copy.setImageResource(R.drawable.ic_baseline_file_copy_24)
            Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
            Snackbar.make(it, "To start the scan again, you will have to click on the scan preview again", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setBackgroundTint(Color.parseColor("#4666ab"))
                .setAction("") {

                }
                .show()
            if(result_tv.text.isEmpty()) {
                copy.setImageResource(R.drawable.ic_baseline_content_copy_24)
                Toast.makeText(this, "Cannot Copy, No Text Available!", Toast.LENGTH_SHORT).show()
                Snackbar.make(it, "To copy, you have scan something first", Snackbar.LENGTH_LONG)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(Color.parseColor("#4666ab"))
                    .setAction("") {

                    }
                    .show()
            }
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.light_blue)
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 123)
        } else {
            startScanning()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun startScanning() {
        val scannerView:CodeScannerView = findViewById(R.id.scanner_view)
        codescanner = CodeScanner(this, scannerView)
        codescanner.camera = CodeScanner.CAMERA_BACK
        codescanner.formats = CodeScanner.ALL_FORMATS
        codescanner.autoFocusMode = AutoFocusMode.SAFE
        codescanner.scanMode = ScanMode.SINGLE
        codescanner.isAutoFocusEnabled = true
        codescanner.isFlashEnabled = true
        codescanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                //Toast.makeText(this, "Scan Result: ${it.text}", Toast.LENGTH_SHORT).show()
                result_tv.text = "${it.text}"

            }
        }

        codescanner.errorCallback = ErrorCallback {
            runOnUiThread {
                //Toast.makeText(this, "Camera Initialization Error: ${it.message}", Toast.LENGTH_SHORT).show()
                result_tv.text = "${it.message}"
                result_tv.setTextColor(R.color.purple_200)
            }
        }
        scannerView.setOnClickListener {
            codescanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 123) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                startScanning()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(::codescanner.isInitialized) {
            codescanner.startPreview()
        }
    }

    override fun onPause() {
        if(::codescanner.isInitialized) {
            codescanner.releaseResources()
        }
        super.onPause()
    }
}