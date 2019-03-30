package com.aabulhaj.hujiapp.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.widget.Toast
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.ContactsAdapter
import kotlinx.android.synthetic.main.activity_contacts.*


class ContactsActivity : ToolbarActivity() {
    private var mainTitles = ArrayList<String>()
    private var subTitles = ArrayList<String>()
    private var emails = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        supportActionBar.setTitle(R.string.contacts)
        supportActionBar.setDisplayHomeAsUpEnabled(true)

        loadData()

        val data = ArrayList<HashMap<String, String>>()
        for (i in 0 until mainTitles.size) {
            val datum = HashMap<String, String>(2)
            datum["title"] = mainTitles[i]
            datum["sub"] = subTitles[i]
            data.add(datum)
        }

        val adapter = ContactsAdapter(this, data, android.R.layout.simple_list_item_2,
                arrayOf("title", "sub"),
                intArrayOf(android.R.id.text1, android.R.id.text2))

        contactsListView?.adapter = adapter
        contactsListView?.setOnItemClickListener { _, _, pos, _ ->
            if (pos == 0 || pos == 5 || pos == 10) {
                return@setOnItemClickListener
            }
            if (pos < 11) {
                callPhoneNumber(pos)
            } else {
                sendEmail(pos)
            }
        }
    }

    private fun callPhoneNumber(position: Int) {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            return
        }

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:" + subTitles[position])
        try {
            startActivity(intent)
        } catch (activityException: ActivityNotFoundException) {

        }
    }

    private fun sendEmail(position: Int) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "message/rfc822"
        intent.putExtra(android.content.Intent.EXTRA_EMAIL,
                arrayOf(emails[mainTitles[position]]!!))
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.send_mail)))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this@ContactsActivity,
                    getString(R.string.no_email_accounts_error), Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadData() {
        mainTitles.add(getString(R.string.safety))
        mainTitles.add(getString(R.string.emond_safra))
        mainTitles.add(getString(R.string.mount_scopus))
        mainTitles.add(getString(R.string.ein_karem))
        mainTitles.add(getString(R.string.rehovot))
        mainTitles.add(getString(R.string.campus_Security))
        mainTitles.add(getString(R.string.emond_safra))
        mainTitles.add(getString(R.string.mount_scopus))
        mainTitles.add(getString(R.string.ein_karem))
        mainTitles.add(getString(R.string.rehovot))
        mainTitles.add(getString(R.string.prompt_email))
        mainTitles.add(getString(R.string.campus_Security))
        mainTitles.add(getString(R.string.humanities_Faculty))
        mainTitles.add(getString(R.string.social_Science_Faculty))
        mainTitles.add(getString(R.string.math_science_faculty))
        mainTitles.add(getString(R.string.school_of_ba))
        mainTitles.add(getString(R.string.school_of_social_work))
        mainTitles.add(getString(R.string.law_faculty))
        mainTitles.add(getString(R.string.medicine_Faculty))
        mainTitles.add(getString(R.string.dental_med_faculty))
        mainTitles.add(getString(R.string.faculty_of_agri))
        mainTitles.add(getString(R.string.education_school))
        mainTitles.add(getString(R.string.pre_academic_studies))
        mainTitles.add(getString(R.string.student_matters))
        mainTitles.add(getString(R.string.app_with_bagrut))
        mainTitles.add(getString(R.string.foreign_app))
        mainTitles.add(getString(R.string.huji_spokesman))
        mainTitles.add(getString(R.string.web_master))

        subTitles.add("")
        subTitles.add("02-6584030")
        subTitles.add("02-6585525")
        subTitles.add("02-6758051")
        subTitles.add("08-9489037")
        subTitles.add("")
        subTitles.add("02-6585000")
        subTitles.add("02-5883000")
        subTitles.add("02-6758060")
        subTitles.add("08-9489900")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")
        subTitles.add("")

        emails = HashMap()
        emails[getString(R.string.campus_Security)] = "security@savion.huji.ac.il"
        emails[getString(R.string.humanities_Faculty)] = "humanities_sa@savion.huji.ac.il"
        emails[getString(R.string.social_Science_Faculty)] = "socfaculty@savion.huji.ac.il"
        emails[getString(R.string.math_science_faculty)] = "science@savion.huji.ac.il"
        emails[getString(R.string.school_of_ba)] = "school@mscc.huji.ac.il"
        emails[getString(R.string.school_of_social_work)] = "social-work@savion.huji.ac.il"
        emails[getString(R.string.law_faculty)] = "law_sa@savion.huji.ac.il"
        emails[getString(R.string.medicine_Faculty)] = "medicine_sa@savion.huji.ac.il"
        emails[getString(R.string.dental_med_faculty)] = "dentistry_sa@savion.huji.ac.il"
        emails[getString(R.string.faculty_of_agri)] = "yadlin@agri.huji.ac.il"
        emails[getString(R.string.education_school)] = "education@savion.huji.ac.il"
        emails[getString(R.string.pre_academic_studies)] = "rismechina@savion.huji.ac.il"
        emails[getString(R.string.student_matters)] = "talmidim@savion.huji.ac.il"
        emails[getString(R.string.app_with_bagrut)] = "applicant@savion.huji.ac.il"
        emails[getString(R.string.foreign_app)] = "risinfo@savion.huji.ac.il"
        emails[getString(R.string.huji_spokesman)] = "tamartr@savion.huji.ac.il"
        emails[getString(R.string.web_master)] = "webmaster@savion.huji.ac.il"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }
}
