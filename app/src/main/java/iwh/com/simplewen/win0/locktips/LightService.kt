package iwh.com.simplewen.win0.locktips

import android.app.*
import android.app.Notification.FLAG_ONGOING_EVENT
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.lang.Exception

/**
 * 通知服务
 */
class LightService :Service(){
    private lateinit var mNm: NotificationManager
    private lateinit var mBroadcastReceiver: BroadcastReceiver


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      //  println("@@运行备忘录通知")
        val piPre = PendingIntent.getActivity(this@LightService,1,Intent(this@LightService,addTips::class.java).apply {
            putExtra("intoBy","notify")
        },
            FLAG_UPDATE_CURRENT)
        val notifyBuildPre = NotificationCompat.Builder(this@LightService, "notifyTodo")
            //设置通知标题
            .setContentTitle("点击新建Todo")
            //设置通知内容
            .setContentText("正常运行中:Todo or Not Todo")
            .setAutoCancel(false)
            .setContentIntent(piPre)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSmallIcon(R.drawable.launch)
            .setShowWhen(true).build().apply {
                visibility = Notification.VISIBILITY_PUBLIC
            }

       startForeground(0x11,notifyBuildPre)



        return super.onStartCommand(intent, flags, startId)
    }
    override fun onCreate() {
        super.onCreate()
        mNm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mNm.createNotificationChannels(listOf(  NotificationChannel(
                "lockTodo1",
                "Todo1",
                NotificationManager.IMPORTANCE_HIGH
            ),  NotificationChannel(
                "lockTodo2",
                "Todo2",
                NotificationManager.IMPORTANCE_HIGH
            ),  NotificationChannel(
                "lockTodo3",
                "Todo3",
                NotificationManager.IMPORTANCE_HIGH
            ),  NotificationChannel(
                "notifyTodo",
                "TodoTop",
                NotificationManager.IMPORTANCE_HIGH
            )))
        }
        //服务开启
      //  println("@@服务开启")

        mBroadcastReceiver = object:BroadcastReceiver(){

            override fun onReceive(context: Context?, intent: Intent?) {


                val action = intent!!.action
                if (action == Intent.ACTION_SCREEN_ON) {
                  //println("@@亮屏")

                    val pi = PendingIntent.getActivity(this@LightService,1,Intent(this@LightService,MainActivity::class.java).apply {
                        putExtra("intoBy","notify")
                    },
                        FLAG_UPDATE_CURRENT)
                    val todoList = shareGet(this@LightService,"todo")
                    val list = shareGet(this@LightService,"list")
                    if(list.isNullOrEmpty() || todoList.isNullOrEmpty()){
                    }else{
                        try {
                         //   println("开始遍历")
                            for (i in list){
                              //  println("list：${i.key}")
                                val status = i.value.toString()
                              //  println(status)
                                if(status  == "1"){
                                    val notifyBuild = NotificationCompat.Builder(this@LightService, "lockTodo${i.key}")
                                        //设置通知标题
                                        .setContentTitle("${todoList["${i.key.toInt() - 1 }"]?:"默认todo"}")
                                        //设置通知内容
                                     //  .setContentText("")
                                        .setAutoCancel(true)
                                        .setGroup("todos")
                                        .setGroupSummary(false)
                                        .setContentIntent(pi)

                                        .setLargeIcon(BitmapFactory.decodeResource(context?.resources,R.drawable.launch))
                                        .setSmallIcon(R.drawable.launch)
                                        .setShowWhen(true).build().apply {
                                            visibility = Notification.VISIBILITY_PUBLIC
                                        }

                                    mNm.notify(500.plus(i.key.toInt()), notifyBuild)
                                }else{
                                   // println("list-value：${i.value}")
                                }
                            }

                        }catch (e:Exception){

                           // println("@@遍历出现问题 $todoList} max:$list")
                        }
                    }
                }
            }
        }
        this.registerReceiver(mBroadcastReceiver,IntentFilter(Intent.ACTION_SCREEN_ON).apply {
            priority = 1000
        })

    }

    override fun onDestroy() {
       // this.startService(Intent(this,this::class.java))
        super.onDestroy()
        stopForeground(true)
        //服务销毁
       // println("@@服务销毁")
        Toast.makeText(this@LightService, "备忘录服务被取消!", Toast.LENGTH_SHORT).show()
        this.unregisterReceiver(mBroadcastReceiver)
    }
}