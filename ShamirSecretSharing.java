import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    // Decodes y value from a given base
    public static BigInteger decodeBase(String value, int base) {
        return new BigInteger(value, base);
    }

    // Parses JSON input and returns a list of decoded points as (x, y) pairs
    public static List<BigInteger[]> parseJSON(String filePath) throws Exception {
        FileReader reader = new FileReader(filePath);
        JSONTokener tokener = new JSONTokener(reader);
        JSONObject jsonObject = new JSONObject(tokener);
        
        int k = jsonObject.getJSONObject("keys").getInt("k");
        List<BigInteger[]> points = new ArrayList<>();

        for (String key : jsonObject.keySet()) {
            if (key.equals("keys")) continue;

            JSONObject point = jsonObject.getJSONObject(key);
            int x = Integer.parseInt(key);
            int base = point.getInt("base");
            String value = point.getString("value");

            BigInteger y = decodeBase(value, base);
            points.add(new BigInteger[] {BigInteger.valueOf(x), y});
        }

        return points;
    }

    // Applies Lagrange interpolation to find the constant term
    public static BigInteger findConstantTerm(List<BigInteger[]> points, int k) {
        BigInteger result = BigInteger.ZERO;
        
        for (int i = 0; i < k; i++) {
            BigInteger xi = points.get(i)[0];
            BigInteger yi = points.get(i)[1];
            BigInteger term = yi;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger xj = points.get(j)[0];
                    term = term.multiply(xj.negate()).divide(xi.subtract(xj));
                }
            }
            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            // Specify the path to the JSON files
            String filePath1 = "testcase1.json";
            String filePath2 = "testcase2.json";

            // Parse JSON and get points
            List<BigInteger[]> points1 = parseJSON(filePath1);
            List<BigInteger[]> points2 = parseJSON(filePath2);

            // Get the value of k from the JSON
            int k1 = points1.size() >= 3 ? 3 : points1.size();
            int k2 = points2.size() >= 7 ? 7 : points2.size();

            // Find the constant term (secret)
            BigInteger secret1 = findConstantTerm(points1, k1);
            BigInteger secret2 = findConstantTerm(points2, k2);

            // Print results
            System.out.println("Secret for testcase 1: " + secret1);
            System.out.println("Secret for testcase 2: " + secret2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
