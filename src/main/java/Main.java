import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static int maxQuantity = 0;
    public static int maxFrequency = 0;

    public static void main(String[] args) throws InterruptedException {

        Thread frequencyLeader = new Thread(getLogic());
        frequencyLeader.start();

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                synchronized (sizeToFreq) {
                    calculateFrequencyAndQuantity(route);
                    sizeToFreq.notify();
                }
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        frequencyLeader.interrupt();
        System.out.println("Most frequent number of repetitions " + maxFrequency + " (met " + maxQuantity + " times)");
        System.out.println("Other sizes:");
        for (Map.Entry<Integer, Integer> pair : sizeToFreq.entrySet()) {
            System.out.println("- " + pair.getKey() + " (" + pair.getValue() + " times)");
        }
    }

    private static Runnable getLogic() {
        return () -> {
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
                    System.out.println("Leader among frequencies " + frequency + " (met " + quantity + " times)");
                }
            }
        };
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
