package com.example.feeder_importer.controller;

import com.example.feeder_importer.entity.Prodi;
import com.example.feeder_importer.entity.Transcript;
import com.example.feeder_importer.service.KHSService;
import com.example.feeder_importer.service.ProdiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("laporan/temp_transcript")
public class TempTranscript {

    @Autowired
    private ProdiService prodiService;

    @Autowired
    private KHSService khsService;

    @GetMapping
    public String view (Model mode){
        return "laporan/transcript/temp_transcript";
    }

    @PostMapping
    public String generateKhs(@RequestParam String namamhs,
                              @RequestParam String prodi, //ini id_prodi yak
                              Model model) throws JsonProcessingException {
        System.out.println(namamhs);
        System.out.println(prodi);

        List<Transcript> transcripts = khsService.getTranscript("", namamhs, "0", true);
        String pprodi = transcripts.get(0).getIdProdi();
        Prodi ketuaProdi = prodiService.getProdiByIdProdi(pprodi);
        String namaJenjang = ketuaProdi.getNamaJenjang();

        String singkatan = singkatkan(ketuaProdi.getNamaProdi());
        if(!(singkatan.equals("HK(S"))){
            model.addAttribute("singkatan", ketuaProdi.getNamaProdi()  + " (" + singkatan + ")") ;
        }
        else{
            model.addAttribute("singkatan", ketuaProdi.getNamaProdi());
        }
        String finalJenjang = Objects.equals(namaJenjang, "S1") ? "Sarjana" : "Magister";
        model.addAttribute("namaJenjang", finalJenjang);
        model.addAttribute("ketua", ketuaProdi);

        model.addAttribute("transcripts", transcripts);

        return "laporan/transcript/sementara_transcript";
    }

    public static String singkatkan(String teks) {
        String[] kata = teks.split(" ");
        StringBuilder singkat = new StringBuilder();

        for (String kataAwal : kata) {
            singkat.append(kataAwal.charAt(0));
        }

        return singkat.toString();
    }



}
