package com.example.feeder_importer.service;

import com.example.feeder_importer.config.Akun;
import com.example.feeder_importer.config.Referensi;
import com.example.feeder_importer.entity.Transcript;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class KHSService {

    @Autowired
    private Akun akun;

    @Autowired
    private Referensi ref;

    public List<Transcript> getTranscript(String prodi, String namamhs, String periode) throws JsonProcessingException {
//        akun.login();

        if(!namamhs.isEmpty()){
            akun.setAct("""
                "act" : "GetRekapKHSMahasiswa",
                "filter" : "id_prodi = '%s' AND id_periode = '%s' AND NIM = '%s'"
                """.formatted(prodi,periode, namamhs));
        }
        else{
            akun.setAct("""
                    "act" : "GetRekapKHSMahasiswa",
                    "filter" : "id_prodi = '%s' AND id_periode = '%s'"
                    """.formatted(prodi,periode));
        }


        JSONObject respond = akun.post();

        ObjectMapper obj = new ObjectMapper();
        JsonNode jsonNode = obj.readTree(respond.toString());
        JSONArray datas = new JSONArray(jsonNode.get("data").toString());

        List<Transcript> transcripts = new ArrayList<>();
        Transcript transcript = new Transcript();

        for(int i = 0; i < datas.length(); i++){

            JSONObject dataObject = datas.getJSONObject(i);

            transcript.setNIM(dataObject.getString("nim"));
            transcript.setNama_mahasiswa(dataObject.getString("nama_mahasiswa"));
            transcript.setPeriode(dataObject.getString("nama_periode"));

            int angkatan = Integer.parseInt(dataObject.getString("angkatan"));
            int periode1 = Integer.parseInt(dataObject.getString("id_periode")) ;
            int postper = periode1 % 10;
            int semester = ((periode1/10) - angkatan) * 2 + postper;
            transcript.setSemester(String.valueOf(semester));
            transcript.setProgramStudi(ref.getNamaProdi(prodi));

            String kode_matkul = ref.getKodeMatkul(dataObject.getString("id_matkul"));
            String nama = dataObject.getString("nama_mata_kuliah");
            String sks_matkul = dataObject.getString("sks_mata_kuliah");
            String nilai_angka = dataObject.isNull("nilai_angka") ? "null" : dataObject.getString("nilai_angka");
            String nilai_huruf = dataObject.isNull("nilai_huruf") ? "null" : dataObject.getString("nilai_huruf");
            String nilai_indeks = dataObject.isNull("nilai_indeks") ? "null" : dataObject.getString("nilai_indeks");
            String sxi = dataObject.isNull("sks_x_indeks") ? "null" : dataObject.getString("sks_x_indeks");

            transcript.insertRecord(
                    kode_matkul,
                    nama,
                    sks_matkul,
                    nilai_angka,
                    nilai_huruf,
                    nilai_indeks,
                    sxi
            );

            String current_name = dataObject.getString("nama_mahasiswa");
            String next_name = null;
            if(i != datas.length() - 1){
                next_name = datas.getJSONObject(i + 1).getString("nama_mahasiswa");
            }

            if(!Objects.equals(current_name, next_name)){
                transcripts.add(transcript);
                transcript = new Transcript();
            }
        }
        return transcripts;
    }
}
