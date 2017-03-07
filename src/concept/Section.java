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


    /**
     * Transforme une liste de devoirs(triée) en une liste de sections
     * Une liste de section ne doit pas changer l'ordre des devoirs
     * Une liste de section doit séparer les devoirs par date en faisant apparaitre le mois si différent du précédent (sinon , vaut null)
     * @return  Liste de sections transformée
     */
    static ArrayList<Section> getSections(LinkedList<Work> liste){
        ArrayList<Section> result = new ArrayList<Section>;
        int j=0;
        result.add(0, new Section());
        int i=0;
        while(i<liste.size()){
        if(liste.get(i).date.dayOfMonth()==result.get(j).date && liste.get(i).date.monthOfYear()==result.get(j).month ) {
            result.get(j).homeworks.add(liste.get(i));
            i++;
        }
        else {
            j++;
            if(liste.get(i).date.dayOfMonth()!=result.get(j).date && liste.get(i).date.monthOfYear()==result.get(j).month) {
                result.add(j, new Section()); //avec month null
                result.get(j).homeworks.add(liste.get(i));
                i++;
            }
            else {
                result.add(j, new Section()); //avec month qui n'est pas nul null
                result.get(j).homeworks.add(liste.get(i));
                i++;
            }

            }

        }
        return result;
    }
}