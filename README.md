
# Goal Flow

Android application that show a lot of data (fixtures, scores, standings, lineups, ...) about football game.

Also it show news about different sports.


## Tech Stack

Android studio -> kotlin - xml


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
- DaggerHilt for dependency injection
- data and view binding
- caching on repository level

## API Reference

base URL https://football.sportdevs.com

#### Get all matches

```http
  GET /matches
```

|   Parameter   |   Type   |                Description                |
|:-------------:|:--------:|:-----------------------------------------:|
|  `season_id`  | `string` |    **Required** season of the matches     |
| `status_type` | `string` | status of the match ex:finished, upcoming |
|   `offset`    | `string` |        **Required** for pagination        |

#### Get leagues

```http
  GET /leagues
```

| Parameter | Type     | Description                         |
|:----------|:---------|:------------------------------------|
| `id`      | `string` | **Required**. Id of league to fetch |


#### Get seasons by league

```http
  GET /seasons-by-league
```

| Parameter   | Type     | Description                |
|:------------|:---------|:---------------------------|
| `league_id` | `string` | **Required**. id of league |



#### Get matches statistics

```http
  GET /matches-statistics
```

| Parameter  | Type     | Description               |
|:-----------|:---------|:--------------------------|
| `match_id` | `string` | **Required**. id of match |


#### Get standings

```http
  GET /standings
```

| Parameter   | Type     | Description                |
|:------------|:---------|:---------------------------|
| `season_id` | `string` | **Required**. id of season |


#### Get match graphs

```http
  GET /matches-graphs
```

| Parameter | Type     | Description               |
|:----------|:---------|:--------------------------|
| `id`      | `string` | **Required**. id of graph |


#### Get match players positions

```http
  GET /matches-positions
```

| Parameter  | Type     | Description               |
|:-----------|:---------|:--------------------------|
| `match_id` | `string` | **Required**. id of match |

## API's Documentations

[Football API](https://docs.sportdevs.com/docs/getting_started_football)

[News API](https://newsapi.org/docs/get-started)



## Badges
application security testing action with https://appsweep.guardsquare.com

[![AppSweep application security testing](https://github.com/ahmedjmm/Sportology/actions/workflows/upload-apk-to-appsweep.yml/badge.svg)](https://github.com/ahmedjmm/Sportology/actions/workflows/upload-apk-to-appsweep.yml)


Release build testing  
[![Test Release](https://github.com/ahmedjmm/Sportology/actions/workflows/test-release.yml/badge.svg)](https://github.com/ahmedjmm/Sportology/actions/workflows/test-release.yml)





## Limitations

#### Football API
- this API provides data in English language only, so if you are using the app in Arabic language you will still find some English data which are the API data
- 300 requests/day
  Please take these two notes in consideration when using the app to avoid error responses



#### What happens if I exceed the limit of my plan?
Once you have consumed all the requests of your plan, you will not receive any response from the API. There are no additional fees. You will have to wait for your quota to be reset to zero to get answers from the API again.