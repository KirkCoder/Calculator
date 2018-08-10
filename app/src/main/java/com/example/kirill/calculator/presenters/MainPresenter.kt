package com.example.kirill.calculator.presenters

import com.example.kirill.calculator.views.MainView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.NoSuchElementException

enum class Tokens {
    NUMBER, OPERATOR, OPEN_BUCKET, CLOSE_BUCKET, ERROR, UNAR_PLUS, UNAR_MINUS
}

class MainPresenter {
    fun calculate(expression: String, view: MainView) {
        try {
            val res = parse(expression)
            view.showResult(res)
        } catch (e: NoSuchElementException) {
            view.showError()
        } catch (e1: NumberFormatException) {
            view.showError()
        } catch (e2: NotValidInputException) {
            view.showError()
        }
    }

    companion object {
        private const val PLUS = "+"
        private const val MINUS = "-"
        private const val MINUS_CHAR = '-'
        private const val MULTIPLY = "*"
        private const val DIVIDE = "/"
        private const val OPERATORS = "$PLUS$MINUS$MULTIPLY$DIVIDE"
        private const val OPEN_BUCKET = "("
        private const val CLOSE_BUCKET = ")"
        private const val SEPARATOR = ","
        private const val SEPARATOR2 = "."
        private const val UNAR_MINUS = "m"
        private const val UNAR_PLUS = "p"
        private const val SPACE = " "
        private const val ZERO = "0"
        private const val FIRST_INDEX = 0
        private const val STANDARD_PRIORITY = 1
        private const val HIGH_PRIORITY = 2
        private const val SCALE = 2
        private const val NO_SPACE = ""
        private const val INCREMENT = "1"
        private const val ALL_OPERATORS = "$PLUS$MINUS$MULTIPLY$DIVIDE$UNAR_PLUS$UNAR_MINUS"
    }

    private fun parse(expression: String): String {

        var exp = expression.replace(SPACE, NO_SPACE)
                .replace("$MINUS$MINUS", UNAR_MINUS)
                .replace("$PLUS$PLUS", UNAR_PLUS)
                .replace("$OPEN_BUCKET$MINUS", "$OPEN_BUCKET$ZERO$MINUS")
                .replace("$OPEN_BUCKET$PLUS", "$OPEN_BUCKET$ZERO$PLUS")
                .replace("$MINUS$CLOSE_BUCKET", "$MINUS$ZERO$CLOSE_BUCKET")
                .replace("$PLUS$CLOSE_BUCKET", "$PLUS$ZERO$CLOSE_BUCKET")
                .replace("$SEPARATOR$MINUS", "$SEPARATOR$ZERO$MINUS")
                .replace("$SEPARATOR$PLUS", "$SEPARATOR$ZERO$PLUS")
                .replace("$SEPARATOR2$MINUS", "$SEPARATOR$ZERO$MINUS")
                .replace("$SEPARATOR2$PLUS", "$SEPARATOR$ZERO$PLUS")

        if (exp[FIRST_INDEX] == MINUS_CHAR) {
            exp = "$ZERO$exp"
        }
        val tokens = StringTokenizer(exp,
                "$OPERATORS$ALL_OPERATORS$OPEN_BUCKET$CLOSE_BUCKET", true)

        val out = ArrayDeque<String>()
        val stack = Stack<String>()

        while (tokens.hasMoreTokens()) {
            val token = tokens.nextToken()
            tokensParsing(token, out, stack, tokens)
        }
        while (!stack.empty()) {
            out.addLast(stack.pop())
            calculate(out)
        }
        return out.last
    }

    private fun tokensParsing(token: String, out: ArrayDeque<String>, stack: Stack<String>, tokens: StringTokenizer) {
        when (tokenStatus(token)) {
            Tokens.NUMBER -> out.addLast(token)
            Tokens.OPERATOR -> {
                while (!stack.empty()
                        && isOperator(stack.lastElement())
                        && getPrecedence(token) <= getPrecedence(stack.lastElement())) {
                    out.addLast(stack.pop())
                    calculate(out)
                }
                stack.push(token)
            }
            Tokens.OPEN_BUCKET -> stack.push(token)
            Tokens.CLOSE_BUCKET -> {
                while (!stack.empty() && !isOpenBracket(stack.lastElement())) {
                    out.addLast(stack.pop())
                    calculate(out)
                }
                stack.pop()
            }
            Tokens.UNAR_MINUS -> {
                val tokenTmp = tokens.nextToken()
                if (tokenTmp != null) {
                    if (isNumber(tokenTmp)) {
                        out.addLast(unarMinusCalculate(tokenTmp))
                    } else {
                        tokensParsing(tokenTmp, out, stack, tokens)
                    }
                }
            }
            Tokens.UNAR_PLUS -> {
                val tokenTmp = tokens.nextToken()
                if (tokenTmp != null) {
                    if (isNumber(tokenTmp)) {
                        out.addLast(unarPlusCalculate(tokenTmp))
                    } else {
                        tokensParsing(tokenTmp, out, stack, tokens)
                    }
                }
            }
            Tokens.ERROR -> throw NotValidInputException()
        }
    }

    private fun unarMinusCalculate(token: String): String {
        return BigDecimal(token)
                .add(BigDecimal(INCREMENT).negate())
                .toString()
    }

    private fun unarPlusCalculate(token: String): String {
        return BigDecimal(token)
                .add(BigDecimal(INCREMENT))
                .toString()
    }

    private fun calculate(out: ArrayDeque<String>) {
        val operator = out.removeLast()
        val one = out.removeLast()
        val two = out.removeLast()
        when (operator) {
            PLUS -> {
                out.addLast(BigDecimal(two).add(BigDecimal(one)).toString())
            }
            MINUS -> {
                out.addLast(BigDecimal(two).add(BigDecimal(one).negate()).toString())
            }
            MULTIPLY -> {
                out.addLast(BigDecimal(two).multiply(BigDecimal(one)).toString())
            }
            DIVIDE -> {
                out.addLast(BigDecimal(two).divide(BigDecimal(one), SCALE, RoundingMode.HALF_UP).toString())
            }
        }
    }

    private fun tokenStatus(token: String): Tokens {
        var res = Tokens.ERROR
        if (isNumber(token)) res = Tokens.NUMBER
        if (isCloseBracket(token)) res = Tokens.CLOSE_BUCKET
        if (isOpenBracket(token)) res = Tokens.OPEN_BUCKET
        if (isOperator(token)) res = Tokens.OPERATOR
        if (isUnarPlus(token)) res = Tokens.UNAR_PLUS
        if (isUnarMinus(token)) res = Tokens.UNAR_MINUS
        return res
    }

    private fun isUnarMinus(token: String): Boolean {
        return token.equals(UNAR_MINUS)
    }

    private fun isUnarPlus(token: String): Boolean {
        return token == UNAR_PLUS
    }

    private fun isNumber(token: String): Boolean {
        try {
            java.lang.Double.parseDouble(token)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun isOpenBracket(token: String): Boolean {
        return token == OPEN_BUCKET
    }

    private fun isCloseBracket(token: String): Boolean {
        return token == CLOSE_BUCKET
    }

    private fun isOperator(token: String): Boolean {
        return ALL_OPERATORS.contains(token)
    }

    private fun getPrecedence(token: String): Int {
        return if (token == PLUS || token == MINUS) {
            STANDARD_PRIORITY
        } else HIGH_PRIORITY
    }
}

class NotValidInputException : Exception()