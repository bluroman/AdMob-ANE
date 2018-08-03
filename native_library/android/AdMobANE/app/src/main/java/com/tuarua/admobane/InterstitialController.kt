/*
 *  Copyright 2018 Tua Rua Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tuarua.admobane

import android.os.Bundle
import com.adobe.fre.FREContext
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.gson.Gson
import com.tuarua.admobane.Position.*
import com.tuarua.frekotlin.FreKotlinController

@Suppress("JoinDeclarationAndAssignment")
class InterstitialController(override var context: FREContext?,
                             private val isPersonalised: Boolean) : FreKotlinController, AdListener() {

    private var _adView: InterstitialAd? = null
    private var _showOnLoad:Boolean = true
    private val gson = Gson()

    init {
    }

    fun load(unitId: String, deviceList: List<String>?, targeting: Targeting?, showOnLoad:Boolean) {
        _adView = InterstitialAd(this.context?.activity?.applicationContext)
        _showOnLoad = showOnLoad
        val av = _adView ?: return
        av.adListener = this
        av.adUnitId = unitId

        val builder = AdRequest.Builder()

        if (!isPersonalised){
            val extras = Bundle()
            extras.putString("npa", "1")
            builder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }

        if (targeting != null) {
            if (targeting.forChildren != null) {
                val forChildren = targeting.forChildren
                forChildren?.let { builder.tagForChildDirectedTreatment(it) }
            }
        }
        deviceList?.forEach { device -> builder.addTestDevice(device) }
        av.loadAd(builder.build())

    }

    fun show() {
        val av = _adView ?: return
        if (av.isLoaded){
            av.show()
        }
    }

    override fun onAdImpression() {
        super.onAdImpression()
        dispatchEvent(Constants.ON_IMPRESSION, gson.toJson(AdMobEvent(INTERSTITIAL.ordinal)))
    }

    override fun onAdLeftApplication() {
        super.onAdLeftApplication()
        dispatchEvent(Constants.ON_LEFT_APPLICATION, gson.toJson(AdMobEvent(INTERSTITIAL.ordinal)))
    }

    override fun onAdClicked() {
        super.onAdClicked()
        dispatchEvent(Constants.ON_CLICKED, gson.toJson(AdMobEvent(INTERSTITIAL.ordinal)))
    }

    override fun onAdFailedToLoad(p0: Int) {
        super.onAdFailedToLoad(p0)
        dispatchEvent(Constants.ON_LOAD_FAILED, gson.toJson(AdMobEvent(INTERSTITIAL.ordinal, p0)))
    }

    override fun onAdClosed() {
        super.onAdClosed()
        dispatchEvent(Constants.ON_CLOSED, gson.toJson(AdMobEvent(INTERSTITIAL.ordinal)))
    }

    override fun onAdOpened() {
        super.onAdOpened()
        dispatchEvent(Constants.ON_OPENED, gson.toJson(AdMobEvent(INTERSTITIAL.ordinal)))
    }

    override fun onAdLoaded() {
        super.onAdLoaded()

        val av = _adView ?: return
        if(_showOnLoad) {
            av.show()
        }

        dispatchEvent(Constants.ON_LOADED, gson.toJson(AdMobEvent(INTERSTITIAL.ordinal)))
    }


    override val TAG: String
        get() = this::class.java.canonicalName

}