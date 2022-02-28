package com.arira.ngidol48.app

import android.app.Application
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import com.arira.ngidol48.helper.Helper
import com.arira.ngidol48.helper.HelperToast
import com.arira.ngidol48.helper.Validasi
import com.arira.ngidol48.model.User
import com.arira.ngidol48.utilities.SharedPref
import java.io.File

class App : Application() {

    companion object {
        lateinit var pref: SharedPref
        var validasi = Validasi()
        var helper = Helper
        var toast = HelperToast()
        lateinit var user: User
        var token:String = ""

        val curdate:String = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale("ID")
        ).format(
            java.util.Date()
        )

    }

    fun clearAppData(){
        user = User()
        token = ""
    }


    override fun onCreate() {
        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        pref = SharedPref(this)


    }

}