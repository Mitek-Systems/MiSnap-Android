package com.miteksystems.misnap.examples.science

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.MibiData
import com.miteksystems.misnap.core.Mrz1Line
import com.miteksystems.misnap.nfc.NfcReader

private class NfcRead: Fragment() {
    private val license = "your_sdk_license"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val misnapSettings = MiSnapSettings(MiSnapSettings.UseCase.NFC, license).apply {
            nfc.mrz = Mrz1Line("30-digit MRZ")
            nfc.soundAndVibration = MiSnapSettings.Nfc.SoundAndVibration.FOLLOW_SYSTEM
            nfc.advanced.docType = MiSnapSettings.Nfc.Advanced.DocType.EU_DL
        }

        // Optionally define a Mibi session outside of the reader's lifecycle
        MibiData.startSession(this::class.java.name, misnapSettings)

         val nfcReader = NfcReader(requireContext())

        nfcReader.events.observe(viewLifecycleOwner) {}
        nfcReader.completedEvent.observe(viewLifecycleOwner) {}
        nfcReader.errorEvents.observe(viewLifecycleOwner) {}

        nfcReader.start(requireActivity(), misnapSettings)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Optionally end the Mibi session outside of the reader's lifecycle
        MibiData.releaseSession(this::class.java.name)
    }
}
