package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


/*private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.SEND_SMS
)*/


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

        button.isEnabled = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {
                    if (it) {
                        Log.d(
                            "RequestPermission",
                            "********************************* it=$it *******************************"
                        )
                        Toast.makeText(applicationContext, "permission granted", Toast.LENGTH_LONG)
                            .show()
                        proggy()
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        Log.d(
                            "RequestPermission",
                            "******************************** else it=$it **************************"
                        )
                        Toast.makeText(
                            applicationContext,
                            "cannot send SMS without your permission",
                            Toast.LENGTH_LONG
                        ).show()
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }
                }


            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    Log.d("checkSelfPermission", "PackageManager.PERMISSION_GRANTED")
                    proggy()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.
                    Toast.makeText(
                        applicationContext,
                        "Proggy needs your permission to send SMS",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("Rational", "fucking Rationaly")
                    //showInContextUI()

                }
                else -> {
                    Log.d(
                        "runRequestPer",
                        "requestPermissionLauncher.launch( Manifest.permission.SEND_SMS)"
                    )
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.SEND_SMS
                    )
                }
            }
        } else
            proggy()


    }

    private fun proggy() {
        button.isEnabled = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
        button.setOnClickListener {

/*            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:${editTextTextPhoneNumber.text}")
            intent.putExtra("sms_body", editTextText.text.toString())
    //            if (intent.resolveActivity(packageManager) != null) {
    //                startActivity(intent)
    //            } else {
    //                Log.d("mactivity", "Can't resolve app for ACTION_SENDTO Intent")
    //            }*/

            textViewRecieved.text = ""
            textViewSent.text = ""

            val SENT = "sent"
            val DELIVERED = "delivered"
            val sentIntent = Intent(SENT)
            /*Create Pending Intents*/
            val sentPI = PendingIntent.getBroadcast(
                applicationContext, 0, sentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val deliveryIntent = Intent(DELIVERED)
            val deliverPI = PendingIntent.getBroadcast(
                applicationContext, 0, deliveryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            /* Register for SMS send action */
            registerReceiver(object : BroadcastReceiver() {
                /**
                 * This method is called when the BroadcastReceiver is receiving an Intent
                 * broadcast.  During this time you can use the other methods on
                 * BroadcastReceiver to view/modify the current result values.  This method
                 * is always called within the main thread of its process, unless you
                 * explicitly asked for it to be scheduled on a different thread using
                 * [android.content.Context.registerReceiver]. When it runs on the main
                 * thread you should
                 * never perform long-running operations in it (there is a timeout of
                 * 10 seconds that the system allows before considering the receiver to
                 * be blocked and a candidate to be killed). You cannot launch a popup dialog
                 * in your implementation of onReceive().
                 *
                 *
                 * **If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
                 * then the object is no longer alive after returning from this
                 * function.** This means you should not perform any operations that
                 * return a result to you asynchronously. If you need to perform any follow up
                 * background work, schedule a [android.app.job.JobService] with
                 * [android.app.job.JobScheduler].
                 *
                 * If you wish to interact with a service that is already running and previously
                 * bound using [bindService()][android.content.Context.bindService],
                 * you can use [.peekService].
                 *
                 *
                 * The Intent filters used in [android.content.Context.registerReceiver]
                 * and in application manifests are *not* guaranteed to be exclusive. They
                 * are hints to the operating system about how to find suitable recipients. It is
                 * possible for senders to force delivery to specific recipients, bypassing filter
                 * resolution.  For this reason, [onReceive()][.onReceive]
                 * implementations should respond only to known actions, ignoring any unexpected
                 * Intents that they may receive.
                 *
                 * @param context The Context in which the receiver is running.
                 * @param intent The Intent being received.
                 */
                override fun onReceive(context: Context?, intent: Intent?) {
                    var result = ""
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            result = "Transmission successful"
                            textViewSent.text = result
                            textViewSent.setTextColor(Color.GREEN)
                        }
                        SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                            result = "Transmission failed"
                            textViewSent.text = result
                            textViewSent.setTextColor(Color.RED)
                        }
                        SmsManager.RESULT_ERROR_RADIO_OFF -> {
                            result = "Radio off"
                            textViewSent.text = result
                            textViewSent.setTextColor(Color.RED)
                        }
                        SmsManager.RESULT_ERROR_NULL_PDU -> {
                            result = "No PDU defined"
                            textViewSent.text = result
                            textViewSent.setTextColor(Color.RED)
                        }
                        SmsManager.RESULT_ERROR_NO_SERVICE -> {
                            result = "No service"
                            textViewSent.text=result
                            textViewSent.setTextColor(Color.RED)
                        }
                    }
                    Toast.makeText(
                        applicationContext, result,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }, IntentFilter(SENT))

            registerReceiver(object : BroadcastReceiver() {
                /**
                 * This method is called when the BroadcastReceiver is receiving an Intent
                 * broadcast.  During this time you can use the other methods on
                 * BroadcastReceiver to view/modify the current result values.  This method
                 * is always called within the main thread of its process, unless you
                 * explicitly asked for it to be scheduled on a different thread using
                 * [android.content.Context.registerReceiver]. When it runs on the main
                 * thread you should
                 * never perform long-running operations in it (there is a timeout of
                 * 10 seconds that the system allows before considering the receiver to
                 * be blocked and a candidate to be killed). You cannot launch a popup dialog
                 * in your implementation of onReceive().
                 *
                 *
                 * **If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
                 * then the object is no longer alive after returning from this
                 * function.** This means you should not perform any operations that
                 * return a result to you asynchronously. If you need to perform any follow up
                 * background work, schedule a [android.app.job.JobService] with
                 * [android.app.job.JobScheduler].
                 *
                 * If you wish to interact with a service that is already running and previously
                 * bound using [bindService()][android.content.Context.bindService],
                 * you can use [.peekService].
                 *
                 *
                 * The Intent filters used in [android.content.Context.registerReceiver]
                 * and in application manifests are *not* guaranteed to be exclusive. They
                 * are hints to the operating system about how to find suitable recipients. It is
                 * possible for senders to force delivery to specific recipients, bypassing filter
                 * resolution.  For this reason, [onReceive()][.onReceive]
                 * implementations should respond only to known actions, ignoring any unexpected
                 * Intents that they may receive.
                 *
                 * @param context The Context in which the receiver is running.
                 * @param intent The Intent being received.
                 */
                override fun onReceive(context: Context?, intent: Intent?) {
                    Toast.makeText(
                        applicationContext, "Deliverd",
                        Toast.LENGTH_LONG
                    ).show()
                    textViewRecieved.text="Delivered"
                    textViewRecieved.setTextColor(Color.GREEN)
                }
            }, IntentFilter(DELIVERED))


            val smsText = editTextText.text.toString()
            val phoneNumber = editTextTextPhoneNumber.text.toString()
            if (smsText.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    "Empty text of message. Cannot send empty sms",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if ((!phoneNumber.isNullOrBlank()) and phoneNumber.matches("^\\+?[0-9][0-9 \\-()]*".toRegex())) {
                val smsManager: SmsManager? = SmsManager.getDefault()
                if (smsManager == null)
                    Toast.makeText(this, getString(R.string.no_smsmanager), Toast.LENGTH_LONG)
                        .show()

                smsManager?.sendTextMessage(
                    editTextTextPhoneNumber.text.toString(),
                    null,
                    editTextText.text.toString(),
                    sentPI, deliverPI
                )
                smsManager?.divideMessage("qweqwe")
            } else {
                Toast.makeText(this, getString(R.string.wrong_phone_number), Toast.LENGTH_LONG)
                    .show()
            }
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
