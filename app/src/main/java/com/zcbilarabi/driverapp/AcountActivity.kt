package com.zcbilarabi.driverapp

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zcbilarabi.driverapp.dao.UserDao
import com.zcbilarabi.driverapp.modal.User
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_acount.*

class AcountActivity : AppCompatActivity() {
    var usedao: UserDao? = null
    var progdialog: ProgressDialog? = null

    enum class AccountStatus(var status: String) {
        LOGIN("login"), SIGNUP("signup")
    }
    var status: AccountStatus = AccountStatus.LOGIN
    fun Gotohomepage() {startActivity(Intent(this.baseContext,OrderActivity::class.java)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acount)
        //init dao
        usedao = UserDao()
        ParseUser.logOut()
        //init PrgDialog
        progdialog = ProgressDialog(this)
        progdialog?.setMessage("Pleaze Wait...")
        progdialog?.setCancelable(true)
        btnSignupLogin.text = AccountStatus.LOGIN.toString()
        textViewSignupLogin.text = AccountStatus.SIGNUP.toString()
        if (usedao?.checkLoggedIn()!!){
            Gotohomepage()
        }
    }


    fun doSignupOrLogin(view: View) {
        if (status == AccountStatus.LOGIN) {
// sign in the app
           var user = User(textUsername.text.toString(), textPassword.text.toString(), "", "")
           Login(user)
            //usedao?.SignUp(user2)
        } else if (status == AccountStatus.SIGNUP) {
            progdialog?.show()
            var user = User(textUsername.text.toString(), textPassword.text.toString(), "", "")
            usedao?.signUpWithCallback(user, { returnedUser ->
                progdialog?.hide()
                if (returnedUser.userName != null) {
                    Login(returnedUser)
                }
            })
        }

// sign up the app
        }

    fun ActiveDefaultBehaviour(view: View) {
        var tv = view as TextView
        if (tv.text == AccountStatus.LOGIN.toString()) {
            status = AccountStatus.LOGIN
        } else if (tv.text == AccountStatus.SIGNUP.toString()) {
            status = AccountStatus.SIGNUP
        }
        if (status == AccountStatus.LOGIN) {
            //sign in
            btnSignupLogin.text = AccountStatus.LOGIN.toString()
            textViewSignupLogin.text = AccountStatus.SIGNUP.toString()

        } else if (status == AccountStatus.SIGNUP) {
            //sign up
            status = AccountStatus.SIGNUP
            btnSignupLogin.text = AccountStatus.SIGNUP.toString()
            textViewSignupLogin.text = AccountStatus.LOGIN.toString()
        }
    }

    private fun showAlertDialog(s: String, s1: String) {
        AlertDialog.Builder(this)
            .setTitle(s).setMessage(s1)
            .setMessage(s1)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface?, which: Int -> }
            .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface?, which: Int -> }
            .setIcon(android.R.drawable.ic_dialog_alert).show()
    }
    fun Login(user:User){
        progdialog?.show()
        var user = User(textUsername.text.toString(), textPassword.text.toString(), "", "")
        usedao?.LogInWithCallback(user, { returnedUser ->
            progdialog?.hide()
            if (returnedUser.userName != null) {
                //Go to home page
                Gotohomepage()
            }
        })
    }

}

