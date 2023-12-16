package com.example.feeder_importer.repo;

import com.example.feeder_importer.entity.MahasiswaAkm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface MahasiswaAkmRepo extends JpaRepository<MahasiswaAkm, Integer> {
    List<MahasiswaAkm> findAllByIdAktivitasDb(int id);
    List<MahasiswaAkm> findAllByIdAktivitasDbAndSucces(int id, boolean isSucces);
}
