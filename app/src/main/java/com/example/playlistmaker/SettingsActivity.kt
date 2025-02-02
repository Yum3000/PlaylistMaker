package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.widget.Toast


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareTextView = findViewById<TextView>(R.id.share_textview)
        val shareIcon = findViewById<ImageView>(R.id.share_icon)

        val shareClickListener = View.OnClickListener {
            shareApp()
        }

        shareTextView.setOnClickListener(shareClickListener)
        shareIcon.setOnClickListener(shareClickListener)

        val supportTextView = findViewById<TextView>(R.id.support_textview)
        val supportIcon = findViewById<ImageView>(R.id.support_icon)

        val supportClickListener = View.OnClickListener {
            writeToSupport()
        }

        supportTextView.setOnClickListener(supportClickListener)
        supportIcon.setOnClickListener(supportClickListener)

        val userAgreeTextView = findViewById<TextView>(R.id.user_agreement_textview)
        val userAgreeIcon = findViewById<ImageView>(R.id.user_agreement_icon)

        val agreementClickListener = View.OnClickListener {
            openUserAgreement()
        }

        userAgreeTextView.setOnClickListener(agreementClickListener)
        userAgreeIcon.setOnClickListener(agreementClickListener)

    }

    private fun shareApp(){
        val shareMsg = getString(R.string.share_app_msg)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMsg)
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun writeToSupport() {
        val email = getString(R.string.support_email)
        val subject = getString(R.string.support_subject)
        val body = getString(R.string.support_body)

        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(supportIntent)
    }

    private fun openUserAgreement() {
        val practicumOffer = Uri.parse(getString(R.string.practicum_offer))
        val agreementIntent = Intent(Intent.ACTION_VIEW, practicumOffer)
        startActivity(agreementIntent)
    }
}