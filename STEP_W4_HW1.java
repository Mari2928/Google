import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.ArrayDeque;
import java.util.Queue;
/**
* This program helps investigating SNS followers relationship.
* Please run main() to see the result of some unit tests and the result of SNS investigation.
* @author ashigam
*/
    public class STEP_W4_HW1 {    

    enum State { Unvisited, Visited; }

    class Graph{    	
        public Node[] nodes; // to get the node from a given name
        Graph(int V){ this.nodes = new Node[V]; }
    }

    class Node{	
        public String name;
        public int number;    	
        public State state = State.Unvisited;
        public ArrayList<Node> followers = new ArrayList<>();

        public int score = 100;

        Node(){}
        Node(int i){ this.number = i;}
        Node(int i, String n){ 
            this.number = i; 
            this.name = n; 
        }
    }
    /**
     * Find a BFS path from you to the friend.
     * @param g the graph to be traversed 
     * @param yourName the string of your name
     * @param herName the string of friend's name
     * @return the list of names as a result of BFS
     */
    ArrayList<String> BFS(Graph g, String yourName, String herName) {
        ArrayList<String> result = new ArrayList<>();
        result.add(yourName);
        if(yourName.equals(herName)) return result;	
        Node start = findNodeInName(yourName, g);
        Node end = findNodeInName(herName, g);

        Queue<Node> q = new ArrayDeque<Node>();
        for(Node u : g.nodes)	// reset the states
            u.state = State.Unvisited;

        // start traversing
        q.add(start);  
        Node u;
        while(!q.isEmpty()) {
            u = q.remove();    	
            result.add(u.name);	
            for(Node v : u.followers) {    	
                if(v.state == State.Visited)	continue;				 
                if(v == end) { // target is found
                    result.add(v.name); 
                    return result;	
                }
                q.add(v);
                v.state = State.Visited;   					
            }			
        }    	
        return result;
    }

    /**
     * Find a shortest path from you to the friend.
     * @param result the list of names as a result of BFS
     * @param g the graph to be traversed 
     * @param start the string of starting point
     * @return the list of shortest path in reverse order
     */
    ArrayList<String> findMinPath1(ArrayList<String> result, Graph g, String start){
        ArrayList<String> path = new ArrayList<String>();
        return findMinPath2(result, g, start, path);
    }
    ArrayList<String> findMinPath2(ArrayList<String> result, Graph g, String start, ArrayList<String> path) {    	
        if(result.size() == 1)	{ // base case
            path.add(start);
            return path;
        }  		
        int len = result.size();   	
        String prev = result.get(len-2);
        path.add(result.get(len-1));
        result = BFS(g, start, prev);
        findMinPath2(result, g, start, path);  // recursion	
        return path;
    }

    /**
     * See if a friend can reach me using DFS.
     * @param g the graph to be traversed 
     * @param yourName the string of your name
     * @param herName the string of friend's name
     * @return true is it's reachable and false otherwise
     */
    boolean found = false;
    boolean isReachable(Graph g, String yourName, String herName) {
        for(Node u : g.nodes)	// reset the states
            u.state = State.Unvisited;    	
        isReachable2(g, yourName, herName);
        return found;
    }    
    void isReachable2(Graph g, String yourName, String herName) {   	
        Node root = findNodeInName(yourName, g);
        if(root == null)	return;

        // base case of recursion
        if(root.name.equals(herName)) {
            found = true;
            return;
        }    	 
        root.state = State.Visited;    	    	
        for(Node v : root.followers) {
            if(v.state == State.Unvisited) 
                isReachable2(g, v.name, herName);   			
        } 
    }

    /**
     * Count the number of path as well as print them.
     * @param g the graph
     * @param yourName the start point
     * @param herName the end point
     */
    void DFS(Graph g, String yourName, String herName) {
        ArrayList<String> path = new ArrayList<>();
        path.add(yourName);
        int count = 0;
        count = DFS(g, yourName, herName, path, count);
        System.out.println("count is : "+ count);
    }
    int DFS(Graph g, String yourName, String herName, ArrayList<String> path, int count) {
        Node root = findNodeInName(yourName, g);
        if(root == null) {
            System.out.println("The account doesn't exist.");
            return 0;
        }
        if(root.name.equals(herName)) {	// base case of recursion
            for(String s : path)
                System.out.print(s + " ");
            System.out.println();
            return count + 1;
        }
        root.state = State.Visited;    	    	
        for(Node v : root.followers) {
            if(v.state == State.Unvisited) {
                path.add(v.name);
                count = DFS(g, v.name, herName, path, count);   
                path.remove(v.name);
            }    			
        }  
        root.state = State.Unvisited;
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
            if(n.number == nodeNumber)	return n;
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
        int V = 5;  
        int[][] edges = new int[][] {{0,1},{0,2},{1,0},{1,2},{1,3},{2,0},{2,1},{2,3},{2,4},{3,1},{3,2},{3,4},{4,2},{4,3}};    
        Graph g = createTestGraph(edges, V);   

        ArrayList<String> result = BFS(g, "2", "6");
        result = findMinPath1(result, g, "2");
        printMinPath(result ,"2", "6");    	
        DFS(g, "0", "1");  
        System.out.println(isReachable(g, "0", "3"));
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
    void printMinPath(ArrayList<String> result, String start, String end) {
        System.out.print("Min path (" + start+ " -> " + end+ "): ");
        for(int i = result.size()-1; i >= 0; i--)
            System.out.print(result.get(i) + " ");
    }

    public static void main(String args[]) {	
        STEP_W4_HW1 graph = new STEP_W4_HW1();
        //Graph g = graph.createGraph(6);   
        //graph.findHighestRankedPage(g);

        graph.test();

    //    	System.out.println("SNS investigation start------------");   
    //    	System.out.println();
    //    	
    //    	int V = 54;	// number of vertices(students)
    //    	Graph g = graph.createGraph(V);
    //    	
    //    	System.out.println("■　adrianからあなたまでたどり着けますか？");
    //    	int n = graph.isReachable(g, "helen", "adrian");
    //    	if(n != 0)	System.out.println("Yes");
    //    	else		System.out.println("No");
    //    	
    //    	System.out.println("■　いくつパスが存在するでしょうか？");
    //    	System.out.println(n);
    //    	
    //    	System.out.println("■　あなたからhughまでの最短パスは？");
    //    	graph.printMinPath(graph.findMinPath(g, "helen", "hugh"), "helen", "hugh");
    //    	
    //    	System.out.println("■　一番ページランクの高い人は？");
    //    	graph.findHighestRankedPage(g);
    //    	
    //    	System.out.println();
    //    	System.out.println("SNS investigation end  ------------");
    }
}
