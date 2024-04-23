import java.util.Random;
import java.util.concurrent.Semaphore;


public class task3 extends Thread {
    // Static variable to hold the thread ID
    static int tID;

    // Random number generator for generating burst times
    static Random r = new Random();

    // Number of tasks to be created
    static int tasks = r.nextInt(3, 8);  //CHANGE to [1,25] after testing

    // Array to store maximum burst times (Ready Queue) for each task
    static int[] mBurst = new int[tasks];

    // Array to store current burst times for each task
    static int[] cBurst = new int[tasks];

    // Semaphore arrays for signaling start and finish of each task
    static Semaphore[] taskStart = new Semaphore[tasks];
    static Semaphore[] taskFinish = new Semaphore[tasks];
    static int quantum = 3;
    // Total burst time for all tasks
    static int allBurst;
    static int nextTask = -1;


    // Constructor to initialize thread ID
    public task3(int id) {
        this.tID = id;
    }

    // Method to generate random maximum burst times for tasks
    public static int[] getMaxBurst() {
        for (int i = 0; i < tasks; i++) {
            int burst = r.nextInt(1, 8);  //CHANGE to [1,50] after testing
            mBurst[i] = burst;
            allBurst = mBurst[i] + allBurst;
        }
        return mBurst;
    }

    // Method to initialize current burst times for each task
    public static int[] setCurrentBurst() {
        for (int i = 0; i < tasks; i++) {
            cBurst[i] = 0;          // Set each current Burst from null to zero
        }
        return cBurst;
    }

    // Method to update the total burst time for all tasks
    public static int updateBurst(){
        allBurst = 0;   //reset all bursts

        for (int i = 0; i < tasks; i++){
            allBurst = mBurst[i] + allBurst;    //recalculate
        }
        //System.out.println("Updated All Burst: " + allBurst);
        return allBurst;
    }

    // Method to print the contents of the ready queue
    public static void printReadyQueue() {
        System.out.println("\n--------------- Ready Queue ---------------");

        for (int i = 0; i < tasks; i++) {
            System.out.println("ID:" + i + ", Max Burst: " + mBurst[i] + ", Current Burst: " + "0");
        }
        System.out.println("Sum of burst: " + allBurst);
        System.out.println("-------------------------------------------");
        System.out.println();
    }

    // Method to select the next task to be executed (Non-Preemptive SJF)
    public static synchronized int selectTask() {
        int shortestBurstTime = Integer.MAX_VALUE;
        int selectedTask = -1;

        // Iterate over the tasks to find the one with the shortest burst time
        for (int i = 0; i < tasks; i++) {
            if (mBurst[i] > 0 && mBurst[i] < shortestBurstTime) {
                shortestBurstTime = mBurst[i];
                selectedTask = i;
            }
        }
        return selectedTask;
    }
    
    // Selected task for the current thread
    int selectedTask = task3.selectTask();

    // Initialize current burst and maximum burst arrays
    static int[] setCurrent = setCurrentBurst();
    static int[] RandBurst  = getMaxBurst();

    // Run method for the thread
    @Override
    public void run(){
        while(allBurst >= 0){


            
            
            //taskStart[tID].acquireUninterruptibly();
            taskStart[selectedTask].acquireUninterruptibly();
            


            if (mBurst[selectedTask] == 0){
                //Do nothing
            }else{
                System.out.println("Dispatcher 0   | Running Process " + selectedTask + "." );
                System.out.println("Proc. Thread " + selectedTask +" | Using CPU 0; MB=" + RandBurst[selectedTask] + " , CB=0, BT=" + RandBurst[selectedTask] + " , BG:=" + RandBurst[selectedTask]);


            }
            
            if (mBurst[selectedTask] == 0){
                System.out.print("");

            }
            else if (mBurst[selectedTask] < quantum){
                for (int i = 0; i < mBurst[selectedTask]; i++){
                    System.out.println("Proc. Thread " + selectedTask +" | Using CPU 0; On Burst " + setCurrent[selectedTask] + ".");
                    setCurrent[selectedTask]++;

                    
                    //decrement burst value
                    mBurst[selectedTask]--;
                    

                }
            }
            else{
                for (int i = 0; i < quantum; i++){

                    System.out.println("Proc. Thread " + selectedTask +" | Using CPU 0; On Burst " + setCurrent[selectedTask] + ".");
                    setCurrent[selectedTask]++;


                    //decrement burst value
                    mBurst[selectedTask]--;
                }

            }
           
            updateBurst();

            if (allBurst == 0){
                System.out.println();
                System.out.println("Main thread     | Exiting.");
                break;
            }
            
            taskFinish[selectedTask].release();


        }

    }

    // Main method
    public static void main(String[] args) {
        
        System.out.println("# of threads = " + tasks );
        for (int i = 0; i < tasks; i++){
            System.out.println("Main thread     | Creating process thread "  + i);
        }
        System.out.println();

        printReadyQueue();
        
        System.out.println("Main thread     | Forking dispatcher 0");
        System.out.println("Dispatcher 0    | Using CPU 0");
        System.out.println("Dispatcher 0    | Now releasing dispatchers.");
        System.out.println();
        System.out.println("Dispatcher 0    | Non-Preemptive Shortest Job First, Time Quantum = " + quantum);
        System.out.println();

        
        for (int i = 0; i < tasks; i++) {
            taskStart[i] = new Semaphore(0);

        }

        for (int i = 0; i < tasks; i++) {
            taskFinish[i] = new Semaphore(0);

        }

        for (int i = 0; i < tasks; i++) {
            dispatcher3 t = new dispatcher3(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core3 t = new core3(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            task3 t = new task3(i);
            t.start();
        }
    }
    
}


class dispatcher3 extends Thread {
    static int tID;
    static Semaphore dispatcherStart = new Semaphore(1);


    public dispatcher3(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task3.allBurst > 0){

            dispatcherStart.acquireUninterruptibly();
            //System.out.println("Dispatcher Here");
            //System.out.println("dispatcher: " + tID);
            

            //select task from ready Queue

            int selectedTask = task3.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core3.coreStart.release();
        }
        

    }
}




class core3 extends Thread {
    int tID;
    static Semaphore coreStart = new Semaphore(0);
    static Semaphore[] taskStart = task3.taskStart;
    static Semaphore[] taskFinish = task3.taskFinish;
    int selectedTask = task3.selectTask();



    public core3(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task3.allBurst > 0){
            coreStart.acquireUninterruptibly();


            taskStart[selectedTask].release();

            
            taskFinish[selectedTask].acquireUninterruptibly();



            System.out.println();

            dispatcher3.dispatcherStart.release();
        }
    
        
    }
}

