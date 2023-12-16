package com.example.feeder_importer.repo;

import com.example.feeder_importer.entity.AktivitasMahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface AktivitasMahasiswaRepo extends JpaRepository<AktivitasMahasiswa, Integer> {

    //untuk ngeimport data
    List<AktivitasMahasiswa> findAllByIdProdi(String prodi);

    List<AktivitasMahasiswa> findAllByIdProdiAndSucces(String prodi, boolean succes);

}
