package com.example.test1

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ScoreActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)


        // Retrieve the data from the Intent
        val bundle = intent.extras
        val map = mutableMapOf<String, Int>()


        bundle?.let {
            val keys = it.keySet()
            for (key in keys) {
                val value = it.getInt(key)
                map[key] = value
            }
        }


        val correctCount = map["correct"] ?: 0
        val wrongCount = map["wrong"] ?: 0
        val skippedCount = map["skipped"] ?: 0

        findViewById<TextView>(R.id.RightAnswer).text = "Your right answer is : ${correctCount}"
        findViewById<TextView>(R.id.TopOfRight).text = "${correctCount} out of 10"
        findViewById<TextView>(R.id.WrongAnswer).text = "Your wrong answer is : ${wrongCount}"
        findViewById<TextView>(R.id.SkipAnswer).text = "Your skip answer is : ${skippedCount}"


        findViewById<TextView>(R.id.btnReplay).setOnClickListener {
            // Create an Intent to start the first activity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CALL_API", true)
            startActivity(intent)

            // Finish the second activity
            finish()
        }

        findViewById<TextView>(R.id.btnHome).setOnClickListener {
            // Create an Intent to start the first activity
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CALL_API", true)
            startActivity(intent)

            // Finish the second activity
            finish()
        }


    }
}