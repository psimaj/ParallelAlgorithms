package Structures;

public class Edge {
    private Vertex from, to;

    public Edge(Vertex f, Vertex t) {
        from = f;
        to = t;
    }

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }
}
