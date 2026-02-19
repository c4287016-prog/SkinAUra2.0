package com.example.skinaura20

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.view.animation.AnimationUtils

class Quiz : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView
    private lateinit var text4: TextView

    private lateinit var icon1: ImageView
    private lateinit var icon2: ImageView
    private lateinit var icon3: ImageView
    private lateinit var icon4: ImageView

    private lateinit var card1: CardView
    private lateinit var card2: CardView
    private lateinit var card3: CardView
    private lateinit var card4: CardView

    private var currentQuestion = 0

    private var oily = 0
    private var dry = 0
    private var combo = 0
    private var normal = 0

    private val questions = arrayOf(
        "How does your skin feel 1–2 hours after washing your face?",
        "How often do you get pimples or acne?",
        "How does your skin look by evening?",
        "How does your skin react to new products?"
    )

    private val options = arrayOf(
        arrayOf("Very oily", "Tight & rough", "Oily T-zone", "Soft & normal"),
        arrayOf("Very often", "Rarely", "Sometimes (T-zone)", "Almost never"),
        arrayOf("Very shiny", "Dull & flaky", "Shiny T-zone", "Fresh"),
        arrayOf("Breakouts", "Burning / irritation", "Sometimes reacts", "No reaction")
    )

    private val icons = arrayOf(
        arrayOf(R.drawable.ic_oily, R.drawable.ic_dry, R.drawable.ic_combo, R.drawable.ic_normal),
        arrayOf(R.drawable.ic_pimple, R.drawable.ic_clear, R.drawable.ic_tzone, R.drawable.ic_happy),
        arrayOf(R.drawable.ic_shiny, R.drawable.ic_flaky, R.drawable.ic_combo, R.drawable.ic_fresh),
        arrayOf(R.drawable.ic_breakout, R.drawable.ic_irritaion, R.drawable.ic_confused, R.drawable.ic_safe)
    )

    private val types = arrayOf(
        arrayOf("oily", "dry", "combo", "normal"),
        arrayOf("oily", "dry", "combo", "normal"),
        arrayOf("oily", "dry", "combo", "normal"),
        arrayOf("oily", "dry", "combo", "normal")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        questionText = findViewById(R.id.questionText)
        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        text3 = findViewById(R.id.text3)
        text4 = findViewById(R.id.text4)

        icon1 = findViewById(R.id.icon1)
        icon2 = findViewById(R.id.icon2)
        icon3 = findViewById(R.id.icon3)
        icon4 = findViewById(R.id.icon4)

        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)
        card4 = findViewById(R.id.card4)

        loadQuestion()

        card1.setOnClickListener { selectAnswer(0) }
        card2.setOnClickListener { selectAnswer(1) }
        card3.setOnClickListener { selectAnswer(2) }
        card4.setOnClickListener { selectAnswer(3) }
    }

    private fun loadQuestion() {
        questionText.text = questions[currentQuestion]

        text1.text = options[currentQuestion][0]
        text2.text = options[currentQuestion][1]
        text3.text = options[currentQuestion][2]
        text4.text = options[currentQuestion][3]

        icon1.setImageResource(icons[currentQuestion][0])
        icon2.setImageResource(icons[currentQuestion][1])
        icon3.setImageResource(icons[currentQuestion][2])
        icon4.setImageResource(icons[currentQuestion][3])

        // Reset animation
        card1.clearAnimation()
        card2.clearAnimation()
        card3.clearAnimation()
        card4.clearAnimation()

        animateCards()
    }

    private fun animateCards() {
        val anim1 = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val anim3 = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        val anim4 = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        anim2.startOffset = 120
        anim3.startOffset = 240
        anim4.startOffset = 360

        card1.startAnimation(anim1)
        card2.startAnimation(anim2)
        card3.startAnimation(anim3)
        card4.startAnimation(anim4)
    }

    private fun selectAnswer(index: Int) {

        val selectedType = types[currentQuestion][index]

        when (selectedType) {
            "oily" -> oily++
            "dry" -> dry++
            "combo" -> combo++
            "normal" -> normal++
        }

        currentQuestion++

        if (currentQuestion < questions.size) {
            loadQuestion()
        } else {
            showResult()
        }
    }

    private fun showResult() {

        val result = when {

            oily > 0 && dry > 0 && combo > 0 && normal > 0 -> "COMBINATION SKIN"
            oily > 0 && dry > 0 -> "COMBINATION SKIN"
            oily > 0 && normal > 0 -> "COMBINATION SKIN"
            dry > 0 && normal > 0 -> "DRY SKIN"
            combo > 0 -> "COMBINATION SKIN"

            oily > dry && oily > normal -> "OILY SKIN"
            dry > oily && dry > normal -> "DRY SKIN"
            normal > oily && normal > dry -> "NORMAL SKIN"

            else -> "COMBINATION SKIN"
        }

        val intent = Intent(this, Result::class.java)
        intent.putExtra("SKIN_RESULT", result)
        startActivity(intent)
        finish()
    }
}
