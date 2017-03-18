package fr.agendapp.app.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import fr.agendapp.app.objects.Work;

public class ParseFactory {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .create();

    public static List<Work> parseWork(String json) {
        Type collectionType = new TypeToken<List<Work>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    /**
     * Lecture des dates depuis le format JSON
     */
    private static class DateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                return Work.dateformat.parse(jsonElement.getAsJsonPrimitive().getAsString());
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date();
            }
        }
    }
    /*private static class WorkDeserializer implements JsonDeserializer<Work> {
        public Work deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            jsonObject.get("color").getAsString();
            return object;
        }
    }*/
}
