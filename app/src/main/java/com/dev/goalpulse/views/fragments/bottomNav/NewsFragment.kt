package com.dev.goalpulse.views.fragments.bottomNav

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.core.view.indices
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.dev.goalpulse.R
import com.dev.goalpulse.databinding.FiltersBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.tabs.TabLayout
import com.dev.goalpulse.viewModels.NewsViewModel
import com.dev.goalpulse.views.activities.HomeActivity
import com.dev.goalpulse.views.adapters.newsAdapters.NewsViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NewsFragment : Fragment(R.layout.fragment_news) {
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView
    private lateinit var viewPager: ViewPager2
    private lateinit var newsViewPagerAdapter: NewsViewPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var fullScreenBottomSheetView: View
    private val bottomSheet: BottomSheetDialog by lazy { BottomSheetDialog(requireContext()) }
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var _filtersBottomSheetBinding: FiltersBottomSheetBinding? = null

    private lateinit var newsViewModel: NewsViewModel

    var currentSelectedTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsViewModel = (requireActivity() as HomeActivity).newsViewModel

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity() )
        val newsLanguage = sharedPreferences.getString("news_language", "en")
        newsViewModel.language = newsLanguage!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FiltersBottomSheetBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        setupViewPagerAndTabs()
        setupSearchBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _filtersBottomSheetBinding = null
    }

    private fun setupSearchBar() {
        searchBar.inflateMenu(R.menu.news_options_menu)
        searchBar.setOnMenuItemClickListener { _ ->
            implementFilterViews()
            return@setOnMenuItemClickListener false
        }
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            if(searchView.text.toString() == "") newsViewModel.q = newsViewModel._q
            else {
                searchBar.text = searchView.text
                newsViewModel.q = searchBar.text.toString()
            }
            searchView.hide()
            lifecycleScope.launch(Dispatchers.IO) { newsViewModel.getEveryThingNews() }
            true
        }
    }

    private fun setupViewPagerAndTabs() {
        newsViewPagerAdapter = NewsViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.apply {
            adapter = NewsViewPagerAdapter(childFragmentManager, lifecycle)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                    currentSelectedTab = position
                    if(currentSelectedTab == 0) {
                        isOptionsMenuEnabled(true)
                        searchBar.isEnabled = true
                    }
                    else {
                        isOptionsMenuEnabled(false)
                        searchBar.isEnabled = false
                    }
                }
            })
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position ?: 0
                currentSelectedTab = viewPager.currentItem
                if(currentSelectedTab == 0) {
                    isOptionsMenuEnabled(true)
                    searchBar.isEnabled = true
                }
                else {
                    isOptionsMenuEnabled(false)
                    searchBar.isEnabled = false
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun implementFilterViews() {
        fullScreenBottomSheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.filters_bottom_sheet, null)
        bottomSheet.setContentView(fullScreenBottomSheetView)
        _filtersBottomSheetBinding?.toolbar?.setNavigationOnClickListener { bottomSheet.dismiss() }
        bottomSheet.setCanceledOnTouchOutside(false)
        val behavior = BottomSheetBehavior.from(fullScreenBottomSheetView.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.show()

        implementApplyButton()
        implementResetButton()
        implementSourcesChips()
        implementSearchInChips()
        implementSortChips()
    }

    private fun implementResetButton() {
        val reset = fullScreenBottomSheetView.findViewById<Button>(R.id.reset)
        reset.setOnClickListener {
            newsViewModel.q = newsViewModel._q
            newsViewModel.searchIn = newsViewModel.search_in
            newsViewModel.sources = newsViewModel._sources
            newsViewModel.sortBy = newsViewModel.sort_by
            bottomSheet.dismiss()
            implementFilterViews()
            bottomSheet.show()
        }
    }

    private fun implementApplyButton() {
        val apply = fullScreenBottomSheetView.findViewById<Button>(R.id.apply)
        apply.setOnClickListener {
            if(searchBar.text == null || searchBar.text == "") newsViewModel.q = "football"
            else newsViewModel.q = searchBar.text.toString()
            lifecycleScope.launch(Dispatchers.IO) { newsViewModel.getEveryThingNews() }
            bottomSheet.dismiss()
        }
    }

    private fun implementSourcesChips() {
        for(index in fullScreenBottomSheetView.findViewById<ChipGroup>(R.id.sources_chips).indices) {
            val chip = fullScreenBottomSheetView.findViewById<ChipGroup>(R.id.sources_chips)[index] as Chip
            chip.isChecked = newsViewModel.sources.contains(resources.getStringArray(R.array.news_sources)[index], true)
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) newsViewModel.sources += ",${resources.getStringArray(R.array.news_sources)[index]},"
                else newsViewModel.sources = newsViewModel.sources.replaceFirst(
                    resources.getStringArray(R.array.news_sources)[index], "", true)
                newsViewModel.removeSourcesExtraStrings()
            }
        }
    }

    private fun implementSortChips() {
        fullScreenBottomSheetView.findViewById<ChipGroup>(R.id.sort_chips).forEach {
            val chip = it as Chip
            if(newsViewModel.sortBy == newsViewModel.sort_by && chip.id == R.id.publishedAt)
                chip.isChecked = true
            else
                chip.isChecked = chip.text == newsViewModel.sortBy
            chip.setOnCheckedChangeListener { _, isChecked ->
                if(chip.id == R.id.publishedAt && isChecked)
                    newsViewModel.sortBy = resources.getString(R.string.publishedAt)
                else newsViewModel.sortBy = chip.text.toString()
            }
        }
    }

    private fun implementSearchInChips() {
        val contentChip = fullScreenBottomSheetView.findViewById<Chip>(R.id.content_chip)
        val titleChip = fullScreenBottomSheetView.findViewById<Chip>(R.id.title_chip)
        val descriptionChip = fullScreenBottomSheetView.findViewById<Chip>(R.id.description_chip)

        contentChip.isChecked =
            newsViewModel.searchIn.contains(newsViewModel.content, true)
        contentChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) newsViewModel.searchIn += "," + newsViewModel.content + ","
            else newsViewModel.searchIn = newsViewModel.searchIn.replaceFirst(
                resources.getString(R.string.content), "", true)
            //make sure that at least one chi is selected
            if(!titleChip.isChecked && !descriptionChip.isChecked){
                contentChip.isChecked = true
                newsViewModel.searchIn = newsViewModel.content
            }
            newsViewModel.removeSearchInExtraStrings()
        }

        titleChip.isChecked =
            newsViewModel.searchIn.contains(newsViewModel.title, true)
        titleChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) newsViewModel.searchIn += "," + newsViewModel.title + ","
            else newsViewModel.searchIn = newsViewModel.searchIn.replaceFirst(
                newsViewModel.title, "", true)
            //make sure that at least one chi is selected
            if(!descriptionChip.isChecked && !contentChip.isChecked) {
                _filtersBottomSheetBinding?.titleChip?.isChecked = true
                newsViewModel.searchIn = newsViewModel.title
            }
            newsViewModel.removeSearchInExtraStrings()
        }

        descriptionChip.isChecked =
            newsViewModel.searchIn.contains(newsViewModel.description, true)
        descriptionChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) newsViewModel.searchIn += "," + newsViewModel.description + ","
            else newsViewModel.searchIn = newsViewModel.searchIn.replaceFirst(
                newsViewModel.description, "", true)
            //make sure that at least one chi is selected
            if(!titleChip.isChecked && !contentChip.isChecked) {
                descriptionChip.isChecked = true
                newsViewModel.searchIn = newsViewModel.description
            }
            newsViewModel.removeSearchInExtraStrings()
        }
    }

    private fun isOptionsMenuEnabled(isEnable: Boolean) {
        searchBar.menu.findItem(R.id.filter).isVisible = isEnable
    }

    private fun initializeViews() {
        searchBar = requireView().findViewById(R.id.search_bar)
        searchView = requireView().findViewById(R.id.search_view)
        viewPager = requireView().findViewById(R.id.view_pager)
        tabLayout = requireView().findViewById(R.id.tab_layout)
    }
}