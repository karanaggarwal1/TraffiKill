package com.example.karan.traffikill.Services;

import com.example.karan.traffikill.models.EmailUser;
import com.example.karan.traffikill.models.FacebookUser;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Karan on 29-07-2017.
 */

public interface FirebaseDataClient {
    @GET("unauthorised/usernames.json")
    Call<HashMap<String, String>> getUnauthorisedDataUsernames();

    @GET("unauthorised/email.json")
    Call<HashMap<String, String>> getUnauthorisedDataEmails();


    @GET("authorised/usersFB/{uid}.json")
    Call<FacebookUser> getFBUsers(@Path("uid") String uid);

    @GET("authorised/usersEmail/{uid}.json")
    Call<EmailUser> getEmailUsers(@Path("uid") String uid);
}
