package com.skdevstudio.encrypto

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.database.Cursor
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.skdevstudio.encrypto.R
import java.lang.Exception
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
    private lateinit var mydialog : Dialog
    private var devKey : String = "4LOQWCIPY5eQfTmf51P6LA=="



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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(R.layout.rv_rows,parent,false)
        return MyViewHolder(view,mListner)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "Recycle")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.accountTypeTextView.text = accountType.get(position)
        holder.usernameTextView.text = "Username : ${username.get(position)}"
        holder.passwordTextView.text = "Password :  ${password.get(position)}"
        try{
            if(accountType.get(position).lowercase(Locale.getDefault()).contains("facebook")){
                holder.logo.setImageResource(R.drawable.facebook)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("instagram")){
                holder.logo.setImageResource(R.drawable.instagram)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("github")){
                holder.logo.setImageResource(R.drawable.github)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("dribble")){
                holder.logo.setImageResource(R.drawable.dribble)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("behance")){
                holder.logo.setImageResource(R.drawable.behance)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("linkdlen")){
                holder.logo.setImageResource(R.drawable.linkdlen)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("pinterest")){
                holder.logo.setImageResource(R.drawable.pinterest)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("reddit")){
                holder.logo.setImageResource(R.drawable.reddit)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("wordpress")){
                holder.logo.setImageResource(R.drawable.wordpress)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("twitter")){
                holder.logo.setImageResource(R.drawable.twitter)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("gmail")){
                holder.logo.setImageResource(R.drawable.google)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("google")){
                holder.logo.setImageResource(R.drawable.google)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("office") || accountType.get(position).toLowerCase().contains("outlook") || accountType.get(position).toLowerCase().contains("microsoft") || accountType.get(position).toLowerCase().contains("ms") || accountType.get(position).toLowerCase().contains("teams")){
                holder.logo.setImageResource(R.drawable.outlook)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("apple")){
                holder.logo.setImageResource(R.drawable.apple)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("snapchat")){
                holder.logo.setImageResource(R.drawable.snapchat)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("telegram")){
                holder.logo.setImageResource(R.drawable.telegram)
            }else if(accountType.get(position).lowercase(Locale.getDefault()).contains("spotify")){
                holder.logo.setImageResource(R.drawable.spotify)
            }else{
                holder.logo.setImageResource(R.drawable.dummy)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
        holder.editBtn.visibility = View.GONE
        holder.headerRv.setOnClickListener {
            if(!clickedExpand){
                holder.usernameLayout.visibility = View.VISIBLE
                holder.pwdLayout.visibility= View.VISIBLE
                holder.editBtn.visibility = View.VISIBLE
                holder.deleteLayout.visibility = View.VISIBLE
                clickedExpand = true
            } else{
                holder.usernameLayout.visibility = View.GONE
                holder.pwdLayout.visibility= View.GONE
                holder.editBtn.visibility = View.GONE
                holder.deleteLayout.visibility = View.GONE
                clickedExpand = false
            }
        }

        holder.editBtn.setOnClickListener {
            val intent : Intent = Intent(context, AddIds::class.java)
            intent.putExtra("AccountType",accountType.get(position))
            context.startActivity(intent)
        }

        holder.copyPwd.setOnClickListener {
            copyText(decrypt(password.get(position),pvt_key),"Password")
            holder.copiedText.text = "Copied Password To Clipboard"
            holder.copiedText.visibility = View.VISIBLE
        }


        holder.deleteBtn.setOnClickListener {
            val query = "SELECT * FROM USERDATA"
            val helper = DBHelper(context)
            val db = helper.readableDatabase

            var cursor : Cursor? = null
            if(db != null){
                cursor = db.rawQuery(query,null)
            }
            val builder = androidx.appcompat.app.AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    try {
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                        val accountTypeToDel : String= accountType[position]
                        var str : () -> String = {accountTypeToDel}
                        if(db != null){
                            cursor = db.rawQuery("DELETE FROM USERDATA WHERE ACCOUNT_TYPE = ?",
                                arrayOf(
                                    accountTypeToDel
                                ))
                        }
                        dialog.dismiss()
                        val intent : Intent = Intent(context, ShowIDs::class.java)
                        context.startActivity(intent)
                        val con = context as Activity
                        con.finish()
                        cursor?.getString(1)
                    }catch (e : Exception){
                        dialog.dismiss()
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        holder.showPwd.setOnClickListener {

            mydialog = Dialog(context)
            mydialog.setContentView(R.layout.key_dailog)
            mydialog.show()
            mydialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            var title : TextView = mydialog.findViewById<TextView>(R.id.dailog_text)
            var posBtn : TextView = mydialog.findViewById(R.id.positiveBtn)
            var negBtn : TextView = mydialog.findViewById(R.id.cancelBtn)
            var key : EditText = mydialog.findViewById(R.id.key)
            var noteText : TextView = mydialog.findViewById(R.id.note_text)

            negBtn.setOnClickListener { mydialog.dismiss() }
            title.text = "Enter the Secret Key!"
            noteText.text = "Note : It is the key you used before!"
            mydialog.findViewById<TextInputLayout>(R.id.cnfKeyRoot).visibility = View.GONE

            posBtn.setOnClickListener {
                pvt_key = key.text.toString().trim()
                if(!showPwdClicked){
                    var pwd = "Key is incorrect!"
                    try {
                        pwd =  decrypt(password.get(position),pvt_key)
                        holder.showPwd.visibility = View.GONE
                        holder.copyPwd.visibility = View.VISIBLE
                    }catch (e : Exception){
                        Toast.makeText(context, "Wrong Key!", Toast.LENGTH_SHORT).show()
                    }
                    mydialog.dismiss()
                    holder.passwordTextView.text = "Password :  ${pwd}"

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
                                                            holder.passwordTextView.text = "Password :  ${password.get(position)}"
                                                            holder.copyPwd.visibility = View.GONE
                                                            holder.showPwd.visibility = View.VISIBLE
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

        }
        holder.copyUsername.setOnClickListener {
            copyText(username.get(position),"Username")
            holder.copiedText.text = "Copied Username To Clipboard"
            holder.copiedText.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    Handler(Looper.getMainLooper()).postDelayed(
                                        {
                                            Handler(Looper.getMainLooper()).postDelayed(
                                                {
                                                    holder.copiedText.visibility=View.GONE
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
        var logo : ImageView = itemView.findViewById(R.id.logo)
        var copyPwd : ImageButton = itemView.findViewById(R.id.copyPwd)
        var deleteBtn : AppCompatButton = itemView.findViewById(R.id.delete)
        var deleteLayout : LinearLayout = itemView.findViewById(R.id.dltLayout)

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
        val key : SecretKeySpec = generateKey(pwd+devKey)
        val c : Cipher = Cipher.getInstance(AES)
        c.init(Cipher.DECRYPT_MODE,key)
        val decodedValue : ByteArray = Base64.getDecoder().decode(Data)
        val decValue : ByteArray = c.doFinal(decodedValue)
        val decValStr = String(decValue)
        return decValStr

    }
    private fun generateKey(pwd: String): SecretKeySpec {
        val messageDigest : MessageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(pwd.toByteArray())
        var secretKeySpec : SecretKeySpec = SecretKeySpec(messageDigest.digest(),"AES")
        return  secretKeySpec
    }

}