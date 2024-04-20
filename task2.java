import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;

public class task2 extends Thread {
    //wait on sem
    //for loop using # of burst to print each burst
    public static void main(String[] args) {
        System.out.println("Scheduler Algorithm Select: Round Robin. Time Quantum  ");
        dispatcher2.arrayToArrayList();

        int task = dispatcher2.tasks;
        System.out.println("# of threads = " + task);

        // Thread creation
        for (int i = 0; i < task; i++) {
        dispatcher2 t = new dispatcher2(i);
            t.start();
        }

        //Probably not right
        for (int i = 0; i < task; i++) {
        Core2 t = new Core2(i);
            t.start();
        }

        
    }
}

//Dispatcher Purpose

//Access & loop through ready queue to find the next Task to run
//Decide the allotted burst for task (getMaxBurst); let CPU Core thread know burst time
//Signal permit to a barrier blocking core thread so that the Core may proceed with its logic
//NOTE: dispatcher thread does NOT release a permit to the Semaphore blocking the Thread, that is the Core's responsibility
class dispatcher2 extends Thread{

    static Random r = new Random();
    static int tasks = r.nextInt(3,8);  //CHANGE to [1,25] after testing
    static int count;
    static int bCount;
    static int quantum = 3;

    int tID;
    static Semaphore countMutex = new Semaphore(1);
    static Semaphore barrierSem = new Semaphore(0);
    static Semaphore barrierSem2 = new Semaphore(0);
    static int[] mBurst = new int[tasks];           //mBurst is the Ready Queue
    static int[] cBurst = new int[tasks];
    static int[] rBurst = new int[tasks];
    static List<Integer> readyQ = new ArrayList<>();


    
    
    public dispatcher2(int id){
        tID = id;
    }

    //look at RQ
    //sem1.release()

    //create getMaxBurst -> [1,50]

    //create getMaxBurst, use mBurst[i] where i is the thread you want to get the maxBurst to use
    public static int[] getMaxBurst(){
        for (int i = 0; i < tasks; i++){
            int burst = r.nextInt(1,8);  //CHANGE to [1,50] after testing
            mBurst[i] = burst;
            //System.out.println(mBurst[i]);
        }
        return mBurst;

    }

    public static int[] getCurrentBurst(int tID){
        //System.out.println("Thread " + tID + " burst value updated to " + cBurst[tID]);
        //System.out.println("While Thread " + tID + " MAX burst value is " + mBurst[tID]);



        return cBurst;
    }

    public static int[] getRemainingBurst(int tID, int added){
        rBurst[tID] = mBurst[tID] - added;
        System.out.println("Thread " + tID + " burst value updated to " + rBurst[tID]);

        return rBurst;
    }

    public static void arrayToArrayList(){
        for (Integer item : mBurst){
            readyQ.add(item);
            System.out.println(readyQ);
        }
    }

    static int[] RandBurst  = getMaxBurst();

    


    @Override
    public void run(){
        countMutex.acquireUninterruptibly();
        System.out.println("Main thread     | Creating process thread "  + tID);
        countMutex.release();
        count++;
         //when all tasks have been created now run Ready Queue
        if (count == tasks){
            System.out.println("\n--------------- Ready Queue ---------------");
            for (int i = 0; i < tasks; i++){
                System.out.println("ID:" + i + ", Max Burst: " + RandBurst[i] + ", Current Burst: " + cBurst[i]);
                //barrierSem.release(1);
            }     
        }

        /*
         * barrierSem.acquireUninterruptibly();
         *System.out.println("ID:" + tID + ", Max Burst: " + mBurst[tID] + ", Current Burst: " + cBurst);
         *System.out.println("ID:" + tID + ", Max Burst: " + RandBurst[tID] + ", Current Burst: " + cBurst);
         *barrierSem.release();
         */

      
        bCount++;

        //if true, ready queue description has completed, move on to rest of process
        if (bCount == tasks){
            System.out.println("-------------------------------------------");
            System.out.println("Main thread     | Forking dispatcher 0");
            System.out.println("Dispatcher 0    | Using CPU 0");
            System.out.println("Dispatcher 0    | Now releasing dispatchers.");
            System.out.println();
            System.out.println("Dispatcher 0    | Running Round Robin, Time Quantum = " + quantum);
            System.out.println();

            barrierSem2.release(1);
           

        }
        barrierSem.release();
        
        

    }

}


//Core Purpose

//actually allow its specified task to begin running
class Core2 extends Thread {
    int tID;
    Semaphore barrierSem2 = dispatcher2.barrierSem2;
    static int [] RandBurst = dispatcher2.RandBurst;
    static int[] mBurst = dispatcher2.mBurst;
    static int[] cBurst = dispatcher2.cBurst;
    static int[] rBurst = dispatcher2.rBurst;
    static int quantum = dispatcher2.quantum;
    static List<Integer> readyQ = dispatcher2.readyQ;

    int coreCount = 0;
    int readyQCheck;
    
    
    public Core2(int id){
        tID = id;
    }



  


    @Override
    public void run(){
        //barrierSem2.acquireUninterruptibly();
        while(!readyQ.isEmpty()) {
            barrierSem2.acquireUninterruptibly();

            System.out.println("Dispatcher 0   | Running Process " + tID + "." );
            System.out.println("Proc. Thread " + tID +" | Using CPU 0; MB=" + RandBurst[tID] + " , CB=0, BT=" + RandBurst[tID] + " , BG:=" + RandBurst[tID]);
            //allows rr scheduling
            int cycles = mBurst[tID] + 1;

            //int cycles = mBurst[tID] + 1 ;
            //System.out.println("Cycles at " + tID + " is " + cycle);
            int cb = 0;
            for (int i = 1; i < cycles; i++){
                cBurst[tID]++;
                dispatcher2.getCurrentBurst(tID);

                System.out.println("Proc. Thread " + tID +" | Using CPU 0; On Burst " + cBurst[tID] + ".");
                //dispatcher2.getCurrentBurst(tID, i);

                if (cBurst[tID] == mBurst[tID]){
                    System.out.println("Ready Queue " + dispatcher2.readyQ);
                    System.out.println("HOORAY current is " + cBurst[tID] + " and max is " + mBurst[tID]);
                    for (int a = 0 ; a < readyQ.size(); a++){
                        if (readyQ.indexOf(a) == cBurst[tID]){
                            System.out.println("THAT MF RIGHT HERE");
                            readyQ.remove(a);

                        }

                    }

                }

                cb++;

                if (i == quantum){
                    //cBurst[tID] = cBurst[tID] + cb;
                    //dispatcher2.getCurrentBurst(tID, cb);
                    //dispatcher2.getRemainingBurst(tID, cb);

                    System.out.println("Time to Switch");
                    System.out.println("Ready Queue " + readyQ);



                    cb = 0;
                    break;
                }
            }
            System.out.println();
            coreCount++;

            barrierSem2.release();

            
            if (coreCount == 1){
                break;
            }

            if (readyQ.isEmpty()){
                System.out.println("THAT MF EMPTY, IM DONE");
            }else {
                System.out.println("FUCK, IM TIRED");
            }
            

            // int number = quantum x task
         
           
        }

        /* 
        
        System.out.println("Dispatcher 0   | Running Process " + tID + "." );
        System.out.println("Proc. Thread " + tID +" | Using CPU 0; MB=" + RandBurst[tID] + " , CB=0, BT=" + RandBurst[tID] + " , BG:=" + RandBurst[tID]);

        //allows rr scheduling
        int cycles = mBurst[tID] + 1;

        //int cycles = mBurst[tID] + 1 ;
        //System.out.println("Cycles at " + tID + " is " + cycle);
        int cb = 0;
        for (int i = 1; i < cycles; i++){
            System.out.println("Proc. Thread " + tID +" | Using CPU 0; On Burst " + i + ".");
            cb++;
            //coreCount++;
            if (i == quantum){
                //cBurst[tID] = cBurst[tID] + cb;
                dispatcher2.getCurrentBurst(tID, cb);
                dispatcher2.getRemainingBurst(tID, cb);
                //System.out.println("Currently " + cBurst[tID] +" at THREAD " + tID);
                //System.out.println("There are " + rBurst[tID] +" remaining at THREAD " + tID);
                System.out.println("TIME TO SWITCH");

                cb = 0;
                break;

            }
        }
        System.out.println();
        
        */
        //barrierSem2.release();
        
        /* 
        for (int i = 0; i < dispatcher2.tasks; i++){
            System.out.println("ID: " + i + " remaining: " + rBurst[i]);
        }
        */
        //System.out.println("CORE COUNT: " + coreCount);




        //check to see if RQ at i is equal to zero

        
        
    //find a way to run threads again WHILE all cBurst or not equal to mBurst
        /* 
        for (int i = 1; i < cycles; i++){
            while(rBurst[tID] > 0){
                int currentBurst = i + cBurst[i];
                System.out.println("Proc. Thread " + tID +" | Using CPU 0; On Burst " + currentBurst + ".");
                cb++;
                if (i == quantum){
                    dispatcher2.getCurrentBurst(tID, cb);
                    dispatcher2.getRemainingBurst(tID, cb);
                    System.out.println("Currently " + cBurst[tID] +" at THREAD " + tID);
                    System.out.println("TIME TO SWITCH");
                    cb = 0;
                    break;
                }


            }
        }
        System.out.println();
        */

        

    


    }
}