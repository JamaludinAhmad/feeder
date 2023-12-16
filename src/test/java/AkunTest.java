import com.example.feeder_importer.config.Akun;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

@Component
public class AkunTest {


    @Test
    public void loginTest() throws JsonProcessingException {
        Akun akun = new Akun();
        akun.login();
        System.out.println(akun.getToken());
    }

    @Test
    public void getData() throws JsonProcessingException, JSONException {
        Akun akun = new Akun();
        akun.setAct("""
                "act" : "GetProdi",
                "filter":"",
                "order":"",
                "limit":"",
                "offset":"0"
                """);
        JSONObject dataResponse = akun.post();
        System.out.println(dataResponse.get("data"));

    }
}
