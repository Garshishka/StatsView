package ru.netology.statsview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<StatsView>(R.id.statsView).full = 1000f
        findViewById<StatsView>(R.id.statsView).data = listOf(
            500f,
            150f,
            50f,
            50f,
            100f,
        )
    }
}