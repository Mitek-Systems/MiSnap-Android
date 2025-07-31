package com.miteksystems.misnap.examples.science

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.miteksystems.misnap.apputil.LicenseFetcher
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.MibiData
import com.miteksystems.misnap.voice.MiSnapVoiceProcessor

/**
 * This example demonstrates a direct integration with MiSnap SDK's Voice processor component,
 * this type of integration is best suited for developers that want to interface with the science
 * directly and that will take care of driving the user through the acquisition of voice recordings.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 */
private class VoiceRecord : Fragment() {

    /**
     * Fetch the Misnap SDK license.
     * Good practice: Handle the license in a way that it is remotely updatable.
     */
    private val license by lazy {  
        LicenseFetcher.fetch()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Configure a [MiSnapSettings] object with the appropriate step to start voice record and analysis.
         *
         */
        val misnapSettings = MiSnapSettings(MiSnapSettings.UseCase.VOICE, license).apply {
            // Please see MiSnapSettings.Voice.Flow for full list of options.
            voice.flow = MiSnapSettings.Voice.Flow.ENROLLMENT
        }

        /**
         * Optionally define a [MibiData] session outside of the reader's lifecycle.
         */
        MibiData.startSession(this::class.java.name, misnapSettings)

        /**
         * Observe the [MiSnapVoiceProcessor.events] [LiveData] to receive an events for each sample
         * sent by the audio recorder.
         *
         * @see [MiSnapVoiceProcessor.Event] for more details.
         */
        MiSnapVoiceProcessor(requireContext(), misnapSettings).apply {
            events.observe(viewLifecycleOwner) { event ->
                // Can use event.amplitude to visualize audio being sampled and analyzed.
            }

            /**
             * Observe the [MiSnapVoiceProcessor.result] [LiveData] to receive an [MiSnapVoiceProcessor.Result]
             * indicating voice recording status.
             * @see [MiSnapVoiceProcessor.Result] for full list of possible emitted events.
             */
            result.observe(viewLifecycleOwner) { result ->
                val hasAllRecordings = true
                when (result) {
                    is MiSnapVoiceProcessor.Result.Success -> {
                        if (hasAllRecordings) {
                            val wavBytes = result.audio
                            val rts = result.rts
                            val licenseExpired = result.licenseExpired

                            /**
                             * Releases all held up resources.
                             */
                            release()
                        } else {
                            /**
                             * Starts new voice recording.
                             */
                            start(newAudio = true)
                        }
                    }
                    is MiSnapVoiceProcessor.Result.Failure -> {
                        /**
                         * Handle failure result.
                         */
                        cancel()
                    }
                }
            }
        }.also {
            /**
             * Start voice recording and analysis.
             */
            it.start()
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
