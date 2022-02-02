package com.arira.ngidol48.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arira.ngidol48.R
import com.arira.ngidol48.adapter.AvaMemberAdapter
import com.arira.ngidol48.adapter.BeritaAdapter
import com.arira.ngidol48.adapter.MemberAdapter
import com.arira.ngidol48.adapter.SliderAdapter
import com.arira.ngidol48.app.App.Companion.helper
import com.arira.ngidol48.databinding.ActivityMainBinding
import com.arira.ngidol48.databinding.DialogBdayBinding
import com.arira.ngidol48.databinding.SheetDetailMemberBinding
import com.arira.ngidol48.helper.BaseActivity
import com.arira.ngidol48.helper.Config
import com.arira.ngidol48.model.Member
import com.arira.ngidol48.model.Slider
import com.arira.ngidol48.ui.event.EventActivity
import com.arira.ngidol48.ui.member.MemberActivity
import com.arira.ngidol48.ui.member.MemberCallback
import com.arira.ngidol48.ui.news.BeritaActivity
import com.arira.ngidol48.ui.setlist.SetlistActivity
import com.arira.ngidol48.utilities.Go
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity(), MemberCallback {
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusPutih()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]
        viewModel.context = this

        observerData()
        viewModel.home()

        action()

        subcribeAll()
    }

    private fun showBdayMember(bdayMember:List<Member>){
        val dialog = AlertDialog.Builder(this)
        val bindingDialog = DataBindingUtil.inflate<DialogBdayBinding>(
            LayoutInflater.from(this),
            R.layout.dialog_bday,
            null,
            false
        )
        dialog.setView(bindingDialog.root)
        dialog.setCancelable(true)

        val dialogCreate = dialog.create()
        dialogCreate.window!!.setBackgroundDrawableResource(R.color.transparant)

        bindingDialog.rvAva.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = AvaMemberAdapter(bdayMember, bdayMember.size)
        }

        var memberName = ""
        for (i in bdayMember.indices){
            memberName += if (i == bdayMember.size - 1){
                " ${bdayMember[i].nama_lengkap}"
            }else{
                "${bdayMember[i].nama_lengkap}, "
            }
        }

        bindingDialog.tvNamaMember.text = memberName

        bindingDialog.ivClose.setOnClickListener {
            dialogCreate.dismiss()
        }

        dialogCreate.setOnDismissListener {

        }
        dialogCreate.show()
    }


    fun findTodayBday(bdayMember:List<Member>):ArrayList<Member>{
        val listBday:ArrayList<Member> = ArrayList()
        for (i in bdayMember.indices){
            if (curDay == helper.convert(bdayMember[i].tanggal_lahir, "d MMMM yyyy", "d")){
                listBday.add(bdayMember[i])
            }
        }
        return listBday
    }

    @SuppressLint("SetTextI18n")
    override fun onSelect(member: Member) {
        showSheetMember(member)
    }

    private fun action(){
        binding.linMember.setOnClickListener {
            Go(this).move(MemberActivity::class.java)
        }

        binding.linSetlist.setOnClickListener {
            Go(this).move(SetlistActivity::class.java)
        }

        binding.linKalender.setOnClickListener {
            Go(this).move(EventActivity::class.java)
        }

        binding.ivSemuaBerita.setOnClickListener {
            Go(this).move(BeritaActivity::class.java)
        }
    }

    private  fun subcribeAll(){
        FirebaseMessaging.getInstance().subscribeToTopic("event").addOnSuccessListener {
        }

        FirebaseMessaging.getInstance().subscribeToTopic("news").addOnSuccessListener {
        }
    }

    fun observerData(){
        viewModel.getLoading().observe(this, Observer {
        })

        viewModel.getError().observe(this, Observer {
            it.let {
            }
        })

        viewModel.getResponse().observe(this, Observer {
            it.let {
                if (it != null) {
                    setSlider(it.slider)

                    binding.rvBerita.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = BeritaAdapter(it.news)
                    }

                    if (it.bday_member.isNotEmpty()){
                        binding.linBday.visibility = View.VISIBLE
                        binding.rvBday.apply {
                            layoutManager = GridLayoutManager(context, 3)
                            adapter = MemberAdapter(it.bday_member, this@MainActivity)
                        }

                        val bdayToday = findTodayBday(it.bday_member)
                        if (bdayToday.isNotEmpty()){
                            showBdayMember(bdayToday)
                        }

                    }else{
                        binding.linBday.visibility = View.GONE
                    }

                }
            }
        })
    }

    fun setSlider(slider: List<Slider>){
        var currentPage = 0
        if (slider.isNotEmpty()) {
            val adapter = SliderAdapter(slider as ArrayList<Slider>, this)
            binding.slider.adapter = adapter

            /*auto sliding*/
            val timerSlider: Timer
            val DELAY_MS: Long = 5000
            val PERIOD_MS: Long = 10000

            /*After setting the adapter use the timer */

            /*After setting the adapter use the timer */
            val handler = Handler(Looper.myLooper()!!)
            val Update = Runnable {
                if (currentPage == adapter.count) {
                    currentPage = 0
                }
                binding.slider.setCurrentItem(currentPage++, true)
            }

            timerSlider = Timer() // This will create a new Thread

            timerSlider.schedule(object : TimerTask() {
                // task to be scheduled
                override fun run() {
                    handler.post(Update)
                }
            }, DELAY_MS, PERIOD_MS)
            /*selesai auto sliding*/

        }
    }
}