package com.example.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val mainActivity: MainActivity): RecyclerView.Adapter<NoteAdapter.ViewHolderNote>() {
    //noteList: Will contain the details of every note the user has saved
    var noteList = mutableListOf<Note>()

    //inner class: Can acces data such as variables and methods from the outer class and vice versa,
    // even is the that content is marked as private
    inner class ViewHolderNote(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        internal var mTitle = view.findViewById<View>(R.id.viewTitle) as TextView
        internal var mContents = view.findViewById<View>(R.id.viewContents) as TextView

        init {
            view.isClickable = true
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mainActivity.showNote(layoutPosition)
        }

    }

    //onCreateViewHolder: tells the NoteAdapter to use note_preview.xml layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderNote {
        return ViewHolderNote(LayoutInflater.from(parent.context).inflate(R.layout.note_preview, parent, false))
    }

    //onBindViewHolder: retrieves the tielt and contents of each Note Object in the note List
    override fun onBindViewHolder(holder: ViewHolderNote, position: Int) {
        val note = noteList[position]

        holder.mTitle.text = note.title
        //If the contents string is less than 15
        //characters long then the full contents will be displayed in the preview. Otherwise, the contents string will be
        //shortened to 15 characters using Kotlin’s substring method
        holder.mContents.text = if (note.contents.length < 15) note.contents
        else note.contents.substring(0,15) + "..."
    }

    //will calculate how
    //many items are loaded into the RecyclerView
    override fun getItemCount(): Int = noteList.size
}