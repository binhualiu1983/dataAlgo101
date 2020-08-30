package bayesKNNDigitOcr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;


//https://blog.csdn.net/drawsky/article/details/78034059
public class App {

//Bayes算法的训练结果集合， VecOfNumbers1保存的是值为1的向量的log值， VecOfNumbers0保存的是值为0的向量的log值，
static	double[][] VecOfNumbers1 = new double[10][1024];
static	double[][] VecOfNumbers0 = new double[10][1024];

//KNN训练结果
static Map<Integer, Map<String, ArrayList<Integer>>> kNNTrainMap
	= new TreeMap<Integer, Map<String, ArrayList<Integer>>>();

public static void BayesTrain() throws IOException
{
 int[] documentCount = new int[10];
     
     File trainDir = new File("C:\\Code\\code\\dataAlgo101\\数据集\\数字识别\\trainingDigits");
     for(File file : trainDir.listFiles())
     {
      String fileName = file.getName();
      int digit = Integer.valueOf(fileName.substring(0,1));
      documentCount[digit] +=1;
      
      String context = FileUtils.readFileToString(file, "UTF-8");
      int index=0;
      for(int i=0; i<context.length(); i++)
      {
       char c = context.charAt(i);
       if(c == '0')
       {
        VecOfNumbers0[digit][index]+=1; 
        index++;
       }
       else if(c == '1')
       {
        VecOfNumbers1[digit][index]+=1; 
        index++;
       }
      }
     }
     
     for(int i=0; i<10; i++)
     {
      for(int j=0; j<1024; j++)
      {
       double v1 = VecOfNumbers1[i][j]/documentCount[i] + 0.05; //拉普拉斯平滑
       double log_v1 = Math.log(v1);
       VecOfNumbers1[i][j] = log_v1;
       
       double v0 = VecOfNumbers0[i][j]/documentCount[i] + 0.05; //拉普拉斯平滑
       double log_v0 = Math.log(v0);
       VecOfNumbers0[i][j] = log_v0;
      }
     }
}

public static void KnnTrain() throws IOException
{
	
	for(int i=0; i<10; i++)
	{
		kNNTrainMap.put(i, new HashMap<String, ArrayList<Integer>>());
	}
     
     File trainDir = new File("C:\\Code\\code\\dataAlgo101\\数据集\\数字识别\\trainingDigits");
     for(File file : trainDir.listFiles())
     {
      String fileName = file.getName();
      int digit = Integer.valueOf(fileName.substring(0,1));
      String context = FileUtils.readFileToString(file, "UTF-8");
      
      ArrayList<Integer> points = new ArrayList<Integer>();
      kNNTrainMap.get(digit).put(fileName, points);
      
      
      int index=0;
      for(int i=0; i<context.length(); i++)
      {
       char c = context.charAt(i);
       if(c == '0')
       {
    	   points.add(0);
        index++;
       }
       else if(c == '1')
       {
    	   points.add(1);
    	   index++;
       }
      }
     }
}

//贝叶斯+KNN综合计算
public static void BayesKnnTest() throws IOException
{
	File testDir = new File("C:\\Code\\code\\dataAlgo101\\数据集\\数字识别\\testDigits");
    int success =0;
    int total = 0;
    for(File testFile : testDir.listFiles())
    {
   	 total++;
     Map<Integer, Double> result = new TreeMap<Integer, Double>();
     for(int i=0; i<10; i++)
     {
   	  result.put(i, 0.0);
     }
     
	String context = FileUtils.readFileToString(testFile, "UTF-8");
     List<Integer> points = new   ArrayList<Integer>();
     
     for(int i=0; i<10; i++)
     {
      for(int pos=0; pos<context.length(); pos++)
      {
       char c = context.charAt(pos);
       if(c == '0')
       {
    	   points.add(0);
       }
       else if(c == '1')
       {
    	   points.add(1);
       }
      }
     }
     
     for(int i=0; i<10; i++)
     {
	     double[] Vec1 = VecOfNumbers1[i];
	     double[] Vec0 = VecOfNumbers0[i];
     
      for(int pos=0; pos<1024; pos++)
      {
       int currPoints = points.get(pos);
       if(currPoints == 0)
       {
       	double value = result.get(i);
       	value+=Vec0[pos];
       	result.put(i, value);
       }
       else if(currPoints == 1)
       {
       	double value = result.get(i);
       	value+=Vec1[pos];
       	result.put(i, value);
       }
      }
     }
     
     
     ArrayList<Map.Entry<Integer, Double>> resultList = new ArrayList<Map.Entry<Integer, Double>>(result.entrySet());
     Collections.sort(resultList, (Map.Entry<Integer, Double> item1, Map.Entry<Integer, Double> item2) -> {
   	  if(item1.getValue() > item2.getValue())
   		  return -1;
   	  if(item1.getValue() < item2.getValue())
   		  return 1;
   	  return 0;
     });
     

     final double probability_gap = 150.0; //概率大于150的，直接得出结果，小于150的，再用kNN网络做出判断
     if(resultList.get(0).getValue()- probability_gap >= resultList.get(1).getValue())
     {
    	 int expect_digit = Integer.valueOf(testFile.getName().substring(0,1));
         if(resultList.get(0).getKey() == expect_digit)
       	  success++;
         else
         {
       	  //System.out.println("error");
         }
         
         //System.out.println("Bayes FileName:" + testFile.getName() + ", num:" + resultList.get(0).getKey());
         continue;
     }
     
     double limit = resultList.get(0).getValue()-probability_gap;
     List<Integer> inLimit = new ArrayList<Integer>();
     for(Map.Entry<Integer, Double> item : resultList)
     {
    	 if(item.getValue()>=limit)
    	 {
    		 inLimit.add(item.getKey());
    	 }
     }
     
     //对决赛圈的数字用kNN算法
     List<Tuple<String, Integer>> kList= new ArrayList<Tuple<String, Integer>>();
     for(int digit : inLimit)
     {
    	 Map<String, ArrayList<Integer>> digitFileList = kNNTrainMap.get(digit);
    	 for(Map.Entry<String, ArrayList<Integer>> entry : digitFileList.entrySet())
    	 {
    		 ArrayList<Integer> trainPoints = entry.getValue();
    		 int distance = 0;
    		 for(int i=0; i<1024; i++)
    		 {
    			 if(trainPoints.get(i) !=points.get(i))
    			 {
    				 distance++;
    			 }
    		 }
    		 kList.add(new Tuple(entry.getKey(), distance));
    		 reduceBiggestDistance(kList, 3); //k=3
    	 }
     }
     
     int knnChoosed = getKnnChoosed(kList);
     
     int expect_digit = Integer.valueOf(testFile.getName().substring(0,1));
     if(knnChoosed == expect_digit)
     {
   	  success++;
     }
   	 else
     {
   	  //System.out.println("error");
     }
     
     //System.out.println("kNN -- FileName:" + testFile.getName() + ", num:" + knnChoosed);
    }
    
    System.out.println("准确率："+ success*10000/total);
}

	private static int getKnnChoosed(List<Tuple<String, Integer>> kList) {
		Map<Integer, Integer> statMap = new HashMap<Integer, Integer>(); //数字-数量
		for(Tuple<String, Integer> tuple: kList)
		{
			Integer digit = Integer.valueOf(tuple._1.substring(0,1));
			if(statMap.get(digit)==null)
			{
				statMap.put(digit, 0);
			}
			
			int count = statMap.get(digit);
			count++;
			statMap.put(digit, count);
		}
		
		int choosedDigit=-1;
		int choosedCount = 0;
		
		for(Map.Entry<Integer, Integer> entry: statMap.entrySet())
		{
			if(entry.getValue()>choosedCount)
			{
				choosedDigit = entry.getKey();
				choosedCount = entry.getValue();
			}
		}
		
		return choosedDigit;
	}

	private static void reduceBiggestDistance(List<Tuple<String, Integer>> kList, int i) {
		if(kList.size()<=i)
			return;
		Collections.sort(kList, (item1, item2) -> item2._2 - item1._2);
		while(kList.size()>i)
		{
			kList.remove(0);
		}
	}

	public static void main(String[] args) throws IOException {
    	BayesKnn();
    }

    public static void BayesKnn() throws IOException
    {
    	KnnTrain();
    	BayesTrain();
    	 long startTime=System.currentTimeMillis();   //获取开始时间   
   
    	BayesKnnTest();
    	
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");   
    }
}
