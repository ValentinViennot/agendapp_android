package fr.agendapp.app.factories;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import fr.agendapp.app.App;
import fr.agendapp.app.objects.Work;

public class ParseFactory {

    private static final Gson gson = new Gson();

    public static List<Work> parseWork(String json) {
        Type collectionType = new TypeToken<List<Work>>() {
        }.getType();
        Log.i(App.TAG, json);
        return gson.fromJson(json, collectionType);
    }

}
