package com.pampang.nav.screens.buyer

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.pampang.nav.R
import com.pampang.nav.MapNav.FirstFishLoc
import com.pampang.nav.MapNav.Gulayloc
import com.pampang.nav.MapNav.MeatMapLoc
import com.pampang.nav.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuyerMainFragment : Fragment() {

    private lateinit var mBinding: FragmentMainBinding
    private var isMenuOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        mBinding.lifecycleOwner = this

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initConfig()
    }

    override fun onResume() {
        super.onResume()
        initRequest()
    }

    private fun initConfig() {
        initExtras()
        initEventListener()
    }

    private fun initExtras() {
    }

    private fun initEventListener() {
        mBinding.apply {
            fabMain.setOnClickListener {
                if (isMenuOpen) {
                    fabMain.setImageResource(R.drawable.ic_add)
                    fabMenuLayout.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            fabMenuLayout.visibility = View.GONE
                        }
                        .start()
                } else {
                    fabMenuLayout.visibility = View.VISIBLE
                    fabMenuLayout.animate().alpha(1f).setDuration(200).start()
                    fabMain.setImageResource(R.drawable.ic_close)
                }
                isMenuOpen = !isMenuOpen
            }

            fabFish.setOnClickListener {
                val intent = Intent(requireContext(), FirstFishLoc::class.java)
                val options = ActivityOptions.makeCustomAnimation(
                    requireContext(),
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                startActivity(intent, options.toBundle())
            }

            fabVeggies.setOnClickListener {
                val intent = Intent(requireContext(), Gulayloc::class.java)
                 val options = ActivityOptions.makeCustomAnimation(
                    requireContext(),
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                startActivity(intent, options.toBundle())
            }
            fabMeat.setOnClickListener {
                val intent = Intent(requireContext(), MeatMapLoc::class.java)
                val options = ActivityOptions.makeCustomAnimation(
                    requireContext(),
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                startActivity(intent, options.toBundle())
            }

        }
    }

    private fun initRequest() {

    }

}