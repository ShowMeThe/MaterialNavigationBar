package com.show.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    }

}