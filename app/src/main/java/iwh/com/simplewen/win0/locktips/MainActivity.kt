package iwh.com.simplewen.win0.locktips


import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.KeyEventDispatcher

import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_END
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


/**
 * 锁屏备忘录
 * 插件：...
 */
class MainActivity : AppCompatActivity() {
    //fab按钮状态
    private var initSize:Int? = 0
    private var currentMode = 0
    private var fabMode = 2
    private var todoList:MutableMap<String,*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        todoList = shareGet(this@MainActivity,"todo")


       try {
           if(!todoList.isNullOrEmpty()) initSize = todoList?.size
       }catch (e:Exception){
           //println("@@暂时没有设置todo")
       }
       // println(todoList.toString())
        //添加监听器
        fun clickListener(type: Int) = View.OnClickListener {
            this.currentMode = type
         //   Toast.makeText(this, "$type", Toast.LENGTH_SHORT).show()
            BottomSheetDialog(this@MainActivity)
                .apply {
                    val ly = LinearLayout.inflate(this.context, R.layout.new_add, null).apply {
                        val content = this.findViewById<TextInputEditText>(R.id.todoText)
                        val save = this.findViewById<MaterialButton>(R.id.saveBtn)
                        save.setOnClickListener {
                            if (content.text.toString().isEmpty()) {
                                Toast.makeText(this@MainActivity, "不可以为空！", Toast.LENGTH_SHORT).show()
                            } else {
                                shareSave(content.text.toString(),this@MainActivity,type.toString())
                                when(type){
                                    0 -> this@MainActivity.todoText1.text = content.text.toString()

                                    1 ->this@MainActivity.todoText2.text = content.text.toString()

                                    2 ->this@MainActivity.todoText3.text = content.text.toString()

                                }
                                initSize = 1
                                Toast.makeText(this@MainActivity,"保存完成",Toast.LENGTH_SHORT).show()
                            }

                        }

                    }

                    setContentView(ly)
                    create()

                }.show()
        }

        todoText1.setOnClickListener(clickListener(0))
        todoText2.setOnClickListener(clickListener(1))
        todoText3.setOnClickListener(clickListener(2))

        fun todoController(type:String) = CompoundButton.OnCheckedChangeListener{
                _,selected ->
            if(selected){
                shareSave(type,this@MainActivity,"",1)
            }else{
                shareSave(type,this@MainActivity,"",0)
            }
        }
        val listStatus = shareGet(this@MainActivity,"list")
        todo1Controller.apply {
            setOnCheckedChangeListener(todoController("1"))
            isChecked = listStatus!!["1"].toString() == "1"
        }

        todo2Controller.apply {
            setOnCheckedChangeListener(todoController("2"))
            isChecked = listStatus!!["2"].toString() == "1"
        }

        todo3Controller.apply {
            setOnCheckedChangeListener(todoController("3"))
            isChecked = listStatus!!["3"].toString() == "1"

        }




        //隐藏到后台
        serviceHide.setOnClickListener {
            goBackground()
            Toast.makeText(this@MainActivity,"隐藏中...",Toast.LENGTH_SHORT).show()
        }

        when(initSize){

            1 ->{
                todoText1.text = todoList!!["0"].toString()
            }
            2 ->{
                todoText1.text = todoList!!["0"].toString()
                todoText2.text = todoList!!["1"].toString()
            }
            3 ->{
                todoText1.text = todoList!!["0"].toString()
                todoText2.text = todoList!!["1"].toString()
                todoText3.text = todoList!!["2"].toString()
            }
        }

        if(intent.getStringExtra("intoBy") == "notify"){
            Toast.makeText(this@MainActivity,"服务正运行中！",Toast.LENGTH_SHORT).show()
            bottomBar.fabAlignmentMode = FAB_ALIGNMENT_MODE_END
            fab.setImageResource(R.drawable.ic_action_close)

            fabMode = 1


        }
        fab.setOnClickListener {
            when (fabMode) {
                1 -> {
                    //停止服务
                    stopService(Intent(this@MainActivity, LightService::class.java))
                    fab.setImageResource(R.drawable.ic_action_start)
                    bottomBar.fabAlignmentMode = FAB_ALIGNMENT_MODE_CENTER
                    fabMode = 2
                }
                2 -> {
                    //启动服务


                       startService(Intent(this@MainActivity, LightService::class.java))
                       Toast.makeText(this, "启动通知", Toast.LENGTH_SHORT).show()
                       bottomBar.fabAlignmentMode = FAB_ALIGNMENT_MODE_END
                       fab.setImageResource(R.drawable.ic_action_close)
                       fabMode = 1

                }
            }
        }
    }

    //后台
    private fun goBackground() {
        val home = Intent(Intent.ACTION_MAIN)
        home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        home.addCategory(Intent.CATEGORY_HOME)
        startActivity(home)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_help -> {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("使用帮助")
                    .setIcon(R.drawable.launch)
                    .setMessage("1.点击todo即可自定义内容 \n\n 2.点击todo下面的复选按钮启动你需要的todo \n\n 3.设置todo后，点击最下面的圆形按钮开启服务。\n\n 4.每次亮屏后会显示通知！ \n\n 5.不显示？可能被收纳了，或者非原生系统，你需要去设置，通知管理，给予它通知权限！")
                    .setPositiveButton("分享"){
                            _,_ ->
                        with(Intent(Intent.ACTION_SEND)) {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "文院移动图书馆:https://www.coolapk.com/apk/iwh.com.simplewen.win0.locktips")
                            startActivity(Intent.createChooser(this, "分享锁屏备忘录给朋友！"))
                        }
                    }
                    .create().show()
                true
            }
            R.id.action_about -> {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("关于锁屏备忘录")
                    .setIcon(R.drawable.launch)
                    .setMessage("简介：这是一个在锁屏显示通知的备忘录，最多允许三条同时显示。\n\n by：IWH | version：v1.0.2 ")
                    .setNegativeButton("去反馈bug"){
                        _,_ ->
                        Intent().apply{
                            action = Intent.ACTION_VIEW
                            data = Uri.parse( "market://details?id=${this@MainActivity.packageName}")
                            `package`  = "com.coolapk.market"
                            startActivity(this)
                        }
                    }
                    .setPositiveButton("分享"){
                        _,_ ->
                        with(Intent(Intent.ACTION_SEND)) {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "文院移动图书馆:https://www.coolapk.com/apk/iwh.com.simplewen.win0.locktips")
                            startActivity(Intent.createChooser(this, "分享锁屏备忘录给朋友！"))
                        }
                    }
                    .create().show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        goBackground()
    }
}
