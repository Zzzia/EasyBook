package com.zia.easybook

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.bean.Catalog
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.net.NetUtil
import com.zia.easybookmodule.rx.Subscriber
import kotlinx.android.synthetic.main.activity_preview.*
import java.lang.StringBuilder

class PreviewActivity : AppCompatActivity() {

    private lateinit var catalog: Catalog
    private lateinit var book: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        catalog = intent.getSerializableExtra("catalog") as Catalog
        book = intent.getSerializableExtra("book") as Book
        val site = book.site
        Thread(Runnable {
            val html = NetUtil.getHtml(catalog.url,site.encodeType)
            val t = site.parseContent(html)
            val sb = StringBuilder()
            sb.append(catalog.chapterName)
                .append("\n\n")
            for (line in t) {
                sb.append("        ")
                sb.append(line)
                sb.append("\n\n")
            }
            Thread.sleep(10000)
            runOnUiThread { preview_tv.text = sb.toString() }
        }).start()
//        EasyBook.getContent(book, catalog)
//            .subscribe(object : Subscriber<List<String>> {
//                override fun onFinish(t: List<String>) {
//                    val sb = StringBuilder()
//                    sb.append(catalog.chapterName)
//                        .append("\n\n")
//                    for (line in t) {
//                        sb.append("        ")
//                        sb.append(line)
//                        sb.append("\n\n")
//                    }
//                    preview_tv.text = sb.toString()
//                }
//
//                override fun onError(e: Exception) {
//                    Toast.makeText(this@PreviewActivity,e.message,Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onMessage(message: String) {
//                }
//
//                override fun onProgress(progress: Int) {
//                }
//            })
    }
}
