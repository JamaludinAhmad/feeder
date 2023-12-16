package com.example.feeder_importer.controller.akm;

import com.example.feeder_importer.entity.AktivitasMahasiswa;
import com.example.feeder_importer.entity.Prodi;
import com.example.feeder_importer.service.AktivitasMahasiswaService;
import com.example.feeder_importer.service.ProdiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Controller
@RequestMapping("/import")
public class AkmListController {

    private final String url = "akm";

    private String prodi;
    @Autowired
    private  ProdiService prodiService;

    @Autowired
    private AktivitasMahasiswaService akmService;
    @GetMapping
    public String view(Model model){
        model.addAttribute("url", url);
        List<Prodi> program = prodiService.getAllProdi();
        model.addAttribute("prodi", program);
        return "list_prodi";
    }

    @GetMapping("/akm/{prodi}")
    public String viewAkm(@PathVariable("prodi") String prodi, Model model){
        this.prodi = prodi;
        model.addAttribute("prodi", this.prodi);
        List<AktivitasMahasiswa> akm = akmService.getAllByProdi(prodi);
        model.addAttribute("aktivitas_mahasiswa", akm);
        return "importer/aktivitas_mahasiswa/akm_list";
    }

    //dari excel ke databse
    @PostMapping("/akm/{prodi}")
    public String importData(@RequestParam("file") MultipartFile file){
        akmService.importExcel(file, prodi);
        return "redirect:/import/akm/" + prodi;
    }

    //ke neofeeder
    @PostMapping("/akm/{prodi}/import")
    public String neoFeederImport() throws JsonProcessingException {
        akmService.importNeo(prodi);
        return "redirect:/import/akm/" + prodi;
    }



            


}
