package scheduler;

import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    static private int Nassig;
    static private LinkedList<String> Assignatures_obligatories;

    static private ArrayList<Assignatura> horaris;

    static private PriorityQueue<Horari> horari_final;
    static private double max = -1;

    public static void main(String[] args) {

        try {
            while (true) {
                System.out.println("Quin fitxer vols llegir? (CVS o JSON)");
                Scanner s = new Scanner(System.in);
                String type = s.nextLine();
                if (Objects.equals(type, "CSV")) {
                    CSVReader reader = new CSVReader(new FileReader("../../../data/file.csv"));
                    fillCSV(reader);
                    break;
                } else if (Objects.equals(type, "JSON")) {
                    String data = new String(Files.readAllBytes(Paths.get("data/file.json")));
                    JSONObject json = new JSONObject(data);
                    fillJSON(json);
                    break;
                }
            }
            System.out.println("------- Assignatures llegides correctament -------");
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
        while(true)
        {
            System.out.println();
            Horari h = horari_final.poll();
            System.out.println("NOTA = " + h.getEval() + " ");
            for (int i = 0; i < h.getHorari().size(); i++) System.out.print(h.getHorari().get(i).getNom() + " " + (h.getHorari().get(i).getId()) + "   ");
            System.out.println();
            System.out.println();
            h.print();
            System.out.println();
            if (horari_final.isEmpty()) break;
            System.out.println("----- Enter per veure mes horaris -----");
            s.nextLine();
        }
        System.out.println("------ Enter per sortir del programa -----");
        s.nextLine();
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
        for(String a : aux) {
            a = a.trim().toLowerCase();
            for(Assignatura h : horaris) {
                if(Objects.equals(h.getNom(), a)){
                    Assignatures_obligatories.add(a);
                    break;
                }
            }
        }
        System.out.println("A quina hora maxima vols marxar?");
        Horari.setHMax(s.nextLine());
        while(true) {
            System.out.println("vols omplir preferencies? (Y/N)");
            String res = s.nextLine();
            if (Objects.equals(res, "Y")) break;
            if (Objects.equals(res, "N")) return;
        }
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

    private static void fillJSON(JSONObject json) {
        String[] DAYSnames = {"monday", "tuesday", "wednesday", "thursday", "friday"};
        horaris = new ArrayList<>();
        horari_final = new PriorityQueue<>((o1, o2) -> {
            if (Objects.equals(o1,o2)) return 0;
            if (o2.getEval() > o1.getEval()) return 1;
            return -1;
        });
        for (Iterator<String> it = json.keys(); it.hasNext();) {
            String assigName = it.next();

            JSONObject groups = (JSONObject) json.get(assigName);
            Assignatura a = new Assignatura();
            a.setNom(assigName.toLowerCase());
            a.setNgrups(groups.length());
            ArrayList<Grup> assigGrup = new ArrayList<>();
            int i = 0;
            for (Iterator<String> it2 = groups.keys(); it2.hasNext();i++) {
                String groupName = it2.next();
                JSONObject days = (JSONObject) groups.get(groupName);

                Grup g = new Grup();
                g.setId(groupName);
                g.setNom(assigName);
                ArrayList<ArrayList<Pair<Double,Double>>> dies = new ArrayList<>();
                for (String dayName : DAYSnames) {
                    if (!days.has(dayName)) {
                        dies.add(new ArrayList<>());
                        continue;
                    }
                    ArrayList<Pair<Double, Double>> hours = new ArrayList<>();
                    JSONArray day = (JSONArray) days.get(dayName);
                    for (int j = 0; j < day.length(); j++) {
                        String[] inifinal = ((String) day.get(j)).split("-");
                        String[] inis = inifinal[0].split(":");
                        String[] fins = inifinal[1].split(":");
                        double ini = Integer.parseInt(inis[0]);
                        double fin = Integer.parseInt(fins[0]);
                        if (inis.length > 1) ini += Integer.parseInt(inis[1]) / 15 * 0.25;
                        if (fins.length > 1) fin += Integer.parseInt(fins[1]) / 15 * 0.25;
                        hours.add(new Pair<>(ini, fin));
                    }
                    dies.add(hours);
                }
                g.setHores(dies);
                assigGrup.add(g);
            }
            a.setHorari(assigGrup);
            horaris.add(a);
        }
    }

    private static void fillCSV(CSVReader reader) throws CsvValidationException, IOException {
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
            a.setNom(linea[0].toLowerCase());
            a.setNgrups(linea.length-2);
            ArrayList<Grup> grups = new ArrayList<>();
            for (int i = 1; i < linea.length-1; i++)
            {
                Grup g = new Grup();
                g.setId(Integer.toString((i-1)));
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
