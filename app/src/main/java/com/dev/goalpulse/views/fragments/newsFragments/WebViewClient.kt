package com.dev.goalpulse.views.fragments.newsFragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.dev.goalpulse.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.dev.goalpulse.models.news.News


class WebViewClient(
    private val progressIndicator: LinearProgressIndicator,
    private val context: Context,
    private val fullScreenBottomSheetView: View
): android.webkit.WebViewClient() {
    private lateinit var bottomSheet: BottomSheetDialog
    override fun onPageFinished(view: WebView?, url: String?) {
        progressIndicator.visibility = View.GONE
        super.onPageFinished(view, url)
    }

    fun showDialog(article: News.Article) {
        bottomSheet = BottomSheetDialog(context)
        bottomSheet.setContentView(fullScreenBottomSheetView)
        fullScreenBottomSheetView.findViewById<MaterialToolbar>(R.id.toolbar).title = article.title
        fullScreenBottomSheetView.findViewById<MaterialToolbar>(R.id.toolbar).setOnMenuItemClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(article.url)
            try { context.startActivity(intent) }
            catch (exception: ActivityNotFoundException) {
                Toast.makeText(context,
                    context.resources.getString(R.string.app_not_found), Toast.LENGTH_LONG).show()
            }
            return@setOnMenuItemClickListener true
        }
        article.url?.let {
            fullScreenBottomSheetView.findViewById<WebView>(R.id.web_view).webViewClient = this@WebViewClient
            fullScreenBottomSheetView.findViewById<WebView>(R.id.web_view).loadUrl(it)
        }
        val behavior = BottomSheetBehavior.from(fullScreenBottomSheetView.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val layout = bottomSheet.findViewById<CoordinatorLayout>(R.id.dialog)
        layout?.minimumHeight = context.resources.displayMetrics.heightPixels
        bottomSheet.show()
    }

    fun dismissModalSheet() {
        bottomSheet.cancel()
        bottomSheet.dismiss()
    }
}