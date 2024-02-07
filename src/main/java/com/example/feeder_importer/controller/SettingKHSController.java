package com.example.feeder_importer.controller;


import com.example.feeder_importer.entity.Prodi;
import com.example.feeder_importer.service.ProdiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
@Controller
@RequestMapping("/laporan/setting-khs")
public class SettingKHSController {

    @Autowired
    private ProdiService prodiService;
    @GetMapping
    public String view(Model model){
        List<Prodi> prodiList =  prodiService.getAllProdi();

        model.addAttribute("prodiList", prodiList);
        return "laporan/setting-khs";
    }

}
