package producers;

import config.ProducerConfigFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReservoirProducer {
    private static final String TOPIC = "water-levels";

    private static final String DATA_DIRECTORY = System.getProperty("user.dir") + File.separator + "test_data";

    public static void startReservoirProducer(ProducerConfigFactory producer) {
        Pattern fileNamePattern = Pattern.compile("test_data_reservoir_(\\d+)_(top|bottom).txt");
        File[] files = new File(DATA_DIRECTORY).listFiles();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String filePath = DATA_DIRECTORY + File.separator + fileName;
                Matcher matcher = fileNamePattern.matcher(fileName);

                if (matcher.matches()) {
                    String reservoirNumber = matcher.group(1);
                    String sensorPosition = matcher.group(2);
                    String key = "reservoir_" + reservoirNumber + "_" + sensorPosition;

                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(filePath));
                        while (reader.readLine() != null) {
                            String value = reader.readLine();
                            producer.sendSensorData(TOPIC, key, value);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
