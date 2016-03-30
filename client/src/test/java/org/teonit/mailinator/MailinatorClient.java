package org.teonit.mailinator;

import org.teonit.mailinator.api.MailinatorService;

import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author Andrii Iakovenko
 */
public class MailinatorClient {

    private MailinatorService service;

    public MailinatorClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mailinator.com")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().create()))
                .build();

        service = retrofit.create(MailinatorService.class);
    }

    public MailinatorService getService() {
        return service;
    }

}
