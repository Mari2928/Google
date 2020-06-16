import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
/**
* This program is for studying and experimenting the following algorithms:
* Greedy, Nearest Neighbor, and Nearest Insertion, to solve TSP.
* @author ashigam
*/
    public class STEP_W5_HW1 {    

    /**
     * Get the distance between 2 cities.
     * @param city1 the numbers of x-axis and y-axis for city1
     * @param city2 the numbers of x-axis and y-axis for city2
     * @return the number of distance 
     */
    double distance(Double[] city1, Double[] city2) {
        double x = Math.abs(city1[0] - city2[0]);
        double y = Math.abs(city1[1] - city2[1]);
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y,  2));
    }
    /**
     * Calculate the distances from each city to every cities.
     * @param cities the list of cities with the list of locations
     * @param N the number of cities
     * @return the list of distances from each city to every cities 
     */
    double[][] allDistance(Double[][] cities, int N){
        double[][] dist = new double[N][N];
        for(int i = 0; i < N; i ++) {
            for(int j = i; j < N; j++) 
                dist[i][j] = dist[j][i] = distance(cities[i], cities[j]);			
        }
        return dist;
    }
    /**
     * Solve TSP.
     * @param cities the list of cities with the list of locations
     * @return the list of numbers as a tour path
     */
    Integer[] solve(Double[][] cities) {
        int N = cities.length;
        double[][] dist = allDistance(cities, N);

        //try Nearest Neighbor algorithm
        ArrayList<Integer> tour = nearestNeighbor(N, dist);				

        // try Nearest Insertion algorithm to improve 
        tour = nearestInsertion(tour, dist);

        System.out.println(getTotalDistance(tour, dist));

        return tour.toArray(new Integer[tour.size()]);							
    }
    /**
     * Nearest Insertion: Take a sub-tour on each city to determine
     * which city should be selected to be inserted as well as 
     * where (between which two cities) it should be inserted. 
     * @param tour
     * @param dist
     * @return
     */
    ArrayList<Integer> nearestInsertion(ArrayList<Integer> tour , double[][] dist){
        // make a sub-tour list
        ArrayList<Integer> candidates = new ArrayList<>();
        for(int i = 0; i < tour.size(); i++)	
            candidates.add(tour.get(i));
        // select a city, remove it from tour path, and insert it to new position
        for(int i = 0; i < candidates.size(); i++) {
            int insId = candidates.get(i);  // candidate city			
            int h = tour.indexOf(insId);	// city index on tour list
            tour.remove(h);
            double incmin = Double.MAX_VALUE;
            int posmin = -1;
            for(int n = 1; n <= tour.size(); n++) {
                double inc = dist[tour.get(n-1)][insId] 
                             + dist[insId][tour.get(n%tour.size())]
                             - dist[tour.get(n-1)][tour.get(n%tour.size())];
                if(n==h? inc <= incmin : inc < incmin) {
                    posmin = n;
                    incmin = inc;
                }
            }
            tour.add(posmin, insId);// insert the candidate to nearest location
        }
        return tour;
    }
    /**
     * Nearest Neighbor: Starting from city 0, repeat the greedy algorithm N times 
     * where N as the number of cities by setting each city as a starting point
     * and find a minimum path within the tour paths.
     * @param N the number of cities
     * @param dist the list of distances from each city to every cities
     * @return the list of city numbers as a tour path
     */
    ArrayList<Integer> nearestNeighbor(int N, double[][] dist) {
        int currentCity = 0;
        double min = Double.MAX_VALUE;
        ArrayList<Integer> unvisitedCities = new ArrayList<>();
        //Integer[] tour = new Integer[0];
        ArrayList<Integer> result = new ArrayList<>();
        while(currentCity < N) {				
            buildUnvisitedCities(N, unvisitedCities, currentCity);		
            ArrayList<Integer> tour = greedy(unvisitedCities, currentCity, dist);
            min = Math.min(min, getTotalDistance(tour, dist));
            result = (min == getTotalDistance(tour, dist))? tour : result; 	
            currentCity++;
        }
        return result;
    }
    /**
     * Helper: Build the list of unvisited cities. 
     * @param N the number of cities
     * @param L the list to be overwritten
     * @param currentCity the number of current city
     * @return the list of city numbers unvisited
     */
    ArrayList<Integer> buildUnvisitedCities(int N, ArrayList<Integer> L, int currentCity){
        L.clear();
        for(int i = 0; i < N; i++)	
            if(i != currentCity) L.add(i);
        return L;
    }
    /**
     * Greedy: Take a cheapest path available from the current city.
     * @param unvisitedCities the list of unvisited city numbers
     * @param currentCity the current city number
     * @param dist the list of distances from each city to every cities
     * @return the list of city numbers as a tour path
     */
    ArrayList<Integer> greedy(ArrayList<Integer> unvisitedCities, int currentCity, double[][] dist){
        ArrayList<Integer> tour = new ArrayList<>();
        tour.add(currentCity);
        // start traveling
        while(!unvisitedCities.isEmpty()) {
            int nextCityNum = min(unvisitedCities, currentCity, dist);
            int nextCity = unvisitedCities.get(nextCityNum);
            unvisitedCities.remove(nextCityNum);
            tour.add(nextCity);
            currentCity = nextCity;
        }
        //Integer[] t = tour.toArray(new Integer[tour.size()]);
        return tour;
    }
    /**
     * Helper: Find a next closest city.
     * @param unvisitedCities the list of unvisited cities' numbers 
     * @param currentCity the number of current city 
     * @param dist the list of distances from each city to every cities
     * @return the number of next city
     */
    int min(ArrayList<Integer> unvisitedCities, int currentCity, double[][] dist) {
        double minDist = Double.MAX_VALUE;
        int nextCity = Integer.MAX_VALUE;
        for(int i = 0; i < unvisitedCities.size(); i++) {
            int next = unvisitedCities.get(i);
            if(Math.min(minDist, dist[currentCity][next]) != minDist) {
                minDist = dist[currentCity][next];
                nextCity = i;
            }				
        }			
        return nextCity;
    }
    /**
     * Get the total distance traveled.
     * @param tour the list of city numbers as a tour path 
     * @param dist the list of distances from each city to every cities
     * @return
     */
    double getTotalDistance(ArrayList<Integer> tour, double[][] dist) {
        double distance = 0;
        for(int i = 1; i < tour.size()+1; i++) {
            distance += dist[tour.get(i-1)][tour.get(i % tour.size())];
        }
        return distance;
    }
    /**
     * Helper: Read CSV file as input.	 
     * @param fileName the file name to be read
     * @return the list of locations for every cities
     */
    Double[][] readCSV(String fileName){	

        ArrayList<Double[]> cities = new ArrayList<>();
        try {
            File locations = new File(fileName);
            Scanner sc = new Scanner(locations);
            sc.nextLine();
            while(sc.hasNext()) {	  	    	
                String[] location = sc.nextLine().split(",");
                double x = Double.parseDouble(location[0]);
                double y = Double.parseDouble(location[1]);
                cities.add(new Double[] {x, y});	  	    	
            }	  	    
            sc.close();	  	    
            } catch (FileNotFoundException e) { e.printStackTrace(); } 
        Double[][] c = new Double[cities.size()][cities.size()];
        for(int i = 0; i < cities.size(); i++)
            c[i] = cities.get(i);		 		
        return c;
    }
    /**
     * Helper: Write output to CSV file.
     * @param tour the list of city numbers as a tour path
     * @param fileName the file name to be overwritten
     */
    void writeCSV(Integer[] tour, String fileName) {
        try{
            File file = new File(fileName);
            FileWriter filewriter = new FileWriter(file);

            filewriter.write("x,y\n");
            for(Integer i : tour)
                filewriter.write(i + "\n");
            filewriter.close();			
            }catch(IOException e){e.printStackTrace();}	  
    }

    public static void main(String args[]) {	
        STEP_W5_HW1 test = new STEP_W5_HW1();  

        // generate inputs and outputs for 6 challenges
        int N = 0;
        String inFile = "bin/input_0.csv";
        String OutFile = "bin/output_0.csv";    	

        while(N < 6) {    		
            inFile = inFile.substring(0, 10)+String.valueOf(N)+inFile.substring(11);        	
            Double[][] cities = test.readCSV(inFile);

            Integer[] tour = test.solve(cities);

            OutFile = OutFile.substring(0, 11)+String.valueOf(N)+OutFile.substring(12);    		
            test.writeCSV(tour, OutFile);
            N++;
        }

    }
}
