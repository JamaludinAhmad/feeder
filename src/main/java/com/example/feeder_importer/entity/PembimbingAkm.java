package com.example.feeder_importer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PembimbingAkm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int idAktivitasDb;

    private String namaDosen;
    private String nidnDosen;
    private String idDosen;
    private String kategori;
    private String pembimbingKe;
    private String namakegiatan;

    private boolean succes;
}
