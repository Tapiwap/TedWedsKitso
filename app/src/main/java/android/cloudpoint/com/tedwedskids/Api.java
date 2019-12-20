package android.cloudpoint.com.tedwedskids;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    String baseUrl = "https://tedmadbackend.herokuapp.com/";

    @GET(".")
    Call<List<Guest>> getGuests();
}
