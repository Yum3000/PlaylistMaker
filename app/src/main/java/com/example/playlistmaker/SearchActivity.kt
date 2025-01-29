package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import android.view.inputmethod.InputMethodManager

class SearchActivity : AppCompatActivity() {
    private var inputedText: String = SEARCH_DEFAULT
    private var searchLineFocus: Boolean = false
    private lateinit var inputSearchLine: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        inputSearchLine = findViewById<EditText>(R.id.search_edit_text)

        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEFAULT)
            searchLineFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)
        }

        inputSearchLine.setText(inputedText)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputSearchLine.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputSearchLine.windowToken, 0)
            inputSearchLine.clearFocus()
            searchLineFocus = false
        }

        inputSearchLine.setOnFocusChangeListener { _, hasFocus ->
            searchLineFocus = hasFocus
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) { }
        }
        inputSearchLine.addTextChangedListener(searchTextWatcher)

        if (searchLineFocus || savedInstanceState == null) {
            inputSearchLine.requestFocus()
            inputSearchLine.setSelection(inputSearchLine.text.length)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(inputSearchLine, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, inputSearchLine.text.toString())
        outState.putBoolean(SEARCH_FOCUS, searchLineFocus)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputedText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEFAULT)
        inputSearchLine.setText(inputedText)
        searchLineFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)

        if (searchLineFocus) {
            inputSearchLine.requestFocus()
            inputSearchLine.setSelection(inputSearchLine.text.length)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(inputSearchLine, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEFAULT = ""
        const val SEARCH_FOCUS = "SEARCH_FOCUS"
    }
}