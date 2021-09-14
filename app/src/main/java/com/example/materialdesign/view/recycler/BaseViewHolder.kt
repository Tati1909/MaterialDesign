package com.example.materialdesign.view.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

//Теперь все наши ViewHolders будут наследоваться от BaseViewHolder
abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //Каждому элементу списка будут соответствовать не только
    //какие-то данные, но и состояние элемента Boolean: развёрнут/свёрнут
    abstract fun bind(dataItem: Pair<DataRecycler, Boolean>)
}