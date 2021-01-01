package com.zcbilarabi.driverapp.dao
import android.util.Log
import com.zcbilarabi.driverapp.modal.User
import com.parse.*

class UserDao {
    var parseuser:ParseUser?=null
    constructor() {
        parseuser= ParseUser()
    }
    //sign up=regester=new account
    //sign in=login
    //sign out
    //update
    fun LogIn(user:User){
        ParseUser.logInInBackground(user.userName,user.password,
            {pUser,ex->
                if (pUser!=null){
                    Log.i("singin","sign in succesfully")
                }else{
                    Log.i("signin","Failed"+ex.printStackTrace())
                }
            })
    }
    public fun SignUp(user: User){
        parseuser?.setPassword(user.password)
        parseuser?.username=user.userName
        parseuser?.email=user.userEmail
        parseuser?.signUpInBackground(object :SignUpCallback{
            override fun done(e: ParseException?) {
                if (e==null){
                    //done
                    Log.i("app","sign up succesfully")
                }else{Log.e("Signup","Failed "+e.message)}
            }
        })
    }
    fun checkLoggedIn() :Boolean{
        if (ParseUser.getCurrentUser()!=null){
            Log.i("Appp"," usr signed in succesfully")
            return true
        }else{
            Log.i("Appp","usr is not signin")
            return false
        }
        return false
    }
    fun tosinglerecord(parseUser:ParseUser,pass:String):User{
        var user=User()
        user.ObjectId=parseUser.objectId
        user.userName=parseUser.username
        user.password=pass
        return user
    }

    fun LogInWithCallback(user: User, Callback: (returnedUser:User)->Unit) {

        ParseUser.logInInBackground(user.userName,user.password,
            {pUser,ex->
                if (pUser!=null){
                    Log.i("singin","sign in succesfully")
                    Callback(tosinglerecord(pUser,user.password.toString()))
                }else{
                    Log.i("signin","Failed"+ex.printStackTrace())
                    Callback(User())
                }
            })

    }

    fun signUpWithCallback(user: User, Callback: (returnedUser:User)->Unit) {

        parseuser?.setPassword(user.password)
        parseuser?.username=user.userName
        parseuser?.signUpInBackground(object :SignUpCallback{
            override fun done(e: ParseException?) {
                if (e==null){
                    //done
                    Log.i("app","sign up succesfully")
                    Callback(user)
                }else{
                    Log.e("Signup","Failed"+e.message.toString())
                    Callback(User())
                }
            }

        })
    }

    fun updateUserLocation(location: ParseGeoPoint?) {
       ParseUser.getCurrentUser().put("Location",location)
        ParseUser.getCurrentUser().saveInBackground()
    }


    fun toLocalrecord(parseUser: ParseObject):User{
     var user=User()
        user.ObjectId=parseUser.objectId
        user.userName=parseUser.getString("username")
        user.location=parseUser.getParseGeoPoint("Location")

        return user
    }


    fun getUserByUsername(rec:User,callback:(returnedUser:User)->Unit){
        var record=User()
        var query=ParseUser.getQuery()
        query.whereEqualTo("username",rec.userName)
        query.getFirstInBackground({obj,e->
            if (obj==null)
            {
               Log.e("errur ",e.message)
            }else{
             Log.i("AAP",obj.get("Location").toString())
             record= toLocalrecord(obj)
               callback(record)
            }

        })


    }

}






