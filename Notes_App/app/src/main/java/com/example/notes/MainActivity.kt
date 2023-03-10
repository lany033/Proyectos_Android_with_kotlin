package com.example.notes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Writer

class MainActivity : AppCompatActivity() {

    //lateinit: it a no-null variable and its value will assign later
    private lateinit var adapter: NoteAdapter //adapter: store a reference to the NoteAdapter class
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    //converts users notes into JSON
    companion object{
        private const val FILEPATH = "notes.json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            NewNote().show(supportFragmentManager,"")
        }

        //we’ll now set up the NoteAdapter adapter and RecyclerView widget
        adapter = NoteAdapter(this)

        //RecyclerView widgets
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter

        //The retrieveNotes method will need to run when the app is launched
        adapter.noteList = retrieveNotes()
        //notifies the adapter that one or more items have been added to the RecyclerView
        adapter.notifyItemRangeInserted(0,adapter.noteList.size)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onStart() {
        super.onStart()
        val nightThemeSelected = sharedPreferences.getBoolean("theme",false)
        if (nightThemeSelected) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val showDividingLines = sharedPreferences.getBoolean("dividingLines",false)
        if (showDividingLines) binding.recyclerView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
        else if (binding.recyclerView.itemDecorationCount > 0) binding.recyclerView.removeItemDecorationAt(0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun createNewNote(n: Note) {
        adapter.noteList.add(n)
        adapter.notifyItemInserted(adapter.noteList.size-1)
        saveNotes()
    }

    fun deleteNote(index: Int) {
        adapter.noteList.removeAt(index)
        adapter.notifyItemRemoved(index)
        saveNotes()
    }

    fun showNote(index: Int){
        val dialog = ShowNote(adapter.noteList[index], index)
        dialog.show(supportFragmentManager,"")
    }

    private fun saveNotes(){
        val notes = adapter.noteList
        val gson = GsonBuilder().create()
        val jsonNotes = gson.toJson(notes)

        var writer: Writer? = null
        try {
            val out = this.openFileOutput(FILEPATH, Context.MODE_PRIVATE)
            writer = OutputStreamWriter(out)
            writer.write(jsonNotes)
        }catch (e: Exception){
            writer?.close()
        }finally {
            writer?.close()
        }
    }

    //list of notes objects stored in the notes.json file
    private fun retrieveNotes(): MutableList<Note>{
        var noteList = mutableListOf<Note>()
        //chekear si exite una lista en el lugar especificado
        if (this.getFileStreamPath(FILEPATH).isFile){
            //si exite lalista es decodificada usando BufferedReader and InputStreamReader classes
            var reader: BufferedReader? = null
            try {
                val fileInput = this.openFileInput(FILEPATH)
                reader = BufferedReader(InputStreamReader(fileInput))
                //Los contenidos decodificados se recopilan utilizando una instancia de StringBuilder
                val stringBuilder = StringBuilder()

                for (line in reader.readLine()) stringBuilder.append(line)

                if (stringBuilder.isNotEmpty()){
                    val listType = object: TypeToken<List<Note>>() {}.type
                    //converted to a list of Note objects using GSON
                    noteList = Gson().fromJson(stringBuilder.toString(),listType)
                }
            }catch (e: Exception){
                reader?.close()
            }finally {
                reader?.close()
            }
        }
        return noteList //devuelve una lista mutable de elementos note objec
    }

}