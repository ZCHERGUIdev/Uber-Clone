package com.zcbilarabi.driverapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.zcbilarabi.driverapp.dao.OrderDao
import com.zcbilarabi.driverapp.dao.UserDao
import com.zcbilarabi.driverapp.modal.Order
import com.zcbilarabi.driverapp.modal.OrderStatus
import com.google.android.gms.maps.model.LatLng
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_order.*

class OrderActivity : AppCompatActivity() ,LocationListener {

    var listOfOrders=ArrayList<Order>()
    var listOfOrderString=ArrayList<String>()
    var adapter:ArrayAdapter<String>?=null
    var usernames=ArrayList<String>()
    var latitudes=ArrayList<Double>()
    var longitudes=ArrayList<Double>()
    var currentlocation:Location?=null
    var usedao: UserDao? = null
    var orderdao: OrderDao? = null
    var progdialog: ProgressDialog? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)


        usedao = UserDao()
        orderdao = OrderDao()
        progdialog = ProgressDialog(this)
        progdialog?.setMessage("Pleaze Wait...")
        progdialog?.setCancelable(true)
        listOfOrderString!!.add("Finding Orders...")
       adapter= ArrayAdapter(this.baseContext,R.layout.tipviewcell,R.id.txtTip,listOfOrderString)
       lslOrdersview.adapter=adapter
        lslOrdersview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, i, id ->
            var myintet = Intent(this.baseContext, DriverActivity::class.java)
            myintet.putExtra("username",usernames[i])
            myintet.putExtra("clat",latitudes[i])
            myintet.putExtra("clng",longitudes[i])
            myintet.putExtra("dlat",currentlocation!!.latitude)
            myintet.putExtra("dlng",currentlocation!!.longitude)
            myintet.putExtra("driverusername",ParseUser.getCurrentUser().username)
            startActivity(myintet)
        })
        permissionCheck()
        var locationOnmap = this
        var locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0f, locationOnmap)
        var locationLastKnow = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (locationLastKnow != null) {
            container.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
            container.setOnRefreshListener {
                updatelocation(locationLastKnow)
            }
            container.post {
                updatelocation(locationLastKnow)
            }

        }

    }
    private fun permissionCheck() {
        var ACSESSLOCATIONREQUESTCODE = 200
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
        var locationOnmap = this
        var locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 0f, locationOnmap)
    }
    private fun updatelocation(p0: Location?) {
        Toast.makeText(
            this.baseContext,
            "Location hase been Change" + p0!!.longitude + " " + p0!!.latitude,
            Toast.LENGTH_LONG
        ).show()
        val loc = LatLng(p0!!.latitude, p0!!.longitude)
        var driverLocation=ParseGeoPoint(p0!!.latitude, p0!!.longitude)
        currentlocation = p0
        usedao?.updateUserLocation(ParseGeoPoint(currentlocation!!.latitude, currentlocation!!.longitude))
        var order=Order("","",ParseUser.getCurrentUser().username,0.0,0.0,currentlocation!!.latitude,currentlocation!!.longitude)
        order.orderStatus=OrderStatus.pendding.toString()
        progdialog?.show()
        //get the near by orders to driver
        orderdao?.getNearbyRecords(order,{listoforders ->
            //get pendding+whitoutDrivers
            if (listoforders.size>0)
            {
                listOfOrderString.clear()
                usernames.clear()
                latitudes.clear()
                longitudes.clear()
                for (i in 0..listoforders.size-1)
                {
              var dstninklm=driverLocation.distanceInKilometersTo(listoforders[i].customerLocation)
                    var utiliseddistence=Math.round(dstninklm * 10).toDouble() / 10
                    listOfOrderString.add(utiliseddistence.toString()+ " Km"+" By "+ listoforders[i].customerUsername)
                    usernames.add(listoforders[i].customerUsername.toString())
                    latitudes.add(listoforders[i].customerLocation!!.latitude)
                    longitudes.add(listoforders[i].customerLocation!!.longitude)
                }
                adapter?.notifyDataSetChanged()
                progdialog?.hide()

            }
        })
        if(container.isRefreshing)
        {
            container.isRefreshing=false
        }
    }
    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

}
