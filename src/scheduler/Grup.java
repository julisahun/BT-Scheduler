package scheduler;

import org.apache.commons.lang3.Pair;

import java.util.ArrayList;

public class Grup {

    private String id;
    private String nom;
    private ArrayList<ArrayList<Pair<Double,Double>>> hores;

    public Grup(){
        id = "";
        hores = new ArrayList<>();
    }

    public Grup(Grup g)
    {
        this.id = g.getId();
        this.nom = g.getNom();
        hores = new ArrayList<>();
        hores.addAll(g.getHores());
    }

    public Grup(String id, ArrayList<ArrayList<Pair<Double,Double>>> hores){
        this.id = id;
        this.hores = hores;
    }

    public String getId(){return this.id;}

    public String getNom(){return this.nom;}

    public ArrayList<ArrayList<Pair<Double,Double>>> getHores(){return this.hores;}

    public void setId(String id){
        this.id = id;
    }

    public void setNom(String nom){this.nom = nom;}

    public void setHores(ArrayList<ArrayList<Pair<Double,Double>>> hores)
    {
        this.hores = hores;
    }
}
