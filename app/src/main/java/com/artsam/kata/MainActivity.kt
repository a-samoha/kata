package com.artsam.kata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

//    val backStackIds = mutableListOf<String>()

    private val sharedPrefs by lazy {
        this.getSharedPreferences(SHARED_PREFS_LOGS, Context.MODE_PRIVATE)
    }
    private val btnToggleSharedPrefs by lazy {
        findViewById<AppCompatToggleButton>(R.id.btnToggleSharedPrefs)
    }
    private val tvLifecycleCbs by lazy {
        findViewById<TextView>(R.id.lifecycleCallbacks)
    }

    private val simpleRepo by lazy { SimpleRepository() }
    private val jobs = mutableMapOf<String, Job?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("test activity $this callback " + getString(R.string.on_create))
        appendLog(getString(R.string.on_create))

        savedInstanceState?.let { readFromBundle(it) }
        observeRepository()
        setListeners()
    }

    private fun readFromBundle(savedInstanceState: Bundle) {
        val savedLogs = savedInstanceState.getString(ON_SAVED_INSTANCE_LOGS)
        appendLog("\n" + savedLogs + getString(R.string.end_saved_instance_logs) + "\n")
    }

    private fun appendLog(log: String) = tvLifecycleCbs.append(log + "\n")

    private fun observeRepository() {
        val observeJob = simpleRepo.observe()
            .onEach { appendLog("observeJob onEach $it") }
            .launchIn(lifecycleScope)
        jobs["observeJob"] = observeJob
    }

    private fun setListeners() {
        val btnClearLogs = findViewById<AppCompatButton>(R.id.btnClearLogs)
        btnClearLogs.setOnClickListener { tvLifecycleCbs.text = "" }

        btnToggleSharedPrefs.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) sharedPrefs.edit().clear().apply()
        }

        val btnReadSharedPrefs = findViewById<AppCompatButton>(R.id.btnReadSharedPrefs)
        btnReadSharedPrefs.setOnClickListener {
            appendLog(sharedPrefs.getString(KEY_LOGS, "no shared prefs") ?: "shared prefs is null")
        }
        val btnGetFromSimpleRepo = findViewById<AppCompatButton>(R.id.btnGetFromSimpleRepo)
        btnGetFromSimpleRepo.setOnClickListener {
            val getJob = simpleRepo.get(true)
                .onEach { appendLog("getJob onEach $it") }
                .launchIn(lifecycleScope)
            jobs["getJob"] = getJob
        }

        val btnUpdateSimpleRepo = findViewById<AppCompatButton>(R.id.btnUpdateSimpleRepo)
        btnUpdateSimpleRepo.setOnClickListener {
            val updateJob = lifecycleScope.launch {
                simpleRepo.update(37)
                    .onSuccess { appendLog("updateJob onSuccess") }
                    .onFailure { appendLog("updateJob onFailure") }
            }
            jobs["updateJob"] = updateJob
        }

        val btnJobsInfo = findViewById<AppCompatButton>(R.id.btnJobsInfo)
        btnJobsInfo.setOnClickListener {
            jobs.forEach { (key, job) -> appendLog("$key isCompleted: ${job?.isCompleted}") }
        }

        val btnShowDialog = findViewById<AppCompatButton>(R.id.btnShowDialog)
        btnShowDialog.setOnClickListener { showAlertDialog() }

        val btnShowFragment1 = findViewById<AppCompatButton>(R.id.btnShowFragment1)
        btnShowFragment1.setOnClickListener { showExampleFragment(true) }

        val btnShowFragment2 = findViewById<AppCompatButton>(R.id.btnShowFragment2)
        btnShowFragment2.setOnClickListener { showExampleFragment(false) }

        val btnStartAnotherActivity = findViewById<AppCompatButton>(R.id.btnNewActivity)
        btnStartAnotherActivity.setOnClickListener { startNewActivity() }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setIcon(R.drawable.ic_launcher_foreground)
            .setTitle(R.string.alert_dialog)
            .setMessage(getString(R.string.simple_alert_dialog))
            .setNegativeButton(R.string.cancel) { _, _ -> appendLog(getString(R.string.dialog_canceled)) }
            .setPositiveButton(R.string.ok) { _, _ -> appendLog(getString(R.string.dialog_positive)) }
            .create()
            .show()
    }

    private fun showExampleFragment(showInFirstContainer: Boolean = true) {
        supportFragmentManager.commit {
            val arguments = Bundle()
            arguments.putString("key", "From Activity")
            if (showInFirstContainer) {
                add<ExampleFragment1>(
                    R.id.fragmentContainer1,
                    args = arguments
                )
            } else {
                add<ExampleFragment2>(
                    R.id.fragmentContainer2,
                    args = arguments
                )
            }
            setReorderingAllowed(true)
            addToBackStack(ExampleFragment1::class.java.name)
        }
    }

    private fun startNewActivity() {
        // Создаем Intent для запуска новой активити
        val intent = Intent(this@MainActivity, NewActivity::class.java)

        // Запускаем новую активити
        startActivity(intent)
    }

    // region lifecycle
    override fun onRestart() {
        super.onRestart()
        appendLog(getString(R.string.on_restart))
        println("test activity $this callback " + getString(R.string.on_restart))
    }

    override fun onStart() {
        super.onStart()
        appendLog(getString(R.string.on_start))
        println("test activity $this callback " + getString(R.string.on_start))
    }

    override fun onResume() {
        super.onResume()
        appendLog(getString(R.string.on_resume))
        println("test activity $this callback " + getString(R.string.on_resume))
    }

    override fun onPause() {
        appendLog(getString(R.string.on_pause))
        println("test activity $this callback " + getString(R.string.on_pause))
        super.onPause()
    }

    override fun onStop() {
        appendLog(getString(R.string.on_stop))
        println("test activity $this callback " + getString(R.string.on_stop))
        jobs.forEach { (key, job) -> println("test $key: isCompleted ${job?.isCompleted}") }
        super.onStop()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        println("test activity $this callback " + getString(R.string.on_restore_instance_state))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        appendLog(getString(R.string.on_save_instance_state))
        println("test callback " + getString(R.string.on_save_instance_state))
        val title = "START_SAVED_INSTANCE_LOGS:\n"
        val currentLogs = tvLifecycleCbs.text.toString()
        outState.putString(
            ON_SAVED_INSTANCE_LOGS,
            (if (currentLogs.contains(title)) "" else title) + tvLifecycleCbs.text.toString()
        )
        super.onSaveInstanceState(outState)
    }

    @Deprecated(message = "This declaration overrides deprecated member but not marked as deprecated itself.")
    override fun onStateNotSaved() {
        super.onStateNotSaved()
        appendLog(getString(R.string.on_state_not_saved))
        println("test activity $this callback " + getString(R.string.on_state_not_saved))
    }

    override fun onDestroy() {
        super.onDestroy()
        appendLog(getString(R.string.on_destroy))
        println("test activity $this callback " + getString(R.string.on_destroy))
        if (btnToggleSharedPrefs.isChecked)
            sharedPrefs.edit()
                .putString(KEY_LOGS, "\nSHARED_PREFS_LOGS:\n" + tvLifecycleCbs.text.toString())
                .apply()
        jobs.forEach { (key, job) -> println("test $key: isCompleted ${job?.isCompleted}") }
    }
    // endregion

    companion object {
        const val ON_SAVED_INSTANCE_LOGS = "ON_SAVED_INSTANCE_LOGS"
        const val SHARED_PREFS_LOGS = "SHARED_PREFS_LOGS"
        const val KEY_LOGS = "KEY_LOGS"
        const val CONTAINER = "container"
        const val FIRST = "FIRST"
        const val SECOND = "SECOND"
    }
}