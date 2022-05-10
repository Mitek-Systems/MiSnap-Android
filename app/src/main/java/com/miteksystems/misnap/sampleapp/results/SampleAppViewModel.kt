package com.miteksystems.misnap.sampleapp.results

import androidx.lifecycle.ViewModel
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep

class SampleAppViewModel : ViewModel() {
    var results: List<MiSnapWorkflowStep.Result> = emptyList()
}
