import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
/**
* This program is for studying and experimenting the following algorithms/heuristics:
* Greedy, Two-Opt, Nearest Neighbor, Nearest Insertion, and Farthest Insertion to solve TSP.
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

        // try Nearest Neighbor using Greedy improving with Two-Opt
        ArrayList<Integer> tour = nearestNeighbor(N, dist);	
        System.out.print("neighbor & two-opt: "+ getTotalDistance(tour, dist) +"|");

        // try Nearest Insertion to improve 
    //		tour = nearestInsertion(tour, dist);
    //		System.out.print("nearest: "+getTotalDistance(tour, dist) +"|");

        // Farthest Insertion is selected as it performs better than Nearest Insertion
        tour = farthestInsertion(tour, dist);
        System.out.println("farthest: "+getTotalDistance(tour, dist));

        return tour.toArray(new Integer[tour.size()]);							
    }
    /**
     * Farthest Insertion:
     * @param tour
     * @param dist
     * @return
     */
    ArrayList<Integer> farthestInsertion(ArrayList<Integer> tour , double[][] dist){
        // make a sub-tour list
        ArrayList<Integer> candidates = new ArrayList<>();
        for(int i = 0; i < tour.size(); i++)	
            candidates.add(tour.get(i));

        for(int i = candidates.size()-1; i >= 0 ; i--) {
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
            tour.add(posmin, insId);// insert the candidate to farthest location
        }				
        return tour;
    }
    /**
     * Nearest Insertion: Take a sub-tour on each city to determine
     * which city should be selected to be inserted as well as 
     * where (between which two cities) it should be inserted. 
     * @param tour the list of city numbers as a tour path 
     * @param dist the list of distances from each city to every cities
     * @return the list of city numbers as the improved tour path
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
        ArrayList<Integer> result = new ArrayList<>();
        while(currentCity < N) {				
            buildUnvisitedCities(N, unvisitedCities, currentCity);		
            ArrayList<Integer> tour = greedy(unvisitedCities, currentCity, dist);
            min = Math.min(min, getTotalDistance(tour, dist));
            result = (min == getTotalDistance(tour, dist))? tour : result; 	
            currentCity++;
        }
        // do twoOpt for one tour if input cities >= 150
    //		if(N >= 150) 
    //			result = improveWithTwoOpt(result, dist);				
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
        // do twoOpt for every combinations of tours if input cities < 150
        //if(dist.length < 150) 
            tour = improveWithTwoOpt(tour, dist);
        return tour;
    }
    /**
     * Improve a path using Two-Opt while traveling with Greedy.
     * @param tour the list of city numbers as a tour path 
     * @param dist the list of distances from each city to every cities
     * @return the list of improved tour path
     */
    ArrayList<Integer> improveWithTwoOpt(ArrayList<Integer> tour, double[][] dist){
        int improved = 0;
        while(improved < 15) {
            double bestDist = getTotalDistance(tour, dist);
            for(int i = 0; i < tour.size()-1; i++) {
                for(int k = 0; k <tour.size(); k++) {
                    ArrayList<Integer> newTour = twoOpt(tour, dist, i, k);
                    double newDist = getTotalDistance(newTour, dist);
                    if(newDist < bestDist) {
                        improved = 0;
                        bestDist = newDist;
                        tour = newTour;
                    }
                }
            }
            improved++;
        }
        return tour;
    }
    /**
     * Two-Opt: Improve a path by swapping two cities which edges are crossed.
     * @param tour the list of city numbers as a tour path
     * @param dist the list of distances from each city to every cities
     * @param i the starting index number of the path to be extracted
     * @param k the ending index number of the path to be extracted
     * @return the list of improved tour path
     */
    ArrayList<Integer> twoOpt(ArrayList<Integer> tour, double[][] dist, int i, int k){
        ArrayList<Integer> newTour = new ArrayList<>();
        for(int c = 0; c <= i-1; c++)
            newTour.add(tour.get(c));
        int dec = 0;
        for(int c = i; c <= k; c++) {
            newTour.add(tour.get(k - dec));
            dec++;
        }
        for(int c = k+1; c < tour.size(); c++)			
            newTour.add(tour.get(c));					
        return newTour;
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

        // generate inputs and outputs
        int N = 5;
        String inFile = "bin/input_0.csv";
        String OutFile = "bin/output_0.csv";    	

        while(N <= 5) {    		
            inFile = inFile.substring(0, 10)+String.valueOf(N)+inFile.substring(11);        	
            Double[][] cities = test.readCSV(inFile);

            Integer[] tour = test.solve(cities);

            OutFile = OutFile.substring(0, 11)+String.valueOf(N)+OutFile.substring(12);    		
            test.writeCSV(tour, OutFile);
            N++;
        }
    }
}
