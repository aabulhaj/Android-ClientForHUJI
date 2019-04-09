package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.graphics.Color
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.data.AcademicCalendarObject
import com.aabulhaj.hujiapp.views.NormalHoursIndicator
import de.halfbit.pinnedsection.PinnedSectionListView
import kotlinx.android.synthetic.main.calendar_event_row.view.*
import java.text.SimpleDateFormat
import java.util.*


private const val TYPE_ITEM = 0
private const val TYPE_SEPARATOR = 1

class AcademicCalendarAdapter(private val context: Context) : BaseAdapter(),
        PinnedSectionListView.PinnedSectionListAdapter {
    private val data = ArrayList<AcademicCalendarObject>()
    private val sectionHeader = TreeSet<Int>()

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val googleGreenColor = ResourcesCompat.getColor(context.resources, R.color.google_green, null)

    private val isRTL = context.resources.getBoolean(R.bool.is_rtl)

    fun addItem(item: AcademicCalendarObject) {
        data.add(item)
        notifyDataSetChanged()
    }

    fun addSectionHeaderItem(item: AcademicCalendarObject) {
        sectionHeader.add(data.size)
        data.add(item)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (sectionHeader.contains(position)) TYPE_SEPARATOR else TYPE_ITEM
    }

    fun isSectionHeaderItem(position: Int): Boolean {
        return sectionHeader.contains(position)
    }

    fun getItemPosition(aco: AcademicCalendarObject): Int {
        for (i in 0 until data.size) {
            val cal1 = if (aco.isRange) aco.startCalendar else aco.calendar
            val cal2 = if (aco.isRange) data[i].startCalendar else data[i].calendar
            if (areSameDay(cal1, cal2)) return i
        }
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): AcademicCalendarObject {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view = convertView
        val holder: ViewHolder

        val aco = getItem(position)
        val rowType = getItemViewType(position)

        if (view == null) {
            holder = ViewHolder()
            when (rowType) {
                TYPE_ITEM -> {
                    view = inflater.inflate(R.layout.calendar_event_row, parent, false)
                    holder.eventNameTextView = view.eventNameTextView
                    holder.normalHoursIndicator = view.normalHoursIndicator
                    holder.periodTextView = view.periodTextView
                }
                TYPE_SEPARATOR -> {
                    view = inflater.inflate(R.layout.header_row, parent, false)
                    holder.separatorTextView = view.findViewById(android.R.id.text1)
                }
            }
            view?.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        if (rowType == TYPE_SEPARATOR) {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = sdf.format(if (aco.isRange) aco.startCalendar.time else aco.calendar.time)
            holder.separatorTextView?.text = date
        } else if (rowType == TYPE_ITEM) {
            if (isRTL) {
                holder.eventNameTextView?.text = aco.heName
            } else {
                holder.eventNameTextView?.text = aco.enName
            }

            if (aco.isNormalHours) {
                holder.normalHoursIndicator?.setPaintColor(googleGreenColor)
            } else {
                holder.normalHoursIndicator?.setPaintColor(Color.RED)
            }

            if (aco.isRange) {
                val endHr = aco.endCalendar.get(Calendar.HOUR)
                val areSameDay = areSameDay(aco.startCalendar, aco.endCalendar)

                if (areSameDay) {
                    holder.periodTextView?.text = if (endHr == 0)
                        context.getString(R.string.all_day)
                    else
                        String.format("%s\n%s", context.getString(R.string.ends),
                                SimpleDateFormat("HH:mm",
                                        Locale.getDefault()).format(aco.endCalendar.timeInMillis))
                } else {
                    holder.periodTextView?.text = context.getString(R.string.all_day)
                }
            } else {
                val hr = aco.calendar.get(Calendar.HOUR)
                holder.periodTextView?.text = if (hr == 0)
                    context.getString(R.string.all_day)
                else
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(aco.calendar.timeInMillis)
            }
        }

        return view
    }

    private fun areSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    override fun isItemViewTypePinned(viewType: Int): Boolean {
        return viewType == TYPE_SEPARATOR
    }

    private class ViewHolder {
        var separatorTextView: TextView? = null

        var eventNameTextView: TextView? = null
        var normalHoursIndicator: NormalHoursIndicator? = null
        var periodTextView: TextView? = null
    }
}
