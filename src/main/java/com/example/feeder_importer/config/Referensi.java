package com.example.feeder_importer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Referensi {

    @Autowired
    private Akun akun;

    public String getNamaAktivitas(String jenis) throws JsonProcessingException {
        akun.login();
        akun.setAct("""
                    "act" : "GetJenisAktivitasMahasiswa"
                """);
        JSONObject respond = akun.post();

        ObjectMapper obj = new ObjectMapper();

        JsonNode jsonNode = obj.readTree(respond.toString());
        JsonNode datas = jsonNode.get("data");
        JSONArray jsonArray = new JSONArray(datas.toString());

        System.out.println(jsonArray);
        // Extracting id_jenis_aktivitas_mahasiswa values

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String idValue = jsonObject.getString("id_jenis_aktivitas_mahasiswa");
            if(idValue.equals(jenis)){
                return jsonObject.getString("nama_jenis_aktivitas_mahasiswa");
            }
        }
        return "tidak ada";
    }

    public JSONObject getBiodataMahasiswa(String nim) throws JsonProcessingException {
        akun.login();
        akun.setAct("""
                "act" : "GetListMahasiswa",
                "filter": "nim = '%s'"
                """.formatted(nim));
        return getJsonObject();
    }

    public JSONObject getDosen(String nidn) throws JsonProcessingException {
        akun.login();
        akun.setAct("""
                "act" : "GetListDosen",
                "filter": "nidn = '%s'",
                "limit" : "1"
                """.formatted(nidn));
        System.out.println("DOSEEEEEEEEEEEEEEEEEEN");
        return getJsonObject();
    }

    public String getNamaKategoriKegiatan(String id_kategori) throws JsonProcessingException {
        int id = Integer.parseInt(id_kategori);
        akun.login();
        akun.setAct("""
                "act" : "GetKategoriKegiatan",
                "filter" : "id_kategori_kegiatan = '%d'"
                """.formatted(id));
        JSONObject res = getJsonObject();
        return res.getString("nama_kategori_kegiatan");
    }

    private JSONObject getJsonObject() {
        JSONObject res = new JSONObject();
        try{
            JSONObject respond = akun.post();
            System.out.println(akun.getAct());
            System.out.println(res);
            ObjectMapper obj = new ObjectMapper();
            JsonNode node = obj.readTree(respond.toString());
            JSONArray data = new JSONArray(node.get("data").toString());
            res = (JSONObject) data.get(0);
            System.out.println(res);
        } catch(Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }

        return res;
    }

    public String getNamaProdi(String id_prodi) throws JsonProcessingException {
        akun.setAct("""
                "act" : "GetProdi",
                "filter" : "id_prodi = '%s'"
                """.formatted(id_prodi));

        JSONObject respond = getJsonObject();
        return respond.getString("nama_program_studi");

    }

    public String getKodeMatkul(String id_matkul) {
        akun.setAct("""
                "act" : "GetMataKuliah",
                "filter" : "id_matkul = '%s'"
                """.formatted(id_matkul));

        JSONObject res = getJsonObject();
        return res.getString("kode_mata_kuliah");
    }

    public String getNamaKelas(String id_registrasi_mahasiswa, String kode_matkul){
        akun.setAct("""
                "act" : "GetPesertaKelasKuliah",
                "filter" : "id_registrasi_mahasiswa = '%s' AND kode_mata_kuliah='%s'"
                """.formatted(id_registrasi_mahasiswa, kode_matkul));

        JSONObject res = getJsonObject();
        return res.getString("nama_kelas_kuliah");
    }
}
