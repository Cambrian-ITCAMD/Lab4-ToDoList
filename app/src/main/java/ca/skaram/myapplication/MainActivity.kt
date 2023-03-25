package ca.skaram.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import ca.skaram.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var studentsList: ListView

    private val studentEntries = mutableListOf<String>("Karam", "Rishabh", "Aneri")
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submitButton = binding.submitButton
        deleteButton = binding.deleteButton
        studentsList = binding.studentsList

        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_multichoice, studentEntries)
        studentsList.adapter = arrayAdapter

        submitButton.setOnClickListener { addNewEntry() }
        deleteButton.setOnClickListener { deleteSelectedEntries() }
        studentsList.setOnItemClickListener { parent, view, position, id ->
            toggleCheckedState(view)
        }
    }

    private fun addNewEntry() {
        val studentName = binding.enterName.text.toString().trim()
        if (studentName.isNotEmpty()) {
            if (!studentEntries.contains(studentName)) {
                studentEntries.add(studentName)
                arrayAdapter.notifyDataSetChanged()
                binding.enterName.text.clear()
            } else {
                Log.d("MainActivity", "Duplicate entry: $studentName")
                Toast.makeText(this, "Duplicate entry: $studentName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteSelectedEntries() {
        val checkedPositions = studentsList.checkedItemPositions
        for (i in checkedPositions.size() - 1 downTo 0) {
            if (checkedPositions.valueAt(i)) {
                val position = checkedPositions.keyAt(i)
                studentEntries.removeAt(position)
            }
        }
        studentsList.clearChoices()
        arrayAdapter.notifyDataSetChanged()
    }

    private fun toggleCheckedState(view: View) {
        val checkedTextView = view.findViewById<CheckedTextView>(android.R.id.text1)
        checkedTextView.isChecked = !checkedTextView.isChecked
    }
}
