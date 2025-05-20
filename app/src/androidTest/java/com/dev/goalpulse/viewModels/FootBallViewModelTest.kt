package com.dev.goalpulse.viewModels
//
//import com.dev.goalpulse.Shared
//import com.dev.goalpulse.api.FootballApi
//import com.dev.goalpulse.repositories.RemoteRepository
//import org.junit.Before
//import okhttp3.mockwebserver.MockWebServer
//import org.junit.After
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class FootBallViewModelTest {
//    private lateinit var mockWebServer: MockWebServer
//    private lateinit var footballApi: FootballApi
//    private lateinit var remoteRepository: RemoteRepository
//
//    @Before
//    fun setUp() {
//        mockWebServer = MockWebServer()
//        mockWebServer.start()
//        footballApi = Retrofit.Builder()
//            .baseUrl(mockWebServer.url(Shared.FOOTBALL_BASE_URL))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build().create(FootballApi::class.java)
//
//        remoteRepository = RemoteRepository(footballApi, null)
//    }
//
//    @After
//    fun tearDown() {
//        mockWebServer.shutdown()
//    }
//
//
//}