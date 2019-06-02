package iwh.com.simplewen.win0.locktips

import android.app.Application

class App:Application(){
    companion object {
        lateinit var _context:Application
    }
    override fun onCreate() {
        super.onCreate()
        _context  = this
    }
}