package com.nammaplatform.app.ui.platform

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nammaplatform.app.R
import com.nammaplatform.app.model.Coach
import com.nammaplatform.app.model.CoachType

/**
 * A horizontally-scrollable strip showing the coach sequence of a train.
 *
 * The PDF explicitly asks for a Horizontal ScrollView and a clear visual
 * indication of where to stand for the General / Ladies coach. We extend
 * [HorizontalScrollView] so the parent screen can simply drop it into a
 * layout — no extra wiring needed.
 *
 * Each coach is rendered as a coloured tile:
 *   • ENGINE  → dark blue + emoji
 *   • GEN     → bright yellow (highlight) — primary user concern
 *   • LADIES  → pink (highlight)
 *   • Others  → neutral blue
 */
class CoachStripView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {

    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val pad = dp(8)
        setPadding(pad, pad, pad, pad)
    }

    init {
        isHorizontalScrollBarEnabled = true
        addView(
            container,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        )
    }

    fun setCoaches(coaches: List<Coach>) {
        container.removeAllViews()
        coaches.forEachIndexed { idx, coach ->
            container.addView(buildTile(coach))
            if (idx != coaches.lastIndex) container.addView(buildConnector())
        }
        // Auto-scroll back to the start whenever the data changes.
        post { smoothScrollTo(0, 0) }
    }

    private fun buildTile(coach: Coach): View {
        val tv = TextView(context).apply {
            text     = coach.code
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            gravity  = Gravity.CENTER
            setPadding(dp(14), dp(18), dp(14), dp(18))
            minWidth = dp(64)
            setTextColor(Color.BLACK)
        }
        val bgRes = when (coach.type) {
            CoachType.ENGINE -> R.drawable.bg_coach_engine
            CoachType.GEN    -> R.drawable.bg_coach_gen
            CoachType.LADIES -> R.drawable.bg_coach_ladies
            else             -> R.drawable.bg_coach_other
        }
        tv.background = ContextCompat.getDrawable(context, bgRes)
        // Engine gets a small icon prefix for instant recognition.
        if (coach.type == CoachType.ENGINE) {
            tv.text = "🚂\n${coach.code}"
        }
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(dp(2), 0, dp(2), 0)
        tv.layoutParams = lp
        return tv
    }

    private fun buildConnector(): View {
        // A tiny horizontal line that visually links neighbouring coaches.
        val v = View(context)
        v.setBackgroundColor(ContextCompat.getColor(context, R.color.brand_blue_dark))
        v.layoutParams = LinearLayout.LayoutParams(dp(8), dp(4)).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        return v
    }

    private fun dp(value: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()
}
