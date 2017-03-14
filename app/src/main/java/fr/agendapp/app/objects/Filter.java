package fr.agendapp.app.objects;

import java.util.List;

/**
 * Created by Charline on 14/03/2017.
 */
public class Filter {

    private List<String> matieres;
    private List<Integer> flag;
    private boolean fait;
    private String research;
    private String auteur;

    public Filter(){
        this.matieres=null;
        this.flag=null;
        this.fait=false;
        this.research=null;
    }

    public List<String> getMatieres() {
        return matieres;
    }

    public List<Integer> getFlag() {
        return flag;
    }

    public boolean isFait() {
        return fait;
    }

    public String getResearch() {
        return research;
    }

    public String getAuteur() {
        return auteur;
    }
}
