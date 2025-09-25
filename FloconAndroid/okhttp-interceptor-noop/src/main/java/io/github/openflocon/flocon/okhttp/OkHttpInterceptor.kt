package io.github.openflocon.flocon.okhttp

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class FloconOkhttpInterceptor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}