package com.example.feeder_importer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Transcript {
    private String nama_mahasiswa;
    private String ProgramStudi;
    private String NIM;
    private String Semester;
    private String Periode;
    private String idRegistMahasiswa;
    private String ipk;
    private int totalsks;
    private float totalsxi;
    private float totalIpkSxi;
    private float totalallsks;

    ArrayList<ArrayList<String>> score = new ArrayList<>();
    ArrayList<String> ipkScore = new ArrayList<>();

    public void insertRecord(String kode_matkul, String nama_matkul, String SKS,
                      String nilai_angka, String nilai_huruf, String nilai_indeks,
                      String sks_x_index){
        ArrayList<String> record = new ArrayList<>();

        record.add(0, kode_matkul);
        record.add(1, nama_matkul);
        record.add(2, slashdot(SKS));
        addSks(slashdot(SKS));
        record.add(3, nilai_angka);
        record.add(4, nilai_huruf);
        record.add(5, nilai_indeks);
        record.add(6, givetwo(sks_x_index));
        addsksxindex(givetwo(sks_x_index));

        this.score.add(record);

        sortScoreByKodeMatkul();

    }

    public void insertNilaiMatkul(String nilai){
        ipkScore.add(nilai);
    }

    public String getIpk(){
        float finalnilai = totalIpkSxi / totalallsks;
        return String.format("%.2f", finalnilai);
    }

    public void sortScoreByKodeMatkul() {
        Collections.sort(score, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return o1.get(0).compareTo(o2.get(0)); // Mengurutkan berdasarkan kode mata kuliah (indeks 0)
            }
        });
    }

    public String sxi(){
        return String.format("%.2f", totalsxi);
    }

    public void addSks(String sks){
        if(Objects.equals(sks, "null")) return;
        totalsks += Integer.parseInt(sks);
    }

    public String getIps(){
        float x = Float.parseFloat(sxi()) / totalsks;
        return String.format("%.2f", x);
    }

    public void addsksxindex(String sxs){
        if(Objects.equals(sxs, "null")) return;
        totalsxi += Float.parseFloat(sxs);
    }

    public void addSksIpk(String sks, String indeks){
        if(Objects.equals(sks, "null")) return;
        totalIpkSxi += (Float.parseFloat(sks) * Float.parseFloat(indeks));
        totalallsks += (Float.parseFloat(sks));
        System.out.println(sks + "*" + indeks + "=" + totalIpkSxi);
    }

    public String givetwo(String originalString){
        if(Objects.equals(originalString, "null")) return "null";

        // Create a DecimalFormat object with the desired format
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        // Parse the original string as a double
        double doubleNumber = Double.parseDouble(originalString);

        // Format the double without decimal places
        String resultString = decimalFormat.format(doubleNumber);

        return resultString;
    }

    public String slashdot(String originalString){

        if(Objects.equals(originalString, "null")) return "null";

        // Create a DecimalFormat object with the desired format
        DecimalFormat decimalFormat = new DecimalFormat("#");

        // Parse the original string as a double
        double doubleNumber = Double.parseDouble(originalString);

        // Format the double without decimal places
        String resultString = decimalFormat.format(doubleNumber);

        return resultString;
    }
}

