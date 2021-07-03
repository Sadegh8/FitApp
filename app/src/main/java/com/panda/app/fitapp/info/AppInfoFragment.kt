package com.panda.app.fitapp.info


import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.panda.app.fitapp.R
import com.panda.app.fitapp.databinding.FragmentAppInfoBinding


/**
 * A simple [Fragment] subclass.
 */
class AppInfoFragment : Fragment() {
    private lateinit var binding: FragmentAppInfoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_app_info, container, false
        )

        try {
            val pInfo: PackageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            val version = pInfo.versionName

            binding.version.text = version.toString()

        } catch (e: PackageManager.NameNotFoundException) {

        }

        binding.bubble.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ECLaboratorio/BubbleShowCase-Android"))
            startActivity(i)
        }

        binding.imgInfo.setOnClickListener {
            //redirect user to app Settings
            //redirect user to app Settings
            val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            i.addCategory(Intent.CATEGORY_DEFAULT)
            i.data = Uri.parse("package:" + requireActivity().packageName)
            startActivity(i)
        }

        binding.feedback.setOnClickListener {

            val send = Intent(Intent.ACTION_SENDTO)
            val uriText = "mailto:" + Uri.encode("panda.app.official@gmail.com").toString() +
                    "?subject=" + Uri.encode("Feedback from Get fit play store.").toString() +
                    "&body=" + Uri.encode(getDeviceInformation("App"))
            val uri: Uri = Uri.parse(uriText)

            send.data = uri
            startActivity(Intent.createChooser(send, "Send feedback"))

        }

        binding.lottie.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://airbnb.io/lottie/#/README"))
            startActivity(i)
        }

        binding.flaticon.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flaticon.com/"))
            startActivity(i)
        }

        binding.lottiefiles.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://lottiefiles.com/"))
            startActivity(i)
        }

        return binding.root
    }

    private fun getDeviceInformation(appVersion: String): String {
        return "\n\n" + Build.MODEL + " " + "/" + Build.VERSION.RELEASE + "/" + appVersion
    }


}
