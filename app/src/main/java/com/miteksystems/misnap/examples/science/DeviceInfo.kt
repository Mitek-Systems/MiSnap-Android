package com.miteksystems.misnap.examples.science

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.apputil.LicenseFetcher
import com.miteksystems.misnap.core.DeviceInfoResult
import com.miteksystems.misnap.core.DeviceInfoUtil

/**
 * This example demonstrates how to use the [DeviceInfoUtil] class to retrieve device metadata that
 * can be used to bind a device with a biometric in Mitek server products.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 */
class DeviceInfo : Fragment() {

    /**
     * Fetch the Misnap SDK license.
     * Good practice: Handle the license in a way that it is remotely updatable.
     */
    private val license by lazy {  
        LicenseFetcher.fetch()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deviceInfoResult: DeviceInfoResult =
            DeviceInfoUtil.getDeviceInfo(requireContext(), license)

        if (deviceInfoResult is DeviceInfoResult.Success) {
            /**
             * Recover the device metadata and send it to Mitek server products for binding a device
             * to a biometric.
             */
            val deviceMetadata = deviceInfoResult.deviceInfo
        } else {
            when (deviceInfoResult) {
                is DeviceInfoResult.Failure.Execution -> {

                }
                is DeviceInfoResult.Failure.LibraryLoad -> {

                }
                is DeviceInfoResult.Failure.License -> {

                }
                else -> {

                }
            }
        }
    }
}
