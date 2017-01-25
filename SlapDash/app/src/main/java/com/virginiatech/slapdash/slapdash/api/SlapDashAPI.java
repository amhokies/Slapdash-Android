package com.virginiatech.slapdash.slapdash.api;

import com.virginiatech.slapdash.slapdash.DataModelClasses.Event;
import com.virginiatech.slapdash.slapdash.DataModelClasses.User;
import com.virginiatech.slapdash.slapdash.DataModelClasses.UserLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by Matt on 10/6/2016.
 */

public interface SlapDashAPI {

    String EVENT_ID = "id";
    String USER_ID = "id";

    String EVENTS_URL = "events";
    String EVENT_ID_URL = "events/{" + EVENT_ID + "}";
    String EVENT_LOCK_IN = EVENT_ID_URL + "/lockin";
    String EVENT_RE_ROLL = EVENT_ID_URL + "/reroll";

    String USERS_URL = "users";
    String USER_ID_URL = "users/{" + USER_ID + "}";
    String USER_EVENT_URL = "users/{" + USER_ID + "}/events";

    String EVENT_RESPOND_URL = "users/invitations/respond";

    String ACCESS_TOKEN_QUERY = "accesstoken";

    @GET()
    Call sanityCheck();

    @GET(EVENTS_URL)
    Call<List<Event>> getEvents(@Query(ACCESS_TOKEN_QUERY) String accessToken);

    @POST(EVENTS_URL)
    Call<Event> createEvent(@Query(ACCESS_TOKEN_QUERY) String accessToken,
                            @Body CreateEventRequest eventToBeCreated);

    @POST(EVENT_RESPOND_URL)
    Call<Event> respondToEvent(@Query(ACCESS_TOKEN_QUERY) String accessToken,
                               @Body EventInvitationRequest requestBody);


    @GET(EVENT_ID_URL)
    Call<Event> getEvent(@Path(EVENT_ID) String eventId, @Query(ACCESS_TOKEN_QUERY) String accessToken);

    @POST(EVENT_LOCK_IN)
    Call<ErrorSuccess> lockinEvent(@Path(EVENT_ID) String eventId, @Query(ACCESS_TOKEN_QUERY) String accessToken);

    @POST(EVENT_RE_ROLL)
    Call<ErrorSuccess> rerollEvent(@Path(EVENT_ID) String eventId, @Query(ACCESS_TOKEN_QUERY) String accessToken);

    @PUT(EVENT_ID_URL)
    Call<Event> updateEvent(@Path(EVENT_ID) String eventId, @Body Event updatedEvent);

    @DELETE(EVENT_ID_URL)
    Call<Event> deleteEvent(@Query(ACCESS_TOKEN_QUERY) String accessToken, @Path(EVENT_ID) String eventId);


    @GET(USERS_URL)
    Call<List<User>> getUsers();

    @POST(USERS_URL)
    Call<User> createUser(@Query(ACCESS_TOKEN_QUERY) String accessToken, @Body User userToBeCreated);


    @GET(USER_ID_URL)
    Call<User> getUser(@Path(USER_ID) String userId);

    @GET(USER_EVENT_URL)
    Call<List<Event>> getUsersEvents(@Path(USER_ID) String userId, @Query(ACCESS_TOKEN_QUERY) String accessToken);

    @PUT(USER_ID_URL)
    Call<User> updateUser(@Path(USER_ID) String userId, @Query(ACCESS_TOKEN_QUERY) String accessToken, @Body User updatedUser);

    @DELETE(USER_ID_URL)
    Call<User> deleteUser(@Path(USER_ID) String userId);
}
