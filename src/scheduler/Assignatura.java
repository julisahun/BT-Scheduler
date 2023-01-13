package scheduler;

import java.util.ArrayList;

public class Assignatura {
    private String nom;
    private Integer Ngrups;
    private ArrayList<Grup> grups;

    public Assignatura(){
        grups = new ArrayList<>();
        nom = "";
        Ngrups = -1;
    }

    public Assignatura(String nom, Integer Ngrups, ArrayList<Grup> grups){
        this.nom = nom;
        this.Ngrups = Ngrups;
        this.grups = grups;
    }

    public String getNom(){return nom;}

    public Integer getNgrups() {return Ngrups;}

    public ArrayList<Grup> getGrups(){
        return this.grups;
    }

    public void setNgrups(Integer Ngrups){this.Ngrups = Ngrups;}

    public void setNom(String nom){this.nom = nom;}

    public void setHorari(ArrayList<Grup> grups)
    {
        this.grups = grups;
    }
}
