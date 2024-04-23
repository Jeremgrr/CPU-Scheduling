import java.util.Random;
import java.util.concurrent.Semaphore;

public class task3 extends Thread {
    static int tID;
    static Random r = new Random();
    static int tasks = r.nextInt(3, 8);
    static int[] mBurst = new int[tasks];
    static int[] cBurst = new int[tasks];
    static Semaphore[] taskStart = new Semaphore[tasks];
    static Semaphore[] taskFinish = new Semaphore[tasks];
    static int quantum = 3;
    static int allBurst;
    static int nextTask = -1;

    public task3(int id) {
        this.tID = id;
    }

    public static int[] getMaxBurst() {
        for (int i = 0; i < tasks; i++) {
            int burst = r.nextInt(1, 8);
            mBurst[i] = burst;
            allBurst = mBurst[i] + allBurst;
        }
        return mBurst;
    }

    public static int[] setCurrentBurst() {
        for (int i = 0; i < tasks; i++) {
            cBurst[i] = 0;
        }
        return cBurst;
    }

    public static synchronized int updateBurst() {
        allBurst = 0;
        for (int i = 0; i < tasks; i++) {
            allBurst = mBurst[i] + allBurst;
        }
        return allBurst;
    }

    public static synchronized void printReadyQueue() {
        System.out.println("\n--------------- Ready Queue ---------------");
        for (int i = 0; i < tasks; i++) {
            System.out.println("ID:" + i + ", Max Burst: " + mBurst[i] + ", Current Burst: " + "0");
        }
        System.out.println("Sum of burst: " + allBurst);
        System.out.println("-------------------------------------------");
        System.out.println();
    }

    public static synchronized int selectTask() {
        int shortestBurstTime = Integer.MAX_VALUE;
        int selectedTask = -1;
        for (int i = 0; i < tasks; i++) {
            if (mBurst[i] > 0 && mBurst[i] < shortestBurstTime) {
                shortestBurstTime = mBurst[i];
                selectedTask = i;
            }
        }
        return selectedTask;
    }

    int selectedTask = task3.selectTask();
    static int[] setCurrent = setCurrentBurst();
    static int[] RandBurst = getMaxBurst();

    @Override
    public void run() {
        while (allBurst >= 0) {
            taskStart[selectedTask].acquireUninterruptibly();
            if (mBurst[selectedTask] == 0) {
                // Do nothing
            } else {
                System.out.println("Dispatcher 0   | Running Process " + selectedTask + ".");
                System.out.println("Proc. Thread " + selectedTask + " | Using CPU 0; MB=" + RandBurst[selectedTask] + " , CB=0, BT=" + RandBurst[selectedTask] + " , BG:=" + RandBurst[selectedTask]);
            }
            if (mBurst[selectedTask] == 0) {
                System.out.print("");
            } else if (mBurst[selectedTask] < quantum) {
                for (int i = 0; i < mBurst[selectedTask]; i++) {
                    System.out.println("Proc. Thread " + selectedTask + " | Using CPU 0; On Burst " + setCurrent[selectedTask] + ".");
                    setCurrent[selectedTask]++;
                    mBurst[selectedTask]--;
                }
            } else {
                for (int i = 0; i < quantum; i++) {
                    System.out.println("Proc. Thread " + selectedTask + " | Using CPU 0; On Burst " + setCurrent[selectedTask] + ".");
                    setCurrent[selectedTask]++;
                    mBurst[selectedTask]--;
                }
            }
            updateBurst();
            if (allBurst == 0) {
                System.out.println();
                System.out.println("Main thread     | Exiting.");
                break;
            }
            taskFinish[selectedTask].release();
        }
    }

    public static void main(String[] args) {
        System.out.println("# of threads = " + tasks);
        for (int i = 0; i < tasks; i++) {
            System.out.println("Main thread     | Creating process thread " + i);
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

    public dispatcher3(int id) {
        this.tID = id;
    }

    @Override
    public void run() {
        while (task3.allBurst > 0) {
            dispatcherStart.acquireUninterruptibly();
            int selectedTask = task3.selectTask();
            core3.coreStart.release();
        }
    }
}

class core3 extends Thread {
    int tID;
    static Semaphore coreStart = new Semaphore(0);
    static Semaphore[] taskStart = task3.taskStart;
    static Semaphore[] taskFinish = task3.taskFinish;
    int selectedTask = task2024.selectTask();



    public core3(int id){
        this.tID = id;
    }

    @Override
    public void run() {
        while(task3.allBurst > 0){
            coreStart.acquireUninterruptibly();
            //System.out.println("Core Here");
            //System.out.println("core: " + tID);

            //update assigned Task's allotted burst
            //taskStart[task2024.tID].release();

            taskStart[selectedTask].release();

            //System.out.println("taskStart release " + dispatcher24.tID);
            taskFinish[selectedTask].acquireUninterruptibly();

            //System.out.println("Back to Core");


            System.out.println();

            dispatcher3.dispatcherStart.release();
        }
    
        
    }
}
