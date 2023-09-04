package com.ruoyi.zlyyhmobile.utils;

import java.util.*;

/**
 * 根据概率抽奖
 * @author 25487
 */
public final class AliasMethod {
	/**
	The random number generator used to sample from the distribution.
	用于从分布中抽样的随机数生成器。
	 */
	private final Random random;
	/**
	The probability and alias tables.
	概率和别名表。
	*/
	private final int[] alias;
	private final double[] probability;

	/**
	 * Constructs a new AliasMethod to sample from a discrete distribution and
	 * hand back outcomes based on the probability distribution.
	 * <p/>
	 * Given as input a list of probabilities corresponding to outcomes 0, 1,
	 * ..., n - 1, this constructor creates the probability and alias tables
	 * needed to efficiently sample from this distribution.
	 *
	 * @param probabilities The list of probabilities. 概率列表。
	 *                      <p>
	 *                      构造了一个新的AliasMethod，从一个离散分布中采样，并根据概率分布返回结果。
	 *                      <p/>
	 *                      给定作为输入的概率列表对应结果0,1，
	 *                      … ， n - 1，这个构造函数创建的概率和别名表*需要有效地从这个分布进行抽样。
	 */
	public AliasMethod(List<Double> probabilities) {
		this(probabilities, new Random());
	}

	/**
	 * Constructs a new AliasMethod to sample from a discrete distribution and
	 * hand back outcomes based on the probability distribution.
	 * 构造了一种新的AliasMethod，从离散分布中抽样，并根据概率分布返回结果。
	 * <p>
	 * <p/>
	 * Given as input a list of probabilities corresponding to outcomes 0, 1,
	 * ..., n - 1, along with the random number generator that should be used
	 * as the underlying generator, this constructor creates the probability
	 * and alias tables needed to efficiently sample from this distribution.
	 * <p>
	 * 给定一个对应结果(0,1)的概率列表，．.．  ， n - 1，以及应该使用的随机数生成器
	 * 作为底层生成器，这个构造函数创建概率别名表需要有效地从这个分布中抽样。
	 *
	 * @param probabilities The list of probabilities.  概率列表。
	 * @param random        The random number generator 随机数生成器
	 */
	public AliasMethod(List<Double> probabilities, Random random) {
		/* Begin by doing basic structural checks on the inputs.
		 * 首先对输入进行基本的结构检查。   */
		if (probabilities == null || random == null) {
            throw new NullPointerException();
        }
		if (probabilities.size() == 0) {
            throw new IllegalArgumentException("Probability vector must be nonempty.");
        }
		/* Allocate space for the probability and alias tables.
		 * 为概率表和别名表分配空间。   */
		probability = new double[probabilities.size()];
		alias = new int[probabilities.size()];
		/* Store the underlying generator.
		 * 存储底层生成器。 */
		this.random = random;
		/* Compute the average probability and cache it for later use.
		 * 计算平均概率并将其缓存以备以后使用。   */
		final double average = 1.0 / probabilities.size();
		/* Make a copy of the probabilities list, since we will be making changes to it.
		   复制概率列表，因为我们将对其进行*更改。   */
        probabilities = new ArrayList<>(probabilities);
        // 当概率和不为1是，重新计算概率，使概率和为1
        double probabilitiSum = probabilities.stream().reduce(Double::sum).orElse(0D);
        if(probabilitiSum != 1){
            List<Double> newProbabilities = new ArrayList<>();
            double x = 1.0 / probabilitiSum;
            for (Double probability : probabilities) {
                newProbabilities.add(probability * x);
            }
            probabilities = newProbabilities;
        }
		/* Create two stacks to act as worklists as we populate the tables.
		 * 在填充表时，创建两个堆栈作为工作列表。   */
		Deque<Integer> small = new ArrayDeque<Integer>();
		Deque<Integer> large = new ArrayDeque<Integer>();
		/* Populate the stacks with the input probabilities.
		 * 用输入概率填充堆栈。  */
		for (int i = 0; i < probabilities.size(); ++i) {
			/* If the probability is below the average probability, then we add
			* * it to the small list; otherwise we add it to the large list.
			如果概率低于平均概率，则相加它到小列表; 否则我们将它添加到大列表中。  */
			if (probabilities.get(i) >= average) {
                large.add(i);
            } else {
                small.add(i);
            }
		}
		/* As a note: in the mathematical specification of the algorithm, we
		* * will always exhaust the small list before the big list.  However,
		* * due to floating point inaccuracies, this is not necessarily true.
		* * Consequently, this inner loop (which tries to pair small and large
		* * elements) will have to check that both lists aren't empty.
		注:在数学规范算法中，我们  它到小列表; 否则我们将它添加到大列表中。
		由于浮点数的不精确性，这并不一定是正确的。
		因此，这个内部循环(尝试将小元素和大元素配对)必须检查两个列表是否都为空。  */
		while (!small.isEmpty() && !large.isEmpty()) {
			/* Get the index of the small and the large probabilities.
			 * 得到小概率和大概率的指数。  它到小列表; 否则我们将它添加到大列表中。   */
			int less = small.removeLast();
			int more = large.removeLast();
			/* These probabilities have not yet been scaled up to be such that
			* * 1/n is given weight 1.0.  We do this here instead.
			   这些概率还没有被放大到这样的程度  它到小列表; 否则我们将它添加到大列表中。
				1/n的权重为1.0。 我们在这里做这个。  */
			probability[less] = probabilities.get(less) * probabilities.size();
			alias[less] = more;
			/* Decrease the probability of the larger one by the appropriate amount.
			   把大的概率减小适当的数量。  它到小列表; 否则我们将它添加到大列表中。   */
			probabilities.set(more, (probabilities.get(more) + probabilities.get(less)) - average);
			/* If the new probability is less than the average, add it into the
			 * small list; otherwise add it to the large list.
			如果新概率小于平均值，则将其加入  它到小列表; 否则我们将它添加到大列表中。
			小列表; 否则将其添加到大列表中。  */
			if (probabilities.get(more) >= 1.0 / probabilities.size()) {
                large.add(more);
            } else {
                small.add(more);
            }
		}
		/* At this point, everything is in one list, which means that the
		 * * remaining probabilities should all be 1/n.  Based on this, set them
		 * * appropriately.  Due to numerical issues, we can't be sure which
		 * * stack will hold the entries, so we empty both.
		 此时，所有内容都在一个列表中，这意味着  它到小列表; 否则我们将它添加到大列表中。
		剩下的概率应该都是1/n。 在此基础上，设置它们
		适当。 由于数字上的问题，我们不确定是哪个
		Stack将保存这些条目，因此我们将它们清空。  */
		while (!small.isEmpty()) {
            probability[small.removeLast()] = 1.0;
        }
		while (!large.isEmpty()) {
            probability[large.removeLast()] = 1.0;
        }
	}

	/**
	 * Samples a value from the underlying distribution.
	 * 对底层分布的值进行抽样。  它到小列表; 否则我们将它添加到大列表中。
	 *
	 * @return A random value sampled from the underlying distribution.
	 * 从底层分布中抽样的随机值。  它到小列表; 否则我们将它添加到大列表中。
	 */
	public int next() {
		/* Generate a fair die roll to determine which column to inspect. */
		int column = random.nextInt(probability.length);
		/* Generate a biased coin toss to determine which option to pick.
		 * 生成一个公平的骰子滚动，以确定检查哪一列。  它到小列表; 否则我们将它添加到大列表中。   */
		boolean coinToss = random.nextDouble() < probability[column];
		/* Based on the outcome, return either the column or its alias.
		 * 根据结果，返回列或其别名。  它到小列表; 否则我们将它添加到大列表中。   */
		/* Log.i("1234","column="+column);        Log.i("1234","coinToss="+coinToss);        Log.i("1234","alias[column]="+coinToss);*/
		return coinToss ? column : alias[column];
	}

//	public static void main(String[] args) {
//		TreeMap<String, Double> map = new TreeMap<String, Double>();
////		map.put("0金币", 0.5);
//////		map.put("A金币", 0.5);
////		map.put("1金币", 0.3);
////		map.put("6金币", 0.1);
////		map.put("2金币", 0.052);
////		map.put("3金币", 0.053);
////		map.put("4金币", 0.054);
////		map.put("5金币", 0.055);
////		map.put("6金币", 0.041);
////		map.put("7金币", 0.042);
////		map.put("8金币", 0.043);
////		map.put("14金币", 0.043);
////		map.put("9金币", 0.09);
////		map.put("10金币", 0.01);
////		map.put("12金币", 0.05);
////		map.put("11金币", 0.05);
////		map.put("13金币", 0.05);
////		map.put("未中奖", 0.001);
//		map.put("200", 0.04);
////		map.put("100", 8.0);
////		map.put("400", 0.001);
//		map.put("优惠券", 0.9);
//
//        Map<String, AtomicInteger> resultMap = new HashMap<String, AtomicInteger>();
//        for (int i = 0; i < 10000; i++) {
//            List<Double> list = new ArrayList<Double>(map.values());
//            List<String> gifts = new ArrayList<String>(map.keySet());
//            AliasMethod method = new AliasMethod(list);
//
//            int index = method.next();
//            String key = gifts.get(index);
//            if (!resultMap.containsKey(key)) {
//                resultMap.put(key, new AtomicInteger());
//            }
//            resultMap.get(key).incrementAndGet();
//        }
//        for (String key : resultMap.keySet()) {
//            System.out.println(key + "==" + resultMap.get(key));
//        }
//	}
}
