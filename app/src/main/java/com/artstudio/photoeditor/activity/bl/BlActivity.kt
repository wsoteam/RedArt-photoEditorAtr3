package com.artstudio.photoeditor.activity.bl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.FrameLayout
import com.artstudio.photoeditor.R
import com.artstudio.photoeditor.Utils.PreferenceProvider
import kotlinx.android.synthetic.main.activity_bl.*

class BlActivity : AppCompatActivity(R.layout.activity_bl) {

    lateinit var webBlack: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUI()

        ///

        webBlack.settings.javaScriptEnabled = true
        webBlack.settings.domStorageEnabled = true
        webBlack.settings.userAgentString = webBlack.settings.userAgentString + "MobileAppClient/Android/0.9"
        webBlack.webViewClient = Client()

        var url = PreferenceProvider.getUrl()
        webBlack.loadUrl(url)


    }

    private fun createUI() {
        webBlack = WebView(this)

        webBlack.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        )
        fl_web_screen.addView(webBlack)
    }

    override fun onBackPressed() {
        if (webBlack.canGoBack()) {
            webBlack.goBack()
        } else {
            super.onBackPressed()
        }
    }
}