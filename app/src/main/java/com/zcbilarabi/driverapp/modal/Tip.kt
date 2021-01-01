package com.zcbilarabi.driverapp.modal

class Tip{
    var tipId:Int?=null
    var tipName:String? =null
    var tipDescription:String? =null
    var ObjectId:String? =null
    var timestam:Long=0

constructor(){}
    constructor(id:Int,n:String,d:String,objectId:String){
        this.tipId=id
        this.tipName=n
        this.tipDescription=d
        this.ObjectId=objectId
    }
}

