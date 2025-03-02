package CPU;

import NES.PPU.PPU;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * https://github.com/SingleStepTests/ProcessorTests/tree/main/6502
 */
public class TestProcessor {

    private final Logger logger = LoggerFactory.getLogger(TestProcessor.class);

    @Test
    public void test_00_json() throws IOException {
        String path = "6502_programs/00.json";
        logger.debug("Testing: " + path);

        String json = Files.readString(Path.of(path));
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject);

            JSONObject initial = jsonObject.getJSONObject("initial");
            String name = jsonObject.getString("name");
            JSONObject final_json = jsonObject.getJSONObject("final");
            JSONArray cycles = jsonObject.getJSONArray("cycles");

            break;
        }
    }
}
