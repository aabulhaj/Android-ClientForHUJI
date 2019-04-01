package com.aabulhaj.hujiapp.fragments

import Session
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.aabulhaj.hujiapp.MenuTint
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.activities.CampusShuttleTimesActivity
import com.aabulhaj.hujiapp.activities.ContactsActivity
import com.aabulhaj.hujiapp.activities.LicensesActivity


class MoreFragment : PreferenceFragmentCompat(), RefreshableFragment {
    private val keyToMethodMap = HashMap<String, () -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        addPreferencesFromResource(R.xml.more)

        keyToMethodMap["contact_us"] = this::contactUs
        keyToMethodMap["licenses"] = start(LicensesActivity::class.java)
//        keyToMethodMap["academic_calendar"] = start(CalendarActivity::class.java)
        keyToMethodMap["contacts"] = start(ContactsActivity::class.java)
        keyToMethodMap["campus_shuttle_times"] = start(CampusShuttleTimesActivity::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.more_fragment_menu, menu)
        MenuTint.tint(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            if (context != null) {
                Session.logout(context!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key != null && keyToMethodMap.containsKey(preference.key)) {
            keyToMethodMap[preference.key]?.invoke()
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun contactUs() {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "message/rfc822"
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("aabulhaj1@gmail.com"))
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.send_mail)))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(activity, getString(R.string.no_email_accounts_error),
                    Toast.LENGTH_SHORT).show()
        }

    }

    private fun <T> start(target: Class<T>) = fun() = startActivity(Intent(context, target))


    override fun refresh() {}

    override fun getFragment(): Fragment {
        return this
    }
}
