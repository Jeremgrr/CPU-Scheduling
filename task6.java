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

public class task6 extends Thread {
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
    static int quantum = 3;
    static int nextTask = -1;

    public task6( int id) {
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
    int selectedTask = task6.selectTask();

    static int[] setCurrent = setCurrentBurst();
    static int[] RandBurst  = getMaxBurst();

    @Override
    public void run(){
        while(allBurst > 0){
            
            
            //taskStart[tID].acquireUninterruptibly();
            taskStart[selectedTask].acquireUninterruptibly();
            
            //loop once for allotted burst
            //System.out.println("TASK STARTED");
            //System.out.println("Currently running task " + selectedTask);
            //System.out.println("This task has " + mBurst[selectedTask] + " Bursts remaining");

            if (mBurst[selectedTask] == 0){
                //Do nothing
            }else{
                System.out.println("Dispatcher 0   | Running Process " + selectedTask + "." );
                System.out.println("Proc. Thread " + selectedTask +" | Using CPU 0; MB=" + maxBurst[selectedTask] + " , CB=0, BT=" + RandBurst[selectedTask] + " , BG:=" + RandBurst[selectedTask]);


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
        System.out.println("Dispatcher 0    | Running Round Robin, Time Quantum = " + quantum);
        System.out.println();

        
        for (int i = 0; i < tasks; i++) {
            taskStart[i] = new Semaphore(0);

        }

        for (int i = 0; i < tasks; i++) {
            taskFinish[i] = new Semaphore(0);

        }
        /*Threads for dispatcher and core 1 */

        for (int i = 0; i < tasks; i++) {
            dispatcher600 t = new dispatcher600(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core600 t = new core600(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            task6 t = new task6(i);
            t.start();
        }

        /*Threads for dispatcher and core 2 */
        
        for (int i = 0; i < tasks; i++) {
            dispatcher601 t = new dispatcher601(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core601 t = new core601(i);
            t.start();
        }

        /*Threads for dispatcher and core 3 */

        for (int i = 0; i < tasks; i++) {
            dispatcher610 t = new dispatcher610(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core610 t = new core610(i);
            t.start();
        }
        /*Threads for dispatcher and core 4 */

        for (int i = 0; i < tasks; i++) {
            dispatcher611 t = new dispatcher611(i);
            t.start();
        }
        
        for (int i = 0; i < tasks; i++) {
            core611 t = new core611(i);
            t.start();
        }


    }
    
}




/*CORE AND DISPATCHER 1 - 00 */


class dispatcher600 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task6.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart00 = new Semaphore(1);


    public dispatcher600(int id){
        this.tID = id;
    }
    /* */
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher600.selectTask();


    @Override
    public void run() {
        while(task6.allBurst > 0){
            

            dispatcherStart00.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 0");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task6.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core600.coreStart00.release();
        }
        

    }
}




class core600 extends Thread {
    int tID;
    static Semaphore coreStart00 = new Semaphore(0);
    static Semaphore[] taskStart = task6.taskStart;
    static Semaphore[] taskFinish = task6.taskFinish;
    int selectedTask = task6.selectTask();



    public core600(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task6.allBurst > 0){
            coreStart00.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            /*Helps handle case where selectedTask doesnt iterate from 0 and -1 */
            if (selectedTask == -1){
                selectedTask = 0;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 0 ");

                taskStart[selectedTask].release();

            }else{
                selectedTask++;
                selectedTask = selectedTask % task6.tasks;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 0 ");

                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher600.dispatcherStart00.release();
        }
    
        
    }
}



/*CORE AND DISPATCHER 2 - 01 */


class dispatcher601 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task6.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart01 = new Semaphore(1);


    public dispatcher601(int id){
        this.tID = id;
    }
    /* */
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher601.selectTask();


    @Override
    public void run() {
        while(task6.allBurst > 0){
            

            dispatcherStart01.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 1");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task6.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core601.coreStart01.release();
        }
        

    }
}




class core601 extends Thread {
    int tID;
    static Semaphore coreStart01 = new Semaphore(0);
    static Semaphore[] taskStart = task6.taskStart;
    static Semaphore[] taskFinish = task6.taskFinish;
    int selectedTask = task6.selectTask();



    public core601(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task6.allBurst > 0){
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
                selectedTask = selectedTask % task6.tasks;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 1 ");
                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher601.dispatcherStart01.release();
        }
    
        
    }
}




/*CORE AND DISPATCHER 3 - 10 */


class dispatcher610 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task6.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart10 = new Semaphore(1);


    public dispatcher610(int id){
        this.tID = id;
    }
    /* */
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher610.selectTask();


    @Override
    public void run() {
        while(task6.allBurst > 0){
            

            dispatcherStart10.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 2");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task6.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core610.coreStart10.release();
        }
        

    }
}




class core610 extends Thread {
    int tID;
    static Semaphore coreStart10 = new Semaphore(0);
    static Semaphore[] taskStart = task6.taskStart;
    static Semaphore[] taskFinish = task6.taskFinish;
    int selectedTask = task6.selectTask();



    public core610(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task6.allBurst > 0){
            coreStart10.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            /*Helps handle case where selectedTask doesnt iterate from 0 and -1 */
            if (selectedTask == -1){
                selectedTask = 0;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 2 ");

                taskStart[selectedTask].release();

            }else{
                selectedTask++;
                selectedTask = selectedTask % task6.tasks;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 2 ");
                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher610.dispatcherStart10.release();
        }
    
        
    }
}


/*CORE AND DISPATCHER 4 - 11 */

class dispatcher611 extends Thread {
    static int tID;
    static int nextTask = 0;
    //static int[] maxBurst = task6.mBurst;
    //static int[] RandBurst  = getMaxBurst();


    static Semaphore dispatcherStart11 = new Semaphore(1);


    public dispatcher611(int id){
        this.tID = id;
    }
    public static synchronized int selectTask() {
        int taskID = nextTask;
        nextTask = (nextTask + 1) % task1.tasks;
        return taskID;
    }
    int selectedTask = dispatcher611.selectTask();


    @Override
    public void run() {
        while(task6.allBurst > 0){
            

            dispatcherStart11.acquireUninterruptibly();
            //System.out.println("Dispatcher 0   | Running Process " + selectedTask + ". HAVING PROBLEMS HERE"  );
            System.out.println("Dispatcher 3");

            
            //System.out.println("Proc. Thread " + selectedTask +" );
            //System.out.println("dispatcher: " + tID);
            



            //select task from ready Queue

            int selectedTask = task6.selectTask();
            //System.out.println("Task at " + tID + " Max Burst is " + queuedTask); 

            //assign Task and alloted burst to core

            core611.coreStart11.release();
        }
        

    }
}




class core611 extends Thread {
    int tID;
    static Semaphore coreStart11 = new Semaphore(0);
    static Semaphore[] taskStart = task6.taskStart;
    static Semaphore[] taskFinish = task6.taskFinish;
    int selectedTask = task6.selectTask();



    public core611(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task6.allBurst > 0){
            coreStart11.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            /*Helps handle case where selectedTask doesnt iterate from 0 and -1 */
            if (selectedTask == -1){
                selectedTask = 0;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 3 ");
                taskStart[selectedTask].release();

            }else{
                selectedTask++;
                selectedTask = selectedTask % task6.tasks;
                System.out.print("Proc. Thread " + selectedTask + " | Using CPU 3 ");
                taskStart[selectedTask].release();

            }

      

            //taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher611.dispatcherStart11.release();
        }
    
        
    }
}
