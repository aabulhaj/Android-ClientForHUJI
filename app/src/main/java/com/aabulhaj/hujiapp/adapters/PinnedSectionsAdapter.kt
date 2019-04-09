package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import de.halfbit.pinnedsection.PinnedSectionListView
import java.util.*


private const val TYPE_ITEM = 0
private const val TYPE_SEPARATOR = 1

class PinnedSectionsAdapter(context: Context) : BaseAdapter(),
        PinnedSectionListView.PinnedSectionListAdapter {

    private val data = ArrayList<String>()
    private val sectionHeader = TreeSet<Int>()

    private var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun addItem(item: String) {
        data.add(item)
    }

    fun addSectionHeaderItem(item: String) {
        sectionHeader.add(data.size)
        data.add(item)
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeader.contains(position)) TYPE_SEPARATOR else TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): String {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        val holder: ViewHolder
        val rowType = getItemViewType(position)

        if (view == null) {
            holder = ViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
                    holder.textView = view?.findViewById(android.R.id.text1)
                    holder.textView?.setTextColor(Color.BLACK)
                }
                TYPE_SEPARATOR -> {
                    view = inflater.inflate(com.aabulhaj.hujiapp.R.layout.header_row, parent, false)
                    holder.textView = view?.findViewById(android.R.id.text1)
                }
            }
            view?.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.textView?.text = data[position]

        return view
    }

    override fun isItemViewTypePinned(viewType: Int): Boolean {
        return viewType == TYPE_SEPARATOR
    }

    private class ViewHolder {
        var textView: TextView? = null
    }
}