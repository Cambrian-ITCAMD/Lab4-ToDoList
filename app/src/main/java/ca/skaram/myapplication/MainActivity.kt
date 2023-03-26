package ca.skaram.myapplication

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import ca.skaram.myapplication.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var setTimerButton: Button
    private lateinit var taskList: ListView

    private val activities = mutableListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submitButton = binding.submitButton
        deleteButton = binding.deleteButton
        setTimerButton = binding.timerButton
        taskList = binding.toDoList

        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice, activities)
        taskList.adapter = arrayAdapter
        taskList.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        submitButton.setOnClickListener { addNewEntry() }
        deleteButton.setOnClickListener { deleteSelectedEntries() }
        taskList.onItemClickListener = this

        setTimerButton.setOnClickListener { showTimePickerDialog() }
    }

    private fun addNewEntry() {
        val taskText = binding.enterTask.text.toString()
        if (taskText.isNotEmpty()) {
            activities.add(taskText)
            arrayAdapter.notifyDataSetChanged()
            binding.enterTask.text.clear()
        } else {
            Log.d("MainActivity", "Invalid Entry. Try Again!!!")
            Toast.makeText(this, "Please enter a valid task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteSelectedEntries() {
        val positions = taskList.checkedItemPositions
        for (i in positions.size() - 1 downTo 0) {
            if (positions.valueAt(i)) {
                activities.removeAt(positions.keyAt(i))
                arrayAdapter.notifyDataSetChanged()
            }
        }
        taskList.clearChoices()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        taskList.setItemChecked(position, true)
    }

    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _: TimePicker, hourOfDay: Int, minute: Int ->
            startCountDownTimer(hourOfDay, minute)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun startCountDownTimer(hours: Int, minutes: Int) {
        val totalSeconds = (hours * 60 * 60) + (minutes * 60)
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((totalSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = millisUntilFinished / 1000
                val remainingMinutes = remainingSeconds / 60
                val remainingHours = remainingMinutes / 60
                val displayTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", remainingHours, remainingMinutes % 60, remainingSeconds % 60)
                setTimerButton.isEnabled = false
                setTimerButton.alpha = 0.5f
                setTimerButton.text = displayTime

                // log the timer
                Log.d("MainActivity", "Time remaining: $displayTime")
                Toast.makeText(this@MainActivity, "Time remaining: $displayTime", Toast.LENGTH_SHORT).show()
            }

            override fun onFinish() {
                setTimerButton.isEnabled = true
                setTimerButton.alpha = 1f
                setTimerButton.text = "Set Timer"

                // log the timer finish
                Log.d("MainActivity", "Timer finished")
            }
        }

        try {
            countDownTimer?.start()
        } catch (e: Exception) {
            // handle any exceptions thrown by CountDownTimer
            Log.e("MainActivity", "Error starting timer: ${e.message}", e)
            Toast.makeText(this, "Error starting timer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     *
     * Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.
     *
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(view is CheckedTextView){
            val checkBox: CheckedTextView = view
            checkBox.toggle()
            deleteSelectedEntries()
        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}