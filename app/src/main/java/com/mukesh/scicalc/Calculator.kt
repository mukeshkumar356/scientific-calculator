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
        return requireFinite(eval(e))
    }

    private fun eval(expr: String): Double {
        val p = Parser(expr)
        return p.parseExpr()
    }

    fun applyFunc(func: String, current: Double): Double = requireFinite(when (func) {
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
        "x!" -> factorial(current)
        "eˣ" -> exp(current)
        "10ˣ" -> 10.0.pow(current)
        "2ˣ" -> 2.0.pow(current)
        "abs" -> abs(current)
        else -> current
    })

    // 1/0, ln(-1), asin(2) etc. produce Infinity/NaN. Formatting those gave
    // "∞"/"NaN", which was then fed back in as the next expression and parsed
    // as 0. Reject them so the UI shows "Error" instead.
    private fun requireFinite(v: Double): Double {
        if (v.isNaN() || v.isInfinite()) throw ArithmeticException("result is not a finite number")
        return v
    }

    // Computed in Double: Long overflows silently from 21! onward. Doubles
    // stay finite up to 170!. Anything else is rejected so the UI shows
    // "Error" instead of a wrong number.
    private fun factorial(v: Double): Double {
        if (v < 0 || v > 170 || v != floor(v)) throw ArithmeticException("factorial undefined for $v")
        var result = 1.0
        for (i in 2..v.toInt()) result *= i
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
            var left = parseUnary()
            while (pos < s.length && (s[pos] == '*' || s[pos] == '/' || s[pos] == '%')) {
                val op = s[pos++]
                val right = parseUnary()
                // '%' is the modulo operator; the UI inserts it as a binary op.
                // Without handling it here the parser stopped at '%' and
                // silently dropped the rest, so "7%3" evaluated to 7.
                left = when (op) {
                    '*' -> left * right
                    '/' -> left / right
                    else -> left % right
                }
            }
            return left
        }

        // Unary minus must wrap the whole power expression, not just the
        // base, so "-2^2" evaluates as -(2^2) = -4 (standard convention),
        // not (-2)^2 = 4.
        private fun parseUnary(): Double {
            if (pos < s.length && s[pos] == '-') { pos++; return -parseUnary() }
            if (pos < s.length && s[pos] == '+') { pos++; return parseUnary() }
            return parsePow()
        }

        private fun parsePow(): Double {
            val base = parsePrimary()
            if (pos < s.length && s[pos] == '^') {
                pos++
                val exp = parseUnary()
                return base.pow(exp)
            }
            return base
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
