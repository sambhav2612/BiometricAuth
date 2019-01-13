package io.sambhav2612.biometricauth

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*

class EnrollCandidate : AppCompatActivity() {

    lateinit var btnCapturePicture: Button
    lateinit var btnGetDob: EditText
    lateinit var resultImage: ImageView
    private val CAMERA_REQUEST = 1888

    val myCalendar = Calendar.getInstance()

    var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_candidate)

        resultImage = findViewById(R.id.result)
        btnCapturePicture = findViewById(R.id.openCameraButton)
        btnGetDob = findViewById(R.id.dobButton)

        btnCapturePicture.setOnClickListener {
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }

    fun getDob(view: View) {
        DatePickerDialog(
            this,
            date,
            myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    private fun updateLabel() {
        val myFormat = "MM/dd/yy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        btnGetDob.setText(sdf.format(myCalendar.time))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST) {
            val photo = data?.extras?.get("data") as Bitmap
            resultImage.setImageBitmap(photo)
            resultImage.visibility = View.VISIBLE
        }
    }
}
