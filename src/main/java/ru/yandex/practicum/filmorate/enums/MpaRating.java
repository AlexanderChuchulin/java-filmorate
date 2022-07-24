package ru.yandex.practicum.filmorate.enums;

import java.util.HashMap;
import java.util.Map;

public enum MpaRating {
    UNDEFINED ("Undefined"), A ("G"), B ("PG"), C ("PG-13"), D ("R"), E ("NC-17");

    private final String mpaTitle;

    MpaRating(String mpaTitle) {
        this.mpaTitle = mpaTitle;
    }

    public static Map<Integer, String> getMpaRatingDescription() {
        Map<Integer, String> mpaRatingsMap = new HashMap<>();

        for (MpaRating mpaRating : MpaRating.values()) {
            mpaRatingsMap.put(mpaRating.ordinal(), mpaRating.mpaTitle);
        }
        mpaRatingsMap.remove(0);
        return mpaRatingsMap;
    }
}
