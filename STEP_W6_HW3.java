import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
/**
* This program aims to find a better solution for a large number of cities over 2048 for TSP.
* Entering the city number to start traveling, it searches 30 different paths 
* incrementing the starting city number (Greedy + partial Two-Opt), 
* find the best city to be improved (Nearest Neighbor), and generates 
* one fully improved tour path within the paths (full Two-opt + Farthest Insertion).
* Find a better solution by using multiple terminals diversifying the searching part.
* @author ashigam
*/
public class STEP_W6_HW3 {    

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
     * @param startCity the starting city number
     * @return the list of numbers as a tour path
     */
    ArrayList<Integer> solve(Double[][] cities, int startCity) {
        int N = cities.length;
        double[][] dist = allDistance(cities, N);

        // Nearest Neighbor with Greedy and partial Two-Opt
        // open multiple terminals, select some start cities, and find a best city on each terminal
        ArrayList<Integer> bestCity = nearestNeighborFindBest(128, dist, startCity); // 30 different starting cities 

        // focus on one start city to apply full Two-Opt
        ArrayList<Integer> tour = improveBestCity(dist, bestCity);
        System.out.print("neighbor & two-opt: "+ getTotalDistance(tour, dist) +"|");

        // Farthest Insertion to fully improve
        tour = farthestInsertion(tour, dist);
        System.out.println("farthest: "+ getTotalDistance(tour, dist));

        return tour;							
    }

    /**
     * Farthest Insertion: In opposed to Nearest Insertion, 
     * this starts a tour from the farthest city.  
     * @param tour the list of city numbers as a tour path 
     * @param dist the list of distances from each city to every cities
     * @return the list of city numbers as the improved tour path
     */
    ArrayList<Integer> farthestInsertion(ArrayList<Integer> tour , double[][] dist){		
        ArrayList<Integer> candidates = buildSubTourList(tour);		
        for(int i = candidates.size()-1; i >= 0 ; i--) {
            int insId = candidates.get(i);  // candidate city			
            int h = tour.indexOf(insId);	// city index on tour list
            tour.remove(h);
            int posmin = findMinPosition(insId, dist, tour, h);
            tour.add(posmin, insId);// insert the candidate to nearest location
        }				
        return tour;
    }

    /**
     * Helper: Build a sub-tour list.
     * @param tour the list of city numbers as a tour path 
     * @return the list of candidates' cities.
     */
    ArrayList<Integer> buildSubTourList(ArrayList<Integer> tour){
        ArrayList<Integer> candidates = new ArrayList<>();
        for(int i = 0; i < tour.size(); i++)	
            candidates.add(tour.get(i));
        return candidates;
    }

    /**
     * Helper: Find a position (index) for a candidate city to be inserted
     * in which the distance is minimized.
     * @param insId the candidate city number
     * @param dist the list of distances from each city to every cities
     * @param tour the list of city numbers as a tour path 
     * @param h the index number of the candidate city on the original tour
     * @return the index number to insert the candidate city
     */
    int findMinPosition(int insId, double[][] dist, ArrayList<Integer> tour, int h) {
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
        return posmin;
    }

    /**
     * Nearest Neighbor with partial improve to find a best start city: 
     * Starting from the current city, repeat the greedy algorithm N times 
     * where N as the number of cities by setting each city as a starting point
     * and find a minimum path within the tour paths.
     * @param N the number of cities
     * @param dist the list of distances from each city to every cities
     * @param currentCity the current city number
     * @return the list of city numbers as a tour path
     */
    ArrayList<Integer> nearestNeighborFindBest(int N, double[][] dist, int currentCity) {
        int bestCity = currentCity;
        int start = currentCity;
        double min = Double.MAX_VALUE; 
        ArrayList<Integer> result = new ArrayList<Integer>();
        while(currentCity < (N + start) ) {			
            ArrayList<Integer> tour = greedy(currentCity, dist);
            tour = improveWithPartialTwoOpt(tour, dist);
            double currentDist = getTotalDistance(tour, dist);
            if(currentDist < min) {
                min = currentDist;
                result = tour;
                bestCity = currentCity;
            }
            currentCity++;
        }	
        System.out.println("best city: "+ bestCity);
        return result;
    }

    /**
     * Focus on one tour and apply Two-Opt fully.
     * @param dist the list of distances from each city to every cities
     * @param bestCity the list of tour path from the best city
     * @return the list of city numbers as a tour path
     */
    ArrayList<Integer> improveBestCity(double[][] dist, ArrayList<Integer> bestCity) {        	
    //        ArrayList<Integer> tour = greedy(bestCity, dist);
    //        bestCity = ;
        return improveWithFullTwoOpt(bestCity, dist);
    }

    /**
     * Helper: Build the list of unvisited cities. 
     * @param N the number of cities
     * @param currentCity the number of current city
     * @return the list of city numbers unvisited
     */
    ArrayList<Integer> buildUnvisitedCities(int N, int currentCity){
        ArrayList<Integer> L = new ArrayList<>();
        for(int i = 0; i < N; i++)	
            if(i != currentCity) L.add(i);
        return L;
    }

    /**
     * Greedy: Take a cheapest path available from the current city.
     * @param currentCity the current city number
     * @param dist the list of distances from each city to every cities
     * @return the list of city numbers as a tour path
     */
    ArrayList<Integer> greedy(int currentCity, double[][] dist){
        ArrayList<Integer> unvisitedCities = buildUnvisitedCities(dist.length, currentCity);	
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
        return tour;
    }

    private int noImprove;
    /**
     * Improve a path with partial Two-Opt (for loop only).
     * @param tour the list of city numbers as a tour path 
     * @param dist the list of distances from each city to every cities
     * @return the list of improved tour path
     */
    ArrayList<Integer> improveWithPartialTwoOpt(ArrayList<Integer> tour, double[][] dist){
        int len = tour.size();
        double bestDist = getTotalDistance(tour, dist);	
        for(int i = 0; i < len-1; i++) {		
            for(int k = i+1; k < len-1; k++) {
                ArrayList<Integer> newTour = twoOpt(tour, dist, i, k);
                double newDist = getTotalDistance(newTour, dist);
                bestDist = updateBestDist(newDist, bestDist);
                if(newDist == bestDist) tour = newTour;
            }
        }
        return tour;
    }

    /**
     * Helper: Update the best distance and the improve status.
     * @param newDist the number of distance newly found 
     * @param bestDist the number of best distance found so far
     * @return the number of best distance
     */
    double updateBestDist(double newDist, double bestDist) {
        if(newDist < bestDist) {
            noImprove = 0;
            return newDist;   
        } 
        return bestDist;
    }
    /**
     * Improve a path with full Two-Opt (for loop and while loop).
     * @param tour the list of city numbers as a tour path
     * @param dist the list of distances from each city to every cities
     * @return the list of improved tour path
     */
    ArrayList<Integer> improveWithFullTwoOpt(ArrayList<Integer> tour, double[][] dist){
        noImprove = 0;
        while(noImprove < 2) { // break if no improvement continues more than two times    	
            tour = improveWithPartialTwoOpt(tour, dist);
            noImprove++;         
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
        Double[][] c = new Double[cities.size()][2];
        for(int i = 0; i < cities.size(); i++)
            c[i] = cities.get(i);		 		
        return c;
    }

    /**
     * Helper: Write output to CSV file.
     * @param tour the list of city numbers as a tour path
     * @param fileName the file name to be overwritten
     */
    void writeCSV(ArrayList<Integer> tour, String fileName) {
        try{
            File file = new File(fileName);
            FileWriter filewriter = new FileWriter(file);

            filewriter.write("x,y\n");
            for(int i = 0; i < tour.size(); i++)
                filewriter.write(tour.get(i) + "\n");
            filewriter.close();			
            }catch(IOException e){e.printStackTrace();}	  
    }

    public static void main(String args[]) {	
        STEP_W6_HW3 test = new STEP_W6_HW3();  

        // generate inputs and outputs 
        String inFile = "bin/input_4.csv";
        String OutFile = "bin/outpu_4.csv";

        Scanner sc = new Scanner(System.in);

        Double[][] cities = test.readCSV(inFile);

        System.out.println("Enter the start city: ");
        int currentCity = sc.nextInt(); // input the start city
        sc.close();

        ArrayList<Integer> tour = test.solve(cities, currentCity);   		
        test.writeCSV(tour, OutFile);

    }
}
