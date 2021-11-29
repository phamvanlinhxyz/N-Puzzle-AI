package com.example.npuzzleai;

import java.util.Vector;

public class Node {
    public State state;
    public int f;
    public int g;
    public int h;
    public int cost;
    public Node parent;

    public Node(State state, int cost) {
        this.state = state;
        this.cost = cost;
    }
    public boolean equals(Node n) {
        boolean flag = true;
        int[] val = state.value;
        int[] newVal = n.state.value;
        for (int i = 0; i < val.length; i++) {
            if (val[i] != newVal[i]) {
                flag = false;
                break;
            }
        }
        return flag;
    }
    public int estimate() {
        return state.chooseHeuristic();
    }
    public Vector<Node> successors() {
        Vector<Node> nodes = new Vector<>();
        Vector<State> states = state.successors();
        for (State value : states) {
            nodes.add(new Node(value, 1));
        }
        return nodes;
    }
}
