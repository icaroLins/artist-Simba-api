package com.icaro.simba_dj.service.shared;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Predicate;

public final class ShortlinkUtil {
    private ShortlinkUtil() {
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "")
                .replaceAll("-{2,}", "-");
    }

    public static String generateUniqueShortlink(String source, Predicate<String> existsPredicate) {
        String baseShortlink = normalize(source);
        if (baseShortlink.isBlank()) {
            baseShortlink = "producer";
        }

        String candidate = baseShortlink;
        int suffix = 2;

        while (existsPredicate.test(candidate)) {
            candidate = baseShortlink + "-" + suffix++;
        }

        return candidate;
    }
}
