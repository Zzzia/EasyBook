package com.zia.easybook

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.Subscriber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchAdapter.BookSelectListener {

    private lateinit var bookAdapter: SearchAdapter
    private var searchDisposable: Disposable? = null

    private val dialog by lazy {
        val dialog = ProgressDialog(this@MainActivity)
        dialog.setCancelable(true)
        dialog.progress = 0
        dialog.setTitle("正在搜索")
        dialog.setMessage("")
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.show()
        dialog.setOnCancelListener { searchDisposable?.dispose() }
        dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bookAdapter = SearchAdapter(this)
        searchRv.layoutManager = LinearLayoutManager(this)
        searchRv.adapter = bookAdapter

        main_bt.setOnClickListener {
            val bookName = main_et.text.toString()
            if (bookName.isEmpty()) return@setOnClickListener
            updateDialog(0)
            updateDialog("")
            searchDisposable = EasyBook.search(bookName)
                .subscribe(object : Subscriber<List<Book>> {
                    override fun onFinish(t: List<Book>) {
                        Log.e("MainActivity", t.toString())
                        bookAdapter.freshBooks(ArrayList(t))
                        hideDialog()
                    }

                    override fun onError(e: Exception) {
                        if (e.message != null) {
                            Log.e("MainActivity", e.message)
                        }
                        hideDialog()
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onMessage(message: String) {
                        updateDialog(message)
                    }

                    override fun onProgress(progress: Int) {
                        updateDialog(progress)
                    }
                })
        }
    }

    override fun onBookSelect(itemView: View, book: Book) {
        val intent = Intent(this@MainActivity, CatalogActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    private fun updateDialog(progress: Int?) {
        if (progress != null) {
            dialog.progress = progress
        }
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun updateDialog(msg: String?) {
        if (msg != null) {
            dialog.setMessage(msg)
        }
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideDialog() {
        dialog.dismiss()
    }

    override fun onDestroy() {
        searchDisposable?.dispose()
        super.onDestroy()
    }
}