package com.show.demo

import android.app.ActionBar
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.show.demo.databinding.ActivityMainBinding
import com.show.navigationbar.MaterialNavigationBar


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val drawable = ContextCompat.getDrawable(this, R.drawable.select_home)
        val drawable2 = ContextCompat.getDrawable(this, R.drawable.select_people)
        val drawable3 = ContextCompat.getDrawable(this, R.drawable.select_setting)

        binding.bar.addItem(
            listOf(
                MaterialNavigationBar.NavigationItem(drawable, "主页")
                    .setSelectedTintColor(ContextCompat.getColor(this, R.color.color_1)),
                MaterialNavigationBar.NavigationItem(drawable2, "个人")
                    .setSelectedTintColor(ContextCompat.getColor(this, R.color.color_2)),
                MaterialNavigationBar.NavigationItem(drawable3, "设置")
                    .setSelectedTintColor(ContextCompat.getColor(this, R.color.color_3))

            )
        )

        val colors = mutableListOf(
            ContextCompat.getColor(this, R.color.color_4),
            ContextCompat.getColor(this, R.color.color_5),
            ContextCompat.getColor(this, R.color.color_6),
        )
        val adapter = DemoAdapter(colors)
        binding.apply {
            vp.adapter = adapter
            vp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    bar.smoothToPosition(position)
                }
            })
        }
    }

}

class DemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class DemoAdapter(private val list: MutableList<Int>) : RecyclerView.Adapter<DemoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        return DemoViewHolder(View(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        })
    }

    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        holder.itemView.apply {
            setBackgroundColor(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

}