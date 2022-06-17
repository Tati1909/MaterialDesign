package com.example.materialdesign.view.recycler

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.materialdesign.R
import com.example.materialdesign.databinding.ActivityRecyclerItemNoteBinding

class RecyclerActivityAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private var dataRecycler: MutableList<Pair<DataRecycler, Boolean>>,
    //В адаптер передаём ещё и слушатель нажатия на рукоятку:
    // как только пользователь тянет за неё, мы вызываем метод startDrag(),
    // всю анимацию делают уже за нас.
    private val dragListener: OnStartDragListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)

        return when (viewType) {
            R.layout.activity_recycler_item_header -> HeaderViewHolder(view)
            R.layout.activity_recycler_item_note -> NoteViewHolder(view)
            else -> throw IllegalArgumentException("Unknown ViewHolder")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val element = dataRecycler[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(element)
            is NoteViewHolder -> holder.bind(element)
            else -> throw IllegalArgumentException("Unknown ViewHolder")
        }
    }

    override fun getItemCount(): Int {
        return dataRecycler.size
    }

    //1 - TYPE_HEADER, 2 - Note
    override fun getItemViewType(position: Int): Int {
        return when {
            //1 элемент - это наш заголовок
            position == 0 -> R.layout.activity_recycler_item_header
            else -> R.layout.activity_recycler_item_note
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        dataRecycler.removeAt(fromPosition).apply {
            dataRecycler.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        dataRecycler.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * метод для внесения изменений в наш RecyclerView. Этот метод будет
    //использовать DiffUtil для вычисления разницы (calculateDiff) между старой коллекцией данных и новой(newItems),
    //а также для  применения этих изменений в адаптере (dispatchUpdatesTo(adapter)).
     */
    fun setItems(newItems: List<Pair<DataRecycler, Boolean>>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(dataRecycler, newItems))
        dataRecycler.clear()
        dataRecycler.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }

    //метод для добавления элементов в конец списка
    fun appendItem() {
        dataRecycler.add(generateItem())
        notifyItemInserted(itemCount - 1)
    }

    //метод генерации нового элемента
    private fun generateItem() = Pair(DataRecycler(1, "Note", ""), false)

    //DiffUtilCallback помогает нам понять изменились наши данные в списке или нет

    //Этот класс призван вычислять разницу между двумя массивами (списками) и возвращать набор операций,
    // которые нужно произвести, чтобы превратить старый список в новый.
    // То есть дать нам возможность точечно обработать изменения с анимациями и
    //остальной красотой, которой невозможно добиться методом notifyDataSetChanged.
    inner class DiffUtilCallback(
        private var oldItems: List<Pair<DataRecycler, Boolean>>,
        private var newItems: List<Pair<DataRecycler, Boolean>>
    ) : DiffUtil.Callback() {

        //возвращает размер старого списка
        override fun getOldListSize(): Int = oldItems.size

        //возвращает размер нового списка
        override fun getNewListSize(): Int = newItems.size

        //должен возвращать true, если элементы списка одинаковые(проверяем по id)
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int):
            Boolean =
            oldItems[oldItemPosition].first.id == newItems[newItemPosition].first.id

        //вызывается, только если areItemsTheSame вернул true. Это дополнительная
        //проверка, которая сравнивает переменные класса (элемента списка) между собой по
        //аналогии с equals, чтобы выяснить, изменились ли данные внутри (проверяет само наполнение холдера)
        //В нашем случае мы проверяем по названию someText(Mars, Earth, Upiter)
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int):
            Boolean = oldItems[oldItemPosition].first.someText ==
            newItems[newItemPosition].first.someText

        //вызывается, только если areContentsTheSame возвращает false. То есть старый и новый
        //элементы списка имеют одинаковый id, но содержат разные данные
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int):
            Any {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return Change(
                oldItem,
                newItem
            )
        }
    }

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view), ItemTouchHelperViewHolder {

        private val binding = ActivityRecyclerItemNoteBinding.bind(view)

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: Pair<DataRecycler, Boolean>) {

            binding.noteImageView.setOnClickListener {
                onListItemClickListener.onItemClick(item.first)
            }
            binding.addItemImageView.setOnClickListener { addItem() }
            binding.removeItemImageView.setOnClickListener { removeItem() }
            binding.moveItemDown.setOnClickListener { moveDown() }
            binding.moveItemUp.setOnClickListener { moveUp() }
            binding.noteDescriptionTextView.setOnClickListener { toggleText() }
            //По нажатию на основной текст мы изменяем данные в элементе массива и обновляем конкретный
            //элемент через метод toggleText, который пересоздаёт этот элемент, вызывая метод bind.
            binding.noteSomeTextView.setOnClickListener { toggleText() }
            //наш бургер, за который мы перетаскиваем элемент
            binding.dragHandleImageView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
        }

        /**
         * notifyDataSetChanged() обновляет список полностью, хотя нам нужно всего лишь добавить или удалить один элемент,
         * весь остальной список не меняется. Помимо того, что весь список пересоздаётся, RecyclerView не может применить анимации
         * вставки и удаления, потому что заменяются все элементы: даже те, которые не изменились.
         * Поэтому используем методы notifyItemInserted() и notifyItemRemoved() вместо notifyDataSetChanged():
         */
        private fun addItem() {
            dataRecycler.add(layoutPosition, generateItem())
            //этим методом мы уведомляем адаптер, что на данной позиции layoutPosition добавился элемент
            notifyItemInserted(layoutPosition)
        }

        //удалить элемент
        private fun removeItem() {
            dataRecycler.removeAt(layoutPosition)
            notifyItemRemoved(layoutPosition)
        }

        //поднять элемент
        private fun moveUp() {
            //Обратите внимание на предикат it > 1. Мы специально оставляем первый элемент
            //списка нетронутым, потому что это header и он всегда должен оставаться наверху.
            layoutPosition.takeIf { it > 1 }?.also { currentPosition ->
                //удаляем элемент на текущей и позиции и добавляем его на позицию выше
                dataRecycler.removeAt(currentPosition).apply {
                    dataRecycler.add(currentPosition - 1, this)
                }
                //уведомляем адаптер об изменении: текущая и желаемая позиция элемента
                notifyItemMoved(currentPosition, currentPosition - 1)
            }
        }

        //перетянуть вниз элемент
        private fun moveDown() {
            layoutPosition.takeIf { it < dataRecycler.size - 1 }?.also { currentPosition ->
                dataRecycler.removeAt(currentPosition).apply {
                    dataRecycler.add(currentPosition + 1, this)
                }
                notifyItemMoved(currentPosition, currentPosition + 1)
            }
        }

        //По нажатию на основной текст ЗАМЕТКА мы изменяем данные в элементе массива и обновляем конкретный
        //элемент через метод notifyItemChanged(), который пересоздаёт этот элемент, вызывая метод bind
        private fun toggleText() {
            dataRecycler[layoutPosition] = dataRecycler[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition)
        }

        //эти 2 метода имплементировали для перетягивания и смахивания элементов
        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Pair<DataRecycler, Boolean>) {
            itemView.setOnClickListener {
                onListItemClickListener.onItemClick(item.first)
            }
        }
    }

    // слушатель нажатий на элемент списка.
    // Это  простой интерфейс, который возвращает данные нажатого элемента
    interface OnListItemClickListener {
        fun onItemClick(dataItemClick: DataRecycler)
    }
}

data class DataRecycler(
    val id: Int = 0,
    //основной текст холдера(Заметка)
    val someText: String = "Text",
    val someDescription: String? = "Description"
)



