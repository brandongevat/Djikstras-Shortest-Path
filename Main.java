import java.util.*;
import java.io.*;
import java.rmi.server.UID;

public class Main {
    static public class Treasure {
        int from, to, dist; // dist is the distance from the from city

        public Treasure(int from, int to, int dist) {
            this.from = from;
            this.to = to;
            this.dist = dist;
        }

        @Override
        public boolean equals(Object o) {

            if (o == this) {
                return true;
            }

            if (!(o instanceof Treasure)) {
                return false;
            }

            Treasure c = (Treasure) o;

            return ((Integer.compare(from, c.from) == 0
                    && Integer.compare(to, c.to) == 0) && Integer.compare(dist, c.dist) == 0)
                    || (Integer.compare(from, c.to) == 0 && (Integer.compare(to, c.from) == 0)
                            && (Integer.compare(dist, c.dist) == 0));
        }
    }

    static public class Edge {
        int source, dest, weight;

        public Edge(int source, int dest, int weight) {
            this.source = source;
            this.dest = dest;
            this.weight = weight;
        }
    }

    static public class Node {
        int vertex, weight;

        public Node(int vertex, int weight) {
            this.vertex = vertex;
            this.weight = weight;
        }
    }

    static public class Graph {
        List<List<Edge>> adjList = null;

        Graph(List<Edge> edges, int n) {
            adjList = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                adjList.add(new ArrayList<>());
            }

            for (Edge edge : edges) {
                adjList.get(edge.source).add(edge);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("in.txt");
        Scanner stdin = new Scanner(file);

        // # of cities (2 <= C <= 10^5) |V|
        int c = stdin.nextInt();

        // # of roads (c-1 <= r <= min(10^5, n(n-1)/2)) |E|
        int r = stdin.nextInt();

        // City # - represents capital. (1 <= S <= N)
        int s = stdin.nextInt();

        ArrayList<Edge> edges = new ArrayList<Edge>();

        int v, u, w;
        for (int i = 0; i < r; i++) {
            v = stdin.nextInt() - 1; // Source
            u = stdin.nextInt() - 1; // Dest.
            w = stdin.nextInt(); // Weight
            edges.add(new Edge(v, u, w));
        }

        Graph map = new Graph(edges, c);

        // Distance from the capital to the places where the treasure is located.
        long l = stdin.nextInt();
        stdin.close();
        /*
         * Between any 2 cities, no more than 1 road exists.
         * Each road connects 2 different cities.
         * From each city there is at least 1 way to any other city by the roads.
         */

        djikstraShortestPath(map, s - 1, c, l);
    }

    // To find the shortest path, Djikstra's alg. will be used.
    public static void djikstraShortestPath(Graph graph, int source, int n, long l) {
        // Min heap with source node dist. from itself to 0.
        PriorityQueue<Node> minHeap;
        minHeap = new PriorityQueue<>(Comparator.comparingInt(node -> node.weight));
        minHeap.add(new Node(source, 0));

        List<Integer> dist;
        dist = new ArrayList<>(Collections.nCopies(n, Integer.MAX_VALUE));

        dist.set(source, 0);

        boolean[] done = new boolean[n];
        done[source] = true;

        int[] prev = new int[n];
        prev[source] = -1;

        ArrayList<Treasure> treasuresOnRoad = new ArrayList<Treasure>();

        while (!minHeap.isEmpty()) {
            Node node = minHeap.poll();

            int u = node.vertex; // Current vertex (city)

            for (Edge edge : graph.adjList.get(u)) {
                int v = edge.dest; // Next dest. vertex (city)
                int weight = edge.weight;

                // Check from u -> v
                if (dist.get(u) < l && dist.get(u) + weight > l) {
                    if (u < v)
                        treasuresOnRoad.add(new Treasure(u, v, (int) (l - (dist.get(u)))));
                    else
                        treasuresOnRoad.add(new Treasure(u, v, (int) ((dist.get(u) + weight) - l)));
                }
                // Check from v -> u
                if (dist.get(v) < l && dist.get(v) + weight > l) {
                    if (v < u)
                        treasuresOnRoad.add(new Treasure(v, u, (int) (l - (dist.get(v)))));
                    else
                        treasuresOnRoad.add(new Treasure(v, u, (int) ((dist.get(v) + weight) - l)));
                }


                if (!done[v] && (dist.get(u) + weight) < dist.get(v)) {
                    dist.set(v, dist.get(u) + weight);
                    prev[v] = u;
                    minHeap.add(new Node(v, dist.get(v)));
                }

            }

        }

        int inCity = 0;

        for (int i = 0; i < n; i++) {
            if (dist.get(i) == l) {
                inCity += 1;
            }

            // if (i != source && dist.get(i) != Integer.MAX_VALUE) {
            // getRoute(prev, i, route);
            // System.out.printf("Path (%d -> %d): Minimum cost = %d, Route = %s\n", source
            // + 1, i + 1, dist.get(i), route);
            // route.clear();
            // }
        }
        for (int i = 0; i < treasuresOnRoad.size(); i++) {
            for (int j = i + 1; j < treasuresOnRoad.size(); j++) {

                if (treasuresOnRoad.get(i).equals(treasuresOnRoad.get(j)))
                    treasuresOnRoad.remove(j);
            }
        }

        System.out.printf("In city: %d\n", inCity);
        System.out.printf("On the road: %d\n", treasuresOnRoad.size());

    }
}