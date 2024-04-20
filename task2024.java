import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class task2024 extends Thread {
    static int tID;
 

    //static List<Integer> readyQ = new ArrayList<>();   MAYBE USE ARRAYLIST

    static Random r = new Random();
    static int tasks = r.nextInt(3,8);  //CHANGE to [1,25] after testing
    static int[] mBurst = new int[tasks];           //mBurst is the Ready Queue
    static Semaphore[] taskStart = new Semaphore[tasks];
    static Semaphore[] taskFinish = new Semaphore[tasks];


    public static int[] getMaxBurst(){
        for (int i = 0; i < tasks; i++){
            int burst = r.nextInt(1,8);  //CHANGE to [1,50] after testing
            mBurst[i] = burst;
            //System.out.println(mBurst[i]);
        }
        return mBurst;
    }

    static int[] RandBurst  = getMaxBurst();

    @Override
    public void run(){
        while(true){

            //TO-DO:
            //FIGURE OUT HOW TO USE A SEMAPHORE ARRAY AS A
            
            
            taskStart[tID].acquireUninterruptibly();
            //loop once for allotted burst
            System.out.println("Task Start");
            taskFinish[tID].release();
            System.out.println("Task Finished");

        }

    }

    public static void main(String[] args) {
        System.out.println("number of tasks it " + tasks );
        System.out.println();
        
        for (int i = 0; i < tasks; i++) {
            taskStart[i] = new Semaphore(0);

        }

        for (int i = 0; i < tasks; i++) {
            taskFinish[i] = new Semaphore(1);

        }

        for (int i = 0; i < tasks; i ++) {
            dispatcher24 t = new dispatcher24(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i ++) {
            core24 t = new core24(i);
            t.start();
        }
        
    }
    
}

class dispatcher24 extends Thread {
    int tID;
    static Semaphore dispatcherStart = new Semaphore(1);


    public dispatcher24(int id){
        this.tID = id;
    }

    public int getTaskID() {
        return tID;
    }

    @Override
    public void run() {
        while(true){

            dispatcherStart.acquireUninterruptibly();
            System.out.println("Dispatcher Here");
            //System.out.println("dispatcher: " + tID);

            //select task from ready Queue
            int queuedTask = task2024.mBurst[tID];
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core24.coreStart.release();
        }
        

    }
}


class core24 extends Thread {
    int tID;
    static Semaphore coreStart = new Semaphore(0);
    static Semaphore[] taskStart = task2024.taskStart;
    static Semaphore[] taskFinish = task2024.taskFinish;
    static int[] mBurst = task2024.mBurst;


    public core24(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(true){
            coreStart.acquireUninterruptibly();
            System.out.println("Core Here");
            //System.out.println("core: " + tID);

            //update assigned Task's allotted burst
            int taskID = tID;

            taskStart[mBurst[tID]].release();
            System.out.println("taskStart release");
            taskFinish[task2024.tID].acquireUninterruptibly();

            System.out.println("Back to Core");
            System.out.println();

            dispatcher24.dispatcherStart.release();
        }
    
        
    }
}
