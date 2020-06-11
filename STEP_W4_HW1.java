import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
/**
* This program helps investigating SNS followers relationship.
* Please run main() to see the result of some unit tests and the result of SNS investigation.
* @author ashigam
*/
public class STEP_W4_HW1 {    

    enum State { Unvisited, Visited, Visiting; }

    class Graph{
        // to get the node from a given name
        public Node[] nodes;
        Graph(int V){ this.nodes = new Node[V]; }
    }
    class Node{	
        public String name;
        public int nodeNumber;
        public int score = 100;
        public State state = State.Unvisited;
        public ArrayList<Node> followers = new ArrayList<>();
        Node(){}
        Node(int i){ this.nodeNumber = i;}
        Node(int i, String n){ 
            this.nodeNumber = i; 
            this.name = n; 
        }
    }
    /**
     * Find a shortest path from you to the friend using BFS.
     * @param g the graph to be traversed 
     * @param yourName the string of your name
     * @param herName the string of friend's name
     * @return the list of names in the path
     */
    String[] findMinPath(Graph g, String yourName, String herName) {
        if(g == null)	return new String[] {"Graph is empty"};
        Node start = findNodeInName(yourName, g);
        Node end = findNodeInName(herName, g);
        if(start == end) return new String[] {"0"};    	

        LinkedList<Node> q = new LinkedList<Node>();
        ArrayList<String> result = new ArrayList<>();
        for(Node u : g.nodes)	// reset the states
            u.state = State.Unvisited;

        // start traversing
        start.state = State.Visiting;
        q.add(start);    	
        Node u;
        while(!q.isEmpty()) {
            u = q.removeFirst();	// dequeue
            result.add(u.name);
            if(u != null) {
                // add adjacent nodes if Unvisited 
                for(Node v : u.followers.toArray(new Node[u.followers.size()])) {
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
        return new String[] {"Target wasn't found"};	
    }
    /**
     * See if a friend can reach me using DFS and return the number of paths.
     * @param g the graph to be traversed 
     * @param yourName the string of your name
     * @param herName the string of friend's name
     * @return the number of possible paths. If not reachable it returns 0.
     */
    int isReachable(Graph g, String yourName, String herName) {
        if(g == null)	return 0;
        for(Node u : g.nodes)	// reset the states
            u.state = State.Unvisited;
        int count = 0;
        count = isReachable(g, yourName, herName, count);
        return count;
    }
    int isReachable(Graph g, String yourName, String herName, int count) {
        Node root = findNodeInName(yourName, g);
        if(root == null) {
            System.out.println("The account doesn't exist.");
            return count;
        }
        if(root.name.equals(herName)) {	// base case of recursion
            count++;
            return count;
        }
        root.state = State.Visited;    	    	
        for(Node v : root.followers.toArray(new Node[root.followers.size()])) {
            if(v.state == State.Unvisited) {
                count = isReachable(g, v.name, herName, count);
            }    			
        }      	
        return count;    	
    }
    /**
     * Helper: Find a node in name. 
     * Since the number of nodes in the SNS is small, linear search is used.
     * @param name the string of name to be searched
     * @param g the subjected graph
     * @return the node contains the name 
     */
    Node findNodeInName(String name, Graph g) {
        for(Node n : g.nodes)
            if(n.name.equals(name))	return n;
        return null;
    }
    /**
     * Helper: Find a node in node number. 
     * Since the number of nodes in the SNS is small, linear search is used.
     * @param nodeNumber the number of node to be searched
     * @param g the subjected graph
     * @return the node contains the node number
     */
    Node findNodeInNumber(int nodeNumber, Graph g) {
        for(Node n : g.nodes)
            if(n.nodeNumber == nodeNumber)	return n;
        return null;
    }
    /**
     * Find a page rank of each node distributing scores on every vertices.
     * @param g the subjected graph
     */
    void pageRank(Graph g) {
        // memorize the current score of every nodes 
        int[] currentScore = new int[g.nodes.length];
        int i = 0;
        for(Node n : g.nodes) {
            currentScore[i] = n.score;
            i++;
        }
        // distribute scores
        i = 0;
        for(Node n : g.nodes) {
            int N = n.followers.size();
            if(N == 0)	continue;
            double dist = currentScore[i] / N; 
            i++;
            for(Node s : n.followers.toArray(new Node[N])) {
                s.score += dist;
                n.score -= dist;
            }    		
        }
    }
    /**
     * Find a node which has a highest page rank.
     * @param g the subjected graph
     */
    void findHighestRankedPage(Graph g) {
        int[] prevScore = new int[g.nodes.length];
        boolean check = true;
        // continue distributing scores until converges
        while(check) {
            for(int i=0; i< prevScore.length; i++)
                prevScore[i] = g.nodes[i].score;
            pageRank(g);    		
            for(int i=0; i< prevScore.length; i++) {
                if(prevScore[i] == g.nodes[i].score)
                    check = false;  			
            }        		
        }
        int max = 0;
        for(int i = 0; i < g.nodes.length; i++)     {
            max = Math.max(g.nodes[i].score, max);    
        }	  
        for(int i = 0; i < g.nodes.length-1; i++)  
            if(max == g.nodes[i].score)   	System.out.println(g.nodes[i].name +" has the highest page rank.");
    }  
    /**
     * Create a graph represents SNS relationship.
     * @param V the number of vertices(students)
     */
    Graph createGraph(int V) {	  
        if(V == 0)	return null;
        Graph g = new Graph(V);	  	
        addName(g, V);    	
        addFollower(g);   
        return g;
    }  
    /**
     * Helper: Add student name to each vertex.
     * @param g the subjected graph
     * @param V the number of vertices
     */
    void addName(Graph g, int V) {
        try {
          File nicknames = new File("bin/nicknames.txt");
          Scanner sc = new Scanner(nicknames);
          for(int i = 0; i < V; i++) {
            String[] name = sc.nextLine().split("\t");	 
            g.nodes[i] = new Node(Integer.parseInt(name[0]), name[1]);
          }
        sc.close();
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }
    /**
     * Helper: Add followers to each student.
     * @param g the subjected graph
     */
    void addFollower(Graph g) {    	
        try {
          File links = new File("bin/links.txt");
          Scanner sc = new Scanner(links);
          while(sc.hasNext()) {
            String[] link = sc.nextLine().split("\t");	   	    	
            // link[0] = nodeNumber, link[1] = follower's nodeNumber
            g.nodes[Integer.parseInt(link[0])].followers.add(findNodeInNumber(Integer.parseInt(link[1]), g));
          }
        sc.close();
        } catch (FileNotFoundException e) { e.printStackTrace(); }  
    }
    /**
     * Run test cases. Assume node# starts from 0.
     */
    void test() {   
        int V = 7;
        int[][] edges = new int[][] {{0,1},{0,2},{1,0},{1,3},{2,0},{2,4},{2,6},{3,1},{3,4},{3,5},{4,2},{4,3},{4,5},{5,3},{5,4},{6,2}};    		
        Graph g = createTestGraph(edges, V);    	
        printMinPath(findMinPath(g, "1", "4") ,"1", "4");
        printMinPath(findMinPath(g, "1", "7"), "1", "7");
        printMinPath(findMinPath(g, "3", "6"), "3", "6");
        System.out.println("\nNumber of paths (0 -> 4): " + isReachable(g, "0", "4"));
        System.out.println("\nNumber of paths (2 -> 6): " + isReachable(g, "2", "6"));

        V = 1;
        edges = new int[][] {{0,0}};    		
        g = createTestGraph(edges, V);    	
        printMinPath(findMinPath(g, "0", "0"), "0", "0");

        V = 0;
        edges= new int[][] {{}};    		
        g = createTestGraph(edges, V);    	
        printMinPath(findMinPath(g, "", ""), "", "");
        System.out.println("\nNumber of paths: " + isReachable(g, "", ""));    	
    }
    /**
     * Create a graph for testing purpose.
     * @param edges the 2D array of integers
     * @param V the number of vertices
     * @return the created graph
     */
    Graph createTestGraph(int[][] edges, int V) {    	
        if(V == 0)	return null;
        Graph g = new Graph(V);  
        g.nodes = new Node[V];
        // create nodes
        for(int i = 0; i < V; i++)
            g.nodes[i] = new Node(i, String.valueOf(i));
        // add followers
        for(int j = 0; j < edges.length; j++) {
            int myId = edges[j][0];
            int followerId = edges[j][1];
            g.nodes[myId].followers.add(g.nodes[followerId]);
        } 
        return g;
    }
    /**
     * Helper: Print the minimum path for findMinPath()
     * @param result the list of names in the path
     * @param start the string of name as a starting point
     * @param end the string of name as a ending point
     */
    void printMinPath(String[] result, String start, String end) {
        System.out.print("Min path (" + start+ " -> " + end+ "): ");
        for(String s: result)
            System.out.print(s + " ");
        System.out.println();
    }

    public static void main(String args[]) {	
        STEP_W4_HW1 graph = new STEP_W4_HW1();
        //Graph g = graph.createGraph(6);   
        //graph.findHighestRankedPage(g);

        System.out.println("Test start-------------------------");
        System.out.println();

        graph.test();

        System.out.println();
        System.out.println("Test end---------------------------");
        System.out.println();

        System.out.println("SNS investigation start------------");   
        System.out.println();

        int V = 54;	// number of vertices(students)
        Graph g = graph.createGraph(V);

        System.out.println("■　adrianからあなたまでたどり着けますか？");
        int n = graph.isReachable(g, "helen", "adrian");
        if(n != 0)	System.out.println("Yes");
        else		System.out.println("No");

        System.out.println("■　いくつパスが存在するでしょうか？");
        System.out.println(n);

        System.out.println("■　あなたからhughまでの最短パスは？");
        graph.printMinPath(graph.findMinPath(g, "helen", "hugh"), "helen", "hugh");

        System.out.println("■　一番ページランクの高い人は？");
        graph.findHighestRankedPage(g);

        System.out.println();
        System.out.println("SNS investigation end  ------------");
    }
}
