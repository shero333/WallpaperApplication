package layout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wallpaperapplication.R

class ListLoaderViewAdapter : LoadStateAdapter<ListLoaderViewAdapter.LoaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.loader_view,parent,false)

        return LoaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }


    class LoaderViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){

        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressbar)

        fun bind(loadState: LoadState){
            progressBar.isVisible = loadState is LoadState.Loading
        }
    }

}