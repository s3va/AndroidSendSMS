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
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


//private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.SEND_SMS,
    Manifest.permission.READ_PHONE_STATE
)

const val SENT = "sent"
const val DELIVERED = "delivered"

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
                    ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    val falsePerm = StringBuilder()
                    for (m in it.entries) {
                        if (m.value) {
                            Log.d(
                                "SMS_RequestPermission",
                                "********************************* ${m.key} is ${m.value} *******************************"
                            )
                            Toast.makeText(
                                applicationContext,
                                "${m.key} permission granted",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Permission is granted. Continue the action or workflow in your
                            // app.
                        } else {
                            Log.d(
                                "SMS_RequestPermission",
                                "******************************** else ${m.key} is ${m.value} **************************"
                            )
                            falsePerm.append("${m.key} ")
/*                            Toast.makeText(
                                applicationContext,
                                "cannot send SMS without your permission ${m.key}",
                                Toast.LENGTH_SHORT
                            ).show()*/
                            // Explain to the user that the feature is unavailable because the
                            // features requires a permission that the user has denied. At the
                            // same time, respect the user's decision. Don't link to system
                            // settings in an effort to convince the user to change their
                            // decision.
                        }
                    }
                    if (allPermissionsGranted()) {
                        proggy()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "cannot send SMS without your permission to $falsePerm",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            when {
                /*ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED*/
                allPermissionsGranted() -> {
                    // You can use the API that requires the permission.
                    Log.d("SMS_checkSelfPermission", "PackageManager.PERMISSION_GRANTED")
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
                    Log.d("SMS_Rational", "fucking Rationaly")
                    //showInContextUI()
                    requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)

                }
                else -> {
                    Log.d(
                        "SMS_runRequestPer",
                        "requestPermissionLauncher.launch( Manifest.permission.SEND_SMS)"
                    )
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
                }
            }
        } else
            proggy()


    }

    private val delivRecObj = object : BroadcastReceiver() {
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

            var info = "Delivery information: "

            when (resultCode) {
                Activity.RESULT_OK -> {
                    info += " delivered "
                    textViewRecieved.setTextColor(Color.GREEN)
                }
                Activity.RESULT_CANCELED -> {
                    info += " not delivered "
                    textViewRecieved.setTextColor(Color.RED)
                }
            }
            textViewRecieved.append(info)
            Toast.makeText(baseContext, info, Toast.LENGTH_SHORT).show()

            /*Toast.makeText(
                applicationContext, "Deliverd",
                Toast.LENGTH_LONG
            ).show()
            textViewRecieved.text="Delivered"
            textViewRecieved.setTextColor(Color.GREEN)*/
        }
    }

    private val sentRecObj = object : BroadcastReceiver() {
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
                    result = " Transmission successful "
                    textViewSent.setTextColor(Color.GREEN)
                }
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                    result = " Transmission failed "
                    textViewSent.setTextColor(Color.RED)
                }
                SmsManager.RESULT_ERROR_RADIO_OFF -> {
                    result = " Radio off "
                    textViewSent.setTextColor(Color.RED)
                }
                SmsManager.RESULT_ERROR_NULL_PDU -> {
                    result = " No PDU defined "
                    textViewSent.setTextColor(Color.RED)
                }
                SmsManager.RESULT_ERROR_NO_SERVICE -> {
                    result = " No service "
                    textViewSent.setTextColor(Color.RED)
                }
            }
            textViewSent.append(result)
            Toast.makeText(
                applicationContext, result,
                Toast.LENGTH_LONG
            ).show()

        }
    }

    private lateinit var subscriptionInfoList: List<SubscriptionInfo>
    private lateinit var smsManager: SmsManager
    private fun proggy() {
        val listSIM: ArrayList<String> = arrayListOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                subscriptionInfoList =
                    (getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager).activeSubscriptionInfoList

                if (subscriptionInfoList.isNullOrEmpty()) {
                    spinner.visibility = View.GONE
                    smsManager = SmsManager.getDefault()
                } else {
                    subscriptionInfoList.forEach {
                        listSIM.add("${it.subscriptionId}: ${it.displayName}")
                    }
                    val spiAdapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_item, listSIM)
                    spiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = spiAdapter
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            Log.d("SMS_SubscrServ", "$subscriptionInfoList")
        }

        Log.d("SMS_simList", "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq $listSIM")
        //val sentIntent = Intent(SENT)
        /*Create Pending Intents*/
/*        val sentPI = PendingIntent.getBroadcast(
            this, 0, Intent(SENT),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //val deliveryIntent = Intent(DELIVERED)
        val deliverPI = PendingIntent.getBroadcast(
            this, 0, Intent(DELIVERED),
            PendingIntent.FLAG_UPDATE_CURRENT
        )*/

        /* Register for SMS send action */
        registerReceiver(sentRecObj, IntentFilter(SENT))
        registerReceiver(delivRecObj, IntentFilter(DELIVERED))

        button.isEnabled = true
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

            val smsText = editTextText.text.toString()
            val phoneNumber = editTextTextPhoneNumber.text.toString()
            if (smsText.isBlank()) {
                Toast.makeText(
                    this,
                    "Empty text of message. Cannot send empty sms",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (listSIM.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    smsManager = SmsManager.getSmsManagerForSubscriptionId(
                        subscriptionInfoList[spinner.selectedItemId.toInt()]
                            .subscriptionId
                    )
                    Log.d(
                        "SMS_isIni",
                        "spinner.selectedItemId.toInt()=${spinner.selectedItemId.toInt()} subscriptionInfoList[spinner.selectedItemId.toInt()]\n" +
                                "                        .subscriptionId: ${subscriptionInfoList[spinner.selectedItemId.toInt()].subscriptionId}"
                    )
                }

            } else {
                smsManager= SmsManager.getDefault()
            }
            val smsArray = smsManager.divideMessage(smsText)
            val sentPIlist = ArrayList<PendingIntent>()
            val recvPIlist = ArrayList<PendingIntent>()
            smsArray.forEach { _ ->
                sentPIlist.add(
                    PendingIntent.getBroadcast(
                        this, 0, Intent(SENT),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                recvPIlist.add(
                    PendingIntent.getBroadcast(
                        this, 0, Intent(DELIVERED),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            }

            if ((!phoneNumber.isBlank()) and phoneNumber.matches("^\\+?[0-9][0-9 \\-()]*".toRegex())) {
                smsManager.sendMultipartTextMessage(
                    phoneNumber,
                    null,
                    smsArray,
                    sentPIlist,
                    recvPIlist
                )
                /*if (smsManager == null)
                    Toast.makeText(this, getString(R.string.no_smsmanager), Toast.LENGTH_LONG)
                        .show()

                smsManager?.sendTextMessage(
                    phoneNumber,
                    null,
                    smsText,
                    sentPI, deliverPI
                )
                smsManager?.divideMessage("qweqwe")*/
            } else {
                Toast.makeText(this, getString(R.string.wrong_phone_number), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(sentRecObj)
            unregisterReceiver(delivRecObj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
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
     * @param #requestCode The request code passed in [.requestPermissions].
     * @param #permissions The requested permissions. Never null.
     * @param #grantResults The grant results for the corresponding permissions
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
