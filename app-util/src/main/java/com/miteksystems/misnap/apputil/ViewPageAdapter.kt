package com.miteksystems.misnap.apputil

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.viewpager.widget.PagerAdapter

class ViewPageAdapter : PagerAdapter() {

    private val viewList: MutableList<View> = ArrayList()
    private val titleList: MutableList<String> = ArrayList()

    override fun getCount() = viewList.size

    override fun isViewFromObject(view: View, `object`: Any) = (view == `object`)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pageLayout = getPageLayout(container.context)
        val contentLayout = pageLayout.findViewById<LinearLayout>(CONTENT_VIEW_ID)
        viewList[position].parent?.let {
            (viewList[position].parent as ViewGroup).removeView(viewList[position])
        }

        contentLayout.addView(viewList[position])
        container.addView(pageLayout)
        return pageLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ScrollView)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titleList[position]
    }

    fun addView(view: View, title: String) {
        viewList.add(view)
        titleList.add(title)
        notifyDataSetChanged()
    }

    fun removeViewAt(index: Int) {
        titleList.removeAt(index)
        viewList.removeAt(index)
        notifyDataSetChanged()
    }

    fun clearViews() {
        titleList.clear()
        viewList.clear()
        notifyDataSetChanged()
    }

    //Assuming that the position of our "pages" can change
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    private fun getPageLayout(context: Context) =
        ScrollView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFillViewport = true
            addView(
                LinearLayout(context).apply {
                    id = CONTENT_VIEW_ID
                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    ).apply {
                        val activityMarginVertical =
                            resources.getDimension(R.dimen.misnapAppUtilViewPagerVerticalMargin).toInt()
                        val activityMarginHorizontal =
                            resources.getDimension(R.dimen.misnapAppUtilViewPagerHorizontalMargin).toInt()
                        setPadding(
                            activityMarginHorizontal,
                            activityMarginVertical,
                            activityMarginHorizontal,
                            activityMarginVertical
                        )
                    }
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                }
            )
        }

    companion object {
        const val CONTENT_VIEW_ID = Int.MAX_VALUE
    }
}
