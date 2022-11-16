package com.miteksystems.misnap.sampleapp.results

import android.animation.LayoutTransition
import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import org.json.JSONArray
import org.json.JSONObject

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.miteksystems.misnap.R

class JsonView : LinearLayout {

    private val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics)
    @ColorInt
    private var textColorString: Int = 0
    @ColorInt
    private var textColorBool: Int = 0
    @ColorInt
    private var textColorNull: Int = 0
    @ColorInt
    private var textColorNumber: Int = 0
    @Dimension
    private var textSize: Float = 0f

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
            init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.JsonViewer, 0, 0)
        try {
            textColorString = attributes.getColor(R.styleable.JsonViewer_textColorString,
                ContextCompat.getColor(context, R.color.misnapSampleJsonViewerTextColorString))
            textColorNumber = attributes.getColor(R.styleable.JsonViewer_textColorNumber,
                ContextCompat.getColor(context, R.color.misnapSampleJsonViewerTextColorNumber))
            textColorBool = attributes.getColor(R.styleable.JsonViewer_textColorBool,
                ContextCompat.getColor(context, R.color.misnapSampleJsonViewerTextColorBool))
            textColorNull = attributes.getColor(R.styleable.JsonViewer_textColorNull,
                ContextCompat.getColor(context, R.color.misnapSampleJsonViewerTextColorNull))
            textSize = attributes.getDimension(R.styleable.JsonViewer_textSize,
                resources.getDimension(R.dimen.misnapSampleAppJsonViewerTextSize))
        } finally {
            attributes.recycle()
        }
    }

    /**
     * Set JSON for the view. The old one will be erased and the view reset.
     * @param json The JSON to add.
     */
    fun setJson(json: String) = setJsonAny(JSONObject(json))

    private fun setJsonAny(json: Any) {
        super.setOrientation(VERTICAL)
        this.removeAllViews()
        addJsonNode(this, null, json, false)
    }

    fun setTextColorString(@ColorInt color: Int) {
        textColorString = color
    }

    fun setTextColorNumber(@ColorInt color: Int) {
        textColorNumber = color
    }

    fun setTextColorBool(@ColorInt color: Int) {
        textColorBool = color
    }

    fun setTextColorNull(@ColorInt color: Int) {
        textColorNull = color
    }

    fun setTextSize(@Dimension(unit = Dimension.SP) size: Float) {
        textSize = size
    }

    /**
     * It will collapse all nodes, except the main one.
     */
    fun collapseJson() {
        var i = 0
        while (i < this.childCount) {
            if (this.getChildAt(i) is TextView &&
                this.getChildAt(i + 1) is ViewGroup &&
                this.getChildAt(i + 2) is TextView) {
                changeVisibility(this.getChildAt(i + 1) as ViewGroup, View.VISIBLE)
                i += 2
            }
            i++
        }
    }

    /**
     * It will expands all the json nodes.
     */
    fun expandJson() {
        changeVisibility(this, View.GONE)
    }

    /**
     * Switch collapse status of a node content.
     *
     * @param group         The view group to operate on.
     * @param oldVisibility Current visibility.
     */
    private fun changeVisibility(group: ViewGroup, oldVisibility: Int) {
        var i = 0
        while (i < group.childCount) {
            if (group.getChildAt(i) is TextView &&
                group.getChildAt(i + 1) is ViewGroup &&
                group.getChildAt(i + 2) is TextView) {
                val groupChild = group.getChildAt(i + 1) as ViewGroup
                groupChild.visibility = oldVisibility
                group.getChildAt(i).callOnClick()
                changeVisibility(group.getChildAt(i + 1) as ViewGroup, oldVisibility)
                i += 2
            }
            i++
        }
    }

    /**
     * Add a node to the view with header key and close footer. This method is call for every node in a node.
     *
     * @param content  Current view group.
     * @param nodeKey  key of the current node.
     * @param jsonNode Node to display.
     * @param haveNext If this node is followed by a other one.
     */
    private fun addJsonNode(content: LinearLayout, nodeKey: Any?, jsonNode: Any, haveNext: Boolean) {

        val childCount = when (jsonNode) {
            is JSONObject -> jsonNode.length()
            is JSONArray -> jsonNode.length()
            else -> 0
        }

        val textViewHeader: TextView = getHeader(nodeKey, jsonNode, haveNext, true, childCount)

        content.addView(textViewHeader)

        if (childCount > 0) {
            val viewGroupChild = getJsonNodeChild(nodeKey, jsonNode)
            val textViewFooter = getFooter(jsonNode, haveNext)

            content.addView(viewGroupChild)
            content.addView(textViewFooter)

            textViewHeader.setOnClickListener {
                val newVisibility: Int
                val showChild: Boolean
                if (viewGroupChild.visibility == View.VISIBLE) {
                    newVisibility = View.GONE
                    showChild = false
                } else {
                    newVisibility = View.VISIBLE
                    showChild = true
                }
                textViewHeader.text = getHeaderText(nodeKey, jsonNode, haveNext, showChild, childCount)
                viewGroupChild.visibility = newVisibility
                textViewFooter.visibility = newVisibility
            }
        }
    }

    /**
     * Create a view group for a node content and return it.
     *
     * @param nodeKey  Key of the node passed as parameter.
     * @param jsonNode Content of the node use to fill view.
     * @return View group contain all the children of the node.
     */
    private fun getJsonNodeChild(nodeKey: Any?, jsonNode: Any): ViewGroup {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            setPadding(padding.toInt(), 0, 0, 0)
            if (nodeKey != null) {
                setBackgroundResource(R.drawable.background)
            }
            layoutTransition = LayoutTransition()

            if (jsonNode is JSONObject) {
                // setView key
                val iterator = jsonNode.keys()
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    // set view list
                    addJsonNode(this, key, jsonNode.get(key), iterator.hasNext())
                }
            } else if (jsonNode is JSONArray) {
                // setView key
                for (i in 0 until jsonNode.length()) {
                    // set view list
                    addJsonNode(this, i, jsonNode.get(i), i + 1 < jsonNode.length())
                }
            }
        }
    }

    private fun getHeader(key: Any?, jsonNode: Any?, haveNext: Boolean, childDisplayed: Boolean, childCount: Int): TextView {
        return TextView(context).apply {
            text = getHeaderText(key, jsonNode, haveNext, childDisplayed, childCount)
            TextViewCompat.setTextAppearance(this, R.style.JsonViewer_TextAppearance)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, this@JsonView.textSize)
            isFocusableInTouchMode = false
            isFocusable = false
        }
    }

    private fun getHeaderText(key: Any?, jsonNode: Any?, haveNext: Boolean, childDisplayed: Boolean, childCount: Int): SpannableStringBuilder {
        return SpannableStringBuilder().apply {
            if (key is String) {
                append("\"")
                append(key as String?)
                append("\"")
                append(": ")
            }
            if (!childDisplayed) {
                if (jsonNode is JSONArray) {
                    append("($childCount)", ForegroundColorSpan(textColorNull), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    append("[ ... ]")
                } else if (jsonNode is JSONObject) {
                    append("{ ... }")
                }
                if (haveNext) {
                    append(",")
                }
            } else {
                if (jsonNode is JSONArray) {
                    append("[")
                    if (childCount == 0) {
                        append(getFooterText(jsonNode, haveNext))
                    }
                } else if (jsonNode is JSONObject) {
                    append("{")
                    if (childCount == 0) {
                        append(getFooterText(jsonNode, haveNext))
                    }
                } else if (jsonNode != null) {
                    if (jsonNode is String) {
                        append("\"" + jsonNode + "\"", ForegroundColorSpan(textColorString), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else if (jsonNode is Int || jsonNode is Float || jsonNode is Double) {
                        append(jsonNode.toString(), ForegroundColorSpan(textColorNumber), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else if (jsonNode is Boolean) {
                        append(jsonNode.toString(), ForegroundColorSpan(textColorBool), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else if (jsonNode === JSONObject.NULL) {
                        append(jsonNode.toString(), ForegroundColorSpan(textColorNull), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    } else {
                        append(jsonNode.toString())
                    }

                    if (haveNext) {
                        append(",")
                    }

                    val span = LeadingMarginSpan.Standard(0, padding.toInt())
                    setSpan(span, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }

    private fun getFooter(jsonNode: Any?, haveNext: Boolean): TextView {
        return TextView(context).apply {
            text = getFooterText(jsonNode, haveNext)
            TextViewCompat.setTextAppearance(this, R.style.JsonViewer_TextAppearance)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, this@JsonView.textSize)
            isFocusableInTouchMode = false
            isFocusable = false
        }
    }

    private fun getFooterText(jsonNode: Any?, hasNext: Boolean): StringBuilder? {
        return StringBuilder().apply {
            when (jsonNode) {
                is JSONObject -> append("}")
                is JSONArray -> append("]")
            }
            if (hasNext) append(",")
        }
    }
}
