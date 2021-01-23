package at.fh.swengb.beFast.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import at.fh.swengb.beFast.R
import at.fh.swengb.beFast.api.TwitterApi
import at.fh.swengb.beFast.models.tweets.TweetsItem
import at.fh.swengb.beFast.news.recyclerview.TweetAdapter
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.fragment_news.*


class NewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var tweetAdapter: TweetAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        val textView: TextView = root.findViewById(R.id.text_news)
        newsViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()


    }
    private fun init() {
        SleepyAsyncTask()
        tweetAdapter = TweetAdapter {
            val url = it.entities.urls[0].url
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        TwitterApi.tweetList(
                success = {
                    // handle success
                    tweetAdapter.updateList(it)
                },
                error = {
                    // handle error
                    Log.e("Error", "Repository Error")
                    if (activity != null) {           //TODO: App crashes if started on AirplaneMode on lukis phone news Fragment has no corresponding activity
                        Toast
                                .makeText(activity, getString(R.string.internet_connection), Toast.LENGTH_LONG)
                                .show()
                    }
                }
        )
        parseJson()

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        fragment_recycler_view_news.layoutManager = layoutManager
        fragment_recycler_view_news.adapter = tweetAdapter
    }
    private fun parseJson() {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<TweetsItem>(TweetsItem::class.java)
        val tweet = jsonAdapter.fromJson("""
       {
        "created_at": "Wed Dec 23 17:32:00 +0000 2020",

        "text": "Ad: Women's Nike Air Max 97 'Metallic Red Bronze' on sale for only ${'$'}77.97 + FREE shipping =&gt; https://t.co/AIHwaY72Sp https://t.co/u1FgLUmEdW",

        "entities": {

            "urls": [
                {
                    "url": "https://t.co/AIHwaY72Sp"

                },
                {
                    "url": "https://t.co/tHqagVcBma"
                 }
            ],
            "media": [
                {

                    "media_url_https": "https://pbs.twimg.com/media/Ep70mToVgAEGDIR.jpg"
                }
   
 
            ]
        }
    
    }   
        """.trimIndent())
    }
    fun refresh(){
        val ftr: FragmentTransaction = requireFragmentManager().beginTransaction()
        ftr.detach(this@NewsFragment).attach(this@NewsFragment).commit()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_nav_menu_news, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }
    //function to start the

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.refresh -> consume { refresh() }
            else -> super.onOptionsItemSelected(item)
        }
    }

}