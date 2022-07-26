package com.example.scripturebaseball

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File

class MainActivity : AppCompatActivity() {
    var jBoM: JSONObject? = null
    var chapterLimit: Int = 0

    fun BookInit(): JSONObject {
        val bufferedReader: BufferedReader = this.assets.open("book-of-mormon.json").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        var jBook = JSONObject(inputString)
        return jBook
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jBoM = BookInit()

        val bookChooser: Spinner = findViewById(R.id.book_chooser)

        bookChooser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var books: JSONArray = jBoM?.get("books") as JSONArray
                var chosenBook: JSONObject = books[position] as JSONObject
                var jChapters: JSONArray = chosenBook["chapters"] as JSONArray
                chapterLimit = jChapters.length()

                var output = findViewById<TextView>(R.id.TextDisplayView).apply {
                    text = "Select 1-$chapterLimit"
                }

            }

            //Does Nothing, but needs an implemention
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        //bookChooser.onItemSelectedListener = this

        //spinner adapter, citation https://developer.android.com/guide/topics/ui/controls/spinner#kotlin
        /*val spinner: Spinner = findViewById(R.id.book_chooser)
        ArrayAdapter.createFromResource(this, R.array.BoM_Books, android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }*/
    }

    fun onClick(view: View) {

        val book_chooser = findViewById<Spinner>(R.id.book_chooser)
        //val book_location = book_chooser.selectedItem.toString()
        //val book_index = book_chooser.gravity
        val book_index = book_chooser.selectedItemPosition

        //var gson = Gson()
        //val gson_data = Gson


        var books: JSONArray = jBoM?.get("books") as JSONArray
        var chosenBook: JSONObject = books[book_index] as JSONObject
        var jChapters: JSONArray = chosenBook["chapters"] as JSONArray
        val chapterCount = jChapters.length()

        //call.apply at end and assign message
        var output = findViewById<TextView>(R.id.TextDisplayView).apply {
            text = chapterCount.toString()
        }


    }
}