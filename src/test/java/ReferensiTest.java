import com.example.feeder_importer.config.Akun;
import com.example.feeder_importer.config.Referensi;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

public class ReferensiTest {

    @Test
    public void refTest() throws JsonProcessingException {
        Referensi ref = new Referensi();
        System.out.println(ref.getNamaAktivitas("2"));
    }

    @Test
    public void getMhsTest() throws JsonProcessingException, JSONException {
        Referensi ref = new Referensi();
        System.out.println(ref.getBiodataMahasiswa("20.1.T1.5475").getString("id_registrasi_mahasiswa"));
    }
}
