package org.example.camunda.helpers;

import org.springframework.security.core.Authentication;
import org.example.camunda.entity.User;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max + 1 - min)) + min);
    }

    public static HashMap<String, Object> getAnswer(String value, boolean error, String description) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("error", error);
        map.put("description", description);
        map.put("value", value);
        return map;
    }

    public static String getCollectionLine(Set<?> collection, String delimiter) {
        if (collection == null || collection.size() == 0) return "";
        return collection.stream()
            //.map(String::valueOf)
            .map(c -> String.valueOf(c.toString()))
            .collect(Collectors.joining(delimiter));
    }

    public static String getExtension(String filename) {
        if (filename == null) return "";
        int index = filename.lastIndexOf('.');
        if (index > 0) {
            return filename.substring(index + 1);
        }
        return "";
    }

    public static int checkInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public static User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        try {
            User user = (User) authentication.getPrincipal();
            if (user != null) return user;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static int getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return 0;
        try {
            User user = (User) authentication.getPrincipal();
            if (user != null) return user.getId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static List<Integer> getPages(int current, int max) {
        List<Integer> pages = new ArrayList<>();
        if (max < 2) return pages;
        int currentPage = 0;
        if (current - 3 > 0) {
            currentPage = current - 3;
        }
        if (currentPage == 0) currentPage += 1;
        while (currentPage < current) {
            pages.add(currentPage + 1);
            currentPage += 1;
        }
        while (currentPage < current + 4) {
            pages.add(currentPage + 1);
            currentPage += 1;
            if (currentPage >= max) break;
        }
        return pages;
    }

    public static int getPageNumber(String page) {
        int pageNumber = checkInt(page);
        if (pageNumber < 0) pageNumber = 0;
        if (pageNumber > 1) pageNumber -= 1;
        return pageNumber;
    }
}
