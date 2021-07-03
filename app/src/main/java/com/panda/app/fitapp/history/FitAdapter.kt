package com.panda.app.fitapp.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.panda.app.fitapp.database.FitHistory
import com.panda.app.fitapp.databinding.ListItemFitBinding

class FitAdapter(private val clickListener:FitHistoryListener, private val longClickListener:FitHistoryLongListener): ListAdapter<FitHistory, FitAdapter.ViewHolder>(FitDiffCallback()){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
          holder.bind(getItem(position)!!, clickListener)
          holder.itemHistory.setOnLongClickListener {
              longClickListener.onLongClick(item)
           true
          }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor (val binding: ListItemFitBinding): RecyclerView.ViewHolder(binding.root){
        val pushCount: TextView = binding.pushCount

        val date: TextView = binding.date

        val txtPercents : TextView = binding.txtPrcent


        val progress: ProgressBar = binding.progressBar

        val itemHistory: ConstraintLayout = binding.itemHistory



        fun bind(
            item: FitHistory,
            clickListener: FitHistoryListener) {
          binding.fit = item
          binding.clickListener = clickListener
          binding.executePendingBindings()
        }


        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemFitBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class FitDiffCallback : DiffUtil.ItemCallback<FitHistory>(){
    override fun areItemsTheSame(oldItem: FitHistory, newItem: FitHistory): Boolean {
        return oldItem.fitID == newItem.fitID
    }

    override fun areContentsTheSame(oldItem: FitHistory, newItem: FitHistory): Boolean {
        return  oldItem == newItem
    }

}

class FitHistoryListener(val clickListener:(fitId: Long) -> Unit){
    fun onClick(fit: FitHistory) =  clickListener(fit.fitID)
}

class FitHistoryLongListener(val longClickListener:(fitId: Long) -> Unit){
    fun onLongClick(fit: FitHistory) =  longClickListener(fit.fitID)
}
