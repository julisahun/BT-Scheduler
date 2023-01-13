package scheduler;


import org.apache.commons.lang3.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class Horari {
    private static int Nassig;
    private int assig;
    private double eval;

    private static double p1;
    private static double p2;
    private static double p3;
    private static double p4;
    private static double maxHora;
    private static final String[] dies_setmana = {"Dilluns", "Dimarts" , "Dimecres", "Dijous", "Divendres"};


    private ArrayList<Grup> horari;

    public Horari(){
        this.horari = new ArrayList<>();
        this.assig = 0;
    }

    public Horari(Horari h)
    {
        this.assig = h.getAssig();
        horari = new ArrayList<>();
        for (Grup g : h.getHorari())
        {
            horari.add(new Grup(g));
        }
        eval = h.getEval();
    }

    public static void setHMax(String nextLine) {
        String[] hora = nextLine.split(":");
        try{
            maxHora = Integer.parseInt(hora[0]);
            if (hora.length > 1) maxHora += Integer.parseInt(hora[1]) / 15 * 0.25;
        }
        catch(NumberFormatException nfe)
        {
            maxHora = Integer.MAX_VALUE;
        }
    }

    public ArrayList<Grup> getHorari() {
        return this.horari;
    }

    public static int getNassig() {return Nassig;}

    public static void setNassig(int n){Nassig = n;}

    public void setAssig(int n) {this.assig = n;}

    public int getAssig(){return this.assig;}

    public void setHorari(ArrayList<Grup> horari) {
        this.horari = horari;
    }

    public boolean addgrup(Grup g)
    {
        if (assig == Nassig) return false;
        for (int i = 0; i < 5; i++)
        {
            for (Grup g2 : horari)
            {
                if (!valid(g2.getHores().get(i), g.getHores().get(i))) return false;
            }
        }
        assig++;
        horari.add(g);
        return true;
    }



    public void delGrup(Grup g)
    {
        if (horari.contains(g)) {
            horari.remove(g);
            assig--;
        }
    }

    public static void setP1(double p1)
    {
        Horari.p1 = p1;
    }

    public static void setP2(double p2)
    {
        Horari.p2 = p2;
    }

    public static void setP3(double p3)
    {
        Horari.p3 = p3;
    }

    public static void setP4(double p4)
    {
        Horari.p4 = p4;
    }

    private static String toHour(Double d)
    {
        Integer hora = d.intValue();
        Double minuts = d-hora;
        String ret = hora.toString();
        ret += ":" + (Double.toString(minuts/2.5 * 15).replace(".",""));
        return ret;
    }

    private ArrayList<Pair<String,Pair<Double, Double>>> ordena(int dia)
    {
        LinkedList<Pair<String,Pair<Double, Double>>> ret = new LinkedList<>();
        for (Grup g : horari)
        {
            for (Pair<Double,Double> p : g.getHores().get(dia)) {
                ret.add(new Pair(g.getNom(), p));
            }
        }
        ret.sort((o1, o2) -> {
            if (o1.equals(o2)) return 0;
            if (o1.right.left > o2.right.left) return 1;
            else return -1;
        });
        return new ArrayList<>(ret);
    }

    public void print()
    {
        for (int i = 0; i < 5; i++)
        {
            System.out.println(dies_setmana[i]);
            ArrayList<Pair<String,Pair<Double, Double>>> dia = ordena(i);
            for (Pair<String,Pair<Double, Double>> var : dia)
            {
                System.out.println(toHour(var.right.left) + "-" + toHour(var.right.right) + " " + var.left);
            }
            System.out.println();
        }
    }

    public double getEval(){return this.eval;}

    private boolean solapament(Pair<Double, Double> hores1, Pair<Double, Double> hores2) {
        if (hores1.right > maxHora || hores2.right > maxHora) return true;
        if (hores1.left == -1 || hores2.left == -1) return false;
        return hores1.right > hores2.left && hores2.right > hores1.left;
    }

    private boolean dins(Pair<Double, Double> hores1, Pair<Double, Double> hores2) {
        return (hores1.left <= hores2.left && hores1.right >= hores2.right || hores2.left <= hores1.left && hores2.right >= hores1.right);
    }

    private boolean valid(ArrayList<Pair<Double, Double>> pairs, ArrayList<Pair<Double, Double>> pairs1) {
        return (!solapamentDies(pairs,pairs1) && noMaxHora(pairs,pairs1));
    }

    private boolean noMaxHora(ArrayList<Pair<Double, Double>> pairs, ArrayList<Pair<Double, Double>> pairs1) {
        for (Pair<Double,Double> p : pairs)
        {
            if (p.right > maxHora) return false;
        }
        for (Pair<Double,Double> p : pairs1)
        {
            if (p.right > maxHora) return false;
        }
        return true;
    }

    private boolean solapamentDies(ArrayList<Pair<Double, Double>> hores1, ArrayList<Pair<Double, Double>> hores2) {
        for (Pair<Double,Double> p : hores1)
        {
            for (Pair<Double,Double> p2 : hores2)
            {
                if (solapament(p,p2)) return true;
            }
        }
        return false;
    }

    public void evalua(LinkedList<String> assignatures_obligatories) {
        for (String s : assignatures_obligatories)
        {
            if (s.equals("")) continue;
            boolean ok = false;
            for (Grup g : horari) if (Objects.equals(g.getNom(), s)) {ok = true; break;}
            if (!ok) {eval = -1; return;}
        }
        if (Nassig > assig) {eval = -1;return;}
        int hores_mortes = 0;
        int hores_equilibrades;
        double sortir_dhora = Double.MAX_VALUE;
        boolean dia_lliure = false;
        int[] hores_dia = new int[5];
        for (int i = 0; i < 5; i++)
        {
            int hores = 0;
            ArrayList<Pair<String,Pair<Double, Double>>> dia = ordena(i);
            for (int j = 1; j < dia.size(); j++)
            {
                hores_mortes += Math.abs(dia.get(j-1).right.right - dia.get(j).right.left);
                hores += dia.get(j-1).right.right - dia.get(j-1).right.left;
            }
            hores_dia[i] = hores;
            if (dia.size() == 0) dia_lliure = true;
            else sortir_dhora = Math.max(sortir_dhora,dia.get(dia.size()-1).right.right);
        }
        double val_hores_equilibrades = desviacio(hores_dia);
        double extra;
        if (dia_lliure) extra = p4;
        else extra = 1-p4;
        eval = p1/(hores_mortes+1) + p2/(val_hores_equilibrades+1) + p3*4/sortir_dhora + extra;
    }

    private double desviacio(int[] hores_dia) {
        double mean;
        int sum = 0;
        for(int i = 0; i < 5; i++)
        {
            sum += hores_dia[i];
        }
        mean = sum*1.0 / 5;

        double d = 0;
        for (int i = 0; i < 5; i++)
        {
            d += Math.pow(mean- hores_dia[i],2);
        }
        return d;
    }
}
