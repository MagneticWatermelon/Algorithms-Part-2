import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.In;

public class WordNet {

    private final SAP sap;
    private final RedBlackBST<String, Bag<Synonym>> bst;
    private final Synonym[] synList;

    private class Synonym {
        private final int id;
        private final String[] synset;

        public Synonym(int id, String[] set) {
            this.id = id;
            this.synset = set.clone();
        }
    }

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        bst = new RedBlackBST<>();
        validateArg(synsets);
        validateArg(hypernyms);
        readSynsets(synsets);
        synList = initializeSynList(bst.size());
        Digraph digraph = readHypernyms(hypernyms, bst.size());
        validateAcyclic(digraph);
        validateUniqueRoot(digraph);
        sap = new SAP(digraph);
    }

    private void validateAcyclic(Digraph digraph) {
        DirectedCycle dc = new DirectedCycle(digraph);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException();
        }
    }

    private void validateUniqueRoot(Digraph digraph) {
        int root = -1;
        for (int vertex = 0; vertex < digraph.V(); vertex++) {
            if (digraph.outdegree(vertex) == 0) {
                if (root >= 0) {
                    throw new IllegalArgumentException();
                }
                root = vertex;
            }
        }
    }

    private Digraph readHypernyms(String hypernyms, int size) {
        Digraph digraph = new Digraph(size);
        In in = new In(hypernyms);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] tokens = line.split(",");
            int synId = Integer.parseInt(tokens[0]);
            for (int i = 1; i < tokens.length; i++) {
                int hypernymId = Integer.parseInt(tokens[i]);
                digraph.addEdge(synId, hypernymId);
            }
        }
        return digraph;
    }

    private void readSynsets(String synsets) {
        In in = new In(synsets);
         while (!in.isEmpty()) {
             String line = in.readLine();
             String[] tokens = line.split(",");
             int synId = Integer.parseInt(tokens[0]);
             String[] synset = tokens[1].split(" ");
             Synonym synonym = new Synonym(synId, synset);
             for (String syn : synset) {
                 Bag<Synonym> synonymBag = bst.get(syn);
                 if (synonymBag == null) {
                     synonymBag = new Bag<>();
                 }
                 synonymBag.add(synonym);
                 bst.put(syn, synonymBag);
             }

         }
    }

    private Synonym[] initializeSynList(int size) {
        Synonym[] list = new Synonym[size];
        for (String key : bst.keys()) {
            Bag<Synonym> symbols = bst.get(key);
            for (Synonym s : symbols) {
                list[s.id] = s;
            }
        }
        return list;
    }

    private void validateArg(String arg) {
        if (arg == null) throw new IllegalArgumentException();
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return bst.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        validateArg(word);
        return bst.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        validateArg(nounA);
        validateArg(nounB);
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        return sap.length(matchingKeys(nounA), matchingKeys(nounB));
    }

    private Bag<Integer> matchingKeys(String word) {
        Bag<Integer> bag = new Bag<>();
        for (Synonym synonym : bst.get(word)) {
            bag.add(synonym.id);
        }
        return bag;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        validateArg(nounA);
        validateArg(nounB);
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        int ancestor = sap.ancestor(matchingKeys(nounA), matchingKeys(nounB));
        if (ancestor < 0) {
            return null;
        }
        return String.join(" ", synList[ancestor].synset);
    }

}
