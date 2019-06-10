package iwh.com.simplewen.win0.locktips

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window

import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_tips.*


class addTips : AppCompatActivity() {
    private var todoLevel: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_tips)


        saveBtn.setOnClickListener {
            if (todoText.text.toString().isEmpty()) {
                Toast.makeText(this@addTips, "不可以为空！", Toast.LENGTH_SHORT).show()
            } else {
                when (todos.checkedRadioButtonId) {
                    R.id.newTodo1 -> todoLevel = 1
                    R.id.newTodo2 -> todoLevel = 2
                    R.id.newTodo3 -> todoLevel = 3
                }
                shareSave(todoText.text.toString(), this@addTips, todoLevel.minus(1).toString())
                Toast.makeText(this@addTips, "保存完成", Toast.LENGTH_SHORT).show()
                finish()
            }

        }


    }
}
