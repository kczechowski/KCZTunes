package com.kczechowski.handlers;

import com.kczechowski.main.App;
import com.kczechowski.states.NullState;
import com.kczechowski.states.State;

import java.util.Stack;

public class StateManager {
    private Stack<State> stateStack;

    public StateManager() {
        stateStack = new Stack<State>();
        pushState(new NullState(this));
    }

    public void pushState(State state){
        state.init();
        stateStack.push(state);
        App.eventManager.stateChanged();
    }

    public State peekState(){
        return stateStack.peek();
    }

    public void setState(State state){
        for(State s: stateStack){
            s.dispose();
        }
        stateStack.clear();
        pushState(state);
    }

    public void popState(){
        State temp = stateStack.peek();
        temp.dispose();
        stateStack.pop();
        App.eventManager.stateChanged();
    }

    public void printStateList(){
        for(State s: stateStack){
            System.out.println(s);
        }
    }
}
