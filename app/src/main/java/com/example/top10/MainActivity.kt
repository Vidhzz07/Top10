package com.example.top10

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem


import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*

import java.net.URL

class FeedEntry{
    var name:String=""
    var artist:String=""
    var releaseDate:String=""
    var summary:String=""
    var imageURL:String=""
    override fun toString(): String {

        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            imageURL = $imageURL
           
        """.trimIndent()

    }
}
class MainActivity : AppCompatActivity() {

    private var downloadData :DownloadData?=null
    private var feedUrl:String="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit=10

    private var feedCachedUrl:String="Invalid"
    private var StateUrl:String="feedUrl"
    private var StateLimit:String="feedLimit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState !=null)
        {
            feedUrl=savedInstanceState.getString(StateUrl).toString()
            feedLimit=savedInstanceState.getInt(StateLimit)
        }

        download(feedUrl.format(feedLimit))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feed_menu,menu)



        if(feedLimit ==10)
        {
            menu?.findItem(R.id.mn10)?.isChecked=true
        }
        else
        {
            menu?.findItem(R.id.mn25)?.isChecked=true

        }

        return  true
    }

    private  fun download(feedUrl:String)
    {

        if(feedUrl!=feedCachedUrl)
        {
            downloadData= DownloadData(this,listView)
            downloadData?.execute(feedUrl)
            feedCachedUrl=feedUrl

        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {



        when(item.itemId)
        {
            R.id.mnFree-> feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnPaid-> feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnSongs-> feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mn10,R.id.mn25 ->{
                if(!item.isChecked) {
                    item.isChecked = true

                    feedLimit = 35 - feedLimit
                }


            }

            R.id.mnRefr-> feedCachedUrl="Invalid"

            else-> return super.onOptionsItemSelected(item)
        }


        download(feedUrl.format(feedLimit))
        return  true

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(StateUrl,feedUrl)
        outState.putInt(StateLimit,feedLimit)
    }

    override fun onDestroy() {
        super.onDestroy()

        downloadData?.cancel(true)
    }

    companion object {
        private class DownloadData(context: Context,listView:ListView): AsyncTask<String, Void, String>()
        {
            var context:Context=context
            var listView:ListView=listView
            override fun doInBackground(vararg url: String?): String {

                val rssFeed=downloadXML(url[0])

                if(rssFeed.isEmpty())
                {
                    Log.e("MainActivity", "doInBackground fail -download Failed")
                }
                return rssFeed


            }

            override fun onPostExecute(result: String) {

                super.onPostExecute(result)
                val parseApplications =ParseApplications()
                parseApplications.parse(result)

                val adapter=FeedAdapter(context,R.layout.list_item,parseApplications.applications)
                listView.adapter=adapter
            }

            private fun downloadXML(urlPath:String?):String {

                return URL(urlPath).readText()

            }

        }
    }


}