import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
/**
* This program instructs a path of minimum traveling time from a station to a destination.
* Please run test() to check some results.
* @author ashigam
*/
public class STEP_W4_HW1_Optional {    

    static long INFTY = Long.MAX_VALUE;
    int n;	// the number of stations

    class Graph{
        long[][] edges; 
        ArrayList<String> stations = new ArrayList<>();
    }
    /**
     * Calculate the minimum traveling times for all stations 
     * and store them to edges[][] while updating path[][].
     * e.g. edges[0][2] = 4 means 大崎 to 目黒 takes 4 minutes.
     * @param g the subjected graph
     * @return the 2D array of numbers represents the paths 
     */
    long[][] searchMinTimePath(Graph g) {  
        // create boxes to store the path
        long[][] path = new long[n][n];
        for(int i=0; i < n; i++) {
            for(int j =0; j < n; j++) {
                path[i][j] = i;
                if(i != j && g.edges[i][j] == INFTY) {
                    path[i][j] = -30000;					
                }
            }
        }     		 	
        // calculate the minimum time with updating the path 
        for(int k =0; k < n; k++) {    		
            for(int i = 0; i < n; i++) {
                if(g.edges[i][k] == INFTY) continue;
                for(int j = 0; j < n; j++) {
                    if(g.edges[k][j] == INFTY) continue;       					
                    if(g.edges[i][j] > g.edges[i][k] + g.edges[k][j]) {
                        g.edges[i][j] = g.edges[i][k] + g.edges[k][j];
                        path[i][j] = path[k][j];
                    }    					    				
                }
            }
        }			
        return path;
    }
    /**
     * Print the instruction of the path in which you can reach in minimum time.
     * @param path the 2D array of numbers as station IDs 
     * @param start the name of the starting station 
     * @param end the name of the station of destination
     * @param g the subjected graph
     */
    void printPath(long[][] path, String start, String end, Graph g) {    	
        int i = getId(g, start);
        int j = getId(g, end);
        if(i == -1 || j == -1)	{
            System.out.println("Invalud station name");
            return;
        }		
        if(g.edges[i][j] == INFTY) {
            System.out.println("No path exists");
            return;
        }
        printPath(path,i, j, g);		
        System.out.println(g.edges[i][j] + "分");
    }
    void printPath(long[][] path, int i, int j, Graph g) {
        if(i == j)	System.out.print(g.stations.get(i)+ " ― ");
        else if(path[i][j] == -30000)	System.out.print(g.stations.get(i) + " ― " + g.stations.get(j) + " ― ");
        else {
            printPath(path, i, (int)path[i][j], g);
            System.out.print(g.stations.get(j) + " ― ");
        }
    }
    /**
     * Helper: Get an ID in station name
     * @param g the subjected graph
     * @param name the name of station
     * @return the number of station ID. If ID is not found return -1.
     */
    int getId(Graph g, String name) {
        for(int i = 0; i < g.stations.size(); i++)
            if(g.stations.get(i).equals(name))	return i;

        return -1;
    }
    /**
     * Create a weighted graph represents station as a vertex, 
     * route as an edge, and required time as a weight.
     * @return the graph represents a station map
     */
    Graph createGraph() {
        n = 0;
        Graph g = new Graph();    	
        addStation(g);
        g.edges = new long[n][n+1]; 
        addEdge(g);
        return g;
    }
    /**
     * Add stations as vertices.
     * @param g the subjected graph
     */
    void addStation(Graph g) {
        try {
            File stations = new File("bin/stations.txt");
            Scanner sc = new Scanner(stations);
            while(sc.hasNextLine()) {
                sc.nextInt();
                String name = sc.next();
                g.stations.add(name);
                n++;
            }
            sc.close();
        } catch (FileNotFoundException exeption) { exeption.printStackTrace(); }  
    }
    /**
     * Add routes as edges and required times as weights.
     * @param g the graph without edges
     * @return the graph after the edges and weights added
     */
    Graph addEdge(Graph g) {      	
        int  from, to, time;    	
        try {
            File edgeList = new File("bin/edges.txt");
            Scanner sc = new Scanner(edgeList);
            // initialize the graph
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    g.edges[i][j] = (i == j)? 0 : INFTY;
                }
            }
            // store the time in each edge
            while(sc.hasNextLine()) {
                from = sc.nextInt();
                to = sc.nextInt();
                time = sc.nextInt();
                g.edges[from][to] = time;
            }    	      
            sc.close();
        } catch (FileNotFoundException exeption) { exeption.printStackTrace(); }  
        return g;
    }
    /**
     * Print the minimum traveling time to stationY from stationX in 2D array.
     * e.g. 2Darray[0][5] represents the minimum traveling time from stationID 0 to stationID 5.
     */
    void printMinTimeToEachStation(Graph g) {
        searchMinTimePath(g);
        boolean negative = false;
        for(int i =0; i < g.stations.size(); i++) 
            if(g.edges[i][i] < 0)	negative = true;
        if(negative) System.out.println("NEGATIVE CYCLE");

            for(int i = 0; i< g.stations.size(); i++) {
                for(int j = 0; j < g.stations.size(); j++) {
                    if(g.edges[i][j] == INFTY)	System.out.print("INF ");
                    else	System.out.print(g.edges[i][j] + " ");
                }
                System.out.println();
            }
    }    	
    /**
     * Test the program printing out the results for each case.
     */
    void test() {    	
        Graph g = createGraph();  
        long[][] path = searchMinTimePath(g);
        printPath(path, "五反田","目黒", g);
        printPath(path,"目黒", "大崎", g);
        printPath(path,"東京", "大手町", g);
        printPath(path,"てすと", "てすと２", g);
        printPath(path,"目黒", "てすと２", g);
        printPath(path,"千葉", "代々木上原", g);
        printPath(path,"", "", g);
        printPath(path,"品川", "千葉", g);
    }
    public static void main(String args[]) {	
        STEP_W4_HW1_Optional minTime = new STEP_W4_HW1_Optional();
        minTime.test();		
    }
}
