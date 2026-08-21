package com.mukesh.scicalc

import kotlin.math.*

object Calculator {
    var isDeg = true

    private fun toRad(v: Double) = if (isDeg) Math.toRadians(v) else v
    private fun fromRad(v: Double) = if (isDeg) Math.toDegrees(v) else v

    fun compute(expr: String): Double {
        val e = expr.trim()
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
        return eval(e)
    }

    private fun eval(expr: String): Double {
        val p = Parser(expr)
        return p.parseExpr()
    }

    fun applyFunc(func: String, current: Double): Double = when (func) {
        "sin" -> sin(toRad(current))
        "cos" -> cos(toRad(current))
        "tan" -> tan(toRad(current))
        "asin" -> fromRad(asin(current))
        "acos" -> fromRad(acos(current))
        "atan" -> fromRad(atan(current))
        "sinh" -> sinh(current)
        "cosh" -> cosh(current)
        "tanh" -> tanh(current)
        "ln" -> ln(current)
        "log" -> log10(current)
        "log2" -> log2(current)
        "√" -> sqrt(current)
        "∛" -> cbrt(current)
        "x²" -> current * current
        "x³" -> current * current * current
        "1/x" -> 1.0 / current
        "x!" -> factorial(current.toInt()).toDouble()
        "eˣ" -> exp(current)
        "10ˣ" -> 10.0.pow(current)
        "2ˣ" -> 2.0.pow(current)
        "abs" -> abs(current)
        else -> current
    }

    private fun factorial(n: Int): Long {
        if (n < 0) return 0
        var result = 1L
        for (i in 2..n) result *= i
        return result
    }

    private class Parser(private val s: String) {
        private var pos = 0

        fun parseExpr(): Double = parseAddSub()

        private fun parseAddSub(): Double {
            var left = parseMulDiv()
            while (pos < s.length && (s[pos] == '+' || s[pos] == '-')) {
                val op = s[pos++]
                val right = parseMulDiv()
                left = if (op == '+') left + right else left - right
            }
            return left
        }

        private fun parseMulDiv(): Double {
            var left = parsePow()
            while (pos < s.length && (s[pos] == '*' || s[pos] == '/')) {
                val op = s[pos++]
                val right = parsePow()
                left = if (op == '*') left * right else left / right
            }
            return left
        }

        private fun parsePow(): Double {
            var base = parseUnary()
            while (pos < s.length && s[pos] == '^') {
                pos++
                val exp = parseUnary()
                base = base.pow(exp)
            }
            return base
        }

        private fun parseUnary(): Double {
            if (pos < s.length && s[pos] == '-') { pos++; return -parsePrimary() }
            if (pos < s.length && s[pos] == '+') { pos++ }
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            if (pos < s.length && s[pos] == '(') {
                pos++
                val v = parseExpr()
                if (pos < s.length && s[pos] == ')') pos++
                return v
            }
            val start = pos
            while (pos < s.length && (s[pos].isDigit() || s[pos] == '.' || (s[pos] == 'E' && pos > start))) pos++
            return if (pos > start) s.substring(start, pos).toDoubleOrNull() ?: 0.0 else 0.0
        }
    }
}
