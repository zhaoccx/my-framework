package cn.com.zcc.LeetCode;

import java.util.HashSet;
import java.util.Set;

/**
 * Hello world!
 */
public class App {
    // TODO OK.
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Set<Short> set = new HashSet<>();
        for (short i = 0; i < 100; i++) {
            set.add(i);
            set.remove(i - 1);
        }
        System.out.println(set.size());
    }
}
