package com.zcbilarabi.driverapp.modal

import com.parse.ParseGeoPoint

class User{

    var userName:String? =null
    var userEmail:String? =null
    var ObjectId:String? =null
    var password:String? =null
     var location:ParseGeoPoint?=null
    constructor(){}
    constructor(u:String,p:String,e:String,id:String){
        this.userName=u
        this.password=p
        this.userEmail=e
        this.ObjectId=id
    }
}
