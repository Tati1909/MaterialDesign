package com.example.materialdesign.view.recycler

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

//Это интерфейс, который позволяет нам подписаться на события, связанные с перетаскиванием и
//смахиванием элементов списка.
//Чтобы использовать ItemTouchHelper, нужно написать класс-наследник от ItemTouchHelper.Callback.
class ItemTouchHelperCallback(private val adapter: RecyclerActivityAdapter) :
    ItemTouchHelper.Callback() {
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    //этот callback позволяет определить
    //направления перетаскивания и свайпа. Для перетаскивания мы используем направления
    //вверх/вниз, а для смахивания — влево/вправо. Если у нас горизонтальный scroll, то
    //направления будут другими. Или мы можем определить смахивание только вправо.
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(
            dragFlags,
            swipeFlags
        )
    }

    //onMove(RecyclerView, ViewHolder, ViewHolder) и onSwiped(ViewHolder, int)
    //нужны для оповещения нашего адаптера, что некий элемент изменил положение или был
    //удалён, чтобы адаптер мог корректно обработать эти события.
    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(source.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    //onSelectedChange и clearView нужны, чтобы наш ViewHolder корректно обрабатывал
    //выделение элемента.
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
        itemViewHolder.onItemClear()
    }

    //К сведению №2: ItemTouchHelper.Callback наследуется от RecyclerView.ItemDecoration, а
    //значит, мы можем переопределять анимацию свайпа самостоятельно. Например, в нашем классе
    //ItemTouchHelperCallback можно переопределить метод onChildDraw и менять прозрачность
    //элемента. Чем дальше мы его смахиваем, тем прозрачней он становится:
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        //Аргументы dX и dY отображают направление смахивания:
        //● -1.0f — полностью смахнули справа налево;
        //● 1.0f — полностью смахнули слева направо.
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val width = viewHolder.itemView.width.toFloat()
            val alpha = 1.0f - StrictMath.abs(dX) / width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(
                c, recyclerView, viewHolder, dX, dY,
                actionState, isCurrentlyActive
            )
        }
    }
}

//onItemMove будет вызываться, когда элемент списка будет перетянут
//на достаточное расстояние, чтобы запустить анимацию перемещения. onItemDismiss будет
//вызываться во время свайпа по элементу.
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
}

//  onItemSelected будет вызываться в процессе смахивания или перетаскивания элемента,
//  onItemClear — когда этот процесс закончится.
interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemClear()
}

//интерфейс, сообщает нам, что пользователь намерен переместить элемент
interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}