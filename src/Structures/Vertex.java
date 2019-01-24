package Structures;

public class Vertex {
    private int ID;

    public Vertex(int id) {
        ID = id;
    }

    public int getID() {
        return ID;
    }

    public int hashCode() {
        return getID();
    }

    @Override
    public boolean equals(Object v) {
        if (v instanceof Vertex) {
            return getID() == ((Vertex) v).getID();
        }
        return false;
    }
}
