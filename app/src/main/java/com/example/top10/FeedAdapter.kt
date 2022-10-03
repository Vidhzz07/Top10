package com.example.top10

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item.view.*

class ViewHolder(v:View)
{
    val tvName:TextView=v.findViewById(R.id.tvName)
    val tvArtist:TextView=v.findViewById(R.id.tvArtist)
    val imImage:ImageView=v.findViewById(R.id.ivImage)

}

 class FeedAdapter(context:Context, val resource:Int, val applications:List<FeedEntry>):ArrayAdapter<FeedEntry>(context,resource) {
    val tag="FeedAdapter"
    val inflater=LayoutInflater.from(context)



    override fun getCount(): Int {
        return applications.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view:View
        val viewHolder:ViewHolder
        if(convertView==null)
        {
            view=inflater.inflate(R.layout.list_item,null,false)
            viewHolder= ViewHolder(view)
            view.tag=viewHolder

        }

        else
        {
            view=convertView
            viewHolder=view.tag as ViewHolder

        }

        var currentApp=applications[position]

        view.tvName.text=currentApp.name
        view.tvArtist.text=currentApp.artist
        Glide.with(context).load(currentApp.imageURL).into(view.ivImage)





        return  view
    }
}