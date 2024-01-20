package com.example.bookit.retrofit;

import android.content.Context;

import com.example.bookit.app.LocalDateTimeDeserializer;
import com.example.bookit.app.LocalDateTimeSerializer;

import com.example.bookit.model.enums.AccommodationStatus;
import com.example.bookit.security.ApiInterceptor;
import com.example.bookit.utils.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;


    private final String NATASA_IP=" http://192.168.1.5:8080";
    private final String DULE_IP="http://192.168.0.22:8080";

    private final String ALEX_IP="http://192.168.1.12:8080";

    public RetrofitService(Context context){
        initializeRetrofitFactory(context);
//        initializeRetrofit();
    }


    private void initializeRetrofit() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.22:8080")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    private void initializeRetrofitFactory(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new ApiInterceptor(context))
                .connectTimeout(15, TimeUnit.MINUTES)   //during debug this will make problem and will return SocketClosed err!
                .readTimeout(15, TimeUnit.MINUTES)
                .writeTimeout(15, TimeUnit.MINUTES)
                .build();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setLenient()
                .create();
        this.retrofit = new Retrofit.Builder()

                .baseUrl(NATASA_IP) //izdvojila sam nam ip adrese u const stringove, da lakse menjamo po potrebi-DULE_IP
                .addConverterFactory(GsonConverterFactory.create(customGson()))
                .client(client)
                .build();
    }

    private Gson customGson(){
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .create();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    //TODO: ako mozda tebi ne radi ili nije dobro, ili posumnjam prvo na adresu, odnosno ovde imas 'http://treba ti tvoja adresa:8080
    // tu adresu nabavljas tako sto odes u command prompt, ukucas 'ipconfig' i imaces neko brdo polja, ja posto korisitm wifi
    // sam trebao da nadjem taj odeljak za wifi i odatle uzmem adresu sa polja: IPv4 Address. Ti vidi dal ces traziti isto ili ti koristis
    // ethernet pa onda tamo trazi polje istog imena vrv
}
