package com.artsam.kata.presentation.fr

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.artsam.kata.R
import com.artsam.kata.presentation.MainActivity


class ExampleFragment2 : Fragment(R.layout.fragment_example) {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_create))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView = view.findViewById(R.id.lifecycleCallbacks)

        appendLog(getString(R.string.on_view_created))
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_view_created))

        val sfm = requireActivity().supportFragmentManager
        println("test fr ${this.hashCode()} backStackEntryCount ${requireActivity().supportFragmentManager.backStackEntryCount}")
        println("test btnAdd fragments ${sfm.fragments}")

        setListeners(view)
    }

    private fun setListeners(rootView: View) {
        val btnSend = rootView.findViewById<AppCompatButton>(R.id.btnSend)
        btnSend?.let {
            it.setOnClickListener { (requireActivity() as MainActivity).onButtonClick("btn clicked") }
        }
        val btnAdd = rootView.findViewById<AppCompatButton>(R.id.btnAdd)
        btnAdd?.let {
            it.setOnClickListener {
                val sfm = requireActivity().supportFragmentManager
                sfm.commit {
                    val arguments = Bundle()
                    arguments.putString("key", "From ${this@ExampleFragment2.hashCode()}")
                    add<ExampleFragment2>(R.id.fragmentContainer2, args = arguments)
                    setReorderingAllowed(true)
                    addToBackStack("${this@ExampleFragment2.hashCode()}")
                }
            }
        }
        val btnReplace = rootView.findViewById<AppCompatButton>(R.id.btnReplace)
        btnReplace?.let {
            it.setOnClickListener {
                val sfm = requireActivity().supportFragmentManager
                sfm.commit {
                    val arguments = Bundle()
                    arguments.putString("key", "From ${this@ExampleFragment2.hashCode()}")
                    replace<ExampleFragment2>(R.id.fragmentContainer2, args = arguments)
                    setReorderingAllowed(true)
                    addToBackStack("${this@ExampleFragment2.hashCode()}")
                }
            }
        }
        val btnRemove = rootView.findViewById<AppCompatButton>(R.id.btnRemove)
        btnRemove?.let {
            it.setOnClickListener {
                val sfm = requireActivity().supportFragmentManager
                sfm.commit { remove(this@ExampleFragment2) }
            }
        }
        val btnBack = rootView.findViewById<AppCompatButton>(R.id.btnBack)
        btnBack?.let {
            it.setOnClickListener {
                val sfm = requireActivity().supportFragmentManager
                sfm.popBackStack()
            }
        }
    }

    private fun appendLog(log: String) {
        textView.append(log + "\n")
    }

    // region lifecycle
    override fun onStart() {
        super.onStart()
        appendLog(getString(R.string.on_start))
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_start))
    }

    override fun onResume() {
        super.onResume()
        appendLog(getString(R.string.on_resume))
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_resume))
    }

    override fun onPause() {
        super.onPause()
        appendLog(getString(R.string.on_pause))
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_pause))
    }

    override fun onStop() {
        super.onStop()
        appendLog(getString(R.string.on_stop))
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_stop))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_save_instance_state))
        appendLog(getString(R.string.on_save_instance_state))
        outState.putString(SAVED_LOGS, textView.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_destroy_view))
        appendLog(getString(R.string.on_destroy_view))
        appendLog("view $textView")
        super.onDestroyView()
        appendLog("view $textView")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_destroy))
        appendLog(getString(R.string.on_destroy))
        appendLog("view $textView")
    }
    // endregion

    companion object {
        const val SAVED_LOGS = "fragment_saved_logs"
    }
}