package com.android.sergeyfitis.geektalksdemo.api.facebook;

import com.android.sergeyfitis.geektalksdemo.api.C;
import com.android.sergeyfitis.geektalksdemo.models.Group;
import com.android.sergeyfitis.geektalksdemo.models.GroupResponse;
import com.android.sergeyfitis.geektalksdemo.models.User;

import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public interface ApiCalls {
    @GET(C.GET_GROUPS)
    Observable<GroupResponse> groups();

    @GET(C.GET_MY_PROFILE)
    Observable<User> me();

    @GET(C.GROUP_DETAILS)
    Observable<Group> groupDetails(@Path("group_id") String groupId);
}
