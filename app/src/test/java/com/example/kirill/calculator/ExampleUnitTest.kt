package com.example.kirill.calculator

import com.example.kirill.calculator.presenters.MainPresenter
import com.example.kirill.calculator.views.MainView
import org.junit.Test

import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    companion object {
        private const val NORMAL_EXPRESSION = "100 + 66.774 / (12 - 6) + 45.6"
        private const val NORMAL_RESULT = "156.73"
        private const val UNAR_EXPRESSION = "100 + --66.774 / (12 - 6) + 45.6"
        private const val UNAR_RESULT = "156.56"
        private const val ERROR_EXPRESSION = "100 + -VFDV-66.774 / --(12 - 6) + 45.6"
        private const val ERROR_EXPRESSION2 = "3+1(2+7)"
    }

    @Mock
    lateinit var view: MainView
    lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = MainPresenter()
    }

    @Test
    fun normalExpression() {
        presenter.calculate(NORMAL_EXPRESSION, view)
        Mockito.verify(view).showResult(NORMAL_RESULT)
    }

    @Test
    fun unarExpression() {
        presenter.calculate(UNAR_EXPRESSION, view)
        Mockito.verify(view).showResult(UNAR_RESULT)
    }

    @Test
    fun errorExpression() {
        presenter.calculate(ERROR_EXPRESSION, view)
        Mockito.verify(view).showError()
    }

    @Test
    fun errorExpression2() {
        presenter.calculate(ERROR_EXPRESSION2, view)
        Mockito.verify(view).showError()
    }
}
