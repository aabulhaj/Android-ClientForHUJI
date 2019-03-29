package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import com.aabulhaj.hujiapp.R
import de.halfbit.pinnedsection.PinnedSectionListView


class ContactsAdapter(context: Context,
                      dataSource: ArrayList<HashMap<String, String>>,
                      resource: Int,
                      from: Array<String>,
                      to: IntArray) :
        SimpleAdapter(context, dataSource, resource, from, to),
        PinnedSectionListView.PinnedSectionListAdapter {
    private val TYPE_ITEM = 0
    private val TYPE_SEPARATOR = 1
    private val TYPE_EMAIL = 2

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val rowType = getItemViewType(position)

        if (view == null) {
            when (rowType) {
                TYPE_ITEM -> {
                    view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
                }
                TYPE_SEPARATOR -> {
                    view = inflater.inflate(R.layout.header_row, parent, false)
                    view?.isClickable = false
                }
                TYPE_EMAIL -> {
                    view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                }
            }
        }

        val title = (getItem(position) as HashMap<String, String>)["title"].toString()
        val sub = (getItem(position) as HashMap<String, String>)["sub"].toString()

        val textView1 = view?.findViewById<TextView>(android.R.id.text1)
        textView1?.text = title
        when (rowType) {
            TYPE_ITEM -> view?.findViewById<TextView>(android.R.id.text2)?.text = sub
            TYPE_EMAIL -> textView1?.setTextColor(Color.BLACK)
        }

        return view!!
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 || position == 5 || position == 10) {
            return 1
        } else if (position >= 11) {
            return 2
        }
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 3
    }

    override fun isItemViewTypePinned(viewType: Int): Boolean {
        return viewType == TYPE_SEPARATOR
    }
}
