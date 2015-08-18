package com.ewintory.alexandria.utils;

import java.util.Collection;


public final class Lists {

    private Lists() {
        throw new AssertionError("No instances.");
    }

    public static <E> boolean isEmpty(Collection<E> list) {
        return (list == null || list.size() == 0);
    }

}
