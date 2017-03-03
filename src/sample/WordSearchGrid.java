package sample;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Node {
    char letter;
    boolean isWord = false;
    ArrayList<Node> children = new ArrayList<>();
}

class Solution {
    String word;
    int x;
    int y;
    int xdel;
    int ydel;
}

public class WordSearchGrid extends StackPane {

    private char[][] wordsearch;
    private ArrayList<Node> trie;
    private ArrayList<Solution> solutions;
    private final int MIN_WORD_LENGTH = 3;
    private int wordSearchSize;
    private final String dictionaryFileName = "dictionary.txt";
    private GridPane grid;

    public WordSearchGrid(int size) {
        super();
        createTrie();

        grid = new GridPane();

        createRandomWordSearch(size);

        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.setHgap(10);
        grid.setVgap(5);
        getChildren().add(grid);

    }

    private void layoutWordSearch(){
        grid.getChildren().clear();
        for (int x = 0; x < wordSearchSize; ++x){
            for (int y = 0; y < wordSearchSize; ++y){
                Text text = new Text("" + wordsearch[x][y]);
                text.setText(text.getText().toUpperCase());
                text.setFont(Font.font("Verdana", 30));
                text.setTextAlignment(TextAlignment.CENTER);
                grid.add(text, x, y);
            }
        }
    }

    public void createRandomWordSearch(int size) {
        if (size != wordSearchSize){
            wordSearchSize = size;
            wordsearch = new char[size][size];
        }

        Random r = new Random();
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                wordsearch[x][y] = (char) (r.nextInt(26) + 'a');
            }
        }

        findAllWords();
        layoutWordSearch();
    }

    public void highlightSolutions (){
        Text[][] letters = new Text[wordSearchSize][wordSearchSize];
        for (int x = 0; x < wordSearchSize; ++x){
            for (int y = 0; y < wordSearchSize; ++y){
                Text text = new Text("" + wordsearch[x][y]);
                text.setText(text.getText().toUpperCase());
                text.setFont(Font.font("Verdana", 30));
                text.setTextAlignment(TextAlignment.CENTER);
                letters[x][y] = text;
            }
        }

        for (Solution s : solutions){
            int x = s.x;
            int y = s.y;
            for (int i = 0; i < s.word.length(); ++i){
                letters[x][y].setStyle("-fx-fill: red;");
                x += s.xdel;
                y += s.ydel;
            }
        }

        grid.getChildren().clear();
        for (int x = 0; x < wordSearchSize; ++x){
            for (int y = 0; y < wordSearchSize; ++y){
                grid.add(letters[x][y], x, y);
            }
        }

    }

    public String getSolutionList ()
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (Solution s : solutions){
            String dir = "";
            if (s.xdel == 1){
                if (s.ydel == 1)
                    dir = "DOWN RIGHT";
                else if (s.ydel == -1)
                    dir = "UP RIGHT";
                else
                    dir = "RIGHT";
            }
            else if (s.xdel == -1){
                if (s.ydel == 1)
                    dir = "DOWN LEFT";
                else if (s.ydel == -1)
                    dir = "UP LEFT";
                else
                    dir = "LEFT";
            }
            else { //xdel = 0
                if (s.ydel == 1){
                    dir = "DOWN";
                }
                else
                    dir = "UP";
            }
            stringBuilder.append(s.word.toUpperCase() + " at (" + s.x + ", " + s.y +") going " + dir + "\n");
        }
        return stringBuilder.toString();
    }

    private void findAllWords() {

        HashSet<Solution> result = new HashSet<>();

        for (int x = 0; x < wordsearch.length; ++x) {
            for (int y = 0; y < wordsearch[x].length; ++y) {
                lookForWords(result, trie, "", x, y, x, y, 0, -1); //look up
                lookForWords(result, trie, "", x, y, x, y, 1, -1); //look upright
                lookForWords(result, trie, "", x, y, x, y, 1, 0); //look right
                lookForWords(result, trie, "", x, y, x, y, 1, 1); //look rightdown
                lookForWords(result, trie, "", x, y, x, y, 0, 1); //look down
                lookForWords(result, trie, "", x, y, x, y, -1, 1); //look downleft
                lookForWords(result, trie, "", x, y, x, y, -1, 0); //look left
                lookForWords(result, trie, "", x, y, x, y, -1, -1); //look upleft
            }
        }

        solutions = new ArrayList<>(result);
    }

    private void lookForWords(HashSet<Solution> words, ArrayList<Node> subtrie, String currentPrefix, int startx, int starty, int x, int y, int xdel, int ydel) {
        if (x >= wordsearch.length || x < 0)
            return;
        if (y >= wordsearch[x].length || y < 0)
            return;
        Node n = getNodeByLetter(subtrie, wordsearch[x][y]);
        if (n == null) {
            return;
        }

        if (n.isWord) {
            Solution sol = new Solution();
            sol.x = startx;
            sol.y = starty;
            sol.xdel = xdel;
            sol.ydel = ydel;
            sol.word = currentPrefix + wordsearch[x][y];
            words.add(sol);
        }
        lookForWords(words, n.children, currentPrefix + wordsearch[x][y], startx, starty,x + xdel, y + ydel, xdel, ydel);
    }


    private void createTrie(){
        ArrayList<String> dictionary = new ArrayList<>();

        Scanner scan;
        try {
            scan = new Scanner(new File(dictionaryFileName));
        } catch (FileNotFoundException e){
            System.err.println(e);
            return;
        }

        while (scan.hasNext()) {
            dictionary.add(scan.next());
        }
        scan.close();

        trie = new ArrayList<>();

        for (String s : dictionary) {
            if (s.length() < MIN_WORD_LENGTH)
                continue;

            ArrayList<Node> currentPrefix = trie;

            for (int i = 0; i < s.length(); ++i) {
                Node n = getNodeByLetter(currentPrefix, s.charAt(i));

                if (n == null) {
                    Node currentLetterNode = new Node();
                    currentLetterNode.letter = s.charAt(i);

                    if (i == s.length() - 1) {
                        currentLetterNode.isWord = true;
                    }

                    currentPrefix.add(currentLetterNode);
                    currentPrefix = currentLetterNode.children;
                } else {
                    if (i == s.length() - 1) {
                        n.isWord = true;
                    }

                    currentPrefix = n.children;
                }
            }
        }
    }

    private static Node getNodeByLetter(ArrayList<Node> nodes, char letterToFind) {
        for (Node n : nodes) {
            if (n.letter == letterToFind) {
                return n;
            }
        }
        return null;
    }
}
