package com.thewhitewings.pouch.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.material.snackbar.Snackbar
import com.thewhitewings.pouch.PouchApp
import com.thewhitewings.pouch.R
import com.thewhitewings.pouch.databinding.ActivityMainBinding
import com.thewhitewings.pouch.ui.theme.PouchTheme
import com.thewhitewings.pouch.utils.Zone
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Executors

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<HomeViewModel> { HomeViewModel.Factory }
    private lateinit var currentZone: StateFlow<Zone>

    // Count of how many times the Box of mysteries reveal button has been pressed (knocked)
    private var bomKnocks = 0
    // Boolean of whether the timeout for revealing the Box of mysteries has started
    private var bomTimeoutStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PouchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PouchApp()
                }
            }
        }

//        showBtnRevealBom()
    }


    /**
     * Makes the reveal Box of mysteries button interactable.
     */
    private fun showBtnRevealBom() {
        Handler().postDelayed({ binding.btnRevealBom.visibility = View.VISIBLE }, 1500)
    }

    /**
     * Handles the logic for revealing the Box of mysteries.
     */
    private fun revealBoxOfMysteries() {
        bomKnocks++
        val handler = Handler(Looper.getMainLooper())

        if (!bomTimeoutStarted) {
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                val timeoutKnocking = (7 * 1000).toLong() // 7 seconds
                val startKnockingTime = System.currentTimeMillis()
                bomTimeoutStarted = true
                while (bomTimeoutStarted) {
                    val elapsedKnockingTime = System.currentTimeMillis() - startKnockingTime
                    if (elapsedKnockingTime >= timeoutKnocking) {
                        bomTimeoutStarted = false
                        bomKnocks = 0
                        break
                    } else if (bomKnocks == 4) {
                        runOnUiThread { binding.btnRevealBom.setBackgroundResource(R.drawable.ripple_revealed_word) }
                    } else if (bomKnocks == 5) {
                        handler.postDelayed({
                            bomTimeoutStarted = false
                            bomKnocks = 0
                            viewModel.toggleZone()
                        }, 500)
                        break
                    }

                    synchronized(this) {
                        try {
                            (this as Object).wait(200)
                        } catch (e: InterruptedException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }
        }
    }

    /**
     * Makes the needed changes to the UI for navigating to the Box of mysteries zone.
     */
    private fun goToBoxOfMysteries() {
        binding.btnRevealBom.setBackgroundColor(Color.TRANSPARENT)
        binding.btnRevealBom.visibility = View.GONE

        modifyLogo()
        modifyZoneName()

        binding.lvRevealScreen.setAnimation(R.raw.reveal_screen_black)
        binding.lvRevealScreen.speed = 0.5f
        binding.lvRevealScreen.playAnimation()

        binding.lvRevealLoader.visibility = View.VISIBLE
        binding.lvRevealLoader.playAnimation()
        Handler(Looper.getMainLooper())
            .postDelayed(
                { binding.lvRevealLoader.visibility = View.GONE },
                2000
            )

        Snackbar.make(
            binding.activityMainRoot,
            getString(R.string.bom_revealing_message),
            Snackbar.LENGTH_LONG
        )
            .show()
    }

    /**
     * Makes the needed changes to the UI for navigating to the Creative zone.
     */
    private fun goToCreativeZone() {
        binding.btnRevealBom.visibility = View.VISIBLE

        modifyLogo()
        modifyZoneName()

        binding.lvRevealScreen.setAnimation(R.raw.reveal_screen_red)
        binding.lvRevealScreen.speed = 1f
        binding.lvRevealScreen.playAnimation()
    }

    /**
     * Modifies the logo image view for navigating to a different zone.
     */
    private fun modifyLogo() {
        val initialColor: Int
        val finalColor: Int

        /*
        note that the currentZone here represents the updated value which is the destination you just arrived
         */
        if (currentZone.value == Zone.BOX_OF_MYSTERIES) {
            initialColor = resources.getColor(R.color.md_theme_light_primaryContainer, null)
            finalColor = resources.getColor(R.color.gray_logo_bom, null)
        } else {
            initialColor = resources.getColor(R.color.gray_logo_bom, null)
            finalColor = resources.getColor(R.color.md_theme_light_primaryContainer, null)
        }

        val tintAnimator = ValueAnimator.ofArgb(initialColor, finalColor)
        tintAnimator.setDuration(3500)
        tintAnimator.addUpdateListener { animator: ValueAnimator ->
            val animatedValue = animator.animatedValue as Int
            binding.imgLogo.setColorFilter(animatedValue)
        }
        tintAnimator.start()
    }

    /**
     * Modifies the zone name text view for navigating to a different zone.
     */
    private fun modifyZoneName() {
        val txtZoneName = binding.txtZoneName
        val zoneName: String
        val typeface: Typeface
        val initialTextSize: Float
        val finalTextSize: Float
        val initialColor: Int
        val finalColor: Int

        /*
        note that the currentZone here represents the updated value which is the destination you just arrived
         */
        if (currentZone.value == Zone.BOX_OF_MYSTERIES) {
            zoneName = getString(R.string.box_of_mysteries)
            typeface = Typeface.create("cursive", Typeface.BOLD)
            initialTextSize = 26f
            finalTextSize = 32f
            initialColor = resources.getColor(R.color.md_theme_light_inversePrimary, null)
            finalColor = Color.BLACK
        } else {
            zoneName = getString(R.string.creative_zone)
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            initialTextSize = 32f
            finalTextSize = 26f
            initialColor = Color.BLACK
            finalColor = resources.getColor(R.color.md_theme_light_inversePrimary, null)
        }

        txtZoneName.text = zoneName

        txtZoneName.typeface = typeface

        val textSizeAnimator =
            ObjectAnimator.ofFloat(txtZoneName, "textSize", initialTextSize, finalTextSize)
        textSizeAnimator.setDuration(1000)
        textSizeAnimator.start()

        val colorAnimator = ValueAnimator.ofArgb(initialColor, finalColor)
        colorAnimator.setDuration(3500)
        colorAnimator.addUpdateListener { animator: ValueAnimator ->
            txtZoneName.setTextColor(
                animator.animatedValue as Int
            )
        }
        colorAnimator.start()
    }

}
