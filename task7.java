import java.util.Random;
import java.util.concurrent.Semaphore;



//task

//dispatch

//{dispatcher 0}

//core

/*
 * CURRENT PROBLEMS
 * 
 * 1. print forks for other disp. and core 2-4
 * 2. clean up up prints??
 * 
 */

public class task7 extends Thread {
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

    public task7( int id) {
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

    public static int selectTask() {
        int shortestIndex = -1;
        for ( int i = 0; i < tasks; i++ ){
            if (mBurst[i] != 0 && (shortestIndex == -1 || mBurst[i] < mBurst[shortestIndex])){
                shortestIndex = i;
            }
        }

        if (shortestIndex == -1){
            throw new IllegalArgumentException("No non-zero integer found", null);
        }
        //System.out.println("returning " + shortestIndex);
        
        return shortestIndex;
    }
    int selectedTask = task7.selectTask();

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
                //System.out.println("Dispatcher 0   | Running Process " + selectedTask + "." );
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
        System.out.println("Dispatcher 0    | Running Non-Premptive , Shortest Job First (NSJF) ");
        System.out.println();

        
        for (int i = 0; i < tasks; i++) {
            taskStart[i] = new Semaphore(0);

        }

        for (int i = 0; i < tasks; i++) {
            taskFinish[i] = new Semaphore(0);

        }
        /*Threads for dispatcher and core 1 */

        for (int i = 0; i < tasks; i++) {
            dispatcher700 t = new dispatcher700(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core700 t = new core700(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            task7 t = new task7(i);
            t.start();
        }

        /*Threads for dispatcher and core 2 */
        
        for (int i = 0; i < tasks; i++) {
            dispatcher701 t = new dispatcher701(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core701 t = new core701(i);
            t.start();
        }

        /*Threads for dispatcher and core 3 */

        for (int i = 0; i < tasks; i++) {
            dispatcher710 t = new dispatcher710(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core710 t = new core710(i);
            t.start();
        }
        /*Threads for dispatcher and core 4 */

        for (int i = 0; i < tasks; i++) {
            dispatcher711 t = new dispatcher711(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core711 t = new core711(i);
            t.start();
        }


    }
    
}




/*CORE AND DISPATCHER 1 - 00 */


class dispatcher700 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task7.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart00 = new Semaphore(1);


    public dispatcher700(int id){
        this.tID = id;
    }
    /* */
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher700.selectTask();


    @Override
    public void run() {
        while(task7.allBurst > 0){
            

            dispatcherStart00.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 0");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task7.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core700.coreStart00.release();
        }
        

    }
}




class core700 extends Thread {
    int tID;
    static Semaphore coreStart00 = new Semaphore(0);
    static Semaphore[] taskStart = task7.taskStart;
    static Semaphore[] taskFinish = task7.taskFinish;
    int selectedTask = task7.selectTask();



    public core700(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task7.allBurst > 0){
            coreStart00.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            /*Helps handle case where selectedTask doesnt iterate from 0 and -1 */
            if (selectedTask == -1){
                selectedTask = 0;
                System.out.println("Using CPU 0");
                taskStart[selectedTask].release();

            }else{
                selectedTask++;
                selectedTask = selectedTask % task7.tasks;
                System.out.println("Using CPU 0");
                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher700.dispatcherStart00.release();
        }
    
        
    }
}



/*CORE AND DISPATCHER 2 - 01 */


class dispatcher701 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task7.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart01 = new Semaphore(1);


    public dispatcher701(int id){
        this.tID = id;
    }
    /* */
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher701.selectTask();


    @Override
    public void run() {
        while(task7.allBurst > 0){
            

            dispatcherStart01.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 1");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task7.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core701.coreStart01.release();
        }
        

    }
}




class core701 extends Thread {
    int tID;
    static Semaphore coreStart01 = new Semaphore(0);
    static Semaphore[] taskStart = task7.taskStart;
    static Semaphore[] taskFinish = task7.taskFinish;
    int selectedTask = task7.selectTask();



    public core701(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task7.allBurst > 0){
            coreStart01.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            /*Helps handle case where selectedTask doesnt iterate from 0 and -1 */
            if (selectedTask == -1){
                selectedTask = 0;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 1 ");
                taskStart[selectedTask].release();

            }else{
                selectedTask++;
                selectedTask = selectedTask % task7.tasks;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 1 ");
                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher701.dispatcherStart01.release();
        }
    
        
    }
}




/*CORE AND DISPATCHER 3 - 10 */


class dispatcher710 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task7.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart10 = new Semaphore(1);


    public dispatcher710(int id){
        this.tID = id;
    }
    /* */
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher710.selectTask();


    @Override
    public void run() {
        while(task7.allBurst > 0){
            

            dispatcherStart10.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 3");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task7.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core710.coreStart10.release();
        }
        

    }
}




class core710 extends Thread {
    int tID;
    static Semaphore coreStart10 = new Semaphore(0);
    static Semaphore[] taskStart = task7.taskStart;
    static Semaphore[] taskFinish = task7.taskFinish;
    int selectedTask = task7.selectTask();



    public core710(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task7.allBurst > 0){
            coreStart10.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            /*Helps handle case where selectedTask doesnt iterate from 0 and -1 */
            if (selectedTask == -1){
                selectedTask = 0;
                System.out.println("Using CPU 3");
                taskStart[selectedTask].release();

            }else{
                selectedTask++;
                selectedTask = selectedTask % task7.tasks;
                System.out.println("Using CPU 3");
                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher710.dispatcherStart10.release();
        }
    
        
    }
}


/*CORE AND DISPATCHER 4 - 11 */

class dispatcher711 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task7.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart11 = new Semaphore(1);


    public dispatcher711(int id){
        this.tID = id;
    }
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher711.selectTask();


    @Override
    public void run() {
        while(task7.allBurst > 0){
            

            dispatcherStart11.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 4");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task7.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core711.coreStart11.release();
        }
        

    }
}




class core711 extends Thread {
    int tID;
    static Semaphore coreStart11 = new Semaphore(0);
    static Semaphore[] taskStart = task7.taskStart;
    static Semaphore[] taskFinish = task7.taskFinish;
    int selectedTask = task7.selectTask();



    public core711(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task7.allBurst > 0){
            coreStart11.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            /*Helps handle case where selectedTask doesnt iterate from 0 and -1 */
            if (selectedTask == -1){
                selectedTask = 0;
                System.out.println("Using CPU 0");
                taskStart[selectedTask].release();

            }else{
                selectedTask++;
                selectedTask = selectedTask % task7.tasks;
                System.out.println("Using CPU 0");
                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher711.dispatcherStart11.release();
        }
    
        
    }
}
