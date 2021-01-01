package com.zcbilarabi.driverapp
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.zcbilarabi.driverapp.dao.OrderDao
import com.zcbilarabi.driverapp.dao.UserDao
import com.zcbilarabi.driverapp.modal.Order
import com.zcbilarabi.driverapp.modal.OrderStatus

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.parse.ParseGeoPoint
import kotlinx.android.synthetic.main.activity_driver.*

class DriverActivity:AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var usedao: UserDao? = null
    var orderdao: OrderDao? = null
    var progdialog: ProgressDialog? = null
    var currentlocation: Location? = null
    var currentMarker: Marker? = null
    var orderIsActive=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        btnApprouveWasali.text="Approve Wasali"
        btnCompletOrder.text="Complete Order"

        usedao = UserDao()
        orderdao = OrderDao()
        progdialog = ProgressDialog(this)
        progdialog?.setMessage("Pleaze Wait...")
        progdialog?.setCancelable(true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.Drivermap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        permissionCheck()


    }

    private fun permissionCheck() {
        var ACSESSLOCATIONREQUESTCODE =200
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
        Toast.makeText(this.baseContext, "User enable the location service", Toast.LENGTH_LONG).show()
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))

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

        var myintet=getIntent()
        var username= myintet.getStringExtra("username")
        var clat= myintet.getDoubleExtra("clat",0.0)
        var clng=  myintet.getDoubleExtra("clng",0.0)
        var dlat= myintet.getDoubleExtra("dlat",0.0)
        var dlng=  myintet.getDoubleExtra("dlng",0.0)
        var driverusername= myintet.getStringExtra("driverusername")

        currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(LatLng(clat,clng))
                .title("I ma Rider " + username)
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
     //   mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
        val silwad = LatLng(dlat,dlng)
        mMap.addMarker(
            MarkerOptions().position(silwad).title("Driver is coming( "+driverusername+" )")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(silwad,15f))

         usedao?.updateUserLocation(ParseGeoPoint(p0.latitude,p0.longitude))

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


    fun approveWasali(view: View) {
        progdialog?.show()

        var myintet=getIntent()
        var username= myintet.getStringExtra("username")
        var clat= myintet.getDoubleExtra("clat",0.0)
        var clng=  myintet.getDoubleExtra("clng",0.0)
        var dlat= myintet.getDoubleExtra("dlat",0.0)
        var dlng=  myintet.getDoubleExtra("dlng",0.0)
        var driverusername= myintet.getStringExtra("driverusername")


        var order = Order("",username,driverusername,clat,clng,dlat,dlng)
        order.orderStatus = OrderStatus.inprogress.toString()
        orderdao?.updaterecordWithAllDetailsWithCalback(order,{Order->
       // show location the custumer from google maps
           var intent =Intent(android.content.Intent.ACTION_VIEW,
               Uri.parse("http://maps.google.com/maps?daddr="+clat+","+clng))
            startActivity(intent)
            progdialog?.hide()
        })
    }
    fun completWasali(view: View) {
        progdialog?.show()
        var myintet=getIntent()
        var username= myintet.getStringExtra("username")
        var clat= myintet.getDoubleExtra("clat",0.0)
        var clng=  myintet.getDoubleExtra("clng",0.0)
        var dlat= myintet.getDoubleExtra("dlat",0.0)
        var dlng=  myintet.getDoubleExtra("dlng",0.0)
        var driverusername= myintet.getStringExtra("driverusername")


        var order = Order("",username,driverusername,clat,clng,dlat,dlng)
        order.orderStatus = OrderStatus.completed.toString()
        orderdao?.updaterecordWithAllDetailsWithCalback(order,{Order->
            progdialog?.hide()
        })

    }

    fun backToorders(view: View) {
        var myintet = Intent(this.baseContext, OrderActivity::class.java)
        startActivity(myintet)
    }
}








