package com.aabulhaj.hujiapp.fragments

import Session
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.aabulhaj.hujiapp.MenuTint
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.getAboutMeURL
import kotlinx.android.synthetic.main.fragment_about_me.*
import kotlinx.android.synthetic.main.fragment_about_me.view.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call

class AboutMeFragment : Fragment(), RefreshableFragment, View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_about_me, container, false)

        if (resources.getBoolean(R.bool.is_rtl)) {
            v.aboutMeEmail.gravity = Gravity.END
            v.aboutMeAddress.gravity = Gravity.START
        } else {
            v.aboutMeAddress.gravity = Gravity.LEFT
        }

        onRefresh()

        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_about_me, menu)
        MenuTint.tint(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.logout) {
            Session.logout(activity!!)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(p0: View?) {

    }

    private fun onRefresh() {
        Session.callRequest(fun() = Session.hujiApiClient.getResponseBody(getAboutMeURL()),
                activity!!, object : StringCallback {
            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                val doc = Jsoup.parse(responseBody)

                val elements = doc.getElementsByAttributeValue("border", "0")[8].allElements

                val name = doc.getElementsByClass("gen_title20W").first().text().split(":").last()
                val email = elements[3].text()
                val address = elements[10].text()
                val mobileNumParts = elements[16].text().split("-")

                var mobileNum = ""
                if (mobileNumParts.size == 2) {
                    mobileNum = (mobileNumParts[1] + mobileNumParts[0]).trim()
                } else if (mobileNumParts.size == 1) {
                    mobileNum = mobileNumParts[0].trim()
                }

                val hasPhoneNum = !elements[17].text().trim().isEmpty()
                var phoneNum = ""
                if (hasPhoneNum) {
                    val phoneNumParts = elements[17].text().split("-")

                    if (phoneNumParts.size == 2) {
                        phoneNum = (phoneNumParts[1] + phoneNumParts[0]).trim()
                    } else if (phoneNumParts.size == 1) {
                        phoneNum = phoneNumParts[0].trim()
                    }
                }

                activity?.runOnUiThread {
                    aboutMeName.text = name
                    aboutMeAddress.text = address
                    aboutMeEmail.text = email
                    aboutMeMobileNumber.text = mobileNum

                    if (hasPhoneNum) {
                        aboutMePhoneNum.text = phoneNum
                        aboutMePhoneNum.visibility = View.VISIBLE
                        aboutMePhoneLabel.visibility = View.VISIBLE
                    } else {
                        aboutMePhoneNum.visibility = View.INVISIBLE
                        aboutMePhoneLabel.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {}
        })
    }

    override fun getFragment(): Fragment {
        return this
    }

    override fun refresh() {
    }
}
