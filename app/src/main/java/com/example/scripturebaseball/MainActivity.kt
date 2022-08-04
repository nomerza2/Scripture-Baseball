package com.example.scripturebaseball

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
    private var targetVerse: Int = 1 //Used solely for the link button
    private var targetBookSlug: String = ""//Used in link button, but makes more sense to init in updateVerse, where the jBoM is already being parsed
    private val totalBoMChapters = 239
    private var score = 0
    private var attemptsPerVerse = 3 //Default Value
    private var attemptsRemaining = attemptsPerVerse
    private var currentState: GameState = GameState.GUESSING

    enum class GameState {
        GUESSING, SUCCESS, LOSS
    }

    private fun updateScore(newScore: Int) {
        score = newScore
        findViewById<TextView>(R.id.Score_Displayer).setText(score.toString())
    }

    private fun updateAttemptsRemaining(newAttempts: Int) {
        attemptsRemaining = newAttempts
        findViewById<TextView>(R.id.Remaining_Guesses_Displayer).setText(attemptsRemaining.toString())
    }

    private fun updateCurrentState(state: GameState) {
        val button: Button = findViewById(R.id.submitter)
        val answerShower: TextView = findViewById(R.id.Guess_Result)
        val linkButton: Button = findViewById(R.id.link_button)

        if (state == GameState.GUESSING) {
            linkButton.visibility = View.GONE
            button.setText(R.string.Guess)
            answerShower.text = ""//Awkward when hints from previous verse show up on new one. Now it's cleared when verse updates
            val chapterInput: EditText = findViewById(R.id.chapter_input)
            chapterInput.text.clear() // Removes Previous guesses on a new verse
            var bookChooser: Spinner = findViewById(R.id.book_chooser)

            if (currentState == GameState.LOSS) {// Resets score not when game is lost, but when a new game is started
                updateScore(0)
            }
        } else {
            linkButton.visibility = View.VISIBLE

            if (state == GameState.SUCCESS) {
                updateScore(score + 1)
                button.setText(R.string.Next_Verse)
            } else if (state == GameState.LOSS) {
                button.setText(R.string.Game_Over)
                val bookAnswer: String = resources.getStringArray(R.array.BoM_Books)[targetBookIndex]
                val displayCorrect: String = getString(R.string.Display_Correct)
                val correctAnswer = "$displayCorrect $bookAnswer $targetChapter"
                answerShower.text = correctAnswer

            }
        }

        currentState = state

    }

    private fun bookInit(): JSONObject {
        val bufferedReader: BufferedReader = this.assets.open("book-of-mormon.json").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        var jBook = JSONObject(inputString)
        return jBook
    }

    //Resets currentState, attemptsRemaining, Selects a new verse, displays it, and assigns targetBookIndex and targetChapter
    private fun updateVerse() {
        updateCurrentState(GameState.GUESSING)
        updateAttemptsRemaining(attemptsPerVerse)

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

        ////***FOR TESTING UI - Longest Verse by Characters in BoM is Alma 60:16
        //targetChapter = 60
        //targetBookIndex = 8

        val chosenBook: JSONObject = books[targetBookIndex] as JSONObject
        val jChapters: JSONArray = chosenBook["chapters"] as JSONArray
        val chosenChapter: JSONObject = jChapters[targetChapter - 1] as JSONObject
        val verseArray: JSONArray = chosenChapter["verses"] as JSONArray
        val verseIndex = Random.nextInt(verseArray.length())
        //ALSO FOR LONGEST VERSE TEST
        //val verseIndex = 15

        val verse: JSONObject = verseArray[verseIndex] as JSONObject
        val realVerse: Int = verse["verse"] as Int
        val displayText: String = verse["text"] as String
        val verseDisplay = findViewById<TextView>(R.id.verse_display).apply {
            text = "$realVerse: $displayText"
        }

        targetBookSlug = chosenBook["lds_slug"] as String
        targetVerse = realVerse
        /* These lines will display the answers, for testing purposes

        val tempString = "$targetBookIndex $targetChapter"
        val tempDisplay = findViewById<TextView>(R.id.textView2).apply {
            text = tempString
        }*/
    }

    //Displays message based on guess, handles score, attempts remaining, and currentState changes accordingly
    private fun interpretGuess() {
        val chapterInput = findViewById<EditText>(R.id.chapter_input)

        val guessedChapter: Int = chapterInput.text?.toString()?.toIntOrNull() ?: 0 //Elvis Operator - blank input = null = 0 = Nonexistant guess
        val bookChooser = findViewById<Spinner>(R.id.book_chooser)
        val bookIndex = bookChooser.selectedItemPosition
        var message: String

        if (guessedChapter > chapterLimit || guessedChapter <= 0){
            message = getString(R.string.Nonexistant_Guess)
        } else if (bookIndex != targetBookIndex) {
            message = getString(R.string.Wrong_Book)
            updateAttemptsRemaining(attemptsRemaining - 1)
        } else if (targetChapter > guessedChapter) {
            message = getString(R.string.Higher)
            updateAttemptsRemaining(attemptsRemaining - 1)
        } else if (targetChapter < guessedChapter) {
            message = getString(R.string.Lower)
            updateAttemptsRemaining(attemptsRemaining - 1)
        } else {
            message = getString(R.string.Correct)
            updateCurrentState(GameState.SUCCESS)
        }

        val displayAnswer = findViewById<TextView>(R.id.Guess_Result).apply {
            text = message
        }

        //In event of a loss, the updateCurrentState will override the message that was just displayed
        if (attemptsRemaining == 0) {
            updateCurrentState(GameState.LOSS)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        attemptsPerVerse = intent.getIntExtra("GUESS_LIMIT", attemptsPerVerse) //Uses the preassigned value as the default

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
        updateScore(0)

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

        //HIDE KEYBOARD
        //Citation: https://tutorial.eyehunts.com/android/hide-soft-keyboard-android-code-in-kotlin-programmatically/
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)

        if (currentState == GameState.GUESSING) {
            interpretGuess()
        } else {
            updateVerse()
        }
    }

    fun onLinkButtonClick(view: View) {
        val urlString = "https://www.churchofjesuschrist.org/study/scriptures/bofm/$targetBookSlug/$targetChapter?lang=eng&id=$targetVerse#p$targetVerse"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))

        startActivity(browserIntent)
    }
}
