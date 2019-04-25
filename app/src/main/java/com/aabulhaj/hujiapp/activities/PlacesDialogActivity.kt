package com.aabulhaj.hujiapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import com.aabulhaj.hujiapp.Cache
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.PlacesDialogAdapter
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_places_dialog.*

const val PLACES_CHECKED_POS_TAGS = "checkedpositions"
private const val PLACES_CACHE = "place"

class PlacesDialogActivity : Activity() {
    private var placesDialogAdapter: PlacesDialogAdapter<String>? = null

    private val placesArray: ArrayList<String>
        get() {
            val places = ArrayList<String>()
            places.add(getString(R.string.buildings))
            places.add(getString(R.string.computer_labs))
            places.add(getString(R.string.libraries))
            places.add(getString(R.string.resturants))
            places.add(getString(R.string.cafes))
            places.add(getString(R.string.sport_centers))
            return places
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(android.R.style.Theme_Holo_Dialog)
        } else {
            setTheme(android.R.style.Theme_Holo_Light_Dialog)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_dialog)

        title = getString(R.string.places)

        var checked: BooleanArray? = getCachedArray(this)
        if (checked == null) {
            checked = BooleanArray(6)
        }

        placesDialogAdapter = PlacesDialogAdapter(this)
        placesDialogAdapter?.addAllWithBoolean(placesArray, checked)
        placesDialogListView.adapter = placesDialogAdapter

        placesDoneButton.setOnClickListener {
            val checkedPos = placesDialogAdapter!!.getCheckedItemPositions()
            Cache.cacheObject(this, checkedPos, object : TypeToken<BooleanArray>() {}.type,
                    Session.getCacheKey(PLACES_CACHE))

            val data = Intent()
            data.putExtra(PLACES_CHECKED_POS_TAGS, checkedPos)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    companion object {
        fun getCachedArray(context: Context): BooleanArray? {
            val data = Cache.loadCachedObject(context, object : TypeToken<BooleanArray>() {}.type,
                    Session.getCacheKey(PLACES_CACHE)) ?: return null
            return data as BooleanArray
        }
    }
}
