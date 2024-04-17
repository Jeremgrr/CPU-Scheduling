import java.util.Random;
import java.util.concurrent.Semaphore;

public class task1 extends Thread {
    //wait on sem
    //for loop using # of burst to print each burst
    public static void main(String[] args) {
        int task = dispatcher.tasks;
        System.out.println("# of threads = " + task);

        // Thread creation
        for (int i = 0; i < task; i++) {
        dispatcher t = new dispatcher(i);
            t.start();
        }
        
    }
}


class dispatcher extends Thread{

    static Random r = new Random();
    static int tasks = r.nextInt(3,8);  //CHANGE to [1,25] after testing
    static int obj = r.nextInt(3,8);    //rename
    static int count;
    static int bCount;

    int tID;
    static Semaphore countMutex = new Semaphore(1);
    static Semaphore barrierSem = new Semaphore(0);
    static Semaphore barrierSem2 = new Semaphore(0);
    static int[] mBurst = new int[tasks];
    static int cBurst;


    
    
    public dispatcher(int id){
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

    int[] RandBurst  = getMaxBurst();

    


    @Override
    public void run(){
        countMutex.acquireUninterruptibly();
        System.out.println("Main thread     | Creating process thread "  + tID);
        countMutex.release();
        count++;
        
        
        //when all tasks have been created now run Ready Queue
        if (count == tasks){
            System.out.println("\n--------------- Ready Queue ---------------");
            barrierSem.release(1);       //allow 1 permit at a time so one person eats at a time
        }

       
        barrierSem.acquireUninterruptibly();
        //System.out.println("ID:" + tID + ", Max Burst: " + mBurst[tID] + ", Current Burst: " + cBurst);
        System.out.println("ID:" + tID + ", Max Burst: " + RandBurst[tID] + ", Current Burst: " + cBurst);
        barrierSem.release();
        bCount++;

        //if true, ready queue description has completed, move on to rest of process
        if (bCount == tasks){
            System.out.println("-------------------------------------------");
            System.out.println("Main thread     | Forking dispatcher 0");
            System.out.println("Dispatcher 0    | Using CPU 0");
            System.out.println("Dispatcher 0    | Now releasing dispatchers.");
            System.out.println();
            System.out.println("Dispatcher 0    | Running FCFS Algorithm.");
            System.out.println();

            barrierSem2.release(1);

        }

        barrierSem2.acquireUninterruptibly();
        System.out.println("Dispatcher 0   | Running Process " + tID + "." );
        System.out.println("Proc. Thread " + tID +" | Using CPU 0; MB=" + RandBurst[tID] + " , CB=0, BT=" + RandBurst[tID] + " , BG:=" + RandBurst[tID]);


        int cycles = mBurst[tID];

        for (int i = 0; i < cycles; i++){
            System.out.println("Proc. Thread " + tID +" | Using CPU 0; On Burst " + i + ".");
        }
        System.out.println();

        barrierSem2.release();


    }

}