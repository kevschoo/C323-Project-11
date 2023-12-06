package edu.iu.kevschoo.project_11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.iu.kevschoo.project_11.model.Property

class PropertyAdapter(
    private val properties: List<Property>,
    private val onItemClick: (Property) -> Unit
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView
     */
    class PropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view_item)
        val titleView: TextView = view.findViewById(R.id.text_view_title)
        val descriptionView: TextView = view.findViewById(R.id.text_view_description)
        val costView: TextView = view.findViewById(R.id.text_view_cost)
    }
    /**
     * Provides a new ViewHolder instance for the RecyclerView
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }
    /**
     * Binds the data at the specified position into the ViewHolder
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position
     * @param position The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = properties.getOrNull(position) ?: return
        holder.itemView.setOnClickListener { onItemClick(property) }
        holder.titleView.text = property.name
        holder.descriptionView.text = property.roominfo
        holder.costView.text = "$${property.cost}"

    }
    /**
     * Returns the total number of items in the data set held by the adapter
     *
     * @return The total number of items in this adapter
     */
    override fun getItemCount() = properties.size
}