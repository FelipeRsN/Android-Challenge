package felipesilveira.bitcoinpricehistorical.utils

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import felipesilveira.bitcoinpricehistorical.connection.WebService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkUtils {
    companion object {
        fun getRetrofitConfiguration(): Retrofit {
            val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val httpClient = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS)
                    .connectTimeout(90, TimeUnit.SECONDS)

            val retrofit = Retrofit.Builder().baseUrl(App.CONNECTION_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()

            return retrofit
        }

        fun provideGson(): Gson {
            val builder = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            return builder.setLenient().create()
        }

        fun getRetrofit(): WebService {
            return getRetrofitConfiguration().create<WebService>(WebService::class.java)
        }
    }
}