package com.example.feeder_importer.controller.akm;

import com.example.feeder_importer.config.Akun;
import com.example.feeder_importer.entity.AktivitasMahasiswa;
import com.example.feeder_importer.entity.MahasiswaAkm;
import com.example.feeder_importer.entity.PembimbingAkm;
import com.example.feeder_importer.entity.PengujiAkm;
import com.example.feeder_importer.repo.MahasiswaAkmRepo;
import com.example.feeder_importer.repo.PembimbingAkmRepo;
import com.example.feeder_importer.repo.PengujiAkmRepo;
import com.example.feeder_importer.service.AktivitasMahasiswaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Controller
@RequestMapping("import/akm")
public class AkmViewController {

    private String prodi;

    @Autowired
    private Akun akun;

    @Autowired
    private AktivitasMahasiswaService akmService;

    @Autowired
    private PembimbingAkmRepo pembimbingAkmRepo;

    @Autowired
    private PengujiAkmRepo pengujiAkmRepo;

    @Autowired
    private MahasiswaAkmRepo mahasiswaAkmRepo;


    @GetMapping("/{prodi}/{id}")
    public String edit(@PathVariable("prodi") String prodi, @PathVariable("id") int id, Model model){
        this.prodi = prodi;
        AktivitasMahasiswa akm = akmService.getById(id);

        List<PembimbingAkm> pembimbingAkms = pembimbingAkmRepo.findAllByIdAktivitasDb(id);
        List<PengujiAkm> pengujiAkms = pengujiAkmRepo.findAllByIdAktivitasDb(id);
        List<MahasiswaAkm> mahasiswaAkms = mahasiswaAkmRepo.findAllByIdAktivitasDb(id);

        model.addAttribute("pbbAkms", pembimbingAkms);
        model.addAttribute("pngjAkms", pengujiAkms);
        model.addAttribute("mhsAkms", mahasiswaAkms);

        model.addAttribute("akm", akm);
        model.addAttribute("id", id);
        return "importer/aktivitas_mahasiswa/akm_edit";
    }

    @PostMapping("{prodi}/{id}")
    public String importData(@RequestParam("file") MultipartFile file, @PathVariable("prodi") String prodi, @PathVariable("id") int id) throws IOException {
        //lakukan import data dari excel ke database
        akmService.importExcelView(file, id);

        return "redirect:/import/akm/" + prodi + "/" + id;
    }

    @PostMapping("{prodi}/{id}/import")
    public String exportData(@PathVariable("id") int id) throws JsonProcessingException {
        //lakukan export ke neofeeder
        akmService.importNeoView(id);

        return "redirect:/import/akm/" + prodi + "/" + id;
    }

}
