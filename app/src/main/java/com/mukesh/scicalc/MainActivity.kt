package com.mukesh.scicalc

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mukesh.scicalc.databinding.ActivityMainBinding
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private var expression = ""
    private var result = ""
    private var lastWasResult = false
    private val history = mutableListOf<String>()
    private val fmt = DecimalFormat("0.##########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setupBasicButtons()
        setupSciButtons()

        b.btnDeg.setOnClickListener {
            Calculator.isDeg = !Calculator.isDeg
            b.btnDeg.text = if (Calculator.isDeg) "DEG" else "RAD"
        }

        b.btnHistory.setOnClickListener {
            if (history.isEmpty()) return@setOnClickListener
            val msg = history.takeLast(5).joinToString("\n")
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBasicButtons() {
        val digitIds = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8", R.id.btn9 to "9"
        )
        digitIds.forEach { (id, d) -> findViewById<Button>(id).setOnClickListener { onDigit(d) } }

        b.btnDot.setOnClickListener { onDigit(".") }
        b.btnPlus.setOnClickListener { onOp("+") }
        b.btnMinus.setOnClickListener { onOp("-") }
        b.btnMul.setOnClickListener { onOp("×") }
        b.btnDiv.setOnClickListener { onOp("÷") }
        b.btnOpenParen.setOnClickListener { onDigit("(") }
        b.btnCloseParen.setOnClickListener { onDigit(")") }
        b.btnPi.setOnClickListener { onDigit("π") }
        b.btnE.setOnClickListener { onDigit("e") }
        b.btnPercent.setOnClickListener { onOp("%") }
        b.btnPow.setOnClickListener { onOp("^") }

        b.btnClear.setOnClickListener { clear() }
        b.btnBack.setOnClickListener { backspace() }
        b.btnEquals.setOnClickListener { evaluate() }
    }

    private fun setupSciButtons() {
        val funcMap = mapOf(
            R.id.btnSin to "sin", R.id.btnCos to "cos", R.id.btnTan to "tan",
            R.id.btnAsin to "asin", R.id.btnAcos to "acos", R.id.btnAtan to "atan",
            R.id.btnLn to "ln", R.id.btnLog to "log", R.id.btnLog2 to "log2",
            R.id.btnSqrt to "√", R.id.btnCbrt to "∛", R.id.btnSq to "x²",
            R.id.btnCube to "x³", R.id.btnInv to "1/x", R.id.btnFact to "x!",
            R.id.btnExp to "eˣ", R.id.btnPow10 to "10ˣ", R.id.btnAbs to "abs"
        )
        funcMap.forEach { (id, func) ->
            findViewById<Button>(id).setOnClickListener { onFunction(func) }
        }
    }

    private fun onDigit(d: String) {
        if (lastWasResult && (d == "." || d.first().isDigit())) {
            expression = ""; lastWasResult = false
        }
        expression += d
        b.tvExpression.text = expression
        liveResult()
    }

    private fun onOp(op: String) {
        if (lastWasResult) lastWasResult = false
        if (expression.isEmpty() && result.isNotEmpty()) expression = result
        expression += op
        b.tvExpression.text = expression
    }

    private fun onFunction(func: String) {
        val cur = if (result.isNotEmpty()) result.toDoubleOrNull() ?: 0.0 else 0.0
        try {
            val r = Calculator.applyFunc(func, cur)
            result = fmt.format(r)
            b.tvResult.text = result
            expression = result
            b.tvExpression.text = "$func(${fmt.format(cur)}) ="
            lastWasResult = true
        } catch (e: Exception) {
            b.tvResult.text = "Error"
        }
    }

    private fun evaluate() {
        val expr = expression.ifEmpty { return }
        try {
            val r = Calculator.compute(expr)
            result = fmt.format(r)
            history.add("$expr = $result")
            b.tvResult.text = result
            b.tvExpression.text = "$expr ="
            expression = result
            lastWasResult = true
        } catch (e: Exception) {
            b.tvResult.text = "Error"
        }
    }

    private fun liveResult() {
        try {
            if (expression.length > 2) {
                val r = Calculator.compute(expression)
                b.tvResult.text = fmt.format(r)
            }
        } catch (_: Exception) {}
    }

    private fun clear() {
        expression = ""; result = ""; lastWasResult = false
        b.tvExpression.text = ""; b.tvResult.text = "0"
    }

    private fun backspace() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            b.tvExpression.text = expression
            liveResult()
        }
    }
}
