package com.arira.ngidol48.ui.detailEvent

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arira.ngidol48.R
import com.arira.ngidol48.adapter.MemberAdapter
import com.arira.ngidol48.adapter.SongAdapter
import com.arira.ngidol48.databinding.ActivityDetailEventBinding
import com.arira.ngidol48.databinding.SheetDetailMemberBinding
import com.arira.ngidol48.databinding.SheetLaguBinding
import com.arira.ngidol48.helper.BaseActivity
import com.arira.ngidol48.helper.Config
import com.arira.ngidol48.helper.Config.extra_model
import com.arira.ngidol48.helper.Helper
import com.arira.ngidol48.model.Event
import com.arira.ngidol48.model.Member
import com.arira.ngidol48.model.Song
import com.arira.ngidol48.ui.lagu.LaguCallback
import com.arira.ngidol48.ui.member.MemberCallback
import com.arira.ngidol48.ui.myWeb.MyWebActivity
import com.arira.ngidol48.utilities.Go
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailEventActivity : BaseActivity(), MemberCallback, LaguCallback {
    private lateinit var binding: ActivityDetailEventBinding
    private var event:Event = Event()
    private lateinit var viewModel:DetailEventViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_event)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event)
        event = intent.getParcelableExtra(extra_model) ?: Event()

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DetailEventViewModel::class.java]
        viewModel.context =this

        setToolbar(getString(R.string.teks_event), binding.toolbar)

        observerData()

        viewModel.hitDetail(event.event_id)

        action()
    }

    private fun action(){
        binding.tvTiketDll.setOnClickListener {
            val url = "https://jkt48.com/theater/schedule/id/${event.event_id}?lang=id"
            Go(this).move(MyWebActivity::class.java, url = url)
        }
    }

    override fun onSelectSong(lagu: Song) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        val bindingSheet: SheetLaguBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.sheet_lagu,
            null,
            false
        )
        bottomSheetDialog.dismissWithAnimation = true

        bottomSheetDialog.setContentView(bindingSheet.root)
        bindingSheet.tvJudul.text = lagu.judul
        bindingSheet.tvLirik.text = lagu.lirik
        bindingSheet.tvSetlist.text = lagu.nama
        bottomSheetDialog.show()
    }

    override fun onSelect(member: Member) {
        showSheetMember(member)
    }

    fun observerData(){
        viewModel.getLoading().observe(this, Observer {
            it.let {
                if (it){
                    binding.divKosong.visibility = View.GONE
                    binding.shimmer.visibility = View.VISIBLE
                    binding.shimmer.startShimmer()
                }else{
                    binding.shimmer.visibility = View.GONE
                    binding.shimmer.stopShimmer()
                }
            }
        })

        viewModel.getError().observe(this, {
            it.let {
                if (it != null){
                    binding.divKosong.visibility = View.VISIBLE

                }
            }
        })

        viewModel.getResponse().observe(this, {
            it.let {
                if (it != null) {

                    /*member perform*/
                    if (it.members_perform.isNotEmpty()){
                        binding.rvMemberPerform.apply {
                            layoutManager = GridLayoutManager(context, 3)
                            adapter = MemberAdapter(it.members_perform, this@DetailEventActivity)
                        }
                        binding.divMemberPerform.visibility = View.VISIBLE
                    }else{
                        binding.divMemberPerform.visibility = View.GONE
                    }

                    /*member bday*/
                    if (it.members_bday.isNotEmpty()){
                        binding.rvBday.apply {
                            layoutManager = GridLayoutManager(context, 3)
                            adapter = MemberAdapter(it.members_bday, this@DetailEventActivity)
                        }
                        binding.divBday.visibility = View.VISIBLE
                    }else{
                        binding.divBday.visibility = View.GONE
                    }

                    /*set list / song list*/
                    if (it.song_list.isNotEmpty()){
                        if (!it.song_list[0].cover.isNullOrEmpty()){
                            Glide.with(this)
                                .asBitmap()
                                .load(Config.BASE_STORAGE + it.song_list[0].cover)
                                .into(object : CustomTarget<Bitmap>(){
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                                    ) {
                                        binding.ivEventCover.setImageBitmap(resource)
                                        binding.ivEventCover.setBackgroundColor(Helper.getDominantColor(resource))
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {

                                    }
                                })

//                            Glide.with(this).load(Config.BASE_STORAGE + it.song_list[0].cover).into(binding.ivEventCover)
                        }
                        binding.rvSonglist.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = SongAdapter(it.song_list, this@DetailEventActivity)
                        }
                        binding.divSetlist.visibility = View.VISIBLE
                    }else{
                        binding.divSetlist.visibility = View.GONE
                    }

                    /*event data*/
                    if (it.event.id.isNotEmpty()){
                        Glide.with(this).load(Config.BASE_STORAGE_JKT + it.event.badge_url).into(binding.ivBadge)
                        binding.tvNamaEvent.text = it.event.event_name
                        if(it.event.event_time.isEmpty()){
                            binding.tvTanggal.text = "${it.event.tanggal} ${it.event.bulan_tahun}"
                        }else{
                            binding.tvTanggal.text = "${it.event.event_time}, ${it.event.tanggal} ${it.event.bulan_tahun}"
                        }
                    }
                }
            }
        })
    }
}