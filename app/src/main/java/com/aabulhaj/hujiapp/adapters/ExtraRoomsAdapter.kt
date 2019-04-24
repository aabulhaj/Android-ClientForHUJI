package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.ExamRooms
import de.halfbit.pinnedsection.PinnedSectionListView
import kotlinx.android.synthetic.main.extra_exam_room_layout.view.*
import java.util.*


private const val TYPE_ITEM = 0
private const val TYPE_SEPARATOR = 1

class ExtraRoomsAdapter(context: Context) : BaseAdapter(),
        PinnedSectionListView.PinnedSectionListAdapter {

    private val data = ArrayList<ExamRooms>()
    private val sectionHeader = TreeSet<Int>()

    private var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun addItem(item: ExamRooms) {
        data.add(item)
    }

    fun addSectionHeaderItem(item: ExamRooms) {
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

    override fun getItem(position: Int): ExamRooms {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun isHeader(examRooms: ExamRooms): Boolean {
        return sectionHeader.contains(data.indexOf(examRooms))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        val holder: ViewHolder
        val rowType = getItemViewType(position)

        if (view == null) {
            holder = ViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    view = inflater.inflate(R.layout.extra_exam_room_layout, parent, false)
                }
                TYPE_SEPARATOR -> {
                    view = inflater.inflate(R.layout.header_row, parent, false)
                    holder.textView = view.findViewById(android.R.id.text1)
                }
            }
            view?.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val examRooms = getItem(position)
        if (rowType == TYPE_SEPARATOR) {
            holder.textView?.text = examRooms.semester
        } else {
            view?.namesRangeTextView?.text = examRooms.courseComments

            if (examRooms.courseComments == "N/A") {
                view?.namesRangeTextView?.setTextColor(Color.GRAY)
            } else {
                view?.namesRangeTextView?.setTextColor(Color.BLACK)
            }

            view?.roomTextView?.text = examRooms.room
            view?.dateAndTimeTextView?.text = String.format(Locale.ENGLISH,
                    "$examRooms.date, $examRooms.hour")
        }

        return view
    }

    override fun isItemViewTypePinned(viewType: Int): Boolean {
        return viewType == TYPE_SEPARATOR
    }

    private class ViewHolder {
        var textView: TextView? = null
    }
}