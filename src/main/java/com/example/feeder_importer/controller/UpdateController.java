package com.example.feeder_importer.controller;

import com.example.feeder_importer.config.Akun;
import com.example.feeder_importer.entity.Prodi;
import com.example.feeder_importer.repo.ProdiRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/setting")
public class UpdateController {


    @Autowired
    private Akun akun;

    @Autowired
    private ProdiRepo prodiRepo;

    @GetMapping
    public String view(){
        return "setting";
    }

    @PostMapping
    public String update() throws JsonProcessingException {
        akun.login();
        akun.setAct("""
                "act" : "GetProdi",
                "filter":"",
                "order":"",
                "limit":"",
                "offset":"0"
                """);
        JSONObject response = akun.post();
        ObjectMapper obj = new ObjectMapper();
        JsonNode jsonNode = obj.readTree(response.toString());
        JsonNode datas = jsonNode.get("data");
        System.out.println(datas);

        JSONArray dataArray = new JSONArray(datas.toString());


        for(Object prodi : dataArray){
            JSONObject dataprodi = (JSONObject) prodi;

            Prodi newProdi = new Prodi();
            newProdi.setIdProdi(dataprodi.getString("id_prodi"));
            newProdi.setKodeProdi(dataprodi.getString("kode_program_studi"));
            newProdi.setNamaProdi(dataprodi.getString("nama_program_studi"));
            newProdi.setNamaJenjang(dataprodi.getString("nama_jenjang_pendidikan"));

            prodiRepo.save(newProdi);
        }

        return "redirect:/setting";
    }
}
