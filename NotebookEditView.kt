package com.general.view

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.LineBackgroundSpan
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

private val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

/**
 * 监听输入框焦点控制键盘的弹出
 */
fun subscribeFocusState(context: Context, et: EditText) {
    et.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
                et, InputMethodManager.SHOW_IMPLICIT
            )
        } else {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                et.windowToken, 0
            )
        }
    }
}

private val rect = Rect()
private var initialValue: Int? = null

/**
 * 监听键盘高度变化，判断键盘是否弹出
 */
fun subscribeKeyboardState(activity: Activity) {
    val rootView = activity.window?.decorView?.rootView
    rootView?.viewTreeObserver?.addOnGlobalLayoutListener {
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height
        val initial = initialValue ?: (screenHeight - rect.bottom).also { initialValue = it }
        val keyboardHeight = screenHeight - rect.bottom - initial
        if (keyboardHeight == 0) {
            //show
        } else {
            //hide
        }
    }
}

/**
 * 添加分割线, 注册监听[MyTextWatcher]
 * ——————————————————
 * xxxx
 * __________________
 * xxx
 * __________________
 */
private class DashedUnderlineSpan : LineBackgroundSpan {

    private val mPaint: Paint = Paint()

    override fun drawBackground(
        c: Canvas, p: Paint, left: Int, right: Int, top: Int,
        baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int, lnum: Int
    ) {
        c.drawLine(
            left.toFloat(), top.toFloat() - 10.dp, //xml中 lineSpacingExtra为20dp
            right.toFloat(), top.toFloat() - 10.dp,
            mPaint
        )
        if (end == text.length) {
            c.drawLine(
                left.toFloat(), bottom.toFloat() + 10.dp,
                right.toFloat(), bottom.toFloat() + 10.dp,
                mPaint
            )
        }
    }

    init {
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = Color.GRAY
        mPaint.strokeWidth = 1.dp.toFloat()
        mPaint.pathEffect = DashPathEffect(floatArrayOf(15f, 5f), 0f)
    }
}

private class MyTextWatcher(
    val et: EditText
) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null && count > 0) {
            et.removeTextChangedListener(this)
            val spannableString = SpannableString(s)
            spannableString.setSpan(
                DashedUnderlineSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
            et.setText(spannableString)
            et.setSelection(start + count)
            et.addTextChangedListener(this)
            if (!s.isNullOrEmpty()) {
                val content = s.toString().trim()
                //do something
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {}
}

/**XML
<ScrollView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center_vertical">

    <androidx.appcompat.widget.AppCompatEditText
       android:id="@+id/et"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:background="@null"
       android:lineSpacingExtra="20dp" />
</ScrollView>
 */
