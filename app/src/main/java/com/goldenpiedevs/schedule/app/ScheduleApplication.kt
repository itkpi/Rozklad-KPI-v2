package com.goldenpiedevs.schedule.app

import android.support.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.crashlytics.android.Crashlytics
import com.evernote.android.job.JobManager
import com.goldenpiedevs.schedule.app.core.injection.component.AppComponent
import com.goldenpiedevs.schedule.app.core.injection.component.DaggerAppComponent
import com.goldenpiedevs.schedule.app.core.injection.module.*
import com.goldenpiedevs.schedule.app.core.utils.AppJobCreator
import com.jakewharton.threetenabp.AndroidThreeTen
import io.fabric.sdk.android.Fabric
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.realm.Realm
import io.realm.RealmConfiguration




class ScheduleApplication : MultiDexApplication() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = initDagger()
//
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return
//        }
//
//        LeakCanary.install(this)
        Fabric.with(this, Crashlytics())

        JobManager.create(this).addJobCreator(AppJobCreator())

        Kotpref.init(this)
        AndroidThreeTen.init(this)
        Realm.init(this)

        val config = RealmConfiguration.Builder()
                .name("schedule.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)

        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/ProductSansRegular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())
    }

    private fun initDagger() =
            DaggerAppComponent.builder()
                    .contextModule(ContextModule(this))
                    .networkManagerModule(NetworkManagerModule)
                    .networkingApiModule(NetworkingApiModule)
                    .networkingConfigurationModule(NetworkingConfigurationModule)
                    .extrasModule(ExtrasModule)
                    .build()


    override fun onTerminate() {
        Realm.getDefaultInstance().close()
        super.onTerminate()
    }
}