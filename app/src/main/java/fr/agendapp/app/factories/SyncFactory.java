package fr.agendapp.app.factories;

public class SyncFactory {

    private static SyncFactory instance = null;

    private String token;

    private SyncFactory(String token) {
        this.token = token;
    }

    public static void init(String token) {
        instance = new SyncFactory(token);
    }

    public static SyncFactory getInstance() {
        // TODO
        return instance;
    }

}
