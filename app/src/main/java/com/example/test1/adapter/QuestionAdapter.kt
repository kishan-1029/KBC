package com.example.test1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test1.MainActivity
import com.example.test1.R
import org.json.JSONArray
import org.json.JSONObject

class QuestionAdapter(private val questionList: JSONArray, private val mainActivity: MainActivity) :
    RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private var currentQuestionIndex: Int = 0
    private val userAnswers = mutableMapOf<Int, String>() // Store user answers with answer text
    private val answerIdMap = mutableMapOf<Int, String>() // Map RadioButton IDs to answer text


    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionText: TextView = itemView.findViewById(R.id.questionText)
        val answersRadioGroup: RadioGroup = itemView.findViewById(R.id.answersRadioGroup)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_show, parent, false)
        return QuestionViewHolder(view)    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val questionObject: JSONObject = questionList.getJSONObject(currentQuestionIndex)

        holder.questionText.text = questionObject.getString("question")
        holder.answersRadioGroup.removeAllViews()
        answerIdMap.clear() // Clear previous mappings


        // Get the answer options from the JSONObject
        val answers = listOf(
            questionObject.getString("ans1"),
            questionObject.getString("ans2"),
            questionObject.getString("ans3"),
            questionObject.getString("ans4"),
            questionObject.getString("ans5")
        )

        answers.filter { it.isNotEmpty() }.forEach { answer ->
            val radioButton = RadioButton(holder.itemView.context)
            radioButton.text = answer
            val id = View.generateViewId()
            radioButton.id = id
            holder.answersRadioGroup.addView(radioButton)
            answerIdMap[id] = answer // Map RadioButton ID to answer
        }

        // Restore previously selected answer
        val selectedAnswer = userAnswers[currentQuestionIndex]
        holder.answersRadioGroup.check(
            answerIdMap.entries.find { it.value == selectedAnswer }?.key ?: -1
        )

        holder.answersRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Get the selected answer based on checkedId
            val selectedAnswer = answerIdMap[checkedId] ?: ""
            userAnswers[currentQuestionIndex] = selectedAnswer
        }


    }

    override fun getItemCount(): Int = 1 // Always showing one question at a time

    fun showNextQuestion() {
        if (currentQuestionIndex < questionList.length() - 1) {
            currentQuestionIndex++
            notifyDataSetChanged()
        }
    }

    fun showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            notifyDataSetChanged()
        }
    }

    fun getCurrentQuestionIndex(): Int = currentQuestionIndex


    fun getUserAnswers(): Map<Int, String> = userAnswers
}
