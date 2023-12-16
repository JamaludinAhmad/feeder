package com.example.feeder_importer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Setter @Getter @NoArgsConstructor @AllArgsConstructor @ToString
public class AktivitasMahasiswa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String idProdi;
    private String namaProdi;
    private String semester;
    private String noSkTugas;
    private String jenisAnggota;
    private String tanggalTugas;
    private String jenisAktivitas;
    private String namaAktivitas;
    private String judul;
    private String keterangan;
    private String lokasi;
    private String tanggalMulai;
    private String tanggalAkhir;

    private String idAktivitas;
    private boolean succes;

}