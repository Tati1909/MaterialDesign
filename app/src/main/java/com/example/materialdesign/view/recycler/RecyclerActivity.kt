package com.example.materialdesign.view.recycler

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.materialdesign.databinding.ActivityRecyclerBinding

class RecyclerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerBinding

    //isNewList будет регулировать наполнение списка
    private var isNewList = false
    //перетаскивание за рукоятку
    lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var adapter: RecyclerActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // _binding = FragmentSettingsBinding.inflate(inflater, container, false)


        //создали список из 2 элементов(заголовка и элемента Марс
        val data = arrayListOf(
            Pair(DataRecycler(1,"Mars", ""), false))

        data.add(0, Pair(DataRecycler(0,"Header"), false))

        adapter = RecyclerActivityAdapter(
            object : RecyclerActivityAdapter.OnListItemClickListener {
                override fun onItemClick(dataItemClick: DataRecycler) {
                    Toast.makeText(this@RecyclerActivity, dataItemClick.someText,
                        Toast.LENGTH_SHORT).show()
                }
            },
            data,
            object : OnStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }
            }
        )

        binding.recyclerView.adapter = adapter
        binding.recyclerActivityFAB.setOnClickListener { adapter.appendItem() }

        //применятся для смахивания и перетаскивания элементов списка
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        //по нажатию на кнопку будем менять данные в списке
        binding.recyclerActivityDiffUtilFAB.setOnClickListener { changeAdapterData() }
    }

    //Методы changeAdapterData и createItemList обновляют список с каждым нажатием, имитируя
    //новые данные с сервера или применение фильтров, поиска или какой-либо сортировки контента
    //пользователем:
    private fun changeAdapterData() {
        adapter.setItems(createItemList(isNewList))
        isNewList = !isNewList
    }

    private fun createItemList(instanceNumber: Boolean): List<Pair<DataRecycler, Boolean>> {
        return when (instanceNumber) {
            false -> listOf(
                Pair(DataRecycler(0, "Header"), false),
                Pair(DataRecycler(1, "Mars", ""), false),
                Pair(DataRecycler(2, "Mars", ""), false),
                Pair(DataRecycler(3, "Mars", ""), false),
                Pair(DataRecycler(4, "Mars", ""), false),
                Pair(DataRecycler(5, "Mars", ""), false),
                Pair(DataRecycler(6, "Mars", ""), false)
            )
            true -> listOf(
                Pair(DataRecycler(0, "Header"), false),
                Pair(DataRecycler(1, "Mars", ""), false),
                Pair(DataRecycler(2, "Earth", "Earth"), false),
                Pair(DataRecycler(3, "Mars", ""), false),
                Pair(DataRecycler(4, "Neptune", ""), false),
                Pair(DataRecycler(5, "Earth", "Earth"), false),
                Pair(DataRecycler(6, "Mars", ""), false)
            )
        }
    }
}



