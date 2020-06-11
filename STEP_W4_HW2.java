import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
* This program explores the wikipedia links structure to find a minimum path
* between specific pages as well as to find the farthest page from the given page name.
* Since the data is big, I used HashMap to retrieve the node in O(1) rather than searching linearly.
* @author ashigam
*/
public class STEP_W4_HW2 {    

    enum State { Unvisited, Visited, Visiting; }

    class Graph{
        public HashMap<String, Node> nodesMap = new HashMap<>(); // to retrieve a node by a page name in O(1)
        public ArrayList<Node> nodes = new ArrayList<>();	// manage nodes by id#
    }
    class Node{	
        public String name;
        public int id;
        public State state = State.Unvisited;
        public ArrayList<Node> links = new ArrayList<>();
        Node(int i, String n){ this.id = i; this.name = n; }
    }
    /**
     * Find a minimum path from a start page to an end page.
     * If the end page is not found, it prints the farthest page before returning null.
     * @param g the subjected graph
     * @param startPage the name of the page to start traversing
     * @param endPage the name of the target page
     * @return the list of page names represents the minimum path
     */
    String[] findMinPath(Graph g, String startPage, String endPage) {
        Node start = g.nodesMap.get(startPage);
        Node end = g.nodesMap.get(endPage);
        if(start == end) return new String[] {"0"};

        LinkedList<Node> q = new LinkedList<Node>();
        ArrayList<String> result = new ArrayList<>();
        for(Node u : g.nodes)	
            u.state = State.Unvisited;	// reset the states

        // start traversing with BFS
        start.state = State.Visiting;
        q.add(start);    	
        Node u;
        while(!q.isEmpty()) {
            u = q.removeFirst();	// dequeue
            result.add(u.name);
            if(u != null) {
                // add adjacent nodes if Unvisited 
                for(Node v : u.links.toArray(new Node[u.links.size()])) {
                    if(v.state == State.Unvisited) {
                        if(v == end) { // target is found
                            result.add(v.name);
                            return result.toArray(new String[result.size()]);	
                        }
                        else {
                            q.add(v);
                            v.state = State.Visiting;
                        }    					
                    }    					
                }
                u.state = State.Visited;
            }    		
        }
        // print the farthest page
        System.out.println("The farthest page is: " + result.get(result.size()-1));
        return null;		// target wasn't found
    }
    /**
     * Create a graph represents wikipedia links structure.
     */
    Graph createGraph() {	    	
        Graph g = new Graph();	  	
        addName(g);    	    	
        addLink(g);   
        return g;
    }  
    /**
     * Add page names and their ID#s to HashMap and nodes, respectively.
     * @param g the subjected graph
     */
    void addName(Graph g) {
        int idNum;    	
        try(BufferedReader pages = new BufferedReader(new InputStreamReader(new FileInputStream("bin/wikipedia_pages.txt"),"UTF-8"))) {
            String pageLine;
            while ((pageLine = pages.readLine()) != null) {
                String[] name = pageLine.split("\t");
                idNum = Integer.parseInt(name[0]);
                g.nodes.add(new Node(idNum, name[1])); 
                g.nodesMap.put(name[1], g.nodes.get(idNum));       	    	
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
    /**
     * Add links to each page.
     * @param g the subjected graph
     */
    void addLink(Graph g) {    	
        try(BufferedReader in = new BufferedReader(new FileReader("bin/wikipedia_links.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] link = line.split("\t");
                int pageId = Integer.parseInt(link[0]);
                int linkId = Integer.parseInt(link[1]);
                g.nodes.get(pageId).links.add(g.nodes.get(linkId));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String args[]) {	
        STEP_W4_HW2 test = new STEP_W4_HW2();
        Graph g = test.createGraph();  

        System.out.println("■　Googleから渋谷を最短でたどる方法は？");
        String[] result = test.findMinPath(g, "Google", "渋谷");
        if(result != null) {
            for(String s : result)
                System.out.print(s + " ");
        }
        else	System.out.println("Not reachable");    	
        System.out.println();
        System.out.println("■　Googleから一番遠いページは？");
        test.new Node(-99, "test"); // dummy node
        test.findMinPath(g, "Google", "test");
    }
}
