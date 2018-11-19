package com.zia.easybook

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.zia.easybookmodule.bean.Book
import com.zia.easybookmodule.bean.Catalog
import com.zia.easybookmodule.bean.Type
import com.zia.easybookmodule.engine.EasyBook
import com.zia.easybookmodule.rx.Disposable
import com.zia.easybookmodule.rx.Subscriber
import kotlinx.android.synthetic.main.activity_catalog.*
import java.io.File
import java.util.*

class CatalogActivity : AppCompatActivity(), CatalogAdapter.CatalogSelectListener {

    private lateinit var book: Book
    private lateinit var adapter: CatalogAdapter
    //控制内存泄漏
    private var downloadDisposable: Disposable? = null
    private var searchDisposable: Disposable? = null

    private val dialog by lazy {
        val dialog = ProgressDialog(this@CatalogActivity)
        dialog.setCancelable(false)
        dialog.progress = 0
        dialog.setMessage("")
        dialog.setTitle("正在下载")
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        book = intent.getSerializableExtra("book") as Book
        initBookInfo()

        adapter = CatalogAdapter(this)
        catalogRv.layoutManager = LinearLayoutManager(this)
        catalogRv.adapter = adapter

        searchDisposable = EasyBook.getCatalog(book)
            .subscribe(object : Subscriber<List<Catalog>> {
                override fun onFinish(t: List<Catalog>) {
                    val arrayList = ArrayList<Catalog>(t)
                    arrayList.reverse()
                    adapter.freshCatalogs(arrayList)
                    book_loading.visibility = View.GONE
                }

                override fun onError(e: Exception) {
                    Toast.makeText(this@CatalogActivity, e.message, Toast.LENGTH_SHORT).show()
                    book_loading.text = e.message
                }

                override fun onMessage(message: String) {

                }

                override fun onProgress(progress: Int) {
                }

            })

        book_download.setOnClickListener {
            chooseType()
        }
    }

    /**
     * 没有添加动态权限，需要手动打开一下
     */
    private fun download(type: Type) {
        Toast.makeText(this@CatalogActivity, "请手动打开文件读写权限", Toast.LENGTH_SHORT).show()
        downloadDisposable = EasyBook.download(book)
            .setType(type)
            .setThreadCount(150)
            .setSavePath(Environment.getExternalStorageDirectory().path + File.separator + "book")
            .subscribe(object : Subscriber<File> {
                override fun onFinish(t: File) {
                    hideDialog()
                    Log.e("CatalogActivity", t.path)
                    Toast.makeText(this@CatalogActivity, "保存成功，位置在${t.path}", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@CatalogActivity, e.message, Toast.LENGTH_SHORT).show()
                    hideDialog()
                }

                override fun onMessage(message: String) {
                    updateDialog(message)
                }

                override fun onProgress(progress: Int) {
                    updateDialog(progress)
                }
            })
    }

    override fun onDestroy() {
        downloadDisposable?.dispose()
        searchDisposable?.dispose()
        super.onDestroy()
    }

    override fun onCatalogSelect(itemView: View, position: Int, catalog: Catalog) {
        val intent = Intent(this@CatalogActivity, PreviewActivity::class.java)
        intent.putExtra("catalog", catalog)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    private fun chooseType() {
        val types = arrayOf("EPUB", "TXT")
        val style =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.style.Theme_Material_Light_Dialog
            else android.R.style.Theme_DeviceDefault_Light_Dialog
        AlertDialog.Builder(this, style)
            .setTitle("选择下载格式")
            .setItems(types) { dialog, which ->
                var type = Type.EPUB
                when (which) {
                    0 -> {
                        type = Type.EPUB
                    }
                    1 -> {
                        type = Type.TXT
                    }
                }
                download(type)
            }.show()
    }

    private fun initBookInfo() {
        book_name.text = book.bookName
        book_author.text = book.author
        book_lastUpdateChapter.text = "最新：${book.lastChapterName}"
        book_site.text = book.site.siteName
        book_lastUpdateTime.text = "更新：${book.lastUpdateTime}"
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
}
