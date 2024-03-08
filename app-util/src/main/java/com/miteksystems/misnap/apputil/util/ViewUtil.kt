package com.miteksystems.misnap.apputil.util

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.miteksystems.misnap.apputil.R
import com.miteksystems.misnap.apputil.view.JsonView
import com.miteksystems.misnap.apputil.view.MiSnapAudioView
import com.miteksystems.misnap.apputil.view.MiSnapVideoView
import com.miteksystems.misnap.core.Barcode
import com.miteksystems.misnap.core.DocumentExtraction
import com.miteksystems.misnap.core.ExifUtil
import com.miteksystems.misnap.core.Mrz1Line
import com.miteksystems.misnap.core.MrzData
import com.miteksystems.misnap.core.UserAction
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants

object ViewUtil {
    private const val LOG_TAG = "MiSnapAppUtilViewUtil"
    fun getProgressBarView(context: Context) =
        ProgressBar(context, null, android.R.attr.progressBarStyleLarge).apply {
            isIndeterminate = true
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

    fun addNfcDataEntry(context: Context, layout: LinearLayout, title: String, content: String?) {
        if (content == null || content.isEmpty())
            return

        val titleView = MaterialTextView(context).apply {
            setTypeface(null, Typeface.BOLD)
            text = title
            setTextIsSelectable(true)
        }

        val contentView = MaterialTextView(context)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            contentView, 12, 30, 1,
            TypedValue.COMPLEX_UNIT_DIP
        )
        contentView.text = content
        contentView.setPadding(
            0, 0, 0, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8F, context.resources.displayMetrics
            ).toInt()
        )
        contentView.setTextIsSelectable(true)
        layout.addView(titleView)
        layout.addView(contentView)
    }

    fun getExtractionView(extraction: DocumentExtraction, context: Context) =
        getBaseLinearLayoutView(context).apply {
            extraction.mrz?.let { mrz ->
                addView(MaterialTextView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.START
                    textSize = 25f
                    text = HtmlCompat.fromHtml(
                        context.getString(R.string.misnapAppUtilResultsMrzDataTitle),
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                })

                val mrzView = MaterialTextView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.START
                    textSize = 20f
                    when (mrz) {
                        is MrzData -> {
                            text = HtmlCompat.fromHtml(
                                String.format(
                                    context.getString(R.string.misnapAppUtilResultsMrzData),
                                    mrz.documentNumber,
                                    mrz.documentCode,
                                    mrz.country,
                                    mrz.dateOfBirth,
                                    mrz.dateOfExpiry,
                                    mrz.optionalData1
                                ),
                                HtmlCompat.FROM_HTML_MODE_COMPACT
                            )
                        }

                        is Mrz1Line -> {
                            text = HtmlCompat.fromHtml(
                                String.format(
                                    context.getString(R.string.misnapAppUtilResultsMrzOneLineData),
                                    mrz.mrzString
                                ),
                                HtmlCompat.FROM_HTML_MODE_COMPACT
                            )
                        }
                    }
                    setTextIsSelectable(true)
                }

                addView(mrzView)
            }

            extraction.extractedData?.let { documentData ->
                addView(MaterialTextView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.START
                    textSize = 25f
                    text = HtmlCompat.fromHtml(
                        context.getString(R.string.misnapAppUtilResultsDocumentDataTitle),
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                })

                val documentDataView = MaterialTextView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.START
                    textSize = 20f

                    text = HtmlCompat.fromHtml(
                        String.format(
                            context.getString(R.string.misnapAppUtilResultsDocumentData),
                            documentData.docType ?: "",
                            documentData.country ?: "",
                            documentData.surname ?: "",
                            documentData.firstName ?: "",
                            documentData.sex ?: "",
                            documentData.docNumber ?: "",
                            documentData.nationality ?: "",
                            documentData.dateOfBirth ?: "",
                            documentData.dateOfExpiration ?: "",
                            documentData.optionalData1 ?: "",
                            documentData.optionalData2 ?: "",
                        ),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
                addView(documentDataView)

                documentData.rawData?.let {
                    addView(MaterialTextView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        gravity = Gravity.START
                        textSize = 25f
                        text = HtmlCompat.fromHtml(
                            context.getString(R.string.misnapAppUtilResultsRawMrzTitle),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                    })

                    addView(MaterialTextView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        gravity = Gravity.START
                        textSize = 20f
                        text = documentData.rawData
                    })
                }
            }
        }

    fun getBarcodeView(barcode: Barcode, context: Context) =
        LinearLayout(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val activityMarginVertical =
                    resources.getDimension(R.dimen.misnapAppUtilLayoutVerticalMargin).toInt()
                val activityMarginHorizontal =
                    resources.getDimension(R.dimen.misnapAppUtilLayoutHorizontalMargin)
                        .toInt()
                setMargins(
                    activityMarginHorizontal,
                    activityMarginVertical,
                    activityMarginHorizontal,
                    activityMarginVertical
                )
            }
            orientation = LinearLayout.VERTICAL
            barcode.type?.let {
                addView(
                    MaterialTextView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        gravity = Gravity.START
                        textSize = 25f
                        text = context.getString(R.string.misnapAppUtilResultsBarcodeType, it.name)
                        setTextIsSelectable(true)
                    }
                )
            }
            barcode.encodedBarcode?.let {
                addView(
                    MaterialTextView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        gravity = Gravity.START
                        textSize = 25f
                        text = context.getString(R.string.misnapAppUtilResultsBarcodeData, it)
                        setTextIsSelectable(true)
                    }
                )
            }
        }

    fun getBaseLinearLayoutView(
        context: Context,
        width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
        height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        layoutOrientation: Int = LinearLayout.VERTICAL
    ) = LinearLayout(context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(
            width, height
        ).apply {
            val activityMarginVertical =
                resources.getDimension(R.dimen.misnapAppUtilLayoutVerticalMargin).toInt()
            val activityMarginHorizontal =
                resources.getDimension(R.dimen.misnapAppUtilLayoutHorizontalMargin).toInt()
            setMargins(
                activityMarginHorizontal,
                activityMarginVertical,
                activityMarginHorizontal,
                activityMarginVertical
            )
        }
        orientation = layoutOrientation
    }

    fun getWarningsView(warnings: List<UserAction>, context: Context) =
        getBaseLinearLayoutView(context).apply {
            warnings.iterator().forEach { userAction ->
                addView(
                    getMiSnapListTextView(userAction.toString(), context)
                )
            }
        }

    fun getMiSnapListTextView(content: String, context: Context) = MaterialTextView(context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            val defaultMargin =
                context.resources.getDimension(R.dimen.misnapAppUtilSpaceDefault).toInt()
            setMargins(0, 0, 0, defaultMargin)
        }

        val defaultMarginDouble =
            context.resources.getDimension(R.dimen.misnapAppUtilSpaceDefault).toInt()
        setPadding(defaultMarginDouble, 0, 0, 0)
        background = AppCompatResources.getDrawable(
            context, R.drawable.misnap_list_background
        )
        gravity = Gravity.START
        textSize = 16f
        text = content
        setTextIsSelectable(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun getFinalFrameView(byteImage: ByteArray, context: Context) =
        getBaseLinearLayoutView(
            context,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            val bmp = BitmapFactory.decodeByteArray(
                byteImage,
                0,
                byteImage.size,
                BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                })

            addView(
                getGenericTextView(
                    String.format(
                        context.getString(R.string.misnapAppUtilResultsFinalFrameDiskSize),
                        "${byteImage.size / 1024}KB"
                    ),
                    context
                )
            )
            addView(
                getGenericTextView(
                    String.format(
                        context.getString(R.string.misnapAppUtilResultsFinalFrameDimensions),
                        "${bmp.width}x${bmp.height}"
                    ),
                    context
                )
            )

            addView(
                getEnhancedImageView(
                    byteImage,
                    context
                ).apply {
                    setOnTouchListener { view, motionEvent ->
                        if (motionEvent.action == android.view.MotionEvent.ACTION_DOWN) {
                            kotlin.runCatching {
                                view.parent.parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }

                        if (motionEvent.action == android.view.MotionEvent.ACTION_UP) {
                            kotlin.runCatching {
                                view.parent.parent.requestDisallowInterceptTouchEvent(false)
                            }
                        }

                        false
                    }
                })
        }

    private fun getEnhancedImageView(byteImage: ByteArray, context: Context) =
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            loadData(
                "<html><body style='margin:0;padding:0;'><img src=\"data:image/jpeg;base64,${
                    Base64.encodeToString(
                        byteImage,
                        Base64.DEFAULT
                    )
                }\" /></body></html>",
                "text/html",
                "charset=utf-8"
            )
        }

    fun getMiSnapVideoView(data: ByteArray, context: Context) =
        MiSnapVideoView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            MediaController(context).also {
                it.setAnchorView(this)
                setVideoData(data)
                setMediaController(it)
            }
        }

    fun getGenericTextView(content: String, context: Context, layoutGravity: Int = Gravity.START) =
        MaterialTextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = layoutGravity
            textSize = 16f
            text = content
            setTextIsSelectable(true)
        }

    fun getMiSnapAudioView(data: ByteArray, context: Context) =
        MiSnapAudioView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            MediaController(context, false).also {
                setAudioData(data)
                setMediaController(it)
            }
        }

    fun getMiBiDataView(jpeg: ByteArray, context: Context): View {
        val mibiData = ExifUtil.readExifTag(jpeg, ExifTagConstants.EXIF_TAG_USER_COMMENT)
            ?: context.getString(R.string.misnapAppUtilResultsMibiDataNotFound)

        return getMiBiDataView(mibiData, context)
    }

    fun getMiBiDataView(mibiData: String, context: Context) =
        LinearLayout(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL

            addView(
                getCopyToClipboardButton(
                    context,
                    mibiData,
                    context.getString(R.string.misnapAppUtilResultsMibiCopyButtonLabel),
                    context.getString(R.string.misnapAppUtilResultsMibiCopySuccessMessage),
                    context.getString(R.string.misnapAppUtilResultsMibiCopyFailureMessage)
                )
            )

            addView(JsonView(context).apply {
                isVerticalScrollBarEnabled = true
                this.setJson(mibiData)
            })
        }

    fun getCopyToClipboardButton(
        context: Context,
        contentToCopy: String,
        buttonLabel: String,
        copySuccessMessage: String,
        copyFailedMessage: String
    ) = MaterialButton(context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        text = buttonLabel
        setOnClickListener {
            try {
                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
                    setPrimaryClip(
                        ClipData.newPlainText(
                            context.getString(R.string.misnapAppUtilClipboardId), contentToCopy
                        )
                    )
                }
                Toast.makeText(
                    context, copySuccessMessage, Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error copying contents to clipboard", e)
                Toast.makeText(
                    context, copyFailedMessage, Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
