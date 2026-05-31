package dev.oruizp.expensetracker.android.data.remote

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.cache.normalized.CacheManager
import com.apollographql.cache.normalized.api.DefaultCacheKeyGenerator
import com.apollographql.cache.normalized.api.DefaultCacheResolver
import com.apollographql.cache.normalized.cacheManager
import com.apollographql.cache.normalized.sql.SqlNormalizedCacheFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object GraphqlModule {

    private const val DEFAULT_TIMEOUT_SECONDS = 30L
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/graphql"

    @Volatile
    private var apolloClient: ApolloClient? = null

    fun provideApolloClient(
        context: Context,
        baseUrl: String = DEFAULT_BASE_URL,
        okHttpClient: OkHttpClient = defaultOkHttpClient(),
    ): ApolloClient {
        return apolloClient ?: synchronized(this) {
            apolloClient ?: buildApolloClient(baseUrl, okHttpClient, context).also {
                apolloClient = it
            }
        }
    }

    private fun defaultOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private fun buildApolloClient(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        context: Context,
    ): ApolloClient {
        val cacheManager = CacheManager(
            normalizedCacheFactory = SqlNormalizedCacheFactory(context),
            cacheKeyGenerator = DefaultCacheKeyGenerator,
            cacheResolver = DefaultCacheResolver,
        )
        return ApolloClient.Builder()
            .serverUrl(baseUrl)
            .cacheManager(cacheManager)
            .build()
    }
}
