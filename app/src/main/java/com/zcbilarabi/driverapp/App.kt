package com.zcbilarabi.driverapp

import android.app.Application
import com.parse.Parse
import com.parse.ParseACL


class App: Application() {
    override fun onCreate() {
        super.onCreate()


        Parse.enableLocalDatastore(this)
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.APP_ID) // if defined
                .clientKey(BuildConfig.CLIENT_KEY)
                .server(BuildConfig.SERVER_URL)
                .build()
        )
        var parseAcl=ParseACL()
        parseAcl.publicWriteAccess=true
        parseAcl.publicReadAccess=true
        ParseACL.setDefaultACL(parseAcl,true)




    }
}