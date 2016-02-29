package org.teonit.mailinator.api;

import org.teonit.mailinator.model.Inbox;
import org.teonit.mailinator.model.MessageData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * @author Andrii Iakovenko
 */
public interface MailinatorService {

    @GET(value = "/api/webinbox")
    Call<Inbox> inbox(@Query("to") String account);

    @GET(value = "/api/email")
    Call<MessageData> message(@Query("msgid") String id);

    @GET(value = "/api/expunge")
    Call<Object> delete(@Query("msgid") String id);
}
