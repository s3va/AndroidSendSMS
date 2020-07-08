package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.SEND_SMS
)


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

/*        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            button.isActivated=false
        } else
            proggy()*/

        // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
        button.isEnabled=false

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                if (it) {
                    Log.d("RequestPermission","********************************* it=$it *******************************")
                    Toast.makeText(applicationContext,"permission granted",Toast.LENGTH_LONG).show()
                    proggy()
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    Log.d("RequestPermission","******************************** else it=$it **************************")
                    Toast.makeText(applicationContext,"cannot send SMS without your permission",Toast.LENGTH_LONG).show()
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    Log.d("checkSelfPermission","PackageManager.PERMISSION_GRANTED")
                    proggy()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                    Toast.makeText(applicationContext,"Proggy needs your permission to send SMS",Toast.LENGTH_LONG).show()
                    Log.d("Rational","fucking Rationaly")
                    //showInContextUI()

                }
                else -> {
                    Log.d("runRequestPer","requestPermissionLauncher.launch( Manifest.permission.SEND_SMS)")
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.SEND_SMS)
                }
            }
        }


    }

    private fun proggy() {
        button.isEnabled = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
        button.setOnClickListener {

/*
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:${editTextTextPhoneNumber.text}")
            intent.putExtra("sms_body", editTextText.text.toString())
    //            if (intent.resolveActivity(packageManager) != null) {
    //                startActivity(intent)
    //            } else {
    //                Log.d("mactivity", "Can't resolve app for ACTION_SENDTO Intent")
    //            }
*/

            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                editTextTextPhoneNumber.text.toString(),
                null,
                editTextText.text.toString(),
                null, null
            )
        }
    }

/*    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }*/

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode The request code passed in [.requestPermissions].
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [android.content.pm.PackageManager.PERMISSION_GRANTED]
     * or [android.content.pm.PackageManager.PERMISSION_DENIED]. Never null.
     *
     * @see .requestPermissions
     */
/*    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/
}
