# Scientific Calculator

**A native Android scientific calculator with a hand-written recursive-descent expression parser — no third-party math/eval library.**

[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](#)
[![Language](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](#)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](LICENSE)

## Overview

Most "calculator" side projects just call a math library's `eval()`. This one parses and evaluates arithmetic expressions itself, respecting standard operator precedence (`^` binds tighter than `*`/`/`, which binds tighter than `+`/`-`), with full support for parentheses and unary minus.

## ✨ Features

- Full scientific function set — `sin/cos/tan` (+ inverse & hyperbolic), `ln/log/log2`, `√`/`∛`, powers, factorial, `1/x`
- Degrees/Radians toggle
- Constants: `π`, `e`
- Material Design UI with view binding

## 🧮 The parser

A small recursive-descent parser, implemented from scratch:

```kotlin
private fun parseAddSub(): Double {
    var left = parseMulDiv()
    while (pos < s.length && (s[pos] == '+' || s[pos] == '-')) {
        val op = s[pos++]
        val right = parseMulDiv()
        left = if (op == '+') left + right else left - right
    }
    return left
}
```

Grammar (highest to lowest precedence): `primary → unary → pow → mulDiv → addSub`, with `primary` handling parenthesized sub-expressions recursively. See [`Calculator.kt`](app/src/main/java/com/mukesh/scicalc/Calculator.kt) for the full implementation.

## 🚀 Build it yourself

```bash
git clone https://github.com/mukeshkumar356/scientific-calculator.git
cd scientific-calculator
./gradlew assembleDebug
```

## 📄 License

MIT — see [LICENSE](LICENSE).

---

Built by **[Mukesh Kumar](https://github.com/mukeshkumar356)**
