package com.prashant.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.prashant.task.BR

/**

 *A generic [RecyclerView.Adapter] that can be used to bind a list of [AbstractModel] objects
 *with a RecyclerView by using data binding.
 *@param layoutId The layout resource id for the item view
 *@param T A subclass of [AbstractModel] that represents the type of the items in the list
 */
class RecyclerAdapter<T :AbstractModel>(
    @LayoutRes val layoutId: Int,
) :RecyclerView.Adapter<RecyclerAdapter.VH<T>>() {

    private val items = ArrayList<T>()
    private var inflater: LayoutInflater? = null
    private var onItemClick: OnItemClick? = null

    /**

     *Returns the item at the specified position in the list.
     *@param position The position of the item in the list
     *@return The item at the specified position
     */
    fun getItemAt(position: Int) = items[position]
    fun getAllItem() = items

    /**
     *Clears the existing items in the list and adds the new items to it.
     *@param items The new items to be added to the list
     */
    fun addItems(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
    fun addMoreItems(items: List<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(items: T) {
        this.items.add(items)
        notifyItemChanged(itemCount - 1)
    }

    /**

     *Sets the callback for item click events.
     *@param onItemClick The callback to be invoked when an item in the list is clicked
     */
    fun setOnItemClick(onItemClick: OnItemClick?) {
        this.onItemClick = onItemClick
    }

    /**
     *Returns all the items in the list.
     *@return A list of all the items in the list
     */
    fun getAllItems() = items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<T> {
        // Inflate the layout for the item view
        val layoutInflater = inflater ?: LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, layoutId, parent, false
        )
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH<T>, position: Int) {
        // Disable view recycling to prevent glitches with data binding
        holder.setIsRecyclable(false)

        // Get the item model at the current position and set its view holder, position, and length properties
        val model = items[position]
        model.viewHolder = holder
        model.length = itemCount
        model.position = position

        // Set the on-click listener for the item view
        onItemClick?.let { model.onItemClick = it }

        // Bind the item model to the view
        holder.bind(model)
    }

    /**
     *The view holder class that is responsible for binding an item model with its view.
     *@param binding The view data binding for the item view
     */
    class VH<T :AbstractModel>(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         *Binds the specified item model with its view.
         *@param model The item model to be bound with the view
         */
        fun bind(model: T) {
            // Set the variable with the item model and execute any pending bindings
            binding.setVariable(BR.model, model)
            binding.executePendingBindings()
        }
    }

    /**
     *The callback interface for item click events.
     */
    fun interface OnItemClick {
        /**
         *Invoked when an item in the list is clicked.
         *@param view The item view that was clicked
         *@param viewHolder The view holder for the item view that was clicked
         *@param position The position of the item in the list that was*/
        fun onClick(view: View, viewHolder: RecyclerView.ViewHolder?, position: Int)
    }
}