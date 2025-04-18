package com.example.myapplication

import android.content.Context
import com.example.myapplication.services.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.metamask.androidsdk.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    fun provideDappMetadata(@ApplicationContext context: Context): DappMetadata {
        return DappMetadata(
            name = context.applicationInfo.name,
            url = "https://${context.applicationInfo.name}.com",
            iconUrl = "https://cdn.sstatic.net/Sites/stackoverflow/Img/apple-touch-icon.png"
        )
    }

    @Provides
    fun provideEthereumFlow(
        @ApplicationContext context: Context,
        dappMetadata: DappMetadata
    ): EthereumFlow {
        return EthereumFlow(
            Ethereum(
                context = context,
                dappMetadata = dappMetadata,
                sdkOptions = SDKOptions(
                    infuraAPIKey = BuildConfig.MY_INFURA_KEY,
                    readonlyRPCMap = mapOf(
                        "Ganache" to "http://192.168.50.94:7545"
                    )
                )
            )
        )
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.50.94:5000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}