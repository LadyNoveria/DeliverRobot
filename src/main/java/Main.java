import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static int maxQuantity = 0;
    public static int maxFrequency = 0;

    public static void main(String[] args) {


        Thread frequencyLeader = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int quantity = 0;
                    int frequency = 0;
                    for (Map.Entry<Integer, Integer> pair : sizeToFreq.entrySet()) {
                        if (pair.getValue() > quantity) {
                            quantity = pair.getValue();
                            frequency = pair.getKey();
                        }
                    }
                    System.out.println("Лидер среди частот " + frequency + " (встретилось " + quantity + " раз)");
                }
            }
        });
        frequencyLeader.start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronized (sizeToFreq) {
                    String route = generateRoute("RLRFR", 50);
                    calculateFrequencyAndQuantity(route);
                    sizeToFreq.notify();
                }
            }
            frequencyLeader.interrupt();
            System.out.println("Самое частое количество повторений " + maxFrequency + " (встретилось " + maxQuantity + " раз)");
            System.out.println("Другие размеры:");
            for (Map.Entry<Integer, Integer> pair : sizeToFreq.entrySet()) {
                System.out.println("- " + pair.getKey() + " (" + pair.getValue() + " раз)");
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronized (sizeToFreq) {
                    String route = generateRoute("RLRFR", 50);
                    calculateFrequencyAndQuantity(route);
                    sizeToFreq.notify();
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
