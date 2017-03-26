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
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.objects.User;
import fr.agendapp.app.objects.Work;

public class ParseFactory {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .create();

    public static List<Work> parseWork(String json) {
        Type collectionType = new TypeToken<LinkedList<Work>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendDO> parsePendDO(String json) {
        Type collectionType = new TypeToken<LinkedList<PendDO>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendDEL> parsePendDEL(String json) {
        Type collectionType = new TypeToken<LinkedList<PendDEL>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendDELc> parsePendDELc(String json) {
        Type collectionType = new TypeToken<LinkedList<PendDELc>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendFLAG> parsePendFLAG(String json) {
        Type collectionType = new TypeToken<LinkedList<PendFLAG>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendCOMM> parsePendCOMM(String json) {
        Type collectionType = new TypeToken<LinkedList<PendCOMM>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendALERT> parsePendALERT(String json) {
        Type collectionType = new TypeToken<LinkedList<PendALERT>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendADD> parsePendADD(String json) {
        Type collectionType = new TypeToken<LinkedList<PendADD>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<PendMERGE> parsePendMERGE(String json) {
        Type collectionType = new TypeToken<LinkedList<PendMERGE>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    static List<Invite> parseInvites(String json) {
        Type collectionType = new TypeToken<LinkedList<Invite>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static User parseUser(String json) {
        return gson.fromJson(json, User.class);
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
