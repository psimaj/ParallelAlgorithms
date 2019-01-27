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

    public boolean isConnected() {
        if (vertices.values().size() == 0) {
            return true;
        }
        Vertex start = vertices.values().iterator().next();
        Set<Vertex> black = new HashSet<>();
        Set<Vertex> gray = new HashSet<>();
        Queue<Vertex> q = new ArrayDeque<>();
        q.add(start);
        while (!q.isEmpty()) {
            Vertex curr = q.poll();
            black.add(curr);
            for (Vertex v : edges.get(curr)) {
                if (!black.contains(v) && !gray.contains(v)) {
                    gray.add(v);
                    q.add(v);
                }
            }
        }
        return black.size() == vertices.values().size();
    }
}
