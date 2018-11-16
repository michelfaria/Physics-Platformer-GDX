package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.utils.Array;

public class TodoListAppender {

    private final Array<Runnable> todoList;

    public TodoListAppender(Array<Runnable> todoList) {
        this.todoList = todoList;
    }

    public void addTask(Runnable task) {
        todoList.add(task);
    }
}
