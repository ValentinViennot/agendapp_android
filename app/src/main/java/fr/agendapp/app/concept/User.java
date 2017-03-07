/**
 * TODO
 * @author Dylan Habans
 */
class User {

    static User user;
    int id;
    String firstname;
    String lastname;
    String email;
    int notifications;
    boolean reminders;
    boolean notif_email;
    boolean fake_identity;
    int root;
    Group[] courses = new Group[];

    User() {

    }

    void logout (){

    }
}