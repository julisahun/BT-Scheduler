package scheduler;

import javafx.util.Pair;

import java.util.ArrayList;

public class Grup {

    private int id;
    private String nom;
    private ArrayList<ArrayList<Pair<Double,Double>>> hores;

    public Grup(){
        id = -1;
        hores = new ArrayList<>();
    }

    public Grup(Grup g)
    {
        this.id = g.getId();
        this.nom = g.getNom();
        hores = new ArrayList<>();
        hores.addAll(g.getHores());
    }

    public Grup(int id, ArrayList<ArrayList<Pair<Double,Double>>> hores){
        this.id = id;
        this.hores = hores;
    }

    public int getId(){return this.id;}

    public String getNom(){return this.nom;}

    public ArrayList<ArrayList<Pair<Double,Double>>> getHores(){return this.hores;}

    public void setId(int id){
        this.id = id;
    }

    public void setNom(String nom){this.nom = nom;}

    public void setHores(ArrayList<ArrayList<Pair<Double,Double>>> hores)
    {
        this.hores = hores;
    }
}
