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

    public List<Transcript> getTranscript(String prodi, String namamhs, String periode, Boolean temptranscript) throws JsonProcessingException {
//        akun.login();
        if (!namamhs.isEmpty() && !temptranscript) {
            akun.setAct("""
                    "act" : "GetRekapKHSMahasiswa",
                    "filter" : "id_prodi = '%s' AND id_periode = '%s' AND NIM = '%s'"
                    """.formatted(prodi, periode, namamhs));
            System.out.println("A");
        }

        else if(temptranscript && Integer.parseInt(periode) == 0){
            System.out.println("B");
            akun.setAct("""
                "act" : "GetListMahasiswa",
                "filter" : "nipd = '%s'"
                """.formatted(namamhs));

            JSONObject respond = akun.post();

            ObjectMapper obj = new ObjectMapper();
            JsonNode jsonNode = obj.readTree(respond.toString());
            JSONArray data_mahasiswa = new JSONArray(jsonNode.get("data").toString());
            JSONObject mhs = data_mahasiswa.getJSONObject(0);
            System.out.println(data_mahasiswa);
            prodi = mhs.getString("id_prodi");

            akun.setAct("""
                    "act" : "GetRekapKHSMahasiswa",
                    "filter" : "id_prodi = '%s' AND NIM = '%s'"
                    """.formatted(prodi, namamhs));
        }

        else {
            System.out.println("C");
            akun.setAct("""
                    "act" : "GetRekapKHSMahasiswa",
                    "filter" : "id_prodi = '%s' AND id_periode = '%s'"
                    """.formatted(prodi, periode));
        }


        JSONObject respond = akun.post();

        ObjectMapper obj = new ObjectMapper();
        JsonNode jsonNode = obj.readTree(respond.toString());
        JSONArray datas = new JSONArray(jsonNode.get("data").toString());

        List<Transcript> transcripts = new ArrayList<>();
        Transcript transcript = new Transcript();

        for (int i = 0; i < datas.length(); i++) {

            JSONObject dataObject = datas.getJSONObject(i);

            transcript.setNIM(dataObject.getString("nim"));
            transcript.setNama_mahasiswa(dataObject.getString("nama_mahasiswa"));
            transcript.setPeriode(dataObject.getString("nama_periode"));
            String idRegist = dataObject.getString("id_registrasi_mahasiswa");
            transcript.setIdRegistMahasiswa(idRegist);

            int angkatan = Integer.parseInt(dataObject.getString("angkatan"));
            int periode1 = Integer.parseInt(dataObject.getString("id_periode"));
            int postper = periode1 % 10;
            int semester = ((periode1 / 10) - angkatan) * 2 + postper;
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
            if (i != datas.length() - 1) {
                next_name = datas.getJSONObject(i + 1).getString("nama_mahasiswa");
            }

            if (!Objects.equals(current_name, next_name)) {

                akun.setAct("""
                        "act":"GetTranskripMahasiswa",
                        "filter":"id_registrasi_mahasiswa = '%s'"
                        """.formatted(idRegist));

                JSONObject ipkRespond = akun.post();

                JsonNode ipkJson = obj.readTree(ipkRespond.toString());
                JSONArray ipkData = new JSONArray(ipkJson.get("data").toString());

                for (int j = 0; j < ipkData.length(); j++) {
                    JSONObject matkul = ipkData.getJSONObject(j);
                    if (Integer.parseInt(matkul.getString("smt_diambil")) <= semester) {
                        System.out.print(semester + " and" + matkul.getString("smt_diambil") + " -> ");

                        String nilai = matkul.getString("nilai_indeks");
                        String sks = matkul.getString("sks_mata_kuliah");

                        transcript.addSksIpk(sks, nilai);
                        transcript.insertNilaiMatkul(nilai);
//                    }
                }


                transcripts.add(transcript);
                transcript = new Transcript();
            }
        }
        return transcripts;
    }


//    public List<Transcript> getTempTranscript(String prodi, String NIM) throws JsonProcessingException {
////        if(!prodi.isEmpty()){
//            akun.setAct("""
//                "act" : "GetListMahasiswa",
//                "filter" : "nipd = '%s'"
//                """.formatted(NIM));
////        }
////        else{
////            akun.setAct("""
////                "act" : "GetListMahasiswa",
////                "filter" : "id_prodi = '%s'"
////                """.formatted(prodi));
////        }
//
//        J
//        JSONArray data_mahasiswa = new JSONArray(jsonNode.get("data").toString());
//SONObject respond = akun.post();
////
////        ObjectMapper obj = new ObjectMapper();
////        JsonNode jsonNode = obj.readTree(respond.toString());
////        System.out.println("EAA");
////        System.out.println(jsonNode.toString());
//        JSONObject mhs = data_mahasiswa.getJSONObject(0);
//        Transcript transcript_mhs = new Transcript();
//
//        transcript_mhs.setNIM(mhs.getString("NIM"));
//        transcript_mhs.setNama_mahasiswa(mhs.getString("nama_mahasiswa"));
//        String id_regist = mhs.getString("id_registrasi_mahasiswa");
//        transcript_mhs.setIdRegistMahasiswa(id_regist);
//
//        int angkatan = Integer.parseInt(mhs.getString("angkatan"));
//        int periode1 = Integer.parseInt(mhs.getString("id_periode"));
//        int postper = periode1 % 10;
//        int semester = ((periode1 / 10) - angkatan) * 2 + postper;
//        transcript_mhs.setSemester(String.valueOf(semester));
//        transcript_mhs.setProgramStudi(ref.getNamaProdi(prodi));
//
//        List<Transcript> transcripts = new ArrayList<>();
//
//
//        return null; //TODO: don't forget this
//    }

}
