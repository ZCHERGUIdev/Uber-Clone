package com.zcbilarabi.driverapp.dao

import android.util.Log
import com.zcbilarabi.driverapp.modal.Tip
import com.zcbilarabi.driverapp.parseActivity
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.SaveCallback

class TipDao {

    constructor() {}
// Create Record
    fun creatrecord(rec: Tip) {

        var obj = ParseObject(Tip::class.java.simpleName)
        obj.put("title", rec.tipName)
        obj.put("description", rec.tipDescription)

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
    fun updaterecord(rec: Tip) {

        var query = ParseQuery<ParseObject>(Tip::class.java.simpleName)
        query.getInBackground(rec.ObjectId, { obj, e ->
       if (e==null) {

           obj.put("title", rec.tipName)
           obj.put("description", rec.tipDescription)
           obj.saveInBackground {
           }
       }else{
           creatrecord(rec)
       }
        })
    }
    // Read REcorde
    fun getRecord(rec: Tip):Tip {
        var record=Tip()
        var query = ParseQuery.getQuery<ParseObject>(Tip::class.java.simpleName)
        query.whereEqualTo("title",rec.tipName)
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

    private fun toLocalrecord(parseTip: ParseObject?): Tip {
        var tip=Tip()
        tip.ObjectId=parseTip!!.objectId
        tip.tipName=parseTip.getString("title")
        tip.tipDescription=parseTip.getString("description")
        parseActivity.instence.localTiip=tip
        return tip
    }

    fun deleterecord(rec: Tip) {

          //get and delete
        var query = ParseQuery<ParseObject>(Tip::class.java.simpleName)
        query.getInBackground(rec.ObjectId, { obj, e ->
            if (e==null) {
            obj.deleteInBackground()
            }
        })
    }

}