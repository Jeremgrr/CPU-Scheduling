import java.util.Random;
import java.util.concurrent.Semaphore;

/*
 * CURRENT PROBLEMS
 * 
 * 1) Tasks finish with a single burst not being completed
 * 2) Deadlock occurs before all bursts finish
 */

public class task1 extends Thread {
    static int tID;
 

    //static List<Integer> readyQ = new ArrayList<>();   MAYBE USE ARRAYLIST

    static Random r = new Random();
    static int tasks = r.nextInt(3,8);  //CHANGE to [1,25] after testing
    static int[] mBurst = new int[tasks];           //mBurst is the Ready Queue
    static int[] maxBurst = new int[tasks];
    static int[] cBurst = new int[tasks];
    static Semaphore[] taskStart = new Semaphore[tasks];
    static Semaphore[] taskFinish = new Semaphore[tasks];
    static int allBurst;
    static int nextTask = -1;

    public task1( int id) {
        this.tID = id;
    }


    public static int[] getMaxBurst(){
        for (int i = 0; i < tasks; i++){
            int burst = r.nextInt(1,8);  //CHANGE to [1,50] after testing
            mBurst[i] = burst;
            maxBurst[i] = mBurst[i];
            allBurst = mBurst[i] + allBurst;
        }
        return mBurst;
    }
    
    public static int[] setCurrentBurst(){
        for (int i = 0; i < tasks; i++){
            cBurst[i] = 0;          //set each current Burst from null to zero
        }
        return cBurst;
    }

    public static int updateBurst(){
        allBurst = 0;   //reset all bursts

        for (int i = 0; i < tasks; i++){
            allBurst = mBurst[i] + allBurst;    //recalculate
        }
        //System.out.println("Updated All Burst: " + allBurst);
        return allBurst;
    }

    public static void printReadyQueue(){
        System.out.println("\n--------------- Ready Queue ---------------");

        for (int i = 0; i < tasks; i++){
            System.out.println("ID:" + i + ", Max Burst: " + RandBurst[i] + ", Current Burst: " + "0");
        }
        System.out.println("Sum of burst: " + allBurst);
        System.out.println("-------------------------------------------");
        System.out.println();


    }

    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % tasks;
        return taskID;
    }
    int selectedTask = task1.selectTask();

    static int[] setCurrent = setCurrentBurst();
    static int[] RandBurst  = getMaxBurst();

    @Override
    public void run(){
        while(allBurst > 0){
 
            taskStart[selectedTask].acquireUninterruptibly();
            
            //loop once for allotted burst
            //System.out.println("TASK STARTED");
            //System.out.println("Currently running task " + selectedTask);
            //System.out.println("This task has " + mBurst[selectedTask] + " Bursts remaining");
            
            //Adds Dispatcher 'Running Process Tag' before running bursts
            if (mBurst[selectedTask] == 0){
                //Do nothing
                break;
            }else{
                System.out.println("Dispatcher 0   | Running Process " + selectedTask + "." );
                //this is why we get the case of 1 print at 2??
                System.out.println("Proc. Thread " + selectedTask +" | Using CPU 0; MB=" + maxBurst[selectedTask] + " , CB=0, BT=" + RandBurst[selectedTask] + " , BG:=" + RandBurst[selectedTask]);


            }
            
            
            for (int i = 0; i < maxBurst[selectedTask]; i++){
                System.out.println("Proc. Thread " + selectedTask +" | Using CPU 0; On Burst " + setCurrent[selectedTask] + ".");
                setCurrent[selectedTask]++;

                
                //decrement burst value
                mBurst[selectedTask]--;

                allBurst--;
                

            }
        
           
            //updateBurst();

            if (allBurst == 0){
                System.out.println();
                System.out.println("Main thread     | Exiting.");
                break;
            }
            
            taskFinish[selectedTask].release();
            //System.out.println("Task Finished");

            //test to see value of sum of bursts and each individual burst
            /* 
            System.out.println("all burst is " + allBurst);
            for (int i = 0; i < tasks ; i++){
                System.out.println("mBurst at " + i + " equals " + mBurst[i]);
            }
            */

        }

    }

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
        System.out.println("Dispatcher 0    | Running First Come, First Serve (FCFS) ");
        System.out.println();

        
        for (int i = 0; i < tasks; i++) {
            taskStart[i] = new Semaphore(0);

        }

        for (int i = 0; i < tasks; i++) {
            taskFinish[i] = new Semaphore(0);

        }

        for (int i = 0; i < tasks; i++) {
            dispatcher1 t = new dispatcher1(i);
            t.start();
        }
        
        /*Threads for dispatcher and core 1 */
        for (int i = 0; i < tasks; i++) {
            core1 t = new core1(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            task1 t = new task1(i);
            t.start();
        }

     



    }
    
}








class dispatcher1 extends Thread {
    static int tID;
    static Semaphore dispatcherStart = new Semaphore(1);


    public dispatcher1(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task1.allBurst > 0){

            dispatcherStart.acquireUninterruptibly();
            //System.out.println("Dispatcher Here");
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task1.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core1.coreStart.release();
        }
        

    }
}




class core1 extends Thread {
    int tID;
    static Semaphore coreStart = new Semaphore(0);
    static Semaphore[] taskStart = task1.taskStart;
    static Semaphore[] taskFinish = task1.taskFinish;
    int selectedTask = task1.selectTask();



    public core1(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task1.allBurst > 0){
            coreStart.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

      

            taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher1.dispatcherStart.release();
        }
    
        
    }
}



