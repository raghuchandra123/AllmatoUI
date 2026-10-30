package www.allmato.com.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit = Retrofit.Builder()
                                .baseUrl("http://192.168.100.9:8080/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
    val api: AlmatoServiceApi = retrofit.create(AlmatoServiceApi::class.java)
}