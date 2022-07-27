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
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var jBoM: JSONObject? = null
    private var chapterLimit: Int = 0
    private var targetBookIndex: Int = 0 //0-14, associated with books
    private var targetChapter: Int = 1 //1-chapterLimit, associated with chapters
    private val totalBoMChapters = 239

    private fun bookInit(): JSONObject {
        val bufferedReader: BufferedReader = this.assets.open("book-of-mormon.json").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        var jBook = JSONObject(inputString)
        return jBook
    }

    private fun updateVerse() {
        var books: JSONArray = jBoM?.get("books") as JSONArray

        var relativeChapter = Random.nextInt(1, totalBoMChapters + 1) //until is exclusive, therefore + 1
        //Chapter indexing, to match written chapters, is 1-based rather than 0
         //targetChapter
        for (i in 0 until books.length()) {
            val currentBook: JSONObject = books[i] as JSONObject
            val chaptersArray: JSONArray = currentBook["chapters"] as JSONArray
            val chapterCount = chaptersArray.length() // size is 1-based, so it can be compared to relative chapter without modification
            if (chapterCount < relativeChapter) {
                relativeChapter -= chapterCount
            } else {
                targetChapter = relativeChapter
                targetBookIndex = i
                break
            }
        }


        val chosenBook: JSONObject = books[targetBookIndex] as JSONObject
        val jChapters: JSONArray = chosenBook["chapters"] as JSONArray
        val chosenChapter: JSONObject = jChapters[targetChapter - 1] as JSONObject
        val verseArray: JSONArray = chosenChapter["verses"] as JSONArray
        val verseIndex = Random.nextInt(verseArray.length())
        val verse: JSONObject = verseArray[verseIndex] as JSONObject
        val realVerse: Int = verse["verse"] as Int
        val displayText: String = verse["text"] as String
        val verseDisplay = findViewById<TextView>(R.id.verse_display).apply {
            text = "$realVerse: $displayText"
        }

        val tempString = "$targetBookIndex $targetChapter"

        val tempDisplay = findViewById<TextView>(R.id.textView2).apply {
            text = tempString
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jBoM = bookInit()

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

            //Does Nothing, but needs an implemention, even if empty
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }

        updateVerse()

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

        /*val book_chooser = findViewById<Spinner>(R.id.book_chooser)
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
        }*/

        updateVerse()


    }
}