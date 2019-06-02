package iwh.com.simplewen.win0.locktips

import android.app.Service
import android.content.Context
import java.nio.file.Files.size
import androidx.core.content.ContextCompat.getSystemService
import android.app.ActivityManager




/**
 * 保存todo
 */
fun shareSave(str: String, context: Context, type: String = "",status:Int = 0) {

    if (type.isEmpty()) {
        //设置最大条数
        val share = context.getSharedPreferences("list", Context.MODE_PRIVATE)
        share.edit().putString(str, status.toString()).apply()
    } else {
        //保存todo
        val share = context.getSharedPreferences("todo", Context.MODE_PRIVATE)
        share.edit().putString(type, str).apply()
    }


}

/**
 * 获取todo
 */
fun shareGet(context:Context,type:String):MutableMap<String,*>? {
  return when(type){
       "list" ->{
           val share = context.getSharedPreferences("list", Context.MODE_PRIVATE)
          // println("@@ all-list:${share.all}")
           share.all
       }
       "todo"->{
           val share = context.getSharedPreferences("todo", Context.MODE_PRIVATE)
         //  println("@@ all-todo:${share.all}")
           share.all

       }
      else -> null
   }
}

