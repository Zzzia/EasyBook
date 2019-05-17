package com.zia.easybook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.StepSubscriber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SearchAdapter.BookSelectListener {

    private lateinit var bookAdapter: SearchAdapter
    private var searchDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bookAdapter = SearchAdapter(this)
        searchRv.layoutManager = LinearLayoutManager(this)
        searchRv.adapter = bookAdapter

        main_bt.setOnClickListener {
            val bookName = main_et.text.toString()
            if (bookName.isEmpty()) return@setOnClickListener
            bookAdapter.clear()
            searchDisposable = EasyBook.search(bookName)
                .subscribe(object : StepSubscriber<List<Book>> {
                    override fun onPart(t: List<Book>) {
                        //注意rv不能并发操作
                        searchRv.post {
                            bookAdapter.addBooks(bookName, t)
                            searchRv.scrollToPosition(0)
                        }
                    }

                    override fun onFinish(t: List<Book>) {
                        Log.e("MainActivity", ArrayList<Book>(t).toString())
//                        bookAdapter.freshBooks(ArrayList(t))
                    }

                    override fun onError(e: Exception) {
                        if (e.message != null) {
                            Log.e("MainActivity", e.message)
                        }
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onMessage(message: String) {
                    }

                    override fun onProgress(progress: Int) {
                    }
                })
        }
    }

    override fun onBookSelect(itemView: View, book: Book) {
        val intent = Intent(this@MainActivity, CatalogActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    override fun onDestroy() {
        searchDisposable?.dispose()
        super.onDestroy()
    }
}