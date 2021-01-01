package com.zcbilarabi.driverapp.modal
import com.parse.ParseGeoPoint

enum class OrderStatus {
    created,pendding,cancel,inprogress,completed
}

var status: OrderStatus = OrderStatus.created

class Order{ // parse acsept one Geo Point


    var objectId:String?=null
    var customerUsername:String?=null
    var driverUsername:String?=null
    var customerLocation:ParseGeoPoint?=null // Order
    var driverLocation:ParseGeoPoint?=null // Driver(User)
    var orderStatus:String?=OrderStatus.created.toString() // status -> Location
    constructor(){

    }

    constructor(id:String,cun:String,dun:String,clat:Double,clng:Double,dlat:Double,dlng:Double){
        this.objectId=id
        this.customerUsername=cun
        this.driverUsername=dun
        this.customerLocation=ParseGeoPoint(clat,clng)
        this.driverLocation=ParseGeoPoint(dlat,dlng)
    }

}