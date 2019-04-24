package com.aabulhaj.hujiapp.fragments

import Session
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.aabulhaj.hujiapp.CourseTypeEnum
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.adapters.NoteBooksAdapter
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.NoteBook
import com.aabulhaj.hujiapp.data.getNoteBooksURL
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class NoteBooksFragment : RefreshListFragment() {
    private var noteBooksAdapter: NoteBooksAdapter? = null
    private var progressDialog: ProgressDialog? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        progressDialog = ProgressDialog(context)
        if (noteBooksAdapter == null) {
            noteBooksAdapter = NoteBooksAdapter(context!!)
            listAdapter = noteBooksAdapter
            onRefresh()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (listView.emptyView as TextView).text = getString(R.string.no_notebooks)
    }

    override fun onRefresh() {
        setRefreshing(true)
        if (activity == null) return
        Session.callRequest(fun() = Session.hujiApiClient.getResponseBody(getNoteBooksURL()),
                activity!!, object : StringCallback {

            override fun onResponse(call: Call<ResponseBody>?, responseBody: String) {
                var validIndex = -1
                val doc = Jsoup.parse(responseBody)

                val noteBooks = ArrayList<NoteBook>()
                val tables = doc.getElementsByAttributeValue("cellpadding", "2")
                for (table in tables) {
                    if (table.attr("cellspacing") == "1") {
                        var indexOfCourseNumber = 0
                        var indexOfCourseName = 0
                        var indexOfCourseDate = 0
                        var indexOfNoteBookLink = 0
                        var indexOfCourseType = 0

                        for ((iRow, row) in table.getElementsByTag("tr").withIndex()) {
                            val noteBook = NoteBook()
                            if (iRow > 0 && indexOfCourseDate == indexOfCourseName) {
                                continue
                            }

                            for ((iColumn, column) in row.getElementsByTag("td").withIndex()) {
                                val text = column.text()
                                if (iRow == 0) {
                                    if (text == "מספר קורס") {
                                        indexOfCourseNumber = iColumn
                                    } else if (text == "קורס") {
                                        indexOfCourseName = iColumn
                                    } else if (text == "תאריך בחינה") {
                                        indexOfCourseDate = iColumn
                                    } else if (text == "מספרי מחברות בחינה") {
                                        indexOfNoteBookLink = iColumn
                                    } else if (text == "סוג בחינה") {
                                        indexOfCourseType = iColumn
                                    }
                                } else {
                                    validIndex = tables.indexOf(table)
                                    if (iColumn == indexOfCourseNumber) {
                                        noteBook.courseNumber = text
                                    } else if (iColumn == indexOfCourseName) {
                                        noteBook.courseName = text
                                    } else if (iColumn == indexOfCourseDate) {
                                        noteBook.date = text
                                    } else if (iColumn == indexOfNoteBookLink) {
                                        val elem = column.getElementsByTag("a").first()
                                        if (elem != null) {
                                            noteBook.noteBookUrl = elem.attr("href")
                                        }
                                    } else if (iColumn == indexOfCourseType) {
                                        noteBook.courseType = CourseTypeEnum.getCourseTypeEnum(text)
                                    }
                                }
                            }
                            if (iRow > 0) {
                                noteBooks.add(noteBook)
                            }
                        }
                    }
                    if (validIndex == tables.indexOf(table)) {
                        activity?.runOnUiThread {
                            noteBooksAdapter?.clear()
                            if (noteBooks.isNotEmpty()) {
                                noteBooksAdapter?.addAll(noteBooks)
                            }
                            noteBooksAdapter?.notifyDataSetChanged()
                            stopListRefreshing()
                        }
                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody>?, e: Exception) {
                stopListRefreshing()
            }
        })
    }

    private fun stopListRefreshing() {
        activity?.runOnUiThread { stopRefreshing() }
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        val noteBook = noteBooksAdapter?.getItem(position)
        if (noteBook?.noteBookUrl == null) {
            return
        }

        Session.hujiApiClient.getResponseBody(
                getNoteBooksURL(noteBook.noteBookUrl!!)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (activity == null || response == null) return

                val downloadedFile = File(activity?.cacheDir, "ScannedNoteBook.pdf")
                writeResponseBodyToDisk(response.body()!!, downloadedFile)

                response.body()?.close()

                val uri = FileProvider.getUriForFile(activity!!,
                        "com.aabulhaj.hujiapp.fileprovider", downloadedFile)


                val target = Intent(Intent.ACTION_VIEW)
                target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                target.setDataAndType(uri, "application/pdf")
                startActivity(target)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun writeResponseBodyToDisk(responseBody: ResponseBody, file: File) {
        changeProgressDialogProgress(0.0f, "KB")
        setProgressDialogVisibility(true)
        try {
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(2048)

                var fileSizeDownloaded: Long = 0

                inputStream = responseBody.byteStream()
                outputStream = FileOutputStream(file)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()

                    val total = fileSizeDownloaded.toFloat() / 1024
                    changeProgressDialogProgress(
                            if (total < 1000)
                                total
                            else total / 1024,
                            if (total < 1000)
                                "KB"
                            else "MB")
                }

                outputStream.flush()
            } catch (e: IOException) {
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
        }

        setProgressDialogVisibility(false)
    }

    private fun setProgressDialogVisibility(visible: Boolean) {
        activity?.runOnUiThread {

            if (isAdded) {
                if (visible) {
                    progressDialog?.show()
                } else {
                    progressDialog?.cancel()
                }
            }
        }
    }

    private fun changeProgressDialogProgress(amount: Float, unit: String) {
        activity?.runOnUiThread {
            if (isAdded) {
                progressDialog?.setMessage(getString(R.string.download, amount, unit))
            }
        }
    }

}
