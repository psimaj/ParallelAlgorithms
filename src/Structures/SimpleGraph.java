package Structures;

import java.util.*;

public class SimpleGraph {
    private HashMap<Vertex, List<Vertex>> edges = new HashMap<>();
    private HashMap<Integer, Vertex> vertices = new HashMap<>();

    public boolean addVertex(Integer ind) {
        Vertex v = new Vertex(ind);
        if (edges.keySet().contains(v)) {
            return false;
        }
        vertices.put(v.getID(), v);
        edges.put(v, new ArrayList<>());
        return true;
    }

    public boolean addEdge(Integer f, Integer t) {
        Vertex from = vertices.get(f);
        Vertex to = vertices.get(t);
        if (edges.get(from).contains(to)) {
            return false;
        }
        edges.get(from).add(to);
        edges.get(to).add(from);
        return true;
    }

    public List<Vertex> getNeighbours(Integer ind) {
        return edges.get(vertices.get(ind));
    }

    public Vertex getVertex(Integer ind) {
        return vertices.get(ind);
    }

    public Collection<Vertex> getVertices() {
        return vertices.values();
    }

    public int getVertexCount() {
        return vertices.keySet().size();
    }

    public int getEdgeCount() {
        return edges.size()/2;
    }
}
