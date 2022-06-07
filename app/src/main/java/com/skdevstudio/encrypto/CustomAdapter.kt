package com.skdevstudio.encrypto

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList


class CustomAdapter(context: Context,accountType : ArrayList<String>,username : ArrayList<String>,password : ArrayList<String>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private lateinit var mListner: onItemClickListner
    private var clickedExpand : Boolean = false
    private var showPwdClicked : Boolean = false
    private var AES : String = "AES"
    private var pvt_key : String = "3xh2B0l6v6VpCWr5T4CHBA=="


    var context: Context
    var accountType : ArrayList<String>
    var username : ArrayList<String>
    var password : ArrayList<String>

    interface onItemClickListner{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener : onItemClickListner){
        mListner = listener
    }

    init {
        this.context = context
        this.accountType = accountType
        this.username = username
        this.password = password
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.MyViewHolder {
        var layoutInflater : LayoutInflater = LayoutInflater.from(context)
        var view : View = layoutInflater.inflate(R.layout.rv_rows,parent,false)
        return MyViewHolder(view,mListner)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomAdapter.MyViewHolder, position: Int) {
        holder.accountTypeTextView.text = accountType.get(position)
        holder.usernameTextView.text = "Username : ${username.get(position)}"
        holder.passwordTextView.text = "Password :  ${password.get(position)}"
        holder.editBtn.visibility = View.GONE
        holder.headerRv.setOnClickListener {
            if(!clickedExpand){
                holder.usernameLayout.visibility = View.VISIBLE
                holder.pwdLayout.visibility= View.VISIBLE
                holder.editBtn.visibility = View.VISIBLE
                clickedExpand = true
            } else{
                holder.usernameLayout.visibility = View.GONE
                holder.pwdLayout.visibility= View.GONE
                holder.editBtn.visibility = View.GONE
                clickedExpand = false
            }
        }

        holder.editBtn.setOnClickListener {
            val intent : Intent = Intent(context,AddIds::class.java)
            intent.putExtra("AccountType",accountType.get(position))
            context.startActivity(intent)
        }

        holder.showPwd.setOnClickListener {
            if(!showPwdClicked){
                holder.passwordTextView.text = "Password :  ${decrypt(password.get(position),pvt_key)}"
                holder.copiedText.text = "Password will be encrypted in : 5:00 Seconds!"
                showPwdClicked = true
                holder.copiedText.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        holder.copiedText.text = "Password will be encrypted in : 4:00 Seconds!"
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                holder.copiedText.text = "Password will be encrypted in : 3:00 Seconds!"
                                Handler(Looper.getMainLooper()).postDelayed(
                                    {
                                        holder.copiedText.text = "Password will be encrypted in : 2:00 Seconds!"
                                        Handler(Looper.getMainLooper()).postDelayed(
                                            {
                                                holder.copiedText.text = "Password will be encrypted in : 1:00 Seconds!"
                                                Handler(Looper.getMainLooper()).postDelayed(
                                                    {
                                                        holder.copiedText.visibility = View.GONE
                                                        holder.passwordTextView.text = "Password :  ${encrypt(password.get(position),pvt_key)}"
                                                        showPwdClicked = false
                                                    },
                                                    1000
                                                )
                                            },
                                            1000
                                        )
                                    },
                                    1000
                                )
                            },
                            1000
                        )
                    },
                    1000
                )
            }
        }
        holder.copyUsername.setOnClickListener {
            copyText(username.get(position),"Username")
            holder.copiedText.text = "Copied Username to Clipboard!"
            holder.copiedText.visibility = View.VISIBLE
//            Handler(Looper.getMainLooper()).postDelayed(
//                {
//                    holder.copiedText.visibility = View.GONE
//                },
//                5000
//            )
        }
    }

    override fun getItemCount(): Int {
        return username.size
    }

    class MyViewHolder(itemView: View,listener: onItemClickListner) : RecyclerView.ViewHolder(itemView) {
        var accountTypeTextView : TextView = itemView.findViewById(R.id.accountTypeTextView)
        var usernameTextView : TextView = itemView.findViewById(R.id.usernameTextView)
        var passwordTextView : TextView = itemView.findViewById(R.id.passwordTextView)
        var usernameLayout : LinearLayout = itemView.findViewById(R.id.usernameLayout)
        var pwdLayout : LinearLayout = itemView.findViewById(R.id.pwdLayout)
        var showPwd : ImageButton = itemView.findViewById(R.id.showPasswordBtn)
        var copyUsername : ImageButton = itemView.findViewById(R.id.copyUsernameBtn)
        var copiedText : TextView = itemView.findViewById(R.id.copied)
        var editBtn : ImageButton = itemView.findViewById(R.id.editBtn)
        var headerRv : LinearLayout = itemView.findViewById(R.id.headerRv)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    fun copyText(text:String,label : String){
        val myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText(label, text)
        myClipboard.setPrimaryClip(myClip)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun decrypt(Data: String, pwd: String): String {
        var key : SecretKeySpec = generateKey(pwd)
        var c : Cipher = Cipher.getInstance(AES)
        c.init(Cipher.DECRYPT_MODE,key)
        var decodedValue : ByteArray = Base64.getDecoder().decode(Data)
        var decValue : ByteArray = c.doFinal(decodedValue)
        var decValStr = String(decValue)
        return decValStr

    }
    private fun generateKey(pwd: String): SecretKeySpec {
        val messageDigest : MessageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(pwd.toByteArray())
        var secretKeySpec : SecretKeySpec = SecretKeySpec(messageDigest.digest(),"AES")
        return  secretKeySpec
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun encrypt(Data : String, pwd : String): String {
        var key : SecretKeySpec = generateKey(pwd)
        var c : Cipher = Cipher.getInstance(AES)
        c.init(Cipher.ENCRYPT_MODE,key)
        var encVal = c.doFinal(Data.toByteArray())
        var encValueStr = Base64.getEncoder().encodeToString(encVal)
        return encValueStr
    }
}