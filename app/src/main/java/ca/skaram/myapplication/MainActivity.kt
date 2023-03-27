package ca.skaram.myapplication

import android.app.AlertDialog
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

    // Declaring the UI elements and variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var setTimerButton: Button
    private lateinit var taskList: ListView

    // A list to store the user's tasks
    private val activities = mutableListOf<String>("Activity1", "Activity2", "Activity3", "Activity4", "Activity5")

    // An adapter for the task list
    private lateinit var arrayAdapter: ArrayAdapter<String>

    // A variable to store the count down timer
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflating the layout and setting it as the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing the UI elements
        submitButton = binding.submitButton
        deleteButton = binding.deleteButton
        setTimerButton = binding.timerButton
        taskList = binding.toDoList

        // Initializing the task list adapter
        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice, activities)
        taskList.adapter = arrayAdapter
        taskList.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        // Setting click listeners for the buttons
        submitButton.setOnClickListener { addNewEntry() }
        deleteButton.setOnClickListener { deleteSelectedEntries() }
        setTimerButton.setOnClickListener { showTimePickerDialog() }

        // Setting the item click listener for the task list
        taskList.onItemClickListener = this
    }

    /**
     *This function is called when an item in the task list is clicked.
     *It marks the clicked item as checked.
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        taskList.setItemChecked(position, true)
        // Create a new AlertDialog builder
        val builder = AlertDialog.Builder(this)

        // Set the dialog title and message using the clicked task
        builder.setTitle("Rename Task")
            .setMessage("Enter new name for task:")

        // Create an EditText view for the dialog and set its initial value to the clicked task
        val input = EditText(this)
        input.setText(activities[position])

        // Set the EditText view as the dialog view
        builder.setView(input)

        // Set the positive button action to rename the task
        builder.setPositiveButton("Rename") { _, _ ->
            // Get the new task name from the EditText view
            val newName = input.text.toString()

            // Rename the task in the activities list and update the adapter
            activities[position] = newName
            arrayAdapter.notifyDataSetChanged()
        }

        // Set the negative button action to cancel the dialog
        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Dismiss the dialog when the negative button is clicked
            dialog.cancel()
        }

        // Create and show the dialog
        builder.create().show()
    }



    /**
     *This function starts a count down timer that counts down from the selected time.
     *The remaining time is displayed on the set timer button.
     *When the timer finishes, the button is enabled and its text is set to "Set Timer".
     */
    private fun startCountDownTimer(hours: Int, minutes: Int) {
        val totalSeconds = (hours * 60 * 60) + (minutes * 60)

        // cancel any existing timer
        countDownTimer?.cancel()
        // create a new count down timer
        countDownTimer = object : CountDownTimer((totalSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // calculate the remaining time
                val remainingSeconds = millisUntilFinished / 1000
                val remainingMinutes = remainingSeconds / 60
                val remainingHours = remainingMinutes / 60

                // format the remaining time as a string
                val displayTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", remainingHours, remainingMinutes % 60, remainingSeconds % 60)

                // update the timer button text and appearance
                setTimerButton.isEnabled = false
                setTimerButton.alpha = 0.5f
                setTimerButton.text = displayTime

                // log the remaining time to the console and display a toast message with the remaining time
                Log.d("MainActivity", "Time remaining: $displayTime")
                Toast.makeText(this@MainActivity, "Time remaining: $displayTime", Toast.LENGTH_SHORT).show()
            }

            // This function is called when the timer has finished
            override fun onFinish() {

                // reset the timer button text and appearance to their default values
                setTimerButton.isEnabled = true
                setTimerButton.alpha = 1f
                setTimerButton.text = "Set Timer"

                // log the timer finish to the console
                Log.d("MainActivity", "Timer finished")
            }
        }

        // Start the CountDownTimer and handle any exceptions that might occur
        try {
            countDownTimer?.start()
        } catch (e: Exception) {
            // handle any exceptions thrown by CountDownTimer
            Log.e("MainActivity", "Error starting timer: ${e.message}", e)
            Toast.makeText(this, "Error starting timer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * This function adds a new task to the task list.
     * If the user does not enter a valid task, a toast message is displayed.
     */
    private fun addNewEntry() {
        // get the text entered by the user
        val taskText = binding.enterTask.text.toString()
        if (taskText.isNotEmpty()) {
            // add the task to the list and notify the adapter
            activities.add(taskText)
            arrayAdapter.notifyDataSetChanged()
            binding.enterTask.text.clear()          // clear the text field
        } else {
            // if the user did not enter a valid task, display a message to the user
            Log.d("MainActivity", "Invalid Entry. Try Again!!!")
            Toast.makeText(this, "Please enter a valid task", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     *This function deletes the selected tasks from the task list.
     */
    private fun deleteSelectedEntries() {
        // get the positions of the selected tasks
        val positions = taskList.checkedItemPositions

        // iterate over the selected tasks, removing them from the list and notifying the adapter
        for (i in positions.size() - 1 downTo 0) {
            if (positions.valueAt(i)) {
                activities.removeAt(positions.keyAt(i))
                arrayAdapter.notifyDataSetChanged()
            }
        }

        // clear the selection on the list
        taskList.clearChoices()
    }


    /**
     *This function shows a time picker dialog and starts a count down timer.
     *The timer counts down from the selected time and displays the remaining time on the button.
     */
    private fun showTimePickerDialog() {
        // get the current time
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        // create a time picker dialog
        val timePickerDialog = TimePickerDialog(this, { _: TimePicker, hourOfDay: Int, minute: Int ->
            startCountDownTimer(hourOfDay, minute)
        }, hour, minute, true)

        // show the time picker dialog
        timePickerDialog.show()
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