package com.example.feeder_importer.service;

import com.example.feeder_importer.entity.Prodi;
import com.example.feeder_importer.repo.ProdiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdiService {

    @Autowired
    ProdiRepo prodiRepo;


    public List<Prodi> getAllProdi(){
        return prodiRepo.findAll();
    }

    public Prodi getProdiByIdProdi(String prodi){return prodiRepo.findByIdProdi(prodi);}

}
