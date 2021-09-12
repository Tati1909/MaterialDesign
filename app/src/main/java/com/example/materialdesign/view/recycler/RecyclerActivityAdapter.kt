package com.example.materialdesign.view.recycler

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.materialdesign.R

class RecyclerActivityAdapter(
    private var onListItemClickListener: OnListItemClickListener,
    private var dataRecycler: MutableList<Pair<DataRecycler, Boolean>>,
    //В адаптер передаём ещё и слушатель нажатия на рукоятку:
    // как только пользователь тянет за неё, мы вызываем метод startDrag(),
    // всю анимацию делают уже за нас.
    private val dragListener: OnStartDragListener
) : RecyclerView.Adapter<BaseViewHolder>(), ItemTouchHelperAdapter {

    //viewType получаем из метода getItemViewType
    //у нас 3 ViewHolder, но все они наследуются от BaseViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_EARTH -> EarthViewHolder(
                inflater.inflate(
                    R.layout.activity_recycler_item_earth, parent, false
                ) as View
            )
            TYPE_MARS -> MarsViewHolder(
                inflater.inflate(
                    R.layout.activity_recycler_item_mars, parent, false
                ) as View
            )
            else -> HeaderViewHolder(
                inflater.inflate(
                    R.layout.activity_recycler_item_header, parent, false
                ) as View
            )
        }
    }

    //с помощью позиции элемента можно узнать
    //его viewType и отобразить во ViewHolder соответствующие данные
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(dataRecycler[position])
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else {
            if (payloads.any { it is Pair<*, *> })
                holder.itemView.findViewById<TextView>(R.id.marsTextView).text = dataRecycler[position].first.someText
        }
    }

    override fun getItemCount(): Int {
        return dataRecycler.size
    }

    //сначала мы получаем тип ViewType  и только потом вызывается onCreateViewHolder
    //метод, благодаря которому мы можем всегда знать, какого типа элемент нам нужно отобразить в списке
    //1 - TYPE_HEADER, 2 - TYPE_MARS, 3 - TYPE_EARTH
    override fun getItemViewType(position: Int): Int {
        return when {
            //1 элемент - это наш заголовок
            position == 0 -> TYPE_HEADER

            //если описания нет или оно пустое, значит, элемент относится к типу «Марс»
            dataRecycler[position].first.someDescription.isNullOrBlank() -> TYPE_MARS

            else -> TYPE_EARTH
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

    fun setItems(newItems: List<Pair<DataRecycler, Boolean>>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(dataRecycler, newItems))
        result.dispatchUpdatesTo(this)
        dataRecycler.clear()
        dataRecycler.addAll(newItems)
    }

    //метод для добавления элементов в конец списка
    fun appendItem() {
        dataRecycler.add(generateItem())
        notifyItemInserted(itemCount - 1)
    }

    //метод генерации нового элемента
    private fun generateItem() = Pair(DataRecycler(1,"Mars", ""), false)

    inner class DiffUtilCallback(
        private var oldItems: List<Pair<DataRecycler, Boolean>>,
        private var newItems: List<Pair<DataRecycler, Boolean>>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int):
                Boolean =
            oldItems[oldItemPosition].first.id == newItems[newItemPosition].first.id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int):
                Boolean =
            oldItems[oldItemPosition].first.someText ==
                    newItems[newItemPosition].first.someText
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int):
                Any? {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return Change(
                oldItem,
                newItem
            )
        }
    }

    inner class EarthViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(dataItem: Pair<DataRecycler, Boolean>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<TextView>(R.id.descriptionTextView).text = dataItem.first.someDescription
                itemView.findViewById<ImageView>(R.id.wikiImageView).setOnClickListener {
                    onListItemClickListener.onItemClick(dataItem.first)
                }
            }
        }
    }

    inner class MarsViewHolder(view: View) : BaseViewHolder(view), ItemTouchHelperViewHolder {

        override fun bind(dataItem: Pair<DataRecycler, Boolean>) {

            itemView.findViewById<ImageView>(R.id.marsImageView).setOnClickListener {
                onListItemClickListener.onItemClick(dataItem.first)
            }
            itemView.findViewById<ImageView>(R.id.addItemImageView).setOnClickListener { addItem() }
            itemView.findViewById<ImageView>(R.id.removeItemImageView).setOnClickListener { removeItem() }
            itemView.findViewById<ImageView>(R.id.moveItemDown).setOnClickListener { moveDown() }
            itemView.findViewById<ImageView>(R.id.moveItemUp).setOnClickListener { moveUp() }
            itemView.findViewById<TextView>(R.id.marsDescriptionTextView).visibility =
                if (dataItem.second) View.VISIBLE else View.GONE
                    //По нажатию на основной текст (MARS) мы изменяем данные в элементе массива и обновляем конкретный
            //элемент через метод toggleText, который пересоздаёт этот элемент, вызывая метод bind.
            itemView.findViewById<TextView>(R.id.marsTextView).setOnClickListener { toggleText() }
            itemView.findViewById<ImageView>(R.id.dragHandleImageView).setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(this)
                }
                false
            }
        }

        //добавить элемент
        //notifyDataSetChanged() обновляет список
        //полностью, хотя нам нужно всего лишь добавить или удалить один элемент, весь остальной список не
        //меняется. Помимо того, что весь список пересоздаётся, RecyclerView не может применить анимации
        //вставки и удаления, потому что заменяются все элементы: даже те, которые не изменились.
        // !!!! Поэтому используем методы notifyItemInserted() и notifyItemRemoved() вместо notifyDataSetChanged():
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

        //По нажатию на основной текст МАРС мы изменяем данные в элементе массива и обновляем конкретный
        //элемент через метод notifyItemChanged(), который пересоздаёт этот элемент, вызывая метод bind
        //В процессе обновления или первичной загрузки устанавливаем видимость дополнительного
        //текста в зависимости от параметра, который лежит в Pair.
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

    inner class HeaderViewHolder(view: View) : BaseViewHolder(view) {

        override fun bind(dataItem: Pair<DataRecycler, Boolean>) {
            itemView.setOnClickListener {
                 onListItemClickListener.onItemClick(dataItem.first)
                //data[1] = Pair(Data("Jupiter", ""), false)
               // notifyItemChanged(1, Pair(Data("", ""), false))
            }
        }
    }

    // слушатель нажатий на элемент списка.
    // Это  простой интерфейс, который возвращает данные нажатого элемента
    interface OnListItemClickListener {
        fun onItemClick(dataItemClick: DataRecycler)
    }



//константы для разных типов элементов в списке
    companion object {
        private const val TYPE_EARTH = 0
        private const val TYPE_MARS = 1
        private const val TYPE_HEADER = 2
    }
}

data class DataRecycler(
    val id: Int = 0,
    val someText: String = "Text",
    val someDescription: String? = "Description"
)



