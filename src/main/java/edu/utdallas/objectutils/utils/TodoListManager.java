package edu.utdallas.objectutils.utils;

import edu.utdallas.objectutils.Placeholder;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class TodoListManager {
    private static Queue<List<Placeholder>> pool = new LinkedList<>();

    public static List<Placeholder> allocate() {
        final List<Placeholder> todoList = pool.poll();
        if (todoList == null) {
            return new LinkedList<>();
        }
        return todoList;
    }

    public static void free(final List<Placeholder> todoList) {
        todoList.clear();
        pool.offer(todoList);
    }
}
