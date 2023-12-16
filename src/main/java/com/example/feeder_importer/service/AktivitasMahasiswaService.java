package com.example.feeder_importer.service;

import com.example.feeder_importer.config.Akun;
import com.example.feeder_importer.config.Referensi;
import com.example.feeder_importer.entity.*;
import com.example.feeder_importer.repo.AktivitasMahasiswaRepo;
import com.example.feeder_importer.repo.MahasiswaAkmRepo;
import com.example.feeder_importer.repo.PembimbingAkmRepo;
import com.example.feeder_importer.repo.PengujiAkmRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class AktivitasMahasiswaService {

    @Autowired
    private AktivitasMahasiswaRepo akmRepo;

    @Autowired
    private Referensi ref;

    @Autowired
    private ProdiService prodiService;

    @Autowired
    private Akun akun;


    @Autowired
    private PembimbingAkmRepo pembimbingAkmRepo;

    @Autowired
    private PengujiAkmRepo pengujiAkmRepo;

    @Autowired
    private MahasiswaAkmRepo mahasiswaAkmRepo;


    public List<AktivitasMahasiswa> getAll(){
        return akmRepo.findAll();
    }

    public List<AktivitasMahasiswa> getAllByProdi(String prodi){
        return akmRepo.findAllByIdProdi(prodi);
    }


    public AktivitasMahasiswa getById(int id){
        System.out.println("mencari");
        Optional<AktivitasMahasiswa> akm = akmRepo.findById(id);
        AktivitasMahasiswa akmtemp = null;
        if(akm.isPresent()) akmtemp = akm.get();
        System.out.println(akmtemp.getNamaAktivitas());
        return akmtemp;
    }
    public void importExcel(MultipartFile file, String prodi){
        try{
            XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());

            Sheet sheet = wb.getSheetAt(0);

            int rowNum = sheet.getLastRowNum();
            int colNum = sheet.getRow(0).getLastCellNum();

            for(int r = 1; r <= rowNum; r++){
                Row row = sheet.getRow(r);
                Row key = sheet.getRow(0);
                Map<String, String> akt = new HashMap<>();
                for(int c = 0; c < colNum; c++){
                    Cell cell = row.getCell(c);
                    String value;
                    if(cell == null){
                        value = "";
                    }
                    else {
                        value = cell.getStringCellValue();
                    }
                    akt.put(key.getCell(c).getStringCellValue(), value);
                }

                AktivitasMahasiswa newAktivitas = new AktivitasMahasiswa();

                newAktivitas.setSemester(akt.get("semester"));
                newAktivitas.setNoSkTugas(akt.get("no sk tugas"));
                newAktivitas.setTanggalTugas(akt.get("tanggal tugas"));
                String jenis = akt.get("jenis aktivitas");
                newAktivitas.setJenisAktivitas(jenis);
                newAktivitas.setJudul(akt.get("judul"));
                newAktivitas.setKeterangan(akt.get("keterangan"));
                newAktivitas.setLokasi(akt.get("lokasi"));
                newAktivitas.setTanggalMulai(akt.get("tanggal mulai"));
                newAktivitas.setTanggalAkhir(akt.get("tanggal akhir"));
                newAktivitas.setJenisAnggota(akt.get("jenis anggota"));

                newAktivitas.setNamaAktivitas(ref.getNamaAktivitas(jenis));

                Prodi pr = prodiService.getProdiByIdProdi(prodi);
                newAktivitas.setNamaProdi(pr.getNamaProdi());
                newAktivitas.setIdProdi(prodi);

                akmRepo.save(newAktivitas);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void importNeo(String prodi) throws JsonProcessingException {
        System.out.println(akun.getAct());
        akun.login();
        List<AktivitasMahasiswa> akmlist = akmRepo.findAllByIdProdiAndSucces(prodi, false);

        for(AktivitasMahasiswa akm : akmlist){
            System.out.println(akm.getNamaAktivitas());
            akun.setAct("""
                    "act" : "InsertAktivitasMahasiswa",
                    "record" : {
                          "jenis_anggota": "%s",
                          "id_jenis_aktivitas": "%s",
                          "id_prodi": "%s",
                          "id_semester": "%s",
                          "judul": "%s",
                          "keterangan": "%s",
                          "lokasi": "%s",
                          "sk_tugas": "%s",
                          "tanggal_sk_tugas": "%s",
                          "tanggal_mulai" : "%s",
                          "tanggal_selesai" : "%s"
                        }
                        """.formatted(
                    akm.getJenisAnggota(),
                    akm.getJenisAktivitas(),
                    akm.getIdProdi(),
                    akm.getSemester(),
                    akm.getJudul(),
                    akm.getKeterangan(),
                    akm.getLokasi(),
                    akm.getNoSkTugas(),
                    akm.getTanggalTugas(),
                    akm.getTanggalMulai(),
                    akm.getTanggalAkhir()
            ));
            System.out.println(akun.getAct());
            JSONObject respond = akun.post();
            System.out.println(respond);
            ObjectMapper obj = new ObjectMapper();

            JsonNode jsonNode = obj.readTree(respond.toString());
            if(jsonNode.get("error_code").asInt() != 0){
                System.out.println("error");
                return;
            }

            JsonNode data = jsonNode.get("data");
            System.out.println(data.get("id_aktivitas"));
            akm.setIdAktivitas(data.get("id_aktivitas").asText());
            akm.setSucces(true);
            akmRepo.save(akm);
        }

    }

    public void importExcelView(MultipartFile file, int id) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = wb.getSheetAt(0);

        int rowNum = sheet.getRow(1).getLastCellNum();
        
        Row keyRow = sheet.getRow(1);
        for(int r = 2; r < rowNum; r++){
            Row row = sheet.getRow(r);
            // 0, 1
            if(row == null) continue;

            //mahasiswa NIM, Jenis Peserta
            Map<String, String> mhs = new HashMap<>();
            for(int c = 0; c <= 1; c++){
                Cell cell;
                cell = row.getCell(c);
                if(cell != null){
                    mhs.put(keyRow.getCell(c).getStringCellValue(), cell.getStringCellValue().trim());
                }
            }

            Map<String, String> pbb = new HashMap<>();
            for(int c = 3; c <= 5; c++) {
                Cell cell;
                cell = row.getCell(c);
                if (cell != null) {
                    pbb.put(keyRow.getCell(c).getStringCellValue(), cell.getStringCellValue().trim());
                }
            }

            Map<String, String> pngj = new HashMap<>();
            for(int c = 7; c <= 9; c++) {
                Cell cell;
                cell = row.getCell(c);
                if (cell != null) {
                    pngj.put(keyRow.getCell(c).getStringCellValue(), cell.getStringCellValue().trim());
                }
            }

            if(row.getCell(0) != null && !Objects.equals(row.getCell(0).getStringCellValue(), "")){
                MahasiswaAkm newMhs = new MahasiswaAkm();
                newMhs.setIdAktivitasDb(id);
                String nim = mhs.get("NIM");
                newMhs.setNim(nim);
                newMhs.setJenisPeserta(mhs.get("Jenis Peserta"));

                JSONObject tes = ref.getBiodataMahasiswa(nim);
                newMhs.setIdRegistMhs(tes.get("id_registrasi_mahasiswa").toString());
                newMhs.setNama(tes.get("nama_mahasiswa").toString());
                mahasiswaAkmRepo.save(newMhs);
            }

            if(row.getCell(3) != null && !Objects.equals(row.getCell(3).getStringCellValue(), "")){
                PembimbingAkm newPembimbing = new PembimbingAkm();
                newPembimbing.setIdAktivitasDb(id);

                String nidn = pbb.get("NIDN");
                newPembimbing.setNidnDosen(nidn);

                String kategori = pbb.get("Kategori Kegiatan");
                newPembimbing.setKategori(kategori);
                newPembimbing.setPembimbingKe(pbb.get("Pembimbing Ke"));

                JSONObject tes = ref.getDosen(nidn);
                newPembimbing.setNamaDosen(tes.get("nama_dosen").toString());
                newPembimbing.setIdDosen(tes.get("id_dosen").toString());

                String kegiatan = ref.getNamaKategoriKegiatan(kategori);
                newPembimbing.setNamakegiatan(kegiatan);

                pembimbingAkmRepo.save(newPembimbing);

            }

            if(row.getCell(7) != null && row.getCell(7).getStringCellValue() != ""){
                PengujiAkm newPenguji = new PengujiAkm();

                newPenguji.setIdAktivitasDb(id);

                String nidn = pngj.get("NIDN");
                newPenguji.setNidnDosen(nidn);
                System.out.println("wawawa");
                System.out.println(nidn);

                String kategori = pngj.get("Kategori Kegiatan");
                newPenguji.setKategori(kategori);
                newPenguji.setPembimbingKe(pngj.get("Penguji Ke"));

                JSONObject tes = ref.getDosen(nidn);
                newPenguji.setNamaDosen(tes.get("nama_dosen").toString());
                newPenguji.setIdDosen(tes.get("id_dosen").toString());

                String kegiatan = ref.getNamaKategoriKegiatan(kategori);
                newPenguji.setNamaKegiatan(kegiatan);


                pengujiAkmRepo.save(newPenguji);
            }
        }
    }

    public void importNeoView(int id) throws JsonProcessingException {
        akun.login();
        AktivitasMahasiswa akm = getById(id);

        if(akm.getIdAktivitas() == null) return;

        List<MahasiswaAkm> mahasiswa_list = mahasiswaAkmRepo.findAllByIdAktivitasDbAndSucces(id, false);
        List<PembimbingAkm> pembimbing_list = pembimbingAkmRepo.findAllByIdAktivitasDbAndSucces(id, false);
        List<PengujiAkm> penguji_list = pengujiAkmRepo.findAllByIdAktivitasDbAndSucces(id, false);

        for(MahasiswaAkm mhs : mahasiswa_list){
            akun.setAct("""
                    "act" : "InsertAnggotaAktivitasKampusMahasiswa",
                    "record" : {
                        "id_aktivitas": "%s",
                        "id_registrasi_mahasiswa": "%s",
                        "jenis_peran":"%s"
                    }
                    """.formatted(

                    akm.getIdAktivitas(),
                    mhs.getIdRegistMhs(),
                    mhs.getJenisPeserta()
            ));
            akun.post();

        }

        for(PembimbingAkm pbb : pembimbing_list){
            akun.setAct("""
                    "act" : "InsertBimbingMahasiswa",
                    "record": {
                            "id_aktivitas": "%s",
                            "id_kategori_kegiatan": "%s",
                            "id_dosen": "%s",
                            "pembimbing_ke": "%s"
                        }
                    """.formatted(
                    akm.getIdAktivitas(),
                    pbb.getKategori(),
                    pbb.getIdDosen(),
                    pbb.getPembimbingKe()
            ));
            akun.post();
        }

        for(PengujiAkm penguji : penguji_list){
            akun.setAct("""
                    "act" : "InsertUjiMahasiswa",
                    "record": {
                            "id_aktivitas": "%s",
                            "id_kategori_kegiatan": "%s",
                            "id_dosen": "%s",
                            "penguji_ke": "%s"
                        }
                    """.formatted(
                    akm.getIdAktivitas(),
                    penguji.getKategori(),
                    penguji.getIdDosen(),
                    penguji.getPembimbingKe()
            ));
            akun.post();
        }
    }
}
