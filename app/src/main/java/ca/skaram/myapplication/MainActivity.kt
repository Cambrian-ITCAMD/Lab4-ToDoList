package ca.skaram.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import ca.skaram.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var taskList: ListView

    private val activities = mutableListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submitButton = binding.submitButton
        deleteButton = binding.deleteButton
        taskList = binding.toDoList // add this line to initialize taskList

        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice, activities)
        taskList.adapter = arrayAdapter

        submitButton.setOnClickListener { addNewEntry() }
        deleteButton.setOnClickListener { deleteSelectedEntries() }
        taskList.onItemClickListener = this
    }

        private fun addNewEntry() {
            val taskText = binding.enterTask.text.toString()
            if (taskText.isNotEmpty()) {
                activities.add(taskText)
                arrayAdapter.notifyDataSetChanged()
                binding.enterTask.text.clear()
            }
            else{
                Log.d("MainActivity", "Invalid Entry. Try Again!")
                addNewEntry()
            }
        }
        private fun deleteSelectedEntries() {
            val checkedPositions = taskList.checkedItemPositions
            for (i in checkedPositions.size() - 1 downTo 0) {
                if (checkedPositions.valueAt(i)) {
                    val position = checkedPositions.keyAt(i)
                    activities.removeAt(position)
                }
            }
            arrayAdapter.notifyDataSetChanged()
            taskList.clearChoices()
        }

        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            taskList.setItemChecked(position, true)
        }

        /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     *
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this
     * will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     */
}
/*
override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    if(view is CheckedTextView){
        val checkbox: CheckedTextView = view
        checkbox.toggle()
    }
}*/