package com.mobile.sportology.views.fragments.newsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mobile.sportology.R
import com.mobile.sportology.ResponseState
import com.mobile.sportology.databinding.FragmentArticlesBinding
import com.mobile.sportology.models.news.News
import com.mobile.sportology.viewModels.NewsViewModel
import com.mobile.sportology.views.activities.MainActivity
import com.mobile.sportology.views.adapters.newsAdapters.ArticlesRecyclerViewAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopHeadlinesFragment: Fragment(R.layout.fragment_articles), ArticlesRecyclerViewAdapter.OnItemClickListener {
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentArticlesBinding
    private lateinit var fullScreenBottomSheetView: View
    private lateinit var toolbar: MaterialToolbar
    private lateinit var webView: WebView
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var webViewClient: WebViewClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel = (activity as MainActivity).newsViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch(Dispatchers.IO) {
            newsViewModel.getTopHeadlinesNews()
        }

        newsViewModel.topHeadlinesLiveData.observe(viewLifecycleOwner) { responseState ->
            when (responseState) {
                is ResponseState.Success -> handleSuccessResponse(responseState.data?.articles)
                is ResponseState.Loading -> handleLoadingState()
                is ResponseState.Error -> handleErrorResponse(responseState.message!!)
            }
        }
    }

    private fun handleLoadingState() {
        binding.circularProgressIndicator.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.errorLayout.root.visibility = View.GONE
    }

    private fun handleErrorResponse(errorMessage: String) {
        binding.circularProgressIndicator.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.errorLayout.root.visibility = View.VISIBLE
        binding.errorLayout.errorText.text = errorMessage
        binding.errorLayout.retryButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                newsViewModel.getEveryThingNews()
            }
        }
    }

    private fun handleSuccessResponse(articles: List<News.Article?>?) {
        val everythingRecyclerViewAdapter = ArticlesRecyclerViewAdapter(this)
        everythingRecyclerViewAdapter.context = requireContext()
        binding.recyclerView.apply {
            adapter = everythingRecyclerViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.visibility = View.VISIBLE
            everythingRecyclerViewAdapter.differ.submitList(articles)
            binding.circularProgressIndicator.visibility = View.GONE
            binding.errorLayout.root.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticlesBinding.inflate(inflater)
        return binding.root
    }

    override fun onItemClick(item: News.Article) {
        item.url?.let {
            initializeViews()
            webViewClient = WebViewClient(
                progressIndicator,
                requireContext(),
                fullScreenBottomSheetView
            ).apply { showDialog(item) }
        }
    }

    private fun initializeViews() {
        fullScreenBottomSheetView = LayoutInflater.from(requireContext())
            .inflate(R.layout.web_view_bottom_sheet, null)
        webView = fullScreenBottomSheetView.findViewById(R.id.web_view)!!
        toolbar = fullScreenBottomSheetView.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { webViewClient.dismissModalSheet() }
        progressIndicator = fullScreenBottomSheetView.findViewById(R.id.progress_indicator)
    }
}