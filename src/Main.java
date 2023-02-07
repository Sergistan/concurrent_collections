import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    private static final ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    private static final int countString = 10_000;
    private static final int lengthString = 100_000;
    private static final AtomicLong maxCountA = new AtomicLong();
    private static final AtomicLong maxCountB = new AtomicLong();
    private static final AtomicLong maxCountC = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();

        Thread createString = new Thread(() -> {
            for (int i = 0; i < countString; i++) {
                String abc = generateText("abc", lengthString);
                try {
                    queueA.put(abc);
                    queueB.put(abc);
                    queueC.put(abc);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        createString.start();
        threadList.add(createString);

        Thread threadForA = generateThread(queueA, 'a', maxCountA);
        startAndAddThreads(threadForA, threadList);

        Thread threadForB = generateThread(queueB, 'b', maxCountB);
        startAndAddThreads(threadForB, threadList);

        Thread threadForC = generateThread(queueC, 'c', maxCountC);
        startAndAddThreads(threadForC, threadList);


        for (Thread e : threadList) {
            e.join();
        }

        System.out.println("Максимальное количество букв 'a' в слове: " + maxCountA);
        System.out.println("Максимальное количество букв 'b' в слове: " + maxCountB);
        System.out.println("Максимальное количество букв 'c' в слове: " + maxCountC);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread generateThread(ArrayBlockingQueue<String> queue, char nameChar, AtomicLong maxCount) {
        return new Thread(() -> {
            try {
                for (int i = 0; i < countString; i++) {
                    String letter = queue.take();
                    long countChar = letter.chars()
                            .filter(ch -> Character.valueOf((char) ch).equals(nameChar))
                            .count();
                    if (maxCount.get() < countChar) {
                        maxCount.getAndSet(countChar);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void startAndAddThreads(Thread thread, List<Thread> threadList) {
        thread.start();
        threadList.add(thread);
    }

}
