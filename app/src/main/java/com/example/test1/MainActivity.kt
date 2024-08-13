package com.example.test1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test1.adapter.QuestionAdapter
import com.example.test1.model.ResponseOfData
import com.example.test1.retrofit.RetrofitRestClient
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuestionAdapter
    private  var flag = 0
    private val DataFetch = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val shouldCallApi = intent.getBooleanExtra("CALL_API", false)
        if (shouldCallApi) {
            createApi()
        }
        createApi()
    }

    private fun createApi() {

        checkInternetConnection()

        val params =  JSONObject()
        try {
            params.put("id", "99")
            params.put("question", "What should a diabetic do to track his/her health?")
            params.put("ans1", "Keep a daily log of blood pressure and blood sugar")
            params.put("ans2", "Keep a consistent meal schedule")
            params.put("ans3", "Consult your pharmacist for some herbal or OTC supplements for goodhealth")
            params.put("ans4", "All of the above")
            params.put("ans5", "")
            params.put("correct", "ans4")
            params.put("added_date", "2017-04-08 18:45:47")
            params.put("modified_date", "0000-00-00 00:00:00")
            params.put("status", "Active")
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        val param = JsonParser.parseString(params.toString()) as JsonObject

        val call : Call<ResponseOfData> = RetrofitRestClient.getInstance().getData(param)

        call.enqueue(object  : Callback<ResponseOfData>{
            override fun onResponse(
                call: Call<ResponseOfData>,
                response: Response<ResponseOfData>
            ) {
                try {
                    val dataResponse : ResponseOfData
                    if(response.isSuccessful){
                             dataResponse = response.body()!!
                        if(dataResponse.size>0){
                            Log.e("Responseese", "Successfully" )
                            Log.e("Responseese", dataResponse.toString() )

                            for (i in 0 until 10){

//                                Log.e(i.toString(),  .toString())

                                var randomIndex = Random.nextInt(1, dataResponse.size + 1)

                                val obj = JSONObject()
                                obj.put("id", dataResponse[randomIndex].id)
                                obj.put("question", dataResponse[randomIndex].question)
                                obj.put("ans1", dataResponse[randomIndex].ans1)
                                obj.put("ans2", dataResponse[randomIndex].ans2)
                                obj.put("ans3", dataResponse[randomIndex].ans3)
                                obj.put("ans4", dataResponse[randomIndex].ans4)
                                obj.put("ans5", dataResponse[randomIndex].ans5)
                                obj.put("correct", dataResponse[randomIndex].correct)
                                DataFetch.put(obj)
                            }

                            Log.e("FetchData", DataFetch.toString() )

                            recyclerView = findViewById(R.id.recyclerView)
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                            adapter = QuestionAdapter(DataFetch, this@MainActivity)
                            recyclerView.adapter = adapter
                            if(flag == 0){
                                flag = 1
                                updateButtonVisibility(DataFetch.length())
                            }
                            findViewById<ImageView>(R.id.btnNext).setOnClickListener {
                                adapter.showNextQuestion()
                                updateButtonVisibility(DataFetch.length())

                            }
                            findViewById<ImageView>(R.id.btnPrevious).setOnClickListener {
                                adapter.showPreviousQuestion()
                                updateButtonVisibility(DataFetch.length())

                            }

                            findViewById<TextView>(R.id.btnSubmit).setOnClickListener {
                                val results = calculateQuizResults(adapter)


                                // Convert Map to Bundle
                                val bundle = Bundle()
                                for ((key, value) in results) {
                                    bundle.putInt(key, value)
                                }
                                val intent = Intent(this@MainActivity, ScoreActivity::class.java)
                                intent.putExtras(bundle)
                                startActivity(intent)

                                showResults(results)
                            }



                        }
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseOfData>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }
    private fun showResults(results: Map<String, Int>) {
        val correctCount = results["correct"] ?: 0
        val wrongCount = results["wrong"] ?: 0
        val skippedCount = results["skipped"] ?: 0
        Log.e("showResults", results.toString() )


    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkInternetConnection() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Not internet connected", Toast.LENGTH_SHORT).show()
        }
    }



    fun calculateQuizResults(adapter: QuestionAdapter): Map<String, Int> {
        val userAnswers = adapter.getUserAnswers()
        val correctAnswers = (0 until DataFetch.length()).associate {
            val questionObject = DataFetch.getJSONObject(it)
            questionObject.getString("id") to questionObject.getString("correct")
        }

        var correctCount = 0
        var wrongCount = 0
        var skippedCount = 0

        userAnswers.forEach { (questionIndex, selectedAnswer) ->
            val questionObject = DataFetch.getJSONObject(questionIndex)
            val correctAnswer = questionObject.getString(questionObject.getString("correct"))

            if (selectedAnswer.isEmpty()) {
                skippedCount++
            } else if (selectedAnswer == correctAnswer) {
                correctCount++
            } else {
                wrongCount++
            }
        }

        return mapOf(
            "correct" to correctCount,
            "wrong" to wrongCount,
            "skipped" to skippedCount
        )
    }
    @SuppressLint("SetTextI18n", "CutPasteId")
    private fun updateButtonVisibility(length: Int) {
        val currentQuestionIndex = adapter.getCurrentQuestionIndex()
        findViewById<TextView>(R.id.QuestionTitle).visibility = View.VISIBLE
        findViewById<TextView>(R.id.QuestionTitle).text = "Question ${currentQuestionIndex + 1} of 10"
        findViewById<ImageView>(R.id.btnPrevious).visibility = if (currentQuestionIndex == 0) View.GONE else View.VISIBLE
        findViewById<ImageView>(R.id.btnNext).visibility = if (currentQuestionIndex == length - 1) View.GONE else View.VISIBLE
        findViewById<TextView>(R.id.btnSubmit).visibility = if (currentQuestionIndex == length - 1) View.VISIBLE else View.GONE
    }
}