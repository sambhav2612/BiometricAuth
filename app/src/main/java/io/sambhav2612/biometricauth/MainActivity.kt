package io.sambhav2612.biometricauth

import android.Manifest.permission.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE: Int = 200
    lateinit var layout: View

    /**
     * user data from authentication page
     */

    lateinit var name: String
    lateinit var photoUrl: Uri
    lateinit var auth_email: String
    lateinit var emailVerified: String
    lateinit var uid: String

    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        name = intent.getStringExtra("userName").toString()
        photoUrl = Uri.parse(intent.getStringExtra("userPhoto"))
        auth_email = intent.getStringExtra("userEmail").toString()
        emailVerified = intent.getStringExtra("userEmailVerificationStatus").toString()
        uid = intent.getStringExtra("userId").toString()

        layout = findViewById(R.id.main_layout)
        mDrawerLayout = findViewById(R.id.content)

        setSupportActionBar(findViewById(R.id.toolbar))

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val navigationView: NavigationView = findViewById(R.id.navigation_bar)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            mDrawerLayout.closeDrawers()
            true
        }

        AppCenter.start(
            application, "0589731f-55a3-45a4-866f-d0e29ae0fd86",
            Analytics::class.java, Crashes::class.java
        )
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Runtime Permission Handlers
     */

    override fun onStart() {
        super.onStart()

        if (!checkPermission())
            requestPermission()
    }

    private fun checkPermission(): Boolean {
        val result1: Int = ContextCompat.checkSelfPermission(applicationContext, ACCESS_FINE_LOCATION)
        val result2: Int = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        val result3: Int = ContextCompat.checkSelfPermission(applicationContext, READ_CONTACTS)
        val result4: Int = ContextCompat.checkSelfPermission(applicationContext, INTERNET)
        val result5: Int = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)

        return ((result1 == PackageManager.PERMISSION_GRANTED) &&
                (result2 == PackageManager.PERMISSION_GRANTED) &&
                (result3 == PackageManager.PERMISSION_GRANTED) &&
                (result4 == PackageManager.PERMISSION_GRANTED) &&
                (result5 == PackageManager.PERMISSION_GRANTED))
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                ACCESS_FINE_LOCATION,
                CAMERA,
                READ_CONTACTS,
                INTERNET,
                WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val contactsAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                val internetAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED
                val storageAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED

                if (locationAccepted && cameraAccepted && contactsAccepted && internetAccepted && storageAccepted)
                    Snackbar.make(
                        layout,
                        "Permission Granted, Now you can access location data and camera.",
                        Snackbar.LENGTH_LONG
                    ).show()
                else {
                    Snackbar.make(
                        layout,
                        "Permission Denied, You cannot access location data and camera.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)
                            || shouldShowRequestPermissionRationale(CAMERA)
                            || shouldShowRequestPermissionRationale(READ_CONTACTS)
                            || shouldShowRequestPermissionRationale(INTERNET)
                            || shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)
                        ) {
                            showMessageOKCancel("You need to allow access to all the permissions",
                                DialogInterface.OnClickListener { dialog, which ->
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                            arrayOf(
                                                ACCESS_FINE_LOCATION,
                                                CAMERA,
                                                READ_CONTACTS,
                                                INTERNET,
                                                WRITE_EXTERNAL_STORAGE
                                            ),
                                            PERMISSION_REQUEST_CODE
                                        )
                                    }
                                })
                            return
                        }
                    }
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    /**
     * Intent Handlers
     */

    fun enrollCandidate(view: View) {
        val enrollIntent = Intent(this, EnrollCandidate::class.java)
        startActivity(enrollIntent)
    }

    fun verifyCandidate(view: View) {
        val verifyIntent = Intent(this, VerifyCandidate::class.java)
        startActivity(verifyIntent)
    }

    fun shareApp(view: View) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, R.string.shareAppText)
            type = "text/plain"
        }

        if (shareIntent.resolveActivity(packageManager) != null) {
            //Toast.makeText(applicationContext, R.string.shareToast, Toast.LENGTH_SHORT).show()
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.shareToast)))
        }
    }
}
