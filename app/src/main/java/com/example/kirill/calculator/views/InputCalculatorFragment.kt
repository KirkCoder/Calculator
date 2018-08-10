package com.example.kirill.calculator.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.support.design.widget.Snackbar
import android.text.TextUtils
import com.example.kirill.calculator.R
import com.example.kirill.calculator.presenters.MainPresenter
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


private const val ERROR_MSG_KEY = "error_msg_key"

fun newInputCalculatorFragment(errorMsg: String): InputCalculatorFragment{
    val fragment = InputCalculatorFragment()
    val bundle = Bundle()
    bundle.putString(ERROR_MSG_KEY, errorMsg)
    fragment.arguments = bundle
    return InputCalculatorFragment()
}

class InputCalculatorFragment: Fragment(), MainView {

    companion object {
        const val TAG = "input_calculator_fragment"
        const val ERROR = "ERROR"
    }

    private lateinit var input: EditText
    private lateinit var calculateBtn: Button
    private lateinit var errorMsg: String
    private var presenter: MainPresenter? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        retainInstance = true
        presenter = MainPresenter()
        return inflater.inflate(R.layout.input_calculator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorMsg = arguments?.getString(ERROR_MSG_KEY) ?: ERROR
        findViews(view)
        setListener()
    }

    private fun setListener() {
        calculateBtn.setOnClickListener {
            hideKeyboard()
            if (!TextUtils.isEmpty(input.text)){
                presenter?.calculate(input.text.toString(), this)
            } else {
                showError()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun findViews(view: View) {
        input = view.findViewById(R.id.input)
        calculateBtn = view.findViewById(R.id.btn)
    }

    override fun onDestroyOptionsMenu() {
        presenter = null
        super.onDestroyOptionsMenu()
    }

    override fun showError(){
        if (view != null){
            Snackbar.make(view!!, errorMsg, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showResult(res: String) {
        if (view != null){
            Snackbar.make(view!!, res, Snackbar.LENGTH_LONG).show()
        }
    }
}