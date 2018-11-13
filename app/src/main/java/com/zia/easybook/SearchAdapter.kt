package com.zia.easybook

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zia.easybookmodule.bean.Book
import kotlinx.android.synthetic.main.item_search.view.*

/**
 * Created by zia on 2018/11/1.
 */
class SearchAdapter(val bookSelectListener: BookSelectListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var books = ArrayList<Book>()

    fun freshBooks(books: ArrayList<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_search, p0, false)
        return BookHolder(view)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BookHolder -> {
                val book = books[position]
                holder.itemView.item_book_name.text = book.bookName
                holder.itemView.item_book_author.text = book.author
                holder.itemView.item_book_lastUpdateChapter.text = "最新：${book.lastChapterName}"
                holder.itemView.item_book_site.text = book.siteName
                holder.itemView.item_book_lastUpdateTime.text = "更新：${book.lastUpdateTime}"
                holder.itemView.setOnClickListener { bookSelectListener.onBookSelect(holder.itemView, book) }
            }
        }
    }

    class BookHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface BookSelectListener {
        fun onBookSelect(itemView: View, book: Book)
    }
}