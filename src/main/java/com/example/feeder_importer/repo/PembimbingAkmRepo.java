package com.example.feeder_importer.repo;

import com.example.feeder_importer.entity.PembimbingAkm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface PembimbingAkmRepo extends JpaRepository<PembimbingAkm, Integer> {

    List<PembimbingAkm> findAllByIdAktivitasDb(int id);
    List<PembimbingAkm> findAllByIdAktivitasDbAndSucces(int id, boolean sc);
}
