package com.zcbilarabi.driverapp

import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.zcbilarabi.driverapp.dao.OrderDao
import com.zcbilarabi.driverapp.dao.UserDao
import com.zcbilarabi.driverapp.modal.Order
import com.zcbilarabi.driverapp.modal.OrderStatus
import com.zcbilarabi.driverapp.modal.User

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_rider.*

class RiderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var usedao: UserDao? = null
    var orderdao: OrderDao? = null
    var progdialog: ProgressDialog? = null
    var currentorder:Order= Order()
    var currentlocation: Location? = null
    var currentMarker: Marker? = null
    var orderIsActive=false
    enum class OrderBtnStatus {
        REQUESTWASALI,CANCELWASALI
    }
    enum class REQUESTInfoStatus {
        NOTINITIALIZED,CANCELED,PENDING
    }

    lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rider)
        btnRequestWasali.text="REQUEST WASALI"
        textviewRequestInfo.text="Wasali is not initialized"

        usedao = UserDao()
        orderdao = OrderDao()
        progdialog = ProgressDialog(this)
        progdialog?.setMessage("Pleaze Wait...")
        progdialog?.setCancelable(true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.Ridermap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        permissionCheck()
        mHandler= Handler()
        checkOrderStatus()

    }
    private fun checkOrderStatus() {
    //code
      if (currentorder!=null)
      {
       var ord=currentorder
       ord.orderStatus=OrderStatus.inprogress.toString()
       orderdao?.getRecordsByCustomerUserName(ord,{listofOrders->

           for (i in 0..listofOrders.size-1)
           {
               if (listofOrders[i].orderStatus==OrderStatus.inprogress.toString())
               {
                  ord.driverUsername=listofOrders[i].driverUsername
                   ord.orderStatus=listofOrders[i].orderStatus
                   break
               }
           }
           //cnt
           if (ord.orderStatus==OrderStatus.inprogress.toString()){
               var us=User()
               us.userName=ord.driverUsername
               usedao!!.getUserByUsername(us,{returnedUser ->
var loc=LatLng(returnedUser.location!!.latitude,returnedUser.location!!.longitude)
                   currentMarker = mMap.addMarker(
                       MarkerOptions()
                           .position(loc)
                           .title("I ma Hre")
                           .snippet("this is my current location")
                           .icon(
                               BitmapDescriptorFactory.fromBitmap(
                                   scaleimage(
                                       resources,
                                       R.drawable.markertaxi,
                                       100
                                   )
                               )
                           )
                   )

               })
           }

       })






          mHandler.postDelayed({
              checkOrderStatus()
          },3000)//3scound dailly task exct
      }
    }


    private fun permissionCheck() {
        var ACSESSLOCATIONREQUESTCODE = 0
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    ACSESSLOCATIONREQUESTCODE
                )
                return
            }
        }
        // contunu becouse u have permission
        Getcurruentlocation();
    }


    private fun Getcurruentlocation() {
        Toast.makeText(this.baseContext, "User enable the location service", Toast.LENGTH_LONG)
            .show()
        var locationOnmap = MapLocationListner(this.baseContext)
        var locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0f, locationOnmap)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val loc = LatLng(35.3904125, 0.1494988)
        mMap.addMarker(
            MarkerOptions().position(loc).title("Marker in mascara").icon(
                BitmapDescriptorFactory.fromBitmap(scaleimage(resources, R.drawable.img, 100))
            )
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,15f))

        var locationOnmap = MapLocationListner(this.baseContext)
        var locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0f, locationOnmap)
        var locationLastKnow = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (locationLastKnow != null) {
            updatelocation(locationLastKnow)
        }
    }


    inner class MapLocationListner : LocationListener {
        var context: Context? = null

        constructor(context: Context) : super() {
            currentlocation = Location("Start")
            currentlocation!!.latitude = 0.0
            currentlocation!!.longitude = 0.0
            this.context = context
        }

        override fun onLocationChanged(p0: Location?) {
            updatelocation(p0)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

    }

    private fun updatelocation(p0: Location?) {
        Toast.makeText(
            this.baseContext,
            "Location hase been Change" + p0!!.longitude + " " + p0!!.latitude,
            Toast.LENGTH_LONG
        ).show()
        mMap.clear()
        val loc = LatLng(p0!!.latitude, p0!!.longitude)
        if (currentMarker != null) {
            currentMarker!!.remove()
        }

        //updatecurrent order(location) if its available
        if (currentorder!=null){
           if (currentorder!!.orderStatus==OrderStatus.pendding.toString()){
               currentorder.customerLocation= ParseGeoPoint(loc.latitude,loc.longitude)
              // update current location if its change
               if(orderIsActive){
                   orderdao?.updaterecord(currentorder)
               }
           }
        }
        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(loc)
                .title("I ma Hre")
                .snippet("this is my current location")
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        scaleimage(
                            resources,
                            R.drawable.img,
                            100
                        )
                    )
                )
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))


        currentlocation = p0

    }

    fun scaleimage(res: Resources, id: Int, sidesize: Int): Bitmap {
        var b: Bitmap? = null
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        b = BitmapFactory.decodeResource(res, id, o)
        var scale = 1
        var sc = 0.0f
        if (o.outHeight > o.outWidth) {
            sc = (o.outHeight / sidesize).toFloat()
            scale = Math.round(sc)
        } else {
            sc = (o.outWidth / sidesize).toFloat()
            scale = Math.round(sc)
        }

        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        b = BitmapFactory.decodeResource(res, id, o2)
        return b

    }

fun orderWasali(view: View) {


    if (orderIsActive == false) {

        Log.d("Uber", "request uber driver")
        orderIsActive = true

        btnRequestWasali.text = OrderBtnStatus.CANCELWASALI.toString()
        textviewRequestInfo.text =REQUESTInfoStatus.PENDING.toString()
        //user can order a new uber
        //new order must be pending
        //before we make an order, all previous pending orders must be canceled
        //user cannot make a new order while he/she has a pending order
        //user must cancel an existing order before making a new one

        processOrder(OrderStatus.pendding.toString())

    } else {

        orderIsActive =false
        btnRequestWasali.text = OrderBtnStatus.REQUESTWASALI.toString()
        textviewRequestInfo.text = REQUESTInfoStatus.CANCELED.toString()

        processOrder(OrderStatus.cancel.toString())

    }

}

    private fun processOrder(orderGivenStatus: String) {


        progdialog?.show()
        var order = Order("", ParseUser.getCurrentUser().username, "", currentlocation!!.latitude, currentlocation!!.longitude, 0.0, 0.0)

        order.orderStatus = OrderStatus.pendding.toString()


        //check if there is pending records, canel them and make a new order
        orderdao?.getPendingRecordsByCustomerUserName(order, { listOfOrders ->

            if (listOfOrders.size > 0) {
                //there are pending orders, go and cancel them
                //make a new order

                //cancel all pending orders

                orderdao?.cancelrecordsWithCallback(listOfOrders, { isUpdated ->

                    if (isUpdated) {


                        if (orderGivenStatus == OrderStatus.pendding.toString()) {
                            //creat pendding order
                            orderdao?.creatrecordWithCalback(order, { returnedOrder ->
                                progdialog?.hide()
                                currentorder=returnedOrder
                            })
                        }
                        progdialog?.hide()

                    } else {
                        currentorder=order
                        progdialog?.hide()
                    }

                })


            } else {
                if (orderGivenStatus == OrderStatus.pendding.toString()) {
                    orderdao?.creatrecordWithCalback(order, { returnedOrder ->
                        currentorder=returnedOrder
                        progdialog?.hide()
                    })
                }
            }
        })
    }
    }





