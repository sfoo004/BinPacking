
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stevefoo
 */
public class Bin_Packing {
    
    public static void main(String[] args) {
        for (String str : args) {
            try {
                if (args.length == 0) {
                System.out.println("ERROR invalid inputs");
                break;
                }
                System.out.print("\n" + str + " ");
                double start = System.nanoTime();
                construct(str);
                double stop = System.nanoTime();
                double math = (stop - start) / 1000000000;
                System.out.printf("TIME: %.4f seconds\n", math);
            } catch (IOException e) {
                System.err.println("Error processing" + str);
            }catch (NumberFormatException n) {//checks for wrong input being passed
                System.err.println("Error processing" + str);
            }
        }
    }
     static void construct(String str) throws FileNotFoundException, IOException{   
        BufferedReader in = new BufferedReader(new FileReader(str));
        
        //Holds the first ten bins 
        HashMap<Integer, LinkedList<Integer>> firsten_offnextfit = new HashMap<>();
        HashMap<Integer, LinkedList<Integer>> firsten_onnextfit = new HashMap<>();
        HashMap<Integer, LinkedList<Integer>> firsten_offworstfit = new HashMap<>();
        HashMap<Integer, LinkedList<Integer>> firsten_onworstfit = new HashMap<>();
        HashMap<Integer, LinkedList<Integer>> firsten_offbestfit = new HashMap<>();
        HashMap<Integer, LinkedList<Integer>> firsten_onbestfit = new HashMap<>();
        HashMap<Integer, LinkedList<Integer>> firsten_offfirstfit = new HashMap<>();
        HashMap<Integer, LinkedList<Integer>> firsten_onfirstfit = new HashMap<>();
        
        ArrayList<Integer> offline_order = new ArrayList<>();
        ArrayList<Integer> online_order = new ArrayList<>();
        
        //List for each fit
        ArrayList<bin> on_nextfit= new ArrayList<>();
        LinkedList<bin3> on_firstfit= new LinkedList<>();
        NavigableSet<bin2> on_bestfit= new TreeSet<>();
        PriorityQueue<bin> on_worstfit= new PriorityQueue<>();
        ArrayList<bin> off_nextfit= new ArrayList<>();
        LinkedList<bin3> off_firstfit= new LinkedList<>();
        NavigableSet<bin2> off_bestfit= new TreeSet<>();
        PriorityQueue<bin> off_worstfit= new PriorityQueue<>();
        
        double sumoforders = 0;
        String input = "";
        
        while((input = in.readLine())!=null){
           int order = Integer.parseInt(input);
           if(order>999999999 || order < 1){
               System.out.println("invalid input");
               continue;
           }
           
           nextfit(order, on_nextfit, firsten_onnextfit);
           worstfit(order, on_worstfit, firsten_onworstfit);
           bestfit(order, on_bestfit, firsten_onbestfit);
           online_order.add(order);
           offline_order.add(order);
           sumoforders+=order;
        }
        System.out.print("Ideal # of bins: " + Math.ceil(sumoforders / 1000000000));
        print("Online Nextfit", on_nextfit, sumoforders,firsten_onnextfit);
        print("Online Worstfit", on_worstfit, sumoforders, firsten_onworstfit);
        print("Online Bestfit", on_bestfit, sumoforders, firsten_onbestfit);
        on_nextfit.clear();//free up memory
        on_worstfit.clear();//free up memory
        on_bestfit.clear();//free up memory
        
        firstfit(online_order, on_firstfit,firsten_onfirstfit);
        print("Online Firstfit", on_firstfit, sumoforders, firsten_onfirstfit);
        online_order.clear();//free up memory
        on_firstfit.clear();//free up memory
        
        Collections.sort(offline_order);
        Collections.reverse(offline_order);//puts offline list in descending order
        
        for(int order:offline_order){
        nextfit(order, off_nextfit, firsten_offnextfit);
        worstfit(order, off_worstfit, firsten_offworstfit);
        bestfit(order, off_bestfit, firsten_offbestfit);
        }
        firstfit(offline_order, off_firstfit, firsten_offfirstfit);
        print("Offline Nextfit", off_nextfit, sumoforders, firsten_offnextfit);
        print("Offline Worstfit", off_worstfit, sumoforders, firsten_offworstfit);
        print("Offline Bestfit", off_bestfit, sumoforders, firsten_offbestfit);
        print("Offline Firstfit", off_firstfit, sumoforders, firsten_offfirstfit);
        
    }
    
    static void nextfit(int order, ArrayList<bin> o_nextfit,  HashMap<Integer, LinkedList<Integer>> firsten){
        if(!o_nextfit.isEmpty() && (o_nextfit.get(o_nextfit.size()-1).total+order) <= 1000000000){//checks to see if previous plus order <= 1 billion
            o_nextfit.get(o_nextfit.size()-1).add(order);
            if(o_nextfit.size()<11)
                firsten.get(o_nextfit.size()).add(order);
        }else{
            bin t = new bin(order);
            o_nextfit.add(t);
            if(o_nextfit.size()<11){
                firsten.put(o_nextfit.size(), new LinkedList<Integer>());
                firsten.get(o_nextfit.size()).add(order);
            }
        }
    }
    static void bestfit(int order, NavigableSet<bin2> o_bestfit, HashMap<Integer, LinkedList<Integer>> firsten){
        
        bin2 temp = new bin2(1000000000 - order, o_bestfit.size()+1);//creates a temp bin with least priority for bin #
        
        bin2 b = o_bestfit.floor(temp);//finds greatest lower or equal to temp bin
        if (!o_bestfit.isEmpty() && b != null && (b).available >= order) {
            o_bestfit.remove(b);
            b.add(order);
            o_bestfit.add(b);
            if(b.bin_number<11)
                firsten.get(b.bin_number).add(order);
        } else {
            bin2 t = new bin2(order, o_bestfit.size()+1);
            o_bestfit.add(t);
            if(o_bestfit.size()<11){
                firsten.put(o_bestfit.size(), new LinkedList<Integer>());
                firsten.get(o_bestfit.size()).add(order);
            }
                
        }
    }
    static void worstfit(int order, PriorityQueue<bin> o_worstfit, HashMap<Integer, LinkedList<Integer>> firsten){
        if(!o_worstfit.isEmpty()&& (o_worstfit.peek().total+order)<=1000000000){//checks to see if head plus order <= 1 billion
           bin temp = o_worstfit.poll();
           temp.add(order);
           o_worstfit.add(temp);
           if(temp.bin_number<11)
                firsten.get(temp.bin_number).add(order);
        }
        else{
            bin t = new bin(order, o_worstfit.size()+1);
            o_worstfit.add(t);
            if(o_worstfit.size()<11){
                firsten.put(o_worstfit.size(), new LinkedList<Integer>());
                firsten.get(o_worstfit.size()).add(order);
            }
        }
    }
    
    static void firstfit(ArrayList<Integer> line_order, LinkedList<bin3> o_firstfit, HashMap<Integer, LinkedList<Integer>> firsten) {
        int temp = 3;
        do {
            temp = 2 * (temp + 1) - 1;
        } while (temp < line_order.size());//finds the number of heap bins neccessary to create a complete binary heap
        bin3[] tournament = new bin3[2 * (temp + 1) - 1];// heap number is "doubled" again and binary heap array is created
        bin3 t = new bin3(0);
        Arrays.fill(tournament, t); // arrays is filled with bins set to 0
        bin3 b = new bin3(0);
        for (int order : line_order) {
            if (!o_firstfit.isEmpty() && (b = heap(tournament, order)).total != 0) {// if the bin that is returned has value of 0 then there is no bin to fit order            
                b.add(order);
                redotree(tournament, (tournament.length / 2) - 1 + b.bin_number);
                if (b.bin_number < 11) {
                    firsten.get(b.bin_number).add(order);
                }

            } else {
                t = new bin3(order, o_firstfit.size() + 1);
                o_firstfit.add(t);
                tournament[(tournament.length / 2) - 1 + t.bin_number] = t;
                redotree(tournament, (tournament.length / 2) - 1 + t.bin_number);
                if (o_firstfit.size() < 11) {
                    firsten.put(o_firstfit.size(), new LinkedList<Integer>());
                    firsten.get(o_firstfit.size()).add(order);
                }
            }
        }
    }
    static bin3 heap(bin3 [] winner, int order){// searches through the heap to find the first bin that can hold the order
        int place = 0;
        int child = 0;
        while((child=(2*place)+1) < winner.length){//searches with preference to left child
            if(winner[child].available >= order)
                place = child;
            else 
                place = child+1;
        }
        return winner[place];      
    }
    static void redotree(bin3 [] winner, int child){
        int parent = (int)Math.floor((child-1)/2);
        int child2 = 0;
        while(parent != 0){// heap will perculate from bottom up changing child's parents
            if(child%2==1){//left child
                child2 = child+1;
            }else{//right child
                child2 = child-1;
            }
            if(winner[child2].available>winner[child].available){ //checks to see which child has more space available
                winner[parent]=winner[child2];//winner becomes new parents
            }else{
                winner[parent]=winner[child];//winner becomes new parent               
            }
            child = parent;
            parent = (int)Math.floor((child-1)/2);
        }
        
    }  
     static void print(String list, ArrayList<bin> binlist, double sumoforders, HashMap<Integer, LinkedList<Integer>> firsten) {
        System.out.print("\n" + list + "\nSize: " + binlist.size() + "\nFirst ten Bins: \n");
        for(int key: firsten.keySet()) {
                System.out.println("bin "+ key + ": "+ firsten.get(key).toString());
            }
    }
     
     static void print(String list, LinkedList<bin3> binlist, double sumoforders, HashMap<Integer, LinkedList<Integer>> firsten) {
        System.out.print("\n" + list + "\nSize: " + binlist.size() + "\nFrist ten Bins: \n");
        for(int key: firsten.keySet()) {
                System.out.println("bin "+ key + ": "+ firsten.get(key).toString());
            }
    }

    static void print(String list, PriorityQueue<bin> binlist, double sumoforders, HashMap<Integer, LinkedList<Integer>> firsten) {
        System.out.print("\n" + list + "\nSize: " + binlist.size() + "\nFirst ten Bins:\n ");
       for(int key: firsten.keySet()) {
                System.out.println("bin "+ key + ": "+ firsten.get(key).toString());
            }
    }

    static void print(String list, NavigableSet<bin2> binlist, double sumoforders, HashMap<Integer, LinkedList<Integer>> firsten) {
        System.out.print("\n" + list + "\nSize: " + binlist.size() + "\nFirst ten Bins: \n");
        for(int key: firsten.keySet()) {
                System.out.println("bin "+ key + ": "+ firsten.get(key).toString());
            }
    }
    
    static class bin implements Comparable<bin>{
        int total = 0;
        int bin_number;
        
        bin(int item){
            total+= item;            
        }
        bin(int item, int bin_number){
            this.bin_number = bin_number;
            total+= item;            
        }
        void add(int item){
            total+=item;            
        }
        @Override
        public int compareTo(bin o) {
            return this.total - o.total; //To change body of generated methods, choose Tools | Templates.
        }
    }
    static class bin2 implements Comparable<bin2>{
        
        int total = 0;
        int bin_number;
        int available = 1000000000;
        
        bin2(int item){
            total+= item;
            available-= item;
        }
        bin2(int item, int bin_number){
            total+= item;
            available -= item;
            this.bin_number=bin_number;
        }
        void add(int item){
            total+=item;
            available -= item;
            
        }
        @Override
        public int compareTo(bin2 o) {
            if(this.total==o.total){
                    return this.bin_number-o.bin_number;
            }else{
                return this.total - o.total;             
            }
        }
        
    }
    static class bin3 {
        
        int total = 0;
        int bin_number;
        int available = 1000000000;
        
        bin3(int item){
            total+= item;
            available-= item;
        }
        bin3(int item, int bin_number){
            total+= item;
            available -= item;
            this.bin_number=bin_number;
        }
        void add(int item){
            total+=item;
            available -= item;
            
        }
        
    }
    
}
