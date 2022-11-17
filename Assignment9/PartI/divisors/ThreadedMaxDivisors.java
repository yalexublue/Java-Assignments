package divisors;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ThreadedMaxDivisors implements Runnable {
	
	private long min;
	private long max;
	//volatile keyword makes sure max_divisor and int_max thread safe
	//private volatile static int max_divisor;
	//private volatile static long int_max;
	private int max_divisor;
	private long int_max;
	
	public ThreadedMaxDivisors(long min, long max) {
		this.min = min;
		this.max = max;
	}
	//since multiple thread will end up accessing this data, so it needs to be
	//designated critical region, would be used if using static max_divisor
	/*
	synchronized public static void update (int inMaxDivisor, long inIntMax){
		if(inMaxDivisor > max_divisor){
			max_divisor = inMaxDivisor;
			int_max = inIntMax;
		}
	}
	 */

	//instead of running from from to to, here separate them into multile
	//intervals with min and max in the constructor.
	@Override
	//this run method would run update method above which is commented
	//out, which can easily update static maxdivisor value though out
	//all instances, but won't be implemented here cause the contradiction
	//with the pre written main method.
	/*
	public void run() {
		int currDivisors = 0;
		int maxDivisors = 0;
		long intMax = 0;
		for (long i = min; i < max; i++) {
			currDivisors = CountDivisors.countDivisors(i);
			if (currDivisors > maxDivisors) {
				maxDivisors = currDivisors;
				intMax = i;
			}
		}
		update(maxDivisors, intMax);
	}
	 */
	//Make sure to update the max_divisor of this thread only
	public void run() {
		int currDivisors = 0;
		for (long i = min; i < max; i++) {
			currDivisors = CountDivisors.countDivisors(i);
			if (currDivisors > max_divisor) {
				max_divisor = currDivisors;
				int_max = i;
			}
		}
	}
	

	public static void main(String[] args) {
		
		long min = 100_000;
		long max = 200_000;
		long outputNumMax = 0;
		int outputMaxDivisor = 0;
		final int THREAD_NUM = 150;
		
		Set<Thread> threadSet = new HashSet<Thread>();
		Set<ThreadedMaxDivisors> divisorsSet = new HashSet<ThreadedMaxDivisors>();
		long startTime = System.currentTimeMillis();
		//thread cutting, divide min and max into multiple threads.
		//here the number of threads will be created is 150.
		//TODO: recheck value
		long numberPerThread = (max - min + 1) / THREAD_NUM;
		long start = 100_000;
		long end = start + numberPerThread - 1;
		//Runner implementations are also created added to HashSet of runner.
		//Runner is vital since within the class it contains the real data of
		//max divisor for later comparison.
		//threads been created and add to the HashSet of thread.
		for (int i = 0; i < THREAD_NUM; i++) {
			if (i == THREAD_NUM - 1){
				end = max;
			}
			ThreadedMaxDivisors curr = new ThreadedMaxDivisors(start, end);
			Thread temp = new Thread(curr);
			divisorsSet.add(curr);
			threadSet.add(temp);
			start += numberPerThread;
			end = start + numberPerThread - 1;
		}
		//start all 150 threads.
		for (Thread i : threadSet){
			i.start();
		}
		
		/* join() tells a thread to wait until it's complete before the rest of the code and proceed.
		 * if we do that for all the threads, then then we can get the results of the threads once
		 * all of them are done
		 */
		int count = 0;
		for (Thread t: threadSet) {
			try {
				t.join();
				System.out.print("Done");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// at this point, all threads have been completed, since we
		// called the "join()" method on all the threads we created,
		// which forces the code to wait until the thread is finished
		// before we continue

		//this implementation require non-static max_divisor stored
		//which would be faster if using static and update it within the
		//thread. Purpose is take the runner HashSet and find the max value
		//within.
		for (ThreadedMaxDivisors tmd : divisorsSet) {
			// presumably you've recorded the results of
			// each ThreadedMaxDivisors run. Pick
			// the largest number with the largest number of
			// divisors

			if (tmd.max_divisor > outputMaxDivisor){
				outputMaxDivisor = tmd.max_divisor;
				outputNumMax = tmd.int_max;
			}
		}

		
		long endTime = System.currentTimeMillis();
		System.out.println("Threaded elapsed time: " + (endTime - startTime));
		startTime = System.currentTimeMillis();
		Entry<Long,Long> e = CountDivisors.maxDivisors(min, max);
		
		long number = e.getKey();
		long numDivisors = e.getValue();
		
		System.out.println("\n" + number + ": " + numDivisors);
		endTime = System.currentTimeMillis();
		
		System.out.println("Non-threaded elapsed time: " + (endTime - startTime));
		System.out.println(outputNumMax + " = " + outputMaxDivisor);
		
		
		
	}
}
