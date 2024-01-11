package com.example.bookit.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;

    public RetrofitService(){
        initializeRetrofit();
    }

    private void initializeRetrofit() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.22:8080")
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    //TODO: Retrofit je za sada cini mi se implementiran, ostaje pitanje oko autorizacije ali to cu ujutru pogledati
    // sad odo da spavam da i ja legnem na vreme. Ovo bi van autorizacije trebalo da radi(falice token?), napravljen je api folder
    // koji predstavlja ono sto je servis u angularu bio. Ostavio sam da bude api jer ko zna mozda nam zatreba
    // da nesto bude servis odavde pa reko neka ostane, a ovo neka bude api. Dodao sam i User i UserCredentials(ovo vraca register
    // na backu pa sam samo zato pravio iskreno nemam blage zasto sam napravio da se vraca UserCredentials a ne User na backu al ajde)
    // necu sad ispravljati to sigurno. Taj User i UserCredentials predstavljaju zapravo backend dto objekte.
    // Treba istestirati nisam testirao nista ali mislim da ovo znaci da je spojeno samo treba raditi dalje.
    //TODO: ako mozda tebi ne radi ili nije dobro, ili posumnjam prvo na adresu, odnosno ovde imas 'http://treba ti tvoja adresa:8080
    // tu adresu nabavljas tako sto odes u command prompt, ukucas 'ipconfig' i imaces neko brdo polja, ja posto korisitm wifi
    // sam trebao da nadjem taj odeljak za wifi i odatle uzmem adresu sa polja: IPv4 Address. Ti vidi dal ces traziti isto ili ti koristis
    // ethernet pa onda tamo trazi polje istog imena vrv
}
