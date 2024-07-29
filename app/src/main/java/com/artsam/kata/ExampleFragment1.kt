package com.artsam.kata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace


class ExampleFragment1 : Fragment() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_create))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_example, container, false)
        // only for logs, use view from onViewCreated()
        textView = view.findViewById(R.id.lifecycleCallbacks)

        appendLog("This ${this.hashCode()}")

        val parent = arguments?.getString("key", "DefaultStr")
        parent?.let { appendLog(it) }

        val savedInstanceStateLogs = savedInstanceState?.getString(SAVED_LOGS)

        appendLog(savedInstanceStateLogs + "\n" + getString(R.string.on_create_view))
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_create_view))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appendLog(getString(R.string.on_view_created))
        println("test fr ${this.hashCode()} callback " + getString(R.string.on_view_created))

        val sfm = requireActivity().supportFragmentManager
        println("test fr ${this.hashCode()} backStackEntryCount ${requireActivity().supportFragmentManager.backStackEntryCount}")
        println("test btnAdd fragments ${sfm.fragments}")

        setListeners(view)
    }

    private fun setListeners(rootView: View) {
        val btnAdd = rootView.findViewById<AppCompatButton>(R.id.btnAdd)
        btnAdd?.let {
            it.setOnClickListener {
                val sfm = requireActivity().supportFragmentManager
                sfm.commit {
                    val arguments = Bundle()
                    arguments.putString("key", "From ${this@ExampleFragment1.hashCode()}")
                    add<ExampleFragment1>(R.id.fragmentContainer1, args = arguments)
                    setReorderingAllowed(true)
                    addToBackStack("${this@ExampleFragment1.hashCode()}")
                }
            }
        }
        val btnReplace = rootView.findViewById<AppCompatButton>(R.id.btnReplace)
        btnReplace?.let {
            it.setOnClickListener {
                val sfm = requireActivity().supportFragmentManager
                sfm.commit {
                    val arguments = Bundle()
                    arguments.putString("key", "From ${this@ExampleFragment1.hashCode()}")
                    replace<ExampleFragment1>(R.id.fragmentContainer1, args = arguments)
                    setReorderingAllowed(true)
                    addToBackStack("${this@ExampleFragment1.hashCode()}")
                }
            }
        }
        val btnRemove = rootView.findViewById<AppCompatButton>(R.id.btnRemove)
        btnRemove?.let {
            it.setOnClickListener {
                val sfm = requireActivity().supportFragmentManager
                sfm.commit { remove(this@ExampleFragment1) }
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