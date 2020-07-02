package com.inkapplications.aprs.android

import android.app.Activity
import android.os.Bundle
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.log.LogActivity
import kotlinx.android.synthetic.main.dashboard.*

class DashboardActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        dashboard_log_button.setOnClickListener {
            startActivity(LogActivity::class)
        }
    }
}
