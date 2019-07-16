package ru.skillbranch.devintensive

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.models.Bender

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var benderObj: Bender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeSendOnActionDone(et_message)

        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name
        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name

        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))

        val (r, g, b) = benderObj.status.color
        iv_bender.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)

        tv_text.text = benderObj.askQuestion()
        iv_send.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("STATUS", benderObj.status.name)
        outState?.putString("QUESTION", benderObj.question.name)
    }

    private fun makeSendOnActionDone(editText: EditText) {
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) iv_send.performClick()
            false
        }
    }


    override fun onClick(v: View?) {
        if (v?.id == R.id.iv_send)
            if (isAnswerValid()) sendAnswer() else makeErrorMessage()
    }


    @SuppressLint("SetTextI18n")
    private fun makeErrorMessage() {
        val errorMessage = when (benderObj.question) {
            Bender.Question.NAME -> "Имя должно начинаться с заглавной буквы"
            Bender.Question.PROFESSION -> "Профессия должна начинаться со строчной буквы"
            Bender.Question.MATERIAL -> "Материал не должен содержать цифр"
            Bender.Question.BDAY -> "Год моего рождения должен содержать только цифры"
            Bender.Question.SERIAL -> "Серийный номер содержит только цифры, и их 7"
            else -> "На этом все, вопросов больше нет"
        }
        tv_text.text = "$errorMessage\n ${benderObj.question.question}"
        et_message.setText("")
    }

    private fun isAnswerValid(): Boolean = benderObj.question.validate(et_message.text.toString())


    private fun sendAnswer() {
        val (phase, color) = benderObj.listenAnswer(et_message.text.toString().toLowerCase())
        et_message.setText("")
        val (r, g, b) = color
        iv_bender.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
        tv_text.text = phase
    }

}