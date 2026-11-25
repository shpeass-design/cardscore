package com.sazzad.cardscore

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private var scoreA = 0
    private var scoreB = 0
    private var players = 4
    private var target = 50
    private var method = "দ্রুত স্কোর"

    private lateinit var nameA: TextView
    private lateinit var nameB: TextView
    private lateinit var scoreAView: TextView
    private lateinit var scoreBView: TextView
    private lateinit var status: TextView
    private lateinit var playerInfo: TextView

    private var teamA = "Team A"
    private var teamB = "Team B"

    private val bg = Color.rgb(14, 23, 42)
    private val panel = Color.rgb(30, 41, 59)
    private val green = Color.rgb(34, 197, 94)
    private val white = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUI()
    }

    private fun buildUI() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(content)
        root.addView(scroll)
        setContentView(root)

        content.addView(label("CARD SCORE", 28, green, true))
        content.addView(label("Team Score Manager", 14, white))
        content.addView(space(20))

        playerInfo = label("Players: $players", 17, white, true)
        content.addView(playerInfo)

        content.addView(actionButton("খেলোয়াড় সংখ্যা পরিবর্তন") {
            editNumber("খেলোয়াড় সংখ্যা", players) {
                players = it.coerceIn(1, 20)
                playerInfo.text = "Players: $players"
            }
        })

        content.addView(actionButton("স্কোর পদ্ধতি: $method") {
            val options = arrayOf("দ্রুত স্কোর", "ম্যানুয়াল স্কোর")
            AlertDialog.Builder(this)
                .setTitle("স্কোর পদ্ধতি নির্বাচন")
                .setItems(options) { _, which ->
                    method = options[which]
                    buildUI()
                }.show()
        })

        content.addView(space(18))

        val teams = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }
        teams.addView(teamPanel(true), LinearLayout.LayoutParams(0, -2, 1f))
        teams.addView(spaceWidth(12))
        teams.addView(teamPanel(false), LinearLayout.LayoutParams(0, -2, 1f))
        content.addView(teams)

        content.addView(space(20))
        content.addView(label("Game Status", 19, green, true))
        status = label("খেলা শুরু করুন!", 17, white, true)
        content.addView(status)
        content.addView(space(15))

        content.addView(actionButton("New Game / Reset") {
            AlertDialog.Builder(this)
                .setTitle("নতুন খেলা?")
                .setMessage("দুই দলের স্কোর শূন্য হবে।")
                .setPositiveButton("হ্যাঁ") { _, _ ->
                    scoreA = 0
                    scoreB = 0
                    updateScores()
                }
                .setNegativeButton("না", null)
                .show()
        })

        content.addView(actionButton("জয়ের টার্গেট পরিবর্তন") {
            editNumber("জয়ের টার্গেট", target) {
                target = it.coerceAtLeast(1)
                updateScores()
            }
        })

        updateScores()
    }

    private fun teamPanel(isA: Boolean): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 16, 8, 16)
            setBackgroundColor(panel)
        }

        val name = TextView(this).apply {
            text = if (isA) teamA else teamB
            textSize = 19f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val score = TextView(this).apply {
            text = "0"
            textSize = 42f
            setTextColor(green)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 18)
        }

        if (isA) {
            nameA = name
            scoreAView = score
        } else {
            nameB = name
            scoreBView = score
        }

        box.addView(name)
        box.addView(actionButton("নাম পরিবর্তন") { editName(isA) })
        box.addView(score)

        if (method == "দ্রুত স্কোর") {
            box.addView(actionButton("+ 1") { changeScore(isA, 1) })
            box.addView(actionButton("+ 5") { changeScore(isA, 5) })
            box.addView(actionButton("- 1") { changeScore(isA, -1) })
        } else {
            box.addView(actionButton("স্কোর লিখুন") {
                editNumber("স্কোর লিখুন", if (isA) scoreA else scoreB) {
                    if (isA) scoreA = it.coerceAtLeast(0) else scoreB = it.coerceAtLeast(0)
                    updateScores()
                }
            })
        }
        return box
    }

    private fun changeScore(isA: Boolean, amount: Int) {
        if (isA) scoreA = (scoreA + amount).coerceAtLeast(0)
        else scoreB = (scoreB + amount).coerceAtLeast(0)
        updateScores()
    }

    private fun updateScores() {
        scoreAView.text = scoreA.toString()
        scoreBView.text = scoreB.toString()
        status.text = when {
            scoreA >= target && scoreA > scoreB -> "$teamA বিজয়ী!"
            scoreB >= target && scoreB > scoreA -> "$teamB বিজয়ী!"
            scoreA == scoreB -> "খেলা সমান! | টার্গেট: $target"
            else -> "টার্গেট: $target | খেলা চলছে"
        }
    }

    private fun editName(isA: Boolean) {
        val input = EditText(this).apply {
            setText(if (isA) teamA else teamB)
            inputType = InputType.TYPE_CLASS_TEXT
        }
        AlertDialog.Builder(this)
            .setTitle("Team Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val value = input.text.toString().trim()
                if (value.isNotEmpty()) {
                    if (isA) {
                        teamA = value
                        nameA.text = value
                    } else {
                        teamB = value
                        nameB.text = value
                    }
                    updateScores()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun editNumber(title: String, current: Int, onSave: (Int) -> Unit) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(current.toString())
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                input.text.toString().toIntOrNull()?.let(onSave)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = size.toFloat()
            setTextColor(color)
            gravity = Gravity.CENTER
            if (bold) typeface = Typeface.DEFAULT_BOLD
            setPadding(5, 8, 5, 8)
        }
    }

    private fun actionButton(text: String, action: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setOnClickListener { action() }
            isAllCaps = false
        }
    }

    private fun space(height: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, height)
    }

    private fun spaceWidth(width: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(width, 1)
    }
}
