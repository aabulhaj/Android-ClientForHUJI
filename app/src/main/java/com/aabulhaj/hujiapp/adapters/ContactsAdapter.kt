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


private const val TYPE_ITEM = 0
private const val TYPE_SEPARATOR = 1
private const val TYPE_EMAIL = 2

class ContactsAdapter(context: Context,
                      dataSource: ArrayList<HashMap<String, String>>,
                      resource: Int,
                      from: Array<String>,
                      to: IntArray) :
        SimpleAdapter(context, dataSource, resource, from, to),
        PinnedSectionListView.PinnedSectionListAdapter {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val rowType = getItemViewType(position)

        val viewHolder: ViewHolder

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
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val title = (getItem(position) as HashMap<String, String>)["title"].toString()
        val sub = (getItem(position) as HashMap<String, String>)["sub"].toString()

        viewHolder.textView1?.text = title
        when (rowType) {
            TYPE_ITEM -> viewHolder.textView2?.text = sub
            TYPE_EMAIL -> viewHolder.textView1?.setTextColor(Color.BLACK)
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

    private class ViewHolder(view: View?) {
        val textView1: TextView? = view?.findViewById(android.R.id.text1)
        val textView2: TextView? = view?.findViewById(android.R.id.text1)
    }
}
