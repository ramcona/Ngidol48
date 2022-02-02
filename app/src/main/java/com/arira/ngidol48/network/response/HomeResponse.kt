package com.arira.ngidol48.network.response

import com.arira.ngidol48.model.*
import java.io.Serializable

class HomeResponse : Serializable {

    val error:Boolean  = false
    val code:Int = 0
    val message:String = ""
    var slider:List<Slider> = ArrayList()
    var news:List<Berita> = ArrayList()
    var bday_member:List<Member> = ArrayList()

}