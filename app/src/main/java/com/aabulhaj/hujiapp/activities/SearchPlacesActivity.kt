package com.aabulhaj.hujiapp.activities

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.aabulhaj.hujiapp.HUJIPlaceUtil
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.SearchPlacesAdapter
import com.aabulhaj.hujiapp.data.HUJIPlace
import kotlinx.android.synthetic.main.activity_search_places.*


class SearchPlacesActivity : ToolbarActivity() {
    private var adapter: SearchPlacesAdapter? = null

    private lateinit var scopusData: ArrayList<HUJIPlace>
    private lateinit var edmondData: ArrayList<HUJIPlace>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_places)

        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.title = getString(R.string.places)

        adapter = SearchPlacesAdapter(this)

        scopusData = HUJIPlaceUtil.getPlacesOnCampus(R.raw.scopus, this)
        edmondData = HUJIPlaceUtil.getPlacesOnCampus(R.raw.edmond, this)

        scopusData.sort()
        edmondData.sort()

        adapter?.addSectionHeaderItem(HUJIPlace(getString(R.string.mount_scopus),
                0.0, 0.0, 0.0, 0.0))
        adapter?.addAll(scopusData)
        adapter?.addSectionHeaderItem(HUJIPlace(getString(R.string.emond_safra),
                0.0, 0.0, 0.0, 0.0))
        adapter?.addAll(edmondData)

        searchPlacesListView.adapter = adapter

        val intent = intent
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            doMySearch(query)
        }
    }

    private fun addPlacesFromText(text: String) {
        val scopusHeader = HUJIPlace(getString(R.string.mount_scopus), 0.0, 0.0, 0.0, 0.0)
        val edmondHeader = HUJIPlace(getString(R.string.emond_safra), 0.0, 0.0, 0.0, 0.0)

        adapter?.addSectionHeaderItem(scopusHeader)
        for (place in scopusData) {
            if (place.name!!.toLowerCase().contains(text)) {
                adapter?.addItem(place)
            }
        }

        if (adapter?.count == 1) {
            adapter?.removeSectionHeaderItem(scopusHeader)
        }

        val sizeAfterMountScopus = adapter?.count!!

        adapter?.addSectionHeaderItem(edmondHeader)
        for (place in edmondData) {
            if (place.name!!.toLowerCase().contains(text)) {
                adapter?.addItem(place)
            }
        }

        if (sizeAfterMountScopus + 1 == adapter!!.count) {
            adapter?.removeSectionHeaderItem(edmondHeader)
        }
    }

    private fun doMySearch(query: String) {
        adapter?.clear()
        addPlacesFromText(query)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_place_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search_place)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        adapter?.clear()
                        addPlacesFromText(newText)
                        return false
                    }
                }
        )

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val componentName = ComponentName(this, SearchPlacesActivity::class.java)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId != R.id.action_search_place) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
