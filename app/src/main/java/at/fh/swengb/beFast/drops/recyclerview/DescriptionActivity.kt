package at.fh.swengb.beFast.drops.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_description.*
import java.text.SimpleDateFormat
import java.util.*
import at.fh.swengb.beFast.R
import at.fh.swengb.beFast.drops.DropsFragment.Companion.EXTRA_DROP_ID


class DescriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_description)

        val dropId = intent.getStringExtra(EXTRA_DROP_ID)

        if (dropId != null) {
            description_brand.text = DropsRepository.dropById(dropId)?.brand ?: getString(R.string.brand_error) //Dynamic language
            description_name.text = DropsRepository.dropById(dropId)?.name ?: getString(R.string.name_error)
            description_price.text = DropsRepository.dropById(dropId)?.price ?: getString(R.string.price_error)

        }
        val brand = dropId?.let { DropsRepository.dropById(it)?.brand } ?: getString(R.string.brand_error)
        val name = dropId?.let { DropsRepository.dropById(it)?.name } ?: getString(R.string.name_error)
        val datetime = dropId?.let { DropsRepository.dropById(it)?.datetime } ?: getString(R.string.date_error)
        val homepageUrl = dropId?.let { DropsRepository.dropById(it)?.homepageUrl } ?: getString(R.string.hompageURL_error)



        // share button intent
        description_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "$brand $name Drop by beFast")
                type = "text/plain" //todo send the link not text
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        //reminder button intent
        description_reminder.setOnClickListener {
            val sdf = SimpleDateFormat("dd.MM.yyyy z HH:mm", Locale.ENGLISH)
            val formattedDate = sdf.parse(datetime)
            sdf.timeZone = TimeZone.getDefault()

            val calendarIntent = Intent(Intent.ACTION_EDIT)
            calendarIntent.type = "vnd.android.cursor.item/event"
            calendarIntent.putExtra("beginTime", formattedDate!!.time - 600000)
            calendarIntent.putExtra("endTime", formattedDate.time)
            calendarIntent.putExtra("title", "$brand $name Drop")
            calendarIntent.putExtra("description","Drop Reminder beFast App")
            startActivity(calendarIntent)
        }

        //open homepage button intent
        description_homepage.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(homepageUrl)
            startActivity(intent)
        }


        // back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}