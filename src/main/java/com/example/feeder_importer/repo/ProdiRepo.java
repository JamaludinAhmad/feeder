package com.example.feeder_importer.repo;

import com.example.feeder_importer.entity.Prodi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdiRepo extends JpaRepository<Prodi, Integer> {
    Prodi findByIdProdi(String prodi);

}
