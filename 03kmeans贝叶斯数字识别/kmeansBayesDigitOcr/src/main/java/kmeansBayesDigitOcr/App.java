package kmeansBayesDigitOcr;

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

	static final int K=8;
//Bayes算法的训练结果集合， VecOfNumbers1保存的是值为1的向量的log值， VecOfNumbers0保存的是值为0的向量的log值，
static	double[][][] VecOfNumbers1 = new double[10][K][1024];
static	double[][][] VecOfNumbers0 = new double[10][K][1024];

//训练数据集
static Map<Integer, Map<String, ArrayList<Integer>>> trainData 
				= new HashMap<Integer, Map<String, ArrayList<Integer>>>();

//KMeans结果集
static Map<Integer, ArrayList<Map<String, ArrayList<Integer>>>> kmeansData 
= new HashMap<Integer, ArrayList<Map<String, ArrayList<Integer>>>>();


public static void main(String[] args) throws IOException {
	KmeansBayes();
}

public static void KmeansBayes() throws IOException
{
	loadTrainData();
	kmeansTrain();
	trainBayes();
	
	 long startTime=System.currentTimeMillis();   //获取开始时间   

	 KMeansBayesTest();
	
    long endTime=System.currentTimeMillis(); //获取结束时间
    System.out.println("程序运行时间： "+(endTime-startTime)+"ms");   
}



public static void kmeansTrain()
{
	 File trainDir = new File("C:\\Code\\code\\dataAlgo101\\数据集\\数字识别\\trainingDigits");
	 
}

public static void loadTrainData() throws IOException
{
     
     File trainDir = new File("C:\\Code\\code\\dataAlgo101\\数据集\\数字识别\\trainingDigits");
     for(File file : trainDir.listFiles())
     {
	      String fileName = file.getName();
	      int digit = Integer.valueOf(fileName.substring(0,1));
	      
	      String context = FileUtils.readFileToString(file, "UTF-8");
	      ArrayList<Integer> vect = new ArrayList<Integer>();
	      for(int i=0; i<context.length(); i++)
	      {
	       char c = context.charAt(i);
	       if(c == '0')
	       {
	    	   vect.add(0);
	       }
	       else if(c == '1')
	       {
	    	   vect.add(1);
	       }
	      }
	      trainData.put(digit, new Tuple<String, ArrayList<Integer>>(fileName, vect));
      
     }
}

public static void trainBayes()
{
	for(Map.Entry<Integer, ArrayList<Map<String, ArrayList<Integer>>>> entry: kmeansData.entrySet())
	{
		Integer digit = entry.getKey();
		ArrayList<Map<String, ArrayList<Integer>>> kmeanList = entry.getValue();
		
		double[][] KmeansVecOf1 = VecOfNumbers1[digit];
		double[][] KmeansVecOf0 = VecOfNumbers0[digit];
		
		for(int k = 0 ; k < kmeanList.size(); k++)
		{
			double[] vecOf1 = KmeansVecOf1[k];
			double[] vecOf0 = KmeansVecOf0[k];
			
			Map<String, ArrayList<Integer>> filesOfAMeans = kmeanList.get(k);
			for(Map.Entry<String, ArrayList<Integer>> fileEntry: filesOfAMeans.entrySet())
			{
				ArrayList<Integer> fileContext = fileEntry.getValue();
				for(int point =0; point<1024; point++)
				{
					if(fileContext.get(point)==1)
					{
						vecOf1[point]+=1.0;
					}
					else  if(fileContext.get(point)==0)
					{
						vecOf0[point]+=1.0;
					}
				}
			}
			
			for(int point =0; point<1024; point++)
		    {
		      double v1 = vecOf1[point]/filesOfAMeans.size() + 0.05; //拉普拉斯平滑
		      double log_v1 = Math.log(v1);
		      vecOf1[point] = log_v1;
		      
		      double v0 = vecOf0[point]/filesOfAMeans.size() + 0.05; //拉普拉斯平滑
		      double log_v0 = Math.log(v0);
		      vecOf0[point] = log_v0;
		    }
		}
	}
  }

//贝叶斯+KNN综合计算
public static void KMeansBayesTest() throws IOException
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
   	  result.put(i, -1000000.0);
     }
     
     String context = FileUtils.readFileToString(testFile, "UTF-8");
     List<Integer> points = new   ArrayList<Integer>();
     
     for(int digit=0; digit<10; digit++)
     {
    	 for(int k=0; k<K; k++)
    	 {
    		 double[] Vec1 = VecOfNumbers1[digit][k];
    	     double[] Vec0 =  VecOfNumbers0[digit][k];
    	     double probability = 0.0;
    	     
    	     for(int pos=0; pos<1024; pos++)
	    	    {
	    	       int currPoints = points.get(pos);
	    	       if(currPoints == 0)
	    	       {
	    	    	   probability+=Vec0[pos];
	    	       }
	    	       else if(currPoints == 1)
	    	       {
	    	       		probability+=Vec1[pos];
	    	       }
    	      }
    	     if(result.get(digit)<probability)
    	     {
    	    	 result.put(digit, probability);
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
     

     int expect_digit = Integer.valueOf(testFile.getName().substring(0,1));
     if(resultList.get(0).getKey() == expect_digit)
     {
       	  success++;
     }
     else
     {
       	 System.out.println("error");
      }
         
         System.out.println("Bayes FileName:" + testFile.getName() + ", num:" + resultList.get(0).getKey());
    }
    
    System.out.println("准确率："+ success*10000/total);
}


}
