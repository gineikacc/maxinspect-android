package com.maxinspect

import android.app.Application
import android.util.Log

class MaxInspect : Application() {
    var someVar: String? = "B0ss"

    override fun onCreate() {
        super.onCreate()
        Log.i("MaxInspect", "App created")
        // Initialize any global state here, if needed
    }

    //fun setSomeVar(value: String?) {
        //someVar = value   // ALSO SHOULD SAVE TO WHATEVER STORAGE THERE IS
    //}
}