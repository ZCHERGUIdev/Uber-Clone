package com.zcbilarabi.driverapp.dao

import android.util.Log
import com.zcbilarabi.driverapp.modal.Order
import com.zcbilarabi.driverapp.modal.OrderStatus
import com.parse.*


class OrderDao {
    var userdao:UserDao?=null

    constructor() {
        var userDao=UserDao()
        var obj = ParseObject(Order::class.java.simpleName)
        var defaultAcl=ParseACL()
        defaultAcl.publicReadAccess=true
        defaultAcl.publicWriteAccess=true
        obj.acl=defaultAcl
    }
    /*
       var objectId:String?=null
    var customerUsername:String?=null
    var driverUsername:String?=null
    var cistomerLocation:ParseGeoPoint?=null // Order
    var driverLocation:ParseGeoPoint?=null // Driver(User)
    var orderStatus:String?=null // status -> Location
    */

    // Create Record
    fun creatrecord(rec: Order) {

        var obj = ParseObject(Order::class.java.simpleName)
        obj.put("customerUsername", rec.customerUsername)
        obj.put("cistomerLocation", rec.customerLocation)
        obj.put("status", OrderStatus.pendding.toString())
        obj.saveInBackground(object : SaveCallback {
            override fun done(e: ParseException?) {
                if (e == null) {
                    Log.i("app", "record is saved")
                } else {
                    Log.e("App", "Record is not saved" + e.printStackTrace())
                }
            }
        })
    }
    // update record
    fun updaterecord(rec: Order) {

        var query = ParseQuery<ParseObject>(Order::class.java.simpleName)
        query.getInBackground(rec.objectId, { obj, e ->
            if (e==null) {

                obj.put("customerUsername", rec.customerUsername)
                obj.put("customerLocation", rec.customerLocation)
                obj.put("status", rec.orderStatus)
                obj.saveInBackground {
                    userdao?.updateUserLocation(rec.customerLocation)
                }
            }else{
                creatrecord(rec)
            }
        })
    }
    // Read REcorde
    fun getRecordByCustomerUserName(rec: Order):Order {
        var record=Order()
        var query = ParseQuery.getQuery<ParseObject>(Order::class.java.simpleName)
        query.whereEqualTo("customerUsername",rec.customerUsername)
        query.getFirstInBackground({obj,e->
            if (e==null) {
                Log.i("APP",obj.getString("description"))
                record =toLocalrecord(obj)
            }else{
                Log.i("APP","description"+e.message)
            }
        })

        return record
    }
    private fun toLocalrecord(parseOrder: ParseObject?): Order {
        var Order=Order()
        Order.objectId=parseOrder!!.objectId
        Order.customerUsername=parseOrder.getString("customerUsername")
        Order.customerLocation=parseOrder.getParseGeoPoint("customerLocation")
        Order.orderStatus=parseOrder.getString("status")
        return Order
    }
    private fun toSingleRecord(parseOrder: ParseObject?): Order {
        var Order=Order()
        Order.objectId=parseOrder!!.objectId
        Order.customerUsername=parseOrder.getString("customerUsername")
        Order.customerLocation=parseOrder.getParseGeoPoint("customerLocation")
        Order.orderStatus=parseOrder.getString("status")
        Order.driverUsername=parseOrder.getString("driverUsername")
        return Order
    }
    private fun toLocalRecordDeleted(parseOrder: ParseObject?){
        var Order=Order()
        Order.objectId=parseOrder!!.objectId
        Order.customerUsername=parseOrder.getString("customerUsername")
        Order.customerLocation=parseOrder.getParseGeoPoint("customerLocation")
        Order.orderStatus=parseOrder.getString("status")
    }
    fun deleterecord(rec: Order) {


        //get and delete
        var query = ParseQuery<ParseObject>(Order::class.java.simpleName)
        query.getInBackground(rec.objectId, { obj, e ->
            if (e==null) {
                toLocalRecordDeleted(obj)
                obj.deleteInBackground()
            }else{
                var po=ParseObject(Order::class.java.simpleName)
                po.put("title","not found")
                toLocalRecordDeleted(po)
            }
        })
    }
    fun getRecords(rec: Order){
        var query = ParseQuery.getQuery<ParseObject>(Order::class.java.simpleName)
        query.whereEqualTo("customerUsername",rec.customerUsername)
        query.findInBackground ({ objs, e ->
            if (e==null){
                var item= mutableListOf<Order>()
                for (i in 0..item.size)
                {
                    Log.i("APP",item[i].customerUsername.toString())
                }

            }else
            {
                  Log.e("APP",e.message)
            }
        })


    }
    fun creatrecordWithCalback(rec: Order, callback:(returnedOrder:Order)->Unit){
        var obj = ParseObject(Order::class.java.simpleName)
        obj.put("customerUsername",rec.customerUsername)
        obj.put("customerLocation", rec.customerLocation)
        obj.put("status", rec.orderStatus)
        obj.saveInBackground(object : SaveCallback {
            override fun done(e: ParseException?) {
                if (e == null) {
                    Log.i("app", "record is saved")
                    var o2=toSingleRecord(obj)
                    callback(o2)
                } else {
                    Log.e("App", "Record is not saved" + e.printStackTrace())
                    callback(Order())
                }
            }
        })
    }

    fun getPendingRecordsByCustomerUserName(order: Order, callback:(listOfRecord:MutableList<Order>)->Unit) {
        var query = ParseQuery.getQuery<ParseObject>(Order::class.java.simpleName)
        query.whereEqualTo("status",order.orderStatus)
        query.whereEqualTo("customerUsername",order.customerUsername)

        query.findInBackground ({ objs, e ->
            if (e == null) {
                var item = mutableListOf<Order>()
                objs.mapTo(item){toSingleRecord(it)}
                callback(item)
            } else {
               Log.e("APP",e.message)
            }
        })

}

    fun getRecordsByCustomerUserName(order: Order, callback:(listOfRecord:MutableList<Order>)->Unit) {
        var query = ParseQuery.getQuery<ParseObject>(Order::class.java.simpleName)
        query.whereEqualTo("status",order.orderStatus)
        query.whereEqualTo("customerUsername",order.customerUsername)

        query.findInBackground ({ objs, e ->
            if (e == null) {
                var item = mutableListOf<Order>()
                objs.mapTo(item){toSingleRecord(it)}
                callback(item)
            } else {
                Log.e("APP",e.message)
            }
        })

    }



    fun cancelrecordsWithCallback(listofOrders: MutableList<Order>, callback:(isUpdated:Boolean)->Unit) {
        for (i in 0..listofOrders.size-1) {
         listofOrders[i].orderStatus = OrderStatus.cancel.toString()
            updaterecord( listofOrders[i])
        }
        callback(true)
    }

    fun getNearbyRecords(order: Order, callback:(listofRecords:MutableList<Order>)->Unit) {

        var query = ParseQuery.getQuery<ParseObject>(Order::class.java.simpleName)
        query.whereEqualTo("status",OrderStatus.pendding.toString())
        query.whereNear("customerLocation",order.driverLocation)
        query.setLimit(100)
        query.findInBackground ({ objs, e ->
            if (e == null) {
                var item = mutableListOf<Order>()
                objs.mapTo(item){toSingleRecord(it)}
                callback(item)
            } else {
                Log.e("APP",e.message)
            }
        })

    }

    fun updaterecordWithAllDetailsWithCalback(orderStarter: Order, callback:(retrnedOrder:Order)->Unit ) {

        //get all pendding orders for the given username
        var query1 = ParseQuery.getQuery<ParseObject>(Order::class.java.simpleName)
        query1.whereEqualTo("customerUsername",orderStarter.customerUsername)
        if (orderStarter.orderStatus==OrderStatus.pendding.toString())
        { query1.whereEqualTo("status",OrderStatus.inprogress.toString()) }
        if (orderStarter.orderStatus==OrderStatus.completed.toString())
        {
            query1.whereEqualTo("status",OrderStatus.inprogress.toString())
            query1.whereEqualTo("driverUsername",orderStarter.driverUsername)
        }
        query1.orderByDescending("createdAt")
        query1.findInBackground ({ objs, e ->
            if (e == null) {
                var item = mutableListOf<Order>()
                objs.mapTo(item){toSingleRecord(it)}
                var obj=item[0]
                //get and save
                var query = ParseQuery<ParseObject>(Order::class.java.simpleName)
                query.getInBackground(obj.objectId, { orderParseObject, e ->
                    if (e==null) {
                        orderParseObject.put("customerUsername", orderStarter.customerUsername)
                        orderParseObject.put("driverUsername",orderStarter.driverUsername)
                        orderParseObject.put("customerLocation", orderStarter.customerLocation)
                        orderParseObject.put("status", orderStarter.orderStatus)
                        orderParseObject.saveInBackground {
                            obj.orderStatus= orderStarter.orderStatus
                            callback(obj)
                            // write your code after save
                         // userdao?.updateUserLocation(obj.customerLocation)
                        }
                    }
                })



            } else {
                Log.e("APP",e.message)
            }
        })

    }
}




