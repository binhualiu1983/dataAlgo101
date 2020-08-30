package bayesDigitOcr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    public static void main(String[] args) throws IOException {
    	Bayes();
    }

    public static void Bayes() throws IOException
    {
     double[][] VecOfNumbers1 = new double[10][1024];
     double[][] VecOfNumbers0 = new double[10][1024];
     int[] documentCount = new int[10];
     
     File trainDir = new File("D:\\Code\\code\\dataAlgo101\\数据集\\数字识别\\trainingDigits");
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
     
     File testDir = new File("D:\\Code\\code\\dataAlgo101\\数据集\\数字识别\\testDigits");
     int success =0;
     int total = 0;
     for(File file : testDir.listFiles())
     {
    	 total++;
      double[] result = new double[10];
      String context = FileUtils.readFileToString(file, "UTF-8");
      for(int i=0; i<10; i++)
      {
      double[] Vec1 = VecOfNumbers1[i];
      double[] Vec0 = VecOfNumbers0[i];
      
       int index=0;
       for(int pos=0; pos<context.length(); pos++)
       {
        char c = context.charAt(pos);
        if(c == '0')
        {
         result[i] += Vec0[index];
         index++;
        }
        else if(c == '1')
        {
         result[i] += Vec1[index];
         index++;
        }
       }
      }
      
      int digit = findBigestIndex(result);
      int expect_digit = Integer.valueOf(file.getName().substring(0,1));
      if(digit == expect_digit)
    	  success++;
      else
      {
    	  System.out.println("error");
      }
      
      System.out.println("FileName:" + file.getName() + ", num:" + digit);
      
     }
     
     System.out.println("准确率："+ success*10000/total);
     
    }

    private static int findBigestIndex(double[] result) {
        int index = -1;
        double value = -10000000;
        for(int i=0; i<10; i++)
        {
         if(result[i] > value)
         {
          index = i;
          value = result[i];
         }
        }
        return index;
       }

}
