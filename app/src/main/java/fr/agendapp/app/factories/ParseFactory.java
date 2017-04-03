package fr.agendapp.app.factories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.objects.User;
import fr.agendapp.app.objects.Work;
import fr.agendapp.app.utils.pending.PendADD;
import fr.agendapp.app.utils.pending.PendALERT;
import fr.agendapp.app.utils.pending.PendCOMM;
import fr.agendapp.app.utils.pending.PendDEL;
import fr.agendapp.app.utils.pending.PendDELc;
import fr.agendapp.app.utils.pending.PendDO;
import fr.agendapp.app.utils.pending.PendFLAG;
import fr.agendapp.app.utils.pending.PendMERGE;

public class ParseFactory {

    private static final int DT = 2;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .registerTypeAdapter(Date.class, new DateSerializer())
            .create();

    public static List<Work> parseWork(String json) {
        Type collectionType = new TypeToken<LinkedList<Work>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendDO> parsePendDO(String json) {
        Type collectionType = new TypeToken<LinkedList<PendDO>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendDEL> parsePendDEL(String json) {
        Type collectionType = new TypeToken<LinkedList<PendDEL>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendDELc> parsePendDELc(String json) {
        Type collectionType = new TypeToken<LinkedList<PendDELc>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendFLAG> parsePendFLAG(String json) {
        Type collectionType = new TypeToken<LinkedList<PendFLAG>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendCOMM> parsePendCOMM(String json) {
        Type collectionType = new TypeToken<LinkedList<PendCOMM>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendALERT> parsePendALERT(String json) {
        Type collectionType = new TypeToken<LinkedList<PendALERT>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendADD> parsePendADD(String json) {
        Type collectionType = new TypeToken<LinkedList<PendADD>>() {
        }.getType();
        return gson.fromJson(json, collectionType);
    }

    public static List<PendMERGE> parsePendMERGE(String json) {
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

    public static String workToJson(List<Work> workList) {
        return gson.toJson(workList);
    }

    /**
     * Lecture des dates depuis le format JSON
     */
    private static class DateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(Work.dateformat.parse(jsonElement.getAsJsonPrimitive().getAsString()));
                cal.add(Calendar.HOUR, -DT);
                return cal.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date();
            }
        }
    }

    private static class DateSerializer implements JsonSerializer<Date> {
        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(Work.dateformat.format(date) + "+0" + DT + ":00");
        }
    }
}
