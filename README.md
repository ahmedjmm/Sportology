
# Sportology

Android application that show a lot of data (fixtures, scores, standings, lineups, ...) about football game.

Also it show news about different sports.



## Tech Stack

Android studio 

kotlin - xml




## Features and technologies

- Native Android
- room database
- notifications with exact time
- MVVM design pattern
- firebase backend system for storing settings and google sign in
- Arabic localization
- search history provider
- unit testing 
- room database testing
- support tablet screen size views
- support dark/light mode
- Kotlin coroutines
- retrofit2 for API's calling
- DaggerHilt for dependancy injection
- data and view binding




## Documentations

[Football API](https://www.api-football.com/documentation-v3)

[News API](https://newsapi.org/docs/get-started)




## API References
Football API

Base URL `https://v3.football.api-sports.io`

#### Get fixtures of league
```http
  GET /fixtures?league=${league}&timezone=${timezone}&season=${season}
```
| Parameter | Type     |               Description           |
| :-------- | :------- | :---------------------------------  |
| `league`  | `Int | **Required** Id of league of the match  |
| `timezone`| `string` |timezone to show match time accoding to it                                                        |
| `season`  | `Int` | **Required** season of the match       |


#### Get live fixtures of league
```http
 GET /fixtures?league=${league}&timezone=${timezone}&season=${season}&live=${live}
```
| Parameter | Type     |value|       Description             |
| :-------- | :------- |:----|   :------------------------   |
| `league`  | `Int`    |     | **Required**. Id of league of the match                                 |
| `timezone`| `string` |     | timezone to show match time accoding to it                                                           |
| `season`  | `Int`    |     | **Required**. season of the match                                                        |
|live       | `string` | all | All or several leagues ids    |


#### Get leagues
```http
 GET /leagues?id=${id}&current=${current}
```
| Parameter | Type     |value|       Description         
| :-------- | :------- |:----|   :------------------------    
| `id`      | `Int`    |     | **Required** Id of the league |
| `current` | `string` |  true  | get current season of the league                                                       |


#### Get fixture by id
```http
 GET /fixtures?id=${id}
```
| Parameter | Type     |       Description                 |
| :-------- | :------- |   :---------------------------    |
| `id`      | `Int`    | **Required** Id of fixture        |
| `timezone`| `string` | get current season of the league  |


#### Get league standings
```http
 GET /standings?league=${league}&season=${season}
```
| Parameter | Type     |       Description              |
| :-------- | :------- |   :------------------------    |
| `league`  | `Int`    | **Required** Id of league      |
| `season`  | `Int`    | season of league standings     |


#### Search teams
```http
 GET /teams?search=${search}
```
| Parameter | Type     |       Description                   |
| :-------- | :------- |   :------------------------------   |
| `search`  | `string` | The name or the country name of the team        |


#### Search leagues
```http
 GET /leagues?search=${search}
```
| Parameter | Type     |       Description                   |
| :-------- | :------- |  :----------------------------------|
| `search`  | `string` | The name or the country of the league                                                       |


#### Get coach
```http
 GET /coachs?team=${team}
```
| Parameter | Type     |       Description              |
| :-------- | :------- |   :------------------------    |
| `team`    | `Int`    | The id of the team             |

/**********************************************************************************************/

News API

Base URL `https://newsapi.org`

#### Get all news
```http
 GET v2/everything
```
| Parameter | Type     |       Description                   |
| :-------- | :------- |   :------------------------------   |
| `q`       | `string` | The id of the teamKeywords or phrases to search for in the article title and body.                  |
| `language`| `string` | The 2-letter ISO-639-1 code of the news language you want to get                                 |
| `sortBy`  | `string` | The order to sort the articles in. Possible options: relevancy, popularity, publishedAt.        |
| `sources` | `string` | sources of the news                 |
| `searchIn`| `string` | The fields to restrict your q search to. The possible options are: title, description, content    |


#### Get top headline
```http
 GET v2/top-headlines
```
| Parameter | Type     |       Description                   |
| :-------- | :------- |   :------------------------------   |
| `language`| `string` | The 2-letter ISO-639-1 code of the news language you want to get                                |
| `category`| `string` | The category you want to get headlines for. Possible options: business, entertainment, general, health, science, sports, technology.                |





## Optimizations
**feat**
- replacing old version of settings - see `PreferencesFragment.kt` and `preferences.xml`
- google sign in
- firebase backend service for saving settings - see `Custom_preference.xml` and `CustomPreference.kt`
- show/hide views with animation - see `ViewCrossFadeAnimation.kt` inteface 
- search history provider for football API - see `MySuggestionProvider.kt`

**refactor** 
- deleted all unused resources and files
- `activity_home.xml` renamed from `activity_main.xml`
- `DateTieUtils.kt` refactoring body of `formatDateTime(dateTime: String): String` function
- `all_search_result_item.xml` change parent layout tag from `LinearLayout` to `MaterialCardView` 
- `arrays.xml` renamed and added some tags
- `BindingAdapters.kt` changed body of function `scoreBinding(textView: TextView, match: Fixtures.Response)` by replacing `when` statement with `if` statement
- `dimens.xml` adjusted some dimensions
- `DynamicLeagueFragment.kt` implementing `ViewCrossFadeAnimation.kt` interface and better code readability
- `LeagueStandingsFragment.kt` implementing `ViewCrossFadeAnimation.kt` interface and better code readability
- `EverythingFragment.kt` implementing `LeagueStandingsFragment.kt` interface and better code readability
- `filter_bottom_sheet.xml` removed unused IDs and replaced `space` tag with `divider`
- refactoring some fields from `val` to `var` for better response handling
- `FootballApi.kt` refactoring some function's parameters and return types
- `FootballFieldView.kt` better code readability
- `FootBallFragment.kt` better code readability
- `NewsFragment.kt` better code readability
- `FootBallViewModel.kt` better code readability and error response handling
- `fragment_news.xml` removed unused IDs
- `home_activity_nav_graph.xml` renamed from `main_activity_nav_graph.xml`
- `ic_football.xml` created another version of this file for dark mode
- `LeagueRecyclerViewAdapter.kt` better code readability and Optimizing
- `live_score_background.xml` renamed from `red_card.xml`
- `match_details_activity_nav_graph.xml` changing the ID of match lineups fragment from `matchAwayLineupsFragment` to `matchLineupsFragment`
- `match_details_layout.xml` most of its code moved to `activity_match_details.xml` for more code optimization and readability
- `MatchDetailsActivity.kt` code Optimizations and readability
- `MatchDetailsViewModel.kt` renamed from `MatchDetailsActivityViewModel.kt` and better code optimization and readability
- `MatchLineupsFragment.kt` implemented `ViewCrossFadeAnimation.kt` and code Optimizations and readability
- `MatchStatsFragment.kt` implemented `ViewCrossFadeAnimation.kt` and code Optimizations and readability
- `TopHeadlinesFragment.kt` implemented `ViewCrossFadeAnimation.kt` and code Optimizations and readability
- `open_in_browser_menu.xml` removed unused ID
- `RemoteRepository.kt` added `getFixtureById(timeZone: String, fixtureId: Int)` function
- `searchable.xml` some attributes added for history provider functionality
- `SearchableActivity.kt` better code optimization and readability
- `SearchActivityViewModel.kt` better errors handling, code optimization and readability
- `SettingsViewModel.kt` class for handling google account operations and settings
- `themes.xml` removed unused styles
- `time_line_item.xml` removed unused `layout` tag
- `TimeLineRecyclerViewAdapter.kt` better code optimization and readability
- `MyViewModelProvider.kt` `if` statement added to return `SettingsViewModel`
- `possession_statistic_layout.xml` removed `layout` tag and added another version of the file for supporting RTL


**performance** 
- some code moved from `match_details_layout.xml` to `activity_match_details` to reduce loading time of layout
- `activity_match_details_scene.xml` removed unused tags
- `activity_searchable.xml` removed `layout` tag for better performance
- `fragment_articles.xml` removed `layout` tag for better performance
- `fragment_standing.xml` removed `layout` tag for better performance
- `fragment_stats.xml` removed `layout` tag for better performance
- `league_match_date_item.xml` removed `layout` tag for better performance
- `other_stats_layout.xml` removed `layout` tag for better performance
- `LanguageManager.kt` class for handling application language changes for APIs lower than 33

**style**
- `league_match_result_item.xml` changed how views are visible to user

**test**
- removed unused test cases


## Badges
application security testing action with https://appsweep.guardsquare.com

[![AppSweep application security testing](https://github.com/ahmedjmm/Sportology/actions/workflows/upload-apk-to-appsweep.yml/badge.svg)](https://github.com/ahmedjmm/Sportology/actions/workflows/upload-apk-to-appsweep.yml)


Release build testing  
[![Test Release](https://github.com/ahmedjmm/Sportology/actions/workflows/test-release.yml/badge.svg)](https://github.com/ahmedjmm/Sportology/actions/workflows/test-release.yml)





## Limitations

#### Football API
- this API provides data in English language only, so if you are using the app in Arabic language you will still find some English data which are the API data
- 100 requests/day 
- 10 requests/minute 
Please take the last two notes in consideration when using the app to avoid error responses





## FAQ

#### What happens if I hit the rate limit per minute?
If you reach the maximum number of requests per minute, you receive an API error message instead of the requested data.

#### What happens if I exceed the limit of my plan?
Once you have consumed all the requests of your plan, you will not receive any response from the API. There are no additional fees. You will have to wait for your quota to be reset to zero to get answers from the API again.