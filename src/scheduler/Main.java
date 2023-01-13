package scheduler;

import com.opencsv.exceptions.CsvValidationException;
import javafx.util.Pair;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;

public class Main {

    static private int Nassig;
    static private LinkedList<String> Assignatures_obligatories;

    static private ArrayList<Assignatura> horaris;

    static private PriorityQueue<Horari> horari_final;
    static private double max = -1;

    public static void main(String[] args) {

        try {
            CSVReader reader = new CSVReader(new FileReader("../../../data/file.csv"));
            fill(reader);
            omplir_preferencies();

            CreaHorari();
            print();


        }catch(IOException ioe){
            ioe.printStackTrace();
            System.out.println("no s'ha pogut llegir el fitxer");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Exepcio");
        }


    }

    private static void print() {
        Scanner s = new Scanner(System.in);
        while(!horari_final.isEmpty())
        {
            Horari h = horari_final.poll();
            System.out.println("NOTA = " + h.getEval() + " ");
            for (int i = 0; i < h.getHorari().size(); i++) System.out.print(h.getHorari().get(i).getNom() + " " + (h.getHorari().get(i).getId()+1) + "   ");
            System.out.println();
            h.print();
            System.out.println();

            s.nextLine();
        }
    }

    private static void CreaHorari() {
        Horari h = new Horari();
        Horari.setNassig(Nassig);
        CreaHorarirec(0,h);
    }

    private static void CreaHorarirec(int i, Horari h) {

        if (i == horaris.size()) evalua(h);
        else {
            if (Assignatures_obligatories.isEmpty() || !Assignatures_obligatories.contains(horaris.get(i).getNom()))
            {
                CreaHorarirec(i+1,h);
            }
            for (int j = 0; j < horaris.get(i).getNgrups(); j++)
            {
                if (h.addgrup(horaris.get(i).getGrups().get(j))) {
                    CreaHorarirec(i + 1, h);
                    h.delGrup(horaris.get(i).getGrups().get(j));
                }
            }
        }
    }

    private static void evalua(Horari h) {
        Horari h2 = new Horari(h);

        h2.evalua(Assignatures_obligatories);
        if (h2.getEval() != -1) horari_final.add(h2);
    }

    private static void omplir_preferencies() {

        Assignatures_obligatories = new LinkedList<>();
        System.out.println("Quantes assignatures vols fer?");
        Scanner s = new Scanner(System.in);
        Nassig = s.nextInt();
        System.out.println("Quines assignatures vols fer segur?");

        String assig = s.nextLine();
        assig = s.nextLine();
        String[] aux = assig.split(",");
        Collections.addAll(Assignatures_obligatories, aux);

        System.out.println("A quina hora maxima vols marxar?");
        Horari.setHMax(s.nextLine());

        System.out.println("quin pes li vols donar al seguents parametres?");
        System.out.println("tenir poques hores mortes: ");
        Horari.setP1(s.nextDouble());
        System.out.println("tenir cada dia les mateixes hores");
        Horari.setP2(s.nextDouble());
        System.out.println("Sortir d'hora");
        Horari.setP3(s.nextDouble());
        System.out.println("tenir un dia lliure");
        Horari.setP4(s.nextDouble());
    }

    private static void fill(CSVReader reader) throws CsvValidationException, IOException {
        String[] linea;
        horaris = new ArrayList<>();
        horari_final = new PriorityQueue<>((o1, o2) -> {
            if (Objects.equals(o1,o2)) return 0;
            if (o2.getEval() > o1.getEval()) return 1;
            return -1;
        });
        while ((linea = reader.readNext()) != null)
        {
            Assignatura a = new Assignatura();
            a.setNom(linea[0]);
            a.setNgrups(linea.length-2);
            ArrayList<Grup> grups = new ArrayList<>();
            for (int i = 1; i < linea.length-1; i++)
            {
                Grup g = new Grup();
                g.setId(i-1);
                g.setNom(linea[0]);
                String[] hores = linea[i].split(";");
                ArrayList<ArrayList<Pair<Double,Double>>> dies = new ArrayList<>();
                for (int j = 0; j < 5; j++)
                {
                    if (Objects.equals(hores[j], "-1")) dies.add(new ArrayList<>());
                    else {
                        ArrayList<Pair<Double,Double>> dia = new ArrayList<>();
                        String[] classes = hores[j].split("&");
                        for (String aClass : classes) {

                            String[] inifinal = aClass.split("-");
                            String[] inis = inifinal[0].split(":");
                            String[] fins = inifinal[1].split(":");
                            double ini = Integer.parseInt(inis[0]);
                            double fin = Integer.parseInt(fins[0]);
                            if (inis.length > 1) ini += Integer.parseInt(inis[1]) / 15 * 0.25;
                            if (fins.length > 1) fin += Integer.parseInt(fins[1]) / 15 * 0.25;
                            dia.add(new Pair<>(ini, fin));
                        }
                        dies.add(dia);
                    }
                }
                g.setHores(dies);
                grups.add(g);
            }
            a.setHorari(grups);
            horaris.add(a);
        }
    }
}