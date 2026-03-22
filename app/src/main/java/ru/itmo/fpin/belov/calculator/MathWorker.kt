package ru.itmo.fpin.belov.calculator

class MathWorker {
    fun evaluate(expr: String): Double {
        val tokens = expr.split("+","-","*","/")
        val numbers = tokens.map { it.toDouble() }

        if (expr.contains("+"))
            return numbers[0] + numbers[1]

        if (expr.contains("-"))
            return numbers[0] - numbers[1]

        if (expr.contains("*"))
            return numbers[0] * numbers[1]

        if (expr.contains("/"))
            return numbers[0] / numbers[1]

        return numbers[0]
    }

}