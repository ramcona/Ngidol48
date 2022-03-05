package com.arira.ngidol48.ui.pengaturan

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.arira.ngidol48.R
import com.arira.ngidol48.app.App
import com.arira.ngidol48.databinding.ActivityPengaturanBinding
import com.arira.ngidol48.helper.BaseActivity
import com.arira.ngidol48.utilities.Go

class PengaturanActivity : BaseActivity() {
    private lateinit var binding: ActivityPengaturanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pengaturan)
        setToolbar(getString(R.string.teks_pengaturan), binding.toolbar)
        action()

        if (App.pref.getOnReview()){
            binding.linDonasi.visibility = View.GONE
        }
    }
    private fun action(){
        binding.linDonasi.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://saweria.co/raflipakei")
            startActivity(openURL)
        }
        binding.linNotifikasi.setOnClickListener {
            Go(this).move(PengaturanNotifikasiActivity::class.java)
        }
    }
}