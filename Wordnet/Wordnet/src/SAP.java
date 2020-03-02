import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.DepthFirstOrder;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class SAP {

    private final Digraph graph;
    private final Iterable<Integer> topologicalOrder;

    // constructor takes a digraph (not necessarily a DAG)
    // reverse postorder in a DAG is topological order
    public SAP(Digraph G) {
        this.graph = new Digraph(G);
        DepthFirstOrder dfo = new DepthFirstOrder(graph);
        topologicalOrder = dfo.reversePost();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return calcVertices(v, w, true);
    }

    private int calcVertices(int v, int w, boolean distOrAncestor) {
        BreadthFirstDirectedPaths origin = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths dest = new BreadthFirstDirectedPaths(graph, w);

        int minDist = Integer.MAX_VALUE;
        int dist = 0;
        int ancestor = -1;

        for (int vertex : topologicalOrder) {
            if (origin.hasPathTo(vertex) && dest.hasPathTo(vertex)) {
                dist = origin.distTo(vertex) + dest.distTo(vertex);
                if (dist < minDist) {
                    minDist = dist;
                    ancestor = vertex;
                }
            }
        }

        if (distOrAncestor) {
            if (minDist == Integer.MAX_VALUE) {
                return -1;
            }
            else {
                return minDist;
            }
        }
        else {
            return ancestor;
        }

    }

    private int calcIterables(Iterable<Integer> v, Iterable<Integer> w, boolean distOrAncestor) {
        BreadthFirstDirectedPaths origin = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths dest = new BreadthFirstDirectedPaths(graph, w);

        int minDist = Integer.MAX_VALUE;
        int dist = 0;
        int ancestor = -1;

        for (int vertex : topologicalOrder) {
            if (origin.hasPathTo(vertex) && dest.hasPathTo(vertex)) {
                dist = origin.distTo(vertex) + dest.distTo(vertex);
                if (dist < minDist) {
                    minDist = dist;
                    ancestor = vertex;
                }
            }
        }

        if (distOrAncestor) {
            if (minDist == Integer.MAX_VALUE) {
                return -1;
            }
            else {
                return minDist;
            }
        }
        else {
            return ancestor;
        }

    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= graph.V()) {
            throw new IllegalArgumentException();
        }
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return calcVertices(v, w, false);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateArgs(v, w);
        return calcIterables(v, w, true);
    }

    private void validateArgs(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        Iterator<Integer> iteratorV = v.iterator();
        while (iteratorV.hasNext())  {
            Integer vertex = iteratorV.next();
            if (vertex != null) {
                validateVertex(vertex);
            }
        }
        Iterator<Integer> iteratorW = w.iterator();
        while (iteratorW.hasNext())  {
            Integer vertex = iteratorW.next();
            if (vertex != null) {
                validateVertex(vertex);
            }
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateArgs(v, w);
        return calcIterables(v, w, false);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
