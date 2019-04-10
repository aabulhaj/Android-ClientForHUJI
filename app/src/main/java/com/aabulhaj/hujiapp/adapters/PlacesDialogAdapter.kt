package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.aabulhaj.hujiapp.R
import kotlinx.android.synthetic.main.places_cell_layout.view.*


class PlacesDialogAdapter<T>(context: Context) : AdvancedArrayAdapter<T>(context) {
    private val infalter = LayoutInflater.from(context) as LayoutInflater
    private val checked = BooleanArray(6)

    fun addWithBoolean(`object`: T, `is`: Boolean) {
        arrayList.add(`object`)
        checked[arrayList.size - 1] = `is`
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    fun addAllWithBoolean(collection: Collection<T>?, `is`: BooleanArray) {
        if (collection == null) {
            return
        }

        for ((i, `object`) in collection.withIndex()) {
            addWithBoolean(`object`, `is`[i])
        }
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    fun getCheckedItemPositions(): BooleanArray {
        return checked
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v: View? = convertView
        if (v == null) {
            v = infalter.inflate(R.layout.places_cell_layout, parent, false) as View
        }

        v.placesTextView.text = getItem(position).toString()
        v.placesCheckBox.isChecked = checked[position]
        v.placesCheckBox.setOnCheckedChangeListener(
                CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    if (!buttonView.isPressed) return@OnCheckedChangeListener
                    checked[position] = isChecked
                })
        return v
    }
}