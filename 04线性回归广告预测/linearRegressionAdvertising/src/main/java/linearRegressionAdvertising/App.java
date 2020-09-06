package linearRegressionAdvertising;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class App {
	static double[][] train_x = new double[180][2];
	static double[] train_y = new double[180];
	
	static double[][] test_x = new double[20][2];
	static double[] test_y = new double[20];
	
	static double[] para; 
	
	static OLSMultipleLinearRegression linearRegression = new OLSMultipleLinearRegression();
	
	public static void main(String[] args) throws Exception {

		trainLinearRegression();
		testLinearRegression();
	}
	
	public static void testLinearRegression()
	{
		for(int i=0; i<test_x.length; i++)
		{
			double y = para[1]*test_x[i][0] + para[2]*test_x[i][1] + para[0];
			System.out.println(String.format("actual:%s, expected:%s",
					String.valueOf(test_y[i]), String.valueOf(y)));
		}
	}
	
	public static void trainLinearRegression() throws Exception
	{
		String file = "C:\\Code\\code\\dataAlgo101\\数据集\\广告数据集\\Advertising.csv";
		List<String> lines = FileUtils.readLines(new File(file), "UTF-8");
		
		int i=0;
		for(int line=1; line<181; line++)
		{
			String[] tokens = lines.get(line).split(",");
			String tv = tokens[0];
			String radio = tokens[1];
			String sales = tokens[3];
			train_x[i][0] = Double.valueOf(tv);
			train_x[i][1] = Double.valueOf(radio);
			train_y[i] =  Double.valueOf(sales);
			i++;
		}
		
		i=0;
		for(int line=181; line<201; line++)
		{
			String[] tokens = lines.get(line).split(",");
			String tv = tokens[0];
			String radio = tokens[1];
			String sales = tokens[3];
			test_x[i][0] = Double.valueOf(tv);
			test_x[i][1] = Double.valueOf(radio);
			test_y[i] =  Double.valueOf(sales);
			i++;
		}
		
		
		
		linearRegression.newSampleData(train_y, train_x);
		para = linearRegression.estimateRegressionParameters();
		for( i=0; i<para.length; i++)
		{
			System.out.println(para[i]);
		}
	}
}
