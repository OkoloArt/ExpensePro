package com.example.ext.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ext.R
import com.example.ext.databinding.FragmentSplashBinding
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val splashBottomAnimation: Animation by inject(named("SplashBottom"))
    private val splashTopAnimation: Animation by inject(named("SplashTop"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            splashFragment = this@SplashFragment
        }
        setAnimation()
        Handler().postDelayed({
           goToNextScreen()
        }, 3000)

    }

    private fun goToNextScreen(){
        findNavController().navigate(R.id.action_splashFragment_to_expenseListFragment)
    }

    private fun setAnimation() {
        binding.apply {
            splashImage.startAnimation(splashTopAnimation)
            introLayout.startAnimation(splashBottomAnimation)
        }
    }
}
