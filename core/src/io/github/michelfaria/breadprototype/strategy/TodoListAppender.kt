package io.github.michelfaria.breadprototype.strategy;

import com.badlogic.gdx.utils.Array

class TodoListAppender(private val updateList: Array<() -> Unit>) {

     fun addUpdate(u: () -> Unit) {
         updateList.add(u)
     }
}
