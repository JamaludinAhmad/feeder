package com.example.feeder_importer.controller;

import com.example.feeder_importer.entity.Prodi;
import com.example.feeder_importer.entity.Transcript;
import com.example.feeder_importer.service.KHSService;
import com.example.feeder_importer.service.ProdiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.*;

@Controller
@RequestMapping("/laporan/khs")
public class KHSController {

    @Autowired
    private ProdiService prodiService;

    @Autowired
    private KHSService khsService;
    @GetMapping
    public String view(Model model){
        List<Prodi> prodiList = prodiService.getAllProdi();
        model.addAttribute("prodiList",prodiList);

        Map<Integer, String> periode = new LinkedHashMap<>();
        int startYear = 1980;
        int currentYear = Integer.parseInt(String.valueOf(Year.now()));
        model.addAttribute("year", currentYear);

        for(int i = currentYear ; i >= startYear; i--){
            for(int j = 1; j <= 3; j++){

                int per = Integer.parseInt(Integer.toString(i) + j);

                if(j == 1){
                    periode.put(per, i + "/" + (i + 1) + " GANJIL");
                }
                else if(j == 2){
                    periode.put(per, i + "/" + (i + 1) + " GENAP");
                }
                else {
                    periode.put(per, i + "/" + (i + 1) + " PENDEK");
                }
            }
        }

        model.addAttribute("periodeList", periode);

        return "laporan/khs_mahasiswa";
    }

    @PostMapping
    public String generateKhs(@RequestParam String namamhs,
                              @RequestParam String prodi,
                              @RequestParam String periode,
                              @RequestParam String semester,
                              Model model) throws JsonProcessingException {
        System.out.println(namamhs);
        System.out.println(prodi);
        System.out.println(periode);

        List<Transcript> transcripts = khsService.getTranscript(prodi, namamhs, periode, false);
        Prodi ketuaProdi = prodiService.getProdiByIdProdi(prodi);
        String namaJenjang = ketuaProdi.getNamaJenjang();
        // Filter transcripts for a specific semester (e.g., semester "3")
        List<Transcript> filteredTranscripts = new ArrayList<>();
        for (Transcript transcript : transcripts) {
            if (transcript.getSemester().startsWith(semester)) {
                filteredTranscripts.add(transcript);
            }
        }

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

        model.addAttribute("transcripts", filteredTranscripts);

        return "laporan/khs_template";
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
