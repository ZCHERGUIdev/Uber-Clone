package com.zcbilarabi.driverapp

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.zcbilarabi.driverapp.dao.TipDao
import com.zcbilarabi.driverapp.modal.Tip
import kotlinx.android.synthetic.main.activity_parse.*
import kotlin.properties.Delegates

@Suppress("DEPRECATION")
class parseActivity : AppCompatActivity() {
    var progdialog: ProgressDialog? = null
    var tipdao: TipDao? = null
    var localTiip:Tip by Delegates.observable(Tip()){
        prop,old,new->
    }
    companion object{
        lateinit var instence:parseActivity
        private set

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parse)
        instence=this
        tipdao = TipDao()
        progdialog = ProgressDialog(this)
        progdialog?.setMessage("Pleaze Wait")
        progdialog?.setCancelable(true)
        progdialog?.show()
    }


    fun Parsebottoniscliked(view: View) {
        var btn = view as Button

        if (btn == bnt1) {

            if (NetworkisConnected()) {
                Log.e("app", "network is connected")
            } else {
                Log.e("app", "network is not  connected")
                AlertDialog.Builder(this)
                    .setTitle("No connection")
                    .setMessage("Pleaze Cheack Your Internet and Try again")
                    .setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, i: Int -> }
                    .setIcon(android.R.drawable.ic_dialog_alert).show()

            }
        } else if (btn == btn2) {
            var tip = Tip(1, "title", "this is description", "")
            tipdao?.creatrecord(tip)
        } else if (btn == btn4) {
            var tip = Tip(1, "title1xx", "this is description", "Wbqjyj3pRp")
            tipdao?.updaterecord(tip)
        }else if (btn == btn3) {
            var tip = Tip(1, "title1x", "", "")
            var r= tipdao?.getRecord(tip)

        }else if (btn == btn5) {
            var tip = Tip(1, "title1xx", "this is description", "Wbqjyj3pRp")
            tipdao?.deleterecord(tip)

        }

    }

    private fun showAlertDialog(s:String,s1:String) {
         AlertDialog.Builder(this)
             .setTitle(s).setMessage(s1)
             .setMessage(s1).setPositiveButton(android.R.string.ok){dialog: DialogInterface?, which: Int ->  }
             .setNegativeButton(android.R.string.cancel){dialog: DialogInterface?, which: Int ->  }
             .setIcon(android.R.drawable.ic_dialog_alert).show()


    }



    private fun NetworkisConnected(): Boolean {
        var connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkinfo = connectivityManager.activeNetworkInfo
        return networkinfo != null && networkinfo.isConnected
    }


}
