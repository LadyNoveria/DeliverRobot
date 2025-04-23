import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static int maxQuantity = 0;
    public static int maxFrequency = 0;

    public static void main(String[] args) {

        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (sizeToFreq) {
                    String route = generateRoute("RLRFR", 50);
                    calculateFrequencyAndQuantity(route);
                }
            }
            sizeToFreq.notify();
        }).start();

        new Thread(() -> {
            synchronized (sizeToFreq) {
                if (sizeToFreq.isEmpty()) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Самое частое количество повторений " + maxFrequency + " (встретилось " + maxQuantity + " раз)");
                System.out.println("Другие размеры:");
                for (Map.Entry<Integer, Integer> pair : sizeToFreq.entrySet()) {
                    System.out.println("- " + pair.getKey() + " (" + pair.getValue() + " раз)");
                }
            }
        }).start();
    }

    private static void calculateFrequencyAndQuantity(String route) {
        char[] letters = route.toCharArray();
        int count = 0;
        for (char letter : letters) {
            if ('R' == letter) {
                count++;
            }
        }

        if (count == 0) {
            sizeToFreq.put(1, count);
        } else {
            double frequencyR = ((double) count / route.length() * 100);
            int frequency = (int) frequencyR;
            sizeToFreq.put(frequency, count);
            if (count > maxQuantity) {
                maxQuantity = count;
                maxFrequency = frequency;
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
