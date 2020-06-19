package com.azp.firebaserealtimedatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.azp.firebaserealtimedatabase.model.UserInfo
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseDatabase = FirebaseDatabase.getInstance()
        dbReference = firebaseDatabase.getReference("users")

        userId = dbReference.push().key.toString()

        update_user_btn.setOnClickListener{
            var name: String = name_edt_text.text.toString()
            var mobile: String = mobile_edt_text.text.toString()

            if(TextUtils.isEmpty(userId)){
                createUser(name, mobile)
            } else{
                updateUser(name, mobile)
            }
        }
    }

    private fun updateUser(name: String, mobile: String) {

        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            dbReference.child(userId).child("name").setValue(name)

        if (!TextUtils.isEmpty(mobile))
            dbReference.child(userId).child("mobile").setValue(mobile)

        addUserChangeListener()

    }

    private fun createUser(name: String, mobile: String) {
        val user = UserInfo(name, mobile)
        dbReference.child(userId).setValue(user)
    }

    private fun addUserChangeListener() {
        // User data change listener
        dbReference.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserInfo::class.java)

                // Check for null
                if (user == null) {
                    return
                }


                // Display newly updated name and email
                user_name.setText(user?.name).toString()
                user_mobile.setText(user?.mobile).toString()

                // clear edit text
                name_edt_text.setText("")
                mobile_edt_text.setText("")

                changeButtonText()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    private fun changeButtonText(){
        if (TextUtils.isEmpty(userId)) {
            update_user_btn.text = "Save";
        } else {
            update_user_btn.text = "Update";
        }
    }
}