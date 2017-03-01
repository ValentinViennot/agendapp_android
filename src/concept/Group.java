class Group{

    int id;
    int parentid;
    String name;
    int type;
    Color color;
    boolean isUser;

    Group(int type) {
        this.type = type;
    }

    /**
     * @author Dylan Habans
     * @return true si l'utilisateur rejoint le groupe, false sinon
     */
    boolean join() {

        return false;
    }
}