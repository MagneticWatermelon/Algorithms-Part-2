
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class BaseballElimination {
    private final String[] t;
    private final int[] w;
    private final int[] loss;
    private final int[] r;
    private final int[][] g;
    private FlowNetwork fn;
    private FordFulkerson ff;
    private int totalGames;

    public BaseballElimination(String filename) {
        In input = new In(filename);
        int n = input.readInt();
        t = new String[n];
        w = new int[n];
        loss = new int[n];
        r = new int[n];
        g = new int[n][n];
        fn = new FlowNetwork((n * (n - 1) + 4) / 2);
        int index = 0;
        while (index != n) {
            String teamName = input.readString();
            t[index] = teamName;
            w[index] = input.readInt();
            loss[index] = input.readInt();
            r[index] = input.readInt();
            for (int i = 0; i < n; i++) {
                g[index][i] = input.readInt();
            }
            index++;
        }
        input.close();
    }

    public int numberOfTeams() {
        return t.length;
    }

    public Iterable<String> teams() {
        Iterable<String> teams = Arrays.asList(t);
        return teams;
    }

    public int wins(String team) {
        if (team == null || !isValidTeam(team)) {
            throw new IllegalArgumentException();
        }
        return w[getTeamIndex(team)];
    }

    public int losses(String team) {
        if (team == null || !isValidTeam(team)) {
            throw new IllegalArgumentException();
        }
        return loss[getTeamIndex(team)];
    }

    public int remaining(String team) {
        if (team == null || !isValidTeam(team)) {
            throw new IllegalArgumentException();
        }
        return r[getTeamIndex(team)];
    }

    public int against(String team1, String team2) {
        if (team1 == null || team2 ==null || !isValidTeam(team1) || !isValidTeam(team2)) {
            throw new IllegalArgumentException();
        }
        return g[getTeamIndex(team1)][getTeamIndex(team2)];
    }

    public boolean isEliminated(String team) {
        if (team == null || !isValidTeam(team)) {
            throw new IllegalArgumentException();
        }
        return certificateOfElimination(team) != null;
    }

    public Iterable<String> certificateOfElimination(String team) {
        if (team == null || !isValidTeam(team)) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < numberOfTeams(); i++) {
            if (team.equals(t[i]))
                continue;
            if (wins(team) + remaining(team) < w[i])
                return Collections.singleton(t[i]);
        }
        populateFlowNetwork(team);
        ff = new FordFulkerson(fn, 0, fn.V() -1);
        if (ff.value() == totalGames) {
            return null;
        }
        ArrayList<String> r = new ArrayList<>();
        for (int i = 0; i < numberOfTeams(); i++) {
            if (ff.inCut(i + calcGameVertices())) {
                r.add(t[i]);
            }
        }
        return r;
    }

    private void populateFlowNetwork(String team) {
        fn = new FlowNetwork(numberOfTeams() + calcGameVertices() + 2);
        FlowEdge gameEdge;
        FlowEdge teamEdge1;
        FlowEdge teamEdge2;
        FlowEdge sinkEdge;
        totalGames = 0;
        int indexTeam = getTeamIndex(team);
        int gameVertex = 1;
        int teamVertex = calcGameVertices();

        for (int i = 0; i < numberOfTeams(); i++) {
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (i != indexTeam && j != indexTeam) {
                    gameEdge = new FlowEdge(0, gameVertex, g[i][j]);
                    totalGames += g[i][j];
                    teamEdge1 = new FlowEdge(gameVertex, teamVertex + i, Double.POSITIVE_INFINITY);
                    teamEdge2 = new FlowEdge(gameVertex, teamVertex + j, Double.POSITIVE_INFINITY);
                    fn.addEdge(gameEdge);
                    fn.addEdge(teamEdge1);
                    fn.addEdge(teamEdge2);
                    gameVertex++;
                }
            }
            if (i != indexTeam) {
                sinkEdge = new FlowEdge(teamVertex + i, fn.V() - 1, calcCapacity(team,t[i]));
                fn.addEdge(sinkEdge);
            }
        }
    }

    private int getTeamIndex(String team) {
        return Arrays.asList(t).indexOf(team);
    }

    private int calcGameVertices() {
        return ((numberOfTeams()) * (numberOfTeams() - 1) / 2);
    }

    private double calcCapacity(String team1, String team2) {
        return wins(team1) + remaining(team1) - wins(team2);
    }

    private boolean isValidTeam(String team) {
        for (int i = 0; i < t.length; i++) {
            if(team.equals(t[i])) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination("teams4a.txt");
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }

    }
}
