package com.dev.goalpulse.views.fragments.newsFragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.goalpulse.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.Shared
import com.dev.goalpulse.databinding.ErrorLayoutBinding
import com.dev.goalpulse.models.news.News
import com.dev.goalpulse.viewModels.NewsViewModel
import com.dev.goalpulse.views.activities.HomeActivity
import com.dev.goalpulse.views.adapters.newsAdapters.ArticlesRecyclerViewAdapter
import com.dev.goalpulse.views.viewsUtilities.ViewCrossFadeAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EverythingFragment : Fragment(R.layout.fragment_articles),
    ArticlesRecyclerViewAdapter.OnItemClickListener, ViewCrossFadeAnimation {
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var fullScreenBottomSheetView: View
    private lateinit var toolbar: MaterialToolbar
    private lateinit var webView: WebView
    private lateinit var fragmentProgressIndicator: CircularProgressIndicator
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var webViewClient: WebViewClient
    private lateinit var _errorLayoutBinding: ErrorLayoutBinding


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override var shortAnimationDuration: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shortAnimationDuration = requireContext().resources.getInteger(android.R.integer.config_shortAnimTime)
        newsViewModel = (activity as HomeActivity).newsViewModel
        lifecycleScope.launch(Dispatchers.IO) { newsViewModel.getEveryThingNews() }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFragmentViews(view)
        newsViewModel.everyThingLiveData.observe(viewLifecycleOwner) { responseState ->
            when (responseState) {
                is ResponseState.Success -> handleSuccessResponse(responseState.data?.articles)
                is ResponseState.Loading -> handleLoadingState()
                is ResponseState.Error -> handleErrorResponse(responseState.message!!)
            }
        }

        newsViewModel.newsLanguageLiveData.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.IO) { newsViewModel.getEveryThingNews() }
        }
    }

    override fun hideViewWithAnimation(view: View){
        view.animate().alpha(0f).setDuration(shortAnimationDuration.toLong())
            .setListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }

    override fun showViewWithAnimation(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(shortAnimationDuration.toLong()).setListener(null)
        }
    }

    private fun handleLoadingState() {
        showViewWithAnimation(fragmentProgressIndicator)
        hideViewWithAnimation(recyclerView)
        hideViewWithAnimation(_errorLayoutBinding.root)
    }

    private fun handleErrorResponse(errorMessage: String) {
        _errorLayoutBinding.errorText.text = errorMessage
        _errorLayoutBinding.retryButton.setOnClickListener {
            if (Shared.isConnected)
                lifecycleScope.launch(Dispatchers.IO) {
                    newsViewModel.getEveryThingNews()
                }
            else (requireActivity() as HomeActivity).snackBar.show()
        }
        hideViewWithAnimation(fragmentProgressIndicator)
        hideViewWithAnimation(recyclerView)
        showViewWithAnimation(_errorLayoutBinding.root)
    }

    private fun handleSuccessResponse(articles: List<News.Article?>?) {
        val everythingRecyclerViewAdapter = ArticlesRecyclerViewAdapter(this)
        everythingRecyclerViewAdapter.differ.submitList(articles)
        everythingRecyclerViewAdapter.context = requireContext()
        recyclerView.apply {
            adapter = everythingRecyclerViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            showViewWithAnimation(this)
        }
        hideViewWithAnimation(fragmentProgressIndicator)
        hideViewWithAnimation(_errorLayoutBinding.root)
    }

    private fun initializeFragmentViews(view: View) {
        _errorLayoutBinding = ErrorLayoutBinding.bind(view.findViewById(R.id.error_layout))
        fragmentProgressIndicator = view.findViewById(R.id.circularProgressIndicator)
        recyclerView = view.findViewById(R.id.recycler_view)
    }

    private fun initializeBottomSheetViews() {
        fullScreenBottomSheetView = LayoutInflater.from(requireContext())
            .inflate(R.layout.web_view_bottom_sheet, null)
        webView = fullScreenBottomSheetView.findViewById(R.id.web_view)!!
        toolbar = fullScreenBottomSheetView.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { webViewClient.dismissModalSheet() }
        progressIndicator = fullScreenBottomSheetView.findViewById(R.id.progress_indicator)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_articles, container, false)

    override fun onItemClick(item: News.Article) {
        item.url?.let {
            initializeBottomSheetViews()
            webViewClient = WebViewClient(
                progressIndicator,
                requireContext(),
                fullScreenBottomSheetView
            ).apply { showDialog(item) }
        }
    }
}