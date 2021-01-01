package com.zcbilarabi.driverapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zcbilarabi.driverapp.dao.UserDao
import com.zcbilarabi.driverapp.modal.User

class signinloginActivity : AppCompatActivity() {
    var usedao: UserDao? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signinlogin)
        usedao= UserDao()
        var user1= User("zakaria chergui","ensi2014","info@gmail.com","")
        var user2= User("Mohamed chergui","ensi2014","cherguibabali@gmail.com","")
       // usedao?.SignUp(user2)
      //      usedao?.LogIn(user2)
       // ParseUser.logOut()
        usedao?.checkLoggedIn()
    }
}
