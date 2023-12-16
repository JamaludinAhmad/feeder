package com.example.feeder_importer.repo;

import com.example.feeder_importer.entity.PengujiAkm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface PengujiAkmRepo extends JpaRepository<PengujiAkm, Integer> {

    List<PengujiAkm> findAllByIdAktivitasDb(int id);
    List<PengujiAkm> findAllByIdAktivitasDbAndSucces(int id, boolean sc);

}
