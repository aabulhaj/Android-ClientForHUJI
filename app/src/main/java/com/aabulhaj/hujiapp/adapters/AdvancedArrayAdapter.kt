package com.aabulhaj.hujiapp.adapters

import android.content.Context
import android.widget.BaseAdapter


abstract class AdvancedArrayAdapter<T>(private val context: Context) : BaseAdapter() {
    protected var arrayList: ArrayList<T> = ArrayList()
    protected var notifyChange = true

    fun getContext(): Context {
        return context
    }

    fun add(obj: T) {
        arrayList.add(obj)
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    fun addAll(collection: Collection<T>) {
        arrayList.addAll(collection)
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    fun remove(obj: T) {
        arrayList.remove(obj)
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    fun clear() {
        arrayList.clear()
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    operator fun set(index: Int, obj: T) {
        if (index >= arrayList.size) {
            arrayList.add(obj)
        } else {
            arrayList[index] = obj
        }
        if (notifyChange) {
            notifyDataSetChanged()
        }
    }

    fun setNotifyOnChange(notify: Boolean) {
        notifyChange = notify
    }

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): T {
        return arrayList[position]
    }

    fun getValuePosition(value: T): Int {
        return arrayList.indexOf(value)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    fun getBackingArray(): ArrayList<T> {
        return arrayList
    }
}
