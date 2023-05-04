package com.prashant.task

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.prashant.task.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private var binding = _binding
    private lateinit var navController: NavController

    companion object {
        lateinit var activity: WeakReference<MainActivity>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        activity = WeakReference(this)
        setContentView(binding?.root)
        initNavController()
        clicks()
    }

    private fun clicks() {
        binding?.ivBack?.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activity.clear()
    }

    private fun initNavController() {
        val fragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val navHostFragment = fragment as NavHostFragment
        navController = navHostFragment.navController
        val nav = navController.currentBackStackEntryFlow
        lifecycleScope.launch {
            binding?.let { bind ->
                with(bind) {
                    nav.collectLatest {
                        tvLabel.text = it.destination.label
                        ivBack.isVisible = it.destination.label != "Home"
                    }
                }

            }
        }
    }
}