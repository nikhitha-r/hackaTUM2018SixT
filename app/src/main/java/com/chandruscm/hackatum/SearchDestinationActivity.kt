package com.chandruscm.hackatum

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_search_destination.*
import kotlinx.android.synthetic.main.activity_search_destination_toolbar.*

class SearchDestinationActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_destination)

        initTranslucencyPadding()
        initListeners()
    }

    fun initTranslucencyPadding()
    {
        ViewCompat.setOnApplyWindowInsetsListener(activity_search_main_layout) { v, insets ->
            (activity_search_toolbar.layoutParams as ViewGroup.MarginLayoutParams).topMargin = insets.systemWindowInsetTop + resources.getDimensionPixelSize(R.dimen.spacing_normal)
            insets.consumeSystemWindowInsets()
        }
    }

    fun initListeners()
    {
        val recipents = arrayOf("David, FelmochingerStrasse 54 - 80993", "Peter, Karlsplatz 29 - 80883", "Michael, Marienplatz 27 - 80993")
        activity_search_toolbar_user_dropdown.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, recipents)
    }

    fun onCloseClick(view: View)
    {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun onSubmitClick(view: View)
    {
        val returnIntent = Intent()
        returnIntent.putExtra("DESTINATION", activity_search_toolbar_edit_text_name.text.toString())
        returnIntent.putExtra("RECIPIENT", activity_search_toolbar_user_dropdown.selectedItem.toString())
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}