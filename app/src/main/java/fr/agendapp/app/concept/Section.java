/**
 * @author  Dylan Habans
 * @author Valentin Viennot
 * @author Lucas Probst
 * @author Charline Bardin
 */
class Section {

    String month;
    String day;
    int date;
    LinkedList<Work> homeworks;

    public Section (Work w){
        this.month = w.date.monthOfYear();
        this.day = w.date.dayOfWeek();
        this.date = w.date.dayOfMonth();
        homeworks = new LinkedList<Work>;
    }

    public Section (Work w, boolean b){
        this.month = null;
        this.day = w.date.dayOfWeek();
        this.date = w.date.dayOfMonth();
        homeworks = new LinkedList<Work>;
    }
    /**
     * Transforme une liste de devoirs(triée) en une liste de sections
     * Une liste de section ne doit pas changer l'ordre des devoirs
     * Une liste de section doit séparer les devoirs par date en faisant apparaitre le mois si différent du précédent (sinon , vaut null)
     * @return  Liste de sections transformée
     */
    static ArrayList<Section> getSections(LinkedList<Work> liste){
        ArrayList<Section> result = new ArrayList<Section>;
        int j=0;
        result.add(0, new Section(liste.get(0)));
        int i=0;
        while(i<liste.size()){
        if(liste.get(i).date.dayOfMonth()==result.get(j).date && liste.get(i).date.monthOfYear()==result.get(j).month ) {
            result.get(j).homeworks.add(liste.get(i));
            i++;
        }
        else {
            j++;
            if(liste.get(i).date.dayOfMonth()!=result.get(j).date && liste.get(i).date.monthOfYear()==result.get(j).month) {
                result.add(j, new Section(liste.get(i), false)); //avec month null
                result.get(j).homeworks.add(liste.get(i));
                i++;
            }
            else {
                result.add(j, new Section(liste.get(i))); //avec month qui n'est pas nul null
                result.get(j).homeworks.add(liste.get(i));
                i++;
            }

            }

        }
        return result;
    }
}