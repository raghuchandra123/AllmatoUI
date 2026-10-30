package www.allmato.com.data.remote;

import retrofit2.http.Body
import retrofit2.http.GET;
import retrofit2.http.POST
import retrofit2.http.Query
import www.allmato.com.data.model.Menu;
import www.allmato.com.data.model.OrderResponse

public interface AlmatoServiceApi {

    @GET("allmato/v1/items")
    suspend fun getItems(
        @Query("lstKeEncrpt") lstKeEncrpt : String?
    ): Menu

    @POST("allmato/v1/order")
    suspend fun order(
        @Body items : Map<String, Int>
    ) : OrderResponse

}
