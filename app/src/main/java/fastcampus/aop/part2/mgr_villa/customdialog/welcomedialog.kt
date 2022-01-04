package fastcampus.aop.part2.mgr_villa.customdialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fastcampus.aop.part2.mgr_villa.MainActivity
import fastcampus.aop.part2.mgr_villa.R
import fastcampus.aop.part2.mgr_villa.SignUpActivity

class welcomedialog(context: Context) : AppCompatActivity() {

    private lateinit var listener: WelcomeDialogOKClickedListener
    private val dlg = Dialog(context)

    private val desc: TextView by lazy {
        findViewById(R.id.popupContent)
    }

    private val OKbtn: Button by lazy{
        findViewById(R.id.SignComplete)
    }

    fun start(content: String){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.ok_popup)
        dlg.setCancelable(false)

        desc.text = content


        OKbtn.setOnClickListener {
            Log.d("OKbtn.setOnClickListener","click")
            dlg.dismiss()
        }
//        dlg.show()

//        OKbtn.setOnClickListener {
//            Log.d("OKbtn.setOnClickListener", "Clicked")
//            dlg.dismiss()
//            val mainIntent = Intent(this, MainActivity::class.java)
//            startActivity(mainIntent)
//        }

    }

    fun setOnClickedListener(listener: (String) -> Unit){
        this.listener = object:WelcomeDialogOKClickedListener{
            override fun onOKClicked(content: String) {

                dlg.dismiss()
            }
        }
    }

    interface WelcomeDialogOKClickedListener{
        fun onOKClicked(content:String){
        }
    }


}