package com.miteksystems.misnap.examples.science

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.MibiData
import com.miteksystems.misnap.core.Mrz
import com.miteksystems.misnap.core.Mrz1Line
import com.miteksystems.misnap.nfc.NfcReader

/**
 * This example demonstrates a direct integration with MiSnap SDK's NFC reader, this type of integration
 * is best suited for developers that want to interface with the science directly and that will take
 * care of supplying the [Mrz] data context required to read the NFC chip themselves.
 */
private class NfcRead : Fragment() {
    private val license = "your_sdk_license"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Configure a [MiSnapSettings] object with the appropriate [Mrz] data that will be used as
         * context to unlock the NFC chip of the document.
         *
         * @see [Mrz] for the different types of data and requirements.
         */
        val misnapSettings = MiSnapSettings(MiSnapSettings.UseCase.NFC, license).apply {
            nfc.mrz = Mrz1Line("30-digit MRZ")
            nfc.soundAndVibration = MiSnapSettings.Nfc.SoundAndVibration.FOLLOW_SYSTEM
            nfc.advanced.docType = MiSnapSettings.Nfc.Advanced.DocType.EU_DL
        }

        /**
         * Optionally define a [MibiData] session outside of the reader's lifecycle.
         */
        MibiData.startSession(this::class.java.name, misnapSettings)

        /**
         * Instantiate an [NfcReader] and observe the different [LiveData] objects to receive
         * nfc, errors and completion events.
         */
        NfcReader(requireContext()).apply {
            /**
             * Observe the [NfcReader.events] [LiveData] to receive notifications about the progress
             * of the NFC chip reading process.
             *
             * @see [NfcReader.Event] for the full list of possible emitted events.
             */
            events.observe(viewLifecycleOwner) {

            }

            /**
             * Observe the [NfcReader.completedEvent] [LiveData] to receive an [NfcReader.Result]
             * indicating that the NFC read process has finished successfully.
             */
            completedEvent.observe(viewLifecycleOwner) {

            }

            /**
             * Observe the [NfcReader.errorEvents] [LiveData] to receive notifications about errors
             * found during the NFC chip reading process.
             *
             * @see [NfcReader.Error] for the full list of possible emitted events.
             */
            errorEvents.observe(viewLifecycleOwner) {

            }
        }.also {
            /**
             * Start the NFC reading process with the built [MiSnapSettings] object.
             */
            it.start(requireActivity(), misnapSettings)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        /**
         * Optionally end the [MibiData] session outside of the reader's lifecycle.
         */
        MibiData.releaseSession(this::class.java.name)
    }
}
