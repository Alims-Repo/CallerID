package com.alim.callerid.ui.bindings

import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alim.callerid.R

@BindingAdapter("animateVisibility")
fun setAnimatedVisibility(view: View, isVisible: Boolean) {
    if (isVisible) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("itemAnimation")
fun setRecyclerViewAnimation(recyclerView: RecyclerView, enable: Boolean) {
    if (enable) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_in_bottom)
        recyclerView.layoutAnimation = controller
        recyclerView.scheduleLayoutAnimation()
    }
}