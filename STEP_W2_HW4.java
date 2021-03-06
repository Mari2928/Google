import java.util.*;
/**
* This program updates the cache every time URL is accessed.
* It uses HashMap to achieve search, add, remove an element in O(1)
* and uses customized doubly-linked-list to update the cache in O(1).
* Please run cacheTest() to check test cases. 
* @author ashigam
*/
public class STEP_W2_HW4 {
    // declare instance variables
    private Node head = null;
    private Node tail = null;
    private int cacheSize;
    private HashMap<String, Node> map = new HashMap<String, Node>();

    /**
     * Constructor: initialize cache size, HashMap, and head Node 
     * @param N the number of cache size 
     */
    public STEP_W2_HW4(int N){
        this.cacheSize = N;
    }
    // Node for DLList
    class Node{
        String url;
        String contents;// to remove a node from HashMap
        Node prev;
        Node next;
        // constructor
        Node(String u, String c){ url = u; contents = c; }
    }
    /**
     * Access a page and update the cache so that it stores the most
     * recently accessed N pages in O(1) except in time to remove tail.
     * @param url the accessed URL
     * @param contents the contents of the URL
     */
    void accessPage(String url, String contents) {

        // contents are in the map: move it up to head
        if(map.containsKey(contents)) {
            moveUp(map.get(contents));
            return;
        }
        // map is full: remove tail and push to head
        if(map.size() == cacheSize) {
            removeTail();
        }
        // add data to HashMap and use the URL data as a node for DLList 
        Node u = new Node(url, contents);
        map.put(contents, u);
        push(u);				
    }
    /**
     * Helper: Move a node up to the head by updating DLList pointers
     * @param u the node to be moved up
     */
    void moveUp(Node u) {
        if(u.prev == null)	// u is head
            return;	
        if(u.next == null) {// u is tail
            u.prev.next = null;		
            tail = u.prev;
        }
        else {			  	// u is in middle		
            u.prev.next = u.next;
            u.next.prev = u.prev;
        }		
        // bring u to head
        u.prev = null;
        head.prev = u;
        u.next = head;
        head = u;
    }
    /**
     * Helper: Remove a tail from DLList and from HashMap 
     */
    void removeTail() {
        String contents = tail.contents;
        if(tail.prev == null ) {	// tail only
            tail = null;
            head = null;
        }
        else {	// tail has a prev node
            tail.prev.next = null;
            tail = tail.prev;			
        }
        map.remove(contents);		
    }
    /**
     * Helper: Push a node to the head of the DLList
     * @param newN the node to be pushed to the head
     */
    void push(Node newN) {
        newN.next = head;
        newN.prev = null;
        if(head != null) 
            head.prev = newN;	
        else
            tail = newN;
        head = newN;
    }
    /**
     * Return the URLs stored in the cache. The URLs are ordered
     * in the order in which the URLs are mostly recently accessed.
     * @return the list of URLs
     */
    String[] getPages() {
        ArrayList<String> pages = new ArrayList<>();
        Node temp = head;
        while(temp != null) {
            pages.add(temp.url);
            temp = temp.next;
        }
        return 	pages.toArray(new String[pages.size()]);	
    }
    /**
     * To check if all test cases are passed.
     */
    static void cacheTest() {
        STEP_W2_HW4 cache = new STEP_W2_HW4(4);

        System.out.println(Arrays.equals(cache.getPages(), new String[] {}));

        cache.accessPage("a.com", "AAA");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"a.com"}));

        cache.accessPage("b.com", "BBB");	
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"b.com","a.com"}));

        cache.accessPage("c.com", "CCC");		
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"c.com","b.com","a.com"}));

        cache.accessPage("d.com", "DDD");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"d.com","c.com","b.com","a.com"}));

        cache.accessPage("d.com", "DDD");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"d.com","c.com","b.com","a.com"}));

        cache.accessPage("a.com", "AAA");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"a.com","d.com","c.com","b.com"}));

        cache.accessPage("c.com", "CCC");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"c.com","a.com","d.com","b.com"}));

        cache.accessPage("a.com", "AAA");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"a.com","c.com","d.com","b.com"}));

        cache.accessPage("a.com", "AAA");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"a.com","c.com","d.com","b.com"}));

        cache.accessPage("e.com", "EEE");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"e.com","a.com","c.com","d.com"}));

        cache.accessPage("f.com", "FFF");
        System.out.println(Arrays.equals(cache.getPages(), new String[] {"f.com","e.com","a.com","c.com"}));			
    }
    public static void main(String args[]) {		
        cacheTest();	  			
    }
}
